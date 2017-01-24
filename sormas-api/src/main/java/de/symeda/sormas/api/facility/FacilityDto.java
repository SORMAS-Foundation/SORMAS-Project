package de.symeda.sormas.api.facility;

import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

public class FacilityDto extends FacilityReferenceDto {

	private static final long serialVersionUID = -7987228795475507196L;

	public static final String I18N_PREFIX = "Facility";
	public static final String FACILITY_REGION = "facilityRegion";
	public static final String FACILITY_DISTRICT = "facilityDistrict";
	public static final String FACILITY_COMMUNITY = "facilityCommunity";

	private String name;
	private LocationDto location;
	private FacilityType type;
	private boolean publicOwnership;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public LocationDto getLocation() {
		return location;
	}
	public void setLocation(LocationDto location) {
		this.location = location;
	}
	
	public FacilityType getType() {
		return type;
	}
	public void setType(FacilityType type) {
		this.type = type;
	}
	
	public boolean isPublicOwnership() {
		return publicOwnership;
	}
	public void setPublicOwnership(boolean publicOwnership) {
		this.publicOwnership = publicOwnership;
	}

	public RegionReferenceDto getFacilityRegion() {
		return location.getRegion();
	}
	
	public DistrictReferenceDto getFacilityDistrict() {
		return location.getDistrict();
	}
	
	public CommunityReferenceDto getFacilityCommunity() {
		return location.getCommunity();
	}
	
	@Override
	public String toString() {
		StringBuilder caption = new StringBuilder();
		caption.append(name);
		if (location != null) {
			if (location.getCommunity() != null) {
				caption.append(" (").append(location.getCommunity().getCaption()).append(")");
			}
		}
		return caption.toString();
	}
}
