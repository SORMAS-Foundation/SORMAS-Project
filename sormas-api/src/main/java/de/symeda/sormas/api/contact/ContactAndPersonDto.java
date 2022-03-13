package de.symeda.sormas.api.contact;

import de.symeda.sormas.api.person.PersonDto;

public class ContactAndPersonDto extends ContactDto {

	private PersonDto personDto;

	public PersonDto getPersonDto() {
		return personDto;
	}
}
