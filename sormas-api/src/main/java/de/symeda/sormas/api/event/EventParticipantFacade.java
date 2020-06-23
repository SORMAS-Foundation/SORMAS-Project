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
package de.symeda.sormas.api.event;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface EventParticipantFacade {

	List<EventParticipantDto> getAllEventParticipantsByEventAfter(Date date, String eventUuid);

	List<EventParticipantDto> getAllActiveEventParticipantsAfter(Date date);

	EventParticipantDto getEventParticipantByUuid(String uuid);

	EventParticipantDto saveEventParticipant(EventParticipantDto dto);

	List<String> getAllActiveUuids();

	List<EventParticipantDto> getByUuids(List<String> uuids);

	void deleteEventParticipant(EventParticipantReferenceDto eventParticipantRef);

	List<EventParticipantIndexDto> getIndexList(
		EventParticipantCriteria eventParticipantCriteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties);

	void validate(EventParticipantDto eventParticipant);

	long count(EventParticipantCriteria eventParticipantCriteria);
}
