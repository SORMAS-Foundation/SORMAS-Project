package de.symeda.sormas.backend.common;

import java.util.Properties;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.utils.CompatibilityCheckResponse;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.InfoProvider;
import de.symeda.sormas.api.utils.VersionHelper;

/**
 * Provides the application configuration settings
 */
@Stateless(name="ConfigFacade")
public class ConfigFacadeEjb implements ConfigFacade {

	public static final String COUNTRY_NAME = "country.name";
	
	public static final String VERSION_PLACEHOLER = "%version";
	
	public static final String APP_URL = "app.url";
	public static final String APP_LEGACY_URL = "app.legacy.url";
	
	public static final String FEATURE_AUTOMATIC_CASE_CLASSIFICATION = "feature.automaticcaseclassification";
	
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

	protected String getProperty(String name, String defaultValue) {
		String prop = props.getProperty(name);

		if (prop == null){
			return defaultValue;
		} else {
			return prop;
		}
	}
	
	protected boolean getBoolean(String name, boolean defaultValue) {
		return Boolean.parseBoolean(getProperty(name, Boolean.toString(defaultValue)));
	}

	@Override
	public String getCountryName() {
		return getProperty(COUNTRY_NAME, "nigeria");
	}

	@Override
	public String getAppUrl() {
		String appUrl = getProperty(APP_URL, null);
		if (appUrl != null) {
			appUrl = appUrl.replaceAll(VERSION_PLACEHOLER, InfoProvider.get().getVersion());
		}
		return appUrl;
	}

	@Override
	public String getAppLegacyUrl() {
		return getProperty(APP_LEGACY_URL, null);
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
	public boolean isFeatureAutomaticCaseClassification() {
		return getBoolean(FEATURE_AUTOMATIC_CASE_CLASSIFICATION, false);
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
	
	@Override
	public void validateAppUrls() {
		String appUrl = getAppUrl();
		String appLegacyUrl = getAppLegacyUrl();
		
		// must contain version information
		int[] appVersion = VersionHelper.extractVersion(appUrl);
		if (!DataHelper.isNullOrEmpty(appUrl) && !VersionHelper.isVersion(appVersion)) {
			throw new IllegalArgumentException("Property '" + ConfigFacadeEjb.APP_URL + "' must contain a valid version: '" + appUrl + "'");
		}
		int[] appLegacyVersion = VersionHelper.extractVersion(appLegacyUrl);
		if (!DataHelper.isNullOrEmpty(appLegacyUrl) && !VersionHelper.isVersion(appLegacyVersion)) {
			throw new IllegalArgumentException("Property '" + ConfigFacadeEjb.APP_LEGACY_URL + "' must contain a valid version: '" + appLegacyUrl + "'");
		}
		
		// legacy must be empty or before app version
		if (appLegacyVersion != null && appVersion != null) {
			if (!VersionHelper.isBefore(appLegacyVersion, appVersion)) {
				throw new IllegalArgumentException("Property '" + ConfigFacadeEjb.APP_LEGACY_URL + "' must have a version smaller "
						+ "than property '" + ConfigFacadeEjb.APP_URL + "': '" + appLegacyUrl + "' - '" + appUrl + "'");
			}
		}
		
		// both have to be compatible
		if (appVersion != null && InfoProvider.get().isCompatibleToApi(appVersion) != CompatibilityCheckResponse.COMPATIBLE) {
			throw new IllegalArgumentException("Property '" + ConfigFacadeEjb.APP_URL + "' does not point to a compatible app version: '"
					+ appUrl + "'. Minimum is '" + InfoProvider.get().getMinimumRequiredVersion() + "'");
		}
		
		if (appLegacyVersion != null && InfoProvider.get().isCompatibleToApi(appLegacyVersion) != CompatibilityCheckResponse.COMPATIBLE) {
			throw new IllegalArgumentException("Property '" + ConfigFacadeEjb.APP_LEGACY_URL + "' does not point to a compatible app version: '"
					+ appLegacyUrl + "'. Minimum is '" + InfoProvider.get().getMinimumRequiredVersion() + "'");
		}

	}
	
	@LocalBean
	@Stateless
	public static class ConfigFacadeEjbLocal extends ConfigFacadeEjb {
	}
	
}