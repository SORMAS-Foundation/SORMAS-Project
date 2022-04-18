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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Remote;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.CoreFacade;
import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface EventParticipantFacade
	extends CoreFacade<EventParticipantDto, EventParticipantIndexDto, EventParticipantReferenceDto, EventParticipantCriteria> {

	List<EventParticipantDto> getAllEventParticipantsByEventAfter(Date date, String eventUuid);

	List<EventParticipantDto> getAllActiveEventParticipantsByEvent(String eventUuid);

	List<EventParticipantDto> getAllActiveEventParticipantsAfter(Date date);

	EventParticipantDto getEventParticipantByUuid(String uuid);

	EventParticipantDto save(@Valid @NotNull EventParticipantDto dto);

	List<String> getAllActiveUuids();

	Page<EventParticipantIndexDto> getIndexPage(
		EventParticipantCriteria eventParticipantCriteria,
		Integer offset,
		Integer size,
		List<SortProperty> sortProperties);

	List<EventParticipantListEntryDto> getListEntries(EventParticipantCriteria eventParticipantCriteria, Integer first, Integer max);

	void validate(EventParticipantDto eventParticipant);

	Map<String, Long> getContactCountPerEventParticipant(List<String> eventParticipantUuids, EventParticipantCriteria eventParticipantCriteria);

	boolean exists(String personUuid, String eventUUID);

	EventParticipantReferenceDto getReferenceByEventAndPerson(String eventUuid, String personUuid);

	List<EventParticipantDto> getAllActiveEventParticipantsAfter(Date date, Integer batchSize, String lastSynchronizedUuid);

	List<String> getArchivedUuidsSince(Date since);

	List<String> getDeletedUuidsSince(Date date);

	EditPermissionType isEventParticipantEditAllowed(String uuid);

	EventParticipantDto getFirst(EventParticipantCriteria eventParticipantCriteria);

	List<EventParticipantExportDto> getExportList(
		EventParticipantCriteria eventParticipantCriteria,
		Collection<String> selectedRows,
		int first,
		int max,
		Language userLanguage,
		ExportConfigurationDto exportConfiguration);

	List<EventParticipantDto> getByEventUuids(List<String> eventUuids);

	List<SimilarEventParticipantDto> getMatchingEventParticipants(EventParticipantCriteria criteria);

	List<EventParticipantDto> getByPersonUuids(List<String> personUuids);

	List<EventParticipantDto> getByEventAndPersons(String eventUuid, List<String> personUuids);
}
