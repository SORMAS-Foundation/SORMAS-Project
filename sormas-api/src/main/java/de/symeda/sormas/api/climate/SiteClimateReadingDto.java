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

import java.util.Date;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

/**
 * A spot measurement taken at an environment, for example at sampling time. It is kept beside the daily climate record
 * and never changes it.
 */
@AuditedClass
public class SiteClimateReadingDto extends EntityDto {

	private static final long serialVersionUID = 2817795041726377305L;

	public static final String I18N_PREFIX = "SiteClimateReading";

	public static final String ENVIRONMENT = "environment";
	public static final String READING_DATE_TIME = "readingDateTime";
	public static final String AIR_TEMPERATURE = "airTemperature";
	public static final String RAINFALL = "rainfall";
	public static final String RAINFALL_PERIOD_HOURS = "rainfallPeriodHours";
	public static final String RELATIVE_HUMIDITY = "relativeHumidity";
	public static final String NOTES = "notes";
	public static final String REPORTING_USER = "reportingUser";

	private EnvironmentReferenceDto environment;
	private Date readingDateTime;
	private Double airTemperature;
	private Double rainfall;
	private Integer rainfallPeriodHours;
	private Double relativeHumidity;
	private String notes;
	private UserReferenceDto reportingUser;

	public static SiteClimateReadingDto build(EnvironmentReferenceDto environment) {
		SiteClimateReadingDto reading = new SiteClimateReadingDto();
		reading.setUuid(DataHelper.createUuid());
		reading.setEnvironment(environment);
		return reading;
	}

	public EnvironmentReferenceDto getEnvironment() {
		return environment;
	}

	public void setEnvironment(EnvironmentReferenceDto environment) {
		this.environment = environment;
	}

	public Date getReadingDateTime() {
		return readingDateTime;
	}

	public void setReadingDateTime(Date readingDateTime) {
		this.readingDateTime = readingDateTime;
	}

	/**
	 * @return the ambient air temperature in °C, not the sample's temperature
	 */
	public Double getAirTemperature() {
		return airTemperature;
	}

	public void setAirTemperature(Double airTemperature) {
		this.airTemperature = airTemperature;
	}

	/**
	 * @return the rain gauge reading in mm, over {@link #getRainfallPeriodHours()}
	 */
	public Double getRainfall() {
		return rainfall;
	}

	public void setRainfall(Double rainfall) {
		this.rainfall = rainfall;
	}

	public Integer getRainfallPeriodHours() {
		return rainfallPeriodHours;
	}

	public void setRainfallPeriodHours(Integer rainfallPeriodHours) {
		this.rainfallPeriodHours = rainfallPeriodHours;
	}

	public Double getRelativeHumidity() {
		return relativeHumidity;
	}

	public void setRelativeHumidity(Double relativeHumidity) {
		this.relativeHumidity = relativeHumidity;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * @return the user who recorded the reading; set by the server on save
	 */
	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
	}
}
