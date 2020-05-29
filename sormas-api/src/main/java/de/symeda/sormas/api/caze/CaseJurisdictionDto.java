/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 */

package de.symeda.sormas.api.caze;

import java.io.Serializable;

public class CaseJurisdictionDto implements Serializable {

	private static final long serialVersionUID = -5412823431238056752L;

	private String reportingUserUuid;
	private String regionUui;
	private String districtUud;
	private String communityUuid;
	private String healthFacilityUuid;
	private String pointOfEntryUuid;

	public CaseJurisdictionDto() {
	}

	public CaseJurisdictionDto(String reportingUserUuid, String regionUui, String districtUud, String communityUuid, String healthFacilityUuid, String pointOfEntryUuid) {
		this.reportingUserUuid = reportingUserUuid;
		this.regionUui = regionUui;
		this.districtUud = districtUud;
		this.communityUuid = communityUuid;
		this.healthFacilityUuid = healthFacilityUuid;
		this.pointOfEntryUuid = pointOfEntryUuid;
	}

	public CaseJurisdictionDto(CaseDataDto caseDto) {
		this();

		if (caseDto.getReportingUser() != null) {
			reportingUserUuid = caseDto.getReportingUser().getUuid();
		}
		if (caseDto.getRegion() != null) {
			regionUui = caseDto.getRegion().getUuid();
		}
		if (caseDto.getDistrict() != null) {
			districtUud = caseDto.getDistrict().getUuid();
		}
		if (caseDto.getCommunity() != null) {
			communityUuid = caseDto.getCommunity().getUuid();
		}
		if (caseDto.getHealthFacility() != null) {
			healthFacilityUuid = caseDto.getHealthFacility().getUuid();
		}
		if (caseDto.getPointOfEntry() != null) {
			pointOfEntryUuid = caseDto.getPointOfEntry().getUuid();
		}
	}

	public String getReportingUserUuid() {
		return reportingUserUuid;
	}

	public void setReportingUserUuid(String reportingUserUuid) {
		this.reportingUserUuid = reportingUserUuid;
	}

	public String getRegionUuid() {
		return regionUui;
	}

	public void setRegionUui(String regionUui) {
		this.regionUui = regionUui;
	}

	public String getDistrictUud() {
		return districtUud;
	}

	public void setDistrictUud(String districtUud) {
		this.districtUud = districtUud;
	}

	public String getCommunityUuid() {
		return communityUuid;
	}

	public void setCommunityUuid(String communityUuid) {
		this.communityUuid = communityUuid;
	}

	public String getHealthFacilityUuid() {
		return healthFacilityUuid;
	}

	public void setHealthFacilityUuid(String healthFacilityUuid) {
		this.healthFacilityUuid = healthFacilityUuid;
	}

	public String getPointOfEntryUuid() {
		return pointOfEntryUuid;
	}

	public void setPointOfEntryUuid(String pointOfEntryUuid) {
		this.pointOfEntryUuid = pointOfEntryUuid;
	}
}
