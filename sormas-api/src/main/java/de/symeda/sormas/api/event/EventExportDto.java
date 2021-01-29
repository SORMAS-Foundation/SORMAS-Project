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
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class EventExportDto implements Serializable {

	public static final String I18N_PREFIX = "EventExport";

	private String uuid;
	private String externalId;
	private String externalToken;
	private EventStatus eventStatus;
	private RiskLevel riskLevel;
	private EventInvestigationStatus eventInvestigationStatus;
	private long participantCount;
	private long caseCount;
	private long deathCount;
	private Disease disease;
	private String diseaseDetails;
	private Date startDate;
	private Date endDate;
	private Date evolutionDate;
	private String evolutionComment;
	private String eventTitle;
	private String eventDesc;
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
	private EventJurisdictionDto jurisdiction;

	public EventExportDto(
		String uuid,
		String externalId,
		String externalToken,
		EventStatus eventStatus,
		RiskLevel riskLevel,
		EventInvestigationStatus eventInvestigationStatus,
		Disease disease,
		String diseaseDetails,
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
		String reportingUserUid,
		String surveillanceOfficerUuid) {
		this.uuid = uuid;
		this.externalId = externalId;
		this.externalToken = externalToken;
		this.eventStatus = eventStatus;
		this.riskLevel = riskLevel;
		this.eventInvestigationStatus = eventInvestigationStatus;
		this.disease = disease;
		this.diseaseDetails = diseaseDetails;
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

		this.jurisdiction = new EventJurisdictionDto(reportingUserUid, surveillanceOfficerUuid, regionUuid, districtUuid, communityUuid);
	}

	@Order(0)
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Order(1)
	public String getExternalId() {
		return externalId;
	}

	@Order(2)
	public EventStatus getEventStatus() {
		return eventStatus;
	}

	public void setEventStatus(EventStatus eventStatus) {
		this.eventStatus = eventStatus;
	}

	@Order(3)
	public RiskLevel getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(RiskLevel riskLevel) {
		this.riskLevel = riskLevel;
	}

	@Order(4)
	public EventInvestigationStatus getEventInvestigationStatus() {
		return eventInvestigationStatus;
	}

	public void setEventInvestigationStatus(EventInvestigationStatus eventInvestigationStatus) {
		this.eventInvestigationStatus = eventInvestigationStatus;
	}

	@Order(5)
	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	@Order(6)
	public String getDiseaseDetails() {
		return diseaseDetails;
	}

	public void setDiseaseDetails(String diseaseDetails) {
		this.diseaseDetails = diseaseDetails;
	}

	@Order(7)
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Order(8)
	public Date getEndDate() {
		return endDate;
	}

	@Order(9)
	public Date getEvolutionDate() {
		return evolutionDate;
	}

	public void setEvolutionDate(Date evolutionDate) {
		this.evolutionDate = evolutionDate;
	}

	@Order(10)
	public String getEvolutionComment() {
		return evolutionComment;
	}

	public void setEvolutionComment(String evolutionComment) {
		this.evolutionComment = evolutionComment;
	}

	@Order(11)
	public String getEventTitle() {
		return eventTitle;
	}

	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}

	@Order(12)
	public String getEventDesc() {
		return eventDesc;
	}

	public void setEventDesc(String eventDesc) {
		this.eventDesc = eventDesc;
	}

	@Order(13)
	public DiseaseTransmissionMode getDiseaseTransmissionMode() {
		return diseaseTransmissionMode;
	}

	@Order(14)
	public YesNoUnknown getNosocomial() {
		return nosocomial;
	}

	@Order(15)
	public YesNoUnknown getTransregionalOutbreak() {
		return transregionalOutbreak;
	}

	@Order(16)
	public String getMeansOfTransport() {
		return meansOfTransport;
	}

	@Order(17)
	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	@Order(18)
	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	@Order(19)
	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	@Order(20)
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Order(21)
	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	@Order(22)
	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	@Order(23)
	public String getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	@Order(24)
	public EventSourceType getSrcType() {
		return srcType;
	}

	@Order(25)
	public String getSrcInstitutionalPartnerType() {
		return srcInstitutionalPartnerType;
	}

	@Order(26)
	public String getSrcFirstName() {
		return srcFirstName;
	}

	public void setSrcFirstName(String srcFirstName) {
		this.srcFirstName = srcFirstName;
	}

	@Order(27)
	public String getSrcLastName() {
		return srcLastName;
	}

	public void setSrcLastName(String srcLastName) {
		this.srcLastName = srcLastName;
	}

	@Order(28)
	public String getSrcTelNo() {
		return srcTelNo;
	}

	public void setSrcTelNo(String srcTelNo) {
		this.srcTelNo = srcTelNo;
	}

	@Order(29)
	public String getSrcEmail() {
		return srcEmail;
	}

	@Order(30)
	public String getSrcMediaWebsite() {
		return srcMediaWebsite;
	}

	@Order(31)
	public String getSrcMediaName() {
		return srcMediaName;
	}

	@Order(32)
	public String getSrcMediaDetails() {
		return srcMediaDetails;
	}

	@Order(33)
	public Date getReportDateTime() {
		return reportDateTime;
	}

	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}

	@Order(34)
	public long getParticipantCount() {
		return participantCount;
	}

	public void setParticipantCount(long participantCount) {
		this.participantCount = participantCount;
	}

	@Order(35)
	public long getCaseCount() {
		return caseCount;
	}

	public void setCaseCount(long caseCount) {
		this.caseCount = caseCount;
	}

	@Order(36)
	public long getDeathCount() {
		return deathCount;
	}

	@Order(37)
	public String getExternalToken() {
		return externalToken;
	}

	public void setDeathCount(long deathCount) {
		this.deathCount = deathCount;
	}

	public EventJurisdictionDto getJurisdiction() {
		return jurisdiction;
	}
}
