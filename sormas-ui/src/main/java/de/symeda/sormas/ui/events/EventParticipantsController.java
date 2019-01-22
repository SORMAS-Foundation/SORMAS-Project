/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.events;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import com.vaadin.navigator.View;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
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
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.SormasUI;
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
        // form is too big for typical screens
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
		
		if (UserProvider.getCurrent().hasUserRole(UserRole.ADMIN)) {
			editView.addDeleteListener(new DeleteListener() {
				@Override
				public void onDelete() {
					FacadeProvider.getEventParticipantFacade().deleteEventParticipant(editForm.getValue().toReference(), UserProvider.getCurrent().getUserReference().getUuid());
					UI.getCurrent().removeWindow(window);
					refreshView();
				}
			}, I18nProperties.getCaption("EventParticipant"));
		}
	}
	
	public EventParticipantDto createNewEventParticipant(EventReferenceDto eventRef) {
		EventParticipantDto eventParticipant = new EventParticipantDto();
		eventParticipant.setUuid(DataHelper.createUuid());
		eventParticipant.setEvent(eventRef);
		return eventParticipant;
	}
	
	public List<EventParticipantDto> getEventParticipantIndexListByEvent(EventReferenceDto eventRef) {
		UserDto user = UserProvider.getCurrent().getUser();
		return FacadeProvider.getEventParticipantFacade().getAllEventParticipantsByEventAfter(null, eventRef.getUuid(), user.getUuid());
	}
	
	private void refreshView() {
		View currentView = SormasUI.get().getNavigator().getCurrentView();
    	if (currentView instanceof EventParticipantsView) {
    		// force refresh, because view didn't change
    		((EventParticipantsView)currentView).enter(null);
    	}
	}

	public void deleteAllSelectedItems(Collection<Object> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification("No event participants selected", "You have not selected any event participants.", Type.WARNING_MESSAGE, false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showDeleteConfirmationWindow("Are you sure you want to delete all " + selectedRows.size() + " selected event participants?", new Runnable() {
				public void run() {
					for (Object selectedRow : selectedRows) {
						FacadeProvider.getEventParticipantFacade().deleteEventParticipant(new EventParticipantReferenceDto(((EventParticipantDto) selectedRow).getUuid()), UserProvider.getCurrent().getUuid());
					}
					callback.run();
					new Notification("Event participants deleted", "All selected event participants have been deleted.", Type.HUMANIZED_MESSAGE, false).show(Page.getCurrent());
				}
			});
		}
	}

}
