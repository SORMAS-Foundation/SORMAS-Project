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
import de.symeda.sormas.api.utils.jurisdiction.WithJurisdiction;

public class EventIndexDto implements WithJurisdiction<EventJurisdictionDto>, Serializable {

	private static final long serialVersionUID = 8322646404033924938L;

	public static final String I18N_PREFIX = "Event";

	public static final String UUID = "uuid";
	public static final String EVENT_STATUS = "eventStatus";
	public static final String PARTICIPANT_COUNT = "participantCount";
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
	private int participantCount;
	private Disease disease;
	private String diseaseDetails;
	private Date startDate;
	private Date endDate;
	private String eventDesc;
	private EventIndexLocation eventLocation;
	private EventSourceType srcType;
	private String srcFirstName;
	private String srcLastName;
	private String srcTelNo;
	private String srcMediaWebsite;
	private String srcMediaName;
	private Date reportDateTime;
	private EventJurisdictionDto jurisdiction;

	public EventIndexDto(
		String uuid,
		EventStatus eventStatus,
		Integer participantCount,
		Disease disease,
		String diseaseDetails,
		Date startDate,
		Date endDate,
		String eventDesc,
		String regionUuid,
		String regionName,
		String districtUuid,
		String districtName,
		String communityUuid,
		String communityName,
		String city,
		String street,
		String houseNumber,
		String additionalInformation,
		EventSourceType srcType,
		String srcFirstName,
		String srcLastName,
		String srcTelNo,
		String srcMediaWebsite,
		String srcMediaName,
		Date reportDateTime,
		String reportingUserUuid,
		String surveillanceOfficerUuid) {

		this.uuid = uuid;
		this.eventStatus = eventStatus;
		this.disease = disease;
		this.diseaseDetails = diseaseDetails;
		this.startDate = startDate;
		this.endDate = endDate;
		this.eventDesc = eventDesc;
		this.eventLocation = new EventIndexLocation(regionName, districtName, communityName, city, street, houseNumber, additionalInformation);
		this.srcType = srcType;
		this.srcFirstName = srcFirstName;
		this.srcLastName = srcLastName;
		this.srcTelNo = srcTelNo;
		this.srcMediaWebsite = srcMediaWebsite;
		this.srcMediaName = srcMediaName;
		this.reportDateTime = reportDateTime;
		this.jurisdiction = new EventJurisdictionDto(reportingUserUuid, surveillanceOfficerUuid, regionUuid, districtUuid, communityUuid);
		this.participantCount = participantCount;
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

	public EventIndexLocation getEventLocation() {
		return eventLocation;
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

	public int getParticipantCount() {
		return participantCount;
	}

	public void setParticipantCount(int participantCount) {
		this.participantCount = participantCount;
	}

	public EventReferenceDto toReference() {
		return new EventReferenceDto(getUuid(), getDisease(), getDiseaseDetails(), getEventStatus(), getStartDate());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		EventIndexDto that = (EventIndexDto) o;

		return uuid.equals(that.uuid);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	public EventJurisdictionDto getJurisdiction() {
		return jurisdiction;
	}

	public static class EventIndexLocation implements Serializable {

		private String regionName;
		private String districtName;
		private String communityName;
		private String city;
		private String street;
		private String houseNumber;
		private String additionalInformation;

		public EventIndexLocation(
			String regionName,
			String districtName,
			String communityName,
			String city,
			String street,
			String houseNumber,
			String additionalInformation) {
			this.regionName = regionName;
			this.districtName = districtName;
			this.communityName = communityName;
			this.city = city;
			this.street = street;
			this.houseNumber = houseNumber;
			this.additionalInformation = additionalInformation;
		}

		@Override
		public String toString() {
			return LocationReferenceDto.buildCaption(regionName, districtName, communityName, city, street, houseNumber, additionalInformation);
		}
	}
}
