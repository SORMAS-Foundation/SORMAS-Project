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

/**
 * One day's climate values for one place, as they arrive by CSV import, push or manual entry. The place is given by
 * exactly one key: a district (by uuid or external ID), an environment (its own climate record), or a climate location.
 * The date is a calendar day written {@code yyyy-mm-dd}, so no time zone can shift it.
 */
@AuditedClass
public class ClimateObservationEntryDto implements Serializable {

	private static final long serialVersionUID = 4196503228215893377L;

	public static final String I18N_PREFIX = "ClimateObservationEntry";

	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String DISTRICT_UUID = "districtUuid";
	public static final String DISTRICT_EXTERNAL_ID = "districtExternalId";
	public static final String ENVIRONMENT_UUID = "environmentUuid";
	public static final String CLIMATE_LOCATION_UUID = "climateLocationUuid";
	public static final String DATE = "date";
	public static final String TEMPERATURE_MIN = "temperatureMin";
	public static final String TEMPERATURE_MAX = "temperatureMax";
	public static final String TEMPERATURE_MEAN = "temperatureMean";
	public static final String RAINFALL = "rainfall";
	public static final String RELATIVE_HUMIDITY = "relativeHumidity";
	public static final String HUMIDITY_MEASURE = "humidityMeasure";
	public static final String SOURCE_DETAIL = "sourceDetail";

	private String districtUuid;
	private String districtExternalId;
	private String environmentUuid;
	private String climateLocationUuid;
	private String date;
	private Double temperatureMin;
	private Double temperatureMax;
	private Double temperatureMean;
	private Double rainfall;
	private Double relativeHumidity;
	private HumidityMeasure humidityMeasure;
	private String sourceDetail;

	public String getDistrictUuid() {
		return districtUuid;
	}

	public void setDistrictUuid(String districtUuid) {
		this.districtUuid = districtUuid;
	}

	public String getDistrictExternalId() {
		return districtExternalId;
	}

	public void setDistrictExternalId(String districtExternalId) {
		this.districtExternalId = districtExternalId;
	}

	public String getEnvironmentUuid() {
		return environmentUuid;
	}

	public void setEnvironmentUuid(String environmentUuid) {
		this.environmentUuid = environmentUuid;
	}

	public String getClimateLocationUuid() {
		return climateLocationUuid;
	}

	public void setClimateLocationUuid(String climateLocationUuid) {
		this.climateLocationUuid = climateLocationUuid;
	}

	/**
	 * @return the calendar day, written {@code yyyy-mm-dd}
	 */
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Double getTemperatureMin() {
		return temperatureMin;
	}

	public void setTemperatureMin(Double temperatureMin) {
		this.temperatureMin = temperatureMin;
	}

	public Double getTemperatureMax() {
		return temperatureMax;
	}

	public void setTemperatureMax(Double temperatureMax) {
		this.temperatureMax = temperatureMax;
	}

	public Double getTemperatureMean() {
		return temperatureMean;
	}

	public void setTemperatureMean(Double temperatureMean) {
		this.temperatureMean = temperatureMean;
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

	public void setRelativeHumidity(Double relativeHumidity) {
		this.relativeHumidity = relativeHumidity;
	}

	public HumidityMeasure getHumidityMeasure() {
		return humidityMeasure;
	}

	public void setHumidityMeasure(HumidityMeasure humidityMeasure) {
		this.humidityMeasure = humidityMeasure;
	}

	/**
	 * @return the agency, station or file the values came from; empty means the user who sent them
	 */
	public String getSourceDetail() {
		return sourceDetail;
	}

	public void setSourceDetail(String sourceDetail) {
		this.sourceDetail = sourceDetail;
	}
}
