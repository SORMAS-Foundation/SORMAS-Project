/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.event;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_BIG;
import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.user.User;

@Entity(name = Event.TABLE_NAME)
@DatabaseTable(tableName = Event.TABLE_NAME)
public class Event extends AbstractDomainObject {

	private static final long serialVersionUID = 4964495716032049582L;

	public static final String TABLE_NAME = "events";
	public static final String I18N_PREFIX = "Event";

	public static final String EVENT_STATUS = "eventStatus";
	public static final String EVENT_PERSONS = "eventPersons";
	public static final String EVENT_DESC = "eventDesc";
	public static final String EVENT_DATE = "eventDate";
	public static final String REPORT_DATE_TIME = "reportDateTime";
	public static final String REPORTING_USER = "reportingUser";
	public static final String EVENT_LOCATION = "eventLocation";
	public static final String TYPE_OF_PLACE = "typeOfPlace";
	public static final String SRC_FIRST_NAME = "srcFirstName";
	public static final String SRC_LAST_NAME = "srcLastName";
	public static final String SRC_TEL_NO = "srcTelNo";
	public static final String SRC_EMAIL = "srcEmail";
	public static final String DISEASE = "disease";
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String SURVEILLANCE_OFFICER = "surveillanceOfficer";
	public static final String TYPE_OF_PLACE_TEXT = "typeOfPlaceText";

	@Deprecated
	@DatabaseField
	private String eventType;

	@Enumerated(EnumType.STRING)
	private EventStatus eventStatus;

	@Column(length = COLUMN_LENGTH_BIG)
	private String eventDesc;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date eventDate;

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date reportDateTime;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User reportingUser;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 2)
	private Location eventLocation;

	@Enumerated(EnumType.STRING)
	private TypeOfPlace typeOfPlace;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String srcFirstName;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String srcLastName;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String srcTelNo;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String srcEmail;

	@Enumerated(EnumType.STRING)
	private Disease disease;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String diseaseDetails;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User surveillanceOfficer;

	@Column(length = COLUMN_LENGTH_DEFAULT)
	private String typeOfPlaceText;

	@DatabaseField
	private Double reportLat;
	@DatabaseField
	private Double reportLon;
	@DatabaseField
	private Float reportLatLonAccuracy;

	public EventStatus getEventStatus() {
		return eventStatus;
	}

	public void setEventStatus(EventStatus eventStatus) {
		this.eventStatus = eventStatus;
	}

	public String getEventDesc() {
		return eventDesc;
	}

	public void setEventDesc(String eventDesc) {
		this.eventDesc = eventDesc;
	}

	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	public Date getReportDateTime() {
		return reportDateTime;
	}

	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}

	public User getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(User reportingUser) {
		this.reportingUser = reportingUser;
	}

	public Location getEventLocation() {
		return eventLocation;
	}

	public void setEventLocation(Location eventLocation) {
		this.eventLocation = eventLocation;
	}

	public TypeOfPlace getTypeOfPlace() {
		return typeOfPlace;
	}

	public void setTypeOfPlace(TypeOfPlace typeOfPlace) {
		this.typeOfPlace = typeOfPlace;
	}

	public String getSrcFirstName() {
		return srcFirstName;
	}

	public void setSrcFirstName(String srcFirstName) {
		this.srcFirstName = srcFirstName;
	}

	public String getSrcLastName() {
		return srcLastName;
	}

	public void setSrcLastName(String srcLastName) {
		this.srcLastName = srcLastName;
	}

	public String getSrcTelNo() {
		return srcTelNo;
	}

	public void setSrcTelNo(String srcTelNo) {
		this.srcTelNo = srcTelNo;
	}

	public String getSrcEmail() {
		return srcEmail;
	}

	public void setSrcEmail(String srcEmail) {
		this.srcEmail = srcEmail;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public String getDiseaseDetails() {
		return diseaseDetails;
	}

	public void setDiseaseDetails(String diseaseDetails) {
		this.diseaseDetails = diseaseDetails;
	}

	public User getSurveillanceOfficer() {
		return surveillanceOfficer;
	}

	public void setSurveillanceOfficer(User surveillanceOfficer) {
		this.surveillanceOfficer = surveillanceOfficer;
	}

	public String getTypeOfPlaceText() {
		return typeOfPlaceText;
	}

	public void setTypeOfPlaceText(String typeOfPlaceText) {
		this.typeOfPlaceText = typeOfPlaceText;
	}

	public Double getReportLat() {
		return reportLat;
	}

	public void setReportLat(Double reportLat) {
		this.reportLat = reportLat;
	}

	public Double getReportLon() {
		return reportLon;
	}

	public void setReportLon(Double reportLon) {
		this.reportLon = reportLon;
	}

	@Override
	public String toString() {
		return EventReferenceDto.buildCaption(getDisease(), getDiseaseDetails(), getEventStatus(), getEventDate());
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}

	public Float getReportLatLonAccuracy() {
		return reportLatLonAccuracy;
	}

	public void setReportLatLonAccuracy(Float reportLatLonAccuracy) {
		this.reportLatLonAccuracy = reportLatLonAccuracy;
	}
}
