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

import de.symeda.sormas.api.followup.FollowUpLogic;
import de.symeda.sormas.api.followup.FollowUpPeriodDto;
import de.symeda.sormas.api.followup.FollowUpStartDateType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.visit.VisitDto;

public final class ContactLogic {

	private ContactLogic() {
		// Hide Utility Class Constructor
	}

	public static Date getStartDate(ContactDto contactDto) {
		return getStartDate(contactDto.getLastContactDate(), contactDto.getReportDateTime());
	}

	public static Date getStartDate(Date lastContactDate, Date reportDate) {
		return lastContactDate != null ? lastContactDate : reportDate;
	}

	public static FollowUpPeriodDto getFollowUpStartDate(ContactDto contactDto, List<SampleDto> samples) {
		return getFollowUpStartDate(contactDto.getLastContactDate(), contactDto.getReportDateTime(), samples);
	}

	public static FollowUpPeriodDto getFollowUpStartDate(Date lastContactDate, Date reportDate, List<SampleDto> samples) {

		if (lastContactDate != null) {
			return new FollowUpPeriodDto(lastContactDate, FollowUpStartDateType.LAST_CONTACT_DATE);
		}
		return FollowUpLogic.getFollowUpStartDate(reportDate, samples);
	}

	public static FollowUpPeriodDto getFollowUpStartDate(Date lastContactDate, Date reportDate, Date earliestSampleDate) {

		if (lastContactDate != null) {
			return new FollowUpPeriodDto(lastContactDate, FollowUpStartDateType.LAST_CONTACT_DATE);
		}
		return FollowUpLogic.getFollowUpStartDate(reportDate, earliestSampleDate);
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
	public static FollowUpPeriodDto calculateFollowUpUntilDate(
		ContactDto contact,
		FollowUpPeriodDto followUpPeriod,
		List<VisitDto> visits,
		int followUpDuration,
		boolean ignoreOverwrite,
		boolean allowFreeOverwrite) {

		Date overwriteUntilDate = !ignoreOverwrite && contact.isOverwriteFollowUpUntil() ? contact.getFollowUpUntil() : null;
		return FollowUpLogic.calculateFollowUpUntilDate(followUpPeriod, overwriteUntilDate, visits, followUpDuration, allowFreeOverwrite);
	}
}
