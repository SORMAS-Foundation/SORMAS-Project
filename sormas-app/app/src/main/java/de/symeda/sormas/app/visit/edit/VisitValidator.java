/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.visit.edit;

import java.util.Date;

import org.joda.time.DateTimeComparator;

import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.databinding.FragmentVisitEditLayoutBinding;
import de.symeda.sormas.app.util.ResultCallback;

public final class VisitValidator {

	public static void initializeVisitValidation(final Contact contact, final FragmentVisitEditLayoutBinding contentBinding) {
		if (contact != null) {
			ResultCallback<Boolean> visitDateTimeCallback = () -> {
				Date visitDateTime = (Date) contentBinding.visitVisitDateTime.getValue();
				Date contactReferenceDate = contact.getLastContactDate() != null ? contact.getLastContactDate() : contact.getReportDateTime();
				if (DateTimeComparator.getDateOnlyInstance().compare(visitDateTime, contactReferenceDate) < 0
					&& DateHelper.getDaysBetween(visitDateTime, contactReferenceDate) > VisitDto.ALLOWED_CONTACT_DATE_OFFSET) {
					contentBinding.visitVisitDateTime.enableErrorState(
						contact.getLastContactDate() != null
							? R.string.validation_visit_date_time_before_contact_date
							: R.string.validation_visit_date_time_before_report_date);
					return true;
				} else if (contact.getFollowUpUntil() != null
					&& DateTimeComparator.getDateOnlyInstance().compare(visitDateTime, contact.getFollowUpUntil()) > 0
					&& DateHelper.getDaysBetween(contact.getFollowUpUntil(), visitDateTime) > VisitDto.ALLOWED_CONTACT_DATE_OFFSET) {
					contentBinding.visitVisitDateTime.enableErrorState(R.string.validation_visit_date_time_after_followup);
					return true;
				}

				return false;
			};

			contentBinding.visitVisitDateTime.setValidationCallback(visitDateTimeCallback);
		}
	}
}
