package de.symeda.sormas.api.event;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface EventParticipantFacade {
	
	List<EventParticipantDto> getAllEventParticipantsByEventAfter(Date date, EventReferenceDto eventRef, String userUuid);

	EventParticipantDto getEventParticipantByUuid(String uuid);
	
	EventParticipantDto saveEventParticipant(EventParticipantDto dto);
	
}
