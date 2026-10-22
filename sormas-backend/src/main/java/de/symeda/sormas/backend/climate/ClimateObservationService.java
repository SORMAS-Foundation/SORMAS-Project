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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.infrastructure.district.District;

@Stateless
@LocalBean
public class ClimateObservationService extends BaseAdoService<ClimateObservation> {

	public ClimateObservationService() {
		super(ClimateObservation.class);
	}

	public List<ClimateObservation> getByLocation(ClimateLocation location, ClimateDataSource... sources) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ClimateObservation> cq = cb.createQuery(ClimateObservation.class);
		Root<ClimateObservation> from = cq.from(ClimateObservation.class);

		Predicate filter = cb.equal(from.get(ClimateObservation.CLIMATE_LOCATION), location);
		if (sources.length > 0) {
			filter = cb.and(filter, from.get(ClimateObservation.SOURCE).in(Arrays.asList(sources)));
		}
		cq.where(filter);
		cq.orderBy(cb.asc(from.get(ClimateObservation.OBSERVATION_DATE)));

		return em.createQuery(cq).getResultList();
	}

	/**
	 * @return the days between {@code from} and {@code to} (inclusive) that have a row from any source
	 */
	public Set<LocalDate> getHeldDays(Long locationId, LocalDate from, LocalDate to) {

		List<Date> days = em
			.createQuery(
				"SELECT o." + ClimateObservation.OBSERVATION_DATE + " FROM " + ClimateObservation.TABLE_NAME + " o WHERE o."
					+ ClimateObservation.CLIMATE_LOCATION + ".id = :location AND o." + ClimateObservation.OBSERVATION_DATE
					+ " BETWEEN :from AND :to",
				Date.class)
			.setParameter("location", locationId)
			.setParameter("from", ClimateCalendar.toDate(from))
			.setParameter("to", ClimateCalendar.toDate(to))
			.getResultList();

		Set<LocalDate> held = new HashSet<>();
		days.forEach(day -> held.add(ClimateCalendar.toLocalDate(day)));
		return held;
	}

	/**
	 * @return the earliest day held by the location, from any source, or null when it holds nothing
	 */
	public LocalDate getEarliestDay(Long locationId) {
		Date earliest = em
			.createQuery(
				"SELECT MIN(o." + ClimateObservation.OBSERVATION_DATE + ") FROM " + ClimateObservation.TABLE_NAME + " o WHERE o."
					+ ClimateObservation.CLIMATE_LOCATION + ".id = :location",
				Date.class)
			.setParameter("location", locationId)
			.getSingleResult();
		return ClimateCalendar.toLocalDate(earliest);
	}

	/**
	 * @return the earliest day held by any location a district that is not archived links to, or null when none holds data.
	 *         Automatic top-ups fill new locations back to this day, so all locations share the same reference years.
	 */
	public LocalDate getEarliestDistrictDay() {
		Date earliest = em
			.createQuery(
				"SELECT MIN(o." + ClimateObservation.OBSERVATION_DATE + ") FROM " + ClimateObservation.TABLE_NAME + " o WHERE o."
					+ ClimateObservation.CLIMATE_LOCATION + " IN (SELECT d." + District.CLIMATE_LOCATION + " FROM District d WHERE d."
					+ District.ARCHIVED + " = false)",
				Date.class)
			.getSingleResult();
		return ClimateCalendar.toLocalDate(earliest);
	}

	/**
	 * Writes days fetched from a provider, with the source order applied: a row of a higher-ranked source (IMPORT,
	 * MANUAL) is never touched. A row of the same source is updated only when {@code updateExisting} is set and a value
	 * actually changed, so re-fetching an unchanged day writes nothing (and adds nothing to the history table).
	 *
	 * @param updateExisting
	 *            true for the nightly fetch and fetch now, which pick up the provider's revisions of recent days; false for
	 *            backfill and top-up, which only fill days without a row
	 */
	public ClimateWriteCount saveFetched(ClimateLocation location, List<ClimateDay> days, ClimateDataSource source, boolean updateExisting) {

		ClimateWriteCount count = new ClimateWriteCount();
		if (days.isEmpty()) {
			return count;
		}

		LocalDate first = days.get(0).getDate();
		LocalDate last = first;
		for (ClimateDay day : days) {
			first = day.getDate().isBefore(first) ? day.getDate() : first;
			last = day.getDate().isAfter(last) ? day.getDate() : last;
		}

		Map<LocalDate, ClimateObservation> rowsByDay = new HashMap<>();
		em.createQuery(
			"SELECT o FROM " + ClimateObservation.TABLE_NAME + " o WHERE o." + ClimateObservation.CLIMATE_LOCATION + " = :location AND o."
				+ ClimateObservation.OBSERVATION_DATE + " BETWEEN :from AND :to",
			ClimateObservation.class)
			.setParameter("location", location)
			.setParameter("from", ClimateCalendar.toDate(first))
			.setParameter("to", ClimateCalendar.toDate(last))
			.getResultList()
			.forEach(row -> rowsByDay.put(ClimateCalendar.toLocalDate(row.getObservationDate()), row));

		for (ClimateDay day : days) {
			ClimateObservation row = rowsByDay.get(day.getDate());
			if (row == null) {
				row = new ClimateObservation();
				row.setClimateLocation(location);
				row.setObservationDate(ClimateCalendar.toDate(day.getDate()));
				setFetchedValues(row, day, source);
				persist(row);
				rowsByDay.put(day.getDate(), row);
				count.addAdded();
			} else if (row.getSource() == source) {
				if (updateExisting && !hasSameValues(row, day)) {
					setFetchedValues(row, day, source);
					count.addUpdated();
				}
			} else if (source.outranks(row.getSource())) {
				setFetchedValues(row, day, source);
				count.addUpdated();
			}
		}
		doFlush();
		return count;
	}

	private static void setFetchedValues(ClimateObservation row, ClimateDay day, ClimateDataSource source) {
		row.setTemperatureMin(day.getTemperatureMin());
		row.setTemperatureMax(day.getTemperatureMax());
		row.setTemperatureMean(day.getTemperatureMean());
		row.setRainfall(day.getRainfall());
		row.setRelativeHumidity(day.getRelativeHumidity());
		row.setHumidityMeasure(day.getHumidityMeasure());
		row.setSource(source);
		row.setSourceDetail(null);
		row.setReportingUser(null);
	}

	private static boolean hasSameValues(ClimateObservation row, ClimateDay day) {
		return Objects.equals(row.getTemperatureMin(), day.getTemperatureMin())
			&& Objects.equals(row.getTemperatureMax(), day.getTemperatureMax())
			&& Objects.equals(row.getTemperatureMean(), day.getTemperatureMean())
			&& Objects.equals(row.getRainfall(), day.getRainfall())
			&& Objects.equals(row.getRelativeHumidity(), day.getRelativeHumidity())
			&& row.getHumidityMeasure() == day.getHumidityMeasure();
	}

	/**
	 * Copies the IMPORT and MANUAL rows of one location to another, for example when a district's point changes, because
	 * those rows describe the district rather than the old point. A row is copied when the target has no row that day, or
	 * only one of a lower-ranked source; the target's own data is never overwritten by something it outranks. The rows of
	 * {@code from} stay, so anything still sharing the old cell keeps them. OPENWEATHER rows are not copied: the new point
	 * gets its own. On a tie (IMPORT onto IMPORT, MANUAL onto MANUAL) the target's row stays and the day is counted as
	 * skipped.
	 *
	 * @return how many rows were created or replaced on {@code to}, and how many days were skipped
	 */
	public ClimateRowCopyResult copyExternalRows(ClimateLocation from, ClimateLocation to) {

		if (from == null || to == null || from.getId() == null || from.getId().equals(to.getId())) {
			return ClimateRowCopyResult.NOTHING;
		}

		List<ClimateObservation> sourceRows = getByLocation(from, ClimateDataSource.IMPORT, ClimateDataSource.MANUAL);
		if (sourceRows.isEmpty()) {
			return ClimateRowCopyResult.NOTHING;
		}

		Map<Long, ClimateObservation> targetRowsByDay = new HashMap<>();
		for (ClimateObservation targetRow : getByLocation(to)) {
			targetRowsByDay.put(targetRow.getObservationDate().getTime(), targetRow);
		}

		ClimateRowCopyResult result = new ClimateRowCopyResult();
		for (ClimateObservation sourceRow : sourceRows) {
			ClimateObservation targetRow = targetRowsByDay.get(sourceRow.getObservationDate().getTime());
			if (targetRow == null) {
				targetRow = new ClimateObservation();
				targetRow.setClimateLocation(to);
				targetRow.setObservationDate(sourceRow.getObservationDate());
				targetRow.copyValuesFrom(sourceRow);
				persist(targetRow);
				result.addCopied();
			} else if (sourceRow.getSource().outranks(targetRow.getSource())) {
				// managed entity: the change is written with the flush below
				targetRow.copyValuesFrom(sourceRow);
				result.addCopied();
			} else {
				result.addSkipped(sourceRow.getObservationDate());
			}
		}
		doFlush();
		return result;
	}
}
