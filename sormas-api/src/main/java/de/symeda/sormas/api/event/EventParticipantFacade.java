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
import java.util.Map;

import javax.ejb.Remote;
import javax.validation.Valid;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface EventParticipantFacade {

	List<EventParticipantDto> getAllEventParticipantsByEventAfter(Date date, String eventUuid);

	List<EventParticipantDto> getAllActiveEventParticipantsByEvent(String eventUuid);

	List<EventParticipantDto> getAllActiveEventParticipantsAfter(Date date);

	EventParticipantDto getEventParticipantByUuid(String uuid);

	EventParticipantDto saveEventParticipant(@Valid EventParticipantDto dto);

	List<String> getAllActiveUuids();

	List<EventParticipantDto> getByUuids(List<String> uuids);

	void deleteEventParticipant(EventParticipantReferenceDto eventParticipantRef);

	List<EventParticipantIndexDto> getIndexList(
		EventParticipantCriteria eventParticipantCriteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties);

	List<EventParticipantListEntryDto> getListEntries(
		EventParticipantCriteria eventParticipantCriteria,
		Integer first,
		Integer max);

	EventParticipantDto getByUuid(String uuid);

	void validate(EventParticipantDto eventParticipant);

	long count(EventParticipantCriteria eventParticipantCriteria);

	Map<String, Long> getContactCountPerEventParticipant(List<String> eventParticipantUuids, EventParticipantCriteria eventParticipantCriteria);

	boolean exists(String uuid);

	EventParticipantReferenceDto getReferenceByUuid(String uuid);

	EventParticipantReferenceDto getReferenceByEventAndPerson(String eventUuid, String personUuid);

	List<String> getDeletedUuidsSince(Date date);

	boolean isEventParticipantEditAllowed(String uuid);

	EventParticipantDto getFirst(EventParticipantCriteria eventParticipantCriteria);

	List<EventParticipantExportDto> getExportList(EventParticipantCriteria eventParticipantCriteria, int first, int max, Language userLanguage);

	List<EventParticipantDto> getByEventUuids(List<String> eventUuids);

	List<SimilarEventParticipantDto> getMatchingEventParticipants(EventParticipantCriteria criteria);
}
