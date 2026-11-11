/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.climate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.api.climate.ClimateJobCriteria;
import de.symeda.sormas.api.climate.ClimateJobStatus;
import de.symeda.sormas.api.climate.ClimateJobType;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.QueryHelper;

/**
 * Climate job rows. The methods that record a job's progress run in their own transaction, so that what a batch has done
 * (and why it stopped) is saved even when a later step fails.
 */
@Stateless
@LocalBean
public class ClimateJobService extends BaseAdoService<ClimateJob> {

	/** The order the processor works through open jobs: the nightly fetch first, history last. */
	private static final List<ClimateJobType> RUN_ORDER =
		Arrays.asList(ClimateJobType.DAILY_FETCH, ClimateJobType.FETCH, ClimateJobType.TOP_UP, ClimateJobType.BACKFILL);

	@EJB
	private ClimateObservationService climateObservationService;
	@EJB
	private ClimateLocationService climateLocationService;
	@EJB
	private ClimateSettings climateSettings;

	public ClimateJobService() {
		super(ClimateJob.class);
	}

	/**
	 * Stores a district's move to another climate location as a finished RELINK entry, so data managers can see which days
	 * were not copied. Only called when rows were copied or skipped.
	 *
	 * @param startedBy
	 *            the user who saved the district; null for system relinks (switch-on, nightly safety net, grid size change)
	 */
	public ClimateJob recordRelink(District district, ClimateLocation from, ClimateLocation to, ClimateRowCopyResult copyResult, User startedBy) {

		ClimateJob job = new ClimateJob();
		job.setJobType(ClimateJobType.RELINK);
		job.setStatus(ClimateJobStatus.DONE);
		job.setDistrict(district);
		job.setFromClimateLocation(from);
		job.setClimateLocation(to);
		job.setRowsAdded(copyResult.getCopied());
		job.setRowsSkipped(copyResult.getSkipped());
		job.setFromDate(copyResult.getFirstSkippedDay());
		job.setToDate(copyResult.getLastSkippedDay());
		job.setStartedBy(startedBy);
		Date now = new Date();
		job.setStartedAt(now);
		job.setFinishedAt(now);
		ensurePersisted(job);
		return job;
	}

	/**
	 * Queues an automatic top-up for a location a district or environment has just moved to, when history comes from
	 * OpenWeather: it fills the location back to the earliest day the district locations hold, so all locations share the
	 * same reference years, but no further back than the first reference year (decision 133). Nothing is queued when no
	 * district holds data yet, when the location already goes back that far, or when a top-up for it is still open.
	 */
	public void queueTopUp(ClimateLocation location, LocalDate today) {

		if (location == null || location.getId() == null || !location.hasCoordinates() || !climateSettings.isHistoryFromOpenWeather()) {
			return;
		}

		LocalDate earliestDistrictDay = climateObservationService.getEarliestDistrictDay();
		if (earliestDistrictDay == null) {
			return;
		}
		// no further back than the reference years the normals use (decision 133): imported district history can reach
		// back many years, and a top-up asks for no confirmation
		LocalDate referenceStart = referenceStart(today, climateSettings.getHistoryYears());
		LocalDate from = earliestDistrictDay.isAfter(referenceStart) ? earliestDistrictDay : referenceStart;
		LocalDate to = today.minusDays(1);
		if (from.isAfter(to)) {
			return;
		}
		LocalDate earliestHere = climateObservationService.getEarliestDay(location.getId());
		if (earliestHere != null && !earliestHere.isAfter(from)) {
			return;
		}
		if (hasOpenTopUp(location)) {
			return;
		}

		ClimateJob job = new ClimateJob();
		job.setJobType(ClimateJobType.TOP_UP);
		job.setStatus(ClimateJobStatus.QUEUED);
		job.setClimateLocation(location);
		job.setFromDate(ClimateCalendar.toDate(from));
		job.setToDate(ClimateCalendar.toDate(to));
		ensurePersisted(job);
	}

