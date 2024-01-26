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
package de.symeda.sormas.app.backend.caze;

import java.io.Serializable;
import java.util.List;

public class CaseJurisdictionDto implements Serializable {

	private static final long serialVersionUID = -5412823431238056752L;

	private String reportingUserUuid;

	private ResponsibleJurisdictionDto responsibleJurisdiction;

	private String regionUuid;
	private String districtUuid;
	private String communityUuid;
	private String healthFacilityUuid;
	private String pointOfEntryUuid;
	private List<String> sampleLabUuids;
	private String surveillanceOfficerUuid;

	public CaseJurisdictionDto() {
	}

	public CaseJurisdictionDto(
		String reportingUserUuid,
		ResponsibleJurisdictionDto responsibleJurisdiction,
		String regionUuid,
		String districtUuid,
		String communityUuid,
		String healthFacilityUuid,
		String pointOfEntryUuid) {
		this.reportingUserUuid = reportingUserUuid;
		this.responsibleJurisdiction = responsibleJurisdiction;
		this.regionUuid = regionUuid;
		this.districtUuid = districtUuid;
		this.communityUuid = communityUuid;
		this.healthFacilityUuid = healthFacilityUuid;
		this.pointOfEntryUuid = pointOfEntryUuid;
	}

	public String getReportingUserUuid() {
		return reportingUserUuid;
	}

	public void setReportingUserUuid(String reportingUserUuid) {
		this.reportingUserUuid = reportingUserUuid;
	}

	public ResponsibleJurisdictionDto getResponsibleJurisdiction() {
		return responsibleJurisdiction;
	}

	public void setResponsibleJurisdiction(ResponsibleJurisdictionDto responsibleJurisdiction) {
		this.responsibleJurisdiction = responsibleJurisdiction;
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

	public List<String> getSampleLabUuids() {
		return sampleLabUuids;
	}

	public void setSampleLabUuids(List<String> sampleLabUuids) {
		this.sampleLabUuids = sampleLabUuids;
	}

	public String getSurveillanceOfficerUuid() {
		return surveillanceOfficerUuid;
	}

	public CaseJurisdictionDto setSurveillanceOfficerUuid(String surveillanceOfficerUuid) {
		this.surveillanceOfficerUuid = surveillanceOfficerUuid;
		return this;
	}
}
