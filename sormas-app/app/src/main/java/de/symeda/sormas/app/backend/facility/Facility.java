/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.facility;

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_BIG;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import androidx.databinding.Bindable;

import de.symeda.sormas.api.infrastructure.area.AreaType;
import de.symeda.sormas.api.infrastructure.facility.FacilityHelper;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.app.backend.infrastructure.InfrastructureAdo;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;

@Entity(name = Facility.TABLE_NAME)
@DatabaseTable(tableName = Facility.TABLE_NAME)
public class Facility extends InfrastructureAdo {

	private static final long serialVersionUID = 8572137127616417072L;

	public static final String TABLE_NAME = "facility";
	public static final String I18N_PREFIX = "Facility";

	public static final String REGION = "region_id";
	public static final String DISTRICT = "district_id";
	public static final String COMMUNITY = "community_id";
	public static final String NAME = "name";
	public static final String TYPE = "type";

	public Facility() {
	}

	public Facility(String uuid) {
		setUuid(uuid);
	}

	@Column
	private String name;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Region region;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private District district;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Community community;
	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String city;
	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String postalCode;
	@Column(length = CHARACTER_LIMIT_BIG)
	private String street;
	@Column(length = CHARACTER_LIMIT_DEFAULT)
	private String houseNumber;
	@Column(length = CHARACTER_LIMIT_BIG)
	private String additionalInformation;
	@Enumerated(EnumType.STRING)
	private AreaType areaType;
	@Column(columnDefinition = "text")
	private String contactPersonFirstName;
	@Column(columnDefinition = "text")
	private String contactPersonLastName;
	@Column(columnDefinition = "text")
	private String contactPersonPhone;
	@Column(columnDefinition = "text")
	private String contactPersonEmail;

	@DatabaseField
	private Double latitude;
	@DatabaseField
	private Double longitude;

	@Enumerated(EnumType.STRING)
	private FacilityType type;
	@Column
	private boolean publicOwnership;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Bindable
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
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

	public AreaType getAreaType() {
		return areaType;
	}

	public void setAreaType(AreaType areaType) {
		this.areaType = areaType;
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
	public String buildCaption() {
		return FacilityHelper.buildFacilityString(getUuid(), name);
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}
}
