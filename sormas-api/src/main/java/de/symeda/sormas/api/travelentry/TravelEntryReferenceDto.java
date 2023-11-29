package de.symeda.sormas.api.travelentry;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.PersonalData;

@DependingOnFeatureType(featureType = FeatureType.TRAVEL_ENTRIES)
public class TravelEntryReferenceDto extends ReferenceDto {

    @PersonalData
    private String firstName;
    @PersonalData
    private String lastName;
	private String externalId;

	public TravelEntryReferenceDto(String uuid, String externalId, String firstName, String lastName) {
        super(uuid);
        this.firstName = firstName;
        this.lastName = lastName;
		this.externalId = externalId;
	}

	public TravelEntryReferenceDto(String uuid) {
		super(uuid);
	}

	public String getExternalId() {
		return externalId;
	}

    @Override
    public String getCaption() {
        return PersonDto.buildCaption(firstName, lastName);
    }
}
