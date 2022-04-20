package de.symeda.sormas.api.labmessage;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.user.UserReferenceDto;

public class LabMessageIndexDto implements Serializable {

	public static final String I18N_PREFIX = "LabMessage";

	public static final String UUID = "uuid";
	public static final String MESSAGE_DATE_TIME = "messageDateTime";
	public static final String LAB_NAME = "labName";
	public static final String LAB_POSTAL_CODE = "labPostalCode";
	public static final String TESTED_DISEASE = "testedDisease";
	public static final String SAMPLE_OVERALL_TEST_RESULT = "sampleOverallTestResult";
	public static final String PERSON_FIRST_NAME = "personFirstName";
	public static final String PERSON_LAST_NAME = "personLastName";
	public static final String PERSON_BIRTH_DATE = "personBirthDate";
	public static final String PERSON_POSTAL_CODE = "personPostalCode";
	public static final String STATUS = "status";
	public static final String ASSIGNEE = "assignee";

	private String uuid;

	private Date messageDateTime;
	private String labName;
	private String labPostalCode;
	private Disease testedDisease;
	private PathogenTestResultType sampleOverallTestResult;
	private String personFirstName;
	private String personLastName;
	private Date personBirthDate;
	private String personPostalCode;
	private LabMessageStatus status;
	private UserReferenceDto assignee;

	public LabMessageIndexDto(
		String uuid,
		Date messageDateTime,
		String labName,
		String labPostalCode,
		Disease testedDisease,
		PathogenTestResultType sampleOverallTestResult,
		String personFirstName,
		String personLastName,
		Integer personBirthDateYYYY,
		Integer personBirthDateMM,
		Integer personBirthDateDD,
		String personPostalCode,
		LabMessageStatus status,
		String assigneeUuid,
		String assigneeFirstName,
		String assigneeLastName) {

		this.uuid = uuid;
		this.messageDateTime = messageDateTime;
		this.labName = labName;
		this.labPostalCode = labPostalCode;
		this.testedDisease = testedDisease;
		this.sampleOverallTestResult = sampleOverallTestResult;
		this.personFirstName = personFirstName;
		this.personLastName = personLastName;
		this.personPostalCode = personPostalCode;
		this.status = status;
		if (assigneeUuid != null) {
			this.assignee = new UserReferenceDto(assigneeUuid, assigneeFirstName, assigneeLastName);
		}

		if (personBirthDateYYYY != null && personBirthDateMM != null && personBirthDateDD != null) {
			Calendar birthdate = Calendar.getInstance();
			birthdate.setLenient(false);
			try {
				birthdate.set(personBirthDateYYYY, personBirthDateMM - 1, personBirthDateDD, 0, 0, 0);
				personBirthDate = birthdate.getTime();
			} catch (Exception e) {
				personBirthDate = null;
			}
		}
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Date getMessageDateTime() {
		return messageDateTime;
	}

	public void setMessageDateTime(Date messageDateTime) {
		this.messageDateTime = messageDateTime;
	}

	public String getLabName() {
		return labName;
	}

	public void setLabName(String labName) {
		this.labName = labName;
	}

	public String getLabPostalCode() {
		return labPostalCode;
	}

	public void setLabPostalCode(String testLabPostalCode) {
		this.labPostalCode = testLabPostalCode;
	}

	public Disease getTestedDisease() {
		return testedDisease;
	}

	public void setTestedDisease(Disease testedDisease) {
		this.testedDisease = testedDisease;
	}

	public PathogenTestResultType getSampleOverallTestResult() {
		return sampleOverallTestResult;
	}

	public void setSampleOverallTestResult(PathogenTestResultType sampleOverallTestResult) {
		this.sampleOverallTestResult = sampleOverallTestResult;
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

	public Date getPersonBirthDate() {
		return personBirthDate;
	}

	public void setPersonBirthDate(Date personBirthDate) {
		this.personBirthDate = personBirthDate;
	}

	public String getPersonPostalCode() {
		return personPostalCode;
	}

	public void setPersonPostalCode(String personPostalCode) {
		this.personPostalCode = personPostalCode;
	}

	public LabMessageStatus getStatus() {
		return status;
	}

	public void setStatus(LabMessageStatus status) {
		this.status = status;
	}

	public UserReferenceDto getAssignee() {
		return assignee;
	}

	public void setAssignee(UserReferenceDto assignee) {
		this.assignee = assignee;
	}
}
