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

import de.symeda.sormas.api.climate.HumidityMeasure;
import de.symeda.sormas.api.utils.EpiWeek;

/**
 * One epi week of one location's daily rows rolled up. A value is null (a gap) when fewer than
 * {@link ClimateStatistics#MIN_DAYS_PER_WEEK} days have it; the day counts say how many days did.
 */
public class ClimateWeekFigures {

	private final EpiWeek week;
	private final Double rainfall;
	private final Double temperatureMean;
	private final Double temperatureMin;
	private final Double temperatureMax;
	private final Double humidity;
	private final HumidityMeasure humidityMeasure;
	private final int rainfallDays;
	private final int temperatureDays;
	private final int humidityDays;

	public ClimateWeekFigures(
		EpiWeek week,
		Double rainfall,
		Double temperatureMean,
		Double temperatureMin,
		Double temperatureMax,
		Double humidity,
		HumidityMeasure humidityMeasure,
		int rainfallDays,
		int temperatureDays,
		int humidityDays) {

		this.week = week;
		this.rainfall = rainfall;
		this.temperatureMean = temperatureMean;
		this.temperatureMin = temperatureMin;
		this.temperatureMax = temperatureMax;
		this.humidity = humidity;
		this.humidityMeasure = humidityMeasure;
		this.rainfallDays = rainfallDays;
		this.temperatureDays = temperatureDays;
		this.humidityDays = humidityDays;
	}

	public EpiWeek getWeek() {
		return week;
	}

	/**
	 * @return the week's total, in mm
	 */
	public Double getRainfall() {
		return rainfall;
	}

	/**
	 * @return the mean of the daily means, in °C
	 */
	public Double getTemperatureMean() {
		return temperatureMean;
	}

	/**
	 * @return the mean of the daily minimums, in °C
	 */
	public Double getTemperatureMin() {
		return temperatureMin;
	}

	/**
	 * @return the mean of the daily maximums, in °C
	 */
	public Double getTemperatureMax() {
		return temperatureMax;
	}

	/**
	 * @return the mean of the daily values of {@link #getHumidityMeasure()}, in %
	 */
	public Double getHumidity() {
		return humidity;
	}

	/**
	 * @return the measure the humidity was taken from, or null when the week has no humidity
	 */
	public HumidityMeasure getHumidityMeasure() {
		return humidityMeasure;
	}

	public int getRainfallDays() {
		return rainfallDays;
	}

	/**
	 * @return the days with a mean temperature
	 */
	public int getTemperatureDays() {
		return temperatureDays;
	}

	/**
	 * @return the days with humidity of the measure used, or of the measure most days have when the week is a gap
	 */
	public int getHumidityDays() {
		return humidityDays;
	}
}
