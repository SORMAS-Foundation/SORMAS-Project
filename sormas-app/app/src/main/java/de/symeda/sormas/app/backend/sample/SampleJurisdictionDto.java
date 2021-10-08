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

package de.symeda.sormas.app.backend.sample;

import java.io.Serializable;

import de.symeda.sormas.app.backend.caze.CaseJurisdictionDto;
import de.symeda.sormas.app.backend.contact.ContactJurisdictionDto;
import de.symeda.sormas.app.backend.event.EventParticipantJurisdictionDto;


public class SampleJurisdictionDto implements Serializable {

	private String reportingUserUuid;
	private CaseJurisdictionDto caseJurisdiction;
	private ContactJurisdictionDto contactJurisdiction;
	private EventParticipantJurisdictionDto eventParticipantJurisdiction;
	private String labUuid;

	public SampleJurisdictionDto() {

	}

	public SampleJurisdictionDto(
		String reportingUserUuid,
		CaseJurisdictionDto caseJurisdiction,
		ContactJurisdictionDto contactJurisdiction,
		EventParticipantJurisdictionDto eventParticipantJurisdiction,
		String labUuid) {
		this.reportingUserUuid = reportingUserUuid;
		this.caseJurisdiction = caseJurisdiction;
		this.contactJurisdiction = contactJurisdiction;
		this.eventParticipantJurisdiction = eventParticipantJurisdiction;
		this.labUuid = labUuid;
	}

	public String getReportingUserUuid() {
		return reportingUserUuid;
	}

	public void setReportingUserUuid(String reportingUserUuid) {
		this.reportingUserUuid = reportingUserUuid;
	}

	public CaseJurisdictionDto getCaseJurisdiction() {
		return caseJurisdiction;
	}

	public void setCaseJurisdiction(CaseJurisdictionDto caseJurisdiction) {
		this.caseJurisdiction = caseJurisdiction;
	}

	public ContactJurisdictionDto getContactJurisdiction() {
		return contactJurisdiction;
	}

	public void setContactJurisdiction(ContactJurisdictionDto contactJurisdiction) {
		this.contactJurisdiction = contactJurisdiction;
	}

	public EventParticipantJurisdictionDto getEventParticipantJurisdiction() {
		return eventParticipantJurisdiction;
	}

	public void setEventParticipantJurisdiction(EventParticipantJurisdictionDto eventParticipantJurisdiction) {
		this.eventParticipantJurisdiction = eventParticipantJurisdiction;
	}

	public String getLabUuid() {
		return labUuid;
	}

	public void setLabUuid(String labUuid) {
		this.labUuid = labUuid;
	}
}
