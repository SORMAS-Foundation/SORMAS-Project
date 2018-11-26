package de.symeda.sormas.ui.importer;

import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonNameDto;

public class ImportSimilarityInput {
	
	private final List<PersonNameDto> persons;
	private final CaseDataDto caze;
	private final PersonDto person;
	
	public ImportSimilarityInput(List<PersonNameDto> persons, CaseDataDto caze, PersonDto person) {
		this.persons = persons;
		this.caze = caze;
		this.person = person;
	}

	public List<PersonNameDto> getPersons() {
		return persons;
	}

	public CaseDataDto getCaze() {
		return caze;
	}

	public PersonDto getPerson() {
		return person;
	}

}
