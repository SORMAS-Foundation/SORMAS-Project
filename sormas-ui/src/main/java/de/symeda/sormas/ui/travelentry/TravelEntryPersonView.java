package de.symeda.sormas.ui.travelentry;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.person.PersonContext;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.person.PersonEditForm;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;

public class TravelEntryPersonView extends AbstractTravelEntryView {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/person";

	public TravelEntryPersonView() {
		super(VIEW_NAME);
	}

	@Override
	protected void initView(String params) {

		TravelEntryDto dto = FacadeProvider.getTravelEntryFacade().getByUuid(getReference().getUuid());

		CommitDiscardWrapperComponent<PersonEditForm> travelEntryPersonComponent = ControllerProvider.getPersonController()
			.getPersonEditComponent(
				PersonContext.TRAVEL_ENTRY,
				dto.getPerson().getUuid(),
				dto.getDisease(),
				dto.getDiseaseDetails(),
				UserRight.TRAVEL_ENTRY_EDIT,
				null);
		setSubComponent(travelEntryPersonComponent);

		setTravelEntryEditPermission(travelEntryPersonComponent);
	}

}
