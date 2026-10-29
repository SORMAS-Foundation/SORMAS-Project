package de.symeda.sormas.ui.externalpersondata;

import com.vaadin.ui.UI;
import de.symeda.sormas.ui.person.AbstractPersonView;
import de.symeda.sormas.ui.person.PersonDataView;
import de.symeda.sormas.ui.person.PersonsView;

public class ExternalPersonDataView extends AbstractPersonView {

	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = PersonsView.VIEW_NAME + "/externalPersonData";

	public ExternalPersonDataView() {
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
