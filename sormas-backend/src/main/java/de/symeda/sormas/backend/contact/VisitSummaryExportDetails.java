/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.contact;

import java.util.Date;

import de.symeda.sormas.api.caze.CaseJurisdictionDto;
import de.symeda.sormas.api.contact.ContactJurisdictionDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.symptoms.Symptoms;

public class VisitSummaryExportDetails {

	private Long contactId;
	private Date visitDateTime;
	private VisitStatus visitStatus;
	private Symptoms symptoms;

	private ContactJurisdictionDto jurisdiction;

	public VisitSummaryExportDetails(
		Long contactId,
		Date visitDateTime,
		VisitStatus visitStatus,
		Symptoms symptoms,
		String reportingUserUuid,
		String regionUuid,
		String districtUuid,
		String caseReportingUserUuid,
		String caseRegionUui,
		String caseDistrictUud,
		String caseCommunityUuid,
		String caseHealthFacilityUuid,
		String casePointOfEntryUuid) {

		this.contactId = contactId;
		this.visitDateTime = visitDateTime;
		this.visitStatus = visitStatus;
		this.symptoms = symptoms;

		CaseJurisdictionDto caseJurisdiction = caseReportingUserUuid != null
			? null
			: new CaseJurisdictionDto(
				caseReportingUserUuid,
				caseRegionUui,
				caseDistrictUud,
				caseCommunityUuid,
				caseHealthFacilityUuid,
				casePointOfEntryUuid);
		this.jurisdiction = new ContactJurisdictionDto(reportingUserUuid, regionUuid, districtUuid, caseJurisdiction);
	}

	public Long getContactId() {
		return contactId;
	}

	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}

	public Date getVisitDateTime() {
		return visitDateTime;
	}

	public void setVisitDateTime(Date visitDateTime) {
		this.visitDateTime = visitDateTime;
	}

	public VisitStatus getVisitStatus() {
		return visitStatus;
	}

	public void setVisitStatus(VisitStatus visitStatus) {
		this.visitStatus = visitStatus;
	}

	public Symptoms getSymptoms() {
		return symptoms;
	}

	public void setSymptoms(Symptoms symptoms) {
		this.symptoms = symptoms;
	}

	public ContactJurisdictionDto getJurisdiction() {
		return jurisdiction;
	}
}
