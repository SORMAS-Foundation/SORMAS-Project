package de.symeda.sormas.api.caze;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class CaseListEntryDto extends PseudonymizableIndexDto implements Serializable, Cloneable {

	public static final String I18N_PREFIX = "CaseData";

	public static final String REPORT_DATE = "reportDate";

	private String uuid;
	private Date reportDate;
	private Disease disease;
	private CaseClassification caseClassification;

	private boolean isInJurisdiction;

	public CaseListEntryDto(String uuid, Date reportDate, Disease disease, CaseClassification caseClassification, boolean isInJurisdiction) {
		this.uuid = uuid;
		this.reportDate = reportDate;
		this.disease = disease;
		this.caseClassification = caseClassification;
		this.isInJurisdiction = isInJurisdiction;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public CaseClassification getCaseClassification() {
		return caseClassification;
	}

	public void setCaseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
	}

	public boolean isInJurisdiction() {
		return isInJurisdiction;
	}

	public void setInJurisdiction(boolean inJurisdiction) {
		isInJurisdiction = inJurisdiction;
	}
}
