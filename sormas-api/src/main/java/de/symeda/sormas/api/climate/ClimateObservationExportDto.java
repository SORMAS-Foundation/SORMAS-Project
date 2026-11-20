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

import de.symeda.sormas.api.audit.AuditIncludeProperty;
import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.uuid.HasUuid;

/**
 * One row of the Climate data screen's export: every field of an observation, the places that use its location, and the
 * credit OpenWeather's terms ask for on OpenWeather rows.
 */
@AuditedClass
public class ClimateObservationExportDto implements Serializable, HasUuid {

	private static final long serialVersionUID = 3391270532804455163L;

	public static final String I18N_PREFIX = "ClimateObservationExport";

	/** The credit OpenWeather's terms require wherever its data is shown or exported. */
	public static final String OPENWEATHER_CREDIT = "Weather data provided by OpenWeather (openweathermap.org)";

	@AuditIncludeProperty
	private String uuid;
	private Date observationDate;
	private String regions;
	private String districts;
	private String environments;
	private Double latitude;
	private Double longitude;
	private Double temperatureMin;
	private Double temperatureMean;
	private Double temperatureMax;
	private Double rainfall;
	private Double relativeHumidity;
	private HumidityMeasure humidityMeasure;
	private ClimateDataSource source;
	private String sourceDetail;
	private String reportingUser;
	private Date changeDate;

	@Order(0)
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Order(1)
	public Date getObservationDate() {
		return observationDate;
	}

	public void setObservationDate(Date observationDate) {
		this.observationDate = observationDate;
	}

	/**
	 * @return the regions of the districts that use the location, comma-separated
	 */
	@Order(2)
	public String getRegions() {
		return regions;
	}

	public void setRegions(String regions) {
		this.regions = regions;
	}

	@Order(3)
	public String getDistricts() {
		return districts;
	}

	public void setDistricts(String districts) {
		this.districts = districts;
	}

	@Order(4)
	public String getEnvironments() {
		return environments;
	}

	public void setEnvironments(String environments) {
		this.environments = environments;
	}

	@Order(5)
	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	@Order(6)
	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@Order(7)
	public Double getTemperatureMin() {
		return temperatureMin;
	}

	public void setTemperatureMin(Double temperatureMin) {
		this.temperatureMin = temperatureMin;
	}

	@Order(8)
	public Double getTemperatureMean() {
		return temperatureMean;
	}

	public void setTemperatureMean(Double temperatureMean) {
		this.temperatureMean = temperatureMean;
	}

	@Order(9)
	public Double getTemperatureMax() {
		return temperatureMax;
	}

	public void setTemperatureMax(Double temperatureMax) {
		this.temperatureMax = temperatureMax;
	}

	@Order(10)
	public Double getRainfall() {
		return rainfall;
	}

	public void setRainfall(Double rainfall) {
		this.rainfall = rainfall;
	}

	@Order(11)
	public Double getRelativeHumidity() {
		return relativeHumidity;
	}

	public void setRelativeHumidity(Double relativeHumidity) {
		this.relativeHumidity = relativeHumidity;
	}

	@Order(12)
	public HumidityMeasure getHumidityMeasure() {
		return humidityMeasure;
	}

	public void setHumidityMeasure(HumidityMeasure humidityMeasure) {
		this.humidityMeasure = humidityMeasure;
	}

	@Order(13)
	public ClimateDataSource getSource() {
		return source;
	}

	public void setSource(ClimateDataSource source) {
		this.source = source;
	}

	@Order(14)
	public String getSourceDetail() {
		return sourceDetail;
	}

	public void setSourceDetail(String sourceDetail) {
		this.sourceDetail = sourceDetail;
	}

	@Order(15)
	public String getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(String reportingUser) {
		this.reportingUser = reportingUser;
	}

	@Order(16)
	public Date getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}

	/**
	 * @return the OpenWeather credit on OpenWeather rows, empty on others
	 */
	@Order(17)
	public String getCredit() {
		return source == ClimateDataSource.OPENWEATHER ? OPENWEATHER_CREDIT : null;
	}
}
