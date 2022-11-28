package de.symeda.sormas.ui.travelentry;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.person.PersonContext;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.person.PersonEditForm;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;

public class TravelEntryPersonView extends AbstractTravelEntryView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/person";

	private PersonDto person;

	public TravelEntryPersonView() {
		super(VIEW_NAME);
	}

	@Override
	protected void initView(String params) {

		TravelEntryDto travelEntry = FacadeProvider.getTravelEntryFacade().getByUuid(getReference().getUuid());
		person = FacadeProvider.getPersonFacade().getByUuid(travelEntry.getPerson().getUuid());

		CommitDiscardWrapperComponent<PersonEditForm> travelEntryPersonComponent = ControllerProvider.getPersonController()
			.getPersonEditComponent(
				PersonContext.TRAVEL_ENTRY,
				person,
				travelEntry.getDisease(),
				travelEntry.getDiseaseDetails(),
				UserRight.PERSON_EDIT,
				null);
		setSubComponent(travelEntryPersonComponent);

		setEditPermission(travelEntryPersonComponent);
	}

	@Override
	protected boolean isEditAllowed() {
		return FacadeProvider.getPersonFacade().isEditAllowed(person.getUuid());
	}

}
