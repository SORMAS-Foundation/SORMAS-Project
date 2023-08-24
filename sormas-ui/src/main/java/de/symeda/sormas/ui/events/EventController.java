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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Functions;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.common.progress.ProcessedEntityStatus;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.deletionconfiguration.DeletionInfoDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventFacade;
import de.symeda.sormas.api.event.EventGroupReferenceDto;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolRuntimeException;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.uuid.HasUuid;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.SormasUI;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.events.eventLink.EventSelectionField;
import de.symeda.sormas.ui.externalsurveillanceservice.ExternalSurveillanceServiceGateway;
import de.symeda.sormas.ui.utils.AbstractView;
import de.symeda.sormas.ui.utils.ArchiveHandlers;
import de.symeda.sormas.ui.utils.BulkOperationHandler;
import de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.DeleteRestoreHandlers;
import de.symeda.sormas.ui.utils.NotificationHelper;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.components.automaticdeletion.DeletionLabel;
import de.symeda.sormas.ui.utils.components.page.title.TitleLayout;

public class EventController {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public void registerViews(Navigator navigator) {
		navigator.addView(EventsView.VIEW_NAME, EventsView.class);
		navigator.addView(EventDataView.VIEW_NAME, EventDataView.class);
		if (UserProvider.getCurrent().hasUserRight(UserRight.EVENTPARTICIPANT_VIEW)) {
			navigator.addView(EventParticipantsView.VIEW_NAME, EventParticipantsView.class);
		}
		navigator.addView(EventActionsView.VIEW_NAME, EventActionsView.class);
	}

	public EventDto create(CaseReferenceDto caseRef) {
		CommitDiscardWrapperComponent<EventDataForm> eventCreateComponent = getEventCreateComponent(caseRef);
		EventDto eventDto = eventCreateComponent.getWrappedComponent().getValue();
		VaadinUiUtil.showModalPopupWindow(eventCreateComponent, I18nProperties.getString(Strings.headingCreateNewEvent));
		return eventDto;
	}

	public EventDto createFromCaseList(List<CaseReferenceDto> caseRefs) {
		CommitDiscardWrapperComponent<EventDataForm> eventCreateComponent = getEventCreateComponentForCaseList(caseRefs);
		EventDto eventDto = eventCreateComponent.getWrappedComponent().getValue();
		VaadinUiUtil.showModalPopupWindow(eventCreateComponent, I18nProperties.getString(Strings.headingCreateNewEvent));
		return eventDto;
	}

	public EventDto create(ContactDto contact) {
		CommitDiscardWrapperComponent<EventDataForm> eventCreateComponent = getEventCreateComponent(contact);
		EventDto eventDto = eventCreateComponent.getWrappedComponent().getValue();
		VaadinUiUtil.showModalPopupWindow(eventCreateComponent, I18nProperties.getString(Strings.headingCreateNewEvent));
		return eventDto;
	}

	public EventDto createFromContactList(List<ContactReferenceDto> contactRefs, Consumer<List<ContactReferenceDto>> callback) {
		CommitDiscardWrapperComponent<EventDataForm> eventCreateComponent = getEventCreateComponentForContactList(contactRefs, callback);
		EventDto eventDto = eventCreateComponent.getWrappedComponent().getValue();
		VaadinUiUtil.showModalPopupWindow(eventCreateComponent, I18nProperties.getString(Strings.headingCreateNewEvent));
		return eventDto;
	}

	public EventDto create(PersonReferenceDto personReference) {
		CommitDiscardWrapperComponent<EventDataForm> eventCreateComponent = getEventCreateComponent(personReference);
		EventDto eventDto = eventCreateComponent.getWrappedComponent().getValue();
		VaadinUiUtil.showModalPopupWindow(eventCreateComponent, I18nProperties.getString(Strings.headingCreateNewEvent));
		return eventDto;
	}

	public EventDto createSubordinateEvent(EventReferenceDto superordinateEvent) {
		CommitDiscardWrapperComponent<EventDataForm> eventCreateComponent = getEventCreateComponent(superordinateEvent, false);
		EventDto eventDto = eventCreateComponent.getWrappedComponent().getValue();
		VaadinUiUtil.showModalPopupWindow(eventCreateComponent, I18nProperties.getString(Strings.headingCreateNewEvent));
		return eventDto;
	}

	public EventDto createSuperordinateEvent(EventReferenceDto subordinateEvent) {
		CommitDiscardWrapperComponent<EventDataForm> eventCreateComponent = getEventCreateComponent(subordinateEvent, true);
		EventDto eventDto = eventCreateComponent.getWrappedComponent().getValue();
		VaadinUiUtil.showModalPopupWindow(eventCreateComponent, I18nProperties.getString(Strings.headingCreateNewEvent));
		return eventDto;
	}

	public void selectOrCreateEvent(CaseReferenceDto caseRef) {

		CaseDataDto caseDataDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseRef.getUuid());

