package de.symeda.sormas.ui.contact;

public enum ContactsViewType {

	CONTACTS_OVERVIEW(true),
	DETAILED_OVERVIEW(true),
	FOLLOW_UP_VISITS_OVERVIEW(false);

	private boolean isContactOverview;

	ContactsViewType(boolean isContactOverview) {
		this.isContactOverview = isContactOverview;
	}

	public boolean isContactOverview() {
		return isContactOverview;
	}
}
