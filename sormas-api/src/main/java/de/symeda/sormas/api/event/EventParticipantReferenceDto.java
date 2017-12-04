package de.symeda.sormas.api.event;

import de.symeda.sormas.api.ReferenceDto;

public class EventParticipantReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -8725734604520880084L;

	public EventParticipantReferenceDto() {
		
	}
	
	public EventParticipantReferenceDto(String uuid) {
		setUuid(uuid);
	}
	
	public EventParticipantReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}
	
}
