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
package de.symeda.sormas.backend.infrastructure.facility;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_BIG;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import de.symeda.sormas.api.infrastructure.area.AreaType;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.backend.infrastructure.InfrastructureAdo;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Facility extends InfrastructureAdo {

	private static final long serialVersionUID = 8572137127616417072L;

	public static final String TABLE_NAME = "facility";

	public static final String NAME = "name";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String CITY = "city";
	public static final String POSTAL_CODE = "postalCode";
	public static final String STREET = "street";
	public static final String HOUSE_NUMBER = "houseNumber";
	public static final String ADDITIONAL_INFORMATION = "additionalInformation";
	public static final String AREA_TYPE = "areaType";
	public static final String CONTACT_PERSON_FIRST_NAME = "contactPersonFirstName";
	public static final String CONTACT_PERSON_LAST_NAME = "contactPersonLastName";
	public static final String CONTACT_PERSON_PHONE = "contactPersonPhone";
	public static final String CONTACT_PERSON_EMAIL = "contactPersonEmail";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String TYPE = "type";
	public static final String PUBLIC_OWNERSHIP = "publicOwnership";
	public static final String EXTERNAL_ID = "externalID";

	private String name;
	private Region region;
	private District district;
	private Community community;
	private String city;
	private String postalCode;
	private String street;
	private String houseNumber;
	private String additionalInformation;
	private AreaType areaType;
	private String contactPersonFirstName;
	private String contactPersonLastName;
	private String contactPersonPhone;
	private String contactPersonEmail;
	private Double latitude;
	private Double longitude;
	private FacilityType type;
	private boolean publicOwnership;
	private String externalID;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
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

	@Column(length = CHARACTER_LIMIT_BIG)
	public String getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	@Enumerated(EnumType.STRING)
	public AreaType getAreaType() {
		return areaType;
	}

	public void setAreaType(AreaType areaType) {
		this.areaType = areaType;
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

	@Enumerated(EnumType.STRING)
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

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getExternalID() {
		return externalID;
	}

	public void setExternalID(String externalID) {
		this.externalID = externalID;
	}
}
