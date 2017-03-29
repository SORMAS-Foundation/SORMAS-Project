package de.symeda.sormas.ui.events;

import java.util.List;
import java.util.function.Consumer;

import com.vaadin.navigator.View;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantFacade;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class EventParticipantsController {
	
	private EventParticipantFacade eventParticipantFacade = FacadeProvider.getEventParticipantFacade();
	private PersonFacade personFacade = FacadeProvider.getPersonFacade();
	
	public void createEventParticipant(EventReferenceDto eventRef, Consumer<EventParticipantReferenceDto> doneConsumer) {
		EventParticipantDto eventParticipant = createNewEventParticipant(eventRef);
		EventParticipantCreateForm createForm = new EventParticipantCreateForm();
		createForm.setValue(eventParticipant);
		final CommitDiscardWrapperComponent<EventParticipantCreateForm> createComponent = new CommitDiscardWrapperComponent<EventParticipantCreateForm>(createForm, createForm.getFieldGroup());
		
		createComponent.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (createForm.getFieldGroup().isValid()) {
					final EventParticipantDto dto = createForm.getValue();
					
					ControllerProvider.getPersonController().selectOrCreatePerson(
							createForm.getPersonFirstName(), createForm.getPersonLastName(),
							person -> {
								if (person != null) {
									dto.setPerson(FacadeProvider.getPersonFacade().getPersonByUuid(person.getUuid()));
									eventParticipantFacade.saveEventParticipant(dto);
									Notification.show("New alert person created", Type.ASSISTIVE_NOTIFICATION);
				        			ControllerProvider.getEventParticipantController().editEventParticipant(eventParticipant);
								}
							});
				}
			}
		});
		
		VaadinUiUtil.showModalPopupWindow(createComponent, "Create new alert person");
	}
	
	public void editEventParticipant(EventParticipantDto eventParticipant) {
		EventParticipantEditForm editForm = new EventParticipantEditForm(FacadeProvider.getEventFacade().getEventByUuid(eventParticipant.getEvent().getUuid()));
		editForm.setValue(eventParticipant);
		final CommitDiscardWrapperComponent<EventParticipantEditForm> editView = new CommitDiscardWrapperComponent<EventParticipantEditForm>(editForm, editForm.getFieldGroup());

		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if(editForm.getFieldGroup().isValid()) {
					EventParticipantDto dto = editForm.getValue();
					personFacade.savePerson(dto.getPerson());
					dto = eventParticipantFacade.saveEventParticipant(dto);
					Notification.show("Alert person data saved", Type.WARNING_MESSAGE);
					refreshView();
				}
			}
		});
		
		Window window = VaadinUiUtil.showModalPopupWindow(editView, "Edit alert person");
        // visit form is too big for typical screens
		window.setWidth(editForm.getWidth() + 40, Unit.PIXELS); 
		window.setHeight(80, Unit.PERCENTAGE); 
	}
	
	public EventParticipantDto createNewEventParticipant(EventReferenceDto eventRef) {
		EventDto event = FacadeProvider.getEventFacade().getEventByUuid(eventRef.getUuid());
		EventParticipantDto eventParticipant = new EventParticipantDto();
		eventParticipant.setUuid(DataHelper.createUuid());
		eventParticipant.setEvent(event);
		return eventParticipant;
	}
	
	public List<EventParticipantDto> getEventParticipantIndexListByEvent(EventReferenceDto eventRef) {
		UserDto user = LoginHelper.getCurrentUser();
		return FacadeProvider.getEventParticipantFacade().getAllEventParticipantsByEventAfter(null, eventRef.getUuid(), user.getUuid());
	}
	
	private void refreshView() {
		View currentView = SormasUI.get().getNavigator().getCurrentView();
    	if (currentView instanceof EventParticipantsView) {
    		// force refresh, because view didn't change
    		((EventParticipantsView)currentView).enter(null);
    	}
	}

}
