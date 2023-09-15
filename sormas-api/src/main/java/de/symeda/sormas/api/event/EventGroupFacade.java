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

import java.util.List;
import java.util.Set;

import javax.ejb.Remote;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface EventGroupFacade {

	EventGroupReferenceDto getReferenceByUuid(String uuid);

	boolean exists(String uuid);

	boolean isArchived(String uuid);

	EventGroupDto getEventGroupByUuid(String uuid);

	List<EventGroupReferenceDto> getCommonEventGroupsByEvents(List<EventReferenceDto> eventReferences);

	List<EventGroupIndexDto> getIndexList(EventGroupCriteria eventGroupCriteria, Integer first, Integer max, List<SortProperty> sortProperties);

	long count(EventGroupCriteria eventGroupCriteria);

	Page<EventGroupIndexDto> getIndexPage(
		@NotNull EventGroupCriteria eventGroupCriteria,
		Integer offset,
		Integer size,
		List<SortProperty> sortProperties);

	EventGroupDto saveEventGroup(@Valid @NotNull EventGroupDto eventGroup);

	void linkEventToGroup(EventReferenceDto eventReference, EventGroupReferenceDto eventGroupReference);

	void linkEventToGroups(EventReferenceDto eventReference, List<EventGroupReferenceDto> eventGroupReferences);

	void linkEventsToGroup(List<EventReferenceDto> eventReferences, EventGroupReferenceDto eventGroupReference);

	List<ProcessedEntity> linkEventsToGroups(List<String> eventUuids, List<String> eventGroupReferences, List<String> alreadyLinkedEventUuidsToGroup);

	void unlinkEventGroup(EventReferenceDto eventReference, EventGroupReferenceDto eventGroupReference);

	void deleteEventGroup(String uuid);

	void archiveOrDearchiveEventGroup(String uuid, boolean archive);

	List<RegionReferenceDto> getEventGroupRelatedRegions(String uuid);

	void notifyEventEventGroupCreated(EventGroupReferenceDto eventGroupReference);

	void notifyEventAddedToEventGroup(EventGroupReferenceDto eventGroupReference, List<EventReferenceDto> eventReferences);

	void notifyEventAddedToEventGroup(String eventGroupUuid, Set<String> eventUuids);

	void notifyEventRemovedFromEventGroup(EventGroupReferenceDto eventGroupReference, List<EventReferenceDto> eventReferences);

	List<String> getAlreadyLinkedEventUuidsToGroup(List<String> eventUuids, List<String> eventGroupUuids);
}
