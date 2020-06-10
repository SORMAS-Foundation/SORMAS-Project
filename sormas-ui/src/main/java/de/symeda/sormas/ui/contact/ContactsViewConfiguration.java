package de.symeda.sormas.ui.contact;

import de.symeda.sormas.ui.utils.ViewConfiguration;
import de.symeda.sormas.ui.utils.ViewMode;

public class ContactsViewConfiguration extends ViewConfiguration {

	private ContactsViewType viewType;

	public ContactsViewConfiguration() {
		super();
	}

	public ContactsViewConfiguration(ViewMode viewMode, ContactsViewType viewType) {
		super(viewMode);
		this.viewType = viewType;
	}

	public ContactsViewType getViewType() {
		return viewType;
	}

	public void setViewType(ContactsViewType viewType) {
		this.viewType = viewType;
	}
}
