/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.contact;

import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitStatus;

public final class ContactLogic {

	private ContactLogic() {
		// Hide Utility Class Constructor
	}

	public static Date getStartDate(Date lastContactDate, Date reportDate) {
		return lastContactDate != null ? lastContactDate : reportDate;
	}

	public static Date getEndDate(Date lastContactDate, Date reportDate, Date followUpUntil) {
		return followUpUntil != null ? followUpUntil : lastContactDate != null ? lastContactDate : reportDate;
	}

	/**
	 * Calculates the follow-up until date of the contact based on its start date (last contact or report date), the follow-up duration of
	 * the disease, the current follow-up until date and the date of the last cooperative visit.
	 * 
	 * @param ignoreOverwrite
	 *            Returns the expected follow-up until date based on contact start date, follow-up duration of the disease and date of the
	 *            last cooperative visit. Ignores current follow-up until date and whether or not follow-up until has been overwritten.
	 */
	public static Date calculateFollowUpUntilDate(ContactDto contact, List<VisitDto> visits, int followUpDuration, boolean ignoreOverwrite) {

		Date beginDate = ContactLogic.getStartDate(contact.getLastContactDate(), contact.getReportDateTime());
		Date standardUntilDate = DateHelper.addDays(beginDate, followUpDuration);
		Date untilDate = !ignoreOverwrite && contact.isOverwriteFollowUpUntil() ? contact.getFollowUpUntil() : standardUntilDate;

		Date lastVisitDate = null;
		boolean additionalVisitNeeded = true;
		for (VisitDto visit : visits) {
			if (lastVisitDate == null || DateHelper.getStartOfDay(visit.getVisitDateTime()).after(DateHelper.getStartOfDay(lastVisitDate))) {
				lastVisitDate = visit.getVisitDateTime();
			}
			if (additionalVisitNeeded
				&& !DateHelper.getStartOfDay(visit.getVisitDateTime()).before(DateHelper.getStartOfDay(untilDate))
				&& visit.getVisitStatus() == VisitStatus.COOPERATIVE) {
				additionalVisitNeeded = false;
			}
		}

		// Follow-up until needs to be extended to the date after the last visit if there is no cooperative visit after the follow-up until date
		if (additionalVisitNeeded && lastVisitDate != null && untilDate.before(DateHelper.addDays(lastVisitDate, 1))) {
			untilDate = DateHelper.addDays(lastVisitDate, 1);
		}

		// If the follow-up until date is before the standard follow-up until date for some reason (e.g. because the report date and/or last contact
		// date were changed), set it to the standard follow-up until date
		if (DateHelper.getStartOfDay(untilDate).before(standardUntilDate)) {
			untilDate = standardUntilDate;
		}

		return untilDate;
	}
}
