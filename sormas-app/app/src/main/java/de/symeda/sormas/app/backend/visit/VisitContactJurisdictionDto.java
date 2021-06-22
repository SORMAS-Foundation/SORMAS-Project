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

package de.symeda.sormas.app.backend.visit;


import de.symeda.sormas.app.backend.caze.CaseJurisdictionDto;
import de.symeda.sormas.app.backend.caze.ResponsibleJurisdictionDto;
import de.symeda.sormas.app.backend.contact.ContactJurisdictionDto;

public class VisitContactJurisdictionDto extends ContactJurisdictionDto {

	private static final long serialVersionUID = -5548335838864960141L;

	private long visitId;

	public VisitContactJurisdictionDto(
		long visitId,
		String reportingUserUuid,
		String regionUuid,
		String districtUuid,
		String communityUuid,
		String caseReportingUserUuid,
		String caseResponsibleRegionUuid,
		String caseResponsibleDistrictUid,
		String caseResponsibleCommunityUid,
		String caseRegionUui,
		String caseDistrictUud,
		String caseCommunityUuid,
		String caseHealthFacilityUuid,
		String casePointOfEntryUuid) {

		this(
			visitId,
			reportingUserUuid,
			regionUuid,
			districtUuid,
			communityUuid,
			caseReportingUserUuid != null
				? new CaseJurisdictionDto(
					caseReportingUserUuid,
					ResponsibleJurisdictionDto.of(caseResponsibleRegionUuid, caseResponsibleDistrictUid, caseResponsibleCommunityUid),
					caseRegionUui,
					caseDistrictUud,
					caseCommunityUuid,
					caseHealthFacilityUuid,
					casePointOfEntryUuid)
				: null);
	}

	public VisitContactJurisdictionDto(
		long visitId,
		String reportingUserUuid,
		String regionUuid,
		String districtUuid,
		String communityUuid,
		CaseJurisdictionDto caseJursidiction) {

		super(reportingUserUuid, regionUuid, districtUuid, communityUuid, caseJursidiction);
		this.visitId = visitId;
	}

	public long getVisitId() {
		return visitId;
	}
}
