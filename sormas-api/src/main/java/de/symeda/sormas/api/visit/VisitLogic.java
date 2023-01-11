package de.symeda.sormas.api.visit;

import java.util.Date;

import de.symeda.sormas.api.followup.FollowUpLogic;
import de.symeda.sormas.api.utils.DateHelper;

public final class VisitLogic {

	private VisitLogic() {
		// Hide Utility Class Constructor
	}

	public static Date getAllowedEndDate(Date endDate) {
		return DateHelper.addDays(DateHelper.getEndOfDay(endDate), FollowUpLogic.ALLOWED_DATE_OFFSET);
	}

	public static Date getAllowedStartDate(Date startDate) {
		return DateHelper.subtractDays(DateHelper.getStartOfDay(startDate), FollowUpLogic.ALLOWED_DATE_OFFSET);
	}
}
