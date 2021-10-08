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

package de.symeda.sormas.app.backend.caze;

import java.io.Serializable;

import de.symeda.sormas.api.HasUuid;

public class ResponsibleJurisdictionDto implements Serializable {

	private static final long serialVersionUID = -1038812327109185179L;

	private String regionUuid;
	private String districtUuid;
	private String communityUuid;

	public ResponsibleJurisdictionDto() {
	}

	private ResponsibleJurisdictionDto(String regionUuid, String districtUuid, String communityUuid) {
		this.regionUuid = regionUuid;
		this.districtUuid = districtUuid;
		this.communityUuid = communityUuid;
	}

	public static ResponsibleJurisdictionDto of(String responsibleRegionUuid, String responsibleDistrictUuid, String responsibleCommunityUuid) {
		if (responsibleRegionUuid == null && responsibleDistrictUuid == null && responsibleCommunityUuid == null) {
			return null;
		}

		return new ResponsibleJurisdictionDto(responsibleRegionUuid, responsibleDistrictUuid, responsibleCommunityUuid);
	}

	public static ResponsibleJurisdictionDto of(HasUuid responsibleRegion, HasUuid responsibleDistrict, HasUuid responsibleCommunity) {
		if (responsibleRegion == null && responsibleDistrict == null && responsibleDistrict == null) {
			return null;
		}

		return new ResponsibleJurisdictionDto(
			responsibleRegion != null ? responsibleRegion.getUuid() : null,
			responsibleDistrict != null ? responsibleDistrict.getUuid() : null,
			responsibleCommunity != null ? responsibleCommunity.getUuid() : null);
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
}
