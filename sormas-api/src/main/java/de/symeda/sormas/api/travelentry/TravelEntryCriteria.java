package de.symeda.sormas.api.travelentry;

import java.io.Serializable;

import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.criteria.BaseCriteria;

public class TravelEntryCriteria extends BaseCriteria implements Serializable, Cloneable {

	private PersonReferenceDto person;

	public PersonReferenceDto getPerson() {
		return person;
	}

	public void setPerson(PersonReferenceDto person) {
		this.person = person;
	}

	public TravelEntryCriteria person(PersonReferenceDto person) {
		this.person = person;
		return this;
	}
}
