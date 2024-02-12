package de.symeda.sormas.api.caze;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;

public class CaseListEntryDto extends PseudonymizableIndexDto implements Serializable, Cloneable, ICase {

	private static final long serialVersionUID = -8812926682263746469L;
	private Date reportDate;
	private Disease disease;
	private CaseClassification caseClassification;
	private Date symptomsOnsetDate;

	private boolean isInJurisdiction;

	public CaseListEntryDto(
		String uuid,
		Date reportDate,
		Disease disease,
		CaseClassification caseClassification,
		Date symptomsOnsetDate,
		boolean isInJurisdiction) {
		super(uuid);
		this.reportDate = reportDate;
		this.disease = disease;
		this.caseClassification = caseClassification;
		this.symptomsOnsetDate = symptomsOnsetDate;
		this.isInJurisdiction = isInJurisdiction;
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

	public Date getSymptomsOnsetDate() {
		return symptomsOnsetDate;
	}

	public void setSymptomsOnsetDate(Date symptomsOnsetDate) {
		this.symptomsOnsetDate = symptomsOnsetDate;
	}

	@Override
	public boolean isInJurisdiction() {
		return isInJurisdiction;
	}

	@Override
	public void setInJurisdiction(boolean inJurisdiction) {
		isInJurisdiction = inJurisdiction;
	}
}
