/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.event;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.location.LocationReferenceDto;
import de.symeda.sormas.api.share.ExternalShareStatus;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.HasCaption;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class EventIndexDto extends PseudonymizableIndexDto {

	private static final long serialVersionUID = 8322646404033924938L;

	public static final String I18N_PREFIX = "Event";

	public static final String UUID = "uuid";
	public static final String EXTERNAL_ID = "externalId";
	public static final String EXTERNAL_TOKEN = "externalToken";
	public static final String INTERNAL_TOKEN = "internalToken";
	public static final String EVENT_STATUS = "eventStatus";
	public static final String RISK_LEVEL = "riskLevel";
	public static final String SPECIFIC_RISK = "specificRisk";
	public static final String EVENT_INVESTIGATION_STATUS = "eventInvestigationStatus";
	public static final String EVENT_MANAGEMENT_STATUS = "eventManagementStatus";
	public static final String PARTICIPANT_COUNT = "participantCount";
	public static final String CASE_COUNT = "caseCount";
	public static final String DEATH_COUNT = "deathCount";
	public static final String CONTACT_COUNT = "contactCount";
	public static final String CONTACT_COUNT_SOURCE_IN_EVENT = "contactCountSourceInEvent";
	public static final String DISEASE = "disease";
	public static final String DISEASE_VARIANT = "diseaseVariant";
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String EVOLUTION_DATE = "evolutionDate";
	public static final String EVENT_TITLE = "eventTitle";
	public static final String EVENT_LOCATION = "eventLocation";
	public static final String SRC_TYPE = "srcType";
	public static final String SRC_FIRST_NAME = "srcFirstName";
	public static final String SRC_LAST_NAME = "srcLastName";
	public static final String SRC_TEL_NO = "srcTelNo";
	public static final String REPORT_DATE_TIME = "reportDateTime";
	public static final String REPORTING_USER = "reportingUser";
	public static final String RESPONSIBLE_USER = "responsibleUser";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String ADDRESS = "address";
	public static final String EVENT_GROUPS = "eventGroups";
	public static final String SURVEILLANCE_TOOL_LAST_SHARE_DATE = "surveillanceToolLastShareDate";
	public static final String SURVEILLANCE_TOOL_SHARE_COUNT = "surveillanceToolShareCount";
	public static final String SURVEILLANCE_TOOL_STATUS = "surveillanceToolStatus";
	public static final String EVENT_IDENTIFICATION_SOURCE = "eventIdentificationSource";

	private Long id;
	private String externalId;
	private String externalToken;
	private String internalToken;
	private EventStatus eventStatus;
	private RiskLevel riskLevel;
	private SpecificRisk specificRisk;
	private EventInvestigationStatus eventInvestigationStatus;
	private EventManagementStatus eventManagementStatus;
	private long participantCount;
	private long caseCount;
	private long deathCount;
	/**
	 * number of contacts whose person is involved in this event
	 */
	private long contactCount;
	/**
	 * number of contacts whose person is involved in this event, and where the source case is also part of the event
	 */
	private long contactCountSourceInEvent;
	private Disease disease;
	private DiseaseVariant diseaseVariant;
	private String diseaseDetails;
	private Date startDate;
	private Date endDate;
	private Date evolutionDate;
	private String eventTitle;
	private EventIndexLocation eventLocation;
	private EventSourceType srcType;
	private String srcFirstName;
	private String srcLastName;
	private String srcTelNo;
	private String srcMediaWebsite;
	private String srcMediaName;
	private Date reportDateTime;
	private UserReferenceDto reportingUser;
	private UserReferenceDto responsibleUser;
	private String regionUuid;
	private boolean isInJurisdictionOrOwned;
	private EventGroupsIndexDto eventGroups;
	private EventIdentificationSource eventIdentificationSource;

	private Date surveillanceToolLastShareDate;
	private Long surveillanceToolShareCount;
	private ExternalShareStatus surveillanceToolStatus;

	private DeletionReason deletionReason;
	private String otherDeletionReason;

	public EventIndexDto(
		Long id,
		String uuid,
		String externalId,
		String externalToken,
		String internalToken,
		EventStatus eventStatus,
		RiskLevel riskLevel,
		SpecificRisk specificRisk,
		EventInvestigationStatus eventInvestigationStatus,
		EventManagementStatus eventManagementStatus,
		Disease disease,
		DiseaseVariant diseaseVariant,
		String diseaseDetails,
		Date startDate,
		Date endDate,
		Date evolutionDate,
		String eventTitle,
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
		String reportingUserFirstName,
		String reportingUserLastName,
		String responsibleUserUuid,
		String responsibleUserFirstName,
		String responsibleUserLastName,
		boolean isInJurisdictionOrOwned,
		EventIdentificationSource eventIdentificationSource,
		DeletionReason deletionReason,
		String otherDeletionReason) {

		super(uuid);
		this.id = id;
		this.externalId = externalId;
		this.externalToken = externalToken;
		this.internalToken = internalToken;
		this.eventStatus = eventStatus;
		this.riskLevel = riskLevel;
		this.specificRisk = specificRisk;
		this.eventInvestigationStatus = eventInvestigationStatus;
		this.eventManagementStatus = eventManagementStatus;
		this.disease = disease;
		this.diseaseVariant = diseaseVariant;
		this.diseaseDetails = diseaseDetails;
		this.startDate = startDate;
		this.endDate = endDate;
		this.evolutionDate = evolutionDate;
		this.eventTitle = eventTitle;
		this.eventLocation = new EventIndexLocation(regionName, districtName, communityName, city, street, houseNumber, additionalInformation);
		this.srcType = srcType;
		this.srcFirstName = srcFirstName;
		this.srcLastName = srcLastName;
		this.srcTelNo = srcTelNo;
		this.srcMediaWebsite = srcMediaWebsite;
		this.srcMediaName = srcMediaName;
		this.reportDateTime = reportDateTime;
		this.reportingUser = new UserReferenceDto(reportingUserUuid, reportingUserFirstName, reportingUserLastName);
		this.responsibleUser = new UserReferenceDto(responsibleUserUuid, responsibleUserFirstName, responsibleUserLastName);
		this.isInJurisdictionOrOwned = isInJurisdictionOrOwned;
		this.regionUuid = regionUuid;
		this.eventIdentificationSource = eventIdentificationSource;
		this.deletionReason = deletionReason;
		this.otherDeletionReason = otherDeletionReason;
	}

	public EventIndexDto(String uuid) {
		super(uuid);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getExternalToken() {
		return externalToken;
	}

	public void setExternalToken(String externalToken) {
		this.externalToken = externalToken;
	}

	public String getInternalToken() {
		return internalToken;
	}

	public void setInternalToken(String internalToken) {
		this.internalToken = internalToken;
	}

	public EventStatus getEventStatus() {
		return eventStatus;
	}

	public void setEventStatus(EventStatus eventStatus) {
		this.eventStatus = eventStatus;
	}

	public RiskLevel getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(RiskLevel riskLevel) {
		this.riskLevel = riskLevel;
	}

	public SpecificRisk getSpecificRisk() {
		return specificRisk;
	}

	public void setSpecificRisk(SpecificRisk specificRisk) {
		this.specificRisk = specificRisk;
	}

	public EventInvestigationStatus getEventInvestigationStatus() {
		return eventInvestigationStatus;
	}

	public void setEventInvestigationStatus(EventInvestigationStatus eventInvestigationStatus) {
		this.eventInvestigationStatus = eventInvestigationStatus;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public DiseaseVariant getDiseaseVariant() {
		return diseaseVariant;
	}

	public void setDiseaseVariant(DiseaseVariant diseaseVariant) {
		this.diseaseVariant = diseaseVariant;
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

	public Date getEvolutionDate() {
		return evolutionDate;
	}

	public void setEvolutionDate(Date evolutionDate) {
		this.evolutionDate = evolutionDate;
	}

	public String getEventTitle() {
		return eventTitle;
	}

	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
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

	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
	}

	public UserReferenceDto getResponsibleUser() {
		return responsibleUser;
	}

	public void setResponsibleUser(UserReferenceDto responsibleUser) {
		this.responsibleUser = responsibleUser;
	}

	public long getParticipantCount() {
		return participantCount;
	}

	public void setParticipantCount(long participantCount) {
		this.participantCount = participantCount;
	}

	public long getCaseCount() {
		return caseCount;
	}

	public void setCaseCount(long caseCount) {
		this.caseCount = caseCount;
	}

	public long getDeathCount() {
		return deathCount;
	}

	public void setDeathCount(long deathCount) {
		this.deathCount = deathCount;
	}

	public long getContactCount() {
		return contactCount;
	}

	public void setContactCount(long contactCount) {
		this.contactCount = contactCount;
	}

	public long getContactCountSourceInEvent() {
		return contactCountSourceInEvent;
	}

	public void setContactCountSourceInEvent(long contactCountSourceInEvent) {
		this.contactCountSourceInEvent = contactCountSourceInEvent;
	}

	public String getRegion() {
		return getEventLocation().getRegion();
	}

	public String getDistrict() {
		return getEventLocation().getDistrict();
	}

	public String getCommunity() {
		return getEventLocation().getCommunity();
	}

	public String getAddress() {
		return getEventLocation().getAddress();
	}

	public EventManagementStatus getEventManagementStatus() {
		return eventManagementStatus;
	}

	public void setEventManagementStatus(EventManagementStatus eventManagementStatus) {
		this.eventManagementStatus = eventManagementStatus;
	}

	public EventGroupsIndexDto getEventGroups() {
		return eventGroups;
	}

	public void setEventGroups(EventGroupsIndexDto eventGroups) {
		this.eventGroups = eventGroups;
	}

	public Date getSurveillanceToolLastShareDate() {
		return surveillanceToolLastShareDate;
	}

	public void setSurveillanceToolLastShareDate(Date surveillanceToolLastShareDate) {
		this.surveillanceToolLastShareDate = surveillanceToolLastShareDate;
	}

	public Long getSurveillanceToolShareCount() {
		return surveillanceToolShareCount;
	}

	public void setSurveillanceToolShareCount(Long surveillanceToolShareCount) {
		this.surveillanceToolShareCount = surveillanceToolShareCount;
	}

	public ExternalShareStatus getSurveillanceToolStatus() {
		return surveillanceToolStatus;
	}

	public void setSurveillanceToolStatus(ExternalShareStatus surveillanceToolStatus) {
		this.surveillanceToolStatus = surveillanceToolStatus;
	}

	public EventIdentificationSource getEventIdentificationSource() {
		return eventIdentificationSource;
	}

	public void setEventIdentificationSource(EventIdentificationSource eventIdentificationSource) {
		this.eventIdentificationSource = eventIdentificationSource;
	}

	public DeletionReason getDeletionReason() {
		return deletionReason;
	}

	public void setDeletionReason(DeletionReason deletionReason) {
		this.deletionReason = deletionReason;
	}

	public String getOtherDeletionReason() {
		return otherDeletionReason;
	}

	public void setOtherDeletionReason(String otherDeletionReason) {
		this.otherDeletionReason = otherDeletionReason;
	}

	public EventReferenceDto toReference() {
		return new EventReferenceDto(getUuid(), getDisease(), getDiseaseDetails(), getEventStatus(), getEventInvestigationStatus(), getStartDate());
	}

	public String getRegionUuid() {
		return regionUuid;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		EventIndexDto that = (EventIndexDto) o;

		return getUuid().equals(that.getUuid());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getUuid() == null) ? 0 : getUuid().hashCode());
		return result;
	}

	public boolean getInJurisdictionOrOwned() {
		return isInJurisdictionOrOwned;
	}

	public static class EventIndexLocation implements Serializable, HasCaption {

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

		public String getRegion() {
			return regionName;
		}

		public String getDistrict() {
			return districtName;
		}

		public String getCommunity() {
			return communityName;
		}

		public String getAddress() {
			return LocationReferenceDto.buildCaption(city, street, houseNumber, additionalInformation);
		}

		public String buildCaption() {
			return LocationReferenceDto.buildCaption(regionName, districtName, communityName, city, street, houseNumber, additionalInformation);
		}
	}
}
