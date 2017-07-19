package de.symeda.sormas.api.facility;

import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

public class FacilityDto extends FacilityReferenceDto {

	private static final long serialVersionUID = -7987228795475507196L;

	public static final String I18N_PREFIX = "Facility";
	public static final String OTHER_FACILITY_UUID = "SORMAS-CONSTID-OTHERS-FACILITY";
	
	private String name;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private CommunityReferenceDto community;
	private String city;
	private Float latitude;
	private Float longitude;
	private FacilityType type;
	private boolean publicOwnership;
	
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
	
	public DistrictReferenceDto getDistrict() {
		return district;
	}
	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}
	
	public CommunityReferenceDto getCommunity() {
		return community;
	}
	public void setCommunity(CommunityReferenceDto community) {
		this.community = community;
	}
	
	public Float getLatitude() {
		return latitude;
	}
	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}
	
	public Float getLongitude() {
		return longitude;
	}
	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
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
	
	@Override
	public String toString() {
		StringBuilder caption = new StringBuilder();
		caption.append(name);
		if (community != null) {
			caption.append(" (").append(community.getCaption()).append(")");
		}
		return caption.toString();
	}
}
