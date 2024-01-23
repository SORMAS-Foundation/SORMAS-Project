package de.symeda.sormas.app.backend.person;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.app.backend.caze.CaseJurisdictionDto;
import de.symeda.sormas.app.backend.contact.ContactJurisdictionDto;
import de.symeda.sormas.app.backend.event.EventJurisdictionDto;
import de.symeda.sormas.app.backend.immunization.ImmunizationJurisdictionDto;

public class PersonJurisdictionDto implements Serializable {

	private List<CaseJurisdictionDto> caseJurisdictions = new ArrayList<>();
	private List<ContactJurisdictionDto> contactJurisdictions = new ArrayList<>();
	private List<EventJurisdictionDto> eventJurisdictions = new ArrayList<>();
	private List<ImmunizationJurisdictionDto> immunizationJurisdictions = new ArrayList<>();

	public PersonJurisdictionDto() {

	}

	public PersonJurisdictionDto(
		List<CaseJurisdictionDto> caseJurisdictions,
		List<ContactJurisdictionDto> contactJurisdictions,
		List<EventJurisdictionDto> eventJurisdictions,
		List<ImmunizationJurisdictionDto> immunizationJurisdictions) {
		this.caseJurisdictions = caseJurisdictions;
		this.contactJurisdictions = contactJurisdictions;
		this.eventJurisdictions = eventJurisdictions;
		this.immunizationJurisdictions = immunizationJurisdictions;
	}

	public List<CaseJurisdictionDto> getCaseJurisdictions() {
		return caseJurisdictions;
	}

	public void setCaseJurisdictions(List<CaseJurisdictionDto> caseJurisdictions) {
		this.caseJurisdictions = caseJurisdictions;
	}

	public List<ContactJurisdictionDto> getContactJurisdictions() {
		return contactJurisdictions;
	}

	public void setContactJurisdictions(List<ContactJurisdictionDto> contactJurisdictions) {
		this.contactJurisdictions = contactJurisdictions;
	}

	public List<EventJurisdictionDto> getEventJurisdictions() {
		return eventJurisdictions;
	}

	public void setEventJurisdictions(List<EventJurisdictionDto> eventJurisdictions) {
		this.eventJurisdictions = eventJurisdictions;
	}

	public List<ImmunizationJurisdictionDto> getImmunizationJurisdictions() {
		return immunizationJurisdictions;
	}

	public void setImmunizationJurisdictions(List<ImmunizationJurisdictionDto> immunizationJurisdictions) {
		this.immunizationJurisdictions = immunizationJurisdictions;
	}
}
