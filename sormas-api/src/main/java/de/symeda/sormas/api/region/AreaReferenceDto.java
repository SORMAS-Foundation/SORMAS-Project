package de.symeda.sormas.api.region;

import de.symeda.sormas.api.ReferenceDto;

public class AreaReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -6241927331721175673L;

	public AreaReferenceDto() {

	}

	public AreaReferenceDto(String uuid) {
		setUuid(uuid);
	}

	public AreaReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}

}
