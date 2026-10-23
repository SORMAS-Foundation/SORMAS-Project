package de.symeda.sormas.ui.contact;

import com.vaadin.ui.UI;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.ui.person.ExternalPersonDataComponent;

public class ContactExternalPersonDataView extends AbstractContactView {

	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/externalPersonData";

	public ContactExternalPersonDataView() {
		super(VIEW_NAME);
	}

	@Override
	protected void initView(String params) {
		if (!ExternalPersonDataComponent.isAvailable()) {
			UI.getCurrent().getNavigator().navigateTo(ContactDataView.VIEW_NAME + "/" + getReference().getUuid());
			return;
		}

		setSubComponent(new ExternalPersonDataComponent(FacadeProvider.getContactFacade().getByUuid(getReference().getUuid()).getPerson()));
	}
}
