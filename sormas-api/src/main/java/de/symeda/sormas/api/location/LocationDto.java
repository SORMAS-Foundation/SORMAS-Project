package de.symeda.sormas.api.location;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

public class LocationDto extends EntityDto {

	private static final long serialVersionUID = -1399197327930368752L;

	public static final String I18N_PREFIX = "Location";

	public static final String ADDRESS = "address";
	public static final String DETAILS = "details";
	public static final String CITY = "city";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String LAT_LON_ACCURACY = "latLonAccuracy";
	
	private String address;
	private String details;
	private String city;
	
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private CommunityReferenceDto community;
	
	private Double latitude;
	private Double longitude;
	private Float latLonAccuracy;
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}

	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
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
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
	@Override
	public String toString() {
		return buildCaption(region.getCaption(), district.getCaption(), community.getCaption(), city, address);
	}
	
	public boolean isEmptyLocation() {
		return address == null && details == null && city == null && region == null
				&& district == null && community == null;
	}
	
	public Float getLatLonAccuracy() {
		return latLonAccuracy;
	}
	public void setLatLonAccuracy(Float latLonAccuracy) {
		this.latLonAccuracy = latLonAccuracy;
	}
	
	public static String buildCaption(String regionName, String districtName, String communityName, String city, String address) {
		return regionName + (regionName != null && districtName != null ? ", " : "") + districtName +
				(communityName != null && (regionName !=  null || districtName != null) ? ", " : "") + communityName + 
				(regionName != null || districtName != null || communityName != null ? " - " : "") + city + 
				(regionName != null || districtName != null || communityName != null || city != null ? " (" + address + ")" : address);
	}
	
}
