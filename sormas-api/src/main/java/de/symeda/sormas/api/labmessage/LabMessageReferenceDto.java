package de.symeda.sormas.api.labmessage;

import de.symeda.sormas.api.ReferenceDto;

public class LabMessageReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -4467135674568277340L;

	public LabMessageReferenceDto() {

	}

	public LabMessageReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}

	public LabMessageReferenceDto(String uuid) {
		setUuid(uuid);
	}
}
