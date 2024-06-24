package de.symeda.sormas.ui.events;

import com.vaadin.ui.CustomLayout;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.person.PersonContext;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.person.PersonEditForm;
import de.symeda.sormas.ui.person.PersonSideComponentsElement;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.DetailSubComponentWrapper;

public class EventParticipantPersonView extends AbstractEventParticipantView implements PersonSideComponentsElement {

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/person";

	private PersonDto person;

	public EventParticipantPersonView() {
		super(VIEW_NAME);
	}

	@Override
	protected void initView(String params) {

		EventParticipantDto eventParticipant =
			FacadeProvider.getEventParticipantFacade().getEventParticipantByUuid(getEventParticipantRef().getUuid());
		EventDto event = FacadeProvider.getEventFacade().getEventByUuid(eventParticipant.getEvent().getUuid(), false);
		person = FacadeProvider.getPersonFacade().getByUuid(eventParticipant.getPerson().getUuid());
		CommitDiscardWrapperComponent<PersonEditForm> editComponent = ControllerProvider.getPersonController()
			.getPersonEditComponent(
				PersonContext.CASE,
				person,
				event.getDisease(),
				event.getDiseaseDetails(),
				UserRight.EVENTPARTICIPANT_EDIT,
				null,
				isEditAllowed());
		DetailSubComponentWrapper componentWrapper = addComponentWrapper(editComponent);
		CustomLayout layout = addPageLayout(componentWrapper, editComponent);
		setSubComponent(componentWrapper);
		addSideComponents(
			layout,
			DeletableEntityType.EVENT_PARTICIPANT,
			eventParticipant.getUuid(),
			person.toReference(),
			this::showUnsavedChangesPopup,
			isEditAllowed());
		setEditPermission(editComponent, UiUtil.permitted(UserRight.PERSON_EDIT), PersonDto.ADDRESSES, PersonDto.PERSON_CONTACT_DETAILS);
	}

	@Override
	protected boolean isEditAllowed() {
		return FacadeProvider.getPersonFacade().isEditAllowed(person.getUuid());
	}
}
