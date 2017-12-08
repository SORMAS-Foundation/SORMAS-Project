package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.ReferenceDto;

public class SampleTestReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -5213210080802372054L;

	public SampleTestReferenceDto() {
		
	}
	
	public SampleTestReferenceDto(String uuid) {
		setUuid(uuid);
	}
	
	public SampleTestReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}
	
}
