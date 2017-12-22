package de.symeda.sormas.api.location;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

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
		this.setCaption(buildCaption(regionName, districtName, communityName, city, address));
	}
	
	public static String buildCaption(String regionName, String districtName, String communityName, String city, String address) {
		
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(DataHelper.toStringNullable(regionName));
		if (!DataHelper.isNullOrEmpty(districtName)) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(", ");
			}
			stringBuilder.append(districtName);
		}
		if (!DataHelper.isNullOrEmpty(communityName)) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(", ");
			}
			stringBuilder.append(communityName);
		}
		if (!DataHelper.isNullOrEmpty(city)) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(", ");
			}
			stringBuilder.append(city);
		}
		if (!DataHelper.isNullOrEmpty(address)) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(", ");
			}
			stringBuilder.append(address);
		}
		return stringBuilder.toString();
	}
	
}
