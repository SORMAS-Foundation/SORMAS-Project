package de.symeda.sormas.api.event;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface EventParticipantFacade {
	
	List<EventParticipantDto> getAllEventParticipantsByEventAfter(Date date, String eventUuid, String userUuid);

	List<EventParticipantDto> getAllActiveEventParticipantsAfter(Date date, String userUuid);

	EventParticipantDto getEventParticipantByUuid(String uuid);
	
	EventParticipantDto saveEventParticipant(EventParticipantDto dto);

	List<String> getAllActiveUuids(String userUuid);

	List<EventParticipantDto> getByUuids(List<String> uuids);

	void deleteEventParticipant(EventParticipantReferenceDto eventParticipantRef, String userUuid);
}
