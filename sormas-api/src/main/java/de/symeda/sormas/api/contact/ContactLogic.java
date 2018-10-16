package de.symeda.sormas.api.contact;

import java.util.Date;

import de.symeda.sormas.api.utils.DateHelper;

public class ContactLogic {

	public static int getNumberOfRequiredVisitsSoFar(Date contactReportDate, Date contactFollowUpUntil) {
		Date now = new Date();
		if (now.before(contactFollowUpUntil)) {
			return DateHelper.getDaysBetween(DateHelper.addDays(contactReportDate, 1), now);
		} else {
			return DateHelper.getDaysBetween(DateHelper.addDays(contactReportDate, 1), contactFollowUpUntil);
		}
	}
}
