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
import java.util.stream.Collectors;

import de.symeda.sormas.api.geo.GeoLatLon;
import de.symeda.sormas.api.systemconfiguration.ConfigType;

public interface ConfigFacade {

	boolean isPresent(ConfigType config);

	default boolean isAbsent(ConfigType config) {
		return !isPresent(config);
	}

	Optional<Integer> getAsInteger(ConfigType config);

	Optional<Double> getAsDouble(ConfigType config);

	Optional<Long> getAsLong(ConfigType config);

	Optional<String> getAsString(ConfigType config);

	boolean getAsBoolean(ConfigType config);

	default Integer getAsIntegerOrThrow(ConfigType config) {
		return getAsInteger(config).orElseThrow(() -> buildMissingConfigException(config));
	}

	static IllegalStateException buildMissingConfigException(ConfigType config) {
		return new IllegalStateException(String.format("Required configuration '%s' not found or invalid. \nCheck if ", config.name()));
	}

	default Double getAsDoubleOrThrow(ConfigType config) {
		return getAsDouble(config).orElseThrow(() -> buildMissingConfigException(config));
	}

	default Long getAsLongOrThrow(ConfigType config) {
		return getAsLong(config).orElseThrow(() -> buildMissingConfigException(config));
	}

	default String getAsStringOrThrow(ConfigType config) {
		return getAsString(config).orElseThrow(() -> buildMissingConfigException(config));
	}

	boolean isConfiguredCountry(String countryCode);

	String getCountryCode();

	default boolean isSmsServiceSetUp() {
		return isPresent(ConfigType.SMS_AUTH_SECRET) || isPresent(ConfigType.SMS_AUTH_KEY);
	}

	char getCsvSeparator();

	@Deprecated
	default boolean isS2SConfigured() {
		return isPresent(ConfigType.SORMAS2SORMAS_PATH);
	}

	@Deprecated
	default boolean isExternalSurveillanceToolGatewayConfigured() {
		return isPresent(ConfigType.EXTERNAL_SURVEILLANCE_TOOL_GATEWAY_URL);
	}

	default Set<String> getAllowedFileExtensions() {
		return Arrays.stream(getAsStringOrThrow(ConfigType.ALLOWED_FILE_EXTENSIONS).split(",")).collect(Collectors.toSet());
	}

	default String getSormasInstanceName() {
		return getAsBoolean(ConfigType.CUSTOM_BRANDING) ? getAsStringOrThrow(ConfigType.CUSTOM_BRANDING_NAME) : "SORMAS";
	}

	CaseClassificationCalculationMode getCaseClassificationCalculationMode(Disease disease);

	default GeoLatLon getCountryCenter() {
		return new GeoLatLon(
			getAsDoubleOrThrow(ConfigType.COUNTRY_CENTER_LATITUDE),
			getAsDoubleOrThrow(ConfigType.COUNTRY_CENTER_LATITUDE));
	}

	default String getCountryLocale() {
		return getAsStringOrThrow(ConfigType.COUNTRY_LOCALE);
	}

	boolean isAnyCaseClassificationCalculationEnabled();
}
