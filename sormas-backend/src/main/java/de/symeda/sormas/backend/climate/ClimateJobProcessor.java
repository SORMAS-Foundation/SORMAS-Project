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

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.climate.ClimateJobStatus;
import de.symeda.sormas.api.climate.ClimateJobType;

/**
 * Works through the queued, running and paused climate jobs (CRON.PROCESS_CLIMATE_JOBS, every 5 minutes), in bounded
 * batches: a run stops after {@link #RUN_BUDGET_MILLIS}, and the next run carries on where it stopped.
 * <ul>
 * <li>One run at a time: the scheduler may start the next run while a slow one is still going, so a run that finds
 * another one active returns at once.</li>
 * <li>Provider calls are made outside any transaction. What each call returned is saved together with the job's progress
 * in its own transaction, so a restart loses at most the call in progress.</li>
 * <li>History jobs (backfill, top-up) and paused jobs never take the nightly fetch's calls: while a nightly fetch is
 * expected, they run only after the nightly fetch of the current UTC day (OpenWeather's limits reset at 00:00 UTC) has
 * finished without reaching the limit.</li>
 * <li>When OpenWeather's daily limit is reached, the nightly fetch stops for the night (the next night covers the same
 * days) and other jobs pause until the limit has reset.</li>
 * </ul>
 */
