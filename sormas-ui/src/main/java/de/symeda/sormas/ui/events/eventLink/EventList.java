/*
 * ******************************************************************************
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * *
 * * This program is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.symeda.sormas.ui.events.eventLink;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.PaginationList;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class EventList extends PaginationList<EventIndexDto> {

	private final EventCriteria eventCriteria = new EventCriteria();
	private final Label noEventLabel;
	private BiConsumer<Integer, EventListEntry> addUnlinkEventListener;
	private final Consumer<Runnable> actionCallback;
	private final boolean isEditAllowed;

	public EventList(CaseReferenceDto caseReferenceDto, Consumer<Runnable> actionCallback, boolean isEditAllowed) {

		super(5);
		this.actionCallback = actionCallback;
		this.isEditAllowed = isEditAllowed;
		eventCriteria.caze(caseReferenceDto);
		eventCriteria.setUserFilterIncluded(false);
		noEventLabel = new Label(I18nProperties.getCaption(Captions.eventNoEventLinkedToCase));
		addUnlinkEventListener = (Integer i, EventListEntry listEntry) -> {
			if (UiUtil.permitted(isEditAllowed, UserRight.EVENT_EDIT, UserRight.CASE_EDIT)) {
				listEntry.addUnlinkEventListener(i, (ClickListener) clickEvent -> {
					VaadinUiUtil.showConfirmationPopup(
						I18nProperties.getString(Strings.headingUnlinkCaseFromEvent),
						new Label(I18nProperties.getString(Strings.confirmationUnlinkCaseFromEvent)),
						I18nProperties.getString(Strings.yes),
						I18nProperties.getString(Strings.no),
						480,
						confirmed -> {
							if (confirmed) {
								EventDto selectedEvent = FacadeProvider.getEventFacade().getEventByUuid(listEntry.getEvent().getUuid(), false);
								CaseDataDto caseDataDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(caseReferenceDto.getUuid());
								ControllerProvider.getEventController()
									.removeLinkCaseEventParticipant(
										selectedEvent,
										caseDataDto,
										I18nProperties.getString(Strings.messageEventParticipationUnlinked));
								reload();
							}
						});

				});
			}
		};
	}

	public EventList(ContactDto contact, Consumer<Runnable> actionCallback, boolean isEditAllowed) {

		super(5);
		this.actionCallback = actionCallback;
		this.isEditAllowed = isEditAllowed;
		eventCriteria.setPerson(contact.getPerson());
		eventCriteria.setUserFilterIncluded(false);
		noEventLabel = new Label(I18nProperties.getCaption(Captions.eventNoEventLinkedToContact));
		addUnlinkEventListener = (Integer i, EventListEntry listEntry) -> {
			if (contact.getPerson() != null && UiUtil.permitted(isEditAllowed, UserRight.EVENTPARTICIPANT_DELETE)) {
				listEntry.addUnlinkEventListener(
					i,
					(ClickListener) clickEvent -> ControllerProvider.getEventParticipantController()
						.deleteEventParticipant(listEntry.getEvent().getUuid(), contact.getPerson().getUuid(), this::reload));
			}
		};
	}

	public EventList(EventReferenceDto superordinateEvent, Consumer<Runnable> actionCallback, boolean isEditAllowed) {

		super(5);
		this.actionCallback = actionCallback;
		this.isEditAllowed = isEditAllowed;
		eventCriteria.superordinateEvent(superordinateEvent);
		eventCriteria.setUserFilterIncluded(false);
		noEventLabel = new Label(I18nProperties.getString(Strings.infoNoSubordinateEvents));
		addUnlinkEventListener = (Integer i, EventListEntry listEntry) -> {
			if (UiUtil.permitted(UserRight.EVENT_EDIT)) {
				listEntry.addUnlinkEventListener(i, (ClickListener) clickEvent -> {
					EventDto selectedEvent = FacadeProvider.getEventFacade().getEventByUuid(listEntry.getEvent().getUuid(), false);
					ControllerProvider.getEventController()
						.removeSuperordinateEvent(selectedEvent, false, I18nProperties.getString(Strings.messageEventSubordinateEventUnlinked));
					reload();
				});
			}
		};
	}

	@Override
	public void reload() {

		List<EventIndexDto> events = FacadeProvider.getEventFacade().getIndexList(eventCriteria, 0, maxDisplayedEntries * 20, null);

		setEntries(events);
		if (!events.isEmpty()) {
			showPage(1);
		} else {
			listLayout.removeAllComponents();
			updatePaginationLayout();
			listLayout.addComponent(noEventLabel);
		}
	}

	@Override
	protected void drawDisplayedEntries() {

		List<EventIndexDto> displayedEntries = getDisplayedEntries();
		for (int i = 0, displayedEntriesSize = displayedEntries.size(); i < displayedEntriesSize; i++) {
			EventIndexDto event = displayedEntries.get(i);
			EventListEntry listEntry = new EventListEntry(event);

			if (UiUtil.permitted(UserRight.EVENT_EDIT)) {
				if (addUnlinkEventListener != null) {
					addUnlinkEventListener.accept(i, listEntry);
				}
			}
			listEntry.addActionButton(
				String.valueOf(i),
				(ClickListener) clickEvent -> actionCallback
					.accept(() -> ControllerProvider.getEventController().navigateToData(listEntry.getEvent().getUuid())),
				UiUtil.permitted(isEditAllowed, UserRight.EVENT_EDIT));
			listEntry.setEnabled(isEditAllowed);
			listLayout.addComponent(listEntry);
		}
	}

	protected void filterEventListByCase(CaseReferenceDto caseRef) {
		eventCriteria.caze(caseRef);
	}
}
