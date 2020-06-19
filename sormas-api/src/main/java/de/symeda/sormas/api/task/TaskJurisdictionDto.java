package de.symeda.sormas.api.task;

import java.io.Serializable;

import de.symeda.sormas.api.caze.CaseJurisdictionDto;
import de.symeda.sormas.api.contact.ContactJurisdictionDto;
import de.symeda.sormas.api.event.EventJurisdictionDto;

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
