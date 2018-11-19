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
package de.symeda.sormas.ui.utils;

import java.util.Date;

import org.joda.time.DateTimeComparator;

import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;

/**
 * Compares two date fields. Returns an error if earlierOrSame is true and the value passed to the isValidValue
 * method is not earlier or the same date as the value of the fieldToCompare or if earlierOrSame is false and
 * the date value is not later or the same date as the value of the fieldToCompare.
 */
@SuppressWarnings("serial")
public class DateComparisonValidator extends AbstractValidator<Date> {
	
	private DateField dateFieldToValidate;
	private DateField dateFieldToCompare;
	private DateTimeField dateTimeFieldToValidate;
	private DateTimeField dateTimeFieldToCompare;
	private Date dateToCompare;
	private boolean earlierOrSame;
	private boolean changeInvalidCommitted;
	
	public DateComparisonValidator(DateField fieldToValidate, DateField fieldToCompare, boolean earlierOrSame, boolean changeInvalidCommitted, String errorMessage) {
		super(errorMessage);
		this.dateFieldToValidate = fieldToValidate;
		this.dateFieldToCompare = fieldToCompare;
		this.earlierOrSame = earlierOrSame;
		this.changeInvalidCommitted = changeInvalidCommitted;
	}
	
	public DateComparisonValidator(DateField fieldToValidate, DateTimeField fieldToCompare, boolean earlierOrSame, boolean changeInvalidCommitted, String errorMessage) {
		super(errorMessage);
		this.dateFieldToValidate = fieldToValidate;
		this.dateTimeFieldToCompare = fieldToCompare;
		this.earlierOrSame = earlierOrSame;
		this.changeInvalidCommitted = changeInvalidCommitted;
	}
	
	public DateComparisonValidator(DateField fieldToValidate, Date dateToCompare, boolean earlierOrSame, boolean changeInvalidCommitted, String errorMessage) {
		super(errorMessage);
		this.dateFieldToValidate = fieldToValidate;
		this.dateToCompare = dateToCompare;
		this.earlierOrSame = earlierOrSame;
		this.changeInvalidCommitted = changeInvalidCommitted;
	}

	public DateComparisonValidator(DateTimeField fieldToValidate, DateTimeField fieldToCompare, boolean earlierOrSame, boolean changeInvalidCommitted, String errorMessage) {
		super(errorMessage);
		this.dateTimeFieldToValidate = fieldToValidate;
		this.dateTimeFieldToCompare = fieldToCompare;
		this.earlierOrSame = earlierOrSame;
		this.changeInvalidCommitted = changeInvalidCommitted;
	}

	public DateComparisonValidator(DateTimeField fieldToValidate, DateField fieldToCompare, boolean earlierOrSame, boolean changeInvalidCommitted, String errorMessage) {
		super(errorMessage);
		this.dateTimeFieldToValidate = fieldToValidate;
		this.dateFieldToCompare = fieldToCompare;
		this.earlierOrSame = earlierOrSame;
		this.changeInvalidCommitted = changeInvalidCommitted;
	}
	
	public DateComparisonValidator(DateTimeField fieldToValidate, Date dateToCompare, boolean earlierOrSame, boolean changeInvalidCommitted, String errorMessage) {
		super(errorMessage);
		this.dateTimeFieldToValidate = fieldToValidate;
		this.dateToCompare = dateToCompare;
		this.earlierOrSame = earlierOrSame;
		this.changeInvalidCommitted = changeInvalidCommitted;
	}
	
	@Override
	protected boolean isValidValue(Date date) {
		Field<Date> validationField = dateFieldToValidate != null ? dateFieldToValidate : dateTimeFieldToValidate;
		Field<Date> comparisonField = dateFieldToCompare != null ? dateFieldToCompare : dateTimeFieldToCompare;
		Date comparisonDate = comparisonField != null ? comparisonField.getValue() : dateToCompare;
		
		if (date == null || comparisonDate == null) {
			return true;
		}
		
		if (earlierOrSame) {
			if (DateTimeComparator.getDateOnlyInstance().compare(date, comparisonDate) <= 0) {
				if (changeInvalidCommitted) {
					validationField.setInvalidCommitted(true);
				}
				return true;
			} else {
				if (changeInvalidCommitted) {
					validationField.setInvalidCommitted(false);
				}
				return false;
			}
		} else {
			if(DateTimeComparator.getDateOnlyInstance().compare(date, comparisonDate) >= 0) {
				if (changeInvalidCommitted) {
					validationField.setInvalidCommitted(true);
				}
				return true;
			} else {
				if (changeInvalidCommitted) {
					validationField.setInvalidCommitted(false);
				}
				return false;
			}
		}
	}

	@Override
	public Class<Date> getType() {
		return Date.class;
	}
	
}
