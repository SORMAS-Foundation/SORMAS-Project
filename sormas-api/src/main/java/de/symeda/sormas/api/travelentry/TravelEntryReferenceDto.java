package de.symeda.sormas.api.travelentry;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.person.PersonDto;

public class TravelEntryReferenceDto extends ReferenceDto {

	private Long externalId;

	public TravelEntryReferenceDto(String uuid, Long externalId, String firstName, String lastName) {
		super(uuid, PersonDto.buildCaption(firstName, lastName));
		this.externalId = externalId;
	}

	public Long getExternalId() {
		return externalId;
	}
}
