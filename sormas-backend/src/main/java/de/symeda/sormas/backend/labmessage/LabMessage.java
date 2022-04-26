package de.symeda.sormas.backend.labmessage;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.labmessage.ExternalMessageType;
import de.symeda.sormas.api.labmessage.LabMessageStatus;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.user.User;

@Entity(name = "labmessage")
@Audited
public class LabMessage extends AbstractDomainObject {

	public static final String TABLE_NAME = "labmessage";

	public static final String TYPE = "type";
	public static final String TESTED_DISEASE = "testedDisease";
	public static final String MESSAGE_DATE_TIME = "messageDateTime";
	public static final String SAMPLE_DATE_TIME = "sampleDateTime";
	public static final String SAMPLE_RECEIVED_DATE = "sampleReceivedDate";
	public static final String LAB_SAMPLE_ID = "labSampleId";
	public static final String SAMPLE_MATERIAL = "sampleMaterial";
	public static final String SAMPLE_MATERIAL_TEXT = "sampleMaterialText";
	public static final String SPECIMEN_CONDITION = "specimenCondition";
	public static final String LAB_NAME = "labName";
	public static final String LAB_EXTERNAL_ID = "labExternalId";
	public static final String LAB_POSTAL_CODE = "labPostalCode";
	public static final String LAB_CITY = "labCity";
	public static final String PERSON_FIRST_NAME = "personFirstName";
	public static final String PERSON_LAST_NAME = "personLastName";
	public static final String PERSON_SEX = "personSex";
	public static final String PERSON_BIRTH_DATE_DD = "personBirthDateDD";
	public static final String PERSON_BIRTH_DATE_MM = "personBirthDateMM";
	public static final String PERSON_BIRTH_DATE_YYYY = "personBirthDateYYYY";
	public static final String PERSON_POSTAL_CODE = "personPostalCode";
	public static final String PERSON_CITY = "personCity";
	public static final String PERSON_STREET = "personStreet";
	public static final String PERSON_HOUSE_NUMBER = "personHouseNumber";
	public static final String PERSON_PHONE = "personPhone";
	public static final String PERSON_EMAIL = "personEmail";
	public static final String LAB_MESSAGE_DETAILS = "labMessageDetails";
	public static final String STATUS = "status";
	public static final String REPORT_ID = "reportId";
	public static final String SAMPLE_OVERALL_TEST_RESULT = "sampleOverallTestResult";
	public static final String SAMPLE = "sample";
	public static final String ASSIGNEE = "assignee";

	private ExternalMessageType type;
	private Disease testedDisease;
	private Date messageDateTime;
	private Date sampleDateTime;
	private Date sampleReceivedDate;
	private String labSampleId;
	private SampleMaterial sampleMaterial;
	private String sampleMaterialText;
	private SpecimenCondition specimenCondition;

	private String labName;
	private String labExternalId;
	private String labPostalCode;
	private String labCity;

	private String personFirstName;
	private String personLastName;
	private Sex personSex;
	private Integer personBirthDateDD;
	private Integer personBirthDateMM;
	private Integer personBirthDateYYYY;
	private String personPostalCode;
	private String personCity;
	private String personStreet;
	private String personHouseNumber;
	private String personPhone;
	private String personEmail;
	private List<TestReport> testReports;
	private String labMessageDetails;
	//Lab messages related to each other should have the same reportId
	private String reportId;
	private PathogenTestResultType sampleOverallTestResult;
	private Sample sample;

	private LabMessageStatus status = LabMessageStatus.UNPROCESSED;
	private User assignee;

	@Enumerated(EnumType.STRING)
	public ExternalMessageType getType() {
		return type;
	}

	public void setType(ExternalMessageType type) {
		this.type = type;
	}

	@Enumerated(EnumType.STRING)
	public Disease getTestedDisease() {
		return testedDisease;
	}

