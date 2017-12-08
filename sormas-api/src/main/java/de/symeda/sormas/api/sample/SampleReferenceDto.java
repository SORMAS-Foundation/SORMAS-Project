package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.ReferenceDto;

public class SampleReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -6975445672442728938L;

	public SampleReferenceDto() {
		
	}
	
	public SampleReferenceDto(String uuid) {
		setUuid(uuid);
	}
	
	public SampleReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}
	
}
