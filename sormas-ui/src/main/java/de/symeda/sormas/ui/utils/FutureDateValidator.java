package de.symeda.sormas.ui.utils;

import java.util.Date;

import com.vaadin.v7.data.validator.AbstractValidator;
import com.vaadin.v7.ui.Field;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.DateHelper;

@SuppressWarnings("serial")
public class FutureDateValidator extends AbstractValidator<Date> {

	private Field<?> field;
	private int allowedDaysInFuture;

	public FutureDateValidator(Field<?> field, int allowedDaysInFuture, String caption) {

		super(
			allowedDaysInFuture > 0
				? I18nProperties.getValidationError(Validations.futureDate, caption, allowedDaysInFuture)
				: I18nProperties.getValidationError(Validations.futureDateStrict, caption));

		this.field = field;
		this.allowedDaysInFuture = allowedDaysInFuture;
	}

	@Override
	protected boolean isValidValue(Date date) {
		if (field.isReadOnly()) {
			return true;
		}

		if (date == null) {
			return true;
		}

		if (date.before(new Date())) {
			return true;
		}

		if (allowedDaysInFuture > 0) {
			return DateHelper.getFullDaysBetween(new Date(), date) <= allowedDaysInFuture;
		} else {
			return DateHelper.isSameDay(new Date(), date);
		}
	}

	@Override
	public Class<Date> getType() {
		return Date.class;
	}
}
