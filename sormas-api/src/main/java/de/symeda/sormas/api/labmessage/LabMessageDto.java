package de.symeda.sormas.api.labmessage;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.utils.DataHelper;

public class LabMessageDto extends EntityDto {

	public static final String I18N_PREFIX = "Location";

	public static final String SAMPLE_DATE_TIME = "sampleDateTime";
	public static final String SAMPLE_RECEIVED_DATE = "sampleReceivedDate";
	public static final String LAB_SAMPLE_ID = "labSampleId";
	public static final String SAMPLE_MATERIAL = "sampleMaterial";
	public static final String LAB_NAME = "LabName";
	public static final String LAB_EXTERNAL_ID = "LabExternalId";
	public static final String LAB_POSTAL_CODE = "LabPostalCode";
	public static final String LAB_CITY = "LabCity";
	public static final String SPECIMEN_CONDITION = "specimenCondition";
	public static final String TEST_TYPE = "testType";
	public static final String TESTED_DISEASE = "testedDisease";
	public static final String TEST_DATE_TIME = "testDateTime";
	public static final String RESULT = "result";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String SEX = "sex";
	public static final String BIRTH_DATE_DD = "birthDateDD";
	public static final String BIRTH_DATE_MM = "birthDateMM";
	public static final String BIRTH_DATE_YYYY = "birthDateYYYY";
	public static final String PERSON_POSTAL_CODE = "personPostalCode";
	public static final String PERSON_CITY = "personCity";
	public static final String PERSON_STREET = "personStreet";
	public static final String PERSON_HOUSE_NUMBER = "personHouseNumber";
	public static final String LAB_MESSAGE_DETAILS = "labMessageDetails";

	private Date sampleDateTime;
	private Date sampleReceivedDate;
	private String labSampleId;
	private SampleMaterial sampleMaterial;
	private String sampleLabName;
	private String sampleLabExternalId;
	private String sampleLabPostalCode;
	private String sampleLabCity;
	private SpecimenCondition specimenCondition;
	private PathogenTestType testType;
	private Disease testedDisease;
	private Date testDateTime;
	private PathogenTestResultType result;
	private String firstName;
	private String lastName;
	private Sex sex;
	private Integer birthDateDD;
	private Integer birthDateMM;
	private Integer birthDateYYYY;
	private String personPostalCode;
	private String personCity;
	private String personStreet;
	private String personHouseNumber;

	private String labMessageDetails;

	public Date getSampleDateTime() {
		return sampleDateTime;
	}

	public void setSampleDateTime(Date sampleDateTime) {
		this.sampleDateTime = sampleDateTime;
	}

	public Date getSampleReceivedDate() {
		return sampleReceivedDate;
	}

	public void setSampleReceivedDate(Date sampleReceivedDate) {
		this.sampleReceivedDate = sampleReceivedDate;
	}

	public String getLabSampleId() {
		return labSampleId;
	}

	public void setLabSampleId(String labSampleId) {
		this.labSampleId = labSampleId;
	}

	public SampleMaterial getSampleMaterial() {
		return sampleMaterial;
	}

	public void setSampleMaterial(SampleMaterial sampleMaterial) {
		this.sampleMaterial = sampleMaterial;
	}

	public String getSampleLabName() {
		return sampleLabName;
	}

	public void setSampleLabName(String sampleLabName) {
		this.sampleLabName = sampleLabName;
	}

	public String getSampleLabExternalId() {
		return sampleLabExternalId;
	}

	public void setSampleLabExternalId(String sampleLabExternalId) {
		this.sampleLabExternalId = sampleLabExternalId;
	}

	public String getSampleLabPostalCode() {
		return sampleLabPostalCode;
	}

	public void setSampleLabPostalCode(String sampleLabPostalCode) {
		this.sampleLabPostalCode = sampleLabPostalCode;
	}

	public String getSampleLabCity() {
		return sampleLabCity;
	}

	public void setSampleLabCity(String sampleLabCity) {
		this.sampleLabCity = sampleLabCity;
	}

	public SpecimenCondition getSpecimenCondition() {
		return specimenCondition;
	}

	public void setSpecimenCondition(SpecimenCondition specimenCondition) {
		this.specimenCondition = specimenCondition;
	}

	public PathogenTestType getTestType() {
		return testType;
	}

	public void setTestType(PathogenTestType testType) {
		this.testType = testType;
	}

	public Disease getTestedDisease() {
		return testedDisease;
	}

	public void setTestedDisease(Disease testedDisease) {
		this.testedDisease = testedDisease;
	}

	public Date getTestDateTime() {
		return testDateTime;
	}

	public void setTestDateTime(Date testDateTime) {
		this.testDateTime = testDateTime;
	}

	public PathogenTestResultType getResult() {
		return result;
	}

	public void setResult(PathogenTestResultType result) {
		this.result = result;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public Integer getBirthDateDD() {
		return birthDateDD;
	}

	public void setBirthDateDD(Integer birthDateDD) {
		this.birthDateDD = birthDateDD;
	}

	public Integer getBirthDateMM() {
		return birthDateMM;
	}

	public void setBirthDateMM(Integer birthDateMM) {
		this.birthDateMM = birthDateMM;
	}

	public Integer getBirthDateYYYY() {
		return birthDateYYYY;
	}

	public void setBirthDateYYYY(Integer birthDateYYYY) {
		this.birthDateYYYY = birthDateYYYY;
	}

	public String getPersonPostalCode() {
		return personPostalCode;
	}

	public void setPersonPostalCode(String personPostalCode) {
		this.personPostalCode = personPostalCode;
	}

	public String getPersonCity() {
		return personCity;
	}

	public void setPersonCity(String personCity) {
		this.personCity = personCity;
	}

	public String getPersonStreet() {
		return personStreet;
	}

	public void setPersonStreet(String personStreet) {
		this.personStreet = personStreet;
	}

	public String getPersonHouseNumber() {
		return personHouseNumber;
	}

	public void setPersonHouseNumber(String personHouseNumber) {
		this.personHouseNumber = personHouseNumber;
	}

	public String getLabMessageDetails() {
		return labMessageDetails;
	}

	public void setLabMessageDetails(String labMessageDetails) {
		this.labMessageDetails = labMessageDetails;
	}

	public static LabMessageDto build() {

		LabMessageDto labMessage = new LabMessageDto();
		labMessage.setUuid(DataHelper.createUuid());
		return labMessage;
	}
}
