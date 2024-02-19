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
import java.util.Set;

import javax.ejb.Remote;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.CoreFacade;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.externaldata.ExternalDataDto;
import de.symeda.sormas.api.externaldata.ExternalDataUpdateException;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface EventFacade extends CoreFacade<EventDto, EventIndexDto, EventReferenceDto, EventCriteria> {

	EventDto getEventByUuid(String uuid, boolean detailedReferences);

	EventReferenceDto getReferenceByEventParticipant(String uuid);

	List<String> getAllActiveUuids();

	List<EventDto> getAllByCase(CaseDataDto caseDataDto);

	Page<EventIndexDto> getIndexPage(@NotNull EventCriteria eventCriteria, Integer offset, Integer size, List<SortProperty> sortProperties);

	List<EventExportDto> getExportList(EventCriteria eventCriteria, Collection<String> selectedRows, Integer first, Integer max);

	List<String> getDeletedUuidsSince(Date since);

	void archiveAllArchivableEvents(int daysAfterEventsGetsArchived);

	ProcessedEntity dearchive(String entityUuid, String dearchiveReason);

	boolean doesExternalTokenExist(String externalToken, String eventUuid);

	String getUuidByCaseUuidOrPersonUuid(String value);

	Set<String> getAllSubordinateEventUuids(String eventUuid);

	Set<String> getAllSuperordinateEventUuids(String eventUuid);

	Set<String> getAllEventUuidsByEventGroupUuid(String eventGroupUuid);

	List<String> getEventUuidsWithOwnershipHandedOver(List<String> eventUuids);

	Set<RegionReferenceDto> getAllRegionsRelatedToEventUuids(List<String> uuids);

	void updateExternalData(@Valid List<ExternalDataDto> externalData) throws ExternalDataUpdateException;

	List<String> getSubordinateEventUuids(List<String> uuids);

	boolean hasRegionAndDistrict(String eventUuid);

	boolean hasAnyEventParticipantWithoutJurisdiction(String eventUuid);

	List<ProcessedEntity> saveBulkEvents(
		List<String> eventUuidList,
		EventDto updatedTempEvent,
		boolean eventStatusChange,
		boolean eventInvestigationStatusChange,
		boolean eventManagementStatusChange);

	boolean isInJurisdictionOrOwned(String uuid);

	boolean hasParticipantWithSpecialAccess(EventReferenceDto eventRef);
}
