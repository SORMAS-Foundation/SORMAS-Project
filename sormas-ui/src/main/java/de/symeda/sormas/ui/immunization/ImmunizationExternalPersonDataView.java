package de.symeda.sormas.ui.immunization;

import com.vaadin.ui.UI;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.ui.person.ExternalPersonDataComponent;

public class ImmunizationExternalPersonDataView extends AbstractImmunizationView {

	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/externalPersonData";

	public ImmunizationExternalPersonDataView() {
		super(VIEW_NAME);
	}

	@Override
	protected void initView(String params) {
		if (!ExternalPersonDataComponent.isAvailable()) {
			UI.getCurrent().getNavigator().navigateTo(ImmunizationDataView.VIEW_NAME + "/" + getReference().getUuid());
			return;
		}

		setSubComponent(new ExternalPersonDataComponent(FacadeProvider.getImmunizationFacade().getByUuid(getReference().getUuid()).getPerson()));
	}
}
