package de.symeda.sormas.ui.caze;

public enum CasesViewType {

	DEFAULT(true),
	DETAILED(true),
	FOLLOW_UP_VISITS_OVERVIEW(false);

	private boolean isCaseOverview;

	CasesViewType(boolean isCaseOverview) {
		this.isCaseOverview = isCaseOverview;
	}

	public boolean isCaseOverview() {
		return isCaseOverview;
	}
}
