package de.symeda.sormas.backend.systemconfiguration;

import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.externaljournal.PatientDiaryConfig;
import de.symeda.sormas.api.externaljournal.SymptomJournalConfig;
import de.symeda.sormas.api.externaljournal.UserConfig;
import de.symeda.sormas.api.sormastosormas.SormasToSormasConfig;
import de.symeda.sormas.api.systemconfiguration.ExternalClientConfigurationFacade;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.util.RightsAllowed;

@Singleton(name = "ExternalClientConfiguration")
@Startup
@DependsOn("StartupShutdownService")
@TransactionManagement(TransactionManagementType.CONTAINER)
@RightsAllowed(UserRight._SYSTEM_CONFIGURATION)
public class ExternalClientConfigurationEjb implements ExternalClientConfigurationFacade {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private SystemConfigurationAccessorEjb systemConfigurationValueEjb;

	private static final Map<SystemConfigurationType, String> SORMAS2SORMAS_IGNORE_PROPERTIES = Map.of(
		SystemConfigurationType.SORMAS2SORMAS_IGNORE_PROPERTY_ADDITIONAL_DETAILS,
		SormasToSormasConfig.SORMAS2SORMAS_IGNORE_ADDITIONAL_DETAILS,
		SystemConfigurationType.SORMAS2SORMAS_IGNORE_PROPERTY_EXTERNAL_ID,
		SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_ID,
		SystemConfigurationType.SORMAS2SORMAS_IGNORE_PROPERTY_EXTERNAL_TOKEN,
		SormasToSormasConfig.SORMAS2SORMAS_IGNORE_EXTERNAL_TOKEN,
		SystemConfigurationType.SORMAS2SORMAS_IGNORE_PROPERTY_INTERNAL_TOKEN,
		SormasToSormasConfig.SORMAS2SORMAS_IGNORE_INTERNAL_TOKEN);

	@Override
	public SormasToSormasConfig getS2SConfig() {
		SormasToSormasConfig config = new SormasToSormasConfig();

		config.setPath(systemConfigurationValueEjb.getAsStringOrNull(SystemConfigurationType.SORMAS2SORMAS_PATH));
		config.setKeystoreName(systemConfigurationValueEjb.getAsStringOrNull(SystemConfigurationType.SORMAS2SORMAS_KEYSTORE_NAME));
		config.setKeystorePass(systemConfigurationValueEjb.getAsStringOrNull(SystemConfigurationType.SORMAS2SORMAS_KEYSTORE_PASS));
		config.setTruststoreName(systemConfigurationValueEjb.getAsStringOrNull(SystemConfigurationType.SORMAS2SORMAS_TRUSTSTORE_NAME));
		config.setTruststorePass(systemConfigurationValueEjb.getAsStringOrNull(SystemConfigurationType.SORMAS2SORMAS_TRUSTSTORE_PASS));
		config.setRootCaAlias(systemConfigurationValueEjb.getAsStringOrNull(SystemConfigurationType.SORMAS2SORMAS_ROOT_CA_ALIAS));
		config.setId(systemConfigurationValueEjb.getAsStringOrNull(SystemConfigurationType.SORMAS2SORMAS_ID));
		config.setOidcServer(systemConfigurationValueEjb.getAsStringOrNull(SystemConfigurationType.CENTRAL_OIDC_URL));
		config.setOidcRealm(systemConfigurationValueEjb.getAsStringOrNull(SystemConfigurationType.SORMAS2SORMAS_OIDC_REALM));
		config.setOidcClientId(systemConfigurationValueEjb.getAsStringOrNull(SystemConfigurationType.SORMAS2SORMAS_OIDC_CLIENT_ID));
		config.setOidcClientSecret(systemConfigurationValueEjb.getAsStringOrNull(SystemConfigurationType.SORMAS2SORMAS_OIDC_CLIENT_SECRET));
		config.setKeyPrefix(systemConfigurationValueEjb.getAsStringOrNull(SystemConfigurationType.SORMAS2SORMAS_ETCD_KEY_PREFIX));
		config.setIgnoreProperties(
			SORMAS2SORMAS_IGNORE_PROPERTIES.entrySet()
				.stream()
				.collect(Collectors.toMap(Map.Entry::getValue, entry -> systemConfigurationValueEjb.getAsBoolean(entry.getKey()))));
		config.setDistrictExternalId(systemConfigurationValueEjb.getAsStringOrNull(SystemConfigurationType.SORMAS2SORMAS_DISTRICT_EXTERNAL_ID));

		return config;
	}

