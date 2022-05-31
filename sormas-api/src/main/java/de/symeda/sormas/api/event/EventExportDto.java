/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.importexport.ExportEntity;
import de.symeda.sormas.api.importexport.ExportGroup;
import de.symeda.sormas.api.importexport.ExportGroupType;
import de.symeda.sormas.api.importexport.ExportProperty;
import de.symeda.sormas.api.importexport.format.ExportFormat;
import de.symeda.sormas.api.importexport.format.ImportExportFormat;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.utils.YesNoUnknown;

@ExportEntity(EventDto.class)
public class EventExportDto implements Serializable {

	public static final String I18N_PREFIX = "EventExport";

	public static final String LATEST_EVENT_GROUP = "latestEventGroup";
	public static final String EVENT_GROUP_COUNT = "eventGroupCount";
	public static final String PARTICIPANT_COUNT = "participantCount";
	public static final String CASE_COUNT = "caseCount";
	public static final String DEATH_COUNT = "deathCount";
	public static final String CONTACT_COUNT = "contactCount";
	public static final String CONTACT_COUNT_SOURCE_IN_EVENT = "contactCountSourceInEvent";

	private String uuid;
	private String externalId;
	private String externalToken;
	private String internalToken;
	private EventStatus eventStatus;
	private RiskLevel riskLevel;
	private SpecificRisk specificRisk;
	private EventInvestigationStatus eventInvestigationStatus;
	private Date eventInvestigationStartDate;
	private Date eventInvestigationEndDate;
	private long participantCount;
	private long caseCount;
	private long deathCount;
	private long contactCount;
	private long contactCountSourceInEvent;
	private Disease disease;
	private String diseaseDetails;
	private Date startDate;
	private Date endDate;
	private Date evolutionDate;
	private String evolutionComment;
	private String eventTitle;
	private String eventDesc;
	private EventGroupReferenceDto latestEventGroup;
	private Long eventGroupCount;
	private DiseaseTransmissionMode diseaseTransmissionMode;
	private YesNoUnknown nosocomial;
	private YesNoUnknown transregionalOutbreak;
	private final String meansOfTransport;
	private String region;
	private String district;
	private String community;
	private String city;
	private String street;
	private String houseNumber;
	private String additionalInformation;
	private EventSourceType srcType;
	private String srcInstitutionalPartnerType;
	private String srcFirstName;
	private String srcLastName;
	private String srcTelNo;
	private String srcEmail;
	private String srcMediaWebsite;
	private String srcMediaName;
	private String srcMediaDetails;
	private Date reportDateTime;
	private UserReferenceDto reportingUser;
	private UserReferenceDto responsibleUser;
	private EventManagementStatus eventManagementStatus;
	private DiseaseVariant diseaseVariant;
	private String diseaseVariantDetails;
	private EventIdentificationSource eventIdentificationSource;

	private boolean isInJurisdictionOrOwned;

