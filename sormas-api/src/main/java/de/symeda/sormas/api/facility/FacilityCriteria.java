package de.symeda.sormas.api.facility;

import java.io.Serializable;

import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

/**
 * @author Christopher Riedel
 *
 */
public class FacilityCriteria implements Serializable, Cloneable {

	private static final long serialVersionUID = 3958619224286048978L;

	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private CommunityReferenceDto community;

	private FacilityType type;
	private Boolean excludeStaticFacilities;

	public FacilityCriteria regionEquals(RegionReferenceDto region) {
		this.region = region;
		return this;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public CommunityReferenceDto getCommunity() {
		return community;
	}

	public FacilityType getType() {
		return type;
	}
	
	public Boolean isExcludeStaticFacilities() {
		return excludeStaticFacilities;
	}

	public FacilityCriteria districtEquals(RegionReferenceDto region, DistrictReferenceDto district) {
		this.region = region;
		this.district = district;
		return this;
	}

	public FacilityCriteria communityEquals(RegionReferenceDto region, DistrictReferenceDto district,
			CommunityReferenceDto community) {
		this.region = region;
		this.district = district;
		this.community = community;
		return this;
	}

	public FacilityCriteria typeEquals(FacilityType type) {
		this.type = type;
		return this;
	}
	
	public FacilityCriteria excludeStaticFacilitesEquals(boolean excludeStaticFacilities) {
		this.excludeStaticFacilities = excludeStaticFacilities;
		return this;
	}
}
