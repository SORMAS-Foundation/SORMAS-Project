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
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.login.LoginHelper;
import de.symeda.sormas.ui.person.PersonSelectField;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class EventParticipantsController {
	
	private EventParticipantFacade eventParticipantFacade = FacadeProvider.getEventParticipantFacade();
	private PersonFacade personFacade = FacadeProvider.getPersonFacade();
	
	public void createEventParticipant(EventReferenceDto eventRef, Consumer<EventParticipantReferenceDto> doneConsumer) {
		EventParticipantDto eventParticipant = createNewEventParticipant(eventRef);
		selectOrCreateEventPerson(eventParticipant, "", "",
				person -> {
					eventParticipant.setPerson(FacadeProvider.getPersonFacade().getPersonByUuid(person.getUuid()));
					
					eventParticipantFacade.saveEventParticipant(eventParticipant);
					Notification.show("New event person created", Type.WARNING_MESSAGE);
					refreshView();
				}
		);
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
					Notification.show("Event person data saved", Type.WARNING_MESSAGE);
					refreshView();
				}
			}
		});
		
		Window window = VaadinUiUtil.showModalPopupWindow(editView, "Edit event person");
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
	
	private void selectOrCreateEventPerson(EventParticipantDto eventParticipant, String firstName, String lastName, Consumer<PersonReferenceDto> resultConsumer) {
    	PersonSelectField personSelect = new PersonSelectField();
    	personSelect.setFirstName(firstName);
    	personSelect.setLastName(lastName);
    	personSelect.setWidth(640, Unit.PIXELS);

    	if (personSelect.hasMatches()) {
    		personSelect.selectBestMatch();
	    	final CommitDiscardWrapperComponent<PersonSelectField> selectOrCreateComponent = 
	    			new CommitDiscardWrapperComponent<PersonSelectField>(personSelect, null);
	    	
	    	selectOrCreateComponent.addCommitListener(new CommitListener() {
	        	@Override
	        	public void onCommit() {
	        		PersonReferenceDto person = personSelect.getValue();
	        		if (person != null) {
	        			if (resultConsumer != null) {
	        				resultConsumer.accept(person);
		        			eventParticipant.setPerson(personFacade.getPersonByUuid(person.getUuid()));
		        			ControllerProvider.getEventParticipantController().editEventParticipant(eventParticipant);
	        			}
	        		} else {	
	        			PersonDto personDto = new PersonDto();
	        			personDto.setUuid(DataHelper.createUuid());
	        			personDto.setFirstName(personSelect.getFirstName());
	        			personDto.setLastName(personSelect.getLastName());
	        			// Workaround to avoid binding error
	        			personDto.setAddress(new LocationDto());
	        			eventParticipant.setPerson(personDto);
	        			ControllerProvider.getEventParticipantController().editEventParticipant(eventParticipant);
	        		}
	        	}
	        });
        
	    	VaadinUiUtil.showModalPopupWindow(selectOrCreateComponent, "Pick or create person");
    	} else {
    		PersonDto personDto = new PersonDto();
			personDto.setUuid(DataHelper.createUuid());
			personDto.setFirstName(personSelect.getFirstName());
			personDto.setLastName(personSelect.getLastName());
			// Workaround to avoid binding error
			personDto.setAddress(new LocationDto());
			eventParticipant.setPerson(personDto);
    		ControllerProvider.getEventParticipantController().editEventParticipant(eventParticipant);
    	}
    }

}
