package de.symeda.sormas.api.location;

import de.symeda.sormas.api.ReferenceDto;

public class LocationReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -1399197327930368752L;

	public LocationReferenceDto() {
		
	}
	
	public LocationReferenceDto(String uuid) {
		this.setUuid(uuid);
	}
	
	public LocationReferenceDto(String uuid, String caption) {
		this.setUuid(uuid);
		this.setCaption(caption);
	}
	
	public LocationReferenceDto(String uuid, String regionName, String districtName, String communityName, String city, String address) {
		this.setUuid(uuid);
		this.setCaption(LocationDto.buildCaption(regionName, districtName, communityName, city, address));
	}
	
}
