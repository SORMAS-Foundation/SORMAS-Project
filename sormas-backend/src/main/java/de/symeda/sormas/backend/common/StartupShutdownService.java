/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.AuthProvider;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.externaljournal.PatientDiaryConfig;
import de.symeda.sormas.api.externaljournal.SymptomJournalConfig;
import de.symeda.sormas.api.externaljournal.UserConfig;
import de.symeda.sormas.api.feature.FeatureConfigurationDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityCriteria;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DefaultEntityHelper;
import de.symeda.sormas.api.utils.PasswordHelper;
import de.symeda.sormas.backend.audit.AuditLoggerEjb;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.deletionconfiguration.DeletionConfigurationService;
import de.symeda.sormas.backend.disease.DiseaseConfiguration;
import de.symeda.sormas.backend.disease.DiseaseConfigurationService;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb;
import de.symeda.sormas.backend.feature.FeatureConfigurationService;
import de.symeda.sormas.backend.importexport.ImportFacadeEjb.ImportFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.central.CentralInfraSyncFacade;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.community.CommunityService;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.country.CountryFacadeEjb.CountryFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.country.CountryService;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.facility.FacilityService;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntryService;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.sormastosormas.SormasToSormasFacadeEjb;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserRole;
import de.symeda.sormas.backend.user.UserRoleService;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.user.event.PasswordResetEvent;
import de.symeda.sormas.backend.user.event.UserUpdateEvent;
import de.symeda.sormas.backend.util.MockDataGenerator;
import de.symeda.sormas.backend.util.ModelConstants;