	/**
	 * Queues tonight's fetch, when the daily source is OpenWeather: yesterday and the catch-up days before it, for every
	 * location in use. Without an API key the job is stored as failed, so the admin screen shows why nothing was fetched.
	 * <p>
	 * An earlier night's fetch that is still open is cancelled, because this one covers the same days. One from today that
	 * is still open is left to finish and nothing new is queued (the schedule may run more than once a day). Locations used
	 * only by environments start after the one where an earlier night stopped at the daily limit, so they take turns.
	 *
	 * @return the new job, or null when none was queued
	 */
	public ClimateJob queueDailyFetch(LocalDate today) {

		if (!climateSettings.isDailyFromOpenWeather()) {
			return null;
		}

		ClimateJob previous = getLatestDailyFetch();
		Long rotationStart = null;
		if (previous != null) {
			if (previous.getStatus().isOpen()) {
				if (!ClimateCalendar.toLocalDate(previous.getCreationDate()).isBefore(today)) {
					return null;
				}
				previous.setStatus(ClimateJobStatus.CANCELLED);
				previous.setLastError("Replaced by the next night's fetch, which covers the same days");
				previous.setFinishedAt(new Date());
				ensurePersisted(previous);
			}
			rotationStart = previous.getRotationStartLocationId();
			if (previous.getStatus() == ClimateJobStatus.STOPPED_AT_LIMIT
				&& previous.getCursorGroup() == ClimateLocationGroup.ENVIRONMENT
				&& previous.getCursorLocationId() != null) {
				rotationStart = previous.getCursorLocationId();
			}
		}

		ClimateJob job = new ClimateJob();
		job.setJobType(ClimateJobType.DAILY_FETCH);
		job.setFromDate(ClimateCalendar.toDate(today.minusDays(1L + climateSettings.getCatchupDays())));
		job.setToDate(ClimateCalendar.toDate(today.minusDays(1)));
		job.setRotationStartLocationId(rotationStart);
		if (climateSettings.getApiKey() == null) {
			job.setStatus(ClimateJobStatus.FAILED);
			job.setLastError("No OpenWeather API key is set in System Configuration");
			job.setFinishedAt(new Date());
		} else {
			job.setStatus(ClimateJobStatus.QUEUED);
		}
		ensurePersisted(job);
		return job;
	}

	/**
	 * @return the first day of the reference years: 1 January of the first reference calendar year (daily normals), or the
	 *         first day of the first reference epi year (weekly normals) when that is earlier
	 */
	static LocalDate referenceStart(LocalDate today, int historyYears) {
		LocalDate calendarStart = LocalDate.of(today.getYear() - historyYears, 1, 1);
		LocalDate epiStart = ClimateStatistics.firstDayOfEpiYear(ClimateStatistics.epiWeekOf(today).getYear() - historyYears);
		return epiStart.isBefore(calendarStart) ? epiStart : calendarStart;
	}

	private boolean hasOpenTopUp(ClimateLocation location) {
		return !em
			.createQuery(
				"SELECT j.id FROM " + ClimateJob.TABLE_NAME + " j WHERE j." + ClimateJob.JOB_TYPE + " = :type AND j." + ClimateJob.CLIMATE_LOCATION
					+ " = :location AND j." + ClimateJob.STATUS + " IN :open",
				Long.class)
			.setParameter("type", ClimateJobType.TOP_UP)
			.setParameter("location", location)
			.setParameter("open", Arrays.asList(ClimateJobStatus.QUEUED, ClimateJobStatus.RUNNING, ClimateJobStatus.PAUSED_AT_LIMIT))
			.setMaxResults(1)
			.getResultList()
			.isEmpty();
	}

