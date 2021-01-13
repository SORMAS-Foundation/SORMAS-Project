package de.symeda.sormas.api.labmessage;

import java.io.Serializable;

import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;

public class SimilarEntriesDto implements Serializable {

	CaseIndexDto caze;
	SimilarContactDto contact;
	SimilarEventParticipantDto eventParticipant;
	boolean newCase;
	boolean newContact;
	boolean newEventParticipant;

	public CaseIndexDto getCaze() {
		return caze;
	}

	public void setCaze(CaseIndexDto caze) {
		this.caze = caze;
	}

	public SimilarContactDto getContact() {
		return contact;
	}

	public void setContact(SimilarContactDto contact) {
		this.contact = contact;
	}

	public SimilarEventParticipantDto getEventParticipant() {
		return eventParticipant;
	}

	public void setEventParticipant(SimilarEventParticipantDto eventParticipant) {
		this.eventParticipant = eventParticipant;
	}

	public boolean isNewCase() {
		return newCase;
	}

	public void setNewCase(boolean newCase) {
		this.newCase = newCase;
	}

	public boolean isNewContact() {
		return newContact;
	}

	public void setNewContact(boolean newContact) {
		this.newContact = newContact;
	}

	public boolean isNewEventParticipant() {
		return newEventParticipant;
	}

	public void setNewEventParticipant(boolean newEventParticipant) {
		this.newEventParticipant = newEventParticipant;
	}
}
