package de.symeda.sormas.api.region;

import de.symeda.sormas.api.ReferenceDto;

public class RegionReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -1610675328037466348L;

	public RegionReferenceDto() {
		
	}
	
	public RegionReferenceDto(String uuid) {
		setUuid(uuid);
	}
	
	public RegionReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}
	
}
