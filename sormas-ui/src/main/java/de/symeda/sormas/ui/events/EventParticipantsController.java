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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.events;

import java.util.Collection;
import java.util.function.Consumer;

import com.vaadin.navigator.View;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantFacade;
import de.symeda.sormas.api.event.EventParticipantIndexDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class EventParticipantsController {

	private EventParticipantFacade eventParticipantFacade = FacadeProvider.getEventParticipantFacade();
	private PersonFacade personFacade = FacadeProvider.getPersonFacade();

	public void createEventParticipant(EventReferenceDto eventRef, Consumer<EventParticipantReferenceDto> doneConsumer) {
		EventParticipantDto eventParticipant = EventParticipantDto.build(eventRef);
		EventParticipantCreateForm createForm = new EventParticipantCreateForm();
		createForm.setValue(eventParticipant);
		final CommitDiscardWrapperComponent<EventParticipantCreateForm> createComponent =
			new CommitDiscardWrapperComponent<EventParticipantCreateForm>(
				createForm,
				UserProvider.getCurrent().hasUserRight(UserRight.EVENTPARTICIPANT_CREATE),
				createForm.getFieldGroup());

		createComponent.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				if (!createForm.getFieldGroup().isModified()) {
					final EventParticipantDto dto = createForm.getValue();
					final PersonDto person = PersonDto.build();
					person.setFirstName(createForm.getPersonFirstName());
					person.setLastName(createForm.getPersonLastName());

					ControllerProvider.getPersonController()
						.selectOrCreatePerson(
							person,
							I18nProperties.getString(Strings.infoSelectOrCreatePersonForEventParticipant),
							selectedPerson -> {
								if (selectedPerson != null) {
									dto.setPerson(FacadeProvider.getPersonFacade().getPersonByUuid(selectedPerson.getUuid()));
									EventParticipantDto savedDto = eventParticipantFacade.saveEventParticipant(dto);
									Notification.show(I18nProperties.getString(Strings.messageEventParticipantCreated), Type.ASSISTIVE_NOTIFICATION);
									ControllerProvider.getEventParticipantController().editEventParticipant(savedDto.getUuid(), doneConsumer);
								}
							});
				}
			}
		});

		Window window = VaadinUiUtil.showModalPopupWindow(createComponent, I18nProperties.getString(Strings.headingCreateNewEventParticipant));
		window.addCloseListener(e -> {
			doneConsumer.accept(null);
		});
	}

	public void editEventParticipant(String eventParticipantUuid) {
		editEventParticipant(eventParticipantUuid, null);
	}

	public void editEventParticipant(String eventParticipantUuid, Consumer<EventParticipantReferenceDto> doneConsumer) {

		EventParticipantDto eventParticipant = FacadeProvider.getEventParticipantFacade().getEventParticipantByUuid(eventParticipantUuid);
		EventParticipantEditForm editForm =
			new EventParticipantEditForm(FacadeProvider.getEventFacade().getEventByUuid(eventParticipant.getEvent().getUuid()));
		editForm.setValue(eventParticipant);
		final CommitDiscardWrapperComponent<EventParticipantEditForm> editView = new CommitDiscardWrapperComponent<EventParticipantEditForm>(
			editForm,
			UserProvider.getCurrent().hasUserRight(UserRight.EVENTPARTICIPANT_EDIT),
			editForm.getFieldGroup());

		Window window = VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingEditEventParticipant));
		// form is too big for typical screens
		window.setHeight(80, Unit.PERCENTAGE);

		editView.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				if (!editForm.getFieldGroup().isModified()) {
					EventParticipantDto dto = editForm.getValue();
					personFacade.savePerson(dto.getPerson());
					dto = eventParticipantFacade.saveEventParticipant(dto);
					Notification.show(I18nProperties.getString(Strings.messageEventParticipantSaved), Type.WARNING_MESSAGE);
					if (doneConsumer != null)
						doneConsumer.accept(null);
					refreshView();
				}
			}
		});

		if (UserProvider.getCurrent().hasUserRight(UserRight.EVENTPARTICIPANT_DELETE)) {
			editView.addDeleteListener(() -> {
				FacadeProvider.getEventParticipantFacade().deleteEventParticipant(editForm.getValue().toReference());
				UI.getCurrent().removeWindow(window);
				refreshView();
			}, I18nProperties.getCaption(EventParticipantDto.I18N_PREFIX));
		}
	}

	private void refreshView() {
		View currentView = SormasUI.get().getNavigator().getCurrentView();
		if (currentView instanceof EventParticipantsView) {
			// force refresh, because view didn't change
			((EventParticipantsView) currentView).enter(null);
		}
	}

	public void deleteAllSelectedItems(Collection<EventParticipantIndexDto> selectedRows, Runnable callback) {
		if (selectedRows.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoEventParticipantsSelected),
				I18nProperties.getString(Strings.messageNoEventParticipantsSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showDeleteConfirmationWindow(
				String.format(I18nProperties.getString(Strings.confirmationDeleteEventParticipants), selectedRows.size()),
				() -> {
					for (Object selectedRow : selectedRows) {
						FacadeProvider.getEventParticipantFacade()
							.deleteEventParticipant(new EventParticipantReferenceDto(((EventParticipantDto) selectedRow).getUuid()));
					}
					callback.run();
					new Notification(
						I18nProperties.getString(Strings.headingEventParticipantsDeleted),
						I18nProperties.getString(Strings.messageEventParticipantsDeleted),
						Type.HUMANIZED_MESSAGE,
						false).show(Page.getCurrent());
				});
		}
	}
}
