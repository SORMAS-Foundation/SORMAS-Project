package de.symeda.sormas.api.location;

public class LocationHelper {

	private LocationHelper() {

	}

	public static LocationDto overrideLocationInformation(LocationDto mainAddress, LocationDto address) {
		mainAddress.setAdditionalInformation(address.getAdditionalInformation());
		mainAddress.setAreaType(address.getAreaType());
		mainAddress.setCity(address.getCity());
		mainAddress.setCommunity(address.getCommunity());
		mainAddress.setDetails(address.getDetails());
		mainAddress.setDistrict(address.getDistrict());
		mainAddress.setFacility(address.getFacility());
		mainAddress.setFacilityDetails(address.getFacilityDetails());
		mainAddress.setFacilityType(address.getFacilityType());
		mainAddress.setHouseNumber(address.getHouseNumber());
		mainAddress.setLatitude(address.getLatitude());
		mainAddress.setLatLonAccuracy(address.getLatLonAccuracy());
		mainAddress.setLongitude(address.getLongitude());
		mainAddress.setPostalCode(address.getPostalCode());
		mainAddress.setRegion(address.getRegion());
		mainAddress.setStreet(address.getStreet());
		return mainAddress;
	}

	public static void clearLocation(LocationDto address) {
		address.setAdditionalInformation(null);
		address.setAreaType(null);
		address.setCity(null);
		address.setCommunity(null);
		address.setDetails(null);
		address.setDistrict(null);
		address.setFacility(null);
		address.setFacilityDetails(null);
		address.setFacilityType(null);
		address.setHouseNumber(null);
		address.setLatitude(null);
		address.setLatLonAccuracy(null);
		address.setLongitude(null);
		address.setPostalCode(null);
		address.setRegion(null);
		address.setStreet(null);
	}
}
