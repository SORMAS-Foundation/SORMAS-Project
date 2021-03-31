package de.symeda.sormas.ui.importer;

import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.person.SimilarPersonDto;

public class ContactImportSimilarityResult extends PersonImportSimilarityResult {

	private final SimilarContactDto matchingContact;

	public ContactImportSimilarityResult(
		SimilarPersonDto matchingPerson,
		SimilarContactDto matchingContact,
		ImportSimilarityResultOption resultOption) {
		super(matchingPerson, resultOption);
		this.matchingContact = matchingContact;
	}

	public SimilarContactDto getMatchingContact() {
		return matchingContact;
	}
}
