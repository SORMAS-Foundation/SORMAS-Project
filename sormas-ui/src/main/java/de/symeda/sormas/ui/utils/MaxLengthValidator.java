package de.symeda.sormas.ui.utils;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;

public class MaxLengthValidator extends com.vaadin.v7.data.validator.StringLengthValidator {

	public MaxLengthValidator(Integer maxLength) {
		super(I18nProperties.getValidationError(Validations.textTooLong, maxLength), 0, maxLength, true);
	}
}
