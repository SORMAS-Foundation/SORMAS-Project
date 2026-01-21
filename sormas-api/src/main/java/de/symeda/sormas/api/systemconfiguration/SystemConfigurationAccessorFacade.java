package de.symeda.sormas.api.systemconfiguration;

import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.CharUtils;

public interface SystemConfigurationAccessorFacade {

	boolean isPresent(SystemConfigurationType config);

	default boolean isAbsent(SystemConfigurationType config) {
		return !isPresent(config);
	}

	Optional<Integer> getAsInteger(SystemConfigurationType config);

	Optional<Double> getAsDouble(SystemConfigurationType config);

	Optional<Long> getAsLong(SystemConfigurationType config);

	Optional<String> getAsString(SystemConfigurationType config);

	default String getAsStringOrNull(SystemConfigurationType config) {
		return getAsString(config).orElse(null);
	}

	/**
	 * Precedence Gets value from db, otherwise default and finally defaults to false if missing.
	 * 
	 * @param config
	 *            property
	 * @return found - default or false
	 */
	boolean getAsBoolean(SystemConfigurationType config);

	default Integer getAsIntegerOrThrow(SystemConfigurationType config) {
		return getAsInteger(config).orElseThrow(() -> buildIllegalStateException(config));
	}

	private static IllegalStateException buildIllegalStateException(SystemConfigurationType config) {
		return new IllegalStateException(String.format("Required configuration '%s' not found or invalid. \nCheck if ", config.name()));
	}

	default Double getAsDoubleOrThrow(SystemConfigurationType config) {
		return getAsDouble(config).orElseThrow(() -> buildIllegalStateException(config));
	}

	default Long getAsLongOrThrow(SystemConfigurationType config) {
		return getAsLong(config).orElseThrow(() -> buildIllegalStateException(config));
	}

	default String getAsStringOrThrow(SystemConfigurationType config) {
		return getAsString(config).orElseThrow(() -> buildIllegalStateException(config));
	}

	default char getAsCharOrThrow(SystemConfigurationType config) {
		return getAsString(config).map(CharUtils::toChar).orElseThrow(() -> buildIllegalStateException(config));
	}

	boolean isConfiguredCountry(String countryCode);

	String getCountryCode();

	default boolean isSmsServiceSetUp() {
		return isPresent(SystemConfigurationType.SMS_AUTH_SECRET) || isPresent(SystemConfigurationType.SMS_AUTH_KEY);
	}

	default boolean isS2SConfigured() {
		return isPresent(SystemConfigurationType.SORMAS2SORMAS_PATH);
	}

	default boolean isExternalSurveillanceToolGatewayConfigured() {
		return isPresent(SystemConfigurationType.EXTERNAL_SURVEILLANCE_TOOL_GATEWAY_URL);
	}

	Set<String> getAllowedFileExtensions();

	String getSormasInstanceName();

//    boolean isConfiguredCountry(String countryCode);
//
//    GeoLatLon getCountryCenter();
//
//    SymptomJournalConfig getSymptomJournalConfig();
//
//    PatientDiaryConfig getPatientDiaryConfig();
//
//    SormasToSormasConfig getS2SConfig();
//
//    CaseClassificationCalculationMode getCaseClassificationCalculationMode(Disease disease);

}
