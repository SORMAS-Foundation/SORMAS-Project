package de.symeda.sormas.api.region;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.ReferenceDto;

public class CommunityDto extends DataTransferObject {

	private static final long serialVersionUID = -8833267932522978860L;

	public static final String I18N_PREFIX = "Community";

	private String name;
	private ReferenceDto district;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public ReferenceDto getDistrict() {
		return district;
	}
	public void setDistrict(ReferenceDto district) {
		this.district = district;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
