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
import de.symeda.sormas.app.component.controls.ControlDateField;
import de.symeda.sormas.app.component.validation.ValidationHelper;
import de.symeda.sormas.app.databinding.FragmentContactEditLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentContactNewLayoutBinding;
import de.symeda.sormas.app.util.ResultCallback;

public final class ContactValidator {

	public static void initializeLastContactDateValidation(final Contact contact, final FragmentContactEditLayoutBinding contentBinding) {
		if (contact != null) {
			ResultCallback<Boolean> firstDateCallBack = () -> {
				Date firstContactDate = contentBinding.contactFirstContactDate.getValue();

				if (firstContactDate == null) {
					return false;
				}

				return validateFirstContactDateIsBeforeLastContactDate(contentBinding.contactFirstContactDate, contentBinding.contactLastContactDate);
			};

			contentBinding.contactFirstContactDate.setValidationCallback(firstDateCallBack);

			ResultCallback<Boolean> lastContactDateCallback = () -> {
				Date lastContactDate = contentBinding.contactLastContactDate.getValue();
				Date contactReferenceDate = contact.getReportDateTime();

				if(lastContactDate == null) {
					return false;
				}

				return validateLastContactDateIsAfterFirstContactDate(contentBinding.contactFirstContactDate, contentBinding.contactLastContactDate)
						|| validateLastContactDateIsBeforeReportDate(contentBinding.contactLastContactDate, contactReferenceDate);
			};
			contentBinding.contactLastContactDate.setValidationCallback(lastContactDateCallback);
		}
	}

	public static void initializeLastContactDateValidation(final Contact contact, final FragmentContactNewLayoutBinding contentBinding) {
		if (contact != null) {
			ResultCallback<Boolean> firstDateCallBack = () -> {
				Date firstContactDate = contentBinding.contactFirstContactDate.getValue();

				if (firstContactDate == null) {
					return false;
				}

				return validateFirstContactDateIsBeforeLastContactDate(contentBinding.contactFirstContactDate, contentBinding.contactLastContactDate);
			};

			contentBinding.contactFirstContactDate.setValidationCallback(firstDateCallBack);

			ResultCallback<Boolean> lastContactDateCallback = () -> {
				Date lastContactDate = contentBinding.contactLastContactDate.getValue();
				Date contactReferenceDate = contact.getReportDateTime();

				if(lastContactDate == null) {
					return false;
				}

				return validateLastContactDateIsAfterFirstContactDate(contentBinding.contactFirstContactDate, contentBinding.contactLastContactDate)
						|| validateLastContactDateIsBeforeReportDate(contentBinding.contactLastContactDate, contactReferenceDate);
			};
			contentBinding.contactLastContactDate.setValidationCallback(lastContactDateCallback);
		}
	}

	private static boolean validateFirstContactDateIsBeforeLastContactDate(final ControlDateField firstContactDateField, final ControlDateField lastContactDateField) {
		Date firstContactDate = firstContactDateField.getValue();
		Date lastContactDate = lastContactDateField.getValue();

		DateTimeComparator dateOnlyComparator = DateTimeComparator.getDateOnlyInstance();

		if (firstContactDate != null && lastContactDate != null && dateOnlyComparator.compare(firstContactDate, lastContactDate) >= 0) {
			firstContactDateField.enableErrorState(
					I18nProperties.getValidationError(
							Validations.beforeDate,
							firstContactDateField.getCaption(),
							I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.LAST_CONTACT_DATE)));
			return true;
		}
		return false;
	}

	private static boolean validateLastContactDateIsAfterFirstContactDate(final ControlDateField firstContactDateField, final ControlDateField lastContactDateField) {
		Date firstContactDate = firstContactDateField.getValue();
		Date lastContactDate = lastContactDateField.getValue();

		DateTimeComparator dateOnlyComparator = DateTimeComparator.getDateOnlyInstance();

		if (firstContactDate != null && lastContactDate != null && dateOnlyComparator.compare(firstContactDate, lastContactDate) >= 0) {
			lastContactDateField.enableErrorState(
					I18nProperties.getValidationError(
							Validations.afterDate,
							lastContactDateField.getCaption(),
							I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.FIRST_CONTACT_DATE)));
			return true;
		}
		return false;
	}

	private static boolean validateLastContactDateIsBeforeReportDate(final ControlDateField lastContactDateField, Date contactReferenceDate) {
		Date lastContactDate = lastContactDateField.getValue();

		DateTimeComparator dateOnlyComparator = DateTimeComparator.getDateOnlyInstance();

		if (contactReferenceDate != null && dateOnlyComparator.compare(lastContactDate, contactReferenceDate) > 0) {
			lastContactDateField.enableErrorState(
					I18nProperties.getValidationError(
							Validations.beforeDate,
							lastContactDateField.getCaption(),
							I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.REPORT_DATE_TIME)));
			return true;
		}

		return false;
	}

	static void initializeProhibitionToWorkIntervalValidator(FragmentContactEditLayoutBinding contentBinding) {
		ValidationHelper.initDateIntervalValidator(contentBinding.contactProhibitionToWorkFrom, contentBinding.contactProhibitionToWorkUntil);
	}
}
