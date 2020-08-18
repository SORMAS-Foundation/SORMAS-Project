/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.caze;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.followup.FollowUpDto;
import de.symeda.sormas.api.utils.jurisdiction.WithJurisdiction;

public class CaseFollowUpDto extends FollowUpDto implements WithJurisdiction<CaseJurisdictionDto> {

	private static final long serialVersionUID = -7782443664670559221L;

	private Date symptomsOnsetDate;

	private final CaseJurisdictionDto jurisdiction;

	//@formatter:off
	public CaseFollowUpDto(String uuid, String personUuid, String personFirstName, String personLastName,
							  Date reportDate, Date symptomsOnsetDate, Date followUpUntil, Disease disease,
							  String caseReportingUserUuid, String caseRegionUuid, String caseDistrictUuid,
							  String caseCommunityUud, String caseHealthFacilityUuid, String casePointOfEntryUuid
	) {
	//formatter:on
		super(uuid, personUuid, personFirstName, personLastName, reportDate, followUpUntil, disease);
		this.symptomsOnsetDate = symptomsOnsetDate;
		this.jurisdiction = new CaseJurisdictionDto(
				caseReportingUserUuid,
				caseRegionUuid,
				caseDistrictUuid,
				caseCommunityUud,
				caseHealthFacilityUuid,
				casePointOfEntryUuid);
	}

	public CaseJurisdictionDto getJurisdiction() {
		return jurisdiction;
	}

	public Date getSymptomsOnsetDate() {
		return symptomsOnsetDate;
	}

	public void setSymptomsOnsetDate(Date symptomsOnsetDate) {
		this.symptomsOnsetDate = symptomsOnsetDate;
	}
}
