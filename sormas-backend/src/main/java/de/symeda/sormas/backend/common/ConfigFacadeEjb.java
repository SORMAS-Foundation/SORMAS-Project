package de.symeda.sormas.backend.common;

import java.util.Properties;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.ConfigFacade;

/**
 * Provides the application configuration settings
 */
@Stateless(name="ConfigFacade")
public class ConfigFacadeEjb implements ConfigFacade {

	private static final String COUNTRY_NAME = "country.name";
	
	private static final String APP_URL = "app.url";
	
	private static final String TEMP_FILES_PATH = "temp.path";
	
	private static final String EMAIL_SENDER_ADDRESS = "email.sender.address";
	private static final String EMAIL_SENDER_NAME = "email.sender.name";
	private static final String SMS_SENDER_NAME = "sms.sender.name";
	private static final String SMS_AUTH_KEY = "sms.auth.key";
	private static final String SMS_AUTH_SECRET = "sms.auth.secret";
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(ConfigFacadeEjb.class);

	@Resource(lookup="sormas/Properties")
	private Properties props;

	@Override
	public String getProperty(String name) {
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

	@Override
	public String getCountryName() {
		return getProperty(COUNTRY_NAME, "nigeria");
	}

	@Override
	public String getAppUrl() {
		return getProperty(APP_URL, "");
	}
	
	@Override
	public String getTempFilesPath() {
		return getProperty(TEMP_FILES_PATH, "/opt/sormas-temp/");
	}

	@Override
	public String getEmailSenderAddress() {
		return getProperty(EMAIL_SENDER_ADDRESS, "noreply@sormas.org");
	}

	@Override
	public String getEmailSenderName() {
		return getProperty(EMAIL_SENDER_NAME, "SORMAS Support");
	}

	@Override
	public String getSmsSenderName() {
		return getProperty(SMS_SENDER_NAME, "SORMAS");
	}
	
	@Override
	public String getSmsAuthKey() {
		return getProperty(SMS_AUTH_KEY, "");
	}
	
	@Override
	public String getSmsAuthSecret() {
		return getProperty(SMS_AUTH_SECRET, "");
	}
	
	private String getProperty(String name, String defaultValue){
		return getProp(name, defaultValue);
	}
	
	private boolean getBoolean(String name, boolean defaultValue) {
		return Boolean.parseBoolean(getProperty(name, Boolean.toString(defaultValue)));
	}
	
	@LocalBean
	@Stateless
	public static class ConfigFacadeEjbLocal extends ConfigFacadeEjb {
	}
}
