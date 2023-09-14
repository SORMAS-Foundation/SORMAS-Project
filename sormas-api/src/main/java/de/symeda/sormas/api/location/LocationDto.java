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

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.area.AreaType;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.continent.ContinentReferenceDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentReferenceDto;
import de.symeda.sormas.api.person.PersonAddressType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.HideForCountries;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;
import de.symeda.sormas.api.utils.pseudonymization.Pseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.LatitudePseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.LongitudePseudonymizer;
import de.symeda.sormas.api.utils.pseudonymization.valuepseudonymizers.PostalCodePseudonymizer;

@DependingOnFeatureType(featureType = {
	FeatureType.CASE_SURVEILANCE,
	FeatureType.CONTACT_TRACING,
	FeatureType.EVENT_SURVEILLANCE })
public class LocationDto extends PseudonymizableDto {

	private static final long serialVersionUID = -1399197327930368752L;

	public static final String I18N_PREFIX = "Location";

	public static final String DETAILS = "details";
	public static final String CITY = "city";
	public static final String AREA_TYPE = "areaType";
	public static final String CONTINENT = "continent";
	public static final String SUB_CONTINENT = "subcontinent";
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
	public static final String CONTACT_PERSON_FIRST_NAME = "contactPersonFirstName";
	public static final String CONTACT_PERSON_LAST_NAME = "contactPersonLastName";
	public static final String CONTACT_PERSON_PHONE = "contactPersonPhone";
	public static final String CONTACT_PERSON_EMAIL = "contactPersonEmail";

	private ContinentReferenceDto continent;
	private SubcontinentReferenceDto subcontinent;
	private CountryReferenceDto country;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	@PersonalData
	@SensitiveData
	private CommunityReferenceDto community;
	@PersonalData
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_FRANCE })
	private String details;
	@PersonalData
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String city;
	@PersonalData
	@SensitiveData
	private AreaType areaType;
	@PersonalData
	@SensitiveData
	@Pseudonymizer(LatitudePseudonymizer.class)
	@Min(value = -90, message = Validations.numberTooSmall)
	@Max(value = 90, message = Validations.numberTooBig)
	private Double latitude;
	@PersonalData
	@SensitiveData
	@Pseudonymizer(LongitudePseudonymizer.class)
	@Min(value = -180, message = Validations.numberTooSmall)
	@Max(value = 180, message = Validations.numberTooBig)
	private Double longitude;
	private Float latLonAccuracy;
	@PersonalData()
	@SensitiveData()
	@Pseudonymizer(PostalCodePseudonymizer.class)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String postalCode;
	@PersonalData
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String street;
	@PersonalData
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String houseNumber;
	@PersonalData
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String additionalInformation;
	private PersonAddressType addressType;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String addressTypeDetails;
	@PersonalData
	@SensitiveData
	private FacilityType facilityType;
	@PersonalData
	@SensitiveData
	private FacilityReferenceDto facility;
	@PersonalData
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String facilityDetails;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	@PersonalData
	@SensitiveData
	private String contactPersonFirstName;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	@PersonalData
	@SensitiveData
	private String contactPersonLastName;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	@PersonalData
	@SensitiveData
	private String contactPersonPhone;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	@PersonalData
	@SensitiveData
	private String contactPersonEmail;

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

	public ContinentReferenceDto getContinent() {
		return continent;
	}

	public void setContinent(ContinentReferenceDto continent) {
		this.continent = continent;
	}

	public SubcontinentReferenceDto getSubcontinent() {
		return subcontinent;
	}

	public void setSubcontinent(SubcontinentReferenceDto subcontinent) {
		this.subcontinent = subcontinent;
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

	public String getContactPersonFirstName() {
		return contactPersonFirstName;
	}

	public void setContactPersonFirstName(String contactPersonFirstName) {
		this.contactPersonFirstName = contactPersonFirstName;
	}

	public String getContactPersonLastName() {
		return contactPersonLastName;
	}

	public void setContactPersonLastName(String contactPersonLastName) {
		this.contactPersonLastName = contactPersonLastName;
	}

	public String getContactPersonPhone() {
		return contactPersonPhone;
	}

	public void setContactPersonPhone(String contactPersonPhone) {
		this.contactPersonPhone = contactPersonPhone;
	}

	public String getContactPersonEmail() {
		return contactPersonEmail;
	}

	public void setContactPersonEmail(String contactPersonEmail) {
		this.contactPersonEmail = contactPersonEmail;
	}

	@Override
	public String buildCaption() {
		return LocationReferenceDto.buildCaption(
			region != null ? region.getCaption() : null,
			district != null ? district.getCaption() : null,
			community != null ? community.getCaption() : null,
			city,
			street,
			houseNumber,
			additionalInformation);
	}

	@JsonIgnore
	public String i18nPrefix() {
		return I18N_PREFIX;
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

	public static LocationDto build() {

		LocationDto location = new LocationDto();
		location.setUuid(DataHelper.createUuid());
		return location;
	}

	public static String buildAddressCaption(String street, String houseNumber, String postalCode, String city) {
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
