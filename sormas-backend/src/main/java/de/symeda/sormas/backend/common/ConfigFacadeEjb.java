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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.common;

import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.region.GeoLatLon;
import de.symeda.sormas.api.utils.CompatibilityCheckResponse;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.InfoProvider;
import de.symeda.sormas.api.utils.VersionHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.Locale;
import java.util.Properties;

/**
 * Provides the application configuration settings
 */
@Stateless(name = "ConfigFacade")
public class ConfigFacadeEjb implements ConfigFacade {

	public static final String COUNTRY_NAME = "country.name";
	public static final String COUNTRY_LOCALE = "country.locale";
	public static final String COUNTRY_EPID_PREFIX = "country.epidprefix";
	private static final String COUNTRY_CENTER_LAT = "country.center.latitude";
	private static final String COUNTRY_CENTER_LON = "country.center.longitude";
	private static final String MAP_ZOOM = "map.zoom";

	public static final String VERSION_PLACEHOLER = "%version";

	public static final String DEV_MODE = "devmode";

	public static final String CUSTOM_BRANDING = "custombranding";
	public static final String CUSTOM_BRANDING_NAME = "custombranding.name";
	public static final String CUSTOM_BRANDING_LOGO_PATH = "custombranding.logo.path";

	public static final String APP_URL = "app.url";
	public static final String APP_LEGACY_URL = "app.legacy.url";

	public static final String FEATURE_AUTOMATIC_CASE_CLASSIFICATION = "feature.automaticcaseclassification";

	public static final String TEMP_FILES_PATH = "temp.path";
	public static final String GENERATED_FILES_PATH = "generated.path";
	public static final String CUSTOM_FILES_PATH = "custom.path";
	public static final String CSV_SEPARATOR = "csv.separator";
	public static final String RSCRIPT_EXECUTABLE = "rscript.executable";

	public static final String EMAIL_SENDER_ADDRESS = "email.sender.address";
	public static final String EMAIL_SENDER_NAME = "email.sender.name";
	public static final String SMS_SENDER_NAME = "sms.sender.name";
	public static final String SMS_AUTH_KEY = "sms.auth.key";
	public static final String SMS_AUTH_SECRET = "sms.auth.secret";

	public static final String NAME_SIMILARITY_THRESHOLD = "namesimilaritythreshold";
	public static final String INFRASTRUCTURE_SYNC_THRESHOLD = "infrastructuresyncthreshold";

	public static final String INTERFACE_PIA_URL = "interface.pia.url";

	public static final String DAYS_AFTER_CASE_GETS_ARCHIVED = "daysAfterCaseGetsArchived";
	private static final String DAYS_AFTER_EVENT_GETS_ARCHIVED = "daysAfterEventGetsArchived";

	private static final String GEOCODING_OSGTS_ENDPOINT = "geocodingOsgtsEndpoint";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Resource(lookup = "sormas/Properties")
	private Properties props;

	protected String getProperty(String name, String defaultValue) {

		String prop = props.getProperty(name);
		if (prop == null) {
			return defaultValue;
		} else {
			return prop;
		}
	}

	protected boolean getBoolean(String name, boolean defaultValue) {

		try {
			return Boolean.parseBoolean(getProperty(name, Boolean.toString(defaultValue)));
		} catch (Exception e) {
			logger.error("Could not parse boolean value of property '" + name + "': " + e.getMessage());
			return defaultValue;
		}
	}

	protected double getDouble(String name, double defaultValue) {

		try {
			return Double.parseDouble(getProperty(name, Double.toString(defaultValue)));
		} catch (Exception e) {
			logger.error("Could not parse numeric value of property '" + name + "': " + e.getMessage());
			return defaultValue;
		}
	}

	protected int getInt(String name, int defaultValue) {

		try {
			return Integer.parseInt(getProperty(name, Integer.toString(defaultValue)));
		} catch (Exception e) {
			logger.error("Could not parse integer value of property '" + name + "': " + e.getMessage());
			return defaultValue;
		}
	}

	@Override
	public String getCountryName() {

		String countryName = getProperty(COUNTRY_NAME, "");
		if (countryName.isEmpty()) {
			logger.info("No country name is specified in sormas.properties.");
		}
		return countryName;
	}

	/**
	 * Returns the normalized locale
	 */
	@Override
	public String getCountryLocale() {

		String locale = getProperty(COUNTRY_LOCALE, Language.EN.getLocale().toString());
		return normalizeLocaleString(locale);
	}

	static String normalizeLocaleString(String locale) {

		locale = locale.trim();
		int pos = Math.max(locale.indexOf('-'), locale.indexOf('_'));
		if (pos < 0) {
			locale = locale.toLowerCase();
		} else {
			locale = locale.substring(0, pos).toLowerCase(Locale.ENGLISH) + '-' + locale.substring(pos + 1).toUpperCase(Locale.ENGLISH);
		}
		return locale;
	}

