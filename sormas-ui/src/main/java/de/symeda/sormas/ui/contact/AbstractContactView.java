package de.symeda.sormas.ui.contact;

import de.symeda.sormas.ui.SubNavigationMenu;
import de.symeda.sormas.ui.utils.AbstractSubNavigationView;

@SuppressWarnings("serial")
public abstract class AbstractContactView extends AbstractSubNavigationView {

	protected AbstractContactView(String viewName) {
		super(viewName);
	}

	@Override
	public void refreshMenu(SubNavigationMenu menu, String entityUuid) {
		menu.removeAllViews();
		menu.addView(ContactsView.VIEW_NAME, "Contacts List");
		menu.addView(ContactDataView.VIEW_NAME, "Contact Data", entityUuid);
//		menu.addView(CasePersonView.VIEW_NAME, "Patient Information", entityUuid);
    }
}
