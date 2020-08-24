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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.caze.eventLink.EventSelectionField;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class EventController {

	public void registerViews(Navigator navigator) {
		navigator.addView(EventsView.VIEW_NAME, EventsView.class);
		navigator.addView(EventDataView.VIEW_NAME, EventDataView.class);
		navigator.addView(EventParticipantsView.VIEW_NAME, EventParticipantsView.class);
		navigator.addView(EventActionsView.VIEW_NAME, EventActionsView.class);
	}

	public EventDto create(CaseReferenceDto caseRef) {
		CommitDiscardWrapperComponent<EventDataForm> eventCreateComponent = getEventCreateComponent(caseRef);
		EventDto eventDto = eventCreateComponent.getWrappedComponent().getValue();
		VaadinUiUtil.showModalPopupWindow(eventCreateComponent, I18nProperties.getString(Strings.headingCreateNewEvent));
		return eventDto;
	}

	public void selectOrCreateEvent(CaseReferenceDto caseRef) {

		CaseDataDto caseDataDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseRef.getUuid());

		EventSelectionField eventSelect = new EventSelectionField(caseDataDto);
		eventSelect.setWidth(1024, Sizeable.Unit.PIXELS);

		final CommitDiscardWrapperComponent<EventSelectionField> component = new CommitDiscardWrapperComponent<>(eventSelect);
		component.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				EventIndexDto selectedEvent = eventSelect.getValue();
				if (selectedEvent != null) {

					EventCriteria eventCriteria = new EventCriteria();
					eventCriteria.caze(caseRef);
					eventCriteria.setUserFilterIncluded(false);
					List<EventIndexDto> eventIndexDto = FacadeProvider.getEventFacade().getIndexList(eventCriteria, null, null, null);

					EventReferenceDto eventReferenceDto = new EventReferenceDto(selectedEvent.getUuid());
					if (!eventIndexDto.contains(selectedEvent)) {
						createEventParticipantWithCase(eventReferenceDto, caseDataDto, caseRef);
					}
				} else {
					create(caseRef);
				}
				SormasUI.refreshView();
			}
		});

		eventSelect.setSelectionChangeCallback((commitAllowed) -> {
			component.getCommitButton().setEnabled(commitAllowed);
		});

		VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreateEvent));
	}

	public void createEventParticipantWithCase(EventReferenceDto eventReferenceDto, CaseDataDto caseDataDto, CaseReferenceDto caseRef) {
		PersonDto personDto = FacadeProvider.getPersonFacade().getPersonByUuid(caseDataDto.getPerson().getUuid());
		EventParticipantDto eventParticipantDto;
		eventParticipantDto = new EventParticipantDto().buildFromCase(caseRef, personDto, eventReferenceDto);
		FacadeProvider.getEventParticipantFacade().saveEventParticipant(eventParticipantDto);
	}

	public void navigateToIndex() {
		String navigationState = EventsView.VIEW_NAME;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	public void navigateToData(String eventUuid) {
		navigateToData(eventUuid, false);
	}

	public void navigateToData(String eventUuid, boolean openTab) {

		String navigationState = EventDataView.VIEW_NAME + "/" + eventUuid;
		if (openTab) {
			SormasUI.get().getPage().open(SormasUI.get().getPage().getLocation().getRawPath() + "#!" + navigationState, "_blank", false);
		} else {
			SormasUI.get().getNavigator().navigateTo(navigationState);
		}
	}

	public void navigateToParticipants(String eventUuid) {
		String navigationState = EventParticipantsView.VIEW_NAME + "/" + eventUuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	public void setUriFragmentParameter(String eventUuid) {

		String fragmentParameter;
		if (eventUuid == null || eventUuid.isEmpty()) {
			fragmentParameter = "";
		} else {
			fragmentParameter = eventUuid;
		}

		Page page = SormasUI.get().getPage();
		page.setUriFragment("!" + EventsView.VIEW_NAME + "/" + fragmentParameter, false);
	}

	private EventDto findEvent(String uuid) {
		return FacadeProvider.getEventFacade().getEventByUuid(uuid);
	}

	public CommitDiscardWrapperComponent<EventDataForm> getEventCreateComponent(CaseReferenceDto caseRef) {

		CaseDataDto caseDataDto = null;

		if (caseRef != null) {
			caseDataDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseRef.getUuid());
		}

		EventDataForm eventCreateForm = new EventDataForm(true, true);
		if (caseRef != null) {
			eventCreateForm.setValue(createNewEvent(caseDataDto.getDisease()));
			eventCreateForm.getField(EventDto.DISEASE).setReadOnly(true);
		} else {
			eventCreateForm.setValue(createNewEvent(null));
		}
		final CommitDiscardWrapperComponent<EventDataForm> editView = new CommitDiscardWrapperComponent<EventDataForm>(
			eventCreateForm,
			UserProvider.getCurrent().hasUserRight(UserRight.EVENT_CREATE),
			eventCreateForm.getFieldGroup());

		CaseDataDto finalCaseDataDto = caseDataDto;
		editView.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				if (!eventCreateForm.getFieldGroup().isModified()) {
					EventDto dto = eventCreateForm.getValue();
					FacadeProvider.getEventFacade().saveEvent(dto);
					Notification.show(I18nProperties.getString(Strings.messageEventCreated), Type.WARNING_MESSAGE);

					if (caseRef != null) {
						EventReferenceDto createdEvent = new EventReferenceDto(dto.getUuid());

						createEventParticipantWithCase(createdEvent, finalCaseDataDto, caseRef);
						SormasUI.refreshView();
					} else {
						navigateToParticipants(dto.getUuid());
					}
				}
			}
		});

		return editView;
	}

	public CommitDiscardWrapperComponent<EventDataForm> getEventDataEditComponent(final String eventUuid, boolean inJurisdiction) {

		EventDto event = findEvent(eventUuid);
		EventDataForm eventEditForm = new EventDataForm(false, inJurisdiction);
		eventEditForm.setValue(event);
		final CommitDiscardWrapperComponent<EventDataForm> editView = new CommitDiscardWrapperComponent<EventDataForm>(
			eventEditForm,
			UserProvider.getCurrent().hasUserRight(UserRight.EVENT_EDIT),
			eventEditForm.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!eventEditForm.getFieldGroup().isModified()) {
				EventDto eventDto = eventEditForm.getValue();
				eventDto = FacadeProvider.getEventFacade().saveEvent(eventDto);
				Notification.show(I18nProperties.getString(Strings.messageEventSaved), Type.WARNING_MESSAGE);
				SormasUI.refreshView();
			}
		});

		if (UserProvider.getCurrent().hasUserRole(UserRole.ADMIN)) {
			editView.addDeleteListener(() -> {
				if (!existEventParticipantsLinkedToEvent(event)) {
					FacadeProvider.getEventFacade().deleteEvent(event.getUuid());
				} else {
					VaadinUiUtil.showSimplePopupWindow(
						I18nProperties.getString(Strings.headingEventNotDeleted),
						I18nProperties.getString(Strings.messageEventsNotDeletedReason));
				}
				UI.getCurrent().getNavigator().navigateTo(EventsView.VIEW_NAME);
			}, I18nProperties.getString(Strings.entityEvent));
		}

		// Initialize 'Archive' button
		if (UserProvider.getCurrent().hasUserRight(UserRight.EVENT_ARCHIVE)) {
			boolean archived = FacadeProvider.getEventFacade().isArchived(eventUuid);
			Button archiveEventButton = ButtonHelper.createButton(archived ? Captions.actionDearchive : Captions.actionArchive, e -> {
				editView.commit();
				archiveOrDearchiveEvent(eventUuid, !archived);
			}, ValoTheme.BUTTON_LINK);

			editView.getButtonsPanel().addComponentAsFirst(archiveEventButton);
			editView.getButtonsPanel().setComponentAlignment(archiveEventButton, Alignment.BOTTOM_LEFT);
		}

		return editView;
	}

	public void showBulkEventDataEditComponent(Collection<EventIndexDto> selectedEvents) {

		if (selectedEvents.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoEventsSelected),
				I18nProperties.getString(Strings.messageNoEventsSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
			return;
		}

		// Create a temporary event in order to use the CommitDiscardWrapperComponent
		EventDto tempEvent = new EventDto();

		BulkEventDataForm form = new BulkEventDataForm();
		form.setValue(tempEvent);
		final CommitDiscardWrapperComponent<BulkEventDataForm> editView =
			new CommitDiscardWrapperComponent<BulkEventDataForm>(form, form.getFieldGroup());

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingEditEvents));

		editView.addCommitListener(new CommitListener() {

			@Override
			public void onCommit() {
				EventDto updatedTempEvent = form.getValue();
				for (EventIndexDto indexDto : selectedEvents) {
					EventDto eventDto = FacadeProvider.getEventFacade().getEventByUuid(indexDto.getUuid());
					if (form.getEventStatusCheckBox().getValue() == true) {
						eventDto.setEventStatus(updatedTempEvent.getEventStatus());
					}

					FacadeProvider.getEventFacade().saveEvent(eventDto);
				}
				popupWindow.close();
				navigateToIndex();
				Notification.show(I18nProperties.getString(Strings.messageEventsEdited), Type.HUMANIZED_MESSAGE);
			}
		});

		editView.addDiscardListener(() -> popupWindow.close());
	}

	private EventDto createNewEvent(Disease disease) {
		EventDto event = EventDto.build();

		event.getEventLocation().setRegion(UserProvider.getCurrent().getUser().getRegion());
		UserReferenceDto userReference = UserProvider.getCurrent().getUserReference();
		event.setReportingUser(userReference);
		event.setDisease(disease);

		return event;
	}

	private void archiveOrDearchiveEvent(String eventUuid, boolean archive) {

		if (archive) {
			Label contentLabel = new Label(
				String.format(
					I18nProperties.getString(Strings.confirmationArchiveEvent),
					I18nProperties.getString(Strings.entityEvent).toLowerCase(),
					I18nProperties.getString(Strings.entityEvent).toLowerCase()));
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingArchiveEvent),
				contentLabel,
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				640,
				e -> {
					if (e.booleanValue() == true) {
						FacadeProvider.getEventFacade().archiveOrDearchiveEvent(eventUuid, true);
						Notification.show(
							String.format(I18nProperties.getString(Strings.messageEventArchived), I18nProperties.getString(Strings.entityEvent)),
							Type.ASSISTIVE_NOTIFICATION);
						navigateToData(eventUuid);
					}
				});
		} else {
			Label contentLabel = new Label(
				String.format(
					I18nProperties.getString(Strings.confirmationDearchiveEvent),
					I18nProperties.getString(Strings.entityEvent).toLowerCase(),
					I18nProperties.getString(Strings.entityEvent).toLowerCase()));
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingDearchiveEvent),
				contentLabel,
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				640,
				e -> {
					if (e.booleanValue()) {
						FacadeProvider.getEventFacade().archiveOrDearchiveEvent(eventUuid, false);
						Notification.show(
							String.format(I18nProperties.getString(Strings.messageEventDearchived), I18nProperties.getString(Strings.entityEvent)),
							Type.ASSISTIVE_NOTIFICATION);
						navigateToData(eventUuid);
					}
				});
		}
	}

	public void deleteAllSelectedItems(Collection<EventIndexDto> selectedRows, Runnable callback) {

		if (selectedRows.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoEventsSelected),
				I18nProperties.getString(Strings.messageNoEventsSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VaadinUiUtil
				.showDeleteConfirmationWindow(String.format(I18nProperties.getString(Strings.confirmationDeleteEvents), selectedRows.size()), () -> {
					List<EventDto> eventDtoList = new ArrayList<>();
					StringBuilder nonDeletableEvents = new StringBuilder();
					Integer countNotDeletedEvents = 0;
					for (EventIndexDto selectedRow : selectedRows) {
						EventDto eventDto = FacadeProvider.getEventFacade().getEventByUuid(selectedRow.getUuid());
						if (existEventParticipantsLinkedToEvent(eventDto)) {
							eventDtoList.add(eventDto);
							countNotDeletedEvents = countNotDeletedEvents + 1;
							nonDeletableEvents.append(selectedRow.getUuid().substring(0, 6)).append(", ");
						} else {
							FacadeProvider.getEventFacade().deleteEvent(selectedRow.getUuid());
						}
					}
					if (nonDeletableEvents.length() > 0) {
						nonDeletableEvents = new StringBuilder(" " + nonDeletableEvents.substring(0, nonDeletableEvents.length() - 2) + ". ");

					}
					callback.run();
					if (eventDtoList.isEmpty()) {
						new Notification(
							I18nProperties.getString(Strings.headingEventsDeleted),
							I18nProperties.getString(Strings.messageEventsDeleted),
							Type.HUMANIZED_MESSAGE,
							false).show(Page.getCurrent());
					} else {
						Window response = VaadinUiUtil.showSimplePopupWindow(
							I18nProperties.getString(Strings.headingSomeEventsNotDeleted),
							String.format(
								"%1s <br/> <br/> %2s",
								String.format(
									I18nProperties.getString(Strings.messageCountEventsNotDeleted),
									String.format("<b>%s</b>", countNotDeletedEvents),
									String.format("<b>%s</b>", nonDeletableEvents)),
								I18nProperties.getString(Strings.messageEventsNotDeletedReason)),
							ContentMode.HTML);
						response.setWidth(600, Sizeable.Unit.PIXELS);
					}
				});
		}
	}

	private Boolean existEventParticipantsLinkedToEvent(EventDto event) {
		List<EventParticipantDto> eventParticipantList =
			FacadeProvider.getEventParticipantFacade().getAllActiveEventParticipantsByEvent(event.getUuid());

		return !eventParticipantList.isEmpty();
	}

	public void archiveAllSelectedItems(Collection<EventIndexDto> selectedRows, Runnable callback) {

		if (selectedRows.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoEventsSelected),
				I18nProperties.getString(Strings.messageNoEventsSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingConfirmArchiving),
				new Label(String.format(I18nProperties.getString(Strings.confirmationArchiveEvents), selectedRows.size())),
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				null,
				e -> {
					if (e.booleanValue() == true) {
						for (EventIndexDto selectedRow : selectedRows) {
							FacadeProvider.getEventFacade().archiveOrDearchiveEvent(selectedRow.getUuid(), true);
						}
						callback.run();
						new Notification(
							I18nProperties.getString(Strings.headingEventsArchived),
							I18nProperties.getString(Strings.messageEventsArchived),
							Type.HUMANIZED_MESSAGE,
							false).show(Page.getCurrent());
					}
				});
		}
	}

	public void dearchiveAllSelectedItems(Collection<EventIndexDto> selectedRows, Runnable callback) {

		if (selectedRows.size() == 0) {
			new Notification(
				I18nProperties.getString(Strings.headingNoEventsSelected),
				I18nProperties.getString(Strings.messageNoEventsSelected),
				Type.WARNING_MESSAGE,
				false).show(Page.getCurrent());
		} else {
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingConfirmDearchiving),
				new Label(String.format(I18nProperties.getString(Strings.confirmationDearchiveEvents), selectedRows.size())),
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				null,
				e -> {
					if (e.booleanValue() == true) {
						for (EventIndexDto selectedRow : selectedRows) {
							FacadeProvider.getEventFacade().archiveOrDearchiveEvent(selectedRow.getUuid(), false);
						}
						callback.run();
						new Notification(
							I18nProperties.getString(Strings.headingEventsDearchived),
							I18nProperties.getString(Strings.messageEventsDearchived),
							Type.HUMANIZED_MESSAGE,
							false).show(Page.getCurrent());
					}
				});
		}
	}
}
