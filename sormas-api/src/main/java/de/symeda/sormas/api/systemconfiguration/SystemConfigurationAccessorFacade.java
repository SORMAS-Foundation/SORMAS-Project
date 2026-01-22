package de.symeda.sormas.api.systemconfiguration;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import de.symeda.sormas.api.CaseClassificationCalculationMode;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.geo.GeoLatLon;

public interface SystemConfigurationAccessorFacade {

	boolean isPresent(SystemConfigurationType config);

	default boolean isAbsent(SystemConfigurationType config) {
		return !isPresent(config);
	}

	Optional<Integer> getAsInteger(SystemConfigurationType config);

	Optional<Double> getAsDouble(SystemConfigurationType config);

	Optional<Long> getAsLong(SystemConfigurationType config);

	Optional<String> getAsString(SystemConfigurationType config);

	boolean getAsBoolean(SystemConfigurationType config);

	default Integer getAsIntegerOrThrow(SystemConfigurationType config) {
		return getAsInteger(config).orElseThrow(() -> buildMissingConfigException(config));
	}

	static IllegalStateException buildMissingConfigException(SystemConfigurationType config) {
		return new IllegalStateException(String.format("Required configuration '%s' not found or invalid. \nCheck if ", config.name()));
	}

	default Double getAsDoubleOrThrow(SystemConfigurationType config) {
		return getAsDouble(config).orElseThrow(() -> buildMissingConfigException(config));
	}

	default Long getAsLongOrThrow(SystemConfigurationType config) {
		return getAsLong(config).orElseThrow(() -> buildMissingConfigException(config));
	}

	default String getAsStringOrThrow(SystemConfigurationType config) {
		return getAsString(config).orElseThrow(() -> buildMissingConfigException(config));
	}

	boolean isConfiguredCountry(String countryCode);

	String getCountryCode();

	default boolean isSmsServiceSetUp() {
		return isPresent(SystemConfigurationType.SMS_AUTH_SECRET) || isPresent(SystemConfigurationType.SMS_AUTH_KEY);
	}

	char getCsvSeparator();

	@Deprecated
	default boolean isS2SConfigured() {
		return isPresent(SystemConfigurationType.SORMAS2SORMAS_PATH);
	}

	@Deprecated
	default boolean isExternalSurveillanceToolGatewayConfigured() {
		return isPresent(SystemConfigurationType.EXTERNAL_SURVEILLANCE_TOOL_GATEWAY_URL);
	}

	default Set<String> getAllowedFileExtensions() {
		return Arrays.stream(getAsStringOrThrow(SystemConfigurationType.ALLOWED_FILE_EXTENSIONS).split(",")).collect(Collectors.toSet());
	}

	default String getSormasInstanceName() {
		return getAsBoolean(SystemConfigurationType.CUSTOM_BRANDING) ? getAsStringOrThrow(SystemConfigurationType.CUSTOM_BRANDING_NAME) : "SORMAS";
	}

	CaseClassificationCalculationMode getCaseClassificationCalculationMode(Disease disease);

	default GeoLatLon getCountryCenter() {
		return new GeoLatLon(
			getAsDoubleOrThrow(SystemConfigurationType.COUNTRY_CENTER_LATITUDE),
			getAsDoubleOrThrow(SystemConfigurationType.COUNTRY_CENTER_LATITUDE));
	}

	default String getCountryLocale() {
		return getAsStringOrThrow(SystemConfigurationType.COUNTRY_LOCALE);
	}

	boolean isAnyCaseClassificationCalculationEnabled();
}
