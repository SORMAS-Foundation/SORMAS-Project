package de.symeda.sormas.ui.utils;

import com.vaadin.data.validator.AbstractValidator;

@SuppressWarnings("serial")
public class PhoneNumberValidator extends AbstractValidator<String> {
	
	public PhoneNumberValidator(String errorMessage) {
		super(errorMessage);
	}
	
	@Override
	protected boolean isValidValue(String phoneNumber) {
		return phoneNumber == null  || phoneNumber.isEmpty() || phoneNumber.startsWith("+");
	}
	
	@Override
	public Class<String> getType() {
		return String.class;
	}

}
