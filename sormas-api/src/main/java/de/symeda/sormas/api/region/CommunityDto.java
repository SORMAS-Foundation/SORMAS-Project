package de.symeda.sormas.api.region;

public class CommunityDto extends CommunityReferenceDto {

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
	
	@Override
	public String toString() {
		return getName();
	}
}
