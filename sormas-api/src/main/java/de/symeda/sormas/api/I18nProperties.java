package de.symeda.sormas.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class I18nProperties {

	private static I18nProperties instance = null;

	private final Properties fieldCaptionProperties;
	private final Properties fieldDescriptionProperties;
	private final Properties buttonCaptionProperties;
	private final Properties enumProperties;

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
	
	// Retrieves the property by adding an additional string in between the class name and the property name,
	// e.g. Disease.Short.EVD
	@SuppressWarnings("rawtypes")
	public static String getEnumCaption(Enum value, String addition) {
		String caption = getInstance().enumProperties.getProperty(value.getClass().getSimpleName() + "." + addition + "." + value.name());
		if (caption != null) {
			return caption;
		} else {
			return value.name();
		}
	}
	
	/**
	 * Uses <param>key</param> as default value
	 */
	public static String getButtonCaption(String key) {
		return getButtonCaption(key, key);
	}

	public static String getButtonCaption(String key, String defaultValue) {
		return getInstance().buttonCaptionProperties.getProperty(key, defaultValue);
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

	private I18nProperties() {
		fieldCaptionProperties = loadProperties("/fieldCaptions.properties");
		fieldDescriptionProperties = loadProperties("/fieldDescriptions.properties");
		buttonCaptionProperties = loadProperties("/buttonCaptions.properties");
		enumProperties = loadProperties("/enum.properties");
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
