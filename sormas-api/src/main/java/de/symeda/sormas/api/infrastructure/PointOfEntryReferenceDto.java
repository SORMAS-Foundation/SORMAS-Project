package de.symeda.sormas.api.infrastructure;

import de.symeda.sormas.api.InfrastructureDataReferenceDto;

public class PointOfEntryReferenceDto extends InfrastructureDataReferenceDto {

	private static final long serialVersionUID = 4124483408068181854L;

	private PointOfEntryType pointOfEntryType;

	public PointOfEntryReferenceDto() {
	}

	public PointOfEntryReferenceDto(String uuid) {
		super(uuid);
	}

	public PointOfEntryReferenceDto(String uuid, String caption, PointOfEntryType pointOfEntryType, String externalId) {
		super(uuid, caption, externalId);
		this.pointOfEntryType = pointOfEntryType;
	}

	public PointOfEntryType getPointOfEntryType() {
		return pointOfEntryType;
	}

	public boolean isOtherPointOfEntry() {
		return PointOfEntryDto.OTHER_AIRPORT_UUID.equals(getUuid())
			|| PointOfEntryDto.OTHER_SEAPORT_UUID.equals(getUuid())
			|| PointOfEntryDto.OTHER_GROUND_CROSSING_UUID.equals(getUuid())
			|| PointOfEntryDto.OTHER_POE_UUID.equals(getUuid());
	}
}
