package de.symeda.sormas.ui.utils.components.multidayselector;

import java.time.LocalDate;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import com.vaadin.ui.DateField;

public class DateComparisonValidator implements Validator<LocalDate> {

	private final DateField referenceField;
	private final boolean earlierOrSame;
	private final String errorMessage;

	public DateComparisonValidator(DateField referenceField, boolean earlierOrSame, String errorMessage) {

		this.referenceField = referenceField;
		this.earlierOrSame = earlierOrSame;
		this.errorMessage = errorMessage;
	}

	@Override
	public ValidationResult apply(LocalDate localDate, ValueContext valueContext) {
		LocalDate referenceDate = referenceField.getValue();
		if (localDate == null || referenceDate == null) {
			return ValidationResult.ok();
		}

		if (earlierOrSame) {
			if (localDate.isAfter(referenceDate)) {
				return ValidationResult.error(errorMessage);
			}
		} else {
			if (referenceDate.isAfter(localDate)) {
				return ValidationResult.error(errorMessage);
			}
		}

		return ValidationResult.ok();
	}
}
