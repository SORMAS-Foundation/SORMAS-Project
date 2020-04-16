package de.symeda.sormas.api;

import java.util.Locale;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum Language {

	EN(new Locale("en"), new Locale("en")),
	EN_NG(new Locale("en", "NG"), new Locale("en-NG")),
	EN_GH(new Locale("en", "GH"), new Locale("en-GH")),
	FR(new Locale("fr", "FR"), new Locale("fr-FR")),
	DE(new Locale("de", "DE"), new Locale("de-DE")),
	ES_EC(new Locale("es", "EC"), new Locale("es-EC"));

	private Locale locale;
	private Locale localeWithCountryCode;

	Language(Locale locale, Locale localeWithCountryCode) {
		this.locale = locale;
		this.localeWithCountryCode = localeWithCountryCode;
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

	/**
	 * @return EN when the locale does not fit any language
	 */
	public static Language fromLocaleString(String locale) {
		switch (locale) {
		case "en": return EN;
		case "en-NG": return EN_NG;
		case "en-GH": return EN_GH;
		case "fr-FR": return FR;
		case "de-DE": return DE;
		case "es-EC": return ES_EC;
		default:
			return EN;
		}
	}

}
