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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.climate.SiteClimateReadingValidation;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.environment.Environment;
import de.symeda.sormas.backend.user.User;

/**
 * A spot measurement taken at an environment (site reading). It belongs to the environment, not to a climate location,
 * and never changes the daily record. Earlier values are kept in the history table, also after a delete.
 */
@Entity(name = SiteClimateReading.TABLE_NAME)
public class SiteClimateReading extends AbstractDomainObject {

	private static final long serialVersionUID = 6250934781652203517L;

	public static final String TABLE_NAME = "siteclimatereading";

	public static final String ENVIRONMENT = "environment";
	public static final String READING_DATE_TIME = "readingDateTime";
	public static final String REPORTING_USER = "reportingUser";

	private Environment environment;
	private Date readingDateTime;
	private Double airTemperature;
	private Double rainfall;
	private Integer rainfallPeriodHours;
	private Double relativeHumidity;
	private String notes;
	private User reportingUser;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	public Date getReadingDateTime() {
		return readingDateTime;
	}

	public void setReadingDateTime(Date readingDateTime) {
		this.readingDateTime = readingDateTime;
	}

	public Double getAirTemperature() {
		return airTemperature;
	}

	public void setAirTemperature(Double airTemperature) {
		this.airTemperature = airTemperature;
	}

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

	@Column(length = SiteClimateReadingValidation.NOTES_MAX_LENGTH)
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	public User getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(User reportingUser) {
		this.reportingUser = reportingUser;
	}
}
