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
package de.symeda.sormas.app.backend.contact;

import java.io.Serializable;
import java.util.List;

import de.symeda.sormas.app.backend.caze.CaseJurisdictionDto;


public class ContactJurisdictionDto implements Serializable {

	private static final long serialVersionUID = 2345261069774868477L;

	public static final String I18N_PREFIX = "Contact";

	public static final String REGION_UUID = "regionUuid";
	public static final String DISTRICT_UUID = "districtUuid";
	public static final String COMMUNITY_UUID = "communityUuid";

	private String reportingUserUuid;

	private String regionUuid;

	private String districtUuid;

	private String communityUuid;

	private List<String> sampleLabUuids;

	private CaseJurisdictionDto caseJurisdiction;

	private String contactOfficerUuid;

	public ContactJurisdictionDto() {

	}

	public ContactJurisdictionDto(
		String reportingUserUuid,
		String regionUuid,
		String districtUuid,
		String communityUuid,
		CaseJurisdictionDto caseJurisdiction) {

		this.reportingUserUuid = reportingUserUuid;
		this.regionUuid = regionUuid;
		this.districtUuid = districtUuid;
		this.communityUuid = communityUuid;
		this.caseJurisdiction = caseJurisdiction;
	}

	public String getReportingUserUuid() {
		return reportingUserUuid;
	}

	public void setReportingUserUuid(String reportingUserUuid) {
		this.reportingUserUuid = reportingUserUuid;
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

	public void setCommunityUuid(String communityUuid) {
		this.communityUuid = communityUuid;
	}

	public String getCommunityUuid() {
		return communityUuid;
	}

	public CaseJurisdictionDto getCaseJurisdiction() {
		return caseJurisdiction;
	}

	public void setCaseJurisdiction(CaseJurisdictionDto caseJurisdiction) {
		this.caseJurisdiction = caseJurisdiction;
	}

	public List<String> getSampleLabUuids() {
		return sampleLabUuids;
	}

	public void setSampleLabUuids(List<String> sampleLabUuids) {
		this.sampleLabUuids = sampleLabUuids;
	}

	public String getContactOfficerUuid() {
		return contactOfficerUuid;
	}

	public ContactJurisdictionDto setContactOfficerUuid(String contactOfficerUuid) {
		this.contactOfficerUuid = contactOfficerUuid;
		return this;
	}
}
