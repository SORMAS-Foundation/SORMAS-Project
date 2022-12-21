package de.symeda.sormas.ui.configuration.validator;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;

public class StringToNumberValidator implements Validator<String> {

	private final String errorMessage;
	private boolean decimalAllowed;
	private boolean positive;

	public StringToNumberValidator() {
		this("Invalid value");
	}

	public StringToNumberValidator(String errorMessage) {
		this(errorMessage, true);
	}

	public StringToNumberValidator(String errorMessage, boolean positive) {
		this(errorMessage, true, positive);
	}

	public StringToNumberValidator(String errorMessage, boolean decimalAllowed, boolean positive) {
		this.errorMessage = errorMessage;
		this.decimalAllowed = decimalAllowed;
		this.positive = positive;
	}

	@Override
	public ValidationResult apply(String value, ValueContext context) {
		if (isValidValue(value)) {
			return ValidationResult.ok();
		} else {
			return ValidationResult.error(errorMessage);
		}
	}

	private boolean isValidValue(String number) {
		if (StringUtils.isBlank(number)) {
			return true;
		}

		Number parsedNumber;
		try {
			parsedNumber = Integer.valueOf(number);
			if (positive && parsedNumber.intValue() < 0) {
				return false;
			}
		} catch (NumberFormatException ie) {
			try {
				parsedNumber = Long.valueOf(number);
				if (positive && parsedNumber.longValue() < 0) {
					return false;
				}
			} catch (NumberFormatException le) {
				if (!decimalAllowed) {
					return false;
				}
				try {
					parsedNumber = Float.valueOf(number);
					if (positive && parsedNumber.floatValue() < 0) {
						return false;
					}
				} catch (NumberFormatException fe) {
					try {
						parsedNumber = Double.valueOf(number);
						if (positive && parsedNumber.doubleValue() < 0) {
							return false;
						}
					} catch (NumberFormatException de) {
						return false;
					}
				}
			}
		}
		return true;
	}
}
