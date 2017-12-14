package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.ReferenceDto;

public class CaseReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = 5007131477733638086L;
	
	public CaseReferenceDto() {
		
	}
	
	public CaseReferenceDto(String uuid) {
		setUuid(uuid);
	}
	
	public CaseReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}

	public CaseReferenceDto(String uuid, String firstName, String lastName) {
		setUuid(uuid);
		setCaption(CaseDataDto.buildCaption(uuid, firstName, lastName));
	}
	
}
