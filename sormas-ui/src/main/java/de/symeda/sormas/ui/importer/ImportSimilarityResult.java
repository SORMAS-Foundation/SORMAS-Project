package de.symeda.sormas.ui.importer;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonIndexDto;

public class ImportSimilarityResult {

	private final PersonIndexDto matchingPerson;
	private final CaseDataDto matchingCase;
	private final boolean usePerson;
	private final boolean useCase;
	private final boolean skip;
	private final boolean cancelImport;
	
	public ImportSimilarityResult(PersonIndexDto matchingPerson, CaseDataDto matchingCase, boolean usePerson, boolean useCase,
			boolean skip, boolean cancelImport) {
		this.matchingPerson = matchingPerson;
		this.matchingCase = matchingCase;
		this.usePerson = usePerson;
		this.useCase = useCase;
		this.skip = skip;
		this.cancelImport = cancelImport;
	}

	public PersonIndexDto getMatchingPerson() {
		return matchingPerson;
	}

	public CaseDataDto getMatchingCase() {
		return matchingCase;
	}

	public boolean isUsePerson() {
		return usePerson;
	}

	public boolean isUseCase() {
		return useCase;
	}

	public boolean isSkip() {
		return skip;
	}

	public boolean isCancelImport() {
		return cancelImport;
	}
	
}
