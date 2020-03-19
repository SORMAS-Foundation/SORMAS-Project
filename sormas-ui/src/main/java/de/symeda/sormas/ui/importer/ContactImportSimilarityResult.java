package de.symeda.sormas.ui.importer;

import de.symeda.sormas.api.person.PersonIndexDto;

public class ContactImportSimilarityResult {

	private final PersonIndexDto matchingPerson;
	private final ImportSimilarityResultOption resultOption;

	public ContactImportSimilarityResult(PersonIndexDto matchingPerson, ImportSimilarityResultOption resultOption) {
		this.matchingPerson = matchingPerson;
		this.resultOption = resultOption;
	}

	public PersonIndexDto getMatchingPerson() {
		return matchingPerson;
	}

	public ImportSimilarityResultOption getResultOption() {
		return resultOption;
	}

}
