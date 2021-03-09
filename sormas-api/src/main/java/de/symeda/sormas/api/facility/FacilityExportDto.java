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
package de.symeda.sormas.api.facility;

import java.io.Serializable;

import de.symeda.sormas.api.location.AreaType;
import de.symeda.sormas.api.utils.Order;

public class FacilityExportDto implements Serializable {

	public static final String I18N_PREFIX = "FacilityExport";

	public static final String UUID = "uuid";
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
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String TYPE = "type";
	public static final String EXTERNAL_ID = "externalID";

	private String uuid;
	private String name;
	private FacilityType type;
	private String region;
	private String district;
	private String community;
	private String city;
	private String postalCode;
	private String street;
	private String houseNumber;
	private String additionalInformation;
	private AreaType areaType;
	private Double latitude;
	private Double longitude;
	private String externalID;

	public FacilityExportDto(
		String uuid,
		String name,
		FacilityType type,
		String region,
		String district,
		String community,
		String city,
		String postalCode,
		String street,
		String houseNumber,
		String additionalInformation,
		AreaType areaType,
		Double latitude,
		Double longitude,
		String externalID) {

		this.uuid = uuid;
		this.name = name;
		this.type = type;
		this.region = region;
		this.district = district;
		this.community = community;
		this.city = city;
		this.postalCode = postalCode;
		this.street = street;
		this.houseNumber = houseNumber;
		this.additionalInformation = additionalInformation;
		this.areaType = areaType;
		this.latitude = latitude;
		this.longitude = longitude;
		this.externalID = externalID;
	}

	@Order(0)
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Order(1)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Order(2)
	public FacilityType getType() {
		return type;
	}

	public void setType(FacilityType type) {
		this.type = type;
	}

	@Order(3)
	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	@Order(4)
	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	@Order(5)
	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	@Order(6)
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Order(7)
	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	@Order(8)
	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	@Order(9)
	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	@Order(10)
	public String getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	@Order(11)
	public AreaType getAreaType() {
		return areaType;
	}

	public void setAreaType(AreaType areaType) {
		this.areaType = areaType;
	}

	@Order(12)
	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	@Order(13)
	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@Order(14)
	public String getExternalID() {
		return externalID;
	}

	public void setExternalID(String externalID) {
		this.externalID = externalID;
	}
}
