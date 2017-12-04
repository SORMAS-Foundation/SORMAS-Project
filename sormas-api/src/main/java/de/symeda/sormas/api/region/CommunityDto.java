package de.symeda.sormas.api.region;

import de.symeda.sormas.api.EntityDto;

public class CommunityDto extends EntityDto {

	private static final long serialVersionUID = -8833267932522978860L;

	public static final String I18N_PREFIX = "Community";

	private String name;
	private DistrictReferenceDto district;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public DistrictReferenceDto getDistrict() {
		return district;
	}
	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}
	
	public CommunityReferenceDto toReference() {
		return new CommunityReferenceDto(getUuid());
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
