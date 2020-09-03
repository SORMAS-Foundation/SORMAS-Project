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

package de.symeda.sormas.app.contact.edit;

import java.util.Date;

import org.joda.time.DateTimeComparator;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.databinding.FragmentContactEditLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentContactNewLayoutBinding;
import de.symeda.sormas.app.util.ResultCallback;

public final class ContactValidator {

	public static void initializeValidation(final Contact contact, final FragmentContactEditLayoutBinding contentBinding) {
		if (contact != null) {
			ResultCallback<Boolean> lastContactDateCallback = () -> {
				Date lastContactDate = contentBinding.contactLastContactDate.getValue();
				Date contactReferenceDate = contact.getReportDateTime();
				if (lastContactDate != null
					&& contactReferenceDate != null
					&& DateTimeComparator.getDateOnlyInstance().compare(lastContactDate, contactReferenceDate) > 0) {
					contentBinding.contactLastContactDate.enableErrorState(
						I18nProperties.getValidationError(
							Validations.beforeDate,
							contentBinding.contactLastContactDate.getCaption(),
							contentBinding.contactReportDateTime.getCaption()));
					return true;
				}

				return false;
			};
			contentBinding.contactLastContactDate.setValidationCallback(lastContactDateCallback);
		}
	}

	public static void initializeValidation(final Contact contact, final FragmentContactNewLayoutBinding contentBinding) {
		if (contact != null) {
			ResultCallback<Boolean> lastContactDateCallback = () -> {
				Date lastContactDate = contentBinding.contactLastContactDate.getValue();
				Date contactReferenceDate = contact.getReportDateTime();
				if (lastContactDate != null
					&& contactReferenceDate != null
					&& DateTimeComparator.getDateOnlyInstance().compare(lastContactDate, contactReferenceDate) > 0) {
					contentBinding.contactLastContactDate.enableErrorState(
						I18nProperties.getValidationError(
							Validations.beforeDate,
							contentBinding.contactLastContactDate.getCaption(),
							I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.REPORT_DATE_TIME)));
					return true;
				}

				return false;
			};
			contentBinding.contactLastContactDate.setValidationCallback(lastContactDateCallback);
		}
	}
}
