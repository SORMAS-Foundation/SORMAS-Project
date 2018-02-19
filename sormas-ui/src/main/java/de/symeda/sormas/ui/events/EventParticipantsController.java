package de.symeda.sormas.ui.events;

import java.util.List;
import java.util.function.Consumer;

import com.vaadin.navigator.View;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantFacade;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.DeleteListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class EventParticipantsController {
	
	private EventParticipantFacade eventParticipantFacade = FacadeProvider.getEventParticipantFacade();
	private PersonFacade personFacade = FacadeProvider.getPersonFacade();
	
	public void createEventParticipant(EventReferenceDto eventRef, Consumer<EventParticipantReferenceDto> doneConsumer) {
		EventParticipantDto eventParticipant = createNewEventParticipant(eventRef);
		EventParticipantCreateForm createForm = new EventParticipantCreateForm(UserRight.EVENTPARTICIPANT_CREATE);
		createForm.setValue(eventParticipant);
		final CommitDiscardWrapperComponent<EventParticipantCreateForm> createComponent = new CommitDiscardWrapperComponent<EventParticipantCreateForm>(createForm, createForm.getFieldGroup());
		
		createComponent.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if (!createForm.getFieldGroup().isModified()) {
					final EventParticipantDto dto = createForm.getValue();
					
					ControllerProvider.getPersonController().selectOrCreatePerson(
							createForm.getPersonFirstName(), createForm.getPersonLastName(),
							person -> {
								if (person != null) {
									dto.setPerson(FacadeProvider.getPersonFacade().getPersonByUuid(person.getUuid()));
									EventParticipantDto savedDto = eventParticipantFacade.saveEventParticipant(dto);
									Notification.show("New person created", Type.ASSISTIVE_NOTIFICATION);
				        			ControllerProvider.getEventParticipantController().editEventParticipant(savedDto);
								}
							});
				}
			}
		});
		
		VaadinUiUtil.showModalPopupWindow(createComponent, "Create new person");
	}
	
	public void editEventParticipant(EventParticipantDto eventParticipant) {
		EventParticipantEditForm editForm = new EventParticipantEditForm(FacadeProvider.getEventFacade().getEventByUuid(eventParticipant.getEvent().getUuid()), UserRight.EVENTPARTICIPANT_EDIT);
		editForm.setValue(eventParticipant);
		final CommitDiscardWrapperComponent<EventParticipantEditForm> editView = new CommitDiscardWrapperComponent<EventParticipantEditForm>(editForm, editForm.getFieldGroup());

		Window window = VaadinUiUtil.showModalPopupWindow(editView, "Edit person");
        // visit form is too big for typical screens
		window.setWidth(editForm.getWidth() + 40, Unit.PIXELS); 
		window.setHeight(80, Unit.PERCENTAGE); 
		
		editView.addCommitListener(new CommitListener() {
			@Override
			public void onCommit() {
				if(!editForm.getFieldGroup().isModified()) {
					EventParticipantDto dto = editForm.getValue();
					personFacade.savePerson(dto.getPerson());
					dto = eventParticipantFacade.saveEventParticipant(dto);
					Notification.show("Person data saved", Type.WARNING_MESSAGE);
					refreshView();
				}
			}
		});
		
		if (LoginHelper.getCurrentUserRoles().contains(UserRole.ADMIN)) {
			editView.addDeleteListener(new DeleteListener() {
				@Override
				public void onDelete() {
					FacadeProvider.getEventParticipantFacade().deleteEventParticipant(editForm.getValue().toReference(), LoginHelper.getCurrentUserAsReference().getUuid());
					UI.getCurrent().removeWindow(window);
					refreshView();
				}
			}, I18nProperties.getFieldCaption("EventParticipant"));
		}
	}
	
	public EventParticipantDto createNewEventParticipant(EventReferenceDto eventRef) {
		EventParticipantDto eventParticipant = new EventParticipantDto();
		eventParticipant.setUuid(DataHelper.createUuid());
		eventParticipant.setEvent(eventRef);
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
