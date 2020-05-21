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

package de.symeda.sormas.api.contact;

import de.symeda.sormas.api.caze.CaseJurisdictionDto;

import java.io.Serializable;

public class ContactJurisdictionDto implements Serializable {
	private static final long serialVersionUID = 2345261069774868477L;

	private String reportingUserUuid;

	private String regionUuId;

	private String districtUuid;

	private CaseJurisdictionDto caseJurisdiction;

	public ContactJurisdictionDto() {
	}

	public ContactJurisdictionDto(String reportingUserUuid, String regionUuId, String districtUuid, CaseJurisdictionDto caseJurisdiction) {
		this.reportingUserUuid = reportingUserUuid;
		this.regionUuId = regionUuId;
		this.districtUuid = districtUuid;
		this.caseJurisdiction = caseJurisdiction;
	}

	public String getReportingUserUuid() {
		return reportingUserUuid;
	}

	public void setReportingUserUuid(String reportingUserUuid) {
		this.reportingUserUuid = reportingUserUuid;
	}

	public String getRegionUuId() {
		return regionUuId;
	}

	public void setRegionUuId(String regionUuId) {
		this.regionUuId = regionUuId;
	}

	public String getDistrictUuid() {
		return districtUuid;
	}

	public void setDistrictUuid(String districtUuid) {
		this.districtUuid = districtUuid;
	}

	public CaseJurisdictionDto getCaseJurisdiction() {
		return caseJurisdiction;
	}

	public void setCaseJurisdiction(CaseJurisdictionDto caseJurisdiction) {
		this.caseJurisdiction = caseJurisdiction;
	}
}
