package de.symeda.sormas.api.region;

import java.io.Serializable;

public class DistrictCriteria implements Serializable, Cloneable {

	private static final long serialVersionUID = -1794892073657582900L;
	
	private RegionReferenceDto region;

	public DistrictCriteria regionEquals(RegionReferenceDto region) {
		this.region = region;
		return this;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

}
