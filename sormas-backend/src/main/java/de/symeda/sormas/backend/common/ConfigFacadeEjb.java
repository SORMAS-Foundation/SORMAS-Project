package de.symeda.sormas.backend.common;

import java.util.Properties;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.jboss.weld.exceptions.IllegalArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.ConfigFacade;

/**
 * Provides the application configuration settings
 */
@Stateless(name="ConfigFacade")
public class ConfigFacadeEjb implements ConfigFacade {

	public static final String COUNTRY_NAME = "country.name";
	
	public static final String APP_URL = "app.url";
	
	public static final String TEMP_FILES_PATH = "temp.path";
	public static final String GENERATED_FILES_PATH = "generated.path";
	public static final String CSV_SEPARATOR = "csv.separator";
	
	public static final String EMAIL_SENDER_ADDRESS = "email.sender.address";
	public static final String EMAIL_SENDER_NAME = "email.sender.name";
	public static final String SMS_SENDER_NAME = "sms.sender.name";
	public static final String SMS_AUTH_KEY = "sms.auth.key";
	public static final String SMS_AUTH_SECRET = "sms.auth.secret";
	
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
			return prop;
		}
	}

	@Override
	public String getCountryName() {
		return getProperty(COUNTRY_NAME, "nigeria");
	}

	@Override
	public String getAppUrl() {
		return getProperty(APP_URL, null);
	}
	
	@Override
	public String getTempFilesPath() {
		return getProperty(TEMP_FILES_PATH, "/opt/sormas-temp/");
	}
	
	@Override
	public String getGeneratedFilesPath() {
		return getProperty(GENERATED_FILES_PATH, "/opt/sormas-generated/");
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

	@Override
	public char getCsvSeparator() {
		String seperatorString = getProperty(CSV_SEPARATOR, ",");
		if (seperatorString.length() != 1) {
			throw new IllegalArgumentException(CSV_SEPARATOR + " must be a single character instead of '" + seperatorString + "'");
		}
		return seperatorString.charAt(0);
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
