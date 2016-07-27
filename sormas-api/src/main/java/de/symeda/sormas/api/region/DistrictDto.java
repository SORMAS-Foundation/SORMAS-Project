package de.symeda.sormas.api.region;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.ReferenceDto;

public class DistrictDto extends DataTransferObject {

	private static final long serialVersionUID = 8990957700033431836L;

	public static final String I18N_PREFIX = "District";

	private String name;
	private ReferenceDto region;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public ReferenceDto getRegion() {
		return region;
	}
	public void setRegion(ReferenceDto region) {
		this.region = region;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
