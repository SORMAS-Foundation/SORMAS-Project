package de.symeda.sormas.api.dashboard;

import de.symeda.sormas.api.caze.NewCaseDateType;

public class CaseDataTypeDashboardCriteria extends DashboardCriteria {

	private NewCaseDateType newCaseDateType;

	@Override
	public NewCaseDateType getNewCaseDateType() {
		return newCaseDateType;
	}
}
