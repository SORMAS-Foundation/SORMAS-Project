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

package de.symeda.sormas.api.climate;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;

/**
 * The weekly climate of a district, or of a region (the mean of its districts), with normal ranges, unusual weeks and
 * last year's values. Range figures use complete weeks only; completeness is shown beside them.
 */
@AuditedClass
public class ClimateWeeklySeriesDto implements Serializable {

	private static final long serialVersionUID = -2093317519734216218L;

	private final RegionReferenceDto region;
	private final DistrictReferenceDto district;
	private final boolean hasPoint;
	private final LocalDate historyFrom;
	private final int minHistoryYears;
	private final int firstReferenceYear;
	private final int lastReferenceYear;
	private final List<ClimateWeekDto> weeks;
	private final Map<ClimateVariable, Double> completeness = new EnumMap<>(ClimateVariable.class);
	private final List<ClimateWeeklySeriesDto> districtSeries = new ArrayList<>();
	private int leadWeeks;
	private boolean openWeatherData;

	public ClimateWeeklySeriesDto(
		RegionReferenceDto region,
		DistrictReferenceDto district,
		boolean hasPoint,
		LocalDate historyFrom,
		int minHistoryYears,
		int firstReferenceYear,
		int lastReferenceYear,
		List<ClimateWeekDto> weeks) {

		this.region = region;
		this.district = district;
		this.hasPoint = hasPoint;
		this.historyFrom = historyFrom;
		this.minHistoryYears = minHistoryYears;
		this.firstReferenceYear = firstReferenceYear;
		this.lastReferenceYear = lastReferenceYear;
		this.weeks = weeks;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	/**
	 * @return the district, or null for the region's series
	 */
	public DistrictReferenceDto getDistrict() {
		return district;
	}

	/**
	 * @return false when the district has no climate point (only imports and manual entries fill it); for a region, when
	 *         none of its districts has one
	 */
	public boolean isHasPoint() {
		return hasPoint;
	}

	/**
	 * @return the earliest day held; for a region, that of the district with the shortest history; null without data
	 */
	public LocalDate getHistoryFrom() {
		return historyFrom;
	}

	/**
	 * @return the time from the first day held to {@code today}, for "Building climate history"
	 */
	public Period getHeldPeriod(LocalDate today) {
		return historyFrom == null || !historyFrom.isBefore(today) ? Period.ZERO : Period.between(historyFrom, today);
	}

	public int getMinHistoryYears() {
		return minHistoryYears;
	}

	/**
	 * @return the first of the epi years the normal ranges come from
	 */
	public int getFirstReferenceYear() {
		return firstReferenceYear;
	}

	public int getLastReferenceYear() {
		return lastReferenceYear;
	}

	/**
	 * @return every week returned: the lead weeks asked for the lag, then the weeks of the period
	 */
	public List<ClimateWeekDto> getWeeks() {
		return Collections.unmodifiableList(weeks);
	}

	/**
	 * @return the weeks of the period, without the lead weeks; the range figures below use these
	 */
	public List<ClimateWeekDto> getShownWeeks() {
		return Collections.unmodifiableList(weeks.subList(Math.min(leadWeeks, weeks.size()), weeks.size()));
	}

	/**
	 * @return how many weeks before the period start {@link #getWeeks()}, for the dashboard's lag (decision 126)
	 */
	public int getLeadWeeks() {
		return leadWeeks;
	}

	public void setLeadWeeks(int leadWeeks) {
		this.leadWeeks = leadWeeks;
	}

	/**
	 * @return true when a day of the weeks returned comes from OpenWeather, so the screen shows its credit (decision 102)
	 */
	public boolean isOpenWeatherData() {
		return openWeatherData;
	}

	public void setOpenWeatherData(boolean openWeatherData) {
		this.openWeatherData = openWeatherData;
	}

	/**
	 * @return the share of days in the period (lead weeks left out), up to yesterday, with a value of the variable, from 0
	 *         to 1; for a region the mean of its districts; null when the period has no past days
	 */
	public Double getCompleteness(ClimateVariable variable) {
		return completeness.get(variable);
	}

	public void setCompleteness(ClimateVariable variable, Double share) {
		completeness.put(variable, share);
	}

	/**
	 * @return the series of each of the region's districts, by name, when they were asked for
	 */
	public List<ClimateWeeklySeriesDto> getDistrictSeries() {
		return districtSeries;
	}

	/**
	 * @return true when at least one week has a normal range for the variable
	 */
	public boolean hasNormals(ClimateVariable variable) {
		return getShownWeeks().stream().anyMatch(week -> week.get(variable).getNormal() != null);
	}

	public long countUnusualWeeks(ClimateVariable variable) {
		return getShownWeeks().stream().filter(week -> week.get(variable).isUnusual()).count();
	}

	/**
	 * @return the total rainfall of the complete weeks, or null when there are none
	 */
	public Double getRainfallTotal() {
		List<Double> values = completeValues(ClimateVariable.RAINFALL, null);
		return values.isEmpty() ? null : values.stream().mapToDouble(Double::doubleValue).sum();
	}

	/**
	 * @return the mean temperature of the complete weeks, or null when there are none
	 */
	public Double getTemperatureMean() {
		return mean(completeValues(ClimateVariable.TEMPERATURE, null));
	}

	/**
	 * @return the humidity measure most complete weeks have, which {@link #getHumidityMean()} uses
	 */
	public HumidityMeasure getRangeHumidityMeasure() {
		return getShownWeeks().stream()
			.filter(week -> week.getHumidity().getValue() != null)
			.collect(Collectors.groupingBy(ClimateWeekDto::getHumidityMeasure, Collectors.counting()))
			.entrySet()
			.stream()
			.max(Map.Entry.comparingByValue())
			.map(Map.Entry::getKey)
			.orElse(null);
	}

	/**
	 * @return the mean humidity of the complete weeks of {@link #getRangeHumidityMeasure()}, or null when there are none
	 */
	public Double getHumidityMean() {
		HumidityMeasure measure = getRangeHumidityMeasure();
		return measure == null ? null : mean(completeValues(ClimateVariable.HUMIDITY, measure));
	}

	private List<Double> completeValues(ClimateVariable variable, HumidityMeasure measure) {
		return getShownWeeks().stream()
			.filter(week -> measure == null || measure == week.getHumidityMeasure())
			.map(week -> week.get(variable).getValue())
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	private static Double mean(List<Double> values) {
		return values.isEmpty() ? null : values.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
	}
}
