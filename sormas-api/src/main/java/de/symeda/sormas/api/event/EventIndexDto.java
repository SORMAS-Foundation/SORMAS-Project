/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.event;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.location.LocationReferenceDto;

public class EventIndexDto implements Serializable {

	private static final long serialVersionUID = 8322646404033924938L;

	public static final String I18N_PREFIX = "Event";

	public static final String UUID = "uuid";
	public static final String EVENT_STATUS = "eventStatus";
	public static final String DISEASE = "disease";
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String EVENT_DESC = "eventDesc";
	public static final String EVENT_LOCATION = "eventLocation";
	public static final String SRC_TYPE = "srcType";
	public static final String SRC_FIRST_NAME = "srcFirstName";
	public static final String SRC_LAST_NAME = "srcLastName";
	public static final String SRC_TEL_NO = "srcTelNo";
	public static final String REPORT_DATE_TIME = "reportDateTime";

	private String uuid;
	private EventStatus eventStatus;
	private Disease disease;
	private String diseaseDetails;
	private Date startDate;
	private Date endDate;
	private String eventDesc;
	private LocationReferenceDto eventLocation;
	private EventSourceType srcType;
	private String srcFirstName;
	private String srcLastName;
	private String srcTelNo;
	private String srcMediaWebsite;
	private String srcMediaName;
	private Date reportDateTime;

	public EventIndexDto(
		String uuid,
		EventStatus eventStatus,
		Disease disease,
		String diseaseDetails,
		Date startDate,
		Date endDate,
		String eventDesc,
		String locationUuid,
		String regionName,
		String districtName,
		String communityName,
		String city,
		String address,
		EventSourceType srcType,
		String srcFirstName,
		String srcLastName,
		String srcTelNo,
		String srcMediaWebsite,
		String srcMediaName,
		Date reportDateTime) {

		this.uuid = uuid;
		this.eventStatus = eventStatus;
		this.disease = disease;
		this.diseaseDetails = diseaseDetails;
		this.startDate = startDate;
		this.endDate = endDate;
		this.eventDesc = eventDesc;
		this.eventLocation = new LocationReferenceDto(locationUuid, regionName, districtName, communityName, city, address);
		this.srcType = srcType;
		this.srcFirstName = srcFirstName;
		this.srcLastName = srcLastName;
		this.srcTelNo = srcTelNo;
		this.srcMediaWebsite = srcMediaWebsite;
		this.srcMediaName = srcMediaName;
		this.reportDateTime = reportDateTime;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public EventStatus getEventStatus() {
		return eventStatus;
	}

	public void setEventStatus(EventStatus eventStatus) {
		this.eventStatus = eventStatus;
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

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getEventDesc() {
		return eventDesc;
	}

	public void setEventDesc(String eventDesc) {
		this.eventDesc = eventDesc;
	}

	public LocationReferenceDto getEventLocation() {
		return eventLocation;
	}

	public void setEventLocation(LocationReferenceDto eventLocation) {
		this.eventLocation = eventLocation;
	}

	public EventSourceType getSrcType() {
		return srcType;
	}

	public void setSrcType(EventSourceType srcType) {
		this.srcType = srcType;
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

	public String getSrcMediaWebsite() {
		return srcMediaWebsite;
	}

	public void setSrcMediaWebsite(String srcMediaWebsite) {
		this.srcMediaWebsite = srcMediaWebsite;
	}

	public String getSrcMediaName() {
		return srcMediaName;
	}

	public void setSrcMediaName(String srcMediaName) {
		this.srcMediaName = srcMediaName;
	}

	public Date getReportDateTime() {
		return reportDateTime;
	}

	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}

	public EventReferenceDto toReference() {
		return new EventReferenceDto(getUuid(), getDisease(), getDiseaseDetails(), getEventStatus(), getStartDate());
	}
}
