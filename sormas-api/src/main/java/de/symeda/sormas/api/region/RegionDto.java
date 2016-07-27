package de.symeda.sormas.api.region;

import de.symeda.sormas.api.DataTransferObject;

public class RegionDto extends DataTransferObject {

	private static final long serialVersionUID = -1610675328037466348L;

	public static final String I18N_PREFIX = "Region";

	private String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
