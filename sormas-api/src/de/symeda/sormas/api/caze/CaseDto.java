package de.symeda.sormas.api.caze;

public class CaseDto extends DataTransferObject {

	public static final String CASE_STATUS = "caseStatus";
	public static final String DESCRIPTION = "description";
	
	private CaseStatus caseStatus;
	private String description;
	
	public CaseStatus getCaseStatus() {
		return caseStatus;
	}

	public void setCaseStatus(CaseStatus caseStatus) {
		this.caseStatus = caseStatus;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
}
