package de.symeda.sormas.api.selfreport;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class SelfReportListEntryDto extends PseudonymizableIndexDto implements Serializable {

	public static final String I18N_PREFIX = "SelfReport";

	private Date reportingDate;
	private String caseReference;
	private Disease disease;
	private Date dateOfTest;

	public SelfReportListEntryDto(String uuid, Date reportingDate, String caseReference, Disease disease, Date dateOfTest) {
		super(uuid);
		this.reportingDate = reportingDate;
		this.caseReference = caseReference;
		this.disease = disease;
		this.dateOfTest = dateOfTest;
	}

	public Date getReportingDate() {
		return reportingDate;
	}

	public void setReportingDate(Date reportingDate) {
		this.reportingDate = reportingDate;
	}

	public String getCaseReference() {
		return caseReference;
	}

	public void setCaseReference(String caseReference) {
		this.caseReference = caseReference;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public Date getDateOfTest() {
		return dateOfTest;
	}

	public void setDateOfTest(Date dateOfTest) {
		this.dateOfTest = dateOfTest;
	}
}
