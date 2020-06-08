package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.ReferenceDto;

public class AdditionalTestReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -7306267901413644171L;

	public AdditionalTestReferenceDto() {

	}

	public AdditionalTestReferenceDto(String uuid) {
		setUuid(uuid);
	}

	public AdditionalTestReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}
}
