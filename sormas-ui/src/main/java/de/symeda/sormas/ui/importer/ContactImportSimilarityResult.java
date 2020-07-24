package de.symeda.sormas.ui.importer;

import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.person.PersonIndexDto;
import de.symeda.sormas.api.person.SimilarPersonDto;

public class ContactImportSimilarityResult {

	private final SimilarPersonDto matchingPerson;
	private final SimilarContactDto matchingContact;
	private final ImportSimilarityResultOption resultOption;

	public ContactImportSimilarityResult(
		SimilarPersonDto matchingPerson,
		SimilarContactDto matchingContact,
		ImportSimilarityResultOption resultOption) {
		this.matchingPerson = matchingPerson;
		this.resultOption = resultOption;
		this.matchingContact = matchingContact;
	}

	public SimilarContactDto getMatchingContact() {
		return matchingContact;
	}

	public SimilarPersonDto getMatchingPerson() {
		return matchingPerson;
	}

	public ImportSimilarityResultOption getResultOption() {
		return resultOption;
	}
}
