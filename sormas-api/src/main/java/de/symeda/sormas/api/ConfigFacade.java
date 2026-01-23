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

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.geo.GeoLatLon;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.systemconfiguration.Config;

public interface ConfigFacade {

	boolean isPresent(Config config);

	default boolean isAbsent(Config config) {
		return !isPresent(config);
	}

	Optional<Integer> getAsInteger(Config config);

	Optional<Double> getAsDouble(Config config);

	Optional<Long> getAsLong(Config config);

	Optional<String> getAsString(Config config);

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
		return getAsBoolean(Config.CUSTOM_BRANDING) ? getAsStringOrThrow(Config.CUSTOM_BRANDING_NAME) : "SORMAS";
	}

	@NotNull
	default GeoLatLon getCountryCenter() {
		return new GeoLatLon(getAsDoubleOrThrow(Config.COUNTRY_CENTER_LATITUDE), getAsDoubleOrThrow(Config.COUNTRY_CENTER_LATITUDE));
	}

	@NotNull
	default String getCountryLocale() {
		return getAsStringOrThrow(Config.COUNTRY_LOCALE);
	}

	default String getTempFilesPath() {
		return getAsStringOrThrow(Config.TEMP_PATH);
	}
}
