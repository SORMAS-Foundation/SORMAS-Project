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

import de.symeda.sormas.api.audit.AuditedClass;

/**
 * One summary card of the environment Climate data tab: the last 7 days with data of one variable, compared with the
 * daily normals of the same days, or with the same days last year before normals exist. Sums and means of percentiles
 * are an approximation, accepted for the cards.
 */
@AuditedClass
public class ClimateSummaryCardDto implements Serializable {

	private static final long serialVersionUID = 5523160458217740417L;

	private ClimateVariable variable;
	private LocalDate firstDay;
	private LocalDate lastDay;
	private int days;
	private Double value;
	private HumidityMeasure humidityMeasure;
	private ClimateComparison comparison;
	private ClimateRangeDto normal;
	private ClimateRangePosition position;
	private boolean normallyDry;
	private Double lastYear;
	private Double percentDifference;
	private Double difference;

	public ClimateSummaryCardDto(ClimateVariable variable, LocalDate firstDay, LocalDate lastDay) {
		this.variable = variable;
		this.firstDay = firstDay;
		this.lastDay = lastDay;
		this.comparison = ClimateComparison.NOT_ENOUGH_DATA;
	}

	public ClimateVariable getVariable() {
		return variable;
	}

	/**
	 * @return the first of the 7 days, or null when the place holds no data
	 */
	public LocalDate getFirstDay() {
		return firstDay;
	}

	/**
	 * @return the latest day with data, up to yesterday
	 */
	public LocalDate getLastDay() {
		return lastDay;
	}

	/**
	 * @return the days of the 7 with a value
	 */
	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	/**
	 * @return the total rainfall in mm, or the mean temperature (°C) or humidity (%); null with too few days
	 */
	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	/**
	 * @return the measure of the humidity value; normals and last year's value are of the same measure
	 */
	public HumidityMeasure getHumidityMeasure() {
		return humidityMeasure;
	}

	public void setHumidityMeasure(HumidityMeasure humidityMeasure) {
		this.humidityMeasure = humidityMeasure;
	}

	public ClimateComparison getComparison() {
		return comparison;
	}

	public void setComparison(ClimateComparison comparison) {
		this.comparison = comparison;
	}

	/**
	 * @return for {@link ClimateComparison#NORMAL}: the sums (rainfall) or means of the days' p10, p50 and p90
	 */
	public ClimateRangeDto getNormal() {
		return normal;
	}

	public void setNormal(ClimateRangeDto normal) {
		this.normal = normal;
	}

	/**
	 * @return for {@link ClimateComparison#NORMAL}: where the value lies against the normal range; warning colour only
	 *         outside it
	 */
	public ClimateRangePosition getPosition() {
		return position;
	}

	public void setPosition(ClimateRangePosition position) {
		this.position = position;
	}

	/**
	 * @return true for rainfall when the normal for these days is 0 mm: the card shows the amount and "normally dry"
	 */
	public boolean isNormallyDry() {
		return normallyDry;
	}

	public void setNormallyDry(boolean normallyDry) {
		this.normallyDry = normallyDry;
	}

	/**
	 * @return for {@link ClimateComparison#LAST_YEAR}: the same days' value a year earlier
	 */
	public Double getLastYear() {
		return lastYear;
	}

	public void setLastYear(Double lastYear) {
		this.lastYear = lastYear;
	}

	/**
	 * @return rainfall only: how much more (positive) or less rain than the normal p50 or last year, in percent; null when
	 *         that is 0 mm
	 */
	public Double getPercentDifference() {
		return percentDifference;
	}

	public void setPercentDifference(Double percentDifference) {
		this.percentDifference = percentDifference;
	}

	/**
	 * @return temperature (°C) and humidity (percentage points): the value minus the normal p50 or last year's value
	 */
	public Double getDifference() {
		return difference;
	}

	public void setDifference(Double difference) {
		this.difference = difference;
	}
}
