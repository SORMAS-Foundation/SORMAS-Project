package de.symeda.sormas.api.labmessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.utils.DataHelper;

public class LabMessageDto extends EntityDto {

	public static final String I18N_PREFIX = "LabMessage";

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
	public static final String PERSON_PHONE = "personPhone";
	public static final String PERSON_EMAIL = "personEmail";
	public static final String PERSON_STREET = "personStreet";
	public static final String PERSON_HOUSE_NUMBER = "personHouseNumber";
	public static final String LAB_MESSAGE_DETAILS = "labMessageDetails";
	public static final String PROCESSED = "processed";
	public static final String REPORT_ID = "reportId";

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

	private List<TestReportDto> testReports = new ArrayList<>();

	private String labMessageDetails;
	private String reportId;

	private LabMessageStatus status = LabMessageStatus.UNPROCESSED;

	public Disease getTestedDisease() {
		return testedDisease;
	}

	public void setTestedDisease(Disease testedDisease) {
		this.testedDisease = testedDisease;
	}

	public Date getMessageDateTime() {
		return messageDateTime;
	}

	public void setMessageDateTime(Date messageDateTime) {
		this.messageDateTime = messageDateTime;
	}

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

	public String getSampleMaterialText() {
		return sampleMaterialText;
	}

	public void setSampleMaterialText(String sampleMaterialText) {
		this.sampleMaterialText = sampleMaterialText;
	}

	public SpecimenCondition getSpecimenCondition() {
		return specimenCondition;
	}

	public String getLabName() {
		return labName;
	}

	public void setLabName(String labName) {
		this.labName = labName;
	}

	public String getLabExternalId() {
		return labExternalId;
	}

	public void setLabExternalId(String labExternalId) {
		this.labExternalId = labExternalId;
	}

	public String getLabPostalCode() {
		return labPostalCode;
	}

	public void setLabPostalCode(String labPostalCode) {
		this.labPostalCode = labPostalCode;
	}

	public String getLabCity() {
		return labCity;
	}

	public void setLabCity(String labCity) {
		this.labCity = labCity;
	}

	public void setSpecimenCondition(SpecimenCondition specimenCondition) {
		this.specimenCondition = specimenCondition;
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

	public Sex getPersonSex() {
		return personSex;
	}

	public void setPersonSex(Sex personSex) {
		this.personSex = personSex;
	}

	public Integer getPersonBirthDateDD() {
		return personBirthDateDD;
	}

	public void setPersonBirthDateDD(Integer personBirthDateDD) {
		this.personBirthDateDD = personBirthDateDD;
	}

	public Integer getPersonBirthDateMM() {
		return personBirthDateMM;
	}

	public void setPersonBirthDateMM(Integer personBirthDateMM) {
		this.personBirthDateMM = personBirthDateMM;
	}

	public Integer getPersonBirthDateYYYY() {
		return personBirthDateYYYY;
	}

	public void setPersonBirthDateYYYY(Integer personBirthDateYYYY) {
		this.personBirthDateYYYY = personBirthDateYYYY;
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

	public String getPersonPhone() {
		return personPhone;
	}

	public void setPersonPhone(String personPhone) {
		this.personPhone = personPhone;
	}

	public String getPersonEmail() {
		return personEmail;
	}

	public void setPersonEmail(String personEmail) {
		this.personEmail = personEmail;
	}

	public List<TestReportDto> getTestReports() {
		return testReports;
	}

	public void setTestReports(List<TestReportDto> testReports) {
		this.testReports = testReports;
	}

	public void addTestReport(TestReportDto testReport) {
		testReport.setLabMessage(this.toReference());
		if (this.testReports == null) {
			List<TestReportDto> testReports = new ArrayList();
			testReports.add(testReport);
			this.testReports = testReports;
		} else {
			this.testReports.add(testReport);
		}
	}

	public String getLabMessageDetails() {
		return labMessageDetails;
	}

	public void setLabMessageDetails(String labMessageDetails) {
		this.labMessageDetails = labMessageDetails;
	}

	public LabMessageStatus getStatus() {
		return status;
	}

	public void setStatus(LabMessageStatus status) {
		this.status = status;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public static LabMessageDto build() {

		LabMessageDto labMessage = new LabMessageDto();
		labMessage.setUuid(DataHelper.createUuid());
		return labMessage;
	}

	public LabMessageReferenceDto toReference() {
		return new LabMessageReferenceDto(getUuid());
	}
}
