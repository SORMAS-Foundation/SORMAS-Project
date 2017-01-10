package de.symeda.sormas.api.event;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface EventFacade {
	
	List<EventDto> getAllEventsAfter(Date date, String userUuid);
	
	EventDto getEventByUuid(String uuid);
	
	EventDto saveEvent(EventDto dto);
	
	EventReferenceDto getReferenceByUuid(String uuid);

}
