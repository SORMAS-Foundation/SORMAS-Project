package de.symeda.sormas.api.systemconfiguration;

import org.apache.commons.lang3.CharUtils;

import java.util.Optional;

public interface SystemConfigurationAccessorFacade {

	boolean isPresent(SystemConfiguration config);

	default boolean isAbsent(SystemConfiguration config) {
		return !isPresent(config);
	}

	Optional<Integer> getAsInteger(SystemConfiguration config);

	Optional<Double> getAsDouble(SystemConfiguration config);

	Optional<Long> getAsLong(SystemConfiguration config);

	Optional<String> getAsString(SystemConfiguration config);

	boolean getAsBoolean(SystemConfiguration config);

	default Integer getAsIntegerOrThrow(SystemConfiguration config) {
		return getAsInteger(config).orElseThrow(() -> buildIllegalStateException(config));
	}

	private static IllegalStateException buildIllegalStateException(SystemConfiguration config) {
		return new IllegalStateException(String.format("Required configuration '%s' not found or invalid", config.name()));
	}

	default Double getAsDoubleOrThrow(SystemConfiguration config) {
		return getAsDouble(config).orElseThrow(() -> buildIllegalStateException(config));
	}

	default Long getAsLongOrThrow(SystemConfiguration config) {
		return getAsLong(config).orElseThrow(() -> buildIllegalStateException(config));
	}

	default String getAsStringOrThrow(SystemConfiguration config) {
		return getAsString(config).orElseThrow(() -> buildIllegalStateException(config));
	}

	default char getAsCharOrThrow(SystemConfiguration config) {
		return getAsString(config).map(CharUtils::toChar).orElseThrow(() -> buildIllegalStateException(config));
	}

	String getCountryCode();

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
