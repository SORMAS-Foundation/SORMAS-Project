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

import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.utils.EpiWeek;

/**
 * The climate of one district or region in one epi week. Weekly values are rolled up from the daily rows when asked for,
 * never stored.
 */
@AuditedClass
public class ClimateWeekDto implements Serializable {

	private static final long serialVersionUID = 3086146457722390046L;

	private final EpiWeek week;
	private final ClimateWeekValueDto rainfall;
	private final ClimateWeekValueDto temperature;
	private final ClimateWeekValueDto humidity;
	private final Double temperatureMin;
	private final Double temperatureMax;
	private final HumidityMeasure humidityMeasure;

	public ClimateWeekDto(
		EpiWeek week,
		ClimateWeekValueDto rainfall,
		ClimateWeekValueDto temperature,
		ClimateWeekValueDto humidity,
		Double temperatureMin,
		Double temperatureMax,
		HumidityMeasure humidityMeasure) {

		this.week = week;
		this.rainfall = rainfall;
		this.temperature = temperature;
		this.humidity = humidity;
		this.temperatureMin = temperatureMin;
		this.temperatureMax = temperatureMax;
		this.humidityMeasure = humidityMeasure;
	}

	public EpiWeek getWeek() {
		return week;
	}

	/**
	 * @return the week's total rainfall, in mm
	 */
	public ClimateWeekValueDto getRainfall() {
		return rainfall;
	}

	/**
	 * @return the mean of the daily mean temperatures, in °C
	 */
	public ClimateWeekValueDto getTemperature() {
		return temperature;
	}

	/**
	 * @return the mean daily humidity, in %, of {@link #getHumidityMeasure()}; its normal and last year's value are of the
	 *         same measure
	 */
	public ClimateWeekValueDto getHumidity() {
		return humidity;
	}

	public ClimateWeekValueDto get(ClimateVariable variable) {
		switch (variable) {
		case RAINFALL:
			return rainfall;
		case TEMPERATURE:
			return temperature;
		case HUMIDITY:
			return humidity;
		default:
			throw new IllegalArgumentException(variable.name());
		}
	}

	/**
	 * @return the mean of the daily minimums, in °C, for the hover; null for a gap
	 */
	public Double getTemperatureMin() {
		return temperatureMin;
	}

	/**
	 * @return the mean of the daily maximums, in °C, for the hover; null for a gap
	 */
	public Double getTemperatureMax() {
		return temperatureMax;
	}

	/**
	 * @return the humidity measure of the week's value, or of its normal when the week is a gap; null when neither exists
	 */
	public HumidityMeasure getHumidityMeasure() {
		return humidityMeasure;
	}
}