@Singleton(name = "StartupShutdownService")
@Startup
@RunAs(UserRight._SYSTEM)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class StartupShutdownService {

	static final String SORMAS_SCHEMA = "sql/sormas_schema.sql";
	static final String AUDIT_SCHEMA = "sql/sormas_audit_schema.sql";
	private static final Pattern SQL_COMMENT_PATTERN = Pattern.compile("^\\s*(--.*)?");
	//@formatter:off
    private static final Pattern SCHEMA_VERSION_SQL_PATTERN = Pattern.compile(
            "^\\s*INSERT\\s+INTO\\s+schema_version\\s*" +
                    "\\(\\s*version_number\\s*,[^)]+\\)\\s*" +
                    "VALUES\\s*\\(\\s*([0-9]+)\\s*,.+");
    //@formatter:on

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;
	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME_AUDITLOG)
	private EntityManager emAudit;

	@EJB
	private ConfigFacadeEjbLocal configFacade;
	@EJB
	private UserService userService;
	@EJB
	private ContactService contactService;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private CommunityService communityService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private FacilityFacadeEjbLocal facilityFacade;
	@EJB
	private PointOfEntryService pointOfEntryService;
	@EJB
	private ImportFacadeEjbLocal importFacade;
	@EJB
	private DiseaseConfigurationService diseaseConfigurationService;
	@EJB
	private FeatureConfigurationService featureConfigurationService;
	@EJB
	private FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;
	@EJB
	private CountryFacadeEjbLocal countryFacade;
	@EJB
	private CountryService countryService;
	@EJB
	private SormasToSormasFacadeEjb.SormasToSormasFacadeEjbLocal sormasToSormasFacadeEjb;
	@EJB
	private CentralInfraSyncFacade centralInfraSync;
	@EJB
	private UpdateQueryTransactionWrapper updateQueryTransactionWrapper;
	@EJB
	DefaultEntitiesCreator defaultEntitiesCreator;
	@Inject
	private Event<UserUpdateEvent> userUpdateEvent;
	@EJB
	private DeletionConfigurationService deletionConfigurationService;
	@EJB
	AuditLoggerEjb.AuditLoggerEjbLocal auditLogger;
	@EJB
	private UserRoleService userRoleService;

	@Inject
	private Event<PasswordResetEvent> passwordResetEvent;

	static boolean isBlankOrSqlComment(String sqlLine) {
		return SQL_COMMENT_PATTERN.matcher(sqlLine).matches();
	}

	static Integer extractSchemaVersion(String sqlLine) {

		return Optional.ofNullable(sqlLine)
			.map(SCHEMA_VERSION_SQL_PATTERN::matcher)
			.filter(Matcher::matches)
			.map(m -> m.group(1))
			.map(Integer::parseInt)
			.orElse(null);
	}

	@PostConstruct
	public void startup() {
		auditLogger.logApplicationStart();
		checkDatabaseConfig(em);

		logger.info("Initiating automatic database update of main database...");
		updateDatabase(UpdateQueryTransactionWrapper.TargetDb.SORMAS, em, SORMAS_SCHEMA);

		logger.info("Initiating automatic database update of audit database...");
		updateDatabase(UpdateQueryTransactionWrapper.TargetDb.AUDIT, emAudit, AUDIT_SCHEMA);

		I18nProperties.setDefaultLanguage(Language.fromLocaleString(configFacade.getCountryLocale()));

		createDefaultInfrastructureData();

		facilityService.createConstantFacilities();

		pointOfEntryService.createConstantPointsOfEntry();

		upgrade(); // Has to be called before createDefaultUsers.

		createDefaultUsers();

		createOrUpdateSormasToSormasUser();

		createOrUpdateSymptomJournalUser();

		createOrUpdatePatientDiaryUser();

		syncUsers();

		createMissingDiseaseConfigurations();

		featureConfigurationService.createMissingFeatureConfigurations();
		featureConfigurationService.updateFeatureConfigurations();

		createImportTemplateFiles(featureConfigurationFacade.getActiveServerFeatureConfigurations());

		deletionConfigurationService.createMissingDeletionConfigurations();

		configFacade.validateAppUrls();
		configFacade.validateConfigUrls();

		centralInfraSync.syncAll();
	}

	private void createDefaultInfrastructureData() {
		if (!configFacade.isCreateDefaultEntities()) {
			// return if isCreateDefaultEntities() is false
			logger.info("Skipping the creation of default infrastructure data");
			return;
		}

		// Region
		Region region = null;
		if (regionService.count() == 0) {
			region = defaultEntitiesCreator.createDefaultRegion(false);
			regionService.ensurePersisted(region);
		}

		// District
		District district = null;
		if (districtService.count() == 0) {
			if (region == null) {
				region = regionService.getAll().get(0);
			}
			district = defaultEntitiesCreator.createDefaultDistrict(region, false);
			districtService.ensurePersisted(district);
			region.getDistricts().add(district);
		}

		// Community
		Community community = null;
		if (communityService.count() == 0) {
			if (district == null) {
				district = districtService.getAll().get(0);
			}
			community = defaultEntitiesCreator.createDefaultCommunity(district, false);
			communityService.ensurePersisted(community);
			district.getCommunities().add(community);
		}

		// Facility
		Facility facility;
		FacilityCriteria facilityCriteria = new FacilityCriteria();
		if (facilityFacade.count(facilityCriteria) == 0) {
			if (community == null) {
				community = communityService.getAll().get(0);
			}
			if (district == null) {
				district = districtService.getAll().get(0);
			}
			if (region == null) {
				region = regionService.getAll().get(0);
			}
			facility = defaultEntitiesCreator.createDefaultFacility(region, district, community);
			facilityService.ensurePersisted(facility);
		}

		// Laboratory
		Facility laboratory;
		facilityCriteria.type(FacilityType.LABORATORY);
		if (facilityFacade.count(facilityCriteria) == 0) {
			if (community == null) {
				community = communityService.getAll().get(0);
			}
			if (district == null) {
				district = districtService.getAll().get(0);
			}
			if (region == null) {
				region = regionService.getAll().get(0);
			}
			laboratory = defaultEntitiesCreator.createDefaultLaboratory(region, district, community);
			facilityService.ensurePersisted(laboratory);
		}

		// Point of Entry
		PointOfEntry pointOfEntry;
		if (pointOfEntryService.count() == 0) {
			if (district == null) {
				district = districtService.getAll().get(0);
			}
			if (region == null) {
				region = regionService.getAll().get(0);
			}
			pointOfEntry = defaultEntitiesCreator.createDefaultPointOfEntry(region, district);
			pointOfEntryService.ensurePersisted(pointOfEntry);
		}
	}

	private void createDefaultUsers() {

		if (userService.count() == 0) {

			// Create Admin
			createAndPersistDefaultUser(
				userRoleService.getByCaption(I18nProperties.getEnumCaption(DefaultUserRole.ADMIN)),
				"ad",
				"min",
				DefaultEntityHelper.ADMIN_USERNAME_AND_PASSWORD,
				u -> {
				});

			if (!configFacade.isCreateDefaultEntities()) {
				// return if isCreateDefaultEntities() is false
				logger.info("Skipping the creation of default entities");
				return;
			}

			Region region = regionService.getByUuid(DefaultEntityHelper.getConstantUuidFor(DefaultEntityHelper.DefaultInfrastructureUuidSeed.REGION));
			District district = region.getDistricts().get(0);
			Community community = district.getCommunities().get(0);
			List<Facility> healthFacilities = facilityService.getActiveFacilitiesByCommunityAndType(community, FacilityType.HOSPITAL, false, false);
			Facility facility = !healthFacilities.isEmpty() ? healthFacilities.get(0) : null;
			List<Facility> laboratories = facilityService.getAllActiveLaboratories(false);
			Facility laboratory = !laboratories.isEmpty() ? laboratories.get(0) : null;
			PointOfEntry pointOfEntry = pointOfEntryService.getAllActive().get(0);

			logger.info("Create default users");

			// Create Surveillance Supervisor
			createAndPersistDefaultUser(
				userRoleService.getByCaption(I18nProperties.getEnumCaption(DefaultUserRole.SURVEILLANCE_SUPERVISOR)),
				"Surveillance",
				"Supervisor",
				DefaultEntityHelper.SURV_SUP_USERNAME_AND_PASSWORD,
				u -> u.setRegion(region));

			// Create Case Supervisor
			createAndPersistDefaultUser(
				userRoleService.getByCaption(I18nProperties.getEnumCaption(DefaultUserRole.CASE_SUPERVISOR)),
				"Case",
				"Supervisor",
				DefaultEntityHelper.CASE_SUP_USERNAME_AND_PASSWORD,
				u -> u.setRegion(region));

			// Create Contact Supervisor
			createAndPersistDefaultUser(
				userRoleService.getByCaption(I18nProperties.getEnumCaption(DefaultUserRole.CONTACT_SUPERVISOR)),
				"Contact",
				"Supervisor",
				DefaultEntityHelper.CONT_SUP_USERNAME_AND_PASSWORD,
				u -> u.setRegion(region));

			// Create Point of Entry Supervisor
			createAndPersistDefaultUser(
				userRoleService.getByCaption(I18nProperties.getEnumCaption(DefaultUserRole.POE_SUPERVISOR)),
				"Point of Entry",
				"Supervisor",
				DefaultEntityHelper.POE_SUP_USERNAME_AND_PASSWORD,
				u -> u.setRegion(region));

			// Create Laboratory Officer
			createAndPersistDefaultUser(
				userRoleService.getByCaption(I18nProperties.getEnumCaption(DefaultUserRole.LAB_USER)),
				"Laboratory",
				"Officer",
				DefaultEntityHelper.LAB_OFF_USERNAME_AND_PASSWORD,
				u -> u.setLaboratory(laboratory));

			// Create Event Officer
			createAndPersistDefaultUser(
				userRoleService.getByCaption(I18nProperties.getEnumCaption(DefaultUserRole.EVENT_OFFICER)),
				"Event",
				"Officer",
				DefaultEntityHelper.EVE_OFF_USERNAME_AND_PASSWORD,
				u -> u.setRegion(region));

			// Create National User
			createAndPersistDefaultUser(
				userRoleService.getByCaption(I18nProperties.getEnumCaption(DefaultUserRole.NATIONAL_USER)),
				"National",
				"User",
				DefaultEntityHelper.NAT_USER_USERNAME_AND_PASSWORD,
				u -> {
				});

			// Create National Clinician
			createAndPersistDefaultUser(
				userRoleService.getByCaption(I18nProperties.getEnumCaption(DefaultUserRole.NATIONAL_CLINICIAN)),
				"National",
				"Clinician",
				DefaultEntityHelper.NAT_CLIN_USERNAME_AND_PASSWORD,
				u -> {
				});

			// Create Surveillance Officer
			User surveillanceOfficer = createAndPersistDefaultUser(
				userRoleService.getByCaption(I18nProperties.getEnumCaption(DefaultUserRole.SURVEILLANCE_OFFICER)),
				"Surveillance",
				"Officer",
				DefaultEntityHelper.SURV_OFF_USERNAME_AND_PASSWORD,
				u -> {
					u.setRegion(region);
					u.setDistrict(district);
				});

			// Create Hospital Informant
			createAndPersistDefaultUser(
				userRoleService.getByCaption(I18nProperties.getEnumCaption(DefaultUserRole.HOSPITAL_INFORMANT)),
				"Hospital",
				"Informant",
				DefaultEntityHelper.HOSP_INF_USERNAME_AND_PASSWORD,
				u -> {
					u.setRegion(region);
					u.setDistrict(district);
					u.setHealthFacility(facility);
					u.setAssociatedOfficer(surveillanceOfficer);
				});

			// Create Community Officer
			createAndPersistDefaultUser(
				userRoleService.getByCaption(I18nProperties.getEnumCaption(DefaultUserRole.COMMUNITY_OFFICER)),
				"Community",
				"Officer",
				DefaultEntityHelper.COMM_OFF_USERNAME_AND_PASSWORD,
				u -> {
					u.setRegion(region);
					u.setDistrict(district);
					u.setCommunity(community);
				});

			// Create Poe Informant
			createAndPersistDefaultUser(
				userRoleService.getByCaption(I18nProperties.getEnumCaption(DefaultUserRole.POE_INFORMANT)),
				"Poe",
				"Informant",
				DefaultEntityHelper.POE_INF_USERNAME_AND_PASSWORD,
				u -> {
					u.setUserName("PoeInf");
					u.setRegion(region);
					u.setDistrict(district);
					u.setPointOfEntry(pointOfEntry);
					u.setAssociatedOfficer(surveillanceOfficer);
				});
		}
	}

	private User createAndPersistDefaultUser(
		UserRole userRole,
		String firstName,
		String lastName,
		DataHelper.Pair<String, String> usernameAndPassword,
		Consumer<User> userModificator) {
		if (userRole != null) {
			User user = MockDataGenerator.createUser(userRole, firstName, lastName, usernameAndPassword.getElement1());
			user.setUserName(usernameAndPassword.getElement0());
			userModificator.accept(user);
			userService.persist(user);
			userUpdateEvent.fire(new UserUpdateEvent(user));
			return user;
		} else {
			logger.warn(
				"Default user '{} {}' was not created because the user role to be assigned cannot be found in the database.",
				firstName,
				lastName);
			return null;
		}
	}

	private void createOrUpdateSormasToSormasUser() {
		if (sormasToSormasFacadeEjb.isFeatureConfigured()) {
			// password is never used, just to prevent login as this user
			byte[] pwd = new byte[64];
			SecureRandom rnd = new SecureRandom();
			rnd.nextBytes(pwd);

			HashSet<UserRole> userRoles = new HashSet<>();
			userRoles.addAll(
				Arrays.asList(
					userRoleService.getByCaption(I18nProperties.getEnumCaption(DefaultUserRole.SORMAS_TO_SORMAS_CLIENT)),
					userRoleService.getByCaption(I18nProperties.getEnumCaption(DefaultUserRole.NATIONAL_USER))));

			createOrUpdateDefaultUser(userRoles, DefaultEntityHelper.SORMAS_TO_SORMAS_USER_NAME, new String(pwd), "Sormas to Sormas", "Client");
		}
	}

	private void createOrUpdateSymptomJournalUser() {
		SymptomJournalConfig symptomJournalConfig = configFacade.getSymptomJournalConfig();
		UserConfig userConfig = symptomJournalConfig.getDefaultUser();
		if (userConfig == null) {
			logger.debug("Symptom journal default user not configured");
			return;
		}

		createOrUpdateDefaultUser(
			new HashSet<>(Arrays.asList(userRoleService.getByCaption(I18nProperties.getEnumCaption(DefaultUserRole.REST_EXTERNAL_VISITS_USER)))),
			userConfig.getUsername(),
			userConfig.getPassword(),
			"Symptom",
			"Journal");
	}

	private void createOrUpdatePatientDiaryUser() {
		PatientDiaryConfig patientDiaryConfig = configFacade.getPatientDiaryConfig();
		UserConfig userConfig = patientDiaryConfig.getDefaultUser();
		if (userConfig == null) {
			logger.debug("Patient diary default user not configured");
			return;
		}

		createOrUpdateDefaultUser(
			new HashSet<>(Arrays.asList(userRoleService.getByCaption(I18nProperties.getEnumCaption(DefaultUserRole.REST_EXTERNAL_VISITS_USER)))),
			userConfig.getUsername(),
			userConfig.getPassword(),
			"Patient",
			"Diary");
	}

	private void createOrUpdateDefaultUser(Set<UserRole> userRoles, String username, String password, String firstName, String lastName) {

		if (StringUtils.isAnyBlank(username, password)) {
			logger.debug("Invalid user details. Will not create/update default user");
			return;
		}

		User existingUser = userService.getByUserName(username);

		boolean allUserRolesExist = !CollectionUtils.isEmpty(userRoles) && userRoles.stream().noneMatch(Objects::isNull);
		if (existingUser == null) {
			if (!allUserRolesExist) {
				logger.warn(
					"User '{} {}' was not created because at least one of the user roles to be assigned cannot be found in the database.",
					firstName,
					lastName);
			} else if (!DataHelper.isNullOrEmpty(password)) {
				User newUser = MockDataGenerator.createUser(userRoles, firstName, lastName, password);
				newUser.setUserName(username);

				userService.persist(newUser);
				userUpdateEvent.fire(new UserUpdateEvent(newUser));
			}
		} else if (!DataHelper.equal(existingUser.getPassword(), PasswordHelper.encodePassword(password, existingUser.getSeed()))) {
			existingUser.setSeed(PasswordHelper.createPass(16));
			existingUser.setPassword(PasswordHelper.encodePassword(password, existingUser.getSeed()));
			if (allUserRolesExist) {
				existingUser.setUserRoles(userRoles);
			} else {
				logger.warn(
					"User roles of user '{} {}' were not updated because at least one of the user roles to be assigned cannot be found in the database.",
					firstName,
					lastName);
			}

			userService.persist(existingUser);
			passwordResetEvent.fire(new PasswordResetEvent(existingUser));
		} else if (userRoles.stream().anyMatch(r -> !existingUser.getUserRoles().contains(r))
			|| existingUser.getUserRoles().stream().anyMatch(r -> !userRoles.contains(r))) {
			if (allUserRolesExist) {
				existingUser.setUserRoles(userRoles);
				userService.persist(existingUser);
			} else {
				logger.warn(
					"User roles of user '{} {}' were not updated because at least one of the user roles to be assigned cannot be found in the database.",
					firstName,
					lastName);
			}
		}

	}

	/**
	 * Synchronizes all active users with the external Authentication Provider if User Sync at startup is enabled and supported.
	 *
	 * @see AuthProvider#isUserSyncSupported()
	 * @see AuthProvider#isUserSyncAtStartupEnabled()
	 */
	private void syncUsers() {

		AuthProvider authProvider = AuthProvider.getProvider(configFacade);

		if (!authProvider.isUserSyncSupported()) {
			logger.info("Active Authentication Provider {} doesn't support user sync", authProvider.getName());
			return;
		}

		if (!authProvider.isUserSyncAtStartupEnabled()) {
			logger.info("User sync at startup is disabled. Enable this in SORMAS properties if the active Authentication Provider supports it");
			return;
		}

		List<User> users = userService.getAllActive();
		for (User user : users) {
			syncUser(user);
		}
		logger.info("User synchronization finalized");
	}

	/**
	 * Triggers the user sync asynchronously to not block the deployment step
	 */
	private void syncUser(User user) {
		String shortUuid = DataHelper.getShortUuid(user.getUuid());
		logger.debug("Synchronizing user {}", shortUuid);
		try {

			UserUpdateEvent event = new UserUpdateEvent(user);
			event.setExceptionCallback(exceptionMessage -> logger.error("Could not synchronize user {} due to {}", shortUuid, exceptionMessage));

			this.userUpdateEvent.fireAsync(event);
		} catch (Throwable e) {
			logger.error(MessageFormat.format("Unexpected exception when synchronizing user {0}", shortUuid), e);
		}
	}

	/**
	 * Checks if the PostgreSQL server is configured correctly to run SORMAS.
	 */
	private void checkDatabaseConfig(EntityManager entityManager) {

		List<String> errors = new ArrayList<>();

		// Check postgres version
		String versionString = entityManager.createNativeQuery("SHOW server_version").getSingleResult().toString();
		if (isSupportedDatabaseVersion(versionString)) {
			logger.debug("Your PostgreSQL Version ({}) is currently supported.", versionString);
		} else {
			logger.warn("Your PostgreSQL Version ({}) is currently not supported.", versionString);
		}

		// Check setting "max_prepared_transactions"
		int maxPreparedTransactions =
			Integer.parseInt(entityManager.createNativeQuery("select current_setting('max_prepared_transactions')").getSingleResult().toString());
		if (maxPreparedTransactions < 1) {
			errors.add("max_prepared_transactions is not set. A value of at least 64 is recommended.");
		} else if (maxPreparedTransactions < 64) {
			logger.info("max_prepared_transactions is set to {}. A value of at least 64 is recommended.", maxPreparedTransactions);
		}

		// Check that required extensions are installed
		Stream.of("temporal_tables", "pg_trgm").filter(t -> {
			String q = "select count(*) from pg_extension where extname = '" + t + "'";
			int count = ((Number) entityManager.createNativeQuery(q).getSingleResult()).intValue();
			return count == 0;
		}).map(t -> "extension '" + t + "' has to be installed").forEach(errors::add);

		if (!errors.isEmpty()) {
			// List all config problems and stop deployment
			throw new RuntimeException(errors.stream().collect(Collectors.joining("\n * ", "Postgres setup is not compatible:\n * ", "")));
		}
	}

	/**
	 * @param versionString
	 *            Database system version.
	 * @return {@code true}, if the database version is supported.
	 */
	static boolean isSupportedDatabaseVersion(String versionString) {

		String versionBegin = versionString.split(" ")[0];
		String versionRegexp = Stream.of("9\\.5", "9\\.5\\.\\d+", "9\\.6", "9\\.6\\.\\d+", "10\\.\\d+").collect(Collectors.joining(")|(", "(", ")"));
		return versionBegin.matches(versionRegexp);
	}

	private void updateDatabase(UpdateQueryTransactionWrapper.TargetDb db, EntityManager entityManager, String schemaFileName) {

		logger.info("Starting automatic database update...");

		boolean hasSchemaVersion =
			!entityManager.createNativeQuery("SELECT 1 FROM information_schema.tables WHERE table_name = 'schema_version'").getResultList().isEmpty();
		Integer databaseVersion;
		if (hasSchemaVersion) {
			databaseVersion = (Integer) entityManager.createNativeQuery("SELECT MAX(version_number) FROM schema_version").getSingleResult();
		} else {
			databaseVersion = null;
		}

		try (InputStream schemaStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(schemaFileName);
			Scanner scanner = new Scanner(schemaStream, StandardCharsets.UTF_8.name())) {
			StringBuilder nextUpdateBuilder = new StringBuilder();
			boolean currentVersionReached = databaseVersion == null;

			while (scanner.hasNextLine()) {
				String nextLine = scanner.nextLine();

				if (isBlankOrSqlComment(nextLine)) {
					continue;
				}

				Integer schemaLineVersion = extractSchemaVersion(nextLine);

				//skip until current version of database is reached
				if (!currentVersionReached) {
					currentVersionReached = databaseVersion.equals(schemaLineVersion);
					continue;
				}

				// escape for hibernate
				// note: This will also escape ':' in pure strings, where a replacement may cause problems
				nextLine = nextLine.replaceAll(":", "\\\\:");

				// Add the line to the StringBuilder
				nextUpdateBuilder.append(nextLine).append("\n");

				// Perform the current update when the INSERT INTO schema_version statement is reached
				if (schemaLineVersion != null) {
					logger.info("Updating database to version {}...", schemaLineVersion);
					updateQueryTransactionWrapper.executeUpdate(db, nextUpdateBuilder.toString());
					nextUpdateBuilder.setLength(0);
				}
			}
		} catch (IOException e) {
			logger.error("Could not load {} file. Database update not performed.", schemaFileName);
			throw new UncheckedIOException(e);
		} finally {
			logger.info("Database update completed.");
		}
	}

	private void upgrade() {

		@SuppressWarnings("unchecked")
		List<Integer> versionsNeedingUpgrade = em.createNativeQuery("SELECT version_number FROM schema_version WHERE upgradeNeeded").getResultList();

		// IMPORTANT: Never write code to go through all entities in a table and do something
		// here. This will make the deployment fail when there are too many entities in the database.

		for (Integer versionNeedingUpgrade : versionsNeedingUpgrade) {
			switch (versionNeedingUpgrade) {
			case 95:
				// update follow up and status for all contacts
				for (Contact contact : contactService.getAll()) {
					contactService.updateFollowUpDetails(contact, false);
					contactService.udpateContactStatus(contact);
				}
				break;
			case 354:
				CountryReferenceDto serverCountry = countryFacade.getServerCountry();

				if (serverCountry != null) {
					Country country = countryService.getByUuid(serverCountry.getUuid());
					em.createQuery("UPDATE Region set country = :server_country, changeDate = :change_date WHERE country is null")
						.setParameter("server_country", country)
						.setParameter("change_date", new Timestamp(new Date().getTime()))
						.executeUpdate();
				}
				break;
			case 460:
				fillDefaultUserRoles();
				break;
			case 463:
				List<User> usersWithoutUserRoles =
					userService.getAll().stream().filter(user -> user.getUserRoles().isEmpty()).collect(Collectors.toList());
				if (!usersWithoutUserRoles.isEmpty()) {
					UserRole importuserUserRole = userRoleService.getByCaption(I18nProperties.getEnumCaption(DefaultUserRole.IMPORT_USER));
					if (importuserUserRole == null) {
						throw new IllegalArgumentException(
							"Could not find default IMPORT_USER role in the database; Please ensure that the database contains no user without a user role and redeploy the server.");
					} else {
						usersWithoutUserRoles.forEach(user -> {
							user.getUserRoles().add(importuserUserRole);
							userService.persist(user);
						});
					}
				}
				break;

			default:
				throw new NoSuchElementException(DataHelper.toStringNullable(versionNeedingUpgrade));
			}

			int updatedRows = em.createNativeQuery("UPDATE schema_version SET upgradeNeeded=false WHERE version_number=?1")
				.setParameter(1, versionNeedingUpgrade)
				.executeUpdate();
			if (updatedRows != 1) {
				logger.error("Could not UPDATE schema_version table. Missing user rights?");
			}
		}
	}

	/**
	 * UserRoles are created via SQL to support migration for existing users.
	 */
	private void fillDefaultUserRoles() {
		Arrays.stream(DefaultUserRole.values()).forEach(role -> {
			UserRole userRole = userRoleService.getByCaption(role.name());
			userRole.setCaption(I18nProperties.getEnumCaption(role));
			userRole.setPortHealthUser(role.isPortHealthUser());
			userRole.setHasAssociatedDistrictUser(role.hasAssociatedDistrictUser());
			userRole.setHasOptionalHealthFacility(DefaultUserRole.hasOptionalHealthFacility(Collections.singleton(role)));
			userRole.setEnabled(true);
			userRole.setJurisdictionLevel(role.getJurisdictionLevel());
			userRole.setSmsNotificationTypes(role.getSmsNotificationTypes());
			userRole.setEmailNotificationTypes(role.getEmailNotificationTypes());
			userRole.setUserRights(role.getDefaultUserRights());
			userRoleService.persist(userRole);
		});
	}

	private void createImportTemplateFiles(List<FeatureConfigurationDto> featureConfigurations) {

		try {
			importFacade.generateCaseImportTemplateFile(featureConfigurations);
		} catch (IOException e) {
			logger.error("Could not create case import template .csv file.");
		}

		try {
			importFacade.generateCaseContactImportTemplateFile(featureConfigurations);
		} catch (IOException e) {
			logger.error("Could not create case contact import template .csv file.");
		}

		try {
			importFacade.generateContactImportTemplateFile(featureConfigurations);
		} catch (IOException e) {
			logger.error("Could not create contact import template .csv file.");
		}

		try {
			importFacade.generateCaseLineListingImportTemplateFile();
		} catch (IOException e) {
			logger.error("Could not create line listing import template .csv file.");
		}

		try {
			importFacade.generatePointOfEntryImportTemplateFile(featureConfigurations);
		} catch (IOException e) {
			logger.error("Could not create point of entry import template .csv file.");
		}

		try {
			importFacade.generatePopulationDataImportTemplateFile();
		} catch (IOException e) {
			logger.error("Could not create population data import template .csv file.");
		}

		try {
			importFacade.generateAreaImportTemplateFile(featureConfigurations);
		} catch (IOException e) {
			logger.error("Could not create area import template .csv file.");
		}

		try {
			importFacade.generateContinentImportTemplateFile(featureConfigurations);
		} catch (IOException e) {
			logger.error("Could not create continent import template .csv file.");
		}

		try {
			importFacade.generateSubcontinentImportTemplateFile(featureConfigurations);
		} catch (IOException e) {
			logger.error("Could not create subcontinent import template .csv file.");
		}

		try {
			importFacade.generateCountryImportTemplateFile(featureConfigurations);
		} catch (IOException e) {
			logger.error("Could not create country import template .csv file.");
		}
		try {
			importFacade.generateRegionImportTemplateFile(featureConfigurations);
		} catch (IOException e) {
			logger.error("Could not create region import template .csv file.");
		}
		try {
			importFacade.generateDistrictImportTemplateFile(featureConfigurations);
		} catch (IOException e) {
			logger.error("Could not create district import template .csv file.");
		}
		try {
			importFacade.generateCommunityImportTemplateFile(featureConfigurations);
		} catch (IOException e) {
			logger.error("Could not create community import template .csv file.");
		}
		try {
			importFacade.generateFacilityImportTemplateFile(featureConfigurations);
		} catch (IOException e) {
			logger.error("Could not create facility/laboratory import template .csv file.");
		}

		try {
			importFacade.generateEventImportTemplateFile(featureConfigurations);
		} catch (IOException e) {
			logger.error("Could not create event import template .csv file.");
		}

		try {
			importFacade.generateEventParticipantImportTemplateFile(featureConfigurations);
		} catch (IOException e) {
			logger.error("Could not create event participant import template .csv file.");
		}
	}

	private void createMissingDiseaseConfigurations() {
		List<DiseaseConfiguration> diseaseConfigurations = diseaseConfigurationService.getAll();
		List<Disease> configuredDiseases = diseaseConfigurations.stream().map(DiseaseConfiguration::getDisease).collect(Collectors.toList());
		Arrays.stream(Disease.values()).filter(d -> !configuredDiseases.contains(d)).forEach(d -> {
			DiseaseConfiguration configuration = DiseaseConfiguration.build(d);
			diseaseConfigurationService.ensurePersisted(configuration);
		});
	}

	@PreDestroy
	public void shutdown() {
		auditLogger.logApplicationStop();
	}

	@LocalBean
	@Stateless
	public static class UpdateQueryTransactionWrapper {

		enum TargetDb {
			SORMAS,
			AUDIT
		}

		@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
		private EntityManager em;
		@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME_AUDITLOG)
		private EntityManager emAudit;

		/**
		 * Executes the passed SQL update in a new JTA transaction.
		 */
		@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
		public int executeUpdate(TargetDb db, String sqlStatement) {
			switch (db) {
			case SORMAS:
				return em.createNativeQuery(sqlStatement).executeUpdate();
			case AUDIT:
				return emAudit.createNativeQuery(sqlStatement).executeUpdate();
			default:
				throw new IllegalStateException("Unexpected value: " + db);
			}
		}
	}
}
