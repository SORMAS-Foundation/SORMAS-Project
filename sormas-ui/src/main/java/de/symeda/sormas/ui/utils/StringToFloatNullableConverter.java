package de.symeda.sormas.ui.utils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Optional;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;

public class StringToFloatNullableConverter implements Converter<String, Float> {

	private final String fieldCaption;

	public StringToFloatNullableConverter(String fieldCaption) {
		this.fieldCaption = fieldCaption;
	}

	@Override
	public Result<Float> convertToModel(String value, ValueContext context) {
		if (value == null || value.trim().isEmpty()) {
			return Result.ok(null);
		}
		try {
			NumberFormat numberFormat = NumberFormat.getInstance(context.getLocale().orElse(Locale.getDefault()));
			Number number = numberFormat.parse(value.trim());
			return Result.ok(number.floatValue());
		} catch (ParseException e) {
			return Result.error(I18nProperties.getValidationError(Validations.onlyNumbersAllowed, fieldCaption));
		}
	}

	/**
	 * Parses a string to a float using the given locale's number format.
	 * Returns empty if the value is null, blank, or not a valid number.
	 */
	public static Optional<Float> parseLocaleAware(String value, Locale locale) {
		if (value == null || value.trim().isEmpty()) {
			return Optional.empty();
		}
		try {
			NumberFormat numberFormat = NumberFormat.getInstance(locale);
			Number number = numberFormat.parse(value.trim());
			return Optional.of(number.floatValue());
		} catch (ParseException e) {
			return Optional.empty();
		}
	}

	@Override
	public String convertToPresentation(Float value, ValueContext context) {
		if (value == null) {
			return "";
		}
		NumberFormat numberFormat = NumberFormat.getInstance(context.getLocale().orElse(Locale.getDefault()));
		return numberFormat.format(value);
	}
}
