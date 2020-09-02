package de.symeda.sormas.api.therapy;

import de.symeda.sormas.api.ReferenceDto;

public class TreatmentReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = 816932182306785932L;

	public TreatmentReferenceDto() {

	}

	public TreatmentReferenceDto(String uuid) {
		setUuid(uuid);
	}

	public TreatmentReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}
}
