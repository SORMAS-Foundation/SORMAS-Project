package de.symeda.sormas.api.followup;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.EmbeddedPersonalData;
import de.symeda.sormas.api.visit.VisitResult;

public abstract class FollowUpDto implements Serializable {

	private static final long serialVersionUID = 8562530147842271464L;

	public static final String I18N_PREFIX = "FollowUp";
	public static final String UUID = "uuid";
	public static final String PERSON = "person";
	public static final String REPORT_DATE = "reportDate";
	public static final String FOLLOW_UP_UNTIL = "followUpUntil";

	private String uuid;
	@EmbeddedPersonalData
	private PersonReferenceDto person;
	private Date reportDate;
	private Date followUpUntil;
	private Disease disease;
	private VisitResult[] visitResults;

	//@formatter:off
	protected FollowUpDto(String uuid, String personUuid, String personFirstName, String personLastName,
			Date reportDate, Date followUpUntil, Disease disease) {
	//@formatter:on
		this.uuid = uuid;
		this.person = new PersonReferenceDto(personUuid, personFirstName, personLastName);
		this.reportDate = reportDate;
		this.followUpUntil = followUpUntil;
		this.disease = disease;
	}

	public void initVisitSize(int i) {
		visitResults = new VisitResult[i];
		Arrays.fill(visitResults, VisitResult.NOT_PERFORMED);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public PersonReferenceDto getPerson() {
		return person;
	}

	public void setPerson(PersonReferenceDto person) {
		this.person = person;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public Date getFollowUpUntil() {
		return followUpUntil;
	}

	public void setFollowUpUntil(Date followUpUntil) {
		this.followUpUntil = followUpUntil;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public VisitResult[] getVisitResults() {
		return visitResults;
	}

	public void setVisitResults(VisitResult[] visitResults) {
		this.visitResults = visitResults;
	}
}
