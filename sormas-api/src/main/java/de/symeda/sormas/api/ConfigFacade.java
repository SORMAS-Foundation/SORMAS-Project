/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package de.symeda.sormas.api;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.externaljournal.PatientDiaryConfig;
import de.symeda.sormas.api.externaljournal.SymptomJournalConfig;
import de.symeda.sormas.api.sormastosormas.SormasToSormasConfig;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.geo.GeoLatLon;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.systemconfiguration.Config;

public interface ConfigFacade {

	String SORMAS = "SORMAS";

	boolean isPresent(Config config);

	default boolean isAbsent(Config config) {
		return !isPresent(config);
	}

	Optional<Integer> getAsInteger(Config config);

	Optional<Double> getAsDouble(Config config);

	Optional<Long> getAsLong(Config config);

	Optional<String> getAsString(Config config);

	@Nullable
	default String getAsStringOrNull(Config config) {
		return getAsString(config).orElse(null);
	}

	@NotNull
	default String getAsStringOrEmpty(Config config) {
		return getAsString(config).orElse("");
	}

	boolean getAsBoolean(Config config);

	@NotNull
	default Integer getAsIntegerOrThrow(Config config) {
		return getAsInteger(config).orElseThrow(() -> buildMissingConfigException(config));
	}

	static IllegalStateException buildMissingConfigException(Config config) {
		return new IllegalStateException(String.format("Required configuration '%s' not found or invalid. \nCheck if ", config.name()));
	}

	@NotNull
	default Double getAsDoubleOrThrow(Config config) {
		return getAsDouble(config).orElseThrow(() -> buildMissingConfigException(config));
	}

	@NotNull
	default Long getAsLongOrThrow(Config config) {
		return getAsLong(config).orElseThrow(() -> buildMissingConfigException(config));
	}

	@NotNull
	default String getAsStringOrThrow(Config config) {
		return getAsString(config).orElseThrow(() -> buildMissingConfigException(config));
	}

	default boolean isConfiguredCountry(String countryCode) {
		String countryLocale = getAsStringOrThrow(Config.COUNTRY_LOCALE);
		if (Pattern.matches(I18nProperties.FULL_COUNTRY_LOCALE_PATTERN, countryLocale)) {
			return StringUtils.endsWithIgnoreCase(countryLocale, countryCode);
		} else {
			return StringUtils.startsWithIgnoreCase(countryLocale, countryCode);
		}
	}

	@NotNull
	String getCountryCode();

	default boolean isSmsServiceSetUp() {
		return isPresent(Config.SMS_AUTH_SECRET) || isPresent(Config.SMS_AUTH_KEY);
	}

	default char getCsvSeparator() {
		return getAsString(Config.CSV_SEPARATOR).map(CharUtils::toChar)
			.orElseThrow(() -> ConfigFacade.buildMissingConfigException(Config.CSV_SEPARATOR));
	}

	@Deprecated
	default boolean isS2SConfigured() {
		return isPresent(Config.SORMAS2SORMAS_PATH);
	}

	@Deprecated
	default boolean isExternalSurveillanceToolGatewayConfigured() {
		return isPresent(Config.EXTERNAL_SURVEILLANCE_TOOL_GATEWAY_URL);
	}

	@NotNull
	default Set<String> getAllowedFileExtensions() {
		return Arrays.stream(getAsStringOrThrow(Config.ALLOWED_FILE_EXTENSIONS).split(",")).collect(Collectors.toSet());
	}

	@NotNull
	default String getSormasInstanceName() {
		return getAsBoolean(Config.CUSTOM_BRANDING) ? getAsStringOrThrow(Config.CUSTOM_BRANDING_NAME) : SORMAS;
	}

	@NotNull
	default GeoLatLon getCountryCenter() {
		return new GeoLatLon(getAsDoubleOrThrow(Config.COUNTRY_CENTER_LATITUDE), getAsDoubleOrThrow(Config.COUNTRY_CENTER_LATITUDE));
	}

	@NotNull
	default String getCountryLocale() {
		return getAsStringOrThrow(Config.COUNTRY_LOCALE);
	}

	@NotNull
	default String getTempFilesPath() {
		return getAsStringOrThrow(Config.TEMP_PATH);
	}

	// kept for legacy purposes

