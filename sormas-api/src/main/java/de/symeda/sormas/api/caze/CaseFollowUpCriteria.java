package de.symeda.sormas.api.caze;

import java.util.Date;

public class CaseFollowUpCriteria extends CaseCriteria {

	private Date referenceDate;
	private int interval;

	public Date getReferenceDate() {
		return referenceDate;
	}

	public int getInterval() {
		return interval;
	}
}
