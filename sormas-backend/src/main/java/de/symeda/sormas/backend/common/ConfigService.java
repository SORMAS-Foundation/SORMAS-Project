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
	
	private static final String EMAIL_SENDER_ADDRESS = "email.sender.address";
	private static final String EMAIL_SENDER_NAME = "email.sender.name";
	private static final String EMAIL_RECIPIENT = "email.recipient";
	
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
	
	public String getEmailSenderAddress() {
		return getProperty(EMAIL_SENDER_ADDRESS, "noreply@sormas.org");
	}
	
	public String getEmailSenderName() {
		return getProperty(EMAIL_SENDER_NAME, "SORMAS Support");
	}
	
	public String getEmailRecipient() {
		return getProperty(EMAIL_RECIPIENT, "norecipient@example.com");
	}

	private String getProperty(String name, String defaultValue){
		return getProp(name, defaultValue);
	}
	
	private boolean getBoolean(String name, boolean defaultValue) {
		return Boolean.parseBoolean(getProperty(name, Boolean.toString(defaultValue)));
	}
}
