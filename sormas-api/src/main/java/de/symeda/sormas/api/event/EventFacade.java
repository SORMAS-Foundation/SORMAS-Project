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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.event;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface EventFacade {
	
	List<EventDto> getAllActiveEventsAfter(Date date, String userUuid);
	
	List<DashboardEventDto> getNewEventsForDashboard(EventCriteria eventCriteria, String userUuid);
	
	Map<Disease, Long> getEventCountByDisease(EventCriteria eventCriteria, String userUuid);
	
	Map<EventStatus, Long> getEventCountByStatus(EventCriteria eventCriteria, String userUuid);
	
	EventDto getEventByUuid(String uuid);
	
	EventDto saveEvent(EventDto dto);
	
	EventReferenceDto getReferenceByUuid(String uuid);

	List<String> getAllActiveUuids(String userUuid);

	List<EventDto> getByUuids(List<String> uuids);

	void deleteEvent(String eventUuid, String userUuid);

	long count(String userUuid, EventCriteria eventCriteria);

	List<EventIndexDto> getIndexList(String userUuid, EventCriteria eventCriteria, Integer first, Integer max,
			List<SortProperty> sortProperties);
	
	boolean isArchived(String caseUuid);
	
	boolean isDeleted(String eventUuid);
	
	void archiveOrDearchiveEvent(String eventUuid, boolean archive);
	
	List<String> getArchivedUuidsSince(String userUuid, Date since);
	
	List<String> getDeletedUuidsSince(String userUuid, Date since);
	
}
