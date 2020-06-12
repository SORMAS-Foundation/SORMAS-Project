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

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface EventFacade {

	List<EventDto> getAllActiveEventsAfter(Date date);

	List<DashboardEventDto> getNewEventsForDashboard(EventCriteria eventCriteria);

	Map<Disease, Long> getEventCountByDisease(EventCriteria eventCriteria);

	Map<EventStatus, Long> getEventCountByStatus(EventCriteria eventCriteria);

	EventDto getEventByUuid(String uuid);

	EventDto saveEvent(EventDto dto);

	EventReferenceDto getReferenceByUuid(String uuid);

	List<String> getAllActiveUuids();

	List<EventDto> getByUuids(List<String> uuids);

	void deleteEvent(String eventUuid);

	long count(EventCriteria eventCriteria);

	List<EventIndexDto> getIndexList(EventCriteria eventCriteria, Integer first, Integer max, List<SortProperty> sortProperties);

	boolean isArchived(String caseUuid);

	boolean isDeleted(String eventUuid);

	void archiveOrDearchiveEvent(String eventUuid, boolean archive);

	List<String> getArchivedUuidsSince(Date since);

	List<String> getDeletedUuidsSince(Date since);

	void archiveAllArchivableEvents(int daysAfterEventsGetsArchived);

	String getUuidByCaseUuidOrPersonUuid(String value);
}
