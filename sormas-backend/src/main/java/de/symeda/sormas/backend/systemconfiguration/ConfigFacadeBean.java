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
package de.symeda.sormas.backend.systemconfiguration;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.systemconfiguration.Config;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueFacade;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.util.RightsAllowed;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
@RightsAllowed(UserRight._SYSTEM_CONFIGURATION)
public class ConfigFacadeBean implements ConfigFacade {

	public static final String COUNTRY_SEPARATION_CHAR = "-";
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Inject
	private SystemConfigurationValueFacade systemConfigurationValueEjb;

	@Override
	public boolean isPresent(Config config) {
		return getAsString(config).isPresent();
	}

	@Override
	public Optional<Integer> getAsInteger(Config config) {
		return getTypedValueFor(config, Integer::parseInt);
	}

	private <T> Optional<T> getTypedValueFor(Config config, Function<String, T> parsingFct) {
		return systemConfigurationValueEjb.getValue(config).map(value -> {
			logger.debug("Value [{}] for config [{}]", value, config);
			T result = parsingFct.apply(value);

			logger.debug("result [{}]", result);

			return result;
		});
	}

	@Override
	public Optional<Double> getAsDouble(Config config) {
		return getTypedValueFor(config, Double::parseDouble);
	}

	@Override
	public Optional<Long> getAsLong(Config config) {
		return getTypedValueFor(config, Long::parseLong);
	}

	@Override
	public Optional<String> getAsString(Config config) {
		return getTypedValueFor(config, Function.identity());
	}

	@Override
	public boolean getAsBoolean(Config config) {
		return getTypedValueFor(config, Boolean::parseBoolean).orElseThrow(
			() -> new IllegalArgumentException(
				String.format("Boolean configuration must at least have a default value, was not the case: [%s]", config)));
	}

	@Override
	public String getCountryCode() {
		String normalizedLocale = normalizeLocaleString(getAsStringOrThrow(Config.COUNTRY_LOCALE));

		if (normalizedLocale.contains(COUNTRY_SEPARATION_CHAR)) {
			return normalizedLocale.substring(normalizedLocale.lastIndexOf(COUNTRY_SEPARATION_CHAR) + 1);
		}
		return normalizedLocale;
	}

	public String normalizeLocaleString(String locale) {
		return normalizeLocaleStringStatic(locale);
	}

	public static String normalizeLocaleStringStatic(String locale) {
		locale = locale.trim();
		int pos = Math.max(locale.indexOf('-'), locale.indexOf('_'));
		if (pos < 0) {
			locale = locale.toLowerCase();
		} else {
			locale = locale.substring(0, pos).toLowerCase(Locale.ENGLISH) + '-' + locale.substring(pos + 1).toUpperCase(Locale.ENGLISH);
		}
		return locale;
	}

	public void setSystemConfigurationValueEjb(SystemConfigurationValueEjb systemConfigurationValueEjb) {
		this.systemConfigurationValueEjb = systemConfigurationValueEjb;
	}

}
