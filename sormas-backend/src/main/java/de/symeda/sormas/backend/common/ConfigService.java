package de.symeda.sormas.backend.common;

import java.util.Properties;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the application configuration settings
 */
@Stateless(name="ConfigService")
@LocalBean
public class ConfigService {

	private static final String COUNTRY_NAME = "country.name";

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(ConfigService.class);

	@Resource(lookup="sormas/Properties")
	private Properties props;

	public String getProperty(String name){
		return getProp(name, null);
	}

	private String getProp(String name, String defaultValue) {
		String prop = props.getProperty(name);

		if (prop == null){
			return defaultValue;
		} else {
			return prop.trim();
		}
	}
	
	public String getCountryName() {
		return getProperty(COUNTRY_NAME, "nigeria");
	}

	private String getProperty(String name, String defaultValue){
		return getProp(name, defaultValue);
	}
	
	private boolean getBoolean(String name, boolean defaultValue) {
		return Boolean.parseBoolean(getProperty(name, Boolean.toString(defaultValue)));
	}
}
