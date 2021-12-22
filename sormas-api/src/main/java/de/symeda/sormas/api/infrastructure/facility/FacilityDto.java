/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.api.infrastructure.facility;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.InfrastructureDto;
import de.symeda.sormas.api.infrastructure.area.AreaType;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.FieldConstraints;

public class FacilityDto extends InfrastructureDto {

	private static final long serialVersionUID = -7987228795475507196L;

	public static final String I18N_PREFIX = "Facility";
	public static final String OTHER_FACILITY_UUID = "SORMAS-CONSTID-OTHERS-FACILITY";
	public static final String NONE_FACILITY_UUID = "SORMAS-CONSTID-ISNONE-FACILITY";
	public static final List<String> CONSTANT_FACILITY_UUIDS = Arrays.asList(OTHER_FACILITY_UUID, NONE_FACILITY_UUID);
	public static final String OTHER_FACILITY = "OTHER_FACILITY";
	public static final String NO_FACILITY = "NO_FACILITY";
	public static final String CONFIGURED_FACILITY = "CONFIGURED_FACILITY";
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
	public static final String TYPE_GROUP = "typeGroup";
	public static final String TYPE = "type";
	public static final String EXTERNAL_ID = "externalID";

	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String name;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private CommunityReferenceDto community;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String city;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String postalCode;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String street;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String houseNumber;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String additionalInformation;
	private AreaType areaType;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String contactPersonFirstName;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String contactPersonLastName;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String contactPersonPhone;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_TEXT, message = Validations.textTooLong)
	private String contactPersonEmail;
	@Min(value = -90, message = Validations.numberTooSmall)
	@Max(value = 90, message = Validations.numberTooBig)
	private Double latitude;
	@Min(value = -180, message = Validations.numberTooSmall)
	@Max(value = 180, message = Validations.numberTooBig)
	private Double longitude;
	private FacilityType type;
	private boolean publicOwnership;
	private boolean archived;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String externalID;

	public FacilityDto(
		Date creationDate,
		Date changeDate,
		String uuid,
		boolean archived,
		String name,
		String regionUuid,
		String regionName,
		String regionExternalId,
		String districtUuid,
		String districtName,
		String districtExternalId,
		String communityUuid,
		String communityName,
		String communityExternalId,
		String city,
		String postalCode,
		String street,
		String houseNumber,
		String additionalInformation,
		AreaType areaType,
		String contactPersonFirstName,
		String contactPersonLastName,
		String contactPersonPhone,
		String contactPersonEmail,
		Double latitude,
		Double longitude,
		FacilityType type,
		boolean publicOwnership,
		String externalID) {

		super(creationDate, changeDate, uuid);
		this.archived = archived;
		this.name = name;
		if (regionUuid != null) {
			this.region = new RegionReferenceDto(regionUuid, regionName, regionExternalId);
		}
		if (districtUuid != null) {
			this.district = new DistrictReferenceDto(districtUuid, districtName, districtExternalId);
		}
		if (communityUuid != null) {
			this.community = new CommunityReferenceDto(communityUuid, communityName, communityExternalId);
		}
		this.city = city;
		this.postalCode = postalCode;
		this.street = street;
		this.houseNumber = houseNumber;
		this.additionalInformation = additionalInformation;
		this.areaType = areaType;
		this.contactPersonFirstName = contactPersonFirstName;
		this.contactPersonLastName = contactPersonLastName;
		this.contactPersonPhone = contactPersonPhone;
		this.contactPersonEmail = contactPersonEmail;
		this.latitude = latitude;
		this.longitude = longitude;
		this.type = type;
		this.publicOwnership = publicOwnership;
		this.externalID = externalID;
	}

	public FacilityDto() {
		super();
	}

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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
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

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public FacilityReferenceDto toReference() {
		return new FacilityReferenceDto(getUuid(), toString(), externalID);
	}

	public String getExternalID() {
		return externalID;
	}

	public void setExternalID(String externalID) {
		this.externalID = externalID;
	}

	@Override
	public String toString() {
		return FacilityHelper.buildFacilityString(getUuid(), name);
	}

	public static FacilityDto build() {
		FacilityDto dto = new FacilityDto();
		dto.setUuid(DataHelper.createUuid());
		return dto;
	}
}
