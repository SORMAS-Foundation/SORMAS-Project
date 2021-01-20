package de.symeda.sormas.api.labmessage;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestResultType;

public class LabMessageIndexDto implements Serializable {

	public static final String I18N_PREFIX = "LabMessage";

	public static final String UUID = "uuid";
	public static final String MESSAGE_DATE_TIME = "messageDateTime";
	public static final String TESTED_DISEASE = "testedDisease";
	public static final String TEST_RESULT = "testResult";
	public static final String PERSON_FIRST_NAME = "personFirstName";
	public static final String PERSON_LAST_NAME = "personLastName";
	public static final String PROCESSED = "processed";

	private String uuid;
	private Date messageDateTime;
	private Disease testedDisease;
	private PathogenTestResultType testResult;
	private String personFirstName;
	private String personLastName;
	private boolean processed;

	public LabMessageIndexDto(
		String uuid,
		Date messageDateTime,
		Disease testedDisease,
		PathogenTestResultType testResult,
		String personFirstName,
		String personLastName,
		boolean processed) {
		this.uuid = uuid;
		this.messageDateTime = messageDateTime;
		this.testedDisease = testedDisease;
		this.testResult = testResult;
		this.personFirstName = personFirstName;
		this.personLastName = personLastName;
		this.processed = processed;
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

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}
}
