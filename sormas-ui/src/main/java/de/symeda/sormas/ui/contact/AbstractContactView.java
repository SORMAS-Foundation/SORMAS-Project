package de.symeda.sormas.ui.contact;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.ui.SubNavigationMenu;
import de.symeda.sormas.ui.caze.CaseContactsView;
import de.symeda.sormas.ui.utils.AbstractSubNavigationView;

@SuppressWarnings("serial")
public abstract class AbstractContactView extends AbstractSubNavigationView {

	private ContactReferenceDto contactRef;

	protected AbstractContactView(String viewName) {
		super(viewName);
	}

	@Override
	public void refreshMenu(SubNavigationMenu menu, String params) {
		
		ContactDto contact = FacadeProvider.getContactFacade().getContactByUuid(params);
		contactRef = contact;		
		
		menu.removeAllViews();
		menu.addView(ContactsView.VIEW_NAME, "Contacts List");
		menu.addView(CaseContactsView.VIEW_NAME, "Case Contacts", contact.getCaze().getUuid(), true);
		menu.addView(ContactDataView.VIEW_NAME, "Contact Data", params);
		menu.addView(ContactPersonView.VIEW_NAME, "Person Data", params);
    }

	public ContactReferenceDto getContactRef() {
		return contactRef;
	}
}
