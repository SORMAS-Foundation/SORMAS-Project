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
import de.symeda.sormas.api.utils.SensitiveData;

public class EventIndexDto implements Serializable {

	private static final long serialVersionUID = 8322646404033924938L;

	public static final String I18N_PREFIX = "Event";

	public static final String UUID = "uuid";
	public static final String EVENT_STATUS = "eventStatus";
	public static final String DISEASE = "disease";
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String EVENT_DATE = "eventDate";
	public static final String EVENT_DESC = "eventDesc";
	public static final String EVENT_LOCATION = "eventLocationString";
	public static final String SRC_FIRST_NAME = "srcFirstName";
	public static final String SRC_LAST_NAME = "srcLastName";
	public static final String SRC_TEL_NO = "srcTelNo";
	public static final String REPORT_DATE_TIME = "reportDateTime";

	private String uuid;
	private EventStatus eventStatus;
	private Disease disease;
	private String diseaseDetails;
	private Date eventDate;
	@SensitiveData
	private String eventDesc;
	private EventIndexLocation eventIndexLocation;
	@SensitiveData
	private String srcFirstName;
	@SensitiveData
	private String srcLastName;
	@SensitiveData
	private String srcTelNo;
	private Date reportDateTime;
	private EventJurisdictionDto jurisdiction;

	public EventIndexDto(
		String uuid,
		EventStatus eventStatus,
		Disease disease,
		String diseaseDetails,
		Date eventDate,
		String eventDesc,
		String regionUuid,
		String regionName,
		String districtUuid,
		String districtName,
		String communityUuid,
		String communityName,
		String city,
		String address,
		String srcFirstName,
		String srcLastName,
		String srcTelNo,
		Date reportDateTime,
		String reportingUserUuid,
		String surveillanceOfficerUuid) {

		this.uuid = uuid;
		this.eventStatus = eventStatus;
		this.disease = disease;
		this.diseaseDetails = diseaseDetails;
		this.eventDate = eventDate;
		this.eventDesc = eventDesc;
		this.eventIndexLocation = new EventIndexLocation(regionName, districtName, communityName, city, address);
		this.srcFirstName = srcFirstName;
		this.srcLastName = srcLastName;
		this.srcTelNo = srcTelNo;
		this.reportDateTime = reportDateTime;
		this.jurisdiction = new EventJurisdictionDto(reportingUserUuid, surveillanceOfficerUuid, regionUuid, districtUuid, communityUuid);
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

	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	public String getEventDesc() {
		return eventDesc;
	}

	public void setEventDesc(String eventDesc) {
		this.eventDesc = eventDesc;
	}

	public EventIndexLocation getEventIndexLocation() {
		return eventIndexLocation;
	}

	public String getEventLocationString() {
		return eventIndexLocation.formatString();
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

	public Date getReportDateTime() {
		return reportDateTime;
	}

	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}

	public EventReferenceDto toReference() {
		return new EventReferenceDto(getUuid(), getDisease(), getDiseaseDetails(), getEventStatus(), getEventDate());
	}

	public EventJurisdictionDto getJurisdiction() {
		return jurisdiction;
	}

	public static class EventIndexLocation implements Serializable {

		private String regionName;
		private String districtName;
		@SensitiveData
		private String communityName;
		@SensitiveData
		private String city;
		@SensitiveData
		private String address;

		public EventIndexLocation(String regionName, String districtName, String communityName, String city, String address) {
			this.regionName = regionName;
			this.districtName = districtName;
			this.communityName = communityName;
			this.city = city;
			this.address = address;
		}

		public String formatString() {
			return LocationReferenceDto.buildCaption(regionName, districtName, communityName, city, address);
		}
	}
}
