package de.symeda.sormas.api.region;

import de.symeda.sormas.api.ReferenceDto;

public class DistrictReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = 8990957700033431836L;

	public DistrictReferenceDto() {
		
	}
	
	public DistrictReferenceDto(String uuid) {
		setUuid(uuid);
	}
	
	public DistrictReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}
	
}
