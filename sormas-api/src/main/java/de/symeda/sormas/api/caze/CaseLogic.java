package de.symeda.sormas.api.caze;

import de.symeda.sormas.api.utils.ValidationException;

public class CaseLogic {

	public static void validateInvestigationDoneAllowed(CaseDataDto caze) throws ValidationException {
		if (caze.getCaseClassification() == CaseClassification.NOT_CLASSIFIED) {
			throw new ValidationException("Not allowed to set investigation status to done for an unclassified case.");
		}
	}
	
}
