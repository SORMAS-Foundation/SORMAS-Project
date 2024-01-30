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
package de.symeda.sormas.api.caze;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.MergeableIndexDto;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.infrastructure.InfrastructureHelper;
import de.symeda.sormas.api.infrastructure.facility.FacilityHelper;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.person.SymptomJournalStatus;
import de.symeda.sormas.api.share.ExternalShareStatus;
import de.symeda.sormas.api.utils.EmbeddedPersonalData;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;
import de.symeda.sormas.api.uuid.HasUuid;

public class CaseIndexDto extends PseudonymizableIndexDto implements MergeableIndexDto, Serializable, Cloneable, HasUuid {

	private static final long serialVersionUID = -7764607075875188799L;

	public static final String I18N_PREFIX = "CaseData";

	public static final String ID = "id";
	public static final String UUID = "uuid";
	public static final String EPID_NUMBER = "epidNumber";
	public static final String EXTERNAL_ID = "externalID";
	public static final String EXTERNAL_TOKEN = "externalToken";
	public static final String INTERNAL_TOKEN = "internalToken";
	public static final String PERSON_UUID = "personUuid";
	public static final String PERSON_FIRST_NAME = "personFirstName";
	public static final String PERSON_LAST_NAME = "personLastName";
	public static final String DISEASE = "disease";
	public static final String DISEASE_VARIANT = "diseaseVariant";
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String CASE_CLASSIFICATION = "caseClassification";
	public static final String INVESTIGATION_STATUS = "investigationStatus";
	public static final String PRESENT_CONDITION = "presentCondition";
	public static final String REPORT_DATE = "reportDate";
	public static final String CREATION_DATE = "creationDate";
	public static final String REGION_UUID = "regionUuid";
	public static final String DELETE_REASON = "deletionReason";
	public static final String DISTRICT_UUID = "districtUuid";
	public static final String RESPONSIBLE_DISTRICT_NAME = "responsibleDistrictName";
	public static final String HEALTH_FACILITY_UUID = "healthFacilityUuid";
	public static final String HEALTH_FACILITY_NAME = "healthFacilityName";
	public static final String POINT_OF_ENTRY_NAME = "pointOfEntryName";
	public static final String SURVEILLANCE_OFFICER_UUID = "surveillanceOfficerUuid";
	public static final String OUTCOME = "outcome";
	public static final String SEX = "sex";
	public static final String AGE_AND_BIRTH_DATE = "ageAndBirthDate";
	public static final String COMPLETENESS = "completeness";
	public static final String QUARANTINE_TO = "quarantineTo";
	public static final String FOLLOW_UP_STATUS = "followUpStatus";
	public static final String FOLLOW_UP_UNTIL = "followUpUntil";
	public static final String SYMPTOM_JOURNAL_STATUS = "symptomJournalStatus";
	public static final String VACCINATION_STATUS = "vaccinationStatus";
	public static final String SURVEILLANCE_TOOL_LAST_SHARE_DATE = "surveillanceToolLastShareDate";
	public static final String SURVEILLANCE_TOOL_SHARE_COUNT = "surveillanceToolShareCount";
	public static final String SURVEILLANCE_TOOL_STATUS = "surveillanceToolStatus";

	private long id;
	private String epidNumber;
	private String externalID;
	private String externalToken;
	private String internalToken;
	private String personUuid;
	@PersonalData
	@SensitiveData
	private String personFirstName;
	@PersonalData
	@SensitiveData
	private String personLastName;
	private Disease disease;
	private DiseaseVariant diseaseVariant;
	private String diseaseDetails;
	private CaseClassification caseClassification;
	private InvestigationStatus investigationStatus;
	private PresentCondition presentCondition;
	private Date reportDate;
	private Date creationDate;
	@PersonalData
	@SensitiveData
	private String healthFacilityName;
	@PersonalData
	@SensitiveData
	private String pointOfEntryName;
	private String surveillanceOfficerUuid;
	private CaseOutcome outcome;
	private Sex sex;
	@EmbeddedPersonalData
	private AgeAndBirthDateDto ageAndBirthDate;
	private Float completeness;
	private Date quarantineTo;
	private FollowUpStatus followUpStatus;
	private Date followUpUntil;
	private SymptomJournalStatus symptomJournalStatus;
	private VaccinationStatus vaccinationStatus;
	private Integer visitCount;
	private Integer missedVisitsCount;

	private Date surveillanceToolLastShareDate;
	private Long surveillanceToolShareCount;
	private ExternalShareStatus surveillanceToolStatus;

	private String responsibleRegionUuid;
	private String responsibleDistrictUuid;
	private String regionUuid;
	private String districtUuid;
	private String responsibleDistrictName;

	private DeletionReason deletionReason;
	private String otherDeletionReason;

	private Boolean isInJurisdiction;

