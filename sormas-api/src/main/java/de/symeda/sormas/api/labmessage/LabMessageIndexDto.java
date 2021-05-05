package de.symeda.sormas.api.labmessage;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestResultType;

public class LabMessageIndexDto implements Serializable {

	public static final String I18N_PREFIX = "LabMessage";

	public static final String UUID = "uuid";
	public static final String MESSAGE_DATE_TIME = "messageDateTime";
	public static final String TEST_LAB_NAME = "testLabName";
	public static final String TEST_LAB_POSTAL_CODE = "testLabPostalCode";
	public static final String TESTED_DISEASE = "testedDisease";
	public static final String TEST_RESULT = "testResult";
	public static final String PERSON_FIRST_NAME = "personFirstName";
	public static final String PERSON_LAST_NAME = "personLastName";
	public static final String PERSON_POSTAL_CODE = "personPostalCode";
	public static final String STATUS = "status";

	private String uuid;

	private Date messageDateTime;
	private String testLabName;
	private String testLabPostalCode;
	private Disease testedDisease;
	private PathogenTestResultType testResult;
	private String personFirstName;
	private String personLastName;
	private String personPostalCode;
	private LabMessageStatus status;

	public LabMessageIndexDto(
			String uuid,
			Date messageDateTime,
			String testLabName,
			String testLabPostalCode,
			Disease testedDisease,
			PathogenTestResultType testResult,
			String personFirstName,
			String personLastName,
			String personPostalCode,
		LabMessageStatus status) {

		this.uuid = uuid;
		this.messageDateTime = messageDateTime;
		this.testLabName = testLabName;
		this.testLabPostalCode = testLabPostalCode;
		this.testedDisease = testedDisease;
		this.testResult = testResult;
		this.personFirstName = personFirstName;
		this.personLastName = personLastName;
		this.personPostalCode = personPostalCode;
		this.status = status;
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

	public String getTestLabName() {
		return testLabName;
	}

	public void setTestLabName(String testLabName) {
		this.testLabName = testLabName;
	}

	public String getTestLabPostalCode() {
		return testLabPostalCode;
	}

	public void setTestLabPostalCode(String testLabPostalCode) {
		this.testLabPostalCode = testLabPostalCode;
	}

	public Disease getTestedDisease() {
		return testedDisease;
	}

	public void setTestedDisease(Disease testedDisease) {
		this.testedDisease = testedDisease;
	}

	public PathogenTestResultType getTestResult() {
		return testResult;
	}

	public void setTestResult(PathogenTestResultType testResult) {
		this.testResult = testResult;
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
}
