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

import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.PaginationList;

public class EventList extends PaginationList<EventIndexDto> {

	private final EventCriteria eventCriteria = new EventCriteria();
	private final Label noEventLabel;
	private BiConsumer<Integer, EventListEntry> addUnlinkEventListener;

	public EventList(CaseReferenceDto caseReferenceDto) {
		super(5);
		eventCriteria.caze(caseReferenceDto);
		eventCriteria.setUserFilterIncluded(false);
		noEventLabel = new Label(I18nProperties.getCaption(Captions.eventNoEventLinkedToCase));
	}

	public EventList(PersonReferenceDto personRef) {
		super(5);
		eventCriteria.setPerson(personRef);
		eventCriteria.setUserFilterIncluded(false);
		noEventLabel = new Label(I18nProperties.getCaption(Captions.eventNoEventLinkedToCase));
		addUnlinkEventListener = (Integer i, EventListEntry listEntry) -> {
			UserProvider user = UserProvider.getCurrent();
			if (personRef != null && user.hasUserRight(UserRight.EVENTPARTICIPANT_DELETE)) {
				listEntry.addUnlinkEventListener(
					i,
					(ClickListener) clickEvent -> ControllerProvider.getEventParticipantController()
						.deleteEventParticipant(listEntry.getEvent().getUuid(), personRef.getUuid(), this::reload));
			}
		};
	}

	public EventList(EventReferenceDto superordinateEvent) {
		super(5);
		eventCriteria.superordinateEvent(superordinateEvent);
		eventCriteria.setUserFilterIncluded(false);
		noEventLabel = new Label(I18nProperties.getString(Strings.infoNoSubordinateEvents));
		addUnlinkEventListener = (Integer i, EventListEntry listEntry) -> {
			if (UserProvider.getCurrent().hasUserRight(UserRight.EVENT_EDIT)) {
				listEntry.addUnlinkEventListener(i, (ClickListener) clickEvent -> {
					EventDto selectedEvent = FacadeProvider.getEventFacade().getEventByUuid(listEntry.getEvent().getUuid());
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

			UserProvider user = UserProvider.getCurrent();
			if (user.hasUserRight(UserRight.EVENT_EDIT)) {
				if (addUnlinkEventListener != null) {
					addUnlinkEventListener.accept(i, listEntry);
				}
				listEntry.addEditListener(
					i,
					(ClickListener) clickEvent -> ControllerProvider.getEventController().navigateToData(listEntry.getEvent().getUuid()));
			}
			listLayout.addComponent(listEntry);
		}
	}

	protected void filterEventListByCase(CaseReferenceDto caseRef) {
		eventCriteria.caze(caseRef);
	}
}
