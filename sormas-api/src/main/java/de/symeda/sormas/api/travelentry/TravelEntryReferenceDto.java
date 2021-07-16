package de.symeda.sormas.api.travelentry;

import de.symeda.sormas.api.ReferenceDto;

public class TravelEntryReferenceDto extends ReferenceDto {

	private String externalId;

	public TravelEntryReferenceDto(String uuid, String caption, String externalId) {
		super(uuid, caption);
		this.externalId = externalId;
	}

	public String getExternalId() {
		return externalId;
	}
}