	@Override
	public boolean isGermanServer() {
		return getCountryLocale().startsWith("de");
	}

	@Override
	public String getEpidPrefix() {
		return getProperty(COUNTRY_EPID_PREFIX, "");
	}

	@Override
	public GeoLatLon getCountryCenter() {
		return new GeoLatLon(getDouble(COUNTRY_CENTER_LAT, 0), getDouble(COUNTRY_CENTER_LON, 0));
	}

	@Override
	public int getMapZoom() {
		return getInt(MAP_ZOOM, 1);
	}

	@Override
	public boolean isDevMode() {
		return getBoolean(DEV_MODE, false);
	}

	@Override
	public boolean isCustomBranding() {
		return getBoolean(CUSTOM_BRANDING, false);
	}

	@Override
	public String getCustomBrandingName() {
		return getProperty(CUSTOM_BRANDING_NAME, "SORMAS");
	}

	@Override
	public String getCustomBrandingLogoPath() {
		return getProperty(CUSTOM_BRANDING_LOGO_PATH, null);
	}

	@Override
	public String getSormasInstanceName() {
		return isCustomBranding() ? getCustomBrandingName() : "SORMAS";
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
		return getProperty(TEMP_FILES_PATH, "/opt/sormas/temp/");
	}

	@Override
	public String getGeneratedFilesPath() {
		return getProperty(GENERATED_FILES_PATH, "/opt/sormas/generated/");
	}

	@Override
	public String getCustomFilesPath() {
		return getProperty(CUSTOM_FILES_PATH, "/opt/sormas/custom/");
	}

	@Override
	public String getRScriptExecutable() {
		return getProperty(RSCRIPT_EXECUTABLE, null);
	}

	@Override
	public boolean isFeatureAutomaticCaseClassification() {
		return getBoolean(FEATURE_AUTOMATIC_CASE_CLASSIFICATION, true);
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
	public double getNameSimilarityThreshold() {
		return getDouble(NAME_SIMILARITY_THRESHOLD, 0.4D);
	}

	@Override
	public int getInfrastructureSyncThreshold() {
		return getInt(INFRASTRUCTURE_SYNC_THRESHOLD, 1000);
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
	public int getDaysAfterCaseGetsArchived() {
		return getInt(DAYS_AFTER_CASE_GETS_ARCHIVED, 90);
	}

	@Override
	public int getDaysAfterEventGetsArchived() {
		return getInt(DAYS_AFTER_EVENT_GETS_ARCHIVED, 90);
	}

	@Override
	public String getGeocodingOsgtsEndpoint() {
		return getProperty(GEOCODING_OSGTS_ENDPOINT, null);
	}

	@Override
	public String getPIAUrl() {
		return getProperty(INTERFACE_PIA_URL, null);
	}

	@Override
	public void validateExternalUrls() {

		String piaUrl = getPIAUrl();

		if (StringUtils.isBlank(piaUrl)) {
			return;
		}

		// Must be a valid URL
		if (!new UrlValidator(
			new String[] {
				"http",
				"https" }).isValid(piaUrl)) {
			throw new IllegalArgumentException("Property '" + ConfigFacadeEjb.INTERFACE_PIA_URL + "' is not a valid URL");
		}
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
			throw new IllegalArgumentException(
				"Property '" + ConfigFacadeEjb.APP_LEGACY_URL + "' must contain a valid version: '" + appLegacyUrl + "'");
		}

		// legacy must be empty or before app version
		if (appLegacyVersion != null && appVersion != null) {
			if (!VersionHelper.isBefore(appLegacyVersion, appVersion)) {
				throw new IllegalArgumentException(
					"Property '" + ConfigFacadeEjb.APP_LEGACY_URL + "' must have a version smaller " + "than property '" + ConfigFacadeEjb.APP_URL
						+ "': '" + appLegacyUrl + "' - '" + appUrl + "'");
			}
		}

		// both have to be compatible
		if (appVersion != null && InfoProvider.get().isCompatibleToApi(appVersion) != CompatibilityCheckResponse.COMPATIBLE) {
			throw new IllegalArgumentException(
				"Property '" + ConfigFacadeEjb.APP_URL + "' does not point to a compatible app version: '" + appUrl + "'. Minimum is '"
					+ InfoProvider.get().getMinimumRequiredVersion() + "'");
		}

		if (appLegacyVersion != null && InfoProvider.get().isCompatibleToApi(appLegacyVersion) != CompatibilityCheckResponse.COMPATIBLE) {
			throw new IllegalArgumentException(
				"Property '" + ConfigFacadeEjb.APP_LEGACY_URL + "' does not point to a compatible app version: '" + appLegacyUrl + "'. Minimum is '"
					+ InfoProvider.get().getMinimumRequiredVersion() + "'");
		}

	}

	@LocalBean
	@Stateless
	public static class ConfigFacadeEjbLocal extends ConfigFacadeEjb {

	}
}
