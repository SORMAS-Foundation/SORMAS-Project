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
package de.symeda.sormas.backend.common;

import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.audit.AuditIgnore;
import de.symeda.sormas.api.externaljournal.PatientDiaryConfig;
import de.symeda.sormas.api.externaljournal.SymptomJournalConfig;
import de.symeda.sormas.api.externaljournal.UserConfig;
import de.symeda.sormas.api.sormastosormas.SormasToSormasConfig;
import de.symeda.sormas.api.systemconfiguration.Config;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueFacade;
import de.symeda.sormas.backend.systemconfiguration.SystemConfigurationValueEjb;

@AuditIgnore
@Stateless(name = "ConfigFacade")
public class ConfigFacadeEjb implements ConfigFacade {

	public static final String COUNTRY_SEPARATION_CHAR = "-";
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
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

	private static final Map<Config, String> SORMAS2SORMAS_IGNORE_PROPERTIES = Map.of(
		Config.SORMAS2SORMAS_IGNORE_ADDITIONAL_DETAILS,
		SormasToSormasConfig.SORMAS2SORMAS_IGNORE_ADDITIONAL_DETAILS,
		Config.SORMAS2SORMAS_IGNORE_EXTERNAL_ID,
		SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_ID,
		Config.SORMAS2SORMAS_IGNORE_EXTERNAL_TOKEN,
		SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_TOKEN,
		Config.SORMAS2SORMAS_IGNORE_INTERNAL_TOKEN,
		SormasToSormasConfig.SORMAS2SORMAS_IGNORE_INTERNAL_TOKEN);

	@Override
	public SormasToSormasConfig getS2SConfig() {
		SormasToSormasConfig config = new SormasToSormasConfig();

		config.setPath(getConfigAsStringOrNull(Config.SORMAS2SORMAS_PATH));
		config.setKeystoreName(getConfigAsStringOrNull(Config.SORMAS2SORMAS_KEYSTORE_NAME));
		config.setKeystorePass(getConfigAsStringOrNull(Config.SORMAS2SORMAS_KEYSTORE_PASS));
		config.setTruststoreName(getConfigAsStringOrNull(Config.SORMAS2SORMAS_TRUSTSTORE_NAME));
		config.setTruststorePass(getConfigAsStringOrNull(Config.SORMAS2SORMAS_TRUSTSTORE_PASS));
		config.setRootCaAlias(getConfigAsStringOrNull(Config.SORMAS2SORMAS_ROOT_CA_ALIAS));
		config.setId(getConfigAsStringOrNull(Config.SORMAS2SORMAS_ID));
		config.setOidcServer(getConfigAsStringOrNull(Config.CENTRAL_OIDC_URL));
		config.setOidcRealm(getConfigAsStringOrNull(Config.SORMAS2SORMAS_OIDC_REALM));
		config.setOidcClientId(getConfigAsStringOrNull(Config.SORMAS2SORMAS_OIDC_CLIENT_ID));
		config.setOidcClientSecret(getConfigAsStringOrNull(Config.SORMAS2SORMAS_OIDC_CLIENT_SECRET));
		config.setKeyPrefix(getConfigAsStringOrNull(Config.SORMAS2SORMAS_ETCD_KEY_PREFIX));
		config.setIgnoreProperties(
			SORMAS2SORMAS_IGNORE_PROPERTIES.entrySet()
				.stream()
				.collect(Collectors.toMap(Map.Entry::getValue, entry -> getAsBoolean(entry.getKey()))));
		config.setDistrictExternalId(getConfigAsStringOrNull(Config.SORMAS2SORMAS_DISTRICT_EXTERNAL_ID));

		return config;
	}

	@Override
	public boolean isExternalJournalActive() {
		return !StringUtils
			.isAllBlank(getAsStringOrNull(Config.INTERFACE_SYMPTOM_JOURNAL_URL), getAsStringOrNull(Config.INTERFACE_PATIENT_DIARY_URL));
	}

	private String getConfigAsStringOrNull(Config sormas2sormasPath) {
		return getAsString(sormas2sormasPath).orElse(null);
	}

	@Override
	public SymptomJournalConfig getSymptomJournalConfig() {
		SymptomJournalConfig config = new SymptomJournalConfig();
		config.setUrl(getConfigAsStringOrNull(Config.INTERFACE_SYMPTOM_JOURNAL_URL));
		config.setAuthUrl(getConfigAsStringOrNull(Config.INTERFACE_SYMPTOM_JOURNAL_AUTH_URL));
		config.setClientId(getConfigAsStringOrNull(Config.INTERFACE_SYMPTOM_JOURNAL_CLIENTID));
		config.setSecret(getConfigAsStringOrNull(Config.INTERFACE_SYMPTOM_JOURNAL_SECRET));

		UserConfig userConfig = new UserConfig();
		userConfig.setUsername(getConfigAsStringOrNull(Config.INTERFACE_SYMPTOM_JOURNAL_DEFAULTUSER_USERNAME));
		userConfig.setPassword(getConfigAsStringOrNull(Config.INTERFACE_SYMPTOM_JOURNAL_DEFAULTUSER_PASSWORD));

		if (StringUtils.isNoneBlank(userConfig.getUsername(), userConfig.getPassword())) {
			config.setDefaultUser(userConfig);
		}

		return config;
	}

	@Override
	public PatientDiaryConfig getPatientDiaryConfig() {
		PatientDiaryConfig config = new PatientDiaryConfig();
		config.setUrl(getConfigAsStringOrNull(Config.INTERFACE_PATIENT_DIARY_URL));
		config.setProbandsUrl(getConfigAsStringOrNull(Config.INTERFACE_PATIENT_DIARY_PROBANDS_URL));
		config.setAuthUrl(getConfigAsStringOrNull(Config.INTERFACE_PATIENT_DIARY_AUTH_URL));
		config.setFrontendAuthUrl(getConfigAsStringOrNull(Config.INTERFACE_PATIENT_DIARY_FRONTEND_AUTHURL));
		config.setTokenLifetime(Duration.ofSeconds(getAsLongOrThrow(Config.INTERFACE_PATIENT_DIARY_TOKEN_LIFETIME_SECONDS)));
		config.setEmail(getConfigAsStringOrNull(Config.INTERFACE_PATIENT_DIARY_EMAIL));
		config.setPassword(getConfigAsStringOrNull(Config.INTERFACE_PATIENT_DIARY_PASSWORD));
		config.setAcceptPhoneContact(getAsBoolean(Config.INTERFACE_PATIENT_DIARY_ACCEPT_PHONE_CONTACT));

		UserConfig userConfig = new UserConfig();
		userConfig.setUsername(getConfigAsStringOrNull(Config.INTERFACE_PATIENT_DIARY_DEFAULTUSER_USERNAME));
		userConfig.setPassword(getConfigAsStringOrNull(Config.INTERFACE_PATIENT_DIARY_DEFAULTUSER_PASSWORD));

		if (StringUtils.isNoneBlank(userConfig.getUsername(), userConfig.getPassword())) {
			config.setDefaultUser(userConfig);
		}

		return config;
	}

	@LocalBean
	@Stateless
	public static class ConfigFacadeEjbLocal extends ConfigFacadeEjb {

	}
}