	@NotNull
	default String getCountryName() {
		return getAsStringOrThrow(Config.COUNTRY_NAME);
	}

	default String getEpidPrefix() {
		return getAsStringOrEmpty(Config.COUNTRY_EPID_PREFIX);
	}

	@Nullable
	default String getAppUrl() {
		return getAsStringOrNull(Config.APP_URL);
	}

	@Nullable
	default String getUiUrl() {
		return getAsStringOrNull(Config.UI_URL);
	}

	@Nullable
	default String getSormasStatsUrl() {
		return getAsStringOrNull(Config.SORMAS_STATS_URL);
	}

	@NotNull
	default String getDocumentFilesPath() {
		return getAsStringOrThrow(Config.DOCUMENTS_PATH);
	}

	@Nullable
	default String getGeneratedFilesPath() {
		return getAsStringOrNull(Config.GENERATED_FILES_PATH);
	}

	@Nullable
	default String getCustomFilesPath() {
		return getAsStringOrNull(Config.CUSTOM_FILES_PATH);
	}

	@Nullable
	default String getRScriptExecutable() {
		return getAsStringOrNull(Config.RSCRIPT_EXECUTABLE);
	}

	@Nullable
	default String getAppLegacyUrl() {
		return getAsStringOrNull(Config.APP_LEGACY_URL);
	}

	default boolean isDevMode() {
		return getAsBoolean(Config.DEV_MODE);
	}

	default boolean isCustomBranding() {
		return getAsBoolean(Config.CUSTOM_BRANDING);
	}

	@NotNull
	default String getCustomBrandingName() {
		return getAsString(Config.CUSTOM_BRANDING_NAME).orElse(SORMAS);
	}

	@Nullable
	default String getCustomBrandingLogoPath() {
		return getAsStringOrNull(Config.CUSTOM_BRANDING_LOGO_PATH);
	}

	default boolean isUseLoginSidebar() {
		return getAsBoolean(Config.CUSTOM_BRANDING_USELOGINSIDEBAR);
	}

	@Nullable
	default String getLoginBackgroundPath() {
		return getAsStringOrNull(Config.CUSTOM_BRANDING_LOGINBACKGROUND_PATH);
	}

	default boolean isDuplicateChecksExcludePersonsOfArchivedEntries() {
		return getAsBoolean(Config.DUPLICATE_CHECKS_EXCLUDE_PERSONS_ONLY_LINKED_TO_ARCHIVED_ENTRIES);
	}

	default boolean isDuplicateChecksNationalHealthIdOverridesCriteria() {
		return getAsBoolean(Config.DUPLICATECHECKS_NATIONAL_HEALTH_ID_OVERRIDES_CRITERIA);
	}

	default double getNameSimilarityThreshold() {
		return getAsDoubleOrThrow(Config.NAME_SIMILARITY_THRESHOLD);
	}

	default int getInfrastructureSyncThreshold() {
		return getAsIntegerOrThrow(Config.INFRASTRUCTURE_SYNC_THRESHOLD);
	}

	default int getDaysAfterSystemEventGetsDeleted() {
		return getAsIntegerOrThrow(Config.DAYS_AFTER_SYSTEM_EVENT_GETS_DELETED);
	}

	default boolean isMapUseCountryCenter() {
		return getAsBoolean(Config.MAP_USECOUNTRYCENTER);
	}

	@Nullable
	default String getMapTilersUrl() {
		return getAsStringOrNull(Config.MAP_TILES_URL);
	}

	@Nullable
	default String getMapTilersAttribution() {
		return getAsStringOrNull(Config.MAP_TILES_ATTRIBUTION);
	}

	default int getMapZoom() {
		return getAsIntegerOrThrow(Config.MAP_ZOOM);
	}

	@Nullable
	default String getGeocodingServiceUrlTemplate() {
		return getAsStringOrNull(Config.GEOCODING_SERVICE_URL_TEMPLATE);
	}

	@Nullable
	default String getGeocodingLongitudeJsonPath() {
		return getAsStringOrNull(Config.GEOCODING_LONGITUDE_JSON_PATH);
	}

	@Nullable
	default String getGeocodingLatitudeJsonPath() {
		return getAsStringOrNull(Config.GEOCODING_LATITUDE_JSON_PATH);
	}

	@Nullable
	default String getGeocodingEPSG4326_WKT() {
		return getAsStringOrNull(Config.GEOCODING_EPSG4326_WKT);
	}

