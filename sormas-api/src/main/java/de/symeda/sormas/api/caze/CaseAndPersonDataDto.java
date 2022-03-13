package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

public class CaseAndPersonDataDto extends PseudonymizableDto {

	private PersonDto person;
	private CaseDataDto caseData;

	public PersonDto getPerson() {
		return person;
	}

	public CaseDataDto getCaseData() {
		return caseData;
	}
}
