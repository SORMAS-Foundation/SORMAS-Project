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
 * One day of an environment's climate: the best-available values (all empty for a day without data) and the daily
 * normal ranges for that day.
 */
@AuditedClass
public class ClimateDayDto implements Serializable {

	private static final long serialVersionUID = -2786430120372264407L;

	private final LocalDate date;
	private Double temperatureMin;
	private Double temperatureMax;
	private Double temperatureMean;
	private Double rainfall;
	private Double relativeHumidity;
	private HumidityMeasure humidityMeasure;
	private ClimateDataSource source;
	private ClimateRangeDto rainfallNormal;
	private ClimateRangeDto temperatureNormal;
	private ClimateRangeDto humidityNormal;

	public ClimateDayDto(LocalDate date) {
		this.date = date;
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

	public void setTemperatures(Double min, Double mean, Double max) {
		this.temperatureMin = min;
		this.temperatureMean = mean;
		this.temperatureMax = max;
	}

	public Double getRainfall() {
		return rainfall;
	}

	public void setRainfall(Double rainfall) {
		this.rainfall = rainfall;
	}

	public Double getRelativeHumidity() {
		return relativeHumidity;
	}

	/**
	 * @return the measure of the day's humidity, or of its normal when the day has none
	 */
	public HumidityMeasure getHumidityMeasure() {
		return humidityMeasure;
	}

	public void setHumidity(Double relativeHumidity, HumidityMeasure humidityMeasure) {
		this.relativeHumidity = relativeHumidity;
		this.humidityMeasure = humidityMeasure;
	}

	/**
	 * @return where the day's values come from, or null for a day without data
	 */
	public ClimateDataSource getSource() {
		return source;
	}

	public void setSource(ClimateDataSource source) {
		this.source = source;
	}

	public ClimateRangeDto getRainfallNormal() {
		return rainfallNormal;
	}

	public void setRainfallNormal(ClimateRangeDto rainfallNormal) {
		this.rainfallNormal = rainfallNormal;
	}

	/**
	 * @return the normal range of the daily mean temperature
	 */
	public ClimateRangeDto getTemperatureNormal() {
		return temperatureNormal;
	}

	public void setTemperatureNormal(ClimateRangeDto temperatureNormal) {
		this.temperatureNormal = temperatureNormal;
	}

	/**
	 * @return the normal range of {@link #getHumidityMeasure()}
	 */
	public ClimateRangeDto getHumidityNormal() {
		return humidityNormal;
	}

	public void setHumidityNormal(ClimateRangeDto humidityNormal) {
		this.humidityNormal = humidityNormal;
	}
}
