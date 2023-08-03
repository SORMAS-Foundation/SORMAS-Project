package de.symeda.sormas.app.backend.environment;

import java.io.Serializable;

import de.symeda.sormas.api.caze.InvestigationStatus;

public class EnvironmentCriteria implements Serializable {

	private InvestigationStatus investigationStatus;

	public InvestigationStatus getInvestigationStatus() {
		return investigationStatus;
	}

	public void setInvestigationStatus(InvestigationStatus investigationStatus) {
		this.investigationStatus = investigationStatus;
	}
}