	/**
	 * @return the jobs the processor still has to work through, in the order it takes them: the nightly fetch, fetch now,
	 *         top-ups and then backfills, each oldest first. The scope is loaded with them.
	 */
	public List<ClimateJob> getOpenJobs() {

		List<ClimateJob> jobs = em
			.createQuery(
				"SELECT j FROM " + ClimateJob.TABLE_NAME + " j LEFT JOIN FETCH j." + ClimateJob.REGION + " LEFT JOIN FETCH j." + ClimateJob.DISTRICT
					+ " LEFT JOIN FETCH j." + ClimateJob.CLIMATE_LOCATION + " WHERE j." + ClimateJob.STATUS + " IN :open AND j." + ClimateJob.JOB_TYPE
					+ " IN :types",
				ClimateJob.class)
			.setParameter("open", Arrays.asList(ClimateJobStatus.QUEUED, ClimateJobStatus.RUNNING, ClimateJobStatus.PAUSED_AT_LIMIT))
			.setParameter("types", RUN_ORDER)
			.getResultList();

		jobs.sort(
			Comparator.comparing((ClimateJob j) -> RUN_ORDER.indexOf(j.getJobType()))
				.thenComparing(ClimateJob::getCreationDate)
				.thenComparing(ClimateJob::getId));
		return jobs;
	}

