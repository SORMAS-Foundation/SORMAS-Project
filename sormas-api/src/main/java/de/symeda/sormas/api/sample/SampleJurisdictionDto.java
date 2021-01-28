package de.symeda.sormas.api.sample;

import java.io.Serializable;

import de.symeda.sormas.api.caze.CaseJurisdictionDto;
import de.symeda.sormas.api.contact.ContactJurisdictionDto;
import de.symeda.sormas.api.event.EventParticipantJurisdictionDto;

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