	public EventExportDto(
		String uuid,
		String externalId,
		String externalToken,
		String internalToken,
		EventStatus eventStatus,
		RiskLevel riskLevel,
		SpecificRisk specificRisk,
		EventInvestigationStatus eventInvestigationStatus,
		Date eventInvestigationStartDate,
		Date eventInvestigationEndDate,
		Disease disease,
		DiseaseVariant diseaseVariant,
		String diseaseDetails,
		String diseaseVariantDetails,
		Date startDate,
		Date endDate,
		Date evolutionDate,
		String evolutionComment,
		String eventTitle,
		String eventDesc,
		DiseaseTransmissionMode diseaseTransmissionMode,
		YesNoUnknown nosocomial,
		YesNoUnknown transregionalOutbreak,
		MeansOfTransport meansOfTransport,
		String meansOfTransportDetails,
		String regionUuid,
		String region,
		String districtUuid,
		String district,
		String communityUuid,
		String community,
		String city,
		String street,
		String houseNumber,
		String additionalInformation,
		EventSourceType srcType,
		InstitutionalPartnerType srcInstitutionalPartnerType,
		String srcInstitutionalPartnerTypeDetails,
		String srcFirstName,
		String srcLastName,
		String srcTelNo,
		String srcEmail,
		String srcMediaWebsite,
		String srcMediaName,
		String srcMediaDetails,
		Date reportDateTime,
		String reportingUserUuid,
		String reportingUserFirstName,
		String reportingUserLastName,
		String responsibleUserUuid,
		String responsibleUserFirstName,
		String responsibleUserLastName,
		boolean isInJurisdictionOrOwned,
		EventManagementStatus eventManagementStatus,
		EventIdentificationSource eventIdentificationSource) {
		this.uuid = uuid;
		this.externalId = externalId;
		this.externalToken = externalToken;
		this.internalToken = internalToken;
		this.eventStatus = eventStatus;
		this.riskLevel = riskLevel;
		this.specificRisk = specificRisk;
		this.eventInvestigationStatus = eventInvestigationStatus;
		this.eventInvestigationStartDate = eventInvestigationStartDate;
		this.eventInvestigationEndDate = eventInvestigationEndDate;
		this.disease = disease;
		this.diseaseVariant = diseaseVariant;
		this.diseaseDetails = diseaseDetails;
		this.diseaseVariantDetails = diseaseVariantDetails;
		this.startDate = startDate;
		this.endDate = endDate;
		this.evolutionDate = evolutionDate;
		this.evolutionComment = evolutionComment;
		this.eventTitle = eventTitle;
		this.eventDesc = eventDesc;
		this.diseaseTransmissionMode = diseaseTransmissionMode;
		this.nosocomial = nosocomial;
		this.transregionalOutbreak = transregionalOutbreak;
		this.meansOfTransport = EventHelper.buildMeansOfTransportString(meansOfTransport, meansOfTransportDetails);
		this.region = region;
		this.district = district;
		this.community = community;
		this.city = city;
		this.street = street;
		this.houseNumber = houseNumber;
		this.additionalInformation = additionalInformation;
		this.srcType = srcType;
		this.srcInstitutionalPartnerType =
			EventHelper.buildInstitutionalPartnerTypeString(srcInstitutionalPartnerType, srcInstitutionalPartnerTypeDetails);
		this.srcFirstName = srcFirstName;
		this.srcLastName = srcLastName;
		this.srcTelNo = srcTelNo;
		this.srcMediaWebsite = srcMediaWebsite;
		this.srcMediaName = srcMediaName;
		this.srcMediaDetails = srcMediaDetails;
		this.reportDateTime = reportDateTime;
		this.reportingUser = new UserReferenceDto(reportingUserUuid, reportingUserFirstName, reportingUserLastName);
		this.responsibleUser = new UserReferenceDto(responsibleUserUuid, responsibleUserFirstName, responsibleUserLastName);
		this.isInJurisdictionOrOwned = isInJurisdictionOrOwned;
		this.eventManagementStatus = eventManagementStatus;
		this.eventIdentificationSource = eventIdentificationSource;
	}

	@Order(0)
	@ExportProperty(EventDto.UUID)
	@ExportGroup(ExportGroupType.CORE)
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Order(1)
	@ExportProperty(EventDto.EXTERNAL_ID)
	@ExportGroup(ExportGroupType.CORE)
	public String getExternalId() {
		return externalId;
	}

	@Order(2)
	@ExportProperty(EventDto.EVENT_STATUS)
	@ExportGroup(ExportGroupType.CORE)
	public EventStatus getEventStatus() {
		return eventStatus;
	}

	public void setEventStatus(EventStatus eventStatus) {
		this.eventStatus = eventStatus;
	}

	@Order(3)
	@ExportProperty(EventDto.EVENT_MANAGEMENT_STATUS)
	@ExportGroup(ExportGroupType.CORE)
	public EventManagementStatus getEventManagementStatus() {
		return eventManagementStatus;
	}

	public void setEventManagementStatus(EventManagementStatus eventManagementStatus) {
		this.eventManagementStatus = eventManagementStatus;
	}

	@Order(4)
	@ExportProperty(EventDto.EVENT_IDENTIFICATION_SOURCE)
	@ExportGroup(ExportGroupType.CORE)
	public EventIdentificationSource getEventIdentificationSource() {
		return eventIdentificationSource;
	}

	public void setEventIdentificationSource(EventIdentificationSource eventIdentificationSource) {
		this.eventIdentificationSource = eventIdentificationSource;
	}

	@Order(5)
	@ExportProperty(EventDto.RISK_LEVEL)
	@ExportGroup(ExportGroupType.CORE)
	public RiskLevel getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(RiskLevel riskLevel) {
		this.riskLevel = riskLevel;
	}

