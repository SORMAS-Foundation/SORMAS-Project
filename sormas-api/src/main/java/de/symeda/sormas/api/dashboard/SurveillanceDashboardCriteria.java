package de.symeda.sormas.api.dashboard;

import de.symeda.sormas.api.caze.NewCaseDateType;

public class SurveillanceDashboardCriteria extends DashboardCriteria {

	private NewCaseDateType newCaseDateType;

    public SurveillanceDashboardCriteria() {
        super();
    }

    @Override
	public NewCaseDateType getNewCaseDateType() {
		return newCaseDateType;
	}
}
