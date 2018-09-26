package de.symeda.sormas.api.region;

import de.symeda.sormas.api.ReferenceDto;

public class CommunityReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -8833267932522978860L;

	public CommunityReferenceDto() {
		
	}
	
	public CommunityReferenceDto(String uuid) {
		setUuid(uuid);
	}
	
	public CommunityReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}
	
}
