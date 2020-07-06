package de.symeda.sormas.api;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum Language {

	EN(new Locale("en"), "M/d/yyyy", "M/d/yyyy h:mm a", "M/d"),
	EN_NG(new Locale("en", "NG"), "dd/MM/yyyy", "dd/MM/yyyy h:mm a", "dd/MM"),
	EN_GH(new Locale("en", "GH"), "dd/MM/yyyy", "dd/MM/yyyy h:mm a", "dd/MM"),
	FR(new Locale("fr", "FR"), "dd/MM/yyyy", "dd/MM/yyyy HH:mm", "dd/MM"),
	FR_CH(new Locale("fr", "CH"), "dd/MM/yyyy", "dd/MM/yyyy HH:mm", "dd/MM"),
	DE(new Locale("de", "DE"), "dd.MM.yyyy", "dd.MM.yyyy HH:mm", "dd.MM"),
	DE_CH(new Locale("de", "CH"), "dd.MM.yyyy", "dd.MM.yyyy HH:mm", "dd.MM"),
	ES_EC(new Locale("es", "EC"), "dd/MM/yyyy", "dd/MM/yyyy H:mm", "dd/MM"),
	ES_CU(new Locale("es", "CU"), "dd/MM/yyyy", "dd/MM/yyyy H:mm", "dd/MM"),
	IT(new Locale("it", "IT"), "dd/MM/yyyy", "dd/MM/yyyy H:mm", "dd/MM"),
	IT_CH(new Locale("it", "CH"), "dd/MM/yyyy", "dd/MM/yyyy H:mm", "dd/MM"),
	FI(new Locale("fi", "FI"), "dd.MM.yyyy", "dd.MM.yyyy H.mm", "dd.MM");

	/**
	 * Links locale strings to Languages
	 */
	private static final Map<String, Language> languageLookup;
	static {
		languageLookup = new HashMap<>();
		for (Language lang : Language.values()) {
			Locale locale = lang.getLocale();
			languageLookup.put(createKey(locale.getLanguage(), locale.getCountry()), lang);

			//add first Language enum as default for the plain language Locale
			String langKey = createKey(locale.getLanguage(), "");
			if (!languageLookup.containsKey(langKey)) {
				languageLookup.put(langKey, lang);
			}
		}
	}

	private Locale locale;
	private String dateFormat;
	private String dateTimeFormat;
	private String dayMonthFormat;

	Language(Locale locale, String dateFormat, String dateTimeFormat, String dayMonthFormat) {

		this.locale = locale;
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

		if (StringUtils.isBlank(locale)) {
			return EN;
		}

		String key = createKey(locale.trim().split("[_-]"));

		Language language = languageLookup.get(key);

		if (language == null) {
			language = EN;
		}

		return language;
	}

	private static String createKey(String... localeComponents) {

		//only language and country are factored in
		StringBuilder sb = new StringBuilder(5);
		sb.append(localeComponents[0].toLowerCase());

		if (localeComponents.length > 1) {
			String c = localeComponents[1];
			if (StringUtils.isNotEmpty(c)) {
				sb.append('-').append(c.toUpperCase());
			}
		}
		return sb.toString();
	}
}
