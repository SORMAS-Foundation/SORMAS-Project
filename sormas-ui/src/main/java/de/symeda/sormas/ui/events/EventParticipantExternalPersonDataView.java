package de.symeda.sormas.ui.events;

import com.vaadin.ui.UI;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.ui.person.ExternalPersonDataComponent;

public class EventParticipantExternalPersonDataView extends AbstractEventParticipantView {

	private static final long serialVersionUID = 1L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/externalPersonData";

	public EventParticipantExternalPersonDataView() {
		super(VIEW_NAME);
	}

	@Override
	protected void initView(String params) {
		if (!ExternalPersonDataComponent.isAvailable()) {
			UI.getCurrent().getNavigator().navigateTo(EventParticipantDataView.VIEW_NAME + "/" + getReference().getUuid());
			return;
		}

		setSubComponent(
			new ExternalPersonDataComponent(
				FacadeProvider.getEventParticipantFacade().getEventParticipantByUuid(getReference().getUuid()).getPerson().toReference()));
	}
}
