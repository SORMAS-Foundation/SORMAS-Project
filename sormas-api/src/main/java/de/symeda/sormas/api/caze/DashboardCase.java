package de.symeda.sormas.api.caze;

import java.io.Serializable;

import de.symeda.sormas.api.Disease;

public class DashboardCase implements Serializable {

	private static final long serialVersionUID = -5705128377788207648L;

	public static final String I18N_PREFIX = "CaseData";
	
	public static final String CASE_CLASSIFICATION = "caseClassification";
	public static final String DISEASE = "disease";
	public static final String INVESTIGATION_STATUS = "investigationStatus";
	
	private CaseClassification caseClassification;
	private Disease disease;
	private InvestigationStatus investigationStatus;
	
	public DashboardCase(CaseClassification caseClassification, Disease disease, InvestigationStatus investigationStatus) {
		this.caseClassification = caseClassification;
		this.disease = disease;
		this.investigationStatus = investigationStatus;
	}
	
	public CaseClassification getCaseClassification() {
		return caseClassification;
	}
	public void setCaseClassification(CaseClassification caseClassification) {
		this.caseClassification = caseClassification;
	}
	public Disease getDisease() {
		return disease;
	}
	public void setDisease(Disease disease) {
		this.disease = disease;
	}
	public InvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}
	public void setInvestigationStatus(InvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
	}
	
}