		EventSelectionField eventSelect =
			new EventSelectionField(caseDataDto.getDisease(), I18nProperties.getString(Strings.infoPickOrCreateEventForCase), null);
		eventSelect.setWidth(1100, Sizeable.Unit.PIXELS);

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
					linkCaseToEvent(eventReferenceDto, caseDataDto, caseRef);
					SormasUI.refreshView();
				} else {
					SormasUI.refreshView();
					Notification notification =
						new Notification(I18nProperties.getString(Strings.messagePersonAlreadyCaseInEvent), "", Type.HUMANIZED_MESSAGE);
					notification.setDelayMsec(10000);
					notification.show(Page.getCurrent());
				}
			} else {
				create(caseRef);
				SormasUI.refreshView();
			}
		});

		eventSelect.setSelectionChangeCallback((commitAllowed) -> {
			component.getCommitButton().setEnabled(commitAllowed);
		});

		VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreateEvent));
	}

	public void selectOrCreateEventForCaseList(List<CaseReferenceDto> caseRefs) {

		if (caseRefs == null || caseRefs.isEmpty()) {
			return;
		}

		List<CaseDataDto> caseDataDtos =
			FacadeProvider.getCaseFacade().getByUuids(caseRefs.stream().map(ReferenceDto::getUuid).collect(Collectors.toList()));

		EventSelectionField eventSelect = new EventSelectionField(
			caseDataDtos.stream().findFirst().get().getDisease(),
			I18nProperties.getString(Strings.infoPickOrCreateEventForCases),
			null);
		eventSelect.setWidth(1100, Sizeable.Unit.PIXELS);

		final CommitDiscardWrapperComponent<EventSelectionField> component = new CommitDiscardWrapperComponent<>(eventSelect);
		component.addCommitListener(() -> {
			EventIndexDto selectedEvent = eventSelect.getValue();
			if (selectedEvent != null) {
				EventReferenceDto eventReferenceDto = new EventReferenceDto(selectedEvent.getUuid());
				linkCasesToEvent(eventReferenceDto, caseDataDtos);
			} else {
				createFromCaseList(caseRefs);
				SormasUI.refreshView();
			}
		});

		eventSelect.setSelectionChangeCallback(commitAllowed -> component.getCommitButton().setEnabled(commitAllowed));

		VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreateEvent));
	}

	public void selectOrCreateEventForContactList(List<ContactReferenceDto> contactRefs, Consumer<List<ContactReferenceDto>> callback) {

		if (contactRefs == null || contactRefs.isEmpty()) {
			return;
		}

		List<ContactDto> contactDtos =
			FacadeProvider.getContactFacade().getByUuids(contactRefs.stream().map(ReferenceDto::getUuid).collect(Collectors.toList()));

		EventSelectionField eventSelect = new EventSelectionField(
			contactDtos.stream().findFirst().get().getDisease(),
			I18nProperties.getString(Strings.infoPickOrCreateEventForContact),
			null);
		eventSelect.setWidth(1100, Sizeable.Unit.PIXELS);

		final CommitDiscardWrapperComponent<EventSelectionField> component = new CommitDiscardWrapperComponent<>(eventSelect);
		component.addCommitListener(() -> {
			EventIndexDto selectedEvent = eventSelect.getValue();
			if (selectedEvent != null) {
				EventReferenceDto eventReferenceDto = new EventReferenceDto(selectedEvent.getUuid());
				linkContactsToEvent(
					eventReferenceDto,
					contactDtos,
					remaining -> callback.accept(remaining.stream().map(ContactDto::toReference).collect(Collectors.toList())));
			} else {
				createFromContactList(contactRefs, callback);
				SormasUI.refreshView();
			}
		});

		eventSelect.setSelectionChangeCallback(commitAllowed -> component.getCommitButton().setEnabled(commitAllowed));

		VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreateEvent));
	}

	private void linkCasesToEvent(EventReferenceDto eventReferenceDto, List<CaseDataDto> cases) {

		Map<String, CaseDataDto> caseByPersonUuid = new HashMap<>();

		List<String> personUuids = cases.stream().map(caseDataDto -> {
			String personUuid = caseDataDto.getPerson().getUuid();
			if (!caseByPersonUuid.containsKey(personUuid)) {
				caseByPersonUuid.put(personUuid, caseDataDto);
			}
			return personUuid;
		}).collect(Collectors.toList());

		// Set the resulting case for persons already enlisted as EventParticipants
		List<EventParticipantDto> eventParticipantDtos =
			FacadeProvider.getEventParticipantFacade().getByEventAndPersons(eventReferenceDto.getUuid(), personUuids);
		for (EventParticipantDto eventParticipantDto : eventParticipantDtos) {
			String personUuid = eventParticipantDto.getPerson().getUuid();
			if (eventParticipantDto.getResultingCase() != null) {
				CaseReferenceDto resultingCase = caseByPersonUuid.get(personUuid).toReference();
				if (resultingCase != null) {
					eventParticipantDto.setResultingCase(resultingCase);
				}
			}
			caseByPersonUuid.remove(personUuid);
		}

		Collection<CaseDataDto> remainingCases = caseByPersonUuid.values();
		int casesAlreadyLinkedToEvent = cases.size() - remainingCases.size();

		//Create EventParticipants for the remaining cases
		if (!remainingCases.isEmpty()) {
			List<String> remainingPersonUuids =
				remainingCases.stream().map(caseDataDto -> caseDataDto.getPerson().getUuid()).collect(Collectors.toList());
			List<PersonDto> remainingPersons = FacadeProvider.getPersonFacade().getByUuids(remainingPersonUuids);
			HashMap<String, PersonDto> personByUuid = new HashMap<>();
			remainingPersons.stream().forEach(personDto -> personByUuid.put(personDto.getUuid(), personDto));

			remainingCases.stream().forEach(caseDataDto -> {
				EventParticipantDto ep = EventParticipantDto.buildFromCase(
					caseDataDto.toReference(),
					personByUuid.get(caseDataDto.getPerson().getUuid()),
					eventReferenceDto,
					UserProvider.getCurrent().getUserReference());
				FacadeProvider.getEventParticipantFacade().save(ep);
			});
		}

		String message = remainingCases.isEmpty()
			? I18nProperties.getString(Strings.messageAllCasesAlreadyInEvent)
			: casesAlreadyLinkedToEvent == 0
				? I18nProperties.getString(Strings.messageAllCasesLinkedToEvent)
				: String.format(I18nProperties.getString(Strings.messageCountCasesAlreadyInEvent), casesAlreadyLinkedToEvent);

		SormasUI.refreshView();
		NotificationHelper.showNotification(message, Type.HUMANIZED_MESSAGE, 10000);
	}

	private void linkContactsToEvent(EventReferenceDto eventReferenceDto, List<ContactDto> contacts, Consumer<List<ContactDto>> callback) {

		Map<String, ContactDto> contactByPersonUuid =
			contacts.stream().collect(Collectors.toMap(c -> c.getPerson().getUuid(), Functions.identity(), (c1, c2) -> c1));

		List<EventParticipantDto> byEventAndPersons = FacadeProvider.getEventParticipantFacade()
			.getByEventAndPersons(eventReferenceDto.getUuid(), new ArrayList<>(contactByPersonUuid.keySet()));

		List<ContactDto> alreadyLinkedContacts = new ArrayList<>();

		byEventAndPersons.forEach(eventParticipant -> {
			String personUuid = eventParticipant.getPerson().getUuid();

			alreadyLinkedContacts.add(contactByPersonUuid.get(personUuid));
			contactByPersonUuid.remove(personUuid);
		});

		//Create EventParticipants for the remaining contacts

		List<PersonDto> remainingPersons = FacadeProvider.getPersonFacade().getByUuids(new ArrayList<>(contactByPersonUuid.keySet()));
		Map<String, PersonDto> personByUuid = remainingPersons.stream().collect(Collectors.toMap(EntityDto::getUuid, Functions.identity()));

		UserReferenceDto currentUser = UserProvider.getCurrent().getUserReference();

		new BulkOperationHandler<ContactDto>(
			Strings.messageAllContactsLinkedToEvent,
			null,
			Strings.headingSomeContactsAlreadyInEvent,
			Strings.headingContactsNotLinked,
			Strings.messageCountContactsAlreadyInEvent,
			null,
			null,
			Strings.messageCountContactsNotLinkableAccessDeniedReason,
			Strings.messageAllContactsAlreadyInEvent,
			Strings.infoBulkProcessFinishedWithSkipsOutsideJurisdictionOrNotEligible,
			Strings.infoBulkProcessFinishedWithoutSuccess).doBulkOperation(batch -> {
				List<ProcessedEntity> processedContacts = new ArrayList<>();

				batch.forEach(contactDataDto -> {
					try {
						if (!alreadyLinkedContacts.contains(contactDataDto)) {

							EventParticipantDto ep = EventParticipantDto
								.buildFromPerson(personByUuid.get(contactDataDto.getPerson().getUuid()), eventReferenceDto, currentUser);

							FacadeProvider.getEventParticipantFacade().save(ep);
							processedContacts.add(new ProcessedEntity(contactDataDto.getUuid(), ProcessedEntityStatus.SUCCESS));
						} else {
							processedContacts.add(new ProcessedEntity(contactDataDto.getUuid(), ProcessedEntityStatus.NOT_ELIGIBLE));
						}
					} catch (AccessDeniedException e) {
						processedContacts.add(new ProcessedEntity(contactDataDto.getUuid(), ProcessedEntityStatus.ACCESS_DENIED_FAILURE));
						logger.error(
							"The event participant for contact with uuid {} could not be linked due to an AccessDeniedException",
							contactDataDto.getUuid(),
							e);
					} catch (Exception e) {
						processedContacts.add(new ProcessedEntity(contactDataDto.getUuid(), ProcessedEntityStatus.INTERNAL_FAILURE));
						logger.error(
							"The event participant for contact with uuid {} could not be linked due to an Exception",
							contactDataDto.getUuid(),
							e);
					}
				});

				return processedContacts;
			}, new ArrayList<>(contacts), callback);
	}

	public void selectEvent(EventGroupReferenceDto eventGroupReference) {

		Set<String> relatedEventUuids = FacadeProvider.getEventFacade().getAllEventUuidsByEventGroupUuid(eventGroupReference.getUuid());

		EventSelectionField eventSelect = new EventSelectionField(relatedEventUuids);
		eventSelect.setWidth(1024, Sizeable.Unit.PIXELS);

		final CommitDiscardWrapperComponent<EventSelectionField> component = new CommitDiscardWrapperComponent<>(eventSelect);
		component.addCommitListener(() -> {
			EventIndexDto selectedEvent = eventSelect.getValue();
			if (selectedEvent != null) {
				EventReferenceDto eventReference = selectedEvent.toReference();
				FacadeProvider.getEventGroupFacade().linkEventToGroup(eventReference, eventGroupReference);
				FacadeProvider.getEventGroupFacade().notifyEventAddedToEventGroup(eventGroupReference, Collections.singletonList(eventReference));
				Notification.show(I18nProperties.getString(Strings.messageEventLinkedToGroup), Type.TRAY_NOTIFICATION);
				SormasUI.refreshView();
			}
		});

		eventSelect.setSelectionChangeCallback((commitAllowed) -> {
			component.getCommitButton().setEnabled(commitAllowed);
		});

		VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreateEvent));
	}

	public void selectOrCreateEvent(ContactDto contact) {

		EventSelectionField eventSelect =
			new EventSelectionField(contact.getDisease(), I18nProperties.getString(Strings.infoPickOrCreateEventForContact), null);
		eventSelect.setWidth(1100, Sizeable.Unit.PIXELS);

		final CommitDiscardWrapperComponent<EventSelectionField> component = new CommitDiscardWrapperComponent<>(eventSelect);
		component.addCommitListener(() -> {
			EventIndexDto selectedEvent = eventSelect.getValue();
			if (selectedEvent != null) {

				EventCriteria eventCriteria = new EventCriteria();
				eventCriteria.setPerson(contact.getPerson());
				eventCriteria.setUserFilterIncluded(false);
				List<EventIndexDto> personEvents = FacadeProvider.getEventFacade().getIndexList(eventCriteria, null, null, null);

				EventReferenceDto eventReferenceDto = new EventReferenceDto(selectedEvent.getUuid());
				if (!personEvents.contains(selectedEvent)) {
					createEventParticipantWithContact(eventReferenceDto, contact);
				} else {
					Notification notification =
						new Notification(I18nProperties.getString(Strings.messageThisPersonAlreadyEventParticipant), "", Type.HUMANIZED_MESSAGE);
					notification.setDelayMsec(10000);
					notification.show(Page.getCurrent());
				}
			} else {
				create(contact);
			}
			SormasUI.refreshView();
		});

		eventSelect.setSelectionChangeCallback((commitAllowed) -> {
			component.getCommitButton().setEnabled(commitAllowed);
		});

		VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreateEvent));
	}

	public void selectOrCreateEvent(PersonReferenceDto personReference) {

		EventSelectionField eventSelect = new EventSelectionField(null, I18nProperties.getString(Strings.infoPickOrCreateEventForContact), null);
		eventSelect.setWidth(1100, Sizeable.Unit.PIXELS);

		final CommitDiscardWrapperComponent<EventSelectionField> component = new CommitDiscardWrapperComponent<>(eventSelect);
		component.addCommitListener(() -> {
			EventIndexDto selectedEvent = eventSelect.getValue();
			if (selectedEvent != null) {

				EventCriteria eventCriteria = new EventCriteria();
				eventCriteria.setPerson(personReference);
				eventCriteria.setUserFilterIncluded(false);
				List<EventIndexDto> eventIndexDto = FacadeProvider.getEventFacade().getIndexList(eventCriteria, null, null, null);

				EventReferenceDto eventReferenceDto = new EventReferenceDto(selectedEvent.getUuid());
				if (!eventIndexDto.contains(selectedEvent)) {
					createEventParticipantWithPerson(eventReferenceDto, personReference);
				} else {
					SormasUI.refreshView();
					Notification notification =
						new Notification(I18nProperties.getString(Strings.messageThisPersonAlreadyEventParticipant), "", Type.HUMANIZED_MESSAGE);
					notification.setDelayMsec(10000);
					notification.show(Page.getCurrent());
				}
			} else {
				create(personReference);
			}
			SormasUI.refreshView();
		});

		eventSelect.setSelectionChangeCallback((commitAllowed) -> {
			component.getCommitButton().setEnabled(commitAllowed);
		});

		VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreateEvent));
	}

	public void selectOrCreateSubordinateEvent(EventReferenceDto superordinateEventRef) {

		Set<String> excludedUuids = new HashSet<>();
		excludedUuids.add(superordinateEventRef.getUuid());
		excludedUuids.addAll(FacadeProvider.getEventFacade().getAllSuperordinateEventUuids(superordinateEventRef.getUuid()));

		EventDto superordinateEvent = FacadeProvider.getEventFacade().getEventByUuid(superordinateEventRef.getUuid(), false);
		EventSelectionField selectionField = EventSelectionField.forSubordinateEvent(superordinateEvent, excludedUuids);
		selectionField.setWidth(1100, Sizeable.Unit.PIXELS);

		final CommitDiscardWrapperComponent<EventSelectionField> component = new CommitDiscardWrapperComponent<>(selectionField);
		component.addCommitListener(() -> {
			EventIndexDto selectedIndexEvent = selectionField.getValue();
			if (selectedIndexEvent != null) {
				EventDto selectedEvent = FacadeProvider.getEventFacade().getEventByUuid(selectedIndexEvent.getUuid(), false);
				selectedEvent.setSuperordinateEvent(superordinateEventRef);
				FacadeProvider.getEventFacade().save(selectedEvent);

				navigateToData(superordinateEventRef.getUuid());
				Notification.show(I18nProperties.getString(Strings.messageEventLinkedAsSubordinate), Type.TRAY_NOTIFICATION);
			} else {
				createSubordinateEvent(superordinateEventRef);
			}
		});

		selectionField.setSelectionChangeCallback((commitAllowed) -> component.getCommitButton().setEnabled(commitAllowed));
		VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreateEvent));
	}

	public void selectOrCreateSuperordinateEvent(EventReferenceDto subordinateEventRef) {

		Set<String> excludedUuids = new HashSet<>();
		excludedUuids.add(subordinateEventRef.getUuid());
		excludedUuids.addAll(FacadeProvider.getEventFacade().getAllSubordinateEventUuids(subordinateEventRef.getUuid()));

		EventDto subordinateEvent = FacadeProvider.getEventFacade().getEventByUuid(subordinateEventRef.getUuid(), false);
		EventSelectionField selectionField = EventSelectionField.forSuperordinateEvent(subordinateEvent, excludedUuids);
		selectionField.setWidth(1100, Sizeable.Unit.PIXELS);

		final CommitDiscardWrapperComponent<EventSelectionField> component = new CommitDiscardWrapperComponent<>(selectionField);
		component.addCommitListener(() -> {
			EventIndexDto selectedEvent = selectionField.getValue();
			if (selectedEvent != null) {
				subordinateEvent.setSuperordinateEvent(selectedEvent.toReference());
				FacadeProvider.getEventFacade().save(subordinateEvent);

				navigateToData(subordinateEventRef.getUuid());
				Notification.show(I18nProperties.getString(Strings.messageEventLinkedAsSuperordinate), Type.TRAY_NOTIFICATION);
			} else {
				createSuperordinateEvent(subordinateEventRef);
			}
		});

		selectionField.setSelectionChangeCallback((commitAllowed) -> component.getCommitButton().setEnabled(commitAllowed));
		VaadinUiUtil.showModalPopupWindow(component, I18nProperties.getString(Strings.headingPickOrCreateEvent));
	}

	public void removeLinkCaseEventParticipant(EventDto event, CaseDataDto caseDataDto, String notificationMessage) {
		EventParticipantReferenceDto eventParticipantRef =
			FacadeProvider.getEventParticipantFacade().getReferenceByEventAndPerson(event.getUuid(), caseDataDto.getPerson().getUuid());

		EventParticipantDto eventParticipantDto = FacadeProvider.getEventParticipantFacade().getEventParticipantByUuid(eventParticipantRef.getUuid());
		eventParticipantDto.setResultingCase(null);
		FacadeProvider.getEventParticipantFacade().save(eventParticipantDto);

		Notification.show(notificationMessage, Type.TRAY_NOTIFICATION);
	}

	public void removeSuperordinateEvent(EventDto subordinateEvent, boolean reloadPage, String notificationMessage) {
		subordinateEvent.setSuperordinateEvent(null);
		FacadeProvider.getEventFacade().save(subordinateEvent);

		if (reloadPage) {
			navigateToData(subordinateEvent.getUuid());
		}
		Notification.show(notificationMessage, Type.TRAY_NOTIFICATION);
	}

	/**
	 * @return true if the person was already an event participant in the event, false if not
	 */
	public boolean linkCaseToEvent(EventReferenceDto eventReferenceDto, CaseDataDto caseDataDto, CaseReferenceDto caseRef) {
		// Check whether Person is already enlisted as EventParticipant in this Event
		EventParticipantReferenceDto eventParticipantRef =
			FacadeProvider.getEventParticipantFacade().getReferenceByEventAndPerson(eventReferenceDto.getUuid(), caseDataDto.getPerson().getUuid());
		if (eventParticipantRef != null) {
			EventParticipantDto eventParticipant =
				FacadeProvider.getEventParticipantFacade().getEventParticipantByUuid(eventParticipantRef.getUuid());
			eventParticipant.setResultingCase(caseRef);
			FacadeProvider.getEventParticipantFacade().save(eventParticipant);
			Notification notification =
				new Notification(I18nProperties.getString(Strings.messagePersonAlreadyEventParticipant), "", Type.HUMANIZED_MESSAGE);
			notification.setDelayMsec(10000);
			notification.show(Page.getCurrent());
			return true;
		}

		// Create new EventParticipant for this Person
		final PersonDto personDto = FacadeProvider.getPersonFacade().getByUuid(caseDataDto.getPerson().getUuid());
		final EventParticipantDto eventParticipantDto =
			new EventParticipantDto().buildFromCase(caseRef, personDto, eventReferenceDto, UserProvider.getCurrent().getUserReference());
		ControllerProvider.getEventParticipantController().createEventParticipant(eventReferenceDto, r -> {
		}, eventParticipantDto);
		return false;
	}

	public void createEventParticipantWithContact(EventReferenceDto eventReferenceDto, ContactDto contact) {
		final PersonDto personDto = FacadeProvider.getPersonFacade().getByUuid(contact.getPerson().getUuid());
		final EventParticipantDto eventParticipantDto =
			new EventParticipantDto().buildFromPerson(personDto, eventReferenceDto, UserProvider.getCurrent().getUserReference());
		ControllerProvider.getEventParticipantController().createEventParticipant(eventReferenceDto, r -> {
		}, eventParticipantDto);
	}

	public void createEventParticipantWithPerson(EventReferenceDto eventReferenceDto, PersonReferenceDto personReference) {
		final PersonDto personDto = FacadeProvider.getPersonFacade().getByUuid(personReference.getUuid());
		final EventParticipantDto eventParticipantDto =
			new EventParticipantDto().buildFromPerson(personDto, eventReferenceDto, UserProvider.getCurrent().getUserReference());
		ControllerProvider.getEventParticipantController().createEventParticipant(eventReferenceDto, r -> {
		}, eventParticipantDto);
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

	public void navigateTo(EventCriteria eventCriteria) {
		navigateTo(eventCriteria, false);
	}

	public void navigateTo(EventCriteria eventCriteria, boolean changeToDefaultViewType) {
		if (changeToDefaultViewType) {
			ViewModelProviders.of(EventsView.class).remove(EventsViewConfiguration.class);
		}
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
		return FacadeProvider.getEventFacade().getEventByUuid(uuid, false);
	}

	public CommitDiscardWrapperComponent<EventDataForm> getEventCreateComponent(CaseReferenceDto caseRef) {

		CaseDataDto caseDataDto = null;

		if (caseRef != null) {
			caseDataDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseRef.getUuid());
		}

		EventDataForm eventCreateForm = new EventDataForm(true, false, true); // Valid because jurisdiction doesn't matter for entities that are about to be created
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
		editView.addCommitListener(() -> {
			if (!eventCreateForm.getFieldGroup().isModified()) {
				EventDto dto = eventCreateForm.getValue();
				FacadeProvider.getEventFacade().save(dto);
				Notification.show(I18nProperties.getString(Strings.messageEventCreated), Type.WARNING_MESSAGE);

				if (caseRef != null) {
					EventReferenceDto createdEvent = new EventReferenceDto(dto.getUuid());

					linkCaseToEvent(createdEvent, finalCaseDataDto, caseRef);
					SormasUI.refreshView();
				} else if (UserProvider.getCurrent().hasUserRight(UserRight.EVENTPARTICIPANT_VIEW)) {
					navigateToParticipants(dto.getUuid());
				} else {
					navigateToData(dto.getUuid());
				}
			}
		});

		return editView;
	}

	public CommitDiscardWrapperComponent<EventDataForm> getEventCreateComponentForCaseList(List<CaseReferenceDto> caseRefs) {

		List<CaseDataDto> caseDataDtos =
			FacadeProvider.getCaseFacade().getByUuids(caseRefs.stream().map(c -> c.getUuid()).collect(Collectors.toList()));

		EventDataForm eventCreateForm = new EventDataForm(true, false, true); // Valid because jurisdiction doesn't matter for entities that are about to be created
		eventCreateForm.setValue(createNewEvent(caseDataDtos.stream().findFirst().get().getDisease()));
		eventCreateForm.getField(EventDto.DISEASE).setReadOnly(true);
		final CommitDiscardWrapperComponent<EventDataForm> editView = new CommitDiscardWrapperComponent<>(
			eventCreateForm,
			UserProvider.getCurrent().hasUserRight(UserRight.EVENT_CREATE),
			eventCreateForm.getFieldGroup());

		List<CaseDataDto> finalCaseDataDtos = caseDataDtos;
		editView.addCommitListener(() -> {
			if (!eventCreateForm.getFieldGroup().isModified()) {
				EventDto dto = eventCreateForm.getValue();
				FacadeProvider.getEventFacade().save(dto);
				Notification.show(I18nProperties.getString(Strings.messageEventCreated), Type.WARNING_MESSAGE);

				linkCasesToEvent(new EventReferenceDto(dto.getUuid()), finalCaseDataDtos);
			}
		});

		return editView;
	}

	public CommitDiscardWrapperComponent<EventDataForm> getEventCreateComponentForContactList(
		List<ContactReferenceDto> contactRefs,
		Consumer<List<ContactReferenceDto>> callback) {

		List<ContactDto> contactDtos =
			FacadeProvider.getContactFacade().getByUuids(contactRefs.stream().map(ReferenceDto::getUuid).collect(Collectors.toList()));

		EventDataForm eventCreateForm = new EventDataForm(true, false, true); // Valid because jurisdiction doesn't matter for entities that are about to be created
		eventCreateForm.setValue(createNewEvent(contactDtos.stream().findFirst().get().getDisease()));
		eventCreateForm.getField(EventDto.DISEASE).setReadOnly(true);
		final CommitDiscardWrapperComponent<EventDataForm> editView = new CommitDiscardWrapperComponent<EventDataForm>(
			eventCreateForm,
			UserProvider.getCurrent().hasUserRight(UserRight.EVENT_CREATE),
			eventCreateForm.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!eventCreateForm.getFieldGroup().isModified()) {
				EventDto dto = eventCreateForm.getValue();
				FacadeProvider.getEventFacade().save(dto);
				Notification.show(I18nProperties.getString(Strings.messageEventCreated), Type.WARNING_MESSAGE);

				linkContactsToEvent(
					new EventReferenceDto(dto.getUuid()),
					contactDtos,
					remaining -> callback.accept(remaining.stream().map(ContactDto::toReference).collect(Collectors.toList())));
			}
		});

		return editView;
	}

	public CommitDiscardWrapperComponent<EventDataForm> getEventCreateComponent(PersonReferenceDto personReference) {

		EventDataForm eventCreateForm = new EventDataForm(true, false, true); // Valid because jurisdiction doesn't matter for entities that are about to be created
		eventCreateForm.setValue(createNewEvent());

		final CommitDiscardWrapperComponent<EventDataForm> editView = new CommitDiscardWrapperComponent<>(
			eventCreateForm,
			UserProvider.getCurrent().hasUserRight(UserRight.EVENT_CREATE),
			eventCreateForm.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!eventCreateForm.getFieldGroup().isModified()) {
				EventDto dto = eventCreateForm.getValue();
				FacadeProvider.getEventFacade().save(dto);
				Notification.show(I18nProperties.getString(Strings.messageEventCreated), Type.TRAY_NOTIFICATION);

				EventReferenceDto createdEvent = new EventReferenceDto(dto.getUuid());

				createEventParticipantWithPerson(createdEvent, personReference);
				SormasUI.refreshView();
			}
		});

		return editView;
	}

	public CommitDiscardWrapperComponent<EventDataForm> getEventCreateComponent(ContactDto contact) {

		EventDataForm eventCreateForm = new EventDataForm(true, false, true); // Valid because jurisdiction doesn't matter for entities that are about to be created
		eventCreateForm.setValue(createNewEvent(contact.getDisease()));
		eventCreateForm.getField(EventDto.DISEASE).setReadOnly(true);

		final CommitDiscardWrapperComponent<EventDataForm> editView = new CommitDiscardWrapperComponent<>(
			eventCreateForm,
			UserProvider.getCurrent().hasUserRight(UserRight.EVENT_CREATE),
			eventCreateForm.getFieldGroup());

		editView.addCommitListener(() -> {
			if (!eventCreateForm.getFieldGroup().isModified()) {
				EventDto dto = eventCreateForm.getValue();
				FacadeProvider.getEventFacade().save(dto);
				Notification.show(I18nProperties.getString(Strings.messageEventCreated), Type.TRAY_NOTIFICATION);

				EventReferenceDto createdEvent = new EventReferenceDto(dto.getUuid());

				createEventParticipantWithContact(createdEvent, contact);
				SormasUI.refreshView();
			}
		});

		return editView;
	}

	public CommitDiscardWrapperComponent<EventDataForm> getEventCreateComponent(
		EventReferenceDto superOrSubordinateEventRef,
		boolean createSuperordinateEvent) {

		EventDto superOrSubordinateEvent = FacadeProvider.getEventFacade().getEventByUuid(superOrSubordinateEventRef.getUuid(), false);
		EventDataForm form = new EventDataForm(true, false, true); // Valid because jurisdiction doesn't matter for entities that are about to be created
		form.setValue(createNewEvent(superOrSubordinateEvent.getDisease()));
		form.getField(EventDto.DISEASE).setReadOnly(true);

		final CommitDiscardWrapperComponent<EventDataForm> component =
			new CommitDiscardWrapperComponent<>(form, UserProvider.getCurrent().hasAllUserRights(UserRight.EVENT_CREATE), form.getFieldGroup());

		component.addCommitListener(() -> {
			if (!form.getFieldGroup().isModified()) {
				EventDto newEvent = form.getValue();

				if (!createSuperordinateEvent) {
					newEvent.setSuperordinateEvent(superOrSubordinateEvent.toReference());
				}

				FacadeProvider.getEventFacade().save(newEvent);

				EventReferenceDto newEventRef = new EventReferenceDto(newEvent.getUuid());

				if (createSuperordinateEvent) {
					superOrSubordinateEvent.setSuperordinateEvent(newEventRef);
					FacadeProvider.getEventFacade().save(superOrSubordinateEvent);
				}

				navigateToData(superOrSubordinateEvent.getUuid());
				Notification.show(I18nProperties.getString(Strings.messageEventCreated), Type.TRAY_NOTIFICATION);
			}
		});

		return component;
	}

	public CommitDiscardWrapperComponent<EventDataForm> getEventDataEditComponent(final String eventUuid, Consumer<EventStatus> saveCallback) {

		EventDto event = findEvent(eventUuid);
		DeletionInfoDto automaticDeletionInfoDto = FacadeProvider.getEventFacade().getAutomaticDeletionInfo(eventUuid);
		DeletionInfoDto manuallyDeletionInfoDto = FacadeProvider.getEventFacade().getManuallyDeletionInfo(eventUuid);

		EventDataForm eventEditForm = new EventDataForm(false, event.isPseudonymized(), event.isInJurisdiction());
		eventEditForm.setValue(event);
		final CommitDiscardWrapperComponent<EventDataForm> editView =
			new CommitDiscardWrapperComponent<EventDataForm>(eventEditForm, true, eventEditForm.getFieldGroup());

		editView.getButtonsPanel()
			.addComponentAsFirst(new DeletionLabel(automaticDeletionInfoDto, manuallyDeletionInfoDto, event.isDeleted(), EventDto.I18N_PREFIX));

		if (event.isDeleted()) {
			editView.getWrappedComponent().getField(EventDto.DELETION_REASON).setVisible(true);
			if (editView.getWrappedComponent().getField(EventDto.DELETION_REASON).getValue() == DeletionReason.OTHER_REASON) {
				editView.getWrappedComponent().getField(EventDto.OTHER_DELETION_REASON).setVisible(true);
			}
		}

		editView.addCommitListener(() -> {
			if (!eventEditForm.getFieldGroup().isModified()) {
				EventDto eventDto = eventEditForm.getValue();

				final UserDto user = UserProvider.getCurrent().getUser();
				final RegionReferenceDto userRegion = user.getRegion();
				final DistrictReferenceDto userDistrict = user.getDistrict();
				final RegionReferenceDto epEventRegion = eventDto.getEventLocation().getRegion();
				final DistrictReferenceDto epEventDistrict = eventDto.getEventLocation().getDistrict();
				final Boolean eventOutsideJurisdiction =
					(userRegion != null && !userRegion.equals(epEventRegion) || userDistrict != null && !userDistrict.equals(epEventDistrict));

				if (eventOutsideJurisdiction) {
					VaadinUiUtil.showConfirmationPopup(
						I18nProperties.getString(Strings.headingEventJurisdictionUpdated),
						new Label(I18nProperties.getString(Strings.messageEventJurisdictionUpdated)),
						I18nProperties.getString(Strings.yes),
						I18nProperties.getString(Strings.no),
						500,
						confirmed -> {
							if (confirmed) {
								saveEvent(saveCallback, eventDto);
							}
						});
				} else {
					saveEvent(saveCallback, eventDto);
				}
			}
		});

		final String uuid = event.getUuid();
		if (UserProvider.getCurrent().hasUserRight(UserRight.EVENT_DELETE)) {
			editView.addDeleteWithReasonOrRestoreListener((deleteDetails) -> {
				if (!existEventParticipantsLinkedToEvent(event)) {
					try {
						FacadeProvider.getEventFacade().delete(uuid, deleteDetails);
					} catch (ExternalSurveillanceToolRuntimeException e) {
						Notification.show(
							String.format(
								I18nProperties.getString(Strings.ExternalSurveillanceToolGateway_notificationEntryNotDeleted),
								DataHelper.getShortUuid(uuid)),
							"",
							Type.ERROR_MESSAGE);
					}
				} else {
					VaadinUiUtil.showSimplePopupWindow(
						I18nProperties.getString(Strings.headingEventNotDeleted),
						I18nProperties.getString(Strings.messageEventsNotDeletedLinkedEntitiesReason));
				}
				UI.getCurrent().getNavigator().navigateTo(EventsView.VIEW_NAME);
			}, getDeleteConfirmationDetails(Collections.singletonList(eventUuid)), (deleteDetails) -> {
				FacadeProvider.getEventFacade().restore(uuid);
				UI.getCurrent().getNavigator().navigateTo(EventsView.VIEW_NAME);
			}, I18nProperties.getString(Strings.entityEvent), uuid, FacadeProvider.getEventFacade());
		}

		// Initialize 'Archive' button
		if (UserProvider.getCurrent().hasUserRight(UserRight.EVENT_ARCHIVE)) {
			ControllerProvider.getArchiveController().addArchivingButton(event, ArchiveHandlers.forEvent(), editView, () -> {
				ViewModelProviders.of(EventParticipantsView.class).get(EventParticipantsViewConfiguration.class).setRelevanceStatusChangedEvent(null);
				navigateToData(uuid);
			});
		}

		editView.restrictEditableComponentsOnEditView(
			UserRight.EVENT_EDIT,
			null,
			UserRight.EVENT_DELETE,
			UserRight.EVENT_ARCHIVE,
			FacadeProvider.getEventFacade().getEditPermissionType(eventUuid),
			event.isInJurisdiction());

		return editView;
	}

	private String getDeleteConfirmationDetails(List<String> eventUuids) {
		boolean hasPendingRequest = FacadeProvider.getSormasToSormasEventFacade().hasPendingRequest(eventUuids);

		return hasPendingRequest ? "<br/>" + I18nProperties.getString(Strings.messageDeleteWithPendingShareRequest) + "<br/>" : "";
	}

	private void saveEvent(Consumer<EventStatus> saveCallback, EventDto eventDto) {
		eventDto = FacadeProvider.getEventFacade().save(eventDto);
		Notification.show(I18nProperties.getString(Strings.messageEventSaved), Type.WARNING_MESSAGE);
		SormasUI.refreshView();

		if (saveCallback != null) {
			saveCallback.accept(eventDto.getEventStatus());
		}
	}

	public void showBulkEventDataEditComponent(Collection<EventIndexDto> selectedEvents, EventGrid eventGrid) {

		if (selectedEvents.isEmpty()) {
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
		final CommitDiscardWrapperComponent<BulkEventDataForm> editView = new CommitDiscardWrapperComponent<>(form, form.getFieldGroup());

		Window popupWindow = VaadinUiUtil.showModalPopupWindow(editView, I18nProperties.getString(Strings.headingEditEvents));

		editView.addCommitListener(() -> {
			EventDto updatedTempEvent = form.getValue();
			EventFacade eventFacade = FacadeProvider.getEventFacade();
			boolean eventStatusChange = form.getEventStatusCheckBox().getValue();
			boolean eventInvestigationStatusChange = form.getEventInvestigationStatusCheckbox().getValue();
			boolean eventManagementStatusChange = form.getEventManagementStatusCheckbox().getValue();

			List<EventIndexDto> selectedEventsCpy = new ArrayList<>(selectedEvents);

			BulkOperationHandler.<EventIndexDto> forBulkEdit()
				.doBulkOperation(
					selectedEntries -> eventFacade.saveBulkEvents(
						selectedEntries.stream().map(HasUuid::getUuid).collect(Collectors.toList()),
						updatedTempEvent,
						eventStatusChange,
						eventInvestigationStatusChange,
						eventManagementStatusChange),
					selectedEventsCpy,
					bulkOperationCallback(eventGrid, popupWindow));
		});

		editView.addDiscardListener(popupWindow::close);
	}

	private Consumer<List<EventIndexDto>> bulkOperationCallback(EventGrid eventGrid, Window popupWindow) {
		return remainingEvents -> {
			if (popupWindow != null) {
				popupWindow.close();
			}

			eventGrid.reload();
			if (CollectionUtils.isNotEmpty(remainingEvents)) {
				eventGrid.asMultiSelect().selectItems(remainingEvents.toArray(new EventIndexDto[0]));
			} else {
				navigateToIndex();
			}
		};
	}

	public EventDto createNewEvent() {
		return EventDto.build(FacadeProvider.getCountryFacade().getServerCountry(), UserProvider.getCurrent().getUser());
	}

	public EventDto createNewEvent(Disease disease) {
		return EventDto.build(FacadeProvider.getCountryFacade().getServerCountry(), UserProvider.getCurrent().getUser(), disease);
	}

	public void deleteAllSelectedItems(Collection<EventIndexDto> selectedRows, EventGrid eventGrid) {
		ControllerProvider.getDeleteRestoreController()
			.deleteAllSelectedItems(selectedRows, DeleteRestoreHandlers.forEvent(), bulkOperationCallback(eventGrid, null));
	}

	public void restoreSelectedEvents(Collection<EventIndexDto> selectedRows, EventGrid eventGrid) {
		ControllerProvider.getDeleteRestoreController()
			.restoreSelectedItems(selectedRows, DeleteRestoreHandlers.forEvent(), bulkOperationCallback(eventGrid, null));
	}

	private Boolean existEventParticipantsLinkedToEvent(EventDto event) {
		List<EventParticipantDto> eventParticipantList =
			FacadeProvider.getEventParticipantFacade().getAllActiveEventParticipantsByEvent(event.getUuid());

		return !eventParticipantList.isEmpty();
	}

	public void archiveAllSelectedItems(Collection<EventIndexDto> selectedRows, EventGrid eventGrid) {
		ControllerProvider.getArchiveController()
			.archiveSelectedItems(selectedRows, ArchiveHandlers.forEvent(), bulkOperationCallback(eventGrid, null));
	}

	public void dearchiveAllSelectedItems(Collection<EventIndexDto> selectedRows, EventGrid eventGrid) {
		ControllerProvider.getArchiveController()
			.dearchiveSelectedItems(selectedRows, ArchiveHandlers.forEvent(), bulkOperationCallback(eventGrid, null));
	}

	public TitleLayout getEventViewTitleLayout(String uuid) {
		EventDto event = findEvent(uuid);

		TitleLayout titleLayout = new TitleLayout();

		titleLayout.addRow(event.getEventStatus().toString());

		if (event.getStartDate() != null) {
			String eventStartDateLabel = event.getEndDate() != null
				? DateFormatHelper.buildPeriodString(event.getStartDate(), event.getEndDate())
				: DateFormatHelper.formatDate(event.getStartDate());
			titleLayout.addRow(eventStartDateLabel);
		}

		String shortUuid = DataHelper.getShortUuid(event.getUuid());
		String eventTitle = event.getEventTitle();
		String mainRowText = StringUtils.isNotBlank(eventTitle) ? eventTitle + " (" + shortUuid + ")" : shortUuid;
		titleLayout.addMainRow(mainRowText);

		return titleLayout;
	}

	public void sendAllSelectedToExternalSurveillanceTool(Set<EventIndexDto> selectedRows, EventGrid eventGrid) {
		// Show an error when at least one selected event is not a CLUSTER event
		Optional<? extends EventIndexDto> nonClusterEvent = selectedRows.stream().filter(e -> e.getEventStatus() != EventStatus.CLUSTER).findFirst();
		if (nonClusterEvent.isPresent()) {
			Notification.show(
				String.format(
					I18nProperties.getString(Strings.errorExternalSurveillanceToolNonClusterEvent),
					DataHelper.getShortUuid(nonClusterEvent.get().getUuid()),
					I18nProperties.getEnumCaption(EventStatus.CLUSTER)),
				"",
				Type.ERROR_MESSAGE);
			return;
		}

		// Show an error when at least one selected event is not owned by this server because ownership has been handed over
		List<String> selectedUuids = selectedRows.stream().map(EventIndexDto::getUuid).collect(Collectors.toList());
		List<String> ownershipHandedOverUuids = FacadeProvider.getEventFacade().getEventUuidsWithOwnershipHandedOver(selectedUuids);
		if (CollectionUtils.isNotEmpty(ownershipHandedOverUuids)) {
			List<EventIndexDto> withoutNotSharable =
				selectedRows.stream().filter(e -> !ownershipHandedOverUuids.contains(e.getUuid())).collect(Collectors.toList());

			TextArea notShareableListComponent = new TextArea("", new ArrayList<>(ownershipHandedOverUuids).toString());
			notShareableListComponent.setWidthFull();
			notShareableListComponent.setEnabled(false);
			Label notSharableLabel = new Label(
				String.format(I18nProperties.getString(Strings.errorExternalSurveillanceToolEventNotOwned), ownershipHandedOverUuids.size()),
				ContentMode.HTML);
			notSharableLabel.addStyleName(CssStyles.LABEL_WHITE_SPACE_NORMAL);
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getCaption(Captions.ExternalSurveillanceToolGateway_send),
				new VerticalLayout(notSharableLabel, notShareableListComponent),
				String.format(
					I18nProperties.getCaption(Captions.ExternalSurveillanceToolGateway_excludeAndSend),
					withoutNotSharable.size(),
					selectedRows.size()),
				I18nProperties.getCaption(Captions.actionCancel),
				800,
				(confirmed) -> {
					if (confirmed) {
						ExternalSurveillanceServiceGateway
							.sendEventsToExternalSurveillanceTool(withoutNotSharable, false, bulkOperationCallback(eventGrid, null));
					}
				});
		} else {
			ExternalSurveillanceServiceGateway.sendEventsToExternalSurveillanceTool(selectedRows, true, bulkOperationCallback(eventGrid, null));
		}
	}

	public void linkAllToGroup(Set<EventIndexDto> selectedItems, EventGrid eventGrid) {
		ControllerProvider.getEventGroupController().linkAllToGroup(selectedItems, bulkOperationCallback(eventGrid, null));
	}
}