	public void setTestedDisease(Disease testedDisease) {
		this.testedDisease = testedDisease;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getMessageDateTime() {
		return messageDateTime;
	}

	public void setMessageDateTime(Date messageDateTime) {
		this.messageDateTime = messageDateTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getSampleDateTime() {
		return sampleDateTime;
	}

	public void setSampleDateTime(Date sampleDateTime) {
		this.sampleDateTime = sampleDateTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getSampleReceivedDate() {
		return sampleReceivedDate;
	}

	public void setSampleReceivedDate(Date sampleReceivedDate) {
		this.sampleReceivedDate = sampleReceivedDate;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
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

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getSampleMaterialText() {
		return sampleMaterialText;
	}

	public void setSampleMaterialText(String sampleMaterialText) {
		this.sampleMaterialText = sampleMaterialText;
	}

	@Enumerated(EnumType.STRING)
	public SpecimenCondition getSpecimenCondition() {
		return specimenCondition;
	}

	public void setSpecimenCondition(SpecimenCondition specimenCondition) {
		this.specimenCondition = specimenCondition;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getLabName() {
		return labName;
	}

	public void setLabName(String labName) {
		this.labName = labName;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getLabExternalId() {
		return labExternalId;
	}

	public void setLabExternalId(String labExternalId) {
		this.labExternalId = labExternalId;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getLabPostalCode() {
		return labPostalCode;
	}

	public void setLabPostalCode(String testLabPostalCode) {
		this.labPostalCode = testLabPostalCode;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getLabCity() {
		return labCity;
	}

	public void setLabCity(String labCity) {
		this.labCity = labCity;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPersonFirstName() {
		return personFirstName;
	}

	public void setPersonFirstName(String personFirstName) {
		this.personFirstName = personFirstName;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPersonLastName() {
		return personLastName;
	}

	public void setPersonLastName(String personLastName) {
		this.personLastName = personLastName;
	}

	@Enumerated(EnumType.STRING)
	public Sex getPersonSex() {
		return personSex;
	}

	public void setPersonSex(Sex personSex) {
		this.personSex = personSex;
	}

	@Column(name = "personbirthdatedd")
	public Integer getPersonBirthDateDD() {
		return personBirthDateDD;
	}

	public void setPersonBirthDateDD(Integer personBirthDateDD) {
		this.personBirthDateDD = personBirthDateDD;
	}

	@Column(name = "personbirthdatemm")
	public Integer getPersonBirthDateMM() {
		return personBirthDateMM;
	}

	public void setPersonBirthDateMM(Integer personBirthDateMM) {
		this.personBirthDateMM = personBirthDateMM;
	}

	@Column(name = "personbirthdateyyyy")
	public Integer getPersonBirthDateYYYY() {
		return personBirthDateYYYY;
	}

	public void setPersonBirthDateYYYY(Integer personBirthDateYYYY) {
		this.personBirthDateYYYY = personBirthDateYYYY;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPersonPostalCode() {
		return personPostalCode;
	}

	public void setPersonPostalCode(String personPostalCode) {
		this.personPostalCode = personPostalCode;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPersonCity() {
		return personCity;
	}

	public void setPersonCity(String personCity) {
		this.personCity = personCity;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPersonStreet() {
		return personStreet;
	}

	public void setPersonStreet(String personStreet) {
		this.personStreet = personStreet;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPersonHouseNumber() {
		return personHouseNumber;
	}

	public void setPersonHouseNumber(String personHouseNumber) {
		this.personHouseNumber = personHouseNumber;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPersonPhone() {
		return personPhone;
	}

	public void setPersonPhone(String personPhone) {
		this.personPhone = personPhone;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPersonEmail() {
		return personEmail;
	}

	public void setPersonEmail(String personEmail) {
		this.personEmail = personEmail;
	}

	@Column
	public String getLabMessageDetails() {
		return labMessageDetails;
	}

	public void setLabMessageDetails(String labMessageDetails) {
		this.labMessageDetails = labMessageDetails;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public LabMessageStatus getStatus() {
		return status;
	}

	public void setStatus(LabMessageStatus status) {
		this.status = status;
	}

	@OneToMany(cascade = CascadeType.ALL, mappedBy = TestReport.LAB_MESSAGE, fetch = FetchType.LAZY)
	public List<TestReport> getTestReports() {
		return testReports;
	}

	public void setTestReports(List<TestReport> testReports) {
		this.testReports = testReports;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	@Enumerated(EnumType.STRING)
	public PathogenTestResultType getSampleOverallTestResult() {
		return sampleOverallTestResult;
	}

	public void setSampleOverallTestResult(PathogenTestResultType sampleOverallTestResult) {
		this.sampleOverallTestResult = sampleOverallTestResult;
	}

	@ManyToOne
	@JoinColumn
	public User getAssignee() {
		return assignee;
	}

	public void setAssignee(User assignee) {
		this.assignee = assignee;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public Sample getSample() {
		return sample;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
	}

}
