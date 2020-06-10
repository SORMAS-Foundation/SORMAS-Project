package de.symeda.sormas.ui.importer;

import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.person.PersonIndexDto;

public class ContactImportSimilarityResult {

	private final PersonIndexDto matchingPerson;
	private final SimilarContactDto matchingContact;
	private final ImportSimilarityResultOption resultOption;

	public ContactImportSimilarityResult(
		PersonIndexDto matchingPerson,
		SimilarContactDto matchingContact,
		ImportSimilarityResultOption resultOption) {
		this.matchingPerson = matchingPerson;
		this.resultOption = resultOption;
		this.matchingContact = matchingContact;
	}

	public SimilarContactDto getMatchingContact() {
		return matchingContact;
	}

	public PersonIndexDto getMatchingPerson() {
		return matchingPerson;
	}

	public ImportSimilarityResultOption getResultOption() {
		return resultOption;
	}
}
