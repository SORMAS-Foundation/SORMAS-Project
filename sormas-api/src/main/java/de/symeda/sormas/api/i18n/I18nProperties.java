/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle.Control;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.ResourceBundle;

public class I18nProperties {

	private static Map<Language, I18nProperties> instances = new HashMap<>();
	private static ThreadLocal<Language> userLanguage = new ThreadLocal<>();

	private static Language defaultLanguage;

	private final ResourceBundle captionProperties;
	private final ResourceBundle descriptionProperties;
	private final ResourceBundle enumProperties;
	private final ResourceBundle validationProperties;
	private final ResourceBundle stringProperties;

	private static I18nProperties getInstance(Language language) {
		if (language == null) {
			Language defaultLanguage = getDefaultLanguage();
			if (defaultLanguage != null) {
				language = defaultLanguage;
			} else {
				language = Language.EN;
			}
		}

		I18nProperties instance = instances.get(language);
		if (instance == null) {
			instances.put(language, new I18nProperties(language));
			instance = instances.get(language);
		}

		return instance;
	}

	public static Language setUserLanguage(Language language) {
		if (language == null) {
			language = getDefaultLanguage();
		}
		userLanguage.set(language);

		return language;
	}

	public static Language getUserLanguage() {
		Language language = userLanguage.get();
		return language == null ? getDefaultLanguage() : language;
	}

	public static void removeUserLanguage() {
		userLanguage.remove();
	}

	public static void setDefaultLanguage(Language language) {
		if (language != null) {
			defaultLanguage = language;
		}
	}

	private static Language getDefaultLanguage() {
		if (defaultLanguage == null) {
			return Language.EN;
		}

		return defaultLanguage;
	}

	@SuppressWarnings("rawtypes")
	public static String getEnumCaption(Enum value) {
		String caption = getInstance(userLanguage.get()).enumProperties.getString(value.getClass().getSimpleName() + "." + value.name());
		if (caption != null) {
			return caption;
		} else {
			return value.name();
		}
	}

	/**
	 * Retrieves the property by adding an additional string in between the class name and the property name,
	 * e.g. Disease.Short.EVD or FollowUpStatus.Desc.NO_FOLLOW_UP
	 * <p>
	 * Does fallback to enum caption without addition.
	 */
	public static String getEnumCaption(Enum<?> value, String addition) {
		String caption = getInstance(userLanguage.get()).enumProperties.getString(value.getClass().getSimpleName() + "." + addition + "." + value.name());
		if (caption != null) {
			return caption;
		} else {
			return getEnumCaption(value);
		}
	}

	public static String getEnumCaptionShort(Enum<?> value) {
		return getEnumCaption(value, "Short");
	}

	public static String getEnumDescription(Enum<?> value) {
		return getEnumCaption(value, "Desc");
	}

	/**
	 * Uses <param>key</param> as default value
	 */
	public static String getCaption(String key) {
		return getCaption(key, key);
	}

	public static String getCaption(String key, String defaultValue) {
		return getInstance(userLanguage.get()).captionProperties.getString(key, defaultValue);
	}

	/**
	 * Uses <param>key</param> as default value
	 */
	public static String getPrefixCaption(String prefix, String key) {
		return getPrefixCaption(prefix, key, key);
	}

	public static String getPrefixCaption(String prefix, String key, String defaultValue) {
		String result = null;
		if (prefix != null) {
			result = getInstance(userLanguage.get()).captionProperties.getString(prefix + "." + key);
		}
		if (result == null) {
			result = getCaption(key, defaultValue);
		}
		return result;
	}

	public static String getDescription(String key) {
		return getInstance(userLanguage.get()).descriptionProperties.getString(key);
	}

	public static String getDescription(String key, String defaultValue) {
		return getInstance(userLanguage.get()).descriptionProperties.getString(key, defaultValue);
	}

	/**
	 * Uses <param>key</param> as default value
	 */
	public static String getPrefixDescription(String prefix, String key) {
		return getPrefixDescription(prefix, key, key);
	}

	public static String getPrefixDescription(String prefix, String key, String defaultValue) {
		String result = null;
		if (prefix != null) {
			result = getDescription(prefix + "." + key);
		}
		if (result == null) {
			result = getDescription(key, defaultValue);
		}
		return result;
	}

	public static String getRequiredError(String fieldCaption) {
		return getValidationError(Validations.required, fieldCaption);
	}

	/**
	 * Uses <param>key</param> as default value
	 */
	public static String getValidationError(String key, Object... formatArgs) {
		String result = getInstance(userLanguage.get()).validationProperties.getString(key, null);
		if (result != null) {
			return String.format(result, formatArgs);
		} else if (formatArgs.length > 0 && formatArgs[0] != null) {
			return formatArgs[0].toString();
		} else {
			return "";
		}
	}

	public static String getPrefixValidationError(String prefix, String key, Object... formatArgs) {
		String result = null;
		if (prefix != null) {
			result = getInstance(userLanguage.get()).validationProperties.getString(prefix + "." + key);
			if (result != null) {
				return String.format(result, result);
			}
		}

		return getValidationError(key, formatArgs);
	}

	public static String getString(String property) {
		return getInstance(userLanguage.get()).stringProperties.getString(property);
	}

	public static String getString(String property, String defaultValue) {
		String result = getInstance(userLanguage.get()).stringProperties.getString(property);
		return StringUtils.isEmpty(result) ? defaultValue : result;
	}


	private I18nProperties() {
		this(defaultLanguage);
	}

	private I18nProperties(Language language) {
		this.captionProperties = loadProperties("captions", language.getLocaleWithCountryCode());
		this.descriptionProperties = loadProperties("descriptions", language.getLocaleWithCountryCode());
		this.enumProperties = loadProperties("enum", language.getLocaleWithCountryCode());
		this.validationProperties = loadProperties("validations", language.getLocaleWithCountryCode());
		this.stringProperties = loadProperties("strings", language.getLocaleWithCountryCode());
	}

	public static ResourceBundle loadProperties(String propertiesGroup, Locale locale) {
		return new ResourceBundle(java.util.ResourceBundle.getBundle(propertiesGroup, locale, new UTF8Control()));
	}

	public static class UTF8Control extends Control {
		public java.util.ResourceBundle newBundle
				(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
				throws IllegalAccessException, InstantiationException, IOException {
			// The below is a copy of the default implementation.
			String bundleName = toBundleName(baseName, locale);
			if (bundleName.contains("-")) {
				// A simple "replace" does not work here because the country code could be identical to the language code
				String countryCode = bundleName.substring(bundleName.lastIndexOf("-") + 1).toUpperCase();
				bundleName = bundleName.substring(0, bundleName.lastIndexOf("-") + 1) + countryCode;
			}
			String resourceName = toResourceName(bundleName, "properties");
			java.util.ResourceBundle bundle = null;
			InputStream stream = null;
			if (reload) {
				URL url = loader.getResource(resourceName);
				if (url != null) {
					URLConnection connection = url.openConnection();
					if (connection != null) {
						connection.setUseCaches(false);
						stream = connection.getInputStream();
					}
				}
			} else {
				stream = loader.getResourceAsStream(resourceName);
			}
			if (stream != null) {
				try {
					// Only this line is changed to make it to read properties files as UTF-8.
					bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
				} finally {
					stream.close();
				}
			}
			return bundle;
		}
	}

}
