package de.symeda.sormas.api.facility;

import de.symeda.sormas.api.ReferenceDto;

public class FacilityReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -7987228795475507196L;

	public FacilityReferenceDto() {
		
	}
	
	public FacilityReferenceDto(String uuid) {
		setUuid(uuid);
	}
	
	public FacilityReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}
	
}
