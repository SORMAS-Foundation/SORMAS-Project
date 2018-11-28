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
package de.symeda.sormas.api;

//import java.io.IOException;
//import java.io.InputStream;
import java.util.Locale;
//import java.util.Properties;

public class I18nProperties {

	private static I18nProperties instance = null;
	
	private final ResourceBundle fieldCaptionProperties;
	private final ResourceBundle fieldDescriptionProperties;
	private final ResourceBundle fragmentProperties;
	private final ResourceBundle enumProperties;
	private final ResourceBundle validationErrorProperties;
	private final ResourceBundle messageProperties;
	private final ResourceBundle textProperties;
	
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
	public static String getFragment(String key) {
		return getFragment(key, key);
	}

	public static String getFragment(String key, String defaultValue) {
		return getInstance().fragmentProperties.getString(key, defaultValue);
	}

	/**
	 * Uses <param>key</param> as default value
	 */
	public static String getPrefixFragment(String prefix, String key) {
		return getPrefixFragment(prefix, key, key);
	}
	
	public static String getPrefixFragment(String prefix, String key, String defaultValue) {
		String result = null;
		if (prefix != null) {
			result = getInstance().fragmentProperties.getString(prefix+"."+key);
		}
		if (result == null) {
			result = getFragment(key, defaultValue);
		}
		return result;
	}
	
	/**
	 * Uses <param>key</param> as default value
	 */
	public static String getFieldCaption(String key) {
		return getFieldCaption(key, key);
	}

	public static String getFieldCaption(String key, String defaultValue) {
		return getInstance().fieldCaptionProperties.getString(key, defaultValue);
	}

	/**
	 * Uses <param>key</param> as default value
	 */
	public static String getPrefixFieldCaption(String prefix, String key) {
		return getPrefixFieldCaption(prefix, key, key);
	}
	
	public static String getPrefixFieldCaption(String prefix, String key, String defaultValue) {
		String result = null;
		if (prefix != null) {
			result = getInstance().fieldCaptionProperties.getString(prefix+"."+key);
		}
		if (result == null) {
			result = getFieldCaption(key, defaultValue);
		}
		return result;
	}
	
	public static String getFieldDescription(String key) {
		return getInstance().fieldDescriptionProperties.getString(key);
	}

	public static String getFieldDescription(String key, String defaultValue) {
		return getInstance().fieldDescriptionProperties.getString(key, defaultValue);
	}

	/**
	 * Uses <param>key</param> as default value
	 */
	public static String getPrefixFieldDescription(String prefix, String key) {
		return getPrefixFieldDescription(prefix, key, key);
	}

	public static String getPrefixFieldDescription(String prefix, String key, String defaultValue) {
		String result = null;
		if (prefix != null) {
			result = getFieldDescription(prefix + "." + key);
		}
		if (result == null) {
			result = getFieldDescription(key, defaultValue);
		}
		return result;
	}
	
	public static String getRequiredError(String fieldCaption) {
		return getValidationError("required", fieldCaption);
	}
	
	/**
	 * Uses <param>key</param> as default value
	 */
	public static String getValidationError(String key, Object ...formatArgs) {
		String result = getInstance().validationErrorProperties.getString(key, null);
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
			result = getInstance().validationErrorProperties.getString(prefix+"."+key);
			if (result != null) {
				return String.format(result, result);
			}
		}
		
		return getValidationError(key, formatArgs);
	}
	
	public static String getMessage(String property) {
		return getInstance().messageProperties.getString(property);
	}
	
	public static String getText(String property) {
		return getInstance().textProperties.getString(property);
	}

	private I18nProperties() {
		fieldCaptionProperties = loadProperties("fieldCaptions");
		fieldDescriptionProperties = loadProperties("fieldDescriptions");
		fragmentProperties = loadProperties("fragments");
		enumProperties = loadProperties("enum");
		validationErrorProperties = loadProperties("validationErrors");
		messageProperties = loadProperties("messages");
		textProperties = loadProperties("texts");
	}
	
	public static ResourceBundle loadProperties(String propertiesGroup) {
		return new ResourceBundle(java.util.ResourceBundle.getBundle(propertiesGroup, getLocale()));
		
//		try (InputStream inputStream = I18nProperties.class.getResourceAsStream(fileName)) {
//			Properties properties = new Properties();
//			properties.load(inputStream);
//			return properties;
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//			// TODO logging
//			//logger.error("Could not read file " + fileName, e);
//		}
//		//return null;
	}
}
