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

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.api.climate.HumidityMeasure;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.user.User;

/**
 * One day's weather at one climate location: one row per location and day. {@link #getSource()} says where the current
 * values came from; the order MANUAL, IMPORT, OPENWEATHER is applied when data is saved, and earlier values are kept in
 * the history table. A missing day has no row; it is never stored as zero.
 */
@Entity(name = ClimateObservation.TABLE_NAME)
public class ClimateObservation extends AbstractDomainObject {

	private static final long serialVersionUID = -4419531623402869254L;

	public static final String TABLE_NAME = "climateobservation";

	public static final String CLIMATE_LOCATION = "climateLocation";
	public static final String OBSERVATION_DATE = "observationDate";
	public static final String TEMPERATURE_MIN = "temperatureMin";
	public static final String TEMPERATURE_MAX = "temperatureMax";
	public static final String TEMPERATURE_MEAN = "temperatureMean";
	public static final String RAINFALL = "rainfall";
	public static final String RELATIVE_HUMIDITY = "relativeHumidity";
	public static final String HUMIDITY_MEASURE = "humidityMeasure";
	public static final String SOURCE = "source";
	public static final String SOURCE_DETAIL = "sourceDetail";
	public static final String REPORTING_USER = "reportingUser";

	private ClimateLocation climateLocation;
	private Date observationDate;
	private Double temperatureMin;
	private Double temperatureMax;
	private Double temperatureMean;
	private Double rainfall;
	private Double relativeHumidity;
	private HumidityMeasure humidityMeasure;
	private ClimateDataSource source;
	private String sourceDetail;
	private User reportingUser;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	public ClimateLocation getClimateLocation() {
		return climateLocation;
	}

	public void setClimateLocation(ClimateLocation climateLocation) {
		this.climateLocation = climateLocation;
	}

	@Temporal(TemporalType.DATE)
	@Column(nullable = false)
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

	@Enumerated(EnumType.STRING)
	public HumidityMeasure getHumidityMeasure() {
		return humidityMeasure;
	}

	public void setHumidityMeasure(HumidityMeasure humidityMeasure) {
		this.humidityMeasure = humidityMeasure;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public ClimateDataSource getSource() {
		return source;
	}

	public void setSource(ClimateDataSource source) {
		this.source = source;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getSourceDetail() {
		return sourceDetail;
	}

	public void setSourceDetail(String sourceDetail) {
		this.sourceDetail = sourceDetail;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public User getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(User reportingUser) {
		this.reportingUser = reportingUser;
	}

	/**
	 * Copies the measured values, source and reporting details of another row onto this one (not the location or date).
	 */
	public void copyValuesFrom(ClimateObservation other) {
		temperatureMin = other.temperatureMin;
		temperatureMax = other.temperatureMax;
		temperatureMean = other.temperatureMean;
		rainfall = other.rainfall;
		relativeHumidity = other.relativeHumidity;
		humidityMeasure = other.humidityMeasure;
		source = other.source;
		sourceDetail = other.sourceDetail;
		reportingUser = other.reportingUser;
	}
}
