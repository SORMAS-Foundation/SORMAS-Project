package de.symeda.sormas.api.caze;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.person.PresentCondition;

public class DashboardCaseDto implements Serializable {

	private static final long serialVersionUID = -5705128377788207648L;

	public static final String I18N_PREFIX = "CaseData";
	
	private Date reportDate;	
	private Date onsetDate;
	private Date receptionDate;
	private CaseClassification caseClassification;
	private Disease disease;
	private InvestigationStatus investigationStatus;
	private PresentCondition casePersonCondition;
	private Disease causeOfDeathDisease;
	
	public DashboardCaseDto(Date reportDate, Date onsetDate, Date receptionDate, CaseClassification caseClassification, Disease disease, 
			InvestigationStatus investigationStatus, PresentCondition casePersonCondition, Disease causeOfDeathDisease) {
		this.reportDate = reportDate;
		this.onsetDate = onsetDate;
		this.receptionDate = receptionDate;
		this.caseClassification = caseClassification;
		this.disease = disease;
		this.investigationStatus = investigationStatus;
		this.casePersonCondition = casePersonCondition;
		this.causeOfDeathDisease = causeOfDeathDisease;
	}
	
	public Date getReportDate() {
		return reportDate;
	}
	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}
	public Date getOnsetDate() {
		return onsetDate;
	}
	public void setOnsetDate(Date onsetDate) {
		this.onsetDate = onsetDate;
	}
	public Date getReceptionDate() {
		return receptionDate;
	}
	public void setReceptionDate(Date receptionDate) {
		this.receptionDate = receptionDate;
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
	public PresentCondition getCasePersonCondition() {
		return casePersonCondition;
	}
	public void setCasePersonCondition(PresentCondition casePersonCondition) {
		this.casePersonCondition = casePersonCondition;
	}
	public Disease getCauseOfDeathDisease() {
		return causeOfDeathDisease;
	}
	public void setCauseOfDeathDisease(Disease causeOfDeathDisease) {
		this.causeOfDeathDisease = causeOfDeathDisease;
	}
	
}
