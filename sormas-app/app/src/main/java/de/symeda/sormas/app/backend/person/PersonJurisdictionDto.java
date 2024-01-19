package de.symeda.sormas.app.backend.person;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.backend.caze.CaseJurisdictionDto;
import de.symeda.sormas.app.backend.contact.ContactJurisdictionDto;
import de.symeda.sormas.app.backend.event.EventJurisdictionDto;
import de.symeda.sormas.app.backend.immunization.ImmunizationJurisdictionDto;

public class PersonJurisdictionDto implements Serializable {

	private List<CaseJurisdictionDto> caseJurisdiction = new ArrayList<>();
	private List<ContactJurisdictionDto> contactJurisdiction = new ArrayList<>();
	private List<EventJurisdictionDto> eventJurisdiction = new ArrayList<>();
	private List<ImmunizationJurisdictionDto> immunizationJurisdiction = new ArrayList<>();

	public PersonJurisdictionDto() {

	}

	public PersonJurisdictionDto(
		List<CaseJurisdictionDto> caseJurisdiction,
		List<ContactJurisdictionDto> contactJurisdiction,
		List<EventJurisdictionDto> eventJurisdiction,
		List<ImmunizationJurisdictionDto> immunizationJurisdiction) {
		this.caseJurisdiction = caseJurisdiction;
		this.contactJurisdiction = contactJurisdiction;
		this.eventJurisdiction = eventJurisdiction;
		this.immunizationJurisdiction = immunizationJurisdiction;
	}

	public List<CaseJurisdictionDto> getCaseJurisdiction() {
		return caseJurisdiction;
	}

	public void setCaseJurisdiction(List<CaseJurisdictionDto> caseJurisdiction) {
		this.caseJurisdiction = caseJurisdiction;
	}

	public List<ContactJurisdictionDto> getContactJurisdiction() {
		return contactJurisdiction;
	}

	public void setContactJurisdiction(List<ContactJurisdictionDto> contactJurisdiction) {
		this.contactJurisdiction = contactJurisdiction;
	}

	public List<EventJurisdictionDto> getEventJurisdiction() {
		return eventJurisdiction;
	}

	public void setEventJurisdiction(List<EventJurisdictionDto> eventJurisdiction) {
		this.eventJurisdiction = eventJurisdiction;
	}

	public List<ImmunizationJurisdictionDto> getImmunizationJurisdiction() {
		return immunizationJurisdiction;
	}

	public void setImmunizationJurisdiction(List<ImmunizationJurisdictionDto> immunizationJurisdiction) {
		this.immunizationJurisdiction = immunizationJurisdiction;
	}
}
