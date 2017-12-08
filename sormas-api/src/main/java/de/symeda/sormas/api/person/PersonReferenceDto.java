package de.symeda.sormas.api.person;

import de.symeda.sormas.api.ReferenceDto;

public class PersonReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -8558187171374254398L;

	public PersonReferenceDto() {
		
	}
	
	public PersonReferenceDto(String uuid) {
		setUuid(uuid);
	}
	
	public PersonReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}
	
}
