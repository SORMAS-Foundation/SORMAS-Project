package de.symeda.sormas.ui.utils;

import java.util.Date;

import com.vaadin.data.validator.AbstractValidator;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.utils.DateHelper;

@SuppressWarnings("serial")
public class FutureDateValidator extends AbstractValidator<Date> {

	private int allowedDaysInFuture;

	public FutureDateValidator(int allowedDaysInFuture, String caption) {
		super(allowedDaysInFuture > 0 ?
				I18nProperties.getValidationError("futureDate", caption, allowedDaysInFuture) :
					I18nProperties.getValidationError("futureDateStrict", caption));

		this.allowedDaysInFuture = allowedDaysInFuture;
	}

	@Override
	protected boolean isValidValue(Date date) {
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
