package de.symeda.sormas.api.event;

import de.symeda.sormas.api.ReferenceDto;

public class EventReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = 2430932452606853497L;

	public EventReferenceDto() {
		
	}
	
	public EventReferenceDto(String uuid) {
		setUuid(uuid);
	}
	
	public EventReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}
	
}
