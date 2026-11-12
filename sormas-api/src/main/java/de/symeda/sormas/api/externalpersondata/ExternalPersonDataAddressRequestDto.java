package de.symeda.sormas.api.externalpersondata;

import java.io.Serializable;
import java.util.Objects;

import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.person.PersonReferenceDto;

@AuditedClass
public class ExternalPersonDataAddressRequestDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private CaseDataDto caseData;

	private ContactDto contact;

	private EventParticipantDto eventParticipant;

	private PersonReferenceDto personReference;

	public CaseDataDto getCaseData() {
		return caseData;
	}

	public void setCaseData(CaseDataDto caseData) {
		this.caseData = caseData;
	}

	public ContactDto getContact() {
		return contact;
	}

	public void setContact(ContactDto contact) {
		this.contact = contact;
	}

	public EventParticipantDto getEventParticipant() {
		return eventParticipant;
	}

	public void setEventParticipant(EventParticipantDto eventParticipant) {
		this.eventParticipant = eventParticipant;
	}

	public PersonReferenceDto getPersonReference() {
		return personReference;
	}

	public void setPersonReference(PersonReferenceDto personReference) {
		this.personReference = personReference;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		ExternalPersonDataAddressRequestDto that = (ExternalPersonDataAddressRequestDto) o;
		return Objects.equals(caseData, that.caseData)
			&& Objects.equals(contact, that.contact)
			&& Objects.equals(eventParticipant, that.eventParticipant)
			&& Objects.equals(personReference, that.personReference);
	}

	@Override
	public int hashCode() {
		return Objects.hash(caseData, contact, eventParticipant, personReference);
	}

	@Override
	public String toString() {
		return "ExternalPersonDataAddressRequestDto{" + "caseData=" + caseData + ", contact=" + contact + ", eventParticipant=" + eventParticipant
			+ ", personReference=" + personReference + '}';
	}
}
