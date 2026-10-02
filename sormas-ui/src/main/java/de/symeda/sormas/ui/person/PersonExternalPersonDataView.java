package de.symeda.sormas.ui.person;

import com.vaadin.ui.UI;

public class PersonExternalPersonDataView extends AbstractPersonView {

	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = PersonsView.VIEW_NAME + "/externalPersonData";

	public PersonExternalPersonDataView() {
		super(VIEW_NAME);
	}

	@Override
	protected void initView(String params) {
		if (!ExternalPersonDataComponent.isAvailable()) {
			UI.getCurrent().getNavigator().navigateTo(PersonDataView.VIEW_NAME + "/" + getReference().getUuid());
			return;
		}

		setSubComponent(new ExternalPersonDataComponent(getReference()));
	}
}
