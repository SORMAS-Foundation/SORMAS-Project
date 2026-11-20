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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.audit.AuditIncludeProperty;
import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.uuid.HasUuid;

/**
 * One climate observation (one location and day) as the Climate data screen lists it, with the places that use the
 * location. Also describes a location and day that has no row yet ({@link #getUuid()} null), so a form can show who
 * shares the location before the first value is entered.
 */
@AuditedClass
public class ClimateObservationIndexDto implements Serializable, HasUuid {

	private static final long serialVersionUID = -6904021719347000371L;

	public static final String I18N_PREFIX = "ClimateObservation";

	public static final String PLACE = "place";
	public static final String OBSERVATION_DATE = "observationDate";
	public static final String TEMPERATURE_MIN = "temperatureMin";
	public static final String TEMPERATURE_MEAN = "temperatureMean";
	public static final String TEMPERATURE_MAX = "temperatureMax";
	public static final String RAINFALL = "rainfall";
	public static final String RELATIVE_HUMIDITY = "relativeHumidity";
	public static final String HUMIDITY_MEASURE = "humidityMeasure";
	public static final String SOURCE = "source";
	public static final String SOURCE_DETAIL = "sourceDetail";

	@AuditIncludeProperty
	private String uuid;
	private String climateLocationUuid;
	private Double latitude;
	private Double longitude;
	private List<String> districtNames = new ArrayList<>();
	private List<String> environmentNames = new ArrayList<>();
	private Date observationDate;
	private Double temperatureMin;
	private Double temperatureMean;
	private Double temperatureMax;
	private Double rainfall;
	private Double relativeHumidity;
	private HumidityMeasure humidityMeasure;
	private ClimateDataSource source;
	private String sourceDetail;

	/**
	 * @return the observation's uuid, or null when the location has no row for the day
	 */
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getClimateLocationUuid() {
		return climateLocationUuid;
	}

	public void setClimateLocationUuid(String climateLocationUuid) {
		this.climateLocationUuid = climateLocationUuid;
	}

	/**
	 * @return the latitude of the location's point, or null for a district location without coordinates
	 */
	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the names of the districts that link to the location (archived ones too), sorted
	 */
	public List<String> getDistrictNames() {
		return districtNames;
	}

	public void setDistrictNames(List<String> districtNames) {
		this.districtNames = districtNames;
	}

	/**
	 * @return the names of the environments that link to the location (not deleted ones), sorted
	 */
	public List<String> getEnvironmentNames() {
		return environmentNames;
	}

	public void setEnvironmentNames(List<String> environmentNames) {
		this.environmentNames = environmentNames;
	}

	/**
	 * @return true when more than one district or environment uses the location, so a change is seen by all of them
	 */
	public boolean isShared() {
		return districtNames.size() + environmentNames.size() > 1;
	}

	public Date getObservationDate() {
		return observationDate;
	}

	public void setObservationDate(Date observationDate) {
		this.observationDate = observationDate;
	}

	public Double getTemperatureMin() {
		return temperatureMin;
	}

	public void setTemperatureMin(Double temperatureMin) {
		this.temperatureMin = temperatureMin;
	}

	public Double getTemperatureMean() {
		return temperatureMean;
	}

	public void setTemperatureMean(Double temperatureMean) {
		this.temperatureMean = temperatureMean;
	}

	public Double getTemperatureMax() {
		return temperatureMax;
	}

	public void setTemperatureMax(Double temperatureMax) {
		this.temperatureMax = temperatureMax;
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

	public ClimateDataSource getSource() {
		return source;
	}

	public void setSource(ClimateDataSource source) {
		this.source = source;
	}

	public String getSourceDetail() {
		return sourceDetail;
	}

	public void setSourceDetail(String sourceDetail) {
		this.sourceDetail = sourceDetail;
	}

	/**
	 * @return an entry holding this row's location, day and values, for a manual correction
	 */
	public ClimateObservationEntryDto toEntry() {
		ClimateObservationEntryDto entry = new ClimateObservationEntryDto();
		entry.setClimateLocationUuid(climateLocationUuid);
		entry.setDate(observationDate != null ? ClimateObservationValidation.formatDate(observationDate) : null);
		entry.setTemperatureMin(temperatureMin);
		entry.setTemperatureMean(temperatureMean);
		entry.setTemperatureMax(temperatureMax);
		entry.setRainfall(rainfall);
		entry.setRelativeHumidity(relativeHumidity);
		entry.setHumidityMeasure(humidityMeasure);
		entry.setSourceDetail(sourceDetail);
		return entry;
	}
}
