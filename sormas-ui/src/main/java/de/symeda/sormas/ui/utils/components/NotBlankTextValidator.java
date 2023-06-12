package de.symeda.sormas.ui.utils.components;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.v7.data.validator.AbstractValidator;

public class NotBlankTextValidator extends AbstractValidator<String> {

	public NotBlankTextValidator(String errorMessage) {
		super(errorMessage);
	}

	@Override
	protected boolean isValidValue(String s) {

		return StringUtils.isNotBlank(s);
	}

	@Override
	public Class<String> getType() {
		return String.class;
	}
}
