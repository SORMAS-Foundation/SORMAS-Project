package de.symeda.sormas.api.outbreak;

import java.io.Serializable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

public class OutbreakCriteria implements Serializable {

	private static final long serialVersionUID = 326691431810294295L;

	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private Disease disease;
	
	public RegionReferenceDto getRegion() {
		return region;
	}
	public OutbreakCriteria districtIsInRegion(RegionReferenceDto region) {
		this.region = region;
		return this;
	}
	public DistrictReferenceDto getDistrict() {
		return district;
	}
	public OutbreakCriteria districtEquals(DistrictReferenceDto district) {
		this.district = district;
		return this;
	}
	public Disease getDisease() {
		return disease;
	}
	public OutbreakCriteria diseaseEquals(Disease disease) {
		this.disease = disease;
		return this;
	}
	
}
