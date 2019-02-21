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

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.ResourceBundle;

public class I18nProperties {

	private static I18nProperties instance = null;
	
	private final ResourceBundle captionProperties;
	private final ResourceBundle descriptionProperties;
	private final ResourceBundle enumProperties;
	private final ResourceBundle validationProperties;
	private final ResourceBundle stringProperties;
	
	private static Locale locale;

	private static I18nProperties getInstance() {
		if (instance == null)
			instance = new I18nProperties();
		return instance;
	}
	
	private static Locale getLocale () {
		if (locale == null) {
			locale = Locale.getDefault();
		}
		
		return locale;
	}
	
	public static void setLocale (Locale _locale) {
		if (_locale == null) return;
		
		locale = _locale;
	}
	
	public static void setLocale (String _locale) {
		if (_locale == null) return;
		
		setLocale(new Locale(_locale));
	}
	
	@SuppressWarnings("rawtypes")
	public static String getEnumCaption(Enum value) {
		String caption = getInstance().enumProperties.getString(value.getClass().getSimpleName() + "." + value.name());
		if (caption != null) {
			return caption;
		} else {
			return value.name();
		}
	}
	
	/**
	 * Retrieves the property by adding an additional string in between the class name and the property name,
	 * e.g. Disease.Short.EVD or FollowUpStatus.Desc.NO_FOLLOW_UP
	 * 
	 * Does fallback to enum caption without addition.
	 */
	public static String getEnumCaption(Enum<?> value, String addition) {
		String caption = getInstance().enumProperties.getString(value.getClass().getSimpleName() + "." + addition + "." + value.name());
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
		return getInstance().captionProperties.getString(key, defaultValue);
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
			result = getInstance().captionProperties.getString(prefix+"."+key);
		}
		if (result == null) {
			result = getCaption("."+key, defaultValue);
		}
		return result;
	}
	
	public static String getDescription(String key) {
		return getInstance().descriptionProperties.getString(key);
	}

	public static String getDescription(String key, String defaultValue) {
		return getInstance().descriptionProperties.getString(key, defaultValue);
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
	public static String getValidationError(String key, Object ...formatArgs) {
		String result = getInstance().validationProperties.getString(key, null);
		if (result != null) {
			return String.format(result, formatArgs);
		} else if (formatArgs.length > 0) {
			return formatArgs[0].toString();
		} else {
			return "";
		}
	}

	public static String getPrefixValidationError(String prefix, String key, Object ...formatArgs) {
		String result = null;
		if (prefix != null) {
			result = getInstance().validationProperties.getString(prefix+"."+key);
			if (result != null) {
				return String.format(result, result);
			}
		}
		
		return getValidationError(key, formatArgs);
	}
	
	public static String getString(String property) {
		return getInstance().stringProperties.getString(property);
	}

	public static String getString(String property, String defaultValue) {
		String result = getInstance().stringProperties.getString(property);
		return StringUtils.isEmpty(result) ? defaultValue : result;
	}


	private I18nProperties() {
		captionProperties = loadProperties("captions");
		descriptionProperties = loadProperties("descriptions");
		enumProperties = loadProperties("enum");
		validationProperties = loadProperties("validations");
		stringProperties = loadProperties("strings");
	}
	
	public static ResourceBundle loadProperties(String propertiesGroup) {
		return new ResourceBundle(java.util.ResourceBundle.getBundle(propertiesGroup, getLocale()));
	}
}
