/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.event.eventimport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventGroupReferenceDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.user.UserReferenceDto;

public class EventImportEntities implements Serializable {

	private static final long serialVersionUID = -4565794925738392508L;

	private final EventDto event;
	private final List<EventParticipantDto> eventParticipants;
	private final List<EventGroupReferenceDto> eventGroupReferences;

	public EventImportEntities(UserReferenceDto reportingUser) {
		event = createEvent(reportingUser);

		eventParticipants = new ArrayList<>();
		eventGroupReferences = new ArrayList<>();
	}

	public EventImportEntities(EventDto event) {
		this.event = event;

		eventParticipants = new ArrayList<>();
		eventGroupReferences = new ArrayList<>();
	}

	public static EventDto createEvent(UserReferenceDto reportingUser) {
		EventDto event = EventDto.build();
		event.setReportingUser(reportingUser);

		return event;
	}

	public EventDto getEvent() {
		return event;
	}

	public List<EventParticipantDto> getEventParticipants() {
		return eventParticipants;
	}

	public List<EventGroupReferenceDto> getEventGroupReferences() {
		return eventGroupReferences;
	}
}
