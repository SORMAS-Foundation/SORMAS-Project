package de.symeda.sormas.api;

import java.util.Locale;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum Language {

	EN(new Locale("en"), new Locale("en"), "M/d/yyyy", "M/d/yyyy h:mm a", "M/d"),
	EN_NG(new Locale("en", "NG"), new Locale("en-NG"), "M/d/yyyy", "M/d/yyyy h:mm a", "M/d"),
	EN_GH(new Locale("en", "GH"), new Locale("en-GH"), "M/d/yyyy", "M/d/yyyy h:mm a", "M/d"),
	FR(new Locale("fr", "FR"), new Locale("fr-FR"), "dd/MM/yyyy", "dd/MM/yyyy HH:mm", "dd/MM"),
	DE(new Locale("de", "DE"), new Locale("de-DE"), "dd.MM.yyyy", "dd.MM.yyyy HH:mm", "dd.MM"),
	ES_EC(new Locale("es", "EC"), new Locale("es-EC"), "dd/MM/yyyy", "dd/MM/yyyy H:mm", "dd/MM");

	private Locale locale;
	private Locale localeWithCountryCode;
	private String dateFormat;
	private String dateTimeFormat;
	private String dayMonthFormat;

	Language(Locale locale, Locale localeWithCountryCode, String dateFormat, String dateTimeFormat, String dayMonthFormat) {
		this.locale = locale;
		this.localeWithCountryCode = localeWithCountryCode;
		this.dateFormat = dateFormat;
		this.dateTimeFormat = dateTimeFormat;
		this.dayMonthFormat = dayMonthFormat;
	}

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public Locale getLocale() {
		return locale;
	}

	public Locale getLocaleWithCountryCode() {
		return localeWithCountryCode;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public String getDateTimeFormat() {
		return dateTimeFormat;
	}

	public String getDayMonthFormat() {
		return dayMonthFormat;
	}

	/**
	 * @return EN when the locale does not fit any language
	 */
	public static Language fromLocaleString(String locale) {
		switch (locale) {
			case "en":
				return EN;
			case "en-NG":
				return EN_NG;
			case "en-GH":
				return EN_GH;
			case "fr-FR":
				return FR;
			case "de-DE":
				return DE;
			case "es-EC":
				return ES_EC;
			default:
				return EN;
		}
	}

}
