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
import java.util.Set;

import javax.ejb.Remote;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.externaldata.ExternalDataDto;
import de.symeda.sormas.api.externaldata.ExternalDataUpdateException;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolException;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

@Remote
public interface EventFacade {

	List<EventDto> getAllActiveEventsAfter(Date date);

	Map<Disease, Long> getEventCountByDisease(EventCriteria eventCriteria);

	EventDto getEventByUuid(String uuid);

	EventDto saveEvent(@Valid @NotNull EventDto dto);

	EventReferenceDto getReferenceByUuid(String uuid);

	EventReferenceDto getReferenceByEventParticipant(String uuid);

	List<String> getAllActiveUuids();

	List<EventDto> getByUuids(List<String> uuids);

	void deleteEvent(String eventUuid) throws ExternalSurveillanceToolException;

	long count(EventCriteria eventCriteria);

	List<EventIndexDto> getIndexList(EventCriteria eventCriteria, Integer first, Integer max, List<SortProperty> sortProperties);

	Page<EventIndexDto> getIndexPage(EventCriteria eventCriteria, Integer offset, Integer size, List<SortProperty> sortProperties);

	List<EventExportDto> getExportList(EventCriteria eventCriteria, Collection<String> selectedRows, Integer first, Integer max);

	boolean isArchived(String caseUuid);

	boolean isDeleted(String eventUuid);

	void archiveOrDearchiveEvent(String eventUuid, boolean archive);

	List<String> getArchivedUuidsSince(Date since);

	List<String> getDeletedUuidsSince(Date since);

	void archiveAllArchivableEvents(int daysAfterEventsGetsArchived);

	Boolean isEventEditAllowed(String eventUuid);

	boolean exists(String uuid);

	boolean doesExternalTokenExist(String externalToken, String eventUuid);

	String getUuidByCaseUuidOrPersonUuid(String value);

	Set<String> getAllSubordinateEventUuids(String eventUuid);

	Set<String> getAllSuperordinateEventUuids(String eventUuid);

	Set<String> getAllEventUuidsByEventGroupUuid(String eventGroupUuid);

	String getFirstEventUuidWithOwnershipHandedOver(List<String> eventUuids);

	void validate(EventDto dto) throws ValidationRuntimeException;

	Set<RegionReferenceDto> getAllRegionsRelatedToEventUuids(List<String> uuids);

	void updateExternalData(List<ExternalDataDto> externalData) throws ExternalDataUpdateException;
}
