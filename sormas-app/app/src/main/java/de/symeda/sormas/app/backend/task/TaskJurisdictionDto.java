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

package de.symeda.sormas.app.backend.task;

import java.io.Serializable;

import de.symeda.sormas.app.backend.caze.CaseJurisdictionDto;
import de.symeda.sormas.app.backend.contact.ContactJurisdictionDto;
import de.symeda.sormas.app.backend.event.EventJurisdictionDto;


public class TaskJurisdictionDto implements Serializable {

	private String creatorUserUuid;
	private String assigneeUserUuid;
	private CaseJurisdictionDto caseJurisdiction;
	private ContactJurisdictionDto contactJurisdiction;
	private EventJurisdictionDto eventJurisdiction;

	public TaskJurisdictionDto() {

	}

	public TaskJurisdictionDto(
		String creatorUserUuid,
		String assigneeUserUuid,
		CaseJurisdictionDto caseJurisdiction,
		ContactJurisdictionDto contactJurisdiction,
		EventJurisdictionDto eventJurisdiction) {
		this.creatorUserUuid = creatorUserUuid;
		this.assigneeUserUuid = assigneeUserUuid;
		this.caseJurisdiction = caseJurisdiction;
		this.contactJurisdiction = contactJurisdiction;
		this.eventJurisdiction = eventJurisdiction;
	}

	public String getCreatorUserUuid() {
		return creatorUserUuid;
	}

	public void setCreatorUserUuid(String creatorUserUuid) {
		this.creatorUserUuid = creatorUserUuid;
	}

	public String getAssigneeUserUuid() {
		return assigneeUserUuid;
	}

	public void setAssigneeUserUuid(String assigneeUserUuid) {
		this.assigneeUserUuid = assigneeUserUuid;
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

	public EventJurisdictionDto getEventJurisdiction() {
		return eventJurisdiction;
	}

	public void setEventJurisdiction(EventJurisdictionDto eventJurisdiction) {
		this.eventJurisdiction = eventJurisdiction;
	}
}
