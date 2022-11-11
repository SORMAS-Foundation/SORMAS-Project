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

package de.symeda.sormas.app.backend.caze;

import java.io.Serializable;

import de.symeda.sormas.api.uuid.HasUuid;

public class ResponsibleJurisdictionDto implements Serializable {

	private static final long serialVersionUID = -1038812327109185179L;

	private String regionUuid;
	private String districtUuid;
	private String communityUuid;
	private String facilityUuid;

	public ResponsibleJurisdictionDto() {
	}

	private ResponsibleJurisdictionDto(String regionUuid, String districtUuid, String communityUuid, String facilityUuid) {
		this.regionUuid = regionUuid;
		this.districtUuid = districtUuid;
		this.communityUuid = communityUuid;
		this.facilityUuid = facilityUuid;
	}

	public static ResponsibleJurisdictionDto of(
		String responsibleRegionUuid,
		String responsibleDistrictUuid,
		String responsibleCommunityUuid,
		String responsibleFacilityUuid) {
		if (responsibleRegionUuid == null && responsibleDistrictUuid == null && responsibleCommunityUuid == null && responsibleFacilityUuid == null) {
			return null;
		}

		return new ResponsibleJurisdictionDto(responsibleRegionUuid, responsibleDistrictUuid, responsibleCommunityUuid, responsibleFacilityUuid);
	}

	public static ResponsibleJurisdictionDto of(
		HasUuid responsibleRegion,
		HasUuid responsibleDistrict,
		HasUuid responsibleCommunity,
		HasUuid responsibleFacility) {
		if (responsibleRegion == null && responsibleDistrict == null && responsibleCommunity == null && responsibleFacility == null) {
			return null;
		}

		return new ResponsibleJurisdictionDto(
			responsibleRegion != null ? responsibleRegion.getUuid() : null,
			responsibleDistrict != null ? responsibleDistrict.getUuid() : null,
			responsibleCommunity != null ? responsibleCommunity.getUuid() : null,
			responsibleFacility != null ? responsibleFacility.getUuid() : null);
	}

	public String getRegionUuid() {
		return regionUuid;
	}

	public void setRegionUuid(String regionUuid) {
		this.regionUuid = regionUuid;
	}

	public String getDistrictUuid() {
		return districtUuid;
	}

	public void setDistrictUuid(String districtUuid) {
		this.districtUuid = districtUuid;
	}

	public String getCommunityUuid() {
		return communityUuid;
	}

	public void setCommunityUuid(String communityUuid) {
		this.communityUuid = communityUuid;
	}

	public String getFacilityUuid() {
		return facilityUuid;
	}

	public void setFacilityUuid(String facilityUuid) {
		this.facilityUuid = facilityUuid;
	}
}
