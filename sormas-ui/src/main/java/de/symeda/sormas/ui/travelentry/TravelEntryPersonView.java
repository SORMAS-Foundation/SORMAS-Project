package de.symeda.sormas.ui.travelentry;

import com.vaadin.ui.CustomLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.person.PersonContext;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.person.PersonEditForm;
import de.symeda.sormas.ui.person.PersonSideComponentsElement;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;

public class TravelEntryPersonView extends AbstractTravelEntryView implements PersonSideComponentsElement {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/person";

	private PersonDto person;

	public TravelEntryPersonView() {
		super(VIEW_NAME);
	}

	@Override
	protected void initView(String params) {

		TravelEntryDto travelEntry = FacadeProvider.getTravelEntryFacade().getByUuid(getReference().getUuid());
		person = FacadeProvider.getPersonFacade().getByUuid(travelEntry.getPerson().getUuid());
		CommitDiscardWrapperComponent<PersonEditForm> editComponent = ControllerProvider.getPersonController()
			.getPersonEditComponent(
				PersonContext.TRAVEL_ENTRY,
				person,
				travelEntry.getDisease(),
				travelEntry.getDiseaseDetails(),
				UserRight.PERSON_EDIT,
				null);
		DetailSubComponentWrapper componentWrapper = addComponentWrapper(editComponent);
		CustomLayout layout = addPageLayout(componentWrapper, editComponent);
		setSubComponent(componentWrapper);
		addSideComponents(
			layout,
			DeletableEntityType.TRAVEL_ENTRY,
			travelEntry.getUuid(),
			person.toReference(),
			this::showUnsavedChangesPopup,
			isEditAllowed());
		setEditPermission(editComponent);
	}

	@Override
	protected boolean isEditAllowed() {
		return FacadeProvider.getPersonFacade().isEditAllowed(person.getUuid());
	}

}
