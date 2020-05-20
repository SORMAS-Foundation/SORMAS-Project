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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.caze;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.facility.FacilityHelper;
import de.symeda.sormas.api.infrastructure.InfrastructureHelper;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.PersonalData;

public class CaseIndexDto implements Serializable {

	private static final long serialVersionUID = -7764607075875188799L;

	public static final String I18N_PREFIX = "CaseData";

	public static final String ID = "id";
	public static final String UUID = "uuid";
	public static final String EPID_NUMBER = "epidNumber";
	public static final String EXTERNAL_ID = "externalID";
	public static final String PERSON_FIRST_NAME = "personFirstName";
	public static final String PERSON_LAST_NAME = "personLastName";
	public static final String DISEASE = "disease";
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String CASE_CLASSIFICATION = "caseClassification";
	public static final String INVESTIGATION_STATUS = "investigationStatus";
	public static final String PRESENT_CONDITION = "presentCondition";
	public static final String REPORT_DATE = "reportDate";
	public static final String CREATION_DATE = "creationDate";
	public static final String REGION_UUID = "regionUuid";
	public static final String DISTRICT_UUID = "districtUuid";
	public static final String DISTRICT_NAME = "districtName";
	public static final String HEALTH_FACILITY_UUID = "healthFacilityUuid";
	public static final String HEALTH_FACILITY_NAME = "healthFacilityName";
	public static final String POINT_OF_ENTRY_NAME = "pointOfEntryName";
	public static final String SURVEILLANCE_OFFICER_UUID = "surveillanceOfficerUuid";
	public static final String OUTCOME = "outcome";
	public static final String SEX = "sex";
	public static final String AGE_AND_BIRTH_DATE = "ageAndBirthDate";
	public static final String COMPLETENESS = "completeness";
	public static final String QUARANTINE_TO = "quarantineTo";

	private long id;
	private String uuid;
	private String epidNumber;
	private String externalID;
	@PersonalData
	private String personFirstName;
	@PersonalData
	private String personLastName;
	private Disease disease;
	private String diseaseDetails;
	private CaseClassification caseClassification;
	private InvestigationStatus investigationStatus;
	private PresentCondition presentCondition;
	private Date reportDate;
	private String reportingUserUuid;
	private Date creationDate;
	private String regionUuid;
	private String districtUuid;
	private String districtName;
	@PersonalData
	private String communityUuid;
	@PersonalData
	private String healthFacilityUuid;
	@PersonalData
	private String healthFacilityName;
	@PersonalData
	private String pointOfEntryUuid;
	@PersonalData
	private String pointOfEntryName;
	private String surveillanceOfficerUuid;
	private CaseOutcome outcome;
	private Sex sex;
	private AgeAndBirthDateDto ageAndBirthDate;
	private Float completeness;
	private Date quarantineTo;

