package de.symeda.sormas.api.labmessage;

import de.symeda.sormas.api.BaseCriteria;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.person.PersonReferenceDto;

public class EventParticipantSimilarityCriteria extends BaseCriteria implements Cloneable {

	private PersonReferenceDto person;
	private Disease disease;

	public PersonReferenceDto getPerson() {
		return person;
	}

	public void setPerson(PersonReferenceDto person) {
		this.person = person;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}
}
