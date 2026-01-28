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

import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.externaljournal.PatientDiaryConfig;
import de.symeda.sormas.api.externaljournal.SymptomJournalConfig;
import de.symeda.sormas.api.externaljournal.UserConfig;
import de.symeda.sormas.api.sormastosormas.SormasToSormasConfig;
import de.symeda.sormas.api.systemconfiguration.Config;
import de.symeda.sormas.api.systemconfiguration.ExternalClientConfigurationFacade;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.util.RightsAllowed;

@Singleton(name = "ExternalClientConfiguration")
@Startup
@DependsOn("StartupShutdownService")
@TransactionManagement(TransactionManagementType.CONTAINER)
@RightsAllowed(UserRight._SYSTEM_CONFIGURATION)
public class ExternalClientConfigurationEjb implements ExternalClientConfigurationFacade {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;

	@EJB
	public void setConfigFacade(ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade) {
		this.configFacade = configFacade;
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
	public boolean isS2SConfigured() {
		return configFacade.isS2SConfigured();
	}

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
				.collect(Collectors.toMap(Map.Entry::getValue, entry -> configFacade.getAsBoolean(entry.getKey()))));
		config.setDistrictExternalId(getConfigAsStringOrNull(Config.SORMAS2SORMAS_DISTRICT_EXTERNAL_ID));

		return config;
	}

	@Override
	public boolean isExternalJournalActive() {
		return !StringUtils.isAllBlank(configFacade.getAsStringOrNull(Config.INTERFACE_SYMPTOM_JOURNAL_URL), configFacade.getAsStringOrNull(Config.INTERFACE_PATIENT_DIARY_URL));
	}

	private String getConfigAsStringOrNull(Config sormas2sormasPath) {
		return configFacade.getAsString(sormas2sormasPath).orElse(null);
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
		config.setTokenLifetime(
			Duration.ofSeconds(configFacade.getAsLongOrThrow(Config.INTERFACE_PATIENT_DIARY_TOKEN_LIFETIME_SECONDS)));
		config.setEmail(getConfigAsStringOrNull(Config.INTERFACE_PATIENT_DIARY_EMAIL));
		config.setPassword(getConfigAsStringOrNull(Config.INTERFACE_PATIENT_DIARY_PASSWORD));
		config.setAcceptPhoneContact(configFacade.getAsBoolean(Config.INTERFACE_PATIENT_DIARY_ACCEPT_PHONE_CONTACT));

		UserConfig userConfig = new UserConfig();
		userConfig.setUsername(getConfigAsStringOrNull(Config.INTERFACE_PATIENT_DIARY_DEFAULTUSER_USERNAME));
		userConfig.setPassword(getConfigAsStringOrNull(Config.INTERFACE_PATIENT_DIARY_DEFAULTUSER_PASSWORD));

		if (StringUtils.isNoneBlank(userConfig.getUsername(), userConfig.getPassword())) {
			config.setDefaultUser(userConfig);
		}

		return config;
	}
}
