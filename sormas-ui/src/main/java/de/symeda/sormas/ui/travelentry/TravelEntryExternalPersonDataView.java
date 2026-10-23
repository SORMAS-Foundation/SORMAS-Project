package de.symeda.sormas.ui.travelentry;

import com.vaadin.ui.UI;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.ui.person.ExternalPersonDataComponent;

public class TravelEntryExternalPersonDataView extends AbstractTravelEntryView {

	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/externalPersonData";

	public TravelEntryExternalPersonDataView() {
		super(VIEW_NAME);
	}

	@Override
	protected void initView(String params) {
		if (!ExternalPersonDataComponent.isAvailable()) {
			UI.getCurrent().getNavigator().navigateTo(TravelEntryDataView.VIEW_NAME + "/" + getReference().getUuid());
			return;
		}

		setSubComponent(new ExternalPersonDataComponent(FacadeProvider.getTravelEntryFacade().getByUuid(getReference().getUuid()).getPerson()));
	}
}
