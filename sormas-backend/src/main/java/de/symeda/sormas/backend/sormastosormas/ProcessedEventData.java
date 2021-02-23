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

package de.symeda.sormas.backend.sormastosormas;

import java.util.List;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;

public class ProcessedEventData extends ProcessedData<EventDto> {

	private static final long serialVersionUID = -7001698437353666024L;

	private final List<EventParticipantDto> eventParticipants;

	public ProcessedEventData(EventDto entity, SormasToSormasOriginInfoDto originInfoDto, List<EventParticipantDto> eventParticipants) {
		super(entity, originInfoDto);
		this.eventParticipants = eventParticipants;
	}

	public List<EventParticipantDto> getEventParticipants() {
		return eventParticipants;
	}
}
