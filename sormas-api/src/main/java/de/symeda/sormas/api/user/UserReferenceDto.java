package de.symeda.sormas.api.user;

import de.symeda.sormas.api.ReferenceDto;

public class UserReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -8558187171374254398L;

	public UserReferenceDto() {
		
	}
	
	public UserReferenceDto(String uuid) {
		setUuid(uuid);
	}
	
	public UserReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}
	
}
