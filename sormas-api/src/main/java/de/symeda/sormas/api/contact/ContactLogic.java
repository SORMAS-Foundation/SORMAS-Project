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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.contact;

import java.util.Date;

import de.symeda.sormas.api.utils.DateHelper;

public final class ContactLogic {

	private ContactLogic() {
		// Hide Utility Class Constructor
	}

	public static int getNumberOfRequiredVisitsSoFar(Date contactReportDate, Date contactFollowUpUntil) {
		if (contactFollowUpUntil == null) {
			return 0;
		}
		
		Date now = new Date();
		if (now.before(contactFollowUpUntil)) {
			return DateHelper.getDaysBetween(DateHelper.addDays(contactReportDate, 1), now);
		} else {
			return DateHelper.getDaysBetween(DateHelper.addDays(contactReportDate, 1), contactFollowUpUntil);
		}
	}
	
	public static Date getStartDate(Date lastContactDate, Date reportDate) {
		if (lastContactDate != null) {
			return lastContactDate;
		} else {
			return reportDate;
		}
	}
}
