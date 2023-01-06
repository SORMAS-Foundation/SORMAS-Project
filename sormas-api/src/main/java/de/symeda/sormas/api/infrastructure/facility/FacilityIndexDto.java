/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.uuid.AbstractUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Light-weight index information on facilities for larger queries")
public class FacilityIndexDto extends AbstractUuidDto {

	public static final String I18N_PREFIX = "Facility";

	public static final String NAME = "name";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String POSTAL_CODE = "postalCode";
	public static final String CITY = "city";
	public static final String STREET = "street";
	public static final String HOUSE_NUMBER = "houseNumber";
	public static final String ADDITIONAL_INFORMATION = "additionalInformation";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String TYPE = "type";
	public static final String EXTERNAL_ID = "externalID";

	@Schema(description = "Facility name")
	private String name;
	private FacilityType type;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private CommunityReferenceDto community;
	@Schema(description = "Postal code of the facility's address")
	private String postalCode;
	@Schema(description = "Name of the city the facility is located in")
	private String city;
	@Schema(description = "Street name of the facility's address")
	private String street;
	@Schema(description = "House number of the facility's address")
	private String houseNumber;
	@Schema(description = "Additional information from the facility's address (e.g. floor)")
	private String additionalInformation;
	@Schema(description = "Geodetic latitude of the facility's coordinates")
	private Double latitude;
	@Schema(description = "Geodetic longitude of the facility's coordinates")
	private Double longitude;
	@Schema(description = "TBD_RESTAPI_SWAGGER_DOC")
	private String externalID;

	public FacilityIndexDto(
		String uuid,
		String name,
		FacilityType type,
		String regionUuid,
		String regionName,
		String districtUuid,
		String districtName,
		String communityUuid,
		String communityName,
		String postalCode,
		String city,
		String street,
		String houseNumber,
		String additionalInformation,
		Double latitude,
		Double longitude,
		String externalID) {

		super(uuid);
		this.name = name;
		this.type = type;
		if (regionUuid != null) {
			this.region = new RegionReferenceDto(regionUuid, regionName, null);
		}
		if (districtUuid != null) {
			this.district = new DistrictReferenceDto(districtUuid, districtName, null);
		}
		if (communityUuid != null) {
			this.community = new CommunityReferenceDto(communityUuid, communityName, null);
		}
		this.postalCode = postalCode;
		this.city = city;
		this.street = street;
		this.houseNumber = houseNumber;
		this.additionalInformation = additionalInformation;
		this.latitude = latitude;
		this.longitude = longitude;
		this.externalID = externalID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FacilityType getType() {
		return type;
	}

	public void setType(FacilityType type) {
		this.type = type;
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

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
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

	public String getExternalID() {
		return externalID;
	}

	public void setExternalID(String externalID) {
		this.externalID = externalID;
	}

	@Schema(description = "Caption based on UUID and facility name")
	public String getCaption() {
		return FacilityHelper.buildFacilityString(getUuid(), name);
	}

	@Override
	public String toString() {
		return I18N_PREFIX + StringUtils.SPACE + getUuid();
	}

	public FacilityReferenceDto toReference() {
		return new FacilityReferenceDto(getUuid(), getCaption(), getExternalID());
	}
}
