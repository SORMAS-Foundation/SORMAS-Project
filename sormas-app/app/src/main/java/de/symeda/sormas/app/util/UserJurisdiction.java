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
package de.symeda.sormas.app.util;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.user.JurisdictionLevel;

public class UserJurisdiction {

	private JurisdictionLevel jurisdictionLevel;
	private String uuid;
	private String regionUuid;
	private String districtUuid;
	private String communityUuid;
	private String healthFacilityUuid;
	private String pointOfEntryUuid;
	private String labUuid;
	private Disease limitedDisease;

	public JurisdictionLevel getJurisdictionLevel() {
		return jurisdictionLevel;
	}

	public void setJurisdictionLevel(JurisdictionLevel jurisdictionLevel) {
		this.jurisdictionLevel = jurisdictionLevel;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public String getLabUuid() {
		return labUuid;
	}

	public void setLabUuid(String labUuid) {
		this.labUuid = labUuid;
	}

	public Disease getLimitedDisease() {
		return limitedDisease;
	}

	public void setLimitedDisease(Disease limitedDisease) {
		this.limitedDisease = limitedDisease;
	}
}
