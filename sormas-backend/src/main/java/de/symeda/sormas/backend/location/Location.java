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
package de.symeda.sormas.backend.location;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_BIG;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;
import static de.symeda.sormas.backend.person.Person.PERSON_LOCATIONS_TABLE_NAME;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.area.AreaType;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.location.LocationReferenceDto;
import de.symeda.sormas.api.person.PersonAddressType;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.continent.Continent;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.subcontinent.Subcontinent;
import de.symeda.sormas.backend.person.Person;

@Entity
@Audited
public class Location extends AbstractDomainObject {

	private static final long serialVersionUID = 392776645668778670L;

	public static final String TABLE_NAME = "location";

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
	public static final String LATLONACCURACY = "latLonAccuracy";
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
	public static final String PERSON = "person";

	private String details;
	private String city;
	private AreaType areaType;

	private Continent continent;
	private Subcontinent subcontinent;
	private Country country;
	private Region region;
	private District district;
	private Community community;

	private Double latitude;
	private Double longitude;
	private Float latLonAccuracy;

	private String postalCode;
	private String street;
	private String houseNumber;
	private String additionalInformation;
	private PersonAddressType addressType;
	private String addressTypeDetails;
	private FacilityType facilityType;
	private Facility facility;
	private String facilityDetails;

	private String contactPersonFirstName;
	private String contactPersonLastName;
	private String contactPersonPhone;
	private String contactPersonEmail;

	private Person person;

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Enumerated(EnumType.STRING)
	public AreaType getAreaType() {
		return areaType;
	}

	public void setAreaType(AreaType areaType) {
		this.areaType = areaType;
	}

	@ManyToOne()
	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	@ManyToOne()
	public Continent getContinent() {
		return continent;
	}

	public void setContinent(Continent continent) {
		this.continent = continent;
	}

	@ManyToOne()
	public Subcontinent getSubcontinent() {
		return subcontinent;
	}

	public void setSubcontinent(Subcontinent subcontinent) {
		this.subcontinent = subcontinent;
	}

	@ManyToOne()
	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	@ManyToOne()
	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	@ManyToOne()
	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
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

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	@Column(length = CHARACTER_LIMIT_BIG)
	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	@Enumerated(EnumType.STRING)
	public PersonAddressType getAddressType() {
		return addressType;
	}

	public void setAddressType(PersonAddressType addressType) {
		this.addressType = addressType;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getAddressTypeDetails() {
		return addressTypeDetails;
	}

	public void setAddressTypeDetails(String addressTypeDetails) {
		this.addressTypeDetails = addressTypeDetails;
	}

	@Enumerated(EnumType.STRING)
	public FacilityType getFacilityType() {
		return facilityType;
	}

	public void setFacilityType(FacilityType facilityType) {
		this.facilityType = facilityType;
	}

	@ManyToOne(cascade = {})
	public Facility getFacility() {
		return facility;
	}

	public void setFacility(Facility facility) {
		this.facility = facility;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getFacilityDetails() {
		return facilityDetails;
	}

	public void setFacilityDetails(String facilityDetails) {
		this.facilityDetails = facilityDetails;
	}

	@Column(columnDefinition = "text")
	public String getContactPersonFirstName() {
		return contactPersonFirstName;
	}

	public void setContactPersonFirstName(String contactPersonFirstName) {
		this.contactPersonFirstName = contactPersonFirstName;
	}

	@Column(columnDefinition = "text")
	public String getContactPersonLastName() {
		return contactPersonLastName;
	}

	public void setContactPersonLastName(String contactPersonLastName) {
		this.contactPersonLastName = contactPersonLastName;
	}

	@Column(columnDefinition = "text")
	public String getContactPersonPhone() {
		return contactPersonPhone;
	}

	public void setContactPersonPhone(String contactPersonPhone) {
		this.contactPersonPhone = contactPersonPhone;
	}

	@Column(columnDefinition = "text")
	public String getContactPersonEmail() {
		return contactPersonEmail;
	}

	public void setContactPersonEmail(String contactPersonEmail) {
		this.contactPersonEmail = contactPersonEmail;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinTable(name = PERSON_LOCATIONS_TABLE_NAME,
		joinColumns = @JoinColumn(name = "location_id"),
		inverseJoinColumns = @JoinColumn(name = "person_id"))
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public static String buildGpsCoordinatesCaption(Double latitude, Double longitude, Float latLonAccuracy) {
		if (latitude == null && longitude == null) {
			return "";
		} else if (latitude == null || longitude == null) {
			return I18nProperties.getString(Strings.messageIncompleteGpsCoordinates);
		} else if (latLonAccuracy == null) {
			return latitude + ", " + longitude;
		} else {
			return latitude + ", " + longitude + " +-" + Math.round(latLonAccuracy) + "m";
		}
	}

	public String buildGpsCoordinatesCaption() {
		return buildGpsCoordinatesCaption(latitude, longitude, latLonAccuracy);
	}

	@Override
	public String toString() {
		return LocationReferenceDto.buildCaption(
			region != null ? region.getName() : null,
			district != null ? district.getName() : null,
			community != null ? community.getName() : null,
			city,
			street,
			houseNumber,
			additionalInformation);
	}
}
