package de.symeda.sormas.ui.utils;

import com.vaadin.v7.data.validator.AbstractValidator;

public class UpdatePasswordValidator extends AbstractValidator<String> {

	/**
	 * Constructs a validator with the given error message.
	 *
	 * @param errorMessage
	 *            the message to be included in an {@link InvalidValueException}
	 *            (with "{0}" replaced by the value that failed validation).
	 */
	public UpdatePasswordValidator(String errorMessage) {
		super(errorMessage);
	}

	@Override
	protected boolean isValidValue(String password) {
		String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";
		return password == null || password.isEmpty() || password.matches(pattern);
	}

	@Override
	public Class getType() {
		return String.class;
	}
}
