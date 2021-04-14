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

	public static Date getFollowUpUntilDate(ContactDto contact, List<VisitDto> visits, int followUpDuration) {

		Date beginDate = ContactLogic.getStartDate(contact.getLastContactDate(), contact.getReportDateTime());
		Date untilDate = contact.isOverwriteFollowUpUntil()
			|| (contact.getFollowUpUntil() != null && contact.getFollowUpUntil().after(DateHelper.addDays(beginDate, followUpDuration)))
				? contact.getFollowUpUntil()
				: DateHelper.addDays(beginDate, followUpDuration);

		VisitDto lastVisit = null;
		boolean additionalVisitNeeded;
		do {
			additionalVisitNeeded = false;
			if (visits != null) {
				for (VisitDto visit : visits) {
					if (lastVisit != null) {
						if (lastVisit.getVisitDateTime().before(visit.getVisitDateTime())) {
							lastVisit = visit;
						}
					} else {
						lastVisit = visit;
					}
				}
			}
			if (lastVisit != null) {
				// if the last visit was not cooperative and happened at the last date of
				// contact tracing ..
				if (lastVisit.getVisitStatus() != VisitStatus.COOPERATIVE && DateHelper.isSameDay(lastVisit.getVisitDateTime(), untilDate)) {
					// .. we need to do an additional visit
					additionalVisitNeeded = true;
					untilDate = DateHelper.addDays(untilDate, 1);
				}
				// if the last visit was cooperative and happened at the last date of contact tracing,
				// revert the follow-up until date back to the original
				if (!contact.isOverwriteFollowUpUntil()
					&& lastVisit.getVisitStatus() == VisitStatus.COOPERATIVE
					&& DateHelper.isSameDay(lastVisit.getVisitDateTime(), DateHelper.addDays(beginDate, followUpDuration))) {
					additionalVisitNeeded = false;
					untilDate = DateHelper.addDays(beginDate, followUpDuration);
				}
			}
		}
		while (additionalVisitNeeded);
		return untilDate;
	}
}
