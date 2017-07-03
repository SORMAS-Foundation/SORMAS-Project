package de.symeda.sormas.api.region;

public class DistrictDto extends DistrictReferenceDto {

	private static final long serialVersionUID = 8990957700033431836L;

	public static final String I18N_PREFIX = "District";

	private String name;
	private RegionReferenceDto region;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public RegionReferenceDto getRegion() {
		return region;
	}
	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
