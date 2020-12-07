package de.symeda.sormas.backend.labmessage;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.hibernate.annotations.Type;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity
@Audited
public class LabMessage extends AbstractDomainObject {

	public static final String TABLE_NAME = "labmessage";

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

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getLabSampleId() {
		return labSampleId;
	}

	public void setLabSampleId(String labSampleId) {
		this.labSampleId = labSampleId;
	}

	@Enumerated(EnumType.STRING)
	public SampleMaterial getSampleMaterial() {
		return sampleMaterial;
	}

	public void setSampleMaterial(SampleMaterial sampleMaterial) {
		this.sampleMaterial = sampleMaterial;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getSampleLabName() {
		return sampleLabName;
	}

	public void setSampleLabName(String sampleLabName) {
		this.sampleLabName = sampleLabName;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getSampleLabExternalId() {
		return sampleLabExternalId;
	}

	public void setSampleLabExternalId(String sampleLabExternalId) {
		this.sampleLabExternalId = sampleLabExternalId;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getSampleLabPostalCode() {
		return sampleLabPostalCode;
	}

	public void setSampleLabPostalCode(String sampleLabPostalCode) {
		this.sampleLabPostalCode = sampleLabPostalCode;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getSampleLabCity() {
		return sampleLabCity;
	}

	public void setSampleLabCity(String sampleLabCity) {
		this.sampleLabCity = sampleLabCity;
	}

	@Enumerated(EnumType.STRING)
	public SpecimenCondition getSpecimenCondition() {
		return specimenCondition;
	}

	public void setSpecimenCondition(SpecimenCondition specimenCondition) {
		this.specimenCondition = specimenCondition;
	}

	@Enumerated(EnumType.STRING)
	public PathogenTestType getTestType() {
		return testType;
	}

	public void setTestType(PathogenTestType testType) {
		this.testType = testType;
	}

	@Enumerated(EnumType.STRING)
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

	@Enumerated(EnumType.STRING)
	public PathogenTestResultType getResult() {
		return result;
	}

	public void setResult(PathogenTestResultType result) {
		this.result = result;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Enumerated(EnumType.STRING)
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

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getPersonPostalCode() {
		return personPostalCode;
	}

	public void setPersonPostalCode(String personPostalCode) {
		this.personPostalCode = personPostalCode;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getPersonCity() {
		return personCity;
	}

	public void setPersonCity(String personCity) {
		this.personCity = personCity;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getPersonStreet() {
		return personStreet;
	}

	public void setPersonStreet(String personStreet) {
		this.personStreet = personStreet;
	}

	@Column(length = COLUMN_LENGTH_DEFAULT)
	public String getPersonHouseNumber() {
		return personHouseNumber;
	}

	public void setPersonHouseNumber(String personHouseNumber) {
		this.personHouseNumber = personHouseNumber;
	}

	@Type(type = "json")
	@Column(columnDefinition = "json")
	public String getLabMessageDetails() {
		return labMessageDetails;
	}

	public void setLabMessageDetails(String labMessageDetails) {
		this.labMessageDetails = labMessageDetails;
	}
}
