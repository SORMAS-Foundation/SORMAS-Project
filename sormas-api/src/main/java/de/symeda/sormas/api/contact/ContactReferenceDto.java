package de.symeda.sormas.api.contact;

import de.symeda.sormas.api.ReferenceDto;

public class ContactReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -7764607075875188799L;
	
	public ContactReferenceDto() {
		
	}
	
	public ContactReferenceDto(String uuid) {
		setUuid(uuid);
	}
	
	public ContactReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}

}
