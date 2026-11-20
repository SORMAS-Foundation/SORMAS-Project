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
import java.util.Date;

import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.utils.Order;

/**
 * One day of the environment Climate data tab's export: the best-available values with their source and the daily normal
 * ranges. Site readings are not part of it (they are never mixed with daily values).
 */
@AuditedClass
public class ClimateDailyExportDto implements Serializable {

	private static final long serialVersionUID = 6604129953520138735L;

	public static final String I18N_PREFIX = "ClimateDailyExport";

	private Date date;
	private Double temperatureMin;
	private Double temperatureMean;
	private Double temperatureMax;
	private Double rainfall;
	private Double relativeHumidity;
	private HumidityMeasure humidityMeasure;
	private ClimateDataSource source;
	private String sourceDetail;
	private Double rainfallP10;
	private Double rainfallP50;
	private Double rainfallP90;
	private Double temperatureP10;
	private Double temperatureP50;
	private Double temperatureP90;
	private Double humidityP10;
	private Double humidityP50;
	private Double humidityP90;
	private String normalsFrom;

	@Order(0)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Order(1)
	public Double getTemperatureMin() {
		return temperatureMin;
	}

	public void setTemperatureMin(Double temperatureMin) {
		this.temperatureMin = temperatureMin;
	}

	@Order(2)
	public Double getTemperatureMean() {
		return temperatureMean;
	}

	public void setTemperatureMean(Double temperatureMean) {
		this.temperatureMean = temperatureMean;
	}

	@Order(3)
	public Double getTemperatureMax() {
		return temperatureMax;
	}

	public void setTemperatureMax(Double temperatureMax) {
		this.temperatureMax = temperatureMax;
	}

	@Order(4)
	public Double getRainfall() {
		return rainfall;
	}

	public void setRainfall(Double rainfall) {
		this.rainfall = rainfall;
	}

	@Order(5)
	public Double getRelativeHumidity() {
		return relativeHumidity;
	}

	public void setRelativeHumidity(Double relativeHumidity) {
		this.relativeHumidity = relativeHumidity;
	}

	@Order(6)
	public HumidityMeasure getHumidityMeasure() {
		return humidityMeasure;
	}

	public void setHumidityMeasure(HumidityMeasure humidityMeasure) {
		this.humidityMeasure = humidityMeasure;
	}

	@Order(7)
	public ClimateDataSource getSource() {
		return source;
	}

	public void setSource(ClimateDataSource source) {
		this.source = source;
	}

	@Order(8)
	public String getSourceDetail() {
		return sourceDetail;
	}

	public void setSourceDetail(String sourceDetail) {
		this.sourceDetail = sourceDetail;
	}

	@Order(9)
	public Double getRainfallP10() {
		return rainfallP10;
	}

	public void setRainfallP10(Double rainfallP10) {
		this.rainfallP10 = rainfallP10;
	}

	@Order(10)
	public Double getRainfallP50() {
		return rainfallP50;
	}

	public void setRainfallP50(Double rainfallP50) {
		this.rainfallP50 = rainfallP50;
	}

	@Order(11)
	public Double getRainfallP90() {
		return rainfallP90;
	}

	public void setRainfallP90(Double rainfallP90) {
		this.rainfallP90 = rainfallP90;
	}

	/**
	 * @return the 10th percentile of the daily mean temperature
	 */
	@Order(12)
	public Double getTemperatureP10() {
		return temperatureP10;
	}

	public void setTemperatureP10(Double temperatureP10) {
		this.temperatureP10 = temperatureP10;
	}

	@Order(13)
	public Double getTemperatureP50() {
		return temperatureP50;
	}

	public void setTemperatureP50(Double temperatureP50) {
		this.temperatureP50 = temperatureP50;
	}

	@Order(14)
	public Double getTemperatureP90() {
		return temperatureP90;
	}

	public void setTemperatureP90(Double temperatureP90) {
		this.temperatureP90 = temperatureP90;
	}

	/**
	 * @return the 10th percentile of humidity of {@link #getHumidityMeasure()}
	 */
	@Order(15)
	public Double getHumidityP10() {
		return humidityP10;
	}

	public void setHumidityP10(Double humidityP10) {
		this.humidityP10 = humidityP10;
	}

	@Order(16)
	public Double getHumidityP50() {
		return humidityP50;
	}

	public void setHumidityP50(Double humidityP50) {
		this.humidityP50 = humidityP50;
	}

	@Order(17)
	public Double getHumidityP90() {
		return humidityP90;
	}

	public void setHumidityP90(Double humidityP90) {
		this.humidityP90 = humidityP90;
	}

	/**
	 * @return the place whose history the normal ranges come from: the environment, or its district
	 */
	@Order(18)
	public String getNormalsFrom() {
		return normalsFrom;
	}

	public void setNormalsFrom(String normalsFrom) {
		this.normalsFrom = normalsFrom;
	}

	/**
	 * @return the credit OpenWeather's terms require, on rows whose values came from OpenWeather
	 */
	@Order(19)
	public String getCredit() {
		return source == ClimateDataSource.OPENWEATHER ? ClimateObservationExportDto.OPENWEATHER_CREDIT : null;
	}
}