	@Nullable
	default String getExternalSurveillanceToolGatewayUrl() {
		return getAsStringOrNull(Config.EXTERNAL_SURVEILLANCE_TOOL_GATEWAY_URL);
	}

	@Nullable
	default String getExternalSurveillanceToolVersionEndpoint() {
		return getAsStringOrNull(Config.EXTERNAL_SURVEILLANCE_TOOL_GATEWAY_VERSION_ENDPOINT);
	}

	@NotNull
	default String getAuthenticationProvider() {
		return getAsStringOrThrow(Config.AUTHENTICATION_PROVIDER);
	}

	default boolean isAuthenticationProviderUserSyncAtStartupEnabled() {
		return getAsBoolean(Config.AUTHENTICATION_PROVIDER_USER_SYNC_AT_STARTUP);
	}

	@Nullable
	default String getAuthenticationProviderSyncedNewUserRole() {
		return getAsStringOrNull(Config.AUTHENTICATION_PROVIDER_SYNCED_NEW_USER_ROLE);
	}

	default int getDashboardMapMarkerLimit() {
		return getAsIntegerOrThrow(Config.DASHBOARD_MAP_MARKER_LIMIT);
	}

	@Nullable
	default String getExternalMessageAdapterJndiName() {
		return getAsStringOrNull(Config.INTERFACE_EXTERNAL_MESSAGE_ADAPTER_JNDI_NAME);
	}

	default boolean isSkipDefaultPasswordCheck() {
		return getAsBoolean(Config.SKIP_DEFAULT_PASSWORD_CHECK);
	}

	default boolean isAuditorAttributeLoggingEnabled() {
		return getAsBoolean(Config.AUDITOR_ATTRIBUTE_LOGGING);
	}

	default int getStepSizeForCsvExport() {
		return getAsIntegerOrThrow(Config.STEP_SIZE_FOR_CSV_EXPORT);
	}

	default long getDocumentUploadSizeLimitMb() {
		return getAsLongOrThrow(Config.DOCUMENT_UPLOAD_SIZE_LIMIT_MB);
	}

	default long getImportFileSizeLimitMb() {
		return getAsLongOrThrow(Config.IMPORT_FILE_SIZE_LIMIT_MB);
	}

	@NotNull
	default String getAuditLoggerConfig() {
		return getAsStringOrEmpty(Config.AUDIT_LOGGER_CONFIG);
	}

	@NotNull
	default String getAuditSourceSite() {
		return getAsStringOrEmpty(Config.AUDIT_SOURCE_SITE);
	}

	@Nullable
	default Integer getNegaiveCovidTestsMaxAgeDays() {
		return getAsInteger(Config.NEGATIVE_COVID_TESTS_MAX_AGE_DAYS).orElse(null);
	}

	default long getMinimumEmancipatedAge() {
		return getAsLongOrThrow(Config.MINIMUM_EMANCIPATED_AGE);
	}

	default long getMinimumAdultAge() {
		return getAsLongOrThrow(Config.MINIMUM_ADULT_AGE);
	}

	@NotNull
	default String getDocgenerationNullReplacement() {
		return getAsStringOrThrow(Config.DOCGENERATION_NULL_REPLACEMENT);
	}

	@Nullable
	default String getCentralEtcdClientName() {
		return getAsStringOrNull(Config.CENTRAL_ETCD_CLIENT_NAME);
	}

	@Nullable
	default String getCentralEtcdClientPassword() {
		return getAsStringOrNull(Config.CENTRAL_ETCD_CLIENT_PASSWORD);
	}

	@Nullable
	default String getCentralEtcdCaPath() {
		return getAsStringOrNull(Config.CENTRAL_ETCD_CA_PATH);
	}

	@Nullable
	default String getCentralEtcdHost() {
		return getAsStringOrNull(Config.CENTRAL_ETCD_HOST);
	}

	default boolean isCentralLocationSync() {
		return getAsBoolean(Config.CENTRAL_LOCATION_SYNC);
	}

	SormasToSormasConfig getS2SConfig();

	@Deprecated
	boolean isExternalJournalActive();

	@Deprecated
	SymptomJournalConfig getSymptomJournalConfig();

	@Deprecated
	PatientDiaryConfig getPatientDiaryConfig();
}
