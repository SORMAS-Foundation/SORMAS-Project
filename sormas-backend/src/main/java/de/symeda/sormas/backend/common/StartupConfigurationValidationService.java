package de.symeda.sormas.backend.common;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import de.symeda.sormas.api.externaljournal.PatientDiaryConfig;
import de.symeda.sormas.api.externaljournal.SymptomJournalConfig;
import de.symeda.sormas.api.sormastosormas.SormasToSormasConfig;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationType;
import de.symeda.sormas.api.utils.CompatibilityCheckResponse;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.InfoProvider;
import de.symeda.sormas.api.utils.VersionHelper;
import de.symeda.sormas.backend.systemconfiguration.ExternalClientConfigurationEjb;
import de.symeda.sormas.backend.systemconfiguration.SystemConfigurationAccessorEjb;

@Stateless
@LocalBean
public class StartupConfigurationValidationService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private SystemConfigurationAccessorEjb systemConfigurationAccessor;
	private ExternalClientConfigurationEjb externalClientConfiguration;

	public StartupConfigurationValidationService() {
	}

	public StartupConfigurationValidationService(
		SystemConfigurationAccessorEjb systemConfigurationAccessor,
		ExternalClientConfigurationEjb externalClientConfiguration) {
		this.systemConfigurationAccessor = systemConfigurationAccessor;
		this.externalClientConfiguration = externalClientConfiguration;
	}

	public void validateAppUrls() {
		logger.info("Validating app urls");
		String appUrl = getAppUrl();
		String appLegacyUrl = systemConfigurationAccessor.getAsString(SystemConfigurationType.APP_LEGACY_URL).orElse(null);

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

	private @Nullable String getAppUrl() {
		return systemConfigurationAccessor.getAsString(SystemConfigurationType.APP_URL).orElse(null);
	}

	public void validateConfigUrls() {
		logger.info("Validating config urls");
		SormasToSormasConfig s2sConfig = externalClientConfiguration.getS2SConfig();
		SymptomJournalConfig symptomJournalConfig = externalClientConfiguration.getSymptomJournalConfig();
		PatientDiaryConfig patientDiaryConfig = externalClientConfiguration.getPatientDiaryConfig();

		List<String> enforceHttps = Lists.newArrayList(
			s2sConfig.getOidcServer(),
			symptomJournalConfig.getUrl(),
			symptomJournalConfig.getAuthUrl(),
			patientDiaryConfig.getUrl(),
			patientDiaryConfig.getProbandsUrl(),
			patientDiaryConfig.getAuthUrl(),
			patientDiaryConfig.getFrontendAuthUrl(),
			getAppUrl(),
			systemConfigurationAccessor.getAsString(SystemConfigurationType.UI_URL).orElse(null),
			systemConfigurationAccessor.getAsString(SystemConfigurationType.SORMAS_STATS_URL).orElse(null));

		List<String> allowHttp =
			Lists.newArrayList(systemConfigurationAccessor.getAsString(SystemConfigurationType.EXTERNAL_SURVEILLANCE_TOOL_GATEWAY_URL).orElse(null));

		// separately as they are interpolated
		if (!StringUtils.isBlank(s2sConfig.getOidcServer())) {

			enforceHttps.add(s2sConfig.getOidcRealmCertEndpoint());
			enforceHttps.add(s2sConfig.getOidcRealmTokenEndpoint());

			if (!StringUtils.isBlank(s2sConfig.getOidcRealm())) {
				enforceHttps.add(s2sConfig.getOidcRealmUrl());
			}
		}

		UrlValidator enforceHttpsValidator = new UrlValidator(
			new String[] {
				"https" },
			UrlValidator.ALLOW_LOCAL_URLS);

		List<String> invalidHttpsUrls =
			enforceHttps.stream().filter(u -> !StringUtils.isBlank(u)).filter(u -> !enforceHttpsValidator.isValid(u)).collect(Collectors.toList());
		if (!invalidHttpsUrls.isEmpty()) {
			String invalid = String.join(",\n\t", invalidHttpsUrls);
			throw new IllegalArgumentException(String.format("Invalid URLs for which HTTPS is enforced in property file:\n\t%s", invalid));
		}

		UrlValidator allowHttpValidator = new UrlValidator(
			new String[] {
				"https",
				"http" },
			UrlValidator.ALLOW_LOCAL_URLS);

		List<String> invalidUrls =
			allowHttp.stream().filter(u -> !StringUtils.isBlank(u)).filter(u -> !allowHttpValidator.isValid(u)).collect(Collectors.toList());
		if (!invalidUrls.isEmpty()) {
			String invalid = String.join(",\n\t", invalidUrls);
			throw new IllegalArgumentException(String.format("Invalid URLs in property file:\n\t%s", invalid));
		}

		// the following two checks cannot be collapsed with the general HTTPS check because they are not valid URLs
		// as they contain placeholders

		String geocodingUrl = systemConfigurationAccessor.getAsString(SystemConfigurationType.GEOCODING_SERVICE_URL_TEMPLATE).orElse(null);
		if (!StringUtils.isBlank(geocodingUrl) && !geocodingUrl.startsWith("https://")) {
			throw new IllegalArgumentException("geocodingServiceUrlTemplate property is required to be HTTPS");
		}

		String mapTilersUrl = systemConfigurationAccessor.getAsString(SystemConfigurationType.MAP_TILES_URL).orElse(null);
		if (!StringUtils.isBlank(mapTilersUrl) && !mapTilersUrl.startsWith("https://")) {
			throw new IllegalArgumentException("map.tiles.url property is required to be HTTPS");
		}
	}

	@Inject
	public void setSystemConfigurationAccessor(SystemConfigurationAccessorEjb systemConfigurationAccessor) {
		this.systemConfigurationAccessor = systemConfigurationAccessor;
	}

	@Inject
	public void setExternalClientConfiguration(ExternalClientConfigurationEjb externalClientConfiguration) {
		this.externalClientConfiguration = externalClientConfiguration;
	}
}
