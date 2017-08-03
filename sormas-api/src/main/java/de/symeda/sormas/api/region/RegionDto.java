package de.symeda.sormas.api.region;

public class RegionDto extends RegionReferenceDto {

	private static final long serialVersionUID = -1610675328037466348L;

	public static final String I18N_PREFIX = "Region";

	private String name;
	private String epidCode;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getEpidCode() {
		return epidCode;
	}
	public void setEpidCode(String epidCode) {
		this.epidCode = epidCode;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
