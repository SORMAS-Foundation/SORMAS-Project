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

import de.symeda.sormas.api.climate.HumidityMeasure;

/**
 * One day's values as a provider returned them, before they are written to a climate observation.
 */
public class ClimateDay {

	private final LocalDate date;
	private final Double temperatureMin;
	private final Double temperatureMax;
	private final Double temperatureMean;
	private final Double rainfall;
	private final Double relativeHumidity;
	private final HumidityMeasure humidityMeasure;

	public ClimateDay(
		LocalDate date,
		Double temperatureMin,
		Double temperatureMax,
		Double temperatureMean,
		Double rainfall,
		Double relativeHumidity,
		HumidityMeasure humidityMeasure) {

		this.date = date;
		this.temperatureMin = temperatureMin;
		this.temperatureMax = temperatureMax;
		this.temperatureMean = temperatureMean;
		this.rainfall = rainfall;
		this.relativeHumidity = relativeHumidity;
		this.humidityMeasure = humidityMeasure;
	}

	public LocalDate getDate() {
		return date;
	}

	public Double getTemperatureMin() {
		return temperatureMin;
	}

	public Double getTemperatureMax() {
		return temperatureMax;
	}

	public Double getTemperatureMean() {
		return temperatureMean;
	}

	public Double getRainfall() {
		return rainfall;
	}

	public Double getRelativeHumidity() {
		return relativeHumidity;
	}

	public HumidityMeasure getHumidityMeasure() {
		return humidityMeasure;
	}
}