@Singleton
@LocalBean
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ClimateJobProcessor {

	/** How long one run makes calls; below the 5-minute schedule, so runs rarely overlap. */
	static final long RUN_BUDGET_MILLIS = 4 * 60 * 1000L;

	/** Locations in a row that OpenWeather fails for (after retries) before the job is given up as failed. */
	static final int UNAVAILABLE_IN_A_ROW_LIMIT = 10;

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final AtomicBoolean running = new AtomicBoolean();

	private Function<String, ClimateDataProvider> providerFactory = OpenWeatherProvider::new;
	private Clock clock = Clock.systemDefaultZone();

	@EJB
	private ClimateSettings climateSettings;
	@EJB
	private ClimateJobService climateJobService;
	@EJB
	private ClimateLocationService climateLocationService;
	@EJB
	private ClimateObservationService climateObservationService;

	public void processJobs() {

		if (!running.compareAndSet(false, true)) {
			logger.info("Climate jobs are still being worked through by an earlier run; this run is skipped");
			return;
		}
		try {
			if (climateSettings.isClimateEnabled()) {
				process();
			}
		} finally {
			running.set(false);
		}
	}

	/**
	 * Replaces the provider and the clock, for tests.
	 */
	public void useForTests(Function<String, ClimateDataProvider> providerFactory, Clock clock) {
		this.providerFactory = providerFactory;
		this.clock = clock;
	}

	private void process() {

		long deadline = clock.millis() + RUN_BUDGET_MILLIS;
		ClimateDataProvider provider = null;
		try {
			for (ClimateJob job : climateJobService.getOpenJobs()) {
				if (clock.millis() >= deadline) {
					return;
				}
				String waitingReason = getWaitingReason(job);
				if (waitingReason != null) {
					climateJobService.setWaitingReason(job.getId(), waitingReason);
					continue;
				}
				if (!isItsTurn(job)) {
					continue;
				}
				if (provider == null) {
					provider = providerFactory.apply(climateSettings.getApiKey());
				}
				if (!new JobRun(job, provider, deadline).run()) {
					return;
				}
			}
		} finally {
			if (provider != null) {
				provider.close();
			}
		}
	}

	/**
	 * @return why a job cannot run with the current settings, or null when it can
	 */
	private String getWaitingReason(ClimateJob job) {

		if (job.getJobType().isHistory()) {
			if (!climateSettings.isHistoryFromOpenWeather()) {
				return "Waiting: the climate history source is not OpenWeather";
			}
		} else if (!climateSettings.isDailyFromOpenWeather()) {
			return "Waiting: the climate daily source is not OpenWeather";
		}
		if (climateSettings.getApiKey() == null) {
			return "Waiting: no OpenWeather API key is set";
		}
		return null;
	}

	private boolean isItsTurn(ClimateJob job) {

		LocalDate utcToday = LocalDate.now(clock.withZone(ZoneOffset.UTC));
		if (job.getStatus() == ClimateJobStatus.PAUSED_AT_LIMIT && job.getPausedAt() != null && !utcDate(job.getPausedAt()).isBefore(utcToday)) {
			return false;
		}
		if ((job.getJobType().isHistory() || job.getStatus() == ClimateJobStatus.PAUSED_AT_LIMIT) && climateSettings.isNightlyFetchActive()) {
			ClimateJob nightly = climateJobService.getLatestDailyFetch();
			return nightly != null
				&& utcDate(nightly.getCreationDate()).equals(utcToday)
				&& !nightly.getStatus().isOpen()
				&& nightly.getStatus() != ClimateJobStatus.STOPPED_AT_LIMIT;
		}
		return true;
	}

	private static LocalDate utcDate(Date timestamp) {
		return timestamp.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
	}

	/**
	 * The order locations used only by environments are worked through by the nightly fetch: starting after
	 * {@code rotationStart}, then from the beginning, so they take turns when the daily limit is short.
	 */
	static Comparator<Long> rotationOrder(Long rotationStart) {
		return Comparator.comparing((Long id) -> rotationStart != null && id <= rotationStart ? 1 : 0).thenComparing(id -> id);
	}

	static List<Long> after(List<Long> ordered, Long cursorId, Long rotationStart) {
		if (cursorId == null) {
			return ordered;
		}
		Comparator<Long> order = rotationOrder(rotationStart);
		return ordered.stream().filter(id -> order.compare(id, cursorId) > 0).collect(Collectors.toList());
	}

	private enum Next {
		CONTINUE,
		STOP_JOB,
		STOP_RUN
	}

	/**
	 * One job's share of a run.
	 */
	private class JobRun {

		private final ClimateJob job;
		private final ClimateDataProvider provider;
		private final long deadline;
		private ClimateJob state;
		private int unavailableInARow;

		JobRun(ClimateJob job, ClimateDataProvider provider, long deadline) {
			this.job = job;
			this.provider = provider;
			this.deadline = deadline;
		}

		/**
		 * @return false when the whole run must stop: out of time, or OpenWeather refuses further calls
		 */
		boolean run() {

			ClimateJobType type = job.getJobType();
			List<Long> districtLocations;
			List<Long> environmentLocations;
			Long rotationStart = type == ClimateJobType.DAILY_FETCH ? job.getRotationStartLocationId() : null;

			if (type == ClimateJobType.TOP_UP) {
				ClimateLocation location = job.getClimateLocation();
				if (location == null || !climateLocationService.isInUse(location.getId())) {
					// the district or environment moved on before the top-up ran
					climateJobService.finish(job.getId(), ClimateJobStatus.CANCELLED, 0, null, "The climate location is no longer in use");
					return true;
				}
				districtLocations =
					location != null && location.hasCoordinates() ? Collections.singletonList(location.getId()) : Collections.emptyList();
				environmentLocations = Collections.emptyList();
			} else {
				Long regionId = job.getRegion() != null ? job.getRegion().getId() : null;
				Long districtId = job.getDistrict() != null ? job.getDistrict().getId() : null;
				districtLocations = climateLocationService.getDistrictLocationIds(regionId, districtId);
				environmentLocations = new ArrayList<>();
				if (type == ClimateJobType.DAILY_FETCH || job.isIncludeEnvironments()) {
					environmentLocations.addAll(climateLocationService.getEnvironmentLocationIds(regionId, districtId));
					environmentLocations.removeAll(new HashSet<>(districtLocations));
					environmentLocations.sort(rotationOrder(rotationStart));
				}
			}

			state = climateJobService.markRunning(job.getId(), districtLocations.size() + environmentLocations.size());
			ClimateLocationGroup cursorGroup = state.getCursorGroup();
			LocalDate resumeDay = ClimateCalendar.toLocalDate(state.getCursorDate());

			for (ClimateLocationGroup group : ClimateLocationGroup.values()) {
				if (cursorGroup == ClimateLocationGroup.ENVIRONMENT && group == ClimateLocationGroup.DISTRICT) {
					continue;
				}
				List<Long> locations = group == ClimateLocationGroup.DISTRICT ? districtLocations : environmentLocations;
				if (group == cursorGroup) {
					locations = after(locations, state.getCursorLocationId(), group == ClimateLocationGroup.ENVIRONMENT ? rotationStart : null);
				} else {
					climateJobService.startGroup(job.getId(), group);
					resumeDay = null;
				}

				for (Long locationId : locations) {
					Next next = runLocation(group, locationId, resumeDay);
					resumeDay = null;
					if (next == Next.STOP_RUN) {
						return false;
					}
					if (next == Next.STOP_JOB || state.getStatus() == ClimateJobStatus.CANCELLED) {
						return true;
					}
				}
			}

			state = climateJobService.finish(job.getId(), ClimateJobStatus.DONE, 0, null, null);
			logger.info(
				"Climate {} job {} done: {} locations fetched, {} failed, {} calls, {} rows added, {} rows updated",
				type,
				job.getUuid(),
				state.getLocationsDone(),
				state.getLocationsFailed(),
				state.getCallsMade(),
				state.getRowsAdded(),
				state.getRowsUpdated());
			return true;
		}

		private Next runLocation(ClimateLocationGroup group, Long locationId, LocalDate resumeDay) {

			ClimateLocation location = climateLocationService.getById(locationId);
			if (location == null || !location.hasCoordinates()) {
				state = climateJobService.finishLocation(job.getId(), group, locationId, 0, null);
				return Next.CONTINUE;
			}

			ClimateJobType type = job.getJobType();
			LocalDate from = ClimateCalendar.toLocalDate(job.getFromDate());
			LocalDate to = ClimateCalendar.toLocalDate(job.getToDate());
			LocalDate start = resumeDay != null ? resumeDay : from;
			boolean updateExisting = !type.isHistory();

			List<LocalDate> windows = new ArrayList<>();
			if (type == ClimateJobType.DAILY_FETCH) {
				windows.add(from);
			} else if (type == ClimateJobType.FETCH) {
				for (LocalDate day = start; !day.isAfter(to); day = day.plusDays(provider.maxDaysPerCall())) {
					windows.add(day);
				}
			} else if (!start.isAfter(to)) {
				windows =
					ClimateCalendar.windowStarts(start, to, climateObservationService.getHeldDays(locationId, start, to), provider.maxDaysPerCall());
			}

			for (int i = 0; i < windows.size(); i++) {
				LocalDate window = windows.get(i);
				if (clock.millis() >= deadline) {
					// remember where this location got to; the next run carries on from here
					state = climateJobService
						.recordWindow(job.getId(), locationId, Collections.emptyList(), provider.source(), updateExisting, 0, window);
					return Next.STOP_RUN;
				}

				ClimateFetchResult result;
				try {
					result = provider.fetchWindow(location.getLatitude(), location.getLongitude(), window);
				} catch (ClimateProviderException e) {
					return handleRefusal(e, group, locationId, window);
				}

				List<ClimateDay> days = result.getDays();
				if (type == ClimateJobType.FETCH) {
					// fetch now writes the days asked for only
					days = days.stream().filter(d -> !d.getDate().isBefore(from) && !d.getDate().isAfter(to)).collect(Collectors.toList());
				}
				LocalDate nextWindow = i + 1 < windows.size() ? windows.get(i + 1) : null;
				state =
					climateJobService.recordWindow(job.getId(), locationId, days, provider.source(), updateExisting, result.getCalls(), nextWindow);
			}

			unavailableInARow = 0;
			state = climateJobService.finishLocation(job.getId(), group, locationId, 0, null);
			return Next.CONTINUE;
		}

		private Next handleRefusal(ClimateProviderException e, ClimateLocationGroup group, Long locationId, LocalDate window) {

			switch (e.getReason()) {
			case LIMIT_REACHED:
				int calls = state.getCallsMade() + e.getCalls();
				if (job.getJobType() == ClimateJobType.DAILY_FETCH) {
					int notFetched = state.getLocationsTotal() - state.getLocationsDone() - state.getLocationsFailed();
					String message = "OpenWeather's daily limit was reached after " + calls + " calls; " + notFetched + " locations were not fetched";
					climateJobService.finish(job.getId(), ClimateJobStatus.STOPPED_AT_LIMIT, e.getCalls(), null, message);
					logger.warn("Climate nightly fetch stopped: {}. Raise the One Call daily limit in the OpenWeather account.", message);
				} else {
					String message = "Paused at OpenWeather's daily limit after " + calls + " calls; resumes after the limit resets at 00:00 UTC";
					climateJobService.finish(job.getId(), ClimateJobStatus.PAUSED_AT_LIMIT, e.getCalls(), window, message);
					logger.info("Climate {} job {}: {}", job.getJobType(), job.getUuid(), message);
				}
				return Next.STOP_RUN;

			case ACCESS_DENIED:
				climateJobService.finish(job.getId(), ClimateJobStatus.FAILED, e.getCalls(), window, e.getMessage());
				logger.error("Climate {} job {} failed: {}", job.getJobType(), job.getUuid(), e.getMessage());
				return Next.STOP_RUN;

			case UNAVAILABLE:
				unavailableInARow++;
				if (unavailableInARow >= UNAVAILABLE_IN_A_ROW_LIMIT) {
					String message = "OpenWeather failed for " + unavailableInARow + " locations in a row: " + e.getMessage();
					climateJobService.finish(job.getId(), ClimateJobStatus.FAILED, e.getCalls(), window, message);
					logger.error("Climate {} job {} failed: {}", job.getJobType(), job.getUuid(), message);
					return Next.STOP_RUN;
				}
				state = climateJobService.finishLocation(job.getId(), group, locationId, e.getCalls(), e.getMessage());
				logger.warn("Climate {} job {}: location {} skipped: {}", job.getJobType(), job.getUuid(), locationId, e.getMessage());
				return Next.CONTINUE;

			case LOCATION_REFUSED:
			default:
				state = climateJobService.finishLocation(job.getId(), group, locationId, e.getCalls(), e.getMessage());
				logger.warn("Climate {} job {}: location {} skipped: {}", job.getJobType(), job.getUuid(), locationId, e.getMessage());
				return Next.CONTINUE;
			}
		}
	}
}
