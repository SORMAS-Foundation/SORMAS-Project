package de.symeda.sormas.api.infrastructure;

import de.symeda.sormas.api.ReferenceDto;

public class PointOfEntryReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = 4124483408068181854L;

	public PointOfEntryReferenceDto() {

	}

	public PointOfEntryReferenceDto(String uuid) {
		setUuid(uuid);
	}

	public PointOfEntryReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}

	public boolean isOtherPointOfEntry() {
		return PointOfEntryDto.OTHER_AIRPORT_UUID.equals(getUuid())
			|| PointOfEntryDto.OTHER_SEAPORT_UUID.equals(getUuid())
			|| PointOfEntryDto.OTHER_GROUND_CROSSING_UUID.equals(getUuid())
			|| PointOfEntryDto.OTHER_POE_UUID.equals(getUuid());
	}
}
