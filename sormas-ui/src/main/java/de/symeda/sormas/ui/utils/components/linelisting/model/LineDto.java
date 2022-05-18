package de.symeda.sormas.ui.utils.components.linelisting.model;

import java.io.Serializable;

import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareableDto;

public class LineDto<T extends SormasToSormasShareableDto> implements Serializable {

	private static final long serialVersionUID = -4356132050282062118L;

	private T entity;
	private PersonDto person;

	public T getEntity() {
		return entity;
	}

	public void setEntity(T entity) {
		this.entity = entity;
	}

	public PersonDto getPerson() {
		return person;
	}

	public void setPerson(PersonDto person) {
		this.person = person;
	}
}