	@Order(6)
	@ExportProperty(EventDto.SPECIFIC_RISK)
	@ExportGroup(ExportGroupType.CORE)
	public SpecificRisk getSpecificRisk() {
		return specificRisk;
	}

	public void setSpecificRisk(SpecificRisk specificRisk) {
		this.specificRisk = specificRisk;
	}

	@Order(7)
	@ExportProperty(EventDto.EVENT_INVESTIGATION_STATUS)
	@ExportGroup(ExportGroupType.CORE)
	public EventInvestigationStatus getEventInvestigationStatus() {
		return eventInvestigationStatus;
	}

	public void setEventInvestigationStatus(EventInvestigationStatus eventInvestigationStatus) {
		this.eventInvestigationStatus = eventInvestigationStatus;
	}

	@Order(8)
	@ExportProperty(EventDto.EVENT_INVESTIGATION_START_DATE)
	@ExportGroup(ExportGroupType.CORE)
	public Date getEventInvestigationStartDate() {
		return eventInvestigationStartDate;
	}

	public void setEventInvestigationStartDate(Date eventInvestigationStartDate) {
		this.eventInvestigationStartDate = eventInvestigationStartDate;
	}

	@Order(9)
	@ExportProperty(EventDto.EVENT_INVESTIGATION_END_DATE)
	@ExportGroup(ExportGroupType.CORE)
	public Date getEventInvestigationEndDate() {
		return eventInvestigationEndDate;
	}

	public void setEventInvestigationEndDate(Date eventInvestigationEndDate) {
		this.eventInvestigationEndDate = eventInvestigationEndDate;
	}

	@Order(10)
	@ExportProperty(EventDto.DISEASE)
	@ExportGroup(ExportGroupType.CORE)
	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	@Order(11)
	@ExportProperty(EventDto.DISEASE_DETAILS)
	@ExportGroup(ExportGroupType.CORE)
	public String getDiseaseDetails() {
		return diseaseDetails;
	}

	public void setDiseaseDetails(String diseaseDetails) {
		this.diseaseDetails = diseaseDetails;
	}

	@Order(12)
	@ExportProperty(EventDto.DISEASE_VARIANT)
	@ExportGroup(ExportGroupType.CORE)
	public DiseaseVariant getDiseaseVariant() {
		return diseaseVariant;
	}

	public void setDiseaseVariant(DiseaseVariant diseaseVariant) {
		this.diseaseVariant = diseaseVariant;
	}

	@Order(13)
	@ExportProperty(EventDto.DISEASE_VARIANT_DETAILS)
	@ExportGroup(ExportGroupType.CORE)
	public String getDiseaseVariantDetails() {
		return diseaseVariantDetails;
	}

	public void setDiseaseVariantDetails(String diseaseVariantDetails) {
		this.diseaseVariantDetails = diseaseVariantDetails;
	}

	@Order(14)
	@ExportProperty(EventDto.START_DATE)
	@ExportGroup(ExportGroupType.CORE)
	@ExportFormat(ImportExportFormat.DATE_TIME)
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Order(15)
	@ExportProperty(EventDto.END_DATE)
	@ExportGroup(ExportGroupType.CORE)
	@ExportFormat(ImportExportFormat.DATE_TIME)
	public Date getEndDate() {
		return endDate;
	}

	@Order(16)
	@ExportProperty(EventDto.EVOLUTION_DATE)
	@ExportGroup(ExportGroupType.CORE)
	public Date getEvolutionDate() {
		return evolutionDate;
	}

	public void setEvolutionDate(Date evolutionDate) {
		this.evolutionDate = evolutionDate;
	}

	@Order(17)
	@ExportProperty(EventDto.EVOLUTION_COMMENT)
	@ExportGroup(ExportGroupType.CORE)
	public String getEvolutionComment() {
		return evolutionComment;
	}

	public void setEvolutionComment(String evolutionComment) {
		this.evolutionComment = evolutionComment;
	}

