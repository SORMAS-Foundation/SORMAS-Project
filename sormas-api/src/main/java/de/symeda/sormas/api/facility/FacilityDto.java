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

import java.util.Date;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

public class FacilityDto extends EntityDto {

	private static final long serialVersionUID = -7987228795475507196L;

	public static final String I18N_PREFIX = "Facility";
	public static final String OTHER_FACILITY_UUID = "SORMAS-CONSTID-OTHERS-FACILITY";
	public static final String NONE_FACILITY_UUID = "SORMAS-CONSTID-ISNONE-FACILITY";
	public static final String OTHER_LABORATORY_UUID = "SORMAS-CONSTID-OTHERS-LABORATO";
	public static final String OTHER_FACILITY = "OTHER_FACILITY";
	public static final String NO_FACILITY = "NO_FACILITY";
	public static final String OTHER_LABORATORY = "OTHER_LABORATORY";
	public static final String NAME = "name";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String CITY = "city";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String EXTERNAL_ID = "externalID";

	private String name;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	private CommunityReferenceDto community;
	private String city;
	private Double latitude;
	private Double longitude;
	private FacilityType type;
	private boolean publicOwnership;
	private boolean archived;
	private String externalID;

	public FacilityDto(
		Date creationDate,
		Date changeDate,
		String uuid,
		boolean archived,
		String name,
		String regionUuid,
		String regionName,
		String districtUuid,
		String districtName,
		String communityUuid,
		String communityName,
		String city,
		Double latitude,
		Double longitude,
		FacilityType type,
		boolean publicOwnership,
		String externalID) {

		super(creationDate, changeDate, uuid);
		this.archived = archived;
		this.name = name;
		if (regionUuid != null) {
			this.region = new RegionReferenceDto(regionUuid, regionName);
		}
		if (districtUuid != null) {
			this.district = new DistrictReferenceDto(districtUuid, districtName);
		}
		if (communityUuid != null) {
			this.community = new CommunityReferenceDto(communityUuid, communityName);
		}
		this.city = city;
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
		return new FacilityReferenceDto(getUuid(), toString());
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
