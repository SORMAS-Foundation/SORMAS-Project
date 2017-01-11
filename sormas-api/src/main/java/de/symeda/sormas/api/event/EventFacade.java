package de.symeda.sormas.api.event;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;

@Remote
public interface EventFacade {
	
	List<EventDto> getAllEventsAfter(Date date, String userUuid);
	
	EventDto getEventByUuid(String uuid);
	
	EventDto saveEvent(EventDto dto);
	
	List<EventReferenceDto> getSelectableEvents(UserReferenceDto user);
	
	EventReferenceDto getReferenceByUuid(String uuid);

}
