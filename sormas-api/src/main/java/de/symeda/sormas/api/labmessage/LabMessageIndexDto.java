package de.symeda.sormas.api.labmessage;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestResultType;

public class LabMessageIndexDto implements Serializable {

	public static final String I18N_PREFIX = "LabMessage";

	public static final String UUID = "uuid";
	public static final String MESSAGE_DATE_TIME = "messageDateTime";
	public static final String TEST_LAB_NAME = "labName";
	public static final String TEST_LAB_POSTAL_CODE = "labPostalCode";
	public static final String TESTED_DISEASE = "testedDisease";
	public static final String PERSON_FIRST_NAME = "personFirstName";
	public static final String PERSON_LAST_NAME = "personLastName";
	public static final String PERSON_POSTAL_CODE = "personPostalCode";
	public static final String STATUS = "status";

	private String uuid;

	private Date messageDateTime;
	private String labName;
	private String labPostalCode;
	private Disease testedDisease;
	private String personFirstName;
	private String personLastName;
	private String personPostalCode;
	private LabMessageStatus status;

	public LabMessageIndexDto(
		String uuid,
		Date messageDateTime,
		String labName,
		String labPostalCode,
		Disease testedDisease,
		String personFirstName,
		String personLastName,
		String personPostalCode,
		LabMessageStatus status) {

		this.uuid = uuid;
		this.messageDateTime = messageDateTime;
		this.labName = labName;
		this.labPostalCode = labPostalCode;
		this.testedDisease = testedDisease;
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

	public String getlabName() {
		return labName;
	}

	public void setTestLabName(String labName) {
		this.labName = labName;
	}

	public String getlabPostalCode() {
		return labPostalCode;
	}

	public void setlabPostalCode(String testLabPostalCode) {
		this.labPostalCode = testLabPostalCode;
	}

	public Disease getTestedDisease() {
		return testedDisease;
	}

	public void setTestedDisease(Disease testedDisease) {
		this.testedDisease = testedDisease;
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
