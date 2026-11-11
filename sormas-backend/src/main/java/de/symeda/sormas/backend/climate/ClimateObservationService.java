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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.collections4.ListUtils;

import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.api.climate.ClimateEarlierValueDto;
import de.symeda.sormas.api.climate.ClimateObservationCriteria;
import de.symeda.sormas.api.climate.ClimatePlaceType;
import de.symeda.sormas.api.climate.HumidityMeasure;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.environment.Environment;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;

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
	 * @return the latest day up to {@code upTo} that the location holds a row for, or null when it holds none
	 */
	public LocalDate getLatestDay(Long locationId, LocalDate upTo) {
		Date latest = em
			.createQuery(
				"SELECT MAX(o." + ClimateObservation.OBSERVATION_DATE + ") FROM " + ClimateObservation.TABLE_NAME + " o WHERE o."
					+ ClimateObservation.CLIMATE_LOCATION + ".id = :location AND o." + ClimateObservation.OBSERVATION_DATE + " <= :upTo",
				Date.class)
			.setParameter("location", locationId)
			.setParameter("upTo", ClimateCalendar.toDate(upTo))
			.getSingleResult();
		return ClimateCalendar.toLocalDate(latest);
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

		Map<LocalDate, ClimateObservation> rowsByDay = getByLocationAndPeriod(location, first, last);

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

	/**
	 * @return the location's rows between {@code from} and {@code to} (inclusive), by day
	 */
	public Map<LocalDate, ClimateObservation> getByLocationAndPeriod(ClimateLocation location, LocalDate from, LocalDate to) {

		Map<LocalDate, ClimateObservation> rowsByDay = new HashMap<>();
		em.createQuery(
			"SELECT o FROM " + ClimateObservation.TABLE_NAME + " o WHERE o." + ClimateObservation.CLIMATE_LOCATION + " = :location AND o."
				+ ClimateObservation.OBSERVATION_DATE + " BETWEEN :from AND :to",
			ClimateObservation.class)
			.setParameter("location", location)
			.setParameter("from", ClimateCalendar.toDate(from))
			.setParameter("to", ClimateCalendar.toDate(to))
			.getResultList()
			.forEach(row -> rowsByDay.put(ClimateCalendar.toLocalDate(row.getObservationDate()), row));
		return rowsByDay;
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

	/**
	 * @return a page of the observations matching the criteria, with their locations (and reporting users, for exports)
	 *         fetched. Sorted by the given properties, then newest day first and by location.
	 */
	public List<ClimateObservation> getPage(
		ClimateObservationCriteria criteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties,
		boolean withReportingUser) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ClimateObservation> cq = cb.createQuery(ClimateObservation.class);
		Root<ClimateObservation> root = cq.from(ClimateObservation.class);
		root.fetch(ClimateObservation.CLIMATE_LOCATION);
		if (withReportingUser) {
			root.fetch(ClimateObservation.REPORTING_USER, JoinType.LEFT);
		}
		Predicate filter = buildCriteriaFilter(criteria, cb, cq, root);
		if (filter != null) {
			cq.where(filter);
		}

		List<Order> order = new ArrayList<>();
		if (sortProperties != null) {
			for (SortProperty sortProperty : sortProperties) {
				if (SORTABLE.contains(sortProperty.propertyName)) {
					Expression<?> property = root.get(sortProperty.propertyName);
					order.add(sortProperty.ascending ? cb.asc(property) : cb.desc(property));
				}
			}
		}
		order.add(cb.desc(root.get(ClimateObservation.OBSERVATION_DATE)));
		order.add(cb.asc(root.get(ClimateObservation.CLIMATE_LOCATION).get(ClimateLocation.ID)));
		cq.orderBy(order);

		return QueryHelper.getResultList(em, cq, first, max);
	}

	public long count(ClimateObservationCriteria criteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ClimateObservation> root = cq.from(ClimateObservation.class);
		cq.select(cb.count(root));
		Predicate filter = buildCriteriaFilter(criteria, cb, cq, root);
		if (filter != null) {
			cq.where(filter);
		}
		return em.createQuery(cq).getSingleResult();
	}

	private static final Set<String> SORTABLE = new HashSet<>(
		Arrays.asList(
			ClimateObservation.OBSERVATION_DATE,
			ClimateObservation.TEMPERATURE_MIN,
			ClimateObservation.TEMPERATURE_MEAN,
			ClimateObservation.TEMPERATURE_MAX,
			ClimateObservation.RAINFALL,
			ClimateObservation.RELATIVE_HUMIDITY,
			ClimateObservation.HUMIDITY_MEASURE,
			ClimateObservation.SOURCE,
			ClimateObservation.SOURCE_DETAIL));

	/**
	 * Region and district select the locations of their districts (archived ones too), of their environments (not
	 * deleted), or both; an environment selects its own location. All places without a region or district selects every
	 * location, including those nothing links to any more.
	 */
	private Predicate buildCriteriaFilter(ClimateObservationCriteria criteria, CriteriaBuilder cb, CriteriaQuery<?> cq, Root<ClimateObservation> root) {

		Predicate filter = null;
		Path<Long> locationId = root.get(ClimateObservation.CLIMATE_LOCATION).get(ClimateLocation.ID);

		if (criteria.getEnvironment() != null) {
			Subquery<Long> environmentLocation = cq.subquery(Long.class);
			Root<Environment> environment = environmentLocation.from(Environment.class);
			environmentLocation.select(environment.get(Environment.CLIMATE_LOCATION).get(ClimateLocation.ID))
				.where(cb.equal(environment.get(Environment.UUID), criteria.getEnvironment().getUuid()));
			filter = locationId.in(environmentLocation);
		} else {
			ClimatePlaceType placeType = criteria.getPlaceType() != null ? criteria.getPlaceType() : ClimatePlaceType.ALL;
			boolean scoped = criteria.getRegion() != null || criteria.getDistrict() != null;
			if (placeType == ClimatePlaceType.DISTRICTS) {
				filter = locationId.in(districtLocations(criteria, cb, cq));
			} else if (placeType == ClimatePlaceType.ENVIRONMENTS) {
				filter = locationId.in(environmentLocations(criteria, cb, cq));
			} else if (scoped) {
				filter = cb.or(locationId.in(districtLocations(criteria, cb, cq)), locationId.in(environmentLocations(criteria, cb, cq)));
			}
		}

		if (criteria.getDateFrom() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.greaterThanOrEqualTo(root.get(ClimateObservation.OBSERVATION_DATE), startOfDay(criteria.getDateFrom())));
		}
		if (criteria.getDateTo() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.lessThanOrEqualTo(root.get(ClimateObservation.OBSERVATION_DATE), startOfDay(criteria.getDateTo())));
		}
		if (criteria.getSource() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(root.get(ClimateObservation.SOURCE), criteria.getSource()));
		}
		return filter;
	}

	private static Subquery<Long> districtLocations(ClimateObservationCriteria criteria, CriteriaBuilder cb, CriteriaQuery<?> cq) {

		Subquery<Long> subquery = cq.subquery(Long.class);
		Root<District> district = subquery.from(District.class);
		subquery.select(district.get(District.CLIMATE_LOCATION).get(ClimateLocation.ID));
		if (criteria.getDistrict() != null) {
			subquery.where(cb.equal(district.get(District.UUID), criteria.getDistrict().getUuid()));
		} else if (criteria.getRegion() != null) {
			subquery.where(cb.equal(district.get(District.REGION).get(Region.UUID), criteria.getRegion().getUuid()));
		}
		return subquery;
	}

	private static Subquery<Long> environmentLocations(ClimateObservationCriteria criteria, CriteriaBuilder cb, CriteriaQuery<?> cq) {

		Subquery<Long> subquery = cq.subquery(Long.class);
		Root<Environment> environment = subquery.from(Environment.class);
		subquery.select(environment.get(Environment.CLIMATE_LOCATION).get(ClimateLocation.ID));
		Predicate filter = cb.isFalse(environment.get(Environment.DELETED));
		Join<Environment, Location> location = environment.join(Environment.LOCATION);
		if (criteria.getDistrict() != null) {
			filter = cb.and(filter, cb.equal(location.get(Location.DISTRICT).get(District.UUID), criteria.getDistrict().getUuid()));
		} else if (criteria.getRegion() != null) {
			filter = cb.and(filter, cb.equal(location.get(Location.REGION).get(Region.UUID), criteria.getRegion().getUuid()));
		}
		subquery.where(filter);
		return subquery;
	}

	private static Date startOfDay(Date date) {
		return ClimateCalendar.toDate(ClimateCalendar.toLocalDate(date));
	}

	/**
	 * @return the values the observation held before later saves replaced them, newest first, from the change history
	 *         that the database's versioning trigger keeps (PostgreSQL only)
	 */
	public List<ClimateEarlierValueDto> getEarlierValues(String uuid, int max) {

		@SuppressWarnings("unchecked")
		List<Object[]> rows = em
			.createNativeQuery(
				"SELECT changedate, temperaturemin, temperaturemean, temperaturemax, rainfall, relativehumidity, humiditymeasure, source, sourcedetail"
					+ " FROM " + ClimateObservation.TABLE_NAME + "_history WHERE uuid = :uuid ORDER BY changedate DESC")
			.setParameter("uuid", uuid)
			.setMaxResults(max)
			.getResultList();
		List<ClimateEarlierValueDto> values = new ArrayList<>(rows.size());
		for (Object[] row : rows) {
			ClimateEarlierValueDto value = new ClimateEarlierValueDto();
			value.setChangeDate((Date) row[0]);
			value.setTemperatureMin(toDouble(row[1]));
			value.setTemperatureMean(toDouble(row[2]));
			value.setTemperatureMax(toDouble(row[3]));
			value.setRainfall(toDouble(row[4]));
			value.setRelativeHumidity(toDouble(row[5]));
			value.setHumidityMeasure(row[6] != null ? HumidityMeasure.valueOf((String) row[6]) : null);
			value.setSource(row[7] != null ? ClimateDataSource.valueOf((String) row[7]) : null);
			value.setSourceDetail((String) row[8]);
			values.add(value);
		}
		return values;
	}

	private static Double toDouble(Object value) {
		return value != null ? ((Number) value).doubleValue() : null;
	}

	/**
	 * @return per location of a district that is not archived (in the region, or all), year and source: the location id,
	 *         the year, the source, the days held and the earliest of them
	 */
	public List<Object[]> getDaysByYearAndSource(Long regionId) {

		String region = regionId != null ? " AND d." + District.REGION + ".id = :region" : "";
		TypedQuery<Object[]> query = em.createQuery(
			"SELECT o." + ClimateObservation.CLIMATE_LOCATION + ".id, YEAR(o." + ClimateObservation.OBSERVATION_DATE + "), o."
				+ ClimateObservation.SOURCE + ", COUNT(o), MIN(o." + ClimateObservation.OBSERVATION_DATE + ") FROM " + ClimateObservation.TABLE_NAME
				+ " o WHERE o." + ClimateObservation.CLIMATE_LOCATION + ".id IN (SELECT d." + District.CLIMATE_LOCATION + ".id FROM District d WHERE d."
				+ District.ARCHIVED + " = false" + region + ") GROUP BY o." + ClimateObservation.CLIMATE_LOCATION + ".id, YEAR(o."
				+ ClimateObservation.OBSERVATION_DATE + "), o." + ClimateObservation.SOURCE,
			Object[].class);
		if (regionId != null) {
			query.setParameter("region", regionId);
		}
		return query.getResultList();
	}

	/**
	 * @return the rows of the locations between {@code from} and {@code to} (inclusive), as days by location id; a
	 *         location without rows is left out
	 */
	public Map<Long, Map<LocalDate, ClimateDay>> getDays(Collection<Long> locationIds, LocalDate from, LocalDate to) {

		Map<Long, Map<LocalDate, ClimateDay>> daysByLocation = new HashMap<>();
		if (locationIds.isEmpty()) {
			return daysByLocation;
		}
		for (List<Long> batch : ListUtils.partition(new ArrayList<>(locationIds), ModelConstants.PARAMETER_LIMIT)) {
			em.createQuery(
				"SELECT o." + ClimateObservation.CLIMATE_LOCATION + ".id, o." + ClimateObservation.OBSERVATION_DATE + ", o."
					+ ClimateObservation.TEMPERATURE_MIN + ", o." + ClimateObservation.TEMPERATURE_MAX + ", o." + ClimateObservation.TEMPERATURE_MEAN
					+ ", o." + ClimateObservation.RAINFALL + ", o." + ClimateObservation.RELATIVE_HUMIDITY + ", o." + ClimateObservation.HUMIDITY_MEASURE
					+ " FROM " + ClimateObservation.TABLE_NAME + " o WHERE o." + ClimateObservation.CLIMATE_LOCATION + ".id IN :locations AND o."
					+ ClimateObservation.OBSERVATION_DATE + " BETWEEN :from AND :to",
				Object[].class)
				.setParameter("locations", batch)
				.setParameter("from", ClimateCalendar.toDate(from))
				.setParameter("to", ClimateCalendar.toDate(to))
				.getResultList()
				.forEach(row -> {
					LocalDate day = ClimateCalendar.toLocalDate((Date) row[1]);
					daysByLocation.computeIfAbsent((Long) row[0], id -> new HashMap<>())
						.put(
							day,
							new ClimateDay(
								day,
								(Double) row[2],
								(Double) row[3],
								(Double) row[4],
								(Double) row[5],
								(Double) row[6],
								(HumidityMeasure) row[7]));
				});
		}
		return daysByLocation;
	}

	/**
	 * @return true when at least one row of the locations between {@code from} and {@code to} (inclusive) comes from the
	 *         source, for example to show the OpenWeather credit
	 */
	public boolean hasSource(Collection<Long> locationIds, LocalDate from, LocalDate to, ClimateDataSource source) {

		for (List<Long> batch : ListUtils.partition(new ArrayList<>(locationIds), ModelConstants.PARAMETER_LIMIT)) {
			boolean found = !em
				.createQuery(
					"SELECT o.id FROM " + ClimateObservation.TABLE_NAME + " o WHERE o." + ClimateObservation.CLIMATE_LOCATION + ".id IN :locations AND o."
						+ ClimateObservation.OBSERVATION_DATE + " BETWEEN :from AND :to AND o." + ClimateObservation.SOURCE + " = :source",
					Long.class)
				.setParameter("locations", batch)
				.setParameter("from", ClimateCalendar.toDate(from))
				.setParameter("to", ClimateCalendar.toDate(to))
				.setParameter("source", source)
				.setMaxResults(1)
				.getResultList()
				.isEmpty();
			if (found) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return a value that changes whenever a row of the locations between {@code from} and {@code to} is added, changed or
	 *         deleted: the number of rows and the latest change date. The normals cache compares it before using an entry.
	 */
	public String getFingerprint(Collection<Long> locationIds, LocalDate from, LocalDate to) {

		long count = 0;
		long latestChange = 0;
		for (List<Long> batch : ListUtils.partition(new ArrayList<>(locationIds), ModelConstants.PARAMETER_LIMIT)) {
			Object[] row = em
				.createQuery(
					"SELECT COUNT(o), MAX(o." + ClimateObservation.CHANGE_DATE + ") FROM " + ClimateObservation.TABLE_NAME + " o WHERE o."
						+ ClimateObservation.CLIMATE_LOCATION + ".id IN :locations AND o." + ClimateObservation.OBSERVATION_DATE + " BETWEEN :from AND :to",
					Object[].class)
				.setParameter("locations", batch)
				.setParameter("from", ClimateCalendar.toDate(from))
				.setParameter("to", ClimateCalendar.toDate(to))
				.getSingleResult();
			count += ((Number) row[0]).longValue();
			if (row[1] != null) {
				latestChange = Math.max(latestChange, ((Date) row[1]).getTime());
			}
		}
		return count + "/" + latestChange;
	}
}