	@Order(18)
	@ExportProperty(EventDto.EVENT_TITLE)
	@ExportGroup(ExportGroupType.CORE)
	public String getEventTitle() {
		return eventTitle;
	}

	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}

	@Order(19)
	@ExportProperty(EventDto.EVENT_DESC)
	@ExportGroup(ExportGroupType.CORE)
	public String getEventDesc() {
		return eventDesc;
	}

	public void setEventDesc(String eventDesc) {
		this.eventDesc = eventDesc;
	}

	@Order(20)
	@ExportProperty(LATEST_EVENT_GROUP)
	@ExportGroup(ExportGroupType.EVENT_GROUP)
	public EventGroupReferenceDto getLatestEventGroup() {
		return latestEventGroup;
	}

	public void setLatestEventGroup(EventGroupReferenceDto latestEventGroup) {
		this.latestEventGroup = latestEventGroup;
	}

	@Order(21)
	@ExportProperty(EVENT_GROUP_COUNT)
	@ExportGroup(ExportGroupType.EVENT_GROUP)
	public Long getEventGroupCount() {
		return eventGroupCount;
	}

	public void setEventGroupCount(Long eventGroupCount) {
		this.eventGroupCount = eventGroupCount;
	}

	@Order(22)
	@ExportProperty(EventDto.DISEASE_TRANSMISSION_MODE)
	@ExportGroup(ExportGroupType.CORE)
	public DiseaseTransmissionMode getDiseaseTransmissionMode() {
		return diseaseTransmissionMode;
	}

	@Order(23)
	@ExportProperty(EventDto.NOSOCOMIAL)
	@ExportGroup(ExportGroupType.CORE)
	public YesNoUnknown getNosocomial() {
		return nosocomial;
	}

	@Order(24)
	@ExportProperty(EventDto.TRANSREGIONAL_OUTBREAK)
	@ExportGroup(ExportGroupType.CORE)
	public YesNoUnknown getTransregionalOutbreak() {
		return transregionalOutbreak;
	}

	@Order(25)
	@ExportProperty(EventDto.MEANS_OF_TRANSPORT)
	@ExportGroup(ExportGroupType.CORE)
	public String getMeansOfTransport() {
		return meansOfTransport;
	}

	@Order(26)
	@ExportEntity(LocationDto.class)
	@ExportProperty({
		EventDto.EVENT_LOCATION,
		LocationDto.REGION })
	@ExportGroup(ExportGroupType.LOCATION)
	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	@Order(27)
	@ExportEntity(LocationDto.class)
	@ExportProperty({
		EventDto.EVENT_LOCATION,
		LocationDto.DISTRICT })
	@ExportGroup(ExportGroupType.LOCATION)
	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	@Order(28)
	@ExportEntity(LocationDto.class)
	@ExportProperty({
		EventDto.EVENT_LOCATION,
		LocationDto.COMMUNITY })
	@ExportGroup(ExportGroupType.LOCATION)
	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	@Order(29)
	@ExportEntity(LocationDto.class)
	@ExportProperty({
		EventDto.EVENT_LOCATION,
		LocationDto.CITY })
	@ExportGroup(ExportGroupType.LOCATION)
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Order(30)
	@ExportEntity(LocationDto.class)
	@ExportProperty({
		EventDto.EVENT_LOCATION,
		LocationDto.STREET })
	@ExportGroup(ExportGroupType.LOCATION)
	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	@Order(31)
	@ExportEntity(LocationDto.class)
	@ExportProperty({
		EventDto.EVENT_LOCATION,
		LocationDto.HOUSE_NUMBER })
	@ExportGroup(ExportGroupType.LOCATION)
	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	@Order(32)
	@ExportEntity(LocationDto.class)
	@ExportProperty({
		EventDto.EVENT_LOCATION,
		LocationDto.ADDITIONAL_INFORMATION })
	@ExportGroup(ExportGroupType.LOCATION)
	public String getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	@Order(33)
	@ExportProperty(EventDto.SRC_TYPE)
	@ExportGroup(ExportGroupType.EVENT_SOURCE)
	public EventSourceType getSrcType() {
		return srcType;
	}

	@Order(34)
	@ExportProperty(EventDto.SRC_INSTITUTIONAL_PARTNER_TYPE)
	@ExportGroup(ExportGroupType.EVENT_SOURCE)
	public String getSrcInstitutionalPartnerType() {
		return srcInstitutionalPartnerType;
	}

	@Order(35)
	@ExportProperty(EventDto.SRC_FIRST_NAME)
	@ExportGroup(ExportGroupType.EVENT_SOURCE)
	public String getSrcFirstName() {
		return srcFirstName;
	}

	public void setSrcFirstName(String srcFirstName) {
		this.srcFirstName = srcFirstName;
	}

	@Order(36)
	@ExportProperty(EventDto.SRC_LAST_NAME)
	@ExportGroup(ExportGroupType.EVENT_SOURCE)
	public String getSrcLastName() {
		return srcLastName;
	}

	public void setSrcLastName(String srcLastName) {
		this.srcLastName = srcLastName;
	}

	@Order(37)
	@ExportProperty(EventDto.SRC_TEL_NO)
	@ExportGroup(ExportGroupType.EVENT_SOURCE)
	public String getSrcTelNo() {
		return srcTelNo;
	}

	public void setSrcTelNo(String srcTelNo) {
		this.srcTelNo = srcTelNo;
	}

	@Order(38)
	@ExportProperty(EventDto.SRC_EMAIL)
	@ExportGroup(ExportGroupType.EVENT_SOURCE)
	public String getSrcEmail() {
		return srcEmail;
	}

	@Order(39)
	@ExportProperty(EventDto.SRC_MEDIA_WEBSITE)
	@ExportGroup(ExportGroupType.EVENT_SOURCE)
	public String getSrcMediaWebsite() {
		return srcMediaWebsite;
	}

	@Order(40)
	@ExportProperty(EventDto.SRC_MEDIA_NAME)
	@ExportGroup(ExportGroupType.EVENT_SOURCE)
	public String getSrcMediaName() {
		return srcMediaName;
	}

	@Order(41)
	@ExportProperty(EventDto.SRC_MEDIA_DETAILS)
	@ExportGroup(ExportGroupType.EVENT_SOURCE)
	public String getSrcMediaDetails() {
		return srcMediaDetails;
	}

	@Order(42)
	@ExportProperty(EventDto.REPORT_DATE_TIME)
	@ExportGroup(ExportGroupType.CORE)
	public Date getReportDateTime() {
		return reportDateTime;
	}

	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}

	@Order(43)
	@ExportProperty(EventDto.REPORTING_USER)
	@ExportGroup(ExportGroupType.CORE)
	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
	}

	@Order(44)
	@ExportProperty(EventDto.RESPONSIBLE_USER)
	@ExportGroup(ExportGroupType.CORE)
	public UserReferenceDto getResponsibleUser() {
		return responsibleUser;
	}

	public void setResponsibleUser(UserReferenceDto responsibleUser) {
		this.responsibleUser = responsibleUser;
	}

	@Order(45)
	@ExportProperty(PARTICIPANT_COUNT)
	@ExportGroup(ExportGroupType.CORE)
	public long getParticipantCount() {
		return participantCount;
	}

	public void setParticipantCount(long participantCount) {
		this.participantCount = participantCount;
	}

	@Order(46)
	@ExportProperty(CASE_COUNT)
	@ExportGroup(ExportGroupType.CORE)
	public long getCaseCount() {
		return caseCount;
	}

	public void setCaseCount(long caseCount) {
		this.caseCount = caseCount;
	}

	@Order(47)
	@ExportProperty(DEATH_COUNT)
	@ExportGroup(ExportGroupType.CORE)
	public long getDeathCount() {
		return deathCount;
	}

	public void setDeathCount(long deathCount) {
		this.deathCount = deathCount;
	}

	@Order(48)
	@ExportProperty(CONTACT_COUNT)
	@ExportGroup(ExportGroupType.CORE)
	public long getContactCount() {
		return contactCount;
	}

	public void setContactCount(long contactCount) {
		this.contactCount = contactCount;
	}

	@Order(49)
	@ExportProperty(CONTACT_COUNT_SOURCE_IN_EVENT)
	@ExportGroup(ExportGroupType.CORE)
	public long getContactCountSourceInEvent() {
		return contactCountSourceInEvent;
	}

	@Order(50)
	@ExportProperty(EventDto.EXTERNAL_TOKEN)
	@ExportGroup(ExportGroupType.CORE)
	public String getExternalToken() {
		return externalToken;
	}

	@Order(51)
	@ExportProperty(EventDto.INTERNAL_TOKEN)
	@ExportGroup(ExportGroupType.CORE)
	public String getInternalToken() {
		return internalToken;
	}

	public void setContactCountSourceInEvent(long contactCountSourceInEvent) {
		this.contactCountSourceInEvent = contactCountSourceInEvent;
	}

	public boolean getInJurisdictionOrOwned() {
		return isInJurisdictionOrOwned;
	}
}
