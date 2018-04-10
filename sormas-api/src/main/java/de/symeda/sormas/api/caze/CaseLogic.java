package de.symeda.sormas.api.caze;

import java.util.Date;

import de.symeda.sormas.api.utils.ValidationException;

public class CaseLogic {

	public static void validateInvestigationDoneAllowed(CaseDataDto caze) throws ValidationException {
		if (caze.getCaseClassification() == CaseClassification.NOT_CLASSIFIED) {
			throw new ValidationException("Not allowed to set investigation status to done for an unclassified case.");
		}
	}
	
	public static Date getStartDate(Date onsetDate, Date receptionDate, Date reportDate) {
		if (onsetDate != null) {
			return onsetDate;
		} else if (receptionDate != null) {
			return receptionDate;
		} else {
			return reportDate;
		}
	}
	
}
