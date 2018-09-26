package de.symeda.sormas.api.visit;

import de.symeda.sormas.api.ReferenceDto;

public class VisitReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -441664767075414789L;

	public VisitReferenceDto() {
		
	}
	
	public VisitReferenceDto(String uuid) {
		setUuid(uuid);
	}
	
	public VisitReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}
	
}
