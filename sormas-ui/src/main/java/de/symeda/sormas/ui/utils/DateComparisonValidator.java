/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.utils;

import java.util.Collection;
import java.util.Date;
import java.util.function.Supplier;

import org.joda.time.DateTimeComparator;

import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.validator.AbstractValidator;
import com.vaadin.v7.ui.Field;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;

/**
 * Compares the value of a date field to a supplied reference date.
 * Returns an error if earlierOrSame is true and the value is later as the reference date or vice versa.
 */
@SuppressWarnings("serial")
public class DateComparisonValidator extends AbstractValidator<Date> {

	private Field<Date> dateField;
	private Supplier<Date> referenceDateSupplier;
	private boolean earlierOrSame;
	private boolean changeInvalidCommitted;
	private boolean dateOnly = true;

	public DateComparisonValidator(
		Field<Date> dateField,
		Supplier<Date> referenceDateSupplier,
		boolean earlierOrSame,
		boolean changeInvalidCommitted,
		String errorMessage) {

		super(errorMessage);
		this.dateField = dateField;
		this.referenceDateSupplier = referenceDateSupplier;
		this.earlierOrSame = earlierOrSame;
		this.changeInvalidCommitted = changeInvalidCommitted;
	}

	public DateComparisonValidator(
		Field<Date> dateField,
		Field<Date> referenceField,
		boolean earlierOrSame,
		boolean changeInvalidCommitted,
		String errorMessage) {

		this(dateField, () -> referenceField.getValue(), earlierOrSame, changeInvalidCommitted, errorMessage);
	}

	public DateComparisonValidator(
		Field<Date> dateField,
		Date referenceDate,
		boolean earlierOrSame,
		boolean changeInvalidCommitted,
		String errorMessage) {

		this(dateField, () -> referenceDate, earlierOrSame, changeInvalidCommitted, errorMessage);
	}

	@Override
	protected boolean isValidValue(Date date) {

		Date referenceDate = referenceDateSupplier.get();
		if (date == null || referenceDate == null) {
			return true;
		}

		if (earlierOrSame) {
			DateTimeComparator comparator = dateOnly ? DateTimeComparator.getDateOnlyInstance() : DateTimeComparator.getInstance();
			if (comparator.compare(date, referenceDate) <= 0) {
				if (changeInvalidCommitted) {
					dateField.setInvalidCommitted(true);
				}
				return true;
			} else {
				if (changeInvalidCommitted) {
					dateField.setInvalidCommitted(false);
				}
				return false;
			}
		} else {
			DateTimeComparator comparator = dateOnly ? DateTimeComparator.getDateOnlyInstance() : DateTimeComparator.getInstance();
			if (comparator.compare(date, referenceDate) >= 0) {
				if (changeInvalidCommitted) {
					dateField.setInvalidCommitted(true);
				}
				return true;
			} else {
				if (changeInvalidCommitted) {
					dateField.setInvalidCommitted(false);
				}
				return false;
			}
		}
	}

	@Override
	public Class<Date> getType() {
		return Date.class;
	}

	public boolean isDateOnly() {
		return dateOnly;
	}

	public void setDateOnly(boolean dateOnly) {
		this.dateOnly = dateOnly;
	}

	public static void addStartEndValidators(Field<Date> startDate, Field<Date> endDate) {
		addStartEndValidators(startDate, endDate, true);
	}

	public static void addStartEndValidators(Field<Date> startDate, Field<Date> endDate, boolean dateOnly) {
		DateComparisonValidator startDateValidator = new DateComparisonValidator(
			startDate,
			endDate,
			true,
			false,
			I18nProperties.getValidationError(Validations.beforeDate, startDate.getCaption(), endDate.getCaption()));
		DateComparisonValidator endDateValidator = new DateComparisonValidator(
			endDate,
			startDate,
			false,
			false,
			I18nProperties.getValidationError(Validations.afterDate, endDate.getCaption(), startDate.getCaption()));

		startDate.addValidator(startDateValidator);
		endDate.addValidator(endDateValidator);

		startDateValidator.setDateOnly(dateOnly);
		endDateValidator.setDateOnly(dateOnly);
	}

	public static void removeDateComparisonValidators(Field<Date> dateField) {
		Collection<Validator> validators = dateField.getValidators();
		for (Validator validator : validators) {
			if (validator instanceof DateComparisonValidator) {
				dateField.removeValidator(validator);
			}
		}
	}
}
