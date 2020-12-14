package de.symeda.sormas.api.labmessage;

import java.io.Serializable;

import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.event.SimilarEventParticipantDto;

public class SimilarEntitiesDto implements Serializable {

	CaseIndexDto caze;
	SimilarContactDto contact;
	SimilarEventParticipantDto eventParticipant;

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
}