	/**
	 * @return a page of the jobs matching the criteria, newest first, with their scope and starting user fetched
	 */
	public List<ClimateJob> getPage(ClimateJobCriteria criteria, Integer first, Integer max) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ClimateJob> cq = cb.createQuery(ClimateJob.class);
		Root<ClimateJob> root = cq.from(ClimateJob.class);
		root.fetch(ClimateJob.REGION, JoinType.LEFT);
		root.fetch(ClimateJob.DISTRICT, JoinType.LEFT);
		root.fetch(ClimateJob.CLIMATE_LOCATION, JoinType.LEFT);
		root.fetch(ClimateJob.STARTED_BY, JoinType.LEFT);
		Predicate filter = buildCriteriaFilter(criteria, cb, root);
		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.desc(root.get(ClimateJob.CREATION_DATE)), cb.desc(root.get(ClimateJob.ID)));
		return QueryHelper.getResultList(em, cq, first, max);
	}

	public long count(ClimateJobCriteria criteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ClimateJob> root = cq.from(ClimateJob.class);
		cq.select(cb.count(root));
		Predicate filter = buildCriteriaFilter(criteria, cb, root);
		if (filter != null) {
			cq.where(filter);
		}
		return em.createQuery(cq).getSingleResult();
	}

	private static Predicate buildCriteriaFilter(ClimateJobCriteria criteria, CriteriaBuilder cb, Root<ClimateJob> root) {

		Predicate filter = null;
		if (criteria.getJobType() != null) {
			filter = cb.equal(root.get(ClimateJob.JOB_TYPE), criteria.getJobType());
		}
		if (criteria.getStatus() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(root.get(ClimateJob.STATUS), criteria.getStatus()));
		}
		if (Boolean.TRUE.equals(criteria.getOnlyRelinksWithSkippedDays())) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(root.get(ClimateJob.JOB_TYPE), ClimateJobType.RELINK),
				cb.greaterThan(root.get(ClimateJob.ROWS_SKIPPED), 0));
		}
		if (criteria.getCreatedFrom() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.or(
					cb.greaterThanOrEqualTo(root.get(ClimateJob.CREATION_DATE), criteria.getCreatedFrom()),
					root.get(ClimateJob.STATUS).in(ClimateJobStatus.QUEUED, ClimateJobStatus.RUNNING, ClimateJobStatus.PAUSED_AT_LIMIT)));
		}
		return filter;
	}

	/**
	 * @return the most recently created nightly fetch, or null
	 */
	public ClimateJob getLatestDailyFetch() {
		List<ClimateJob> jobs = em.createQuery(
			"SELECT j FROM " + ClimateJob.TABLE_NAME + " j WHERE j." + ClimateJob.JOB_TYPE + " = :type ORDER BY j.creationDate DESC, j.id DESC",
			ClimateJob.class).setParameter("type", ClimateJobType.DAILY_FETCH).setMaxResults(1).getResultList();
		return jobs.isEmpty() ? null : jobs.get(0);
	}

	/**
	 * Marks a job as running. A job that starts for the first time records its start and how many locations it covers; a
	 * queued or paused job that resumes loses the reason it was waiting.
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public ClimateJob markRunning(Long jobId, int locationsTotal) {

		ClimateJob job = getById(jobId);
		if (job.getStartedAt() == null) {
			job.setStartedAt(new Date());
			job.setLocationsTotal(locationsTotal);
		}
		if (job.getStatus() != ClimateJobStatus.RUNNING) {
			job.setLastError(null);
			job.setPausedAt(null);
		}
		job.setStatus(ClimateJobStatus.RUNNING);
		ensurePersisted(job);
		return job;
	}

	/**
	 * Saves what one provider call returned, together with the job's progress.
	 *
	 * @param nextDay
	 *            history and fetch-now jobs: the first day of the location's next call, so a batch that ends here resumes
	 *            there; null when the location is finished
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public ClimateJob recordWindow(
		Long jobId,
		Long locationId,
		List<ClimateDay> days,
		ClimateDataSource source,
		boolean updateExisting,
		int calls,
		LocalDate nextDay) {

		ClimateJob job = getById(jobId);
		ClimateLocation location = climateLocationService.getById(locationId);
		ClimateWriteCount count = climateObservationService.saveFetched(location, days, source, updateExisting);
		job.setCallsMade(job.getCallsMade() + calls);
		job.setRowsAdded(job.getRowsAdded() + count.getAdded());
		job.setRowsUpdated(job.getRowsUpdated() + count.getUpdated());
		job.setCursorDate(ClimateCalendar.toDate(nextDay));
		ensurePersisted(job);
		return job;
	}

	/**
	 * Records a location as finished, successfully or not, and moves the cursor past it.
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public ClimateJob finishLocation(Long jobId, ClimateLocationGroup group, Long locationId, int failedCalls, String error) {

		ClimateJob job = getById(jobId);
		job.setCallsMade(job.getCallsMade() + failedCalls);
		job.setCursorGroup(group);
		job.setCursorLocationId(locationId);
		job.setCursorDate(null);
		if (error == null) {
			job.setLocationsDone(job.getLocationsDone() + 1);
		} else {
			job.setLocationsFailed(job.getLocationsFailed() + 1);
			job.setLastError(error);
		}
		ensurePersisted(job);
		return job;
	}

	/**
	 * Remembers which group the job has reached, so a job that stops between groups resumes in the right one.
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void startGroup(Long jobId, ClimateLocationGroup group) {

		ClimateJob job = getById(jobId);
		if (job.getCursorGroup() != group) {
			job.setCursorGroup(group);
			job.setCursorLocationId(null);
			job.setCursorDate(null);
			ensurePersisted(job);
		}
	}

	/**
	 * Ends or pauses a job.
	 *
	 * @param cursorDay
	 *            for a pause in the middle of a location: the first day of the call that was refused, which is made again
	 *            when the job resumes
	 * @param error
	 *            the reason, or null to keep the last one recorded
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public ClimateJob finish(Long jobId, ClimateJobStatus status, int failedCalls, LocalDate cursorDay, String error) {

		ClimateJob job = getById(jobId);
		job.setCallsMade(job.getCallsMade() + failedCalls);
		job.setStatus(status);
		if (cursorDay != null) {
			job.setCursorDate(ClimateCalendar.toDate(cursorDay));
		}
		if (error != null) {
			job.setLastError(error);
		}
		if (status == ClimateJobStatus.PAUSED_AT_LIMIT) {
			job.setPausedAt(new Date());
		} else {
			job.setFinishedAt(new Date());
		}
		ensurePersisted(job);
		return job;
	}

	/**
	 * Notes why a queued job cannot run yet (for example no API key), without changing its status.
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void setWaitingReason(Long jobId, String reason) {

		ClimateJob job = getById(jobId);
		if (!reason.equals(job.getLastError())) {
			job.setLastError(reason);
			ensurePersisted(job);
		}
	}
}
