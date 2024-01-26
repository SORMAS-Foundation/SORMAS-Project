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

package de.symeda.sormas.app.backend.event;

import java.io.Serializable;

public class EventJurisdictionDto implements Serializable {

	private String reportingUserUuid;
	private String responsibleUserUuid;
	private String regionUuid;
	private String districtUuid;
	private String communityUuid;

	public EventJurisdictionDto() {
	}

	public EventJurisdictionDto(
		String reportingUserUuid,
		String responsibleUserUuid,
		String regionUuid,
		String districtUuid,
		String communityUuid) {
		this.reportingUserUuid = reportingUserUuid;
		this.responsibleUserUuid = responsibleUserUuid;
		this.regionUuid = regionUuid;
		this.districtUuid = districtUuid;
		this.communityUuid = communityUuid;
	}

	public String getReportingUserUuid() {
		return reportingUserUuid;
	}

	public void setReportingUserUuid(String reportingUserUuid) {
		this.reportingUserUuid = reportingUserUuid;
	}

	public String getResponsibleUserUuid() {
		return responsibleUserUuid;
	}

	public EventJurisdictionDto setResponsibleUserUuid(String responsibleUserUuid) {
		this.responsibleUserUuid = responsibleUserUuid;
		return this;
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
