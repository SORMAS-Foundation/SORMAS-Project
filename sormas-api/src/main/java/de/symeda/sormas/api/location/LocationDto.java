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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.location;

import de.symeda.sormas.api.PseudonymizableDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.PersonalData;

public class LocationDto extends PseudonymizableDto {

	private static final long serialVersionUID = -1399197327930368752L;

	public static final String I18N_PREFIX = "Location";

	public static final String ADDRESS = "address";
	public static final String DETAILS = "details";
	public static final String CITY = "city";
	public static final String AREA_TYPE = "areaType";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String LAT_LON_ACCURACY = "latLonAccuracy";
	public static final String POSTAL_CODE = "postalCode";

	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	@PersonalData
	private CommunityReferenceDto community;
	@PersonalData
	private String address;
	@PersonalData
	private String details;
	@PersonalData
	private String city;
	@PersonalData
	private AreaType areaType;
	@PersonalData
	private Double latitude;
	@PersonalData
	private Double longitude;
	@PersonalData
	private Float latLonAccuracy;
	@PersonalData
	private String postalCode;

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

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

	@Override
	public String toString() {
		return LocationReferenceDto.buildCaption(
				region != null ? region.getCaption() : null,
				district != null ? district.getCaption() : null,
				community != null ? community.getCaption() : null, city, address);
	}

	public LocationReferenceDto toReference() {
		return new LocationReferenceDto(getUuid(),
				region != null ? region.getCaption() : null,
				district != null ? district.getCaption() : null,
				community != null ? community.getCaption() : null, city, address);
	}

	public boolean isEmptyLocation() {
		return address == null && details == null && city == null && areaType == null
				&& region == null && district == null && community == null;
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

	public static LocationDto build() {
		LocationDto location = new LocationDto();
		location.setUuid(DataHelper.createUuid());
		return location;
	}
}
