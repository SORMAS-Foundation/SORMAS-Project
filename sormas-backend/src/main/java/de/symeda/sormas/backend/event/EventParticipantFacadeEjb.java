package de.symeda.sormas.backend.event;

import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantFacade;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.backend.util.DtoHelper;

public class EventParticipantFacadeEjb implements EventParticipantFacade {
	
	public static EventParticipantReferenceDto toReferenceDto(EventParticipant entity) {
		if(entity == null) {
			return null;
		}
		
		EventParticipantDto dto = new EventParticipantDto();
		DtoHelper.fillReferenceDto(dto, entity);
		return dto;
	}

}
