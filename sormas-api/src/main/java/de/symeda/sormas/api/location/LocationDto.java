/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.location;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.person.PersonAddressType;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.CountryReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;
import de.symeda.sormas.api.utils.pseudonymization.Pseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.LatitudePseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.LongitudePseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.PostalCodePseudonymizer;

public class LocationDto extends PseudonymizableDto {

	private static final long serialVersionUID = -1399197327930368752L;

	public static final String I18N_PREFIX = "Location";

	public static final String DETAILS = "details";
	public static final String CITY = "city";
	public static final String AREA_TYPE = "areaType";
	public static final String COUNTRY = "country";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String LAT_LON_ACCURACY = "latLonAccuracy";
	public static final String POSTAL_CODE = "postalCode";
	public static final String STREET = "street";
	public static final String HOUSE_NUMBER = "houseNumber";
	public static final String ADDITIONAL_INFORMATION = "additionalInformation";
	public static final String ADDRESS_TYPE = "addressType";
	public static final String ADDRESS_TYPE_DETAILS = "addressTypeDetails";
	public static final String FACILITY_TYPE = "facilityType";
	public static final String FACILITY = "facility";
	public static final String FACILITY_DETAILS = "facilityDetails";

	private CountryReferenceDto country;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	@PersonalData
	@SensitiveData
	private CommunityReferenceDto community;
	@PersonalData
	@SensitiveData
	private String details;
	@PersonalData
	@SensitiveData
	private String city;
	@PersonalData
	@SensitiveData
	private AreaType areaType;
	@PersonalData
	@SensitiveData
	@Pseudonymizer(LatitudePseudonymizer.class)
	private Double latitude;
	@PersonalData
	@SensitiveData
	@Pseudonymizer(LongitudePseudonymizer.class)
	private Double longitude;
	private Float latLonAccuracy;
	@PersonalData()
	@SensitiveData()
	@Pseudonymizer(PostalCodePseudonymizer.class)
	private String postalCode;
	@PersonalData
	@SensitiveData
	private String street;
	@PersonalData
	@SensitiveData
	private String houseNumber;
	@PersonalData
	@SensitiveData
	private String additionalInformation;
	private PersonAddressType addressType;
	private String addressTypeDetails;
	@PersonalData
	private FacilityType facilityType;
	@PersonalData
	@SensitiveData
	private FacilityReferenceDto facility;
	@PersonalData
	@SensitiveData
	private String facilityDetails;

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

	public AreaType getAreaType() {
		return areaType;
	}

	public void setAreaType(AreaType areaType) {
		this.areaType = areaType;
	}

	public CountryReferenceDto getCountry() {
		return country;
	}

	public void setCountry(CountryReferenceDto country) {
		this.country = country;
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

	public Float getLatLonAccuracy() {
		return latLonAccuracy;
	}

	public void setLatLonAccuracy(Float latLonAccuracy) {
		this.latLonAccuracy = latLonAccuracy;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	public String getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	public PersonAddressType getAddressType() {
		return addressType;
	}

	public void setAddressType(PersonAddressType addressType) {
		this.addressType = addressType;
	}

	public String getAddressTypeDetails() {
		return addressTypeDetails;
	}

	public void setAddressTypeDetails(String addressTypeDetails) {
		this.addressTypeDetails = addressTypeDetails;
	}

	public FacilityType getFacilityType() {
		return facilityType;
	}

	public void setFacilityType(FacilityType facilityType) {
		this.facilityType = facilityType;
	}

	public FacilityReferenceDto getFacility() {
		return facility;
	}

	public void setFacility(FacilityReferenceDto facility) {
		this.facility = facility;
	}

	public String getFacilityDetails() {
		return facilityDetails;
	}

	public void setFacilityDetails(String facilityDetails) {
		this.facilityDetails = facilityDetails;
	}

	@Override
	public String toString() {

		return LocationReferenceDto.buildCaption(
			region != null ? region.getCaption() : null,
			district != null ? district.getCaption() : null,
			community != null ? community.getCaption() : null,
			city,
			street,
			houseNumber,
			additionalInformation);
	}

	public LocationReferenceDto toReference() {

		return new LocationReferenceDto(
			getUuid(),
			region != null ? region.getCaption() : null,
			district != null ? district.getCaption() : null,
			community != null ? community.getCaption() : null,
			city,
			street,
			houseNumber,
			additionalInformation);
	}

	public boolean checkIsEmptyLocation() {
		return details == null
			&& city == null
			&& areaType == null
			&& region == null
			&& district == null
			&& community == null
			&& street == null
			&& houseNumber == null
			&& additionalInformation == null;
	}

	public static LocationDto build() {

		LocationDto location = new LocationDto();
		location.setUuid(DataHelper.createUuid());
		return location;
	}

	public static String buildStreetAndHouseNumberCaption(String street, String houseNumber) {
		return DataHelper.toStringNullable(street) + " " + DataHelper.toStringNullable(houseNumber);
	}

	public String buildAddressCaption() {
		String streetAndNumber = DataHelper.toStringNullable(street) + " " + DataHelper.toStringNullable(houseNumber);
		String postalAndCity = DataHelper.toStringNullable(postalCode) + " " + DataHelper.toStringNullable(city);
		if (StringUtils.isNotBlank(streetAndNumber)) {
			if (StringUtils.isNotBlank(postalAndCity)) {
				return streetAndNumber + ", " + postalAndCity;
			} else {
				return streetAndNumber;
			}
		} else if (StringUtils.isNotBlank(postalAndCity)) {
			return postalAndCity;
		} else {
			return "";
		}
	}
}
