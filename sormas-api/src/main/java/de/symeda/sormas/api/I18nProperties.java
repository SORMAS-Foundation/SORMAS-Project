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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class I18nProperties {

	private static I18nProperties instance = null;

	private final Properties fieldCaptionProperties;
	private final Properties fieldDescriptionProperties;
	private final Properties fragmentProperties;
	private final Properties enumProperties;
	private final Properties validationErrorProperties;
	private final Properties messageProperties;
	private final Properties textProperties;

	private static I18nProperties getInstance() {
		if (instance == null)
			instance = new I18nProperties();
		return instance;
	}
	
	@SuppressWarnings("rawtypes")
	public static String getEnumCaption(Enum value) {
		String caption = getInstance().enumProperties.getProperty(value.getClass().getSimpleName() + "." + value.name());
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
		String caption = getInstance().enumProperties.getProperty(value.getClass().getSimpleName() + "." + addition + "." + value.name());
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
		return getInstance().fragmentProperties.getProperty(key, defaultValue);
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
			result = getInstance().fragmentProperties.getProperty(prefix+"."+key);
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
		return getInstance().fieldCaptionProperties.getProperty(key, defaultValue);
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
			result = getInstance().fieldCaptionProperties.getProperty(prefix+"."+key);
		}
		if (result == null) {
			result = getFieldCaption(key, defaultValue);
		}
		return result;
	}
	
	public static String getFieldDescription(String key) {
		return getInstance().fieldDescriptionProperties.getProperty(key);
	}

	public static String getFieldDescription(String key, String defaultValue) {
		return getInstance().fieldDescriptionProperties.getProperty(key, defaultValue);
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
		String result = getInstance().validationErrorProperties.getProperty(key, null);
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
			result = getInstance().validationErrorProperties.getProperty(prefix+"."+key);
			if (result != null) {
				return String.format(result, result);
			}
		}
		
		return getValidationError(key, formatArgs);
	}
	
	public static String getMessage(String property) {
		return getInstance().messageProperties.getProperty(property);
	}
	
	public static String getText(String property) {
		return getInstance().textProperties.getProperty(property);
	}

	private I18nProperties() {
		fieldCaptionProperties = loadProperties("/fieldCaptions.properties");
		fieldDescriptionProperties = loadProperties("/fieldDescriptions.properties");
		fragmentProperties = loadProperties("/fragments.properties");
		enumProperties = loadProperties("/enum.properties");
		validationErrorProperties = loadProperties("/validationErrors.properties");
		messageProperties = loadProperties("/messages.properties");
		textProperties = loadProperties("/texts.properties");
	}
	
	public static Properties loadProperties(String fileName) {
		try (InputStream inputStream = I18nProperties.class.getResourceAsStream(fileName)) {
			Properties properties = new Properties();
			properties.load(inputStream);
			return properties;
		} catch (IOException e) {
			throw new RuntimeException(e);
			// TODO logging
			//logger.error("Could not read file " + fileName, e);
		}
		//return null;
	}
}
