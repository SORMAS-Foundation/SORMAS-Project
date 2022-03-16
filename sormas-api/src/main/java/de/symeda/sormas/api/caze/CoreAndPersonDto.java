package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

public class CoreAndPersonDto<T extends EntityDto> extends PseudonymizableDto {

	private PersonDto person;
	private T coreData;

	public PersonDto getPerson() {
		return person;
	}

	public T getCoreData() {
		return coreData;
	}

	public CoreAndPersonDto(PersonDto person, T coreData) {
		this.person = person;
		this.coreData = coreData;
	}

	public CoreAndPersonDto() {
	}

	public void setPerson(PersonDto person) {
		this.person = person;
	}

	public void setCoreData(T coreData) {
		this.coreData = coreData;
	}
}
