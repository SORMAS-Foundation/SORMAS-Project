/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.externalmessage;

import java.util.Calendar;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.uuid.AbstractUuidDto;

public class ExternalMessageIndexDto extends AbstractUuidDto {

	public static final String I18N_PREFIX = "ExternalMessage";

	public static final String MESSAGE_DATE_TIME = "messageDateTime";
	public static final String TYPE = "type";
	public static final String REPORTER_NAME = "reporterName";
	public static final String REPORTER_POSTAL_CODE = "reporterPostalCode";
	public static final String TESTED_DISEASE = "testedDisease";
	public static final String SAMPLE_OVERALL_TEST_RESULT = "sampleOverallTestResult";
	public static final String PERSON_FIRST_NAME = "personFirstName";
	public static final String PERSON_LAST_NAME = "personLastName";
	public static final String PERSON_BIRTH_DATE = "personBirthDate";
	public static final String PERSON_POSTAL_CODE = "personPostalCode";
	public static final String STATUS = "status";
	public static final String ASSIGNEE = "assignee";

	private ExternalMessageType type;
	private Date messageDateTime;
	private String reporterName;
	private String reporterPostalCode;
	private Disease testedDisease;
	private PathogenTestResultType sampleOverallTestResult;
	private String personFirstName;
	private String personLastName;
	private Date personBirthDate;
	private String personPostalCode;
	private ExternalMessageStatus status;
	private UserReferenceDto assignee;

	public ExternalMessageIndexDto(
		String uuid,
		ExternalMessageType type,
		Date messageDateTime,
		String reporterName,
		String reporterPostalCode,
		Disease testedDisease,
		PathogenTestResultType sampleOverallTestResult,
		String personFirstName,
		String personLastName,
		Integer personBirthDateYYYY,
		Integer personBirthDateMM,
		Integer personBirthDateDD,
		String personPostalCode,
		ExternalMessageStatus status,
		String assigneeUuid,
		String assigneeFirstName,
		String assigneeLastName) {

		super(uuid);
		this.type = type;
		this.messageDateTime = messageDateTime;
		this.reporterName = reporterName;
		this.reporterPostalCode = reporterPostalCode;
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

	public ExternalMessageType getType() {
		return type;
	}

	public void setType(ExternalMessageType type) {
		this.type = type;
	}

	public Date getMessageDateTime() {
		return messageDateTime;
	}

	public void setMessageDateTime(Date messageDateTime) {
		this.messageDateTime = messageDateTime;
	}

	public String getReporterName() {
		return reporterName;
	}

	public void setReporterName(String reporterName) {
		this.reporterName = reporterName;
	}

	public String getReporterPostalCode() {
		return reporterPostalCode;
	}

	public void setReporterPostalCode(String testLabPostalCode) {
		this.reporterPostalCode = testLabPostalCode;
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

	public ExternalMessageStatus getStatus() {
		return status;
	}

	public void setStatus(ExternalMessageStatus status) {
		this.status = status;
	}

	public UserReferenceDto getAssignee() {
		return assignee;
	}

	public void setAssignee(UserReferenceDto assignee) {
		this.assignee = assignee;
	}

}