	//@formatter:off
	public CaseIndexDto(long id, String uuid, String epidNumber, String externalID, String externalToken, String internalToken, String personUuid, String personFirstName, String personLastName, Disease disease,
						DiseaseVariant diseaseVariant, String diseaseDetails, CaseClassification caseClassification, InvestigationStatus investigationStatus,
						PresentCondition presentCondition, Date reportDate, Date creationDate, String regionUuid,
						String districtUuid, String healthFacilityUuid, String healthFacilityName, String healthFacilityDetails,
						String pointOfEntryUuid, String pointOfEntryName, String pointOfEntryDetails, String surveillanceOfficerUuid, CaseOutcome outcome,
						Integer age, ApproximateAgeType ageType, Integer birthdateDD, Integer birthdateMM, Integer birthdateYYYY, Sex sex, Date quarantineTo,
						Float completeness, FollowUpStatus followUpStatus, Date followUpUntil,  SymptomJournalStatus symptomJournalStatus, VaccinationStatus vaccinationStatus,
						Date changeDate, Long facilityId, // XXX: unused, only here for TypedQuery mapping
						// responsible jurisdiction
						String responsibleRegionUuid, String responsibleDistrictUuid, String responsibleDistrictName, DeletionReason deletionReason, String otherDeletionReason, boolean isInJurisdiction,
						// others
						Integer visitCount
	) {
		//@formatter:on

		super(uuid);
		this.id = id;
		this.epidNumber = epidNumber;
		this.externalID = externalID;
		this.externalToken = externalToken;
		this.internalToken = internalToken;
		this.personUuid = personUuid;
		this.personFirstName = personFirstName;
		this.personLastName = personLastName;
		this.disease = disease;
		this.diseaseVariant = diseaseVariant;
		this.diseaseDetails = diseaseDetails;
		this.caseClassification = caseClassification;
		this.investigationStatus = investigationStatus;
		this.presentCondition = presentCondition;
		this.reportDate = reportDate;
		this.creationDate = creationDate;
		this.visitCount = visitCount;
		this.healthFacilityName = FacilityHelper.buildFacilityString(healthFacilityUuid, healthFacilityName, healthFacilityDetails);
		this.pointOfEntryName = InfrastructureHelper.buildPointOfEntryString(pointOfEntryUuid, pointOfEntryName, pointOfEntryDetails);
		this.surveillanceOfficerUuid = surveillanceOfficerUuid;
		this.outcome = outcome;
		this.ageAndBirthDate = new AgeAndBirthDateDto(age, ageType, birthdateDD, birthdateMM, birthdateYYYY);
		this.sex = sex;
		this.quarantineTo = quarantineTo;
		this.completeness = completeness;
		this.followUpStatus = followUpStatus;
		this.followUpUntil = followUpUntil;
		this.symptomJournalStatus = symptomJournalStatus;
		this.vaccinationStatus = vaccinationStatus;

		this.responsibleDistrictName = responsibleDistrictName;

		this.responsibleRegionUuid = responsibleRegionUuid;
		this.responsibleDistrictUuid = responsibleDistrictUuid;
		this.districtUuid = districtUuid;
		this.regionUuid = regionUuid;

		this.deletionReason = deletionReason;
		this.otherDeletionReason = otherDeletionReason;

		this.isInJurisdiction = isInJurisdiction;
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

	public String getPersonUuid() {
		return personUuid;
	}

	public void setPersonUuid(String personUuid) {
		this.personUuid = personUuid;
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

	public PresentCondition getPresentCondition() {
		return presentCondition;
	}

	public void setPresentCondition(PresentCondition presentCondition) {
		this.presentCondition = presentCondition;
	}

	public String getHealthFacilityName() {
		return healthFacilityName;
	}

	public void setHealthFacilityName(String healthFacilityName) {
		this.healthFacilityName = healthFacilityName;
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
		return new CaseReferenceDto(getUuid(), personFirstName, personLastName);
	}

	@Override
	public String getCaption() {
		return CaseReferenceDto.buildCaption(getUuid(), getPersonFirstName(), getPersonLastName());
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public String getExternalID() {
		return externalID;
	}

	public void setExternalID(String externalID) {
		this.externalID = externalID;
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

	public Date getQuarantineTo() {
		return quarantineTo;
	}

	public void setQuarantineTo(Date quarantineTo) {
		this.quarantineTo = quarantineTo;
	}

	public Boolean getInJurisdiction() {
		return isInJurisdiction;
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

	public FollowUpStatus getFollowUpStatus() {
		return followUpStatus;
	}

	public void setFollowUpStatus(FollowUpStatus followUpStatus) {
		this.followUpStatus = followUpStatus;
	}

	public Date getFollowUpUntil() {
		return followUpUntil;
	}

	public void setFollowUpUntil(Date followUpUntil) {
		this.followUpUntil = followUpUntil;
	}

	public SymptomJournalStatus getSymptomJournalStatus() {
		return symptomJournalStatus;
	}

	public void setSymptomJournalStatus(SymptomJournalStatus symptomJournalStatus) {
		this.symptomJournalStatus = symptomJournalStatus;
	}

	public VaccinationStatus getVaccinationStatus() {
		return vaccinationStatus;
	}

	public void setVaccinationStatus(VaccinationStatus vaccinationStatus) {
		this.vaccinationStatus = vaccinationStatus;
	}

	public Integer getVisitCount() {
		return visitCount;
	}

	public void setVisitCount(Integer visitCount) {
		this.visitCount = visitCount;
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

	public String getResponsibleRegionUuid() {
		return responsibleRegionUuid;
	}

	public void setResponsibleRegionUuid(String responsibleRegionUuid) {
		this.responsibleRegionUuid = responsibleRegionUuid;
	}

	public String getResponsibleDistrictUuid() {
		return responsibleDistrictUuid;
	}

	public void setResponsibleDistrictUuid(String responsibleDistrictUuid) {
		this.responsibleDistrictUuid = responsibleDistrictUuid;
	}

	public void setInJurisdiction(Boolean inJurisdiction) {
		isInJurisdiction = inJurisdiction;
	}

	public String getResponsibleDistrictName() {
		return responsibleDistrictName;
	}

	public void setResponsibleDistrictName(String responsibleDistrictName) {
		this.responsibleDistrictName = responsibleDistrictName;
	}

	public Integer getMissedVisitsCount() {
		return missedVisitsCount;
	}

	public void setMissedVisitsCount(Integer missedVisitsCount) {
		this.missedVisitsCount = missedVisitsCount;
	}
}
