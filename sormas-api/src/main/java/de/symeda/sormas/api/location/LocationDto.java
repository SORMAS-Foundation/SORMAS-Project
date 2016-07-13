package de.symeda.sormas.api.location;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.ReferenceDto;

public class LocationDto extends DataTransferObject {

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
	
	private String address;
	private String details;
	private String city;
	
	private ReferenceDto region;
	private ReferenceDto district;
	private ReferenceDto community;
	
	private Float latitude;
	private Float longitude;
	
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
	
	public ReferenceDto getRegion() {
		return region;
	}
	public void setRegion(ReferenceDto region) {
		this.region = region;
	}
	public ReferenceDto getDistrict() {
		return district;
	}
	public void setDistrict(ReferenceDto district) {
		this.district = district;
	}
	public ReferenceDto getCommunity() {
		return community;
	}
	public void setCommunity(ReferenceDto community) {
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
}
