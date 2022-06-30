package de.symeda.sormas.api.followup;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.VisitOrigin;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;
import de.symeda.sormas.api.visit.VisitResult;
import de.symeda.sormas.api.visit.VisitResultDto;

public abstract class FollowUpDto extends PseudonymizableIndexDto implements Serializable {

	private static final long serialVersionUID = 8562530147842271464L;

	public static final String I18N_PREFIX = "FollowUp";
	public static final String UUID = "uuid";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String REPORT_DATE = "reportDate";
	public static final String FOLLOW_UP_UNTIL = "followUpUntil";
	public static final String SYMPTOM_JOURNAL_STATUS = "symptomJournalStatus";

	@PersonalData
	private String firstName;
	@PersonalData
	private String lastName;
	private Date reportDate;
	private Date followUpUntil;
	private Disease disease;
	private VisitResultDto[] visitResults;

	protected FollowUpDto(String uuid, String personFirstName, String personLastName, Date reportDate, Date followUpUntil, Disease disease) {
		super(uuid);
		this.firstName = personFirstName;
		this.lastName = personLastName;
		this.reportDate = reportDate;
		this.followUpUntil = followUpUntil;
		this.disease = disease;
	}

	public void initVisitSize(int i) {
		visitResults = new VisitResultDto[i];
		Arrays.fill(visitResults, new VisitResultDto(VisitOrigin.USER, VisitResult.NOT_PERFORMED));
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

	public VisitResultDto[] getVisitResults() {
		return visitResults;
	}

	public void setVisitResults(VisitResultDto[] visitResults) {
		this.visitResults = visitResults;
	}
}