	public CaseIndexDto(long id, String uuid, String epidNumber, String externalID, String personFirstName, String personLastName, Disease disease,
						String diseaseDetails, CaseClassification caseClassification, InvestigationStatus investigationStatus,
						PresentCondition presentCondition, Date reportDate, String reportingUserUuid, Date creationDate, String regionUuid,
						String districtUuid, String districtName, String communityUuid, String healthFacilityUuid, String healthFacilityName, String healthFacilityDetails,
						String pointOfEntryUuid, String pointOfEntryName, String pointOfEntryDetails, String surveillanceOfficerUuid, CaseOutcome outcome,
						Integer age, ApproximateAgeType ageType, Integer birthdateDD, Integer birthdateMM, Integer birthdateYYYY, Sex sex, Date quarantineTo, Float completeness) {
		this.id = id;
		this.uuid = uuid;
		this.epidNumber = epidNumber;
		this.externalID = externalID;
		this.personFirstName = personFirstName;
		this.personLastName = personLastName;
		this.disease = disease;
		this.diseaseDetails = diseaseDetails;
		this.caseClassification = caseClassification;
		this.investigationStatus = investigationStatus;
		this.presentCondition = presentCondition;
		this.reportDate = reportDate;
		this.reportingUserUuid = reportingUserUuid;
		this.creationDate = creationDate;
		this.regionUuid = regionUuid;
		this.districtUuid = districtUuid;
		this.districtName = districtName;
		this.communityUuid = communityUuid;
		this.healthFacilityUuid = healthFacilityUuid;
		this.healthFacilityName = FacilityHelper.buildFacilityString(healthFacilityUuid, healthFacilityName, healthFacilityDetails);
		this.pointOfEntryUuid = pointOfEntryUuid;
		this.pointOfEntryName = InfrastructureHelper.buildPointOfEntryString(pointOfEntryUuid, pointOfEntryName, pointOfEntryDetails);
		this.surveillanceOfficerUuid = surveillanceOfficerUuid;
		this.outcome = outcome;
		this.ageAndBirthDate = new AgeAndBirthDateDto(age, ageType, birthdateDD, birthdateMM, birthdateYYYY);
		this.sex = sex;
		this.quarantineTo = quarantineTo;
		this.completeness = completeness;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEpidNumber() {
		return epidNumber;
	}

	public void setEpidNumber(String epidNumber) {
		this.epidNumber = epidNumber;
	}

	public String getPersonFirstName() {
		return personFirstName;
	}

	public void setPersonFirstName(String personFirstName) {
		this.personFirstName = personFirstName;
	}

	public String getPersonLastName() {
		return personLastName;
	}

	public void setPersonLastName(String personLastName) {
		this.personLastName = personLastName;
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

	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	public void setCaseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
	}

	public InvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public void setInvestigationStatus(InvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public String getReportingUserUuid() {
		return reportingUserUuid;
	}

	public void setReportingUserUuid(String reportingUserUuid) {
		this.reportingUserUuid = reportingUserUuid;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getRegionUuid() {
		return regionUuid;
	}

	public void setRegionUuid(String regionUuid) {
		this.regionUuid = regionUuid;
	}

	public String getDistrictUuid() {
		return districtUuid;
	}

	public void setDistrictUuid(String districtUuid) {
		this.districtUuid = districtUuid;
	}

	public String getSurveillanceOfficerUuid() {
		return surveillanceOfficerUuid;
	}

	public void setSurveillanceOfficerUuid(String surveillanceOfficerUuid) {
		this.surveillanceOfficerUuid = surveillanceOfficerUuid;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public String getCommunityUuid() {
		return communityUuid;
	}

	public void setCommunityUuid(String communityUuid) {
		this.communityUuid = communityUuid;
	}

	public PresentCondition getPresentCondition() {
		return presentCondition;
	}

	public void setPresentCondition(PresentCondition presentCondition) {
		this.presentCondition = presentCondition;
	}

	public String getHealthFacilityUuid() {
		return healthFacilityUuid;
	}

	public void setHealthFacilityUuid(String healthFacilityUuid) {
		this.healthFacilityUuid = healthFacilityUuid;
	}

	public String getHealthFacilityName() {
		return healthFacilityName;
	}

	public void setHealthFacilityName(String healthFacilityName) {
		this.healthFacilityName = healthFacilityName;
	}

	public String getPointOfEntryUuid() {
		return pointOfEntryUuid;
	}

	public void setPointOfEntryUuid(String pointOfEntryUuid) {
		this.pointOfEntryUuid = pointOfEntryUuid;
	}

	public String getPointOfEntryName() {
		return pointOfEntryName;
	}

	public void setPointOfEntryName(String pointOfEntryName) {
		this.pointOfEntryName = pointOfEntryName;
	}

	public CaseOutcome getOutcome() {
		return outcome;
	}

	public void setOutcome(CaseOutcome outcome) {
		this.outcome = outcome;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public AgeAndBirthDateDto getAgeAndBirthDate() {
		return ageAndBirthDate;
	}

	public void setAgeAndBirthDate(AgeAndBirthDateDto ageAndBirthDate) {
		this.ageAndBirthDate = ageAndBirthDate;
	}

	public Float getCompleteness() {
		return completeness;
	}

	public void setCompleteness(Float completeness) {
		this.completeness = completeness;
	}

	public CaseReferenceDto toReference() {
		return new CaseReferenceDto(getUuid(), getPersonFirstName(), getPersonLastName());
	}

	@Override
	public String toString() {
		return CaseReferenceDto.buildCaption(getUuid(), getPersonFirstName(), getPersonLastName());
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getExternalID() {
		return externalID;
	}

	public void setExternalID(String externalID) {
		this.externalID = externalID;
	}

	public Date getQuarantineTo() {
		return quarantineTo;
	}

	public void setQuarantineTo(Date quarantineTo) {
		this.quarantineTo = quarantineTo;
	}

}
