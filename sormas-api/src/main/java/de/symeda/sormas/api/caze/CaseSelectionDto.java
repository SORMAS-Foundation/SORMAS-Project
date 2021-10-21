package de.symeda.sormas.api.caze;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class CaseSelectionDto extends PseudonymizableIndexDto implements Serializable, Cloneable {

	public static final String I18N_PREFIX = "CaseData";

	public static final String UUID = "uuid";
	public static final String EPID_NUMBER = "epidNumber";
	public static final String EXTERNAL_ID = "externalID";
	public static final String PERSON_FIRST_NAME = "personFirstName";
	public static final String PERSON_LAST_NAME = "personLastName";
	public static final String AGE_AND_BIRTH_DATE = "ageAndBirthDate";
	public static final String RESPONSIBLE_DISTRICT_NAME = "responsibleDistrictName";
	public static final String HEALTH_FACILITY_NAME = "healthFacilityName";
	public static final String REPORT_DATE = "reportDate";
	public static final String SEX = "sex";
	public static final String CASE_CLASSIFICATION = "caseClassification";
	public static final String OUTCOME = "outcome";

	private String uuid;
	private String epidNumber;
	private String externalID;
	@PersonalData
	@SensitiveData
	private String personFirstName;
	@PersonalData
	@SensitiveData
	private String personLastName;
	private AgeAndBirthDateDto ageAndBirthDate;
	private String responsibleDistrictName;
	@PersonalData
	@SensitiveData
	private String healthFacilityName;
	private Date reportDate;
	private Sex sex;
	private CaseClassification caseClassification;
	private CaseOutcome outcome;

	private boolean isInJurisdiction;

	public CaseSelectionDto(
		String uuid,
		String epidNumber,
		String externalID,
		String personFirstName,
		String personLastName,
		AgeAndBirthDateDto ageAndBirthDate,
		String responsibleDistrictName,
		String healthFacilityName,
		Date reportDate,
		Sex sex,
		CaseClassification caseClassification,
		CaseOutcome outcome,
		boolean isInJurisdiction) {

		this.uuid = uuid;
		this.epidNumber = epidNumber;
		this.externalID = externalID;
		this.personFirstName = personFirstName;
		this.personLastName = personLastName;
		this.ageAndBirthDate = ageAndBirthDate;
		this.responsibleDistrictName = responsibleDistrictName;
		this.healthFacilityName = healthFacilityName;
		this.reportDate = reportDate;
		this.sex = sex;
		this.caseClassification = caseClassification;
		this.outcome = outcome;
		this.isInJurisdiction = isInJurisdiction;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getEpidNumber() {
		return epidNumber;
	}

	public void setEpidNumber(String epidNumber) {
		this.epidNumber = epidNumber;
	}

	public String getExternalID() {
		return externalID;
	}

	public void setExternalID(String externalID) {
		this.externalID = externalID;
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

	public AgeAndBirthDateDto getAgeAndBirthDate() {
		return ageAndBirthDate;
	}

	public void setAgeAndBirthDate(AgeAndBirthDateDto ageAndBirthDate) {
		this.ageAndBirthDate = ageAndBirthDate;
	}

	public String getResponsibleDistrictName() {
		return responsibleDistrictName;
	}

	public void setResponsibleDistrictName(String responsibleDistrictName) {
		this.responsibleDistrictName = responsibleDistrictName;
	}

	public String getHealthFacilityName() {
		return healthFacilityName;
	}

	public void setHealthFacilityName(String healthFacilityName) {
		this.healthFacilityName = healthFacilityName;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	public void setCaseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
	}

	public CaseOutcome getOutcome() {
		return outcome;
	}

	public void setOutcome(CaseOutcome outcome) {
		this.outcome = outcome;
	}

	public boolean isInJurisdiction() {
		return isInJurisdiction;
	}

	public void setInJurisdiction(boolean inJurisdiction) {
		isInJurisdiction = inJurisdiction;
	}

	public CaseReferenceDto toReference() {
		return new CaseReferenceDto(uuid, personFirstName, personLastName);
	}

	@Override
	public String toString() {
		return CaseReferenceDto.buildCaption(getUuid(), getPersonFirstName(), getPersonLastName());
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
