package de.symeda.sormas.api.event;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;

@Remote
public interface EventFacade {
	
	List<EventDto> getAllActiveEventsAfter(Date date, String userUuid);
	
	List<EventDto> getAllEventsBetween(Date fromDate, Date toDate, DistrictReferenceDto districtRef, Disease disease, String userUuid);
	
	List<DashboardEventDto> getNewEventsForDashboard(RegionReferenceDto regionRef, DistrictReferenceDto districtRef, Disease disease, Date from, Date to, String userUuid);
	
	EventDto getEventByUuid(String uuid);
	
	EventDto saveEvent(EventDto dto);
	
	List<EventReferenceDto> getSelectableEvents(UserReferenceDto user);
	
	EventReferenceDto getReferenceByUuid(String uuid);

	List<String> getAllActiveUuids(String userUuid);

	List<EventDto> getByUuids(List<String> uuids);

	void deleteEvent(EventReferenceDto eventRef, String userUuid);
	
	List<EventIndexDto> getIndexList(String userUuid, EventCriteria eventCriteria);
	
	boolean isArchived(String caseUuid);
	
	void archiveOrDearchiveEvent(String eventUuid, boolean archive);
	
	List<String> getArchivedUuidsSince(String userUuid, Date since);
}
