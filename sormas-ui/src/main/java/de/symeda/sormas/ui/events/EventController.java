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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

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
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.HtmlHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.events.eventLink.EventSelectionField;
import de.symeda.sormas.ui.survnet.SurvnetGateway;
import de.symeda.sormas.ui.survnet.SurvnetGatewayType;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent.CommitListener;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

import javax.validation.constraints.NotNull;

public class EventController {

	public void registerViews(Navigator navigator) {
		navigator.addView(EventsView.VIEW_NAME, EventsView.class);
		navigator.addView(EventDataView.VIEW_NAME, EventDataView.class);
		navigator.addView(EventParticipantsView.VIEW_NAME, EventParticipantsView.class);
		navigator.addView(EventActionsView.VIEW_NAME, EventActionsView.class);
	}

	public EventDto create(@NotNull final SormasUI ui, CaseReferenceDto caseRef) {
		CommitDiscardWrapperComponent<EventDataForm> eventCreateComponent = getEventCreateComponent(ui, caseRef);
		EventDto eventDto = eventCreateComponent.getWrappedComponent().getValue();
		VaadinUiUtil.showModalPopupWindow(eventCreateComponent, I18nProperties.getString(Strings.headingCreateNewEvent));
		return eventDto;
	}

	public EventDto create(@NotNull final SormasUI ui, ContactDto contact) {
		CommitDiscardWrapperComponent<EventDataForm> eventCreateComponent = getEventCreateComponent(ui, contact);
		EventDto eventDto = eventCreateComponent.getWrappedComponent().getValue();
		VaadinUiUtil.showModalPopupWindow(eventCreateComponent, I18nProperties.getString(Strings.headingCreateNewEvent));
		return eventDto;
	}

	public EventDto createSubordinateEvent(@NotNull final SormasUI ui, EventReferenceDto superordinateEvent) {
		CommitDiscardWrapperComponent<EventDataForm> eventCreateComponent = getEventCreateComponent(ui, superordinateEvent, false);
		EventDto eventDto = eventCreateComponent.getWrappedComponent().getValue();
		VaadinUiUtil.showModalPopupWindow(eventCreateComponent, I18nProperties.getString(Strings.headingCreateNewEvent));
		return eventDto;
	}

	public EventDto createSuperordinateEvent(@NotNull final SormasUI ui, EventReferenceDto subordinateEvent) {
		CommitDiscardWrapperComponent<EventDataForm> eventCreateComponent = getEventCreateComponent(ui, subordinateEvent, true);
		EventDto eventDto = eventCreateComponent.getWrappedComponent().getValue();
		VaadinUiUtil.showModalPopupWindow(eventCreateComponent, I18nProperties.getString(Strings.headingCreateNewEvent));
		return eventDto;
	}

	public void selectOrCreateEvent(@NotNull final SormasUI ui, CaseReferenceDto caseRef) {

		CaseDataDto caseDataDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseRef.getUuid());

		EventSelectionField eventSelect =
			new EventSelectionField(caseDataDto.getDisease(), I18nProperties.getString(Strings.infoPickOrCreateEventForCase));
		eventSelect.setWidth(1024, Sizeable.Unit.PIXELS);

		final CommitDiscardWrapperComponent<EventSelectionField> component = new CommitDiscardWrapperComponent<>(eventSelect);
		component.addCommitListener(() -> {
			EventIndexDto selectedEvent = eventSelect.getValue();
			if (selectedEvent != null) {

				EventCriteria eventCriteria = new EventCriteria();
				eventCriteria.caze(caseRef);
				eventCriteria.setUserFilterIncluded(false);
				List<EventIndexDto> eventIndexDto = FacadeProvider.getEventFacade().getIndexList(eventCriteria, null, null, null);

				EventReferenceDto eventReferenceDto = new EventReferenceDto(selectedEvent.getUuid());
				if (!eventIndexDto.contains(selectedEvent)) {
					linkCaseToEvent(ui, eventReferenceDto, caseDataDto, caseRef);
					SormasUI.refreshView();
				} else {
					SormasUI.refreshView();
					Notification notification =
						new Notification(I18nProperties.getString(Strings.messagePersonAlreadyCaseInEvent), "", Type.HUMANIZED_MESSAGE);
					notification.setDelayMsec(10000);
					notification.show(Page.getCurrent());
				}
			} else {
				create(ui, caseRef);
				SormasUI.refreshView();
			}
		});