	@Override
	public SymptomJournalConfig getSymptomJournalConfig() {
		SymptomJournalConfig config = new SymptomJournalConfig();
		config.setUrl(systemConfigurationValueEjb.getAsStringOrNull(SystemConfigurationType.INTERFACE_SYMPTOM_JOURNAL_URL));
		config.setAuthUrl(systemConfigurationValueEjb.getAsStringOrNull(SystemConfigurationType.INTERFACE_SYMPTOM_JOURNAL_AUTH_URL));
		config.setClientId(systemConfigurationValueEjb.getAsStringOrNull(SystemConfigurationType.INTERFACE_SYMPTOM_JOURNAL_CLIENTID));
		config.setSecret(systemConfigurationValueEjb.getAsStringOrNull(SystemConfigurationType.INTERFACE_SYMPTOM_JOURNAL_SECRET));

		UserConfig userConfig = new UserConfig();
		userConfig.setUsername(systemConfigurationValueEjb.getAsStringOrNull(SystemConfigurationType.INTERFACE_SYMPTOM_JOURNAL_DEFAULTUSER_USERNAME));
		userConfig.setPassword(systemConfigurationValueEjb.getAsStringOrNull(SystemConfigurationType.INTERFACE_SYMPTOM_JOURNAL_DEFAULTUSER_PASSWORD));

		if (StringUtils.isNoneBlank(userConfig.getUsername(), userConfig.getPassword())) {
			config.setDefaultUser(userConfig);
		}

		return config;
	}

	@Override
	public PatientDiaryConfig getPatientDiaryConfig() {
		PatientDiaryConfig config = new PatientDiaryConfig();
		config.setUrl(systemConfigurationValueEjb.getAsStringOrNull(SystemConfigurationType.INTERFACE_PATIENT_DIARY_URL));
		config.setProbandsUrl(systemConfigurationValueEjb.getAsStringOrNull(SystemConfigurationType.INTERFACE_PATIENT_DIARY_PROBANDSURL));
		config.setAuthUrl(systemConfigurationValueEjb.getAsStringOrNull(SystemConfigurationType.INTERFACE_PATIENT_DIARY_AUTHURL));
		config.setFrontendAuthUrl(systemConfigurationValueEjb.getAsStringOrNull(SystemConfigurationType.INTERFACE_PATIENT_DIARY_FRONTEND_AUTHURL));
		config.setTokenLifetime(
			Duration.ofSeconds(systemConfigurationValueEjb.getAsLongOrThrow(SystemConfigurationType.INTERFACE_PATIENT_DIARY_TOKEN_LIFETIME_SECONDS)));
		config.setEmail(systemConfigurationValueEjb.getAsStringOrNull(SystemConfigurationType.INTERFACE_PATIENT_DIARY_EMAIL));
		config.setPassword(systemConfigurationValueEjb.getAsStringOrNull(SystemConfigurationType.INTERFACE_PATIENT_DIARY_PASSWORD));
		config.setAcceptPhoneContact(systemConfigurationValueEjb.getAsBoolean(SystemConfigurationType.INTERFACE_PATIENT_DIARY_ACCEPT_PHONE_CONTACT));

		UserConfig userConfig = new UserConfig();
		userConfig.setUsername(systemConfigurationValueEjb.getAsStringOrNull(SystemConfigurationType.INTERFACE_PATIENT_DIARY_DEFAULTUSER_USERNAME));
		userConfig.setPassword(systemConfigurationValueEjb.getAsStringOrNull(SystemConfigurationType.INTERFACE_PATIENT_DIARY_DEFAULTUSER_PASSWORD));

		if (StringUtils.isNoneBlank(userConfig.getUsername(), userConfig.getPassword())) {
			config.setDefaultUser(userConfig);
		}

		return config;
	}
}
