package de.symeda.sormas.api.region;

import java.io.Serializable;

public class CommunityCriteria implements Serializable, Cloneable {

	private static final long serialVersionUID = 7815180508529134182L;

	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	
	public CommunityCriteria regionEquals(RegionReferenceDto region) {
		this.region = region;
		return this;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}
	
	public CommunityCriteria districtEquals(DistrictReferenceDto district) {
		this.district = district;
		return this;
	}
	
	public DistrictReferenceDto getDistrict() {
		return district;
	}	
	
}