		eventSelect.setSelectionChangeCallback((commitAllowed) -> {
			component.getCommitButton().setEnabled(commitAllowed);
		});

		VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreateEvent));
	}

	public void selectOrCreateEvent(@NotNull final SormasUI ui, ContactDto contact) {

		EventSelectionField eventSelect =
			new EventSelectionField(contact.getDisease(), I18nProperties.getString(Strings.infoPickOrCreateEventForContact));
		eventSelect.setWidth(1024, Sizeable.Unit.PIXELS);

		final CommitDiscardWrapperComponent<EventSelectionField> component = new CommitDiscardWrapperComponent<>(eventSelect);
		component.addCommitListener(() -> {
			EventIndexDto selectedEvent = eventSelect.getValue();
			if (selectedEvent != null) {

				EventCriteria eventCriteria = new EventCriteria();
				eventCriteria.setPerson(contact.getPerson());
				eventCriteria.setUserFilterIncluded(false);
				List<EventIndexDto> eventIndexDto = FacadeProvider.getEventFacade().getIndexList(eventCriteria, null, null, null);

				EventReferenceDto eventReferenceDto = new EventReferenceDto(selectedEvent.getUuid());
				if (!eventIndexDto.contains(selectedEvent)) {
					createEventParticipantWithContact(ui, eventReferenceDto, contact);
				}
			} else {
				create(ui, contact);
			}
			SormasUI.refreshView();
		});

		eventSelect.setSelectionChangeCallback((commitAllowed) -> {
			component.getCommitButton().setEnabled(commitAllowed);
		});

		VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreateEvent));
	}

	public void selectOrCreateSubordinateEvent(@NotNull final SormasUI ui, EventReferenceDto superordinateEventRef) {

		Set<String> excludedUuids = new HashSet<>();
		excludedUuids.add(superordinateEventRef.getUuid());
		excludedUuids.addAll(FacadeProvider.getEventFacade().getAllSuperordinateEventUuids(superordinateEventRef.getUuid()));

		EventDto superordinateEvent = FacadeProvider.getEventFacade().getEventByUuid(superordinateEventRef.getUuid());
		EventSelectionField selectionField = new EventSelectionField(superordinateEvent, excludedUuids, false);
		selectionField.setWidth(1024, Sizeable.Unit.PIXELS);

		final CommitDiscardWrapperComponent<EventSelectionField> component = new CommitDiscardWrapperComponent<>(selectionField);
		component.addCommitListener(() -> {
			EventIndexDto selectedIndexEvent = selectionField.getValue();
			if (selectedIndexEvent != null) {
				EventDto selectedEvent = FacadeProvider.getEventFacade().getEventByUuid(selectedIndexEvent.getUuid());
				selectedEvent.setSuperordinateEvent(superordinateEventRef);
				FacadeProvider.getEventFacade().saveEvent(selectedEvent);

				navigateToData(ui, superordinateEventRef.getUuid());
				Notification.show(I18nProperties.getString(Strings.messageEventLinkedAsSubordinate), Type.TRAY_NOTIFICATION);
			} else {
				createSubordinateEvent(ui, superordinateEventRef);
			}
		});

		selectionField.setSelectionChangeCallback((commitAllowed) -> component.getCommitButton().setEnabled(commitAllowed));
		VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreateEvent));
	}

	public void selectOrCreateSuperordinateEvent(@NotNull final SormasUI ui, @NotNull EventReferenceDto subordinateEventRef) {

		Set<String> excludedUuids = new HashSet<>();
		excludedUuids.add(subordinateEventRef.getUuid());
		excludedUuids.addAll(FacadeProvider.getEventFacade().getAllSubordinateEventUuids(subordinateEventRef.getUuid()));

		EventDto subordinateEvent = FacadeProvider.getEventFacade().getEventByUuid(subordinateEventRef.getUuid());
		EventSelectionField selectionField = new EventSelectionField(subordinateEvent, excludedUuids, true);
		selectionField.setWidth(1024, Sizeable.Unit.PIXELS);

		final CommitDiscardWrapperComponent<EventSelectionField> component = new CommitDiscardWrapperComponent<>(selectionField);
		component.addCommitListener(() -> {
			EventIndexDto selectedEvent = selectionField.getValue();
			if (selectedEvent != null) {
				subordinateEvent.setSuperordinateEvent(selectedEvent.toReference());
				FacadeProvider.getEventFacade().saveEvent(subordinateEvent);

				navigateToData(ui, subordinateEventRef.getUuid());
				Notification.show(I18nProperties.getString(Strings.messageEventLinkedAsSuperordinate), Type.TRAY_NOTIFICATION);
			} else {
				createSuperordinateEvent(ui, subordinateEventRef);
			}
		});

		selectionField.setSelectionChangeCallback((commitAllowed) -> component.getCommitButton().setEnabled(commitAllowed));
		VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreateEvent));
	}

	public void removeSuperordinateEvent(@NotNull final SormasUI ui, EventDto subordinateEvent, boolean reloadPage, String notificationMessage) {
		subordinateEvent.setSuperordinateEvent(null);
		FacadeProvider.getEventFacade().saveEvent(subordinateEvent);

		if (reloadPage) {
			navigateToData(ui, subordinateEvent.getUuid());
		}
		Notification.show(notificationMessage, Type.TRAY_NOTIFICATION);
	}

	/**
	 * @return true if the person was already an event participant in the event, false if not
	 */
	public boolean linkCaseToEvent(@NotNull final SormasUI ui, EventReferenceDto eventReferenceDto, CaseDataDto caseDataDto, CaseReferenceDto caseRef) {
		// Check whether Person is already enlisted as EventParticipant in this Event
		EventParticipantReferenceDto eventParticipantRef =
			FacadeProvider.getEventParticipantFacade().getReferenceByEventAndPerson(eventReferenceDto.getUuid(), caseDataDto.getPerson().getUuid());
		if (eventParticipantRef != null) {
			EventParticipantDto eventParticipant =
				FacadeProvider.getEventParticipantFacade().getEventParticipantByUuid(eventParticipantRef.getUuid());
			eventParticipant.setResultingCase(caseRef);
			FacadeProvider.getEventParticipantFacade().saveEventParticipant(eventParticipant);
			Notification notification =
				new Notification(I18nProperties.getString(Strings.messagePersonAlreadyEventParticipant), "", Type.HUMANIZED_MESSAGE);
			notification.setDelayMsec(10000);
			notification.show(Page.getCurrent());
			return true;
		}

		// Create new EventParticipant for this Person
		final PersonDto personDto = FacadeProvider.getPersonFacade().getPersonByUuid(caseDataDto.getPerson().getUuid());
		final EventParticipantDto eventParticipantDto =
			EventParticipantDto.buildFromCase(caseRef, personDto, eventReferenceDto, ui.getUserProvider().getUserReference());
		ControllerProvider.getEventParticipantController().createEventParticipant(ui, eventReferenceDto, r -> {
		}, eventParticipantDto);
		return false;
	}

	public void createEventParticipantWithContact(@NotNull final SormasUI ui, EventReferenceDto eventReferenceDto, ContactDto contact) {
		final PersonDto personDto = FacadeProvider.getPersonFacade().getPersonByUuid(contact.getPerson().getUuid());
		final EventParticipantDto eventParticipantDto =
			EventParticipantDto.buildFromPerson(personDto, eventReferenceDto, ui.getUserProvider().getUserReference());
		ControllerProvider.getEventParticipantController().createEventParticipant(ui, eventReferenceDto, r -> {
		}, eventParticipantDto);
	}

	public void navigateToIndex(@NotNull SormasUI ui) {
		String navigationState = EventsView.VIEW_NAME;
		ui.getNavigator().navigateTo(navigationState);
	}

	public void navigateToData(@NotNull SormasUI ui, String eventUuid) {
		navigateToData(ui,eventUuid, false);
	}

	public void navigateToData(@NotNull SormasUI ui, String eventUuid, boolean openTab) {

		String navigationState = EventDataView.VIEW_NAME + "/" + eventUuid;
		if (openTab) {
			ui.getPage().open(SormasUI.get().getPage().getLocation().getRawPath() + "#!" + navigationState, "_blank", false);
		} else {
			ui.getNavigator().navigateTo(navigationState);
		}
	}

	public void navigateToParticipants(String eventUuid) {
		String navigationState = EventParticipantsView.VIEW_NAME + "/" + eventUuid;
		SormasUI.get().getNavigator().navigateTo(navigationState);
	}

	public void navigateTo(EventCriteria eventCriteria) {
		ViewModelProviders.of(EventsView.class).remove(EventCriteria.class);
		String navigationState = AbstractView.buildNavigationState(EventsView.VIEW_NAME, eventCriteria);
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

	public CommitDiscardWrapperComponent<EventDataForm> getEventCreateComponent(@NotNull final SormasUI ui, CaseReferenceDto caseRef) {

		CaseDataDto caseDataDto = null;

		if (caseRef != null) {
			caseDataDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseRef.getUuid());
		}

		EventDataForm eventCreateForm = new EventDataForm(true, false);
		if (caseRef != null) {
			eventCreateForm.setValue(createNewEvent(ui, caseDataDto.getDisease()));
			eventCreateForm.getField(EventDto.DISEASE).setReadOnly(true);
		} else {
			eventCreateForm.setValue(createNewEvent(ui, null));
		}
		final CommitDiscardWrapperComponent<EventDataForm> editView = new CommitDiscardWrapperComponent<>(
				eventCreateForm,
				ui.getUserProvider().hasUserRight(UserRight.EVENT_CREATE),
				eventCreateForm.getFieldGroup());

		CaseDataDto finalCaseDataDto = caseDataDto;
		editView.addCommitListener(() -> {
			if (!eventCreateForm.getFieldGroup().isModified()) {
				EventDto dto = eventCreateForm.getValue();
				FacadeProvider.getEventFacade().saveEvent(dto);
				Notification.show(I18nProperties.getString(Strings.messageEventCreated), Type.WARNING_MESSAGE);

				if (caseRef != null) {
					EventReferenceDto createdEvent = new EventReferenceDto(dto.getUuid());

					linkCaseToEvent(ui, createdEvent, finalCaseDataDto, caseRef);
					SormasUI.refreshView();
				} else {
					navigateToParticipants(dto.getUuid());
				}
			}
		});

		return editView;
	}

	public CommitDiscardWrapperComponent<EventDataForm> getEventCreateComponent(@NotNull final SormasUI ui, ContactDto contact) {

		EventDataForm eventCreateForm = new EventDataForm(true, false);
		eventCreateForm.setValue(createNewEvent(ui, contact.getDisease()));
		eventCreateForm.getField(EventDto.DISEASE).setReadOnly(true);

		final CommitDiscardWrapperComponent<EventDataForm> editView = new CommitDiscardWrapperComponent<>(
			eventCreateForm,
			ui.getUserProvider().hasUserRight(UserRight.EVENT_CREATE),
			eventCreateForm.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!eventCreateForm.getFieldGroup().isModified()) {
				EventDto dto = eventCreateForm.getValue();
				FacadeProvider.getEventFacade().saveEvent(dto);
				Notification.show(I18nProperties.getString(Strings.messageEventCreated), Type.TRAY_NOTIFICATION);

				EventReferenceDto createdEvent = new EventReferenceDto(dto.getUuid());

				createEventParticipantWithContact(ui, createdEvent, contact);
				SormasUI.refreshView();
			}
		});

		return editView;
	}

	public CommitDiscardWrapperComponent<EventDataForm> getEventCreateComponent(
			@NotNull final SormasUI ui,
		EventReferenceDto superOrSubordinateEventRef,
		boolean createSuperordinateEvent) {

		EventDto superOrSubordinateEvent = FacadeProvider.getEventFacade().getEventByUuid(superOrSubordinateEventRef.getUuid());
		EventDataForm form = new EventDataForm(true, false);
		form.setValue(createNewEvent(ui, superOrSubordinateEvent.getDisease()));
		form.getField(EventDto.DISEASE).setReadOnly(true);

		final CommitDiscardWrapperComponent<EventDataForm> component =
			new CommitDiscardWrapperComponent<>(form, ui.getUserProvider().hasAllUserRights(UserRight.EVENT_CREATE), form.getFieldGroup());

		component.addCommitListener(() -> {
			if (!form.getFieldGroup().isModified()) {
				EventDto newEvent = form.getValue();

				if (!createSuperordinateEvent) {
					newEvent.setSuperordinateEvent(superOrSubordinateEvent.toReference());
				}

				FacadeProvider.getEventFacade().saveEvent(newEvent);

				EventReferenceDto newEventRef = new EventReferenceDto(newEvent.getUuid());

				if (createSuperordinateEvent) {
					superOrSubordinateEvent.setSuperordinateEvent(newEventRef);
					FacadeProvider.getEventFacade().saveEvent(superOrSubordinateEvent);
				}

				navigateToData(ui, superOrSubordinateEvent.getUuid());
				Notification.show(I18nProperties.getString(Strings.messageEventCreated), Type.TRAY_NOTIFICATION);
			}
		});

		return component;
	}

	public CommitDiscardWrapperComponent<EventDataForm> getEventDataEditComponent(
			@NotNull final SormasUI ui, final String eventUuid, Consumer<EventStatus> saveCallback) {

		EventDto event = findEvent(eventUuid);
		EventDataForm eventEditForm = new EventDataForm(false, event.isPseudonymized());
		eventEditForm.setValue(event);
		final CommitDiscardWrapperComponent<EventDataForm> editView = new CommitDiscardWrapperComponent<>(
				eventEditForm,
				ui.getUserProvider().hasUserRight(UserRight.EVENT_EDIT),
				eventEditForm.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!eventEditForm.getFieldGroup().isModified()) {
				EventDto eventDto = eventEditForm.getValue();
				eventDto = FacadeProvider.getEventFacade().saveEvent(eventDto);
				Notification.show(I18nProperties.getString(Strings.messageEventSaved), Type.WARNING_MESSAGE);
				SormasUI.refreshView();

				if (saveCallback != null) {
					saveCallback.accept(eventDto.getEventStatus());
				}
			}
		});

		if (ui.getUserProvider().hasUserRight(UserRight.EVENT_DELETE)) {
			editView.addDeleteListener(() -> {
				if (!existEventParticipantsLinkedToEvent(event)) {
					if (!deleteEvent(event)) {
						Notification.show(
							String.format(
								I18nProperties.getString(Strings.SurvnetGateway_notificationEntryNotDeleted),
								DataHelper.getShortUuid(event.getUuid())),
							"",
							Type.ERROR_MESSAGE);
					}
				} else {
					VaadinUiUtil.showSimplePopupWindow(
						I18nProperties.getString(Strings.headingEventNotDeleted),
						I18nProperties.getString(Strings.messageEventsNotDeletedReason));
				}
				UI.getCurrent().getNavigator().navigateTo(EventsView.VIEW_NAME);
			}, I18nProperties.getString(Strings.entityEvent));
		}

		// Initialize 'Archive' button
		if (ui.getUserProvider().hasUserRight(UserRight.EVENT_ARCHIVE)) {
			boolean archived = FacadeProvider.getEventFacade().isArchived(eventUuid);
			Button archiveEventButton = ButtonHelper.createButton(archived ? Captions.actionDearchive : Captions.actionArchive, e -> {
				editView.commit();
				archiveOrDearchiveEvent(ui, eventUuid, !archived);
			}, ValoTheme.BUTTON_LINK);

			editView.getButtonsPanel().addComponentAsFirst(archiveEventButton);
			editView.getButtonsPanel().setComponentAlignment(archiveEventButton, Alignment.BOTTOM_LEFT);
		}

		return editView;
	}

	private boolean deleteEvent(EventDto event) {
		boolean deletable = true;
		if (event.getEventStatus() == EventStatus.CLUSTER && FacadeProvider.getSurvnetGatewayFacade().isFeatureEnabled()) {
			deletable = SurvnetGateway.deleteInSurvnet(SurvnetGatewayType.EVENTS, Collections.singletonList(event));
		}
		if (deletable) {
			FacadeProvider.getEventFacade().deleteEvent(event.getUuid());
			return true;
		}
		return false;
	}

	public void showBulkEventDataEditComponent(@NotNull final SormasUI ui, Collection<EventIndexDto> selectedEvents) {

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
				new CommitDiscardWrapperComponent<>(form, form.getFieldGroup());

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

					if (form.getEventInvestigationStatusCheckbox().getValue() == true) {
						eventDto.setEventInvestigationStatus(updatedTempEvent.getEventInvestigationStatus());
					}

					FacadeProvider.getEventFacade().saveEvent(eventDto);
				}
				popupWindow.close();
				navigateToIndex(ui);
				Notification.show(I18nProperties.getString(Strings.messageEventsEdited), Type.HUMANIZED_MESSAGE);
			}
		});

		editView.addDiscardListener(() -> popupWindow.close());
	}

	public EventDto createNewEvent(@NotNull final SormasUI ui, Disease disease) {
		EventDto event = EventDto.build();

		event.getEventLocation().setCountry(FacadeProvider.getCountryFacade().getServerCountry());
		event.getEventLocation().setRegion(ui.getUserProvider().getUser().getRegion());
		UserReferenceDto userReference = ui.getUserProvider().getUserReference();
		event.setReportingUser(userReference);
		event.setDisease(disease);

		return event;
	}

	private void archiveOrDearchiveEvent(@NotNull final SormasUI ui, String eventUuid, boolean archive) {

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
						navigateToData(ui, eventUuid);
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
						navigateToData(ui, eventUuid);
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
					StringBuilder nonDeletableEventsWithParticipants = new StringBuilder();
					int countNotDeletedEventsWithParticipants = 0;
					StringBuilder nonDeletableEventsFromSurvnet = new StringBuilder();
					int countNotDeletedEventsFromSurvnet = 0;
					for (EventIndexDto selectedRow : selectedRows) {
						EventDto eventDto = FacadeProvider.getEventFacade().getEventByUuid(selectedRow.getUuid());
						if (existEventParticipantsLinkedToEvent(eventDto)) {
							countNotDeletedEventsWithParticipants = countNotDeletedEventsWithParticipants + 1;
							nonDeletableEventsWithParticipants.append(selectedRow.getUuid(), 0, 6).append(", ");
						} else {
							if (!deleteEvent(eventDto)) {
								countNotDeletedEventsFromSurvnet = countNotDeletedEventsFromSurvnet + 1;
								nonDeletableEventsFromSurvnet.append(selectedRow.getUuid(), 0, 6).append(", ");
							}
						}
					}
					if (nonDeletableEventsWithParticipants.length() > 0) {
						nonDeletableEventsWithParticipants = new StringBuilder(
							" " + nonDeletableEventsWithParticipants.substring(0, nonDeletableEventsWithParticipants.length() - 2) + ". ");
					}
					if (nonDeletableEventsFromSurvnet.length() > 0) {
						nonDeletableEventsFromSurvnet =
							new StringBuilder(" " + nonDeletableEventsFromSurvnet.substring(0, nonDeletableEventsFromSurvnet.length() - 2) + ". ");
					}
					callback.run();
					if (countNotDeletedEventsWithParticipants == 0 && countNotDeletedEventsFromSurvnet == 0) {
						new Notification(
							I18nProperties.getString(Strings.headingEventsDeleted),
							I18nProperties.getString(Strings.messageEventsDeleted),
							Type.HUMANIZED_MESSAGE,
							false).show(Page.getCurrent());
					} else {
						StringBuilder description = new StringBuilder();
						if (countNotDeletedEventsWithParticipants > 0) {
							description.append(
								String.format(
									"%1s <br/> %2s",
									String.format(
										I18nProperties.getString(Strings.messageCountEventsNotDeleted),
										String.format("<b>%s</b>", countNotDeletedEventsWithParticipants),
										String.format("<b>%s</b>", HtmlHelper.cleanHtml(nonDeletableEventsWithParticipants.toString()))),
									I18nProperties.getString(Strings.messageEventsNotDeletedReason)))
								.append("<br/> <br/>");
						}
						if (countNotDeletedEventsFromSurvnet > 0) {
							description.append(
								String.format(
									"%1s <br/> %2s",
									String.format(
										I18nProperties.getString(Strings.messageCountEventsNotDeletedSurvnet),
										String.format("<b>%s</b>", countNotDeletedEventsFromSurvnet),
										String.format("<b>%s</b>", HtmlHelper.cleanHtml(nonDeletableEventsFromSurvnet.toString()))),
									I18nProperties.getString(Strings.messageEventsNotDeletedReasonSurvnet)));
						}

						Window response = VaadinUiUtil.showSimplePopupWindow(
							I18nProperties.getString(Strings.headingSomeEventsNotDeleted),
							description.toString(),
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

	public VerticalLayout getEventViewTitleLayout(String uuid) {
		EventDto event = findEvent(uuid);

		VerticalLayout titleLayout = new VerticalLayout();
		titleLayout.addStyleNames(CssStyles.LAYOUT_MINIMAL, CssStyles.VSPACE_4, CssStyles.VSPACE_TOP_4);
		titleLayout.setSpacing(false);

		Label statusLabel = new Label(event.getEventStatus().toString());
		statusLabel.addStyleNames(CssStyles.H3, CssStyles.VSPACE_NONE, CssStyles.VSPACE_TOP_NONE);
		titleLayout.addComponents(statusLabel);

		if (event.getStartDate() != null) {
			Label eventStartDateLabel = new Label(
				event.getEndDate() != null
					? DateFormatHelper.buildPeriodString(event.getStartDate(), event.getEndDate())
					: DateFormatHelper.formatDate(event.getStartDate()));
			eventStartDateLabel.addStyleNames(CssStyles.H3, CssStyles.VSPACE_NONE, CssStyles.VSPACE_TOP_NONE);
			titleLayout.addComponent(eventStartDateLabel);
		}

		String shortUuid = DataHelper.getShortUuid(event.getUuid());
		String eventTitle = event.getEventTitle();
		Label eventLabel = new Label(StringUtils.isNotBlank(eventTitle) ? eventTitle + " (" + shortUuid + ")" : shortUuid);
		eventLabel.addStyleNames(CssStyles.H2, CssStyles.VSPACE_NONE, CssStyles.VSPACE_TOP_NONE, CssStyles.LABEL_PRIMARY);
		titleLayout.addComponent(eventLabel);

		return titleLayout;
	}

	public void sendAllSelectedToSurvnet(Set<EventIndexDto> selectedRows, Runnable callback) {
		List<String> selectedUuids = selectedRows.stream().map(EventIndexDto::getUuid).collect(Collectors.toList());

		// Show an error when at least one selected event is not a CLUSTER event
		Optional<? extends EventIndexDto> nonClusterEvent = selectedRows.stream().filter(e -> e.getEventStatus() != EventStatus.CLUSTER).findFirst();
		if (nonClusterEvent.isPresent()) {
			Notification.show(
					String.format(
							I18nProperties.getString(Strings.errorSurvNetNonClusterEvent),
							DataHelper.getShortUuid(nonClusterEvent.get().getUuid()),
							I18nProperties.getEnumCaption(EventStatus.CLUSTER)),
					"",
					Type.ERROR_MESSAGE);
			return;
		}

		// Show an error when at least one selected case is not owned by this server because ownership has been handed over
		String ownershipHandedOverUuid = FacadeProvider.getEventFacade().getFirstEventUuidWithOwnershipHandedOver(selectedUuids);
		if (ownershipHandedOverUuid != null) {
			Notification.show(
					String.format(I18nProperties.getString(Strings.errorSurvNetEventNotOwned), DataHelper.getShortUuid(ownershipHandedOverUuid)),
					"",
					Type.ERROR_MESSAGE);
			return;
		}

		SurvnetGateway.sendToSurvnet(SurvnetGatewayType.EVENTS, selectedUuids);

		callback.run();
		new Notification(
				I18nProperties.getString(Strings.headingEventsSentToSurvNet),
				I18nProperties.getString(Strings.messageEventsSentToSurvnet),
				Type.HUMANIZED_MESSAGE,
				false).show(Page.getCurrent());
	}
}
