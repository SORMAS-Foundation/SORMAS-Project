package de.symeda.sormas.api;

import java.util.Locale;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum Language {

	EN(new Locale("en")),
	EN_NG(new Locale("en_ng")),
	EN_GH(new Locale("en_gh")),
	FR(new Locale("fr")),
	DE(new Locale("de"));

	private Locale locale;

	Language(Locale locale) {
		this.locale = locale;
	}

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public Locale getLocale() {
		return locale;
	}

	public static Language fromLocaleString(String locale) {
		switch (locale) {
		case "en": return EN;
		case "en_ng": return EN_NG;
		case "en_gh": return EN_GH;
		case "fr": return FR;
		case "de": return DE;
		default: return EN;
		}
	}

}
