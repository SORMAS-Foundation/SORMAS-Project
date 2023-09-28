package de.symeda.sormas.ui.utils.components;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.v7.data.validator.AbstractValidator;

public class NotBlankTextValidator extends AbstractValidator<Object> {

	public NotBlankTextValidator(String errorMessage) {
		super(errorMessage);
	}

	@Override
	protected boolean isValidValue(Object s) {
		return s != null && s.getClass().isAssignableFrom(String.class) ? StringUtils.isNotBlank((String) s) : s != null;
	}

	@Override
	public Class<Object> getType() {
		return Object.class;
	}
}
