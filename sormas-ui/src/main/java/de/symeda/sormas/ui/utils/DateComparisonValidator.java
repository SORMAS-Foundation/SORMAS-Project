package de.symeda.sormas.ui.utils;

import java.util.Date;

import org.joda.time.DateTimeComparator;

import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.ui.DateField;

/**
 * Compares two date fields. Returns an error if earlierOrSame is true and the value passed to the isValidValue
 * method is not earlier or the same date as the value of the fieldToCompare or if earlierOrSame is false and
 * the date value is not later or the same date as the value of the fieldToCompare.
 */
@SuppressWarnings("serial")
public class DateComparisonValidator extends AbstractValidator<Date> {
	
	private DateField fieldToValidate;
	private DateField fieldToCompare;
	private boolean earlierOrSame;
	private boolean changeInvalidCommitted;
	
	public DateComparisonValidator(DateField fieldToValidate, DateField fieldToCompare, boolean earlierOrSame, boolean changeInvalidCommitted, String errorMessage) {
		super(errorMessage);
		this.fieldToValidate = fieldToValidate;
		this.fieldToCompare = fieldToCompare;
		this.earlierOrSame = earlierOrSame;
		this.changeInvalidCommitted = changeInvalidCommitted;
	}
	
	@Override
	protected boolean isValidValue(Date date) {
		if (date == null || fieldToCompare.getValue() == null) {
			return true;
		}
		
		if (earlierOrSame) {
			if (DateTimeComparator.getDateOnlyInstance().compare(date, fieldToCompare.getValue()) <= 0) {
				if (changeInvalidCommitted) {
					fieldToValidate.setInvalidCommitted(true);
				}
				return true;
			} else {
				if (changeInvalidCommitted) {
					fieldToValidate.setInvalidCommitted(false);
				}
				return false;
			}
		} else {
			if(DateTimeComparator.getDateOnlyInstance().compare(date, fieldToCompare.getValue()) >= 0) {
				if (changeInvalidCommitted) {
					fieldToValidate.setInvalidCommitted(true);
				}
				return true;
			} else {
				if (changeInvalidCommitted) {
					fieldToValidate.setInvalidCommitted(false);
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
