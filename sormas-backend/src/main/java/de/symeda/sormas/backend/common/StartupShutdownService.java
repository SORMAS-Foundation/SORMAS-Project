/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.externaljournal.PatientDiaryConfig;
import de.symeda.sormas.api.externaljournal.SymptomJournalConfig;
import de.symeda.sormas.api.externaljournal.UserConfig;
import de.symeda.sormas.api.facility.FacilityCriteria;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.PointOfEntryType;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.PasswordHelper;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.disease.DiseaseConfiguration;
import de.symeda.sormas.backend.disease.DiseaseConfigurationService;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb.FacilityFacadeEjbLocal;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.feature.FeatureConfigurationService;
import de.symeda.sormas.backend.importexport.ImportFacadeEjb.ImportFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.PointOfEntryService;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.sormastosormas.ServerAccessDataService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.user.event.PasswordResetEvent;
import de.symeda.sormas.backend.user.event.UserUpdateEvent;
import de.symeda.sormas.backend.util.MockDataGenerator;
import de.symeda.sormas.backend.util.ModelConstants;

@Singleton(name = "StartupShutdownService")
@Startup
@RunAs(UserRole._SYSTEM)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class StartupShutdownService {

	static final String SORMAS_SCHEMA = "sql/sormas_schema.sql";

	static final String AUDIT_SCHEMA = "sql/sormas_audit_schema.sql";

	private static final Pattern SQL_COMMENT_PATTERN = Pattern.compile("^\\s*(--.*)?");

	public static final String SORMAS_TO_SORMAS_USER_NAME = "Sormas2Sormas";

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
	private ServerAccessDataService serverAccessDataService;

	@Inject
	private Event<UserUpdateEvent> userUpdateEvent;

	@Inject
	private Event<PasswordResetEvent> passwordResetEvent;

	@PostConstruct
	public void startup() {

		checkDatabaseConfig(em);

		logger.info("Initiating automatic database update of main database...");
		updateDatabase(em, SORMAS_SCHEMA);

		logger.info("Initiating automatic database update of audit database...");
		updateDatabase(emAudit, AUDIT_SCHEMA);

		I18nProperties.setDefaultLanguage(Language.fromLocaleString(configFacade.getCountryLocale()));

		createDefaultInfrastructureData();

		facilityService.createConstantFacilities();

		pointOfEntryService.createConstantPointsOfEntry();

		createDefaultUsers();

		createOrUpdateSormasToSormasUser();

		createOrUpdateSymptomJournalUser();

		createOrUpdatePatientDiaryUser();

		upgrade();

		createImportTemplateFiles();

		createMissingDiseaseConfigurations();

		featureConfigurationService.createMissingFeatureConfigurations();
		featureConfigurationService.updateFeatureConfigurations();

		configFacade.validateAppUrls();
		configFacade.validateExternalUrls();
	}

	private void createDefaultInfrastructureData() {

		// Region
		Region region = null;
		if (regionService.count() == 0) {
			region = new Region();
			region.setUuid(DataHelper.createUuid());
			region.setName(I18nProperties.getCaption(Captions.defaultRegion, "Default Region"));
			region.setEpidCode("DEF-REG");
			region.setDistricts(new ArrayList<District>());
			regionService.ensurePersisted(region);
		}

		// District
		District district = null;
		if (districtService.count() == 0) {
			district = new District();
			district.setUuid(DataHelper.createUuid());
			district.setName(I18nProperties.getCaption(Captions.defaultDistrict, "Default District"));
			if (region == null) {
				region = regionService.getAll().get(0);
			}
			district.setRegion(region);
			district.setEpidCode("DIS");
			district.setCommunities(new ArrayList<Community>());
			districtService.ensurePersisted(district);
			region.getDistricts().add(district);
		}

		// Community
		Community community = null;
		if (communityService.count() == 0) {
			community = new Community();
			community.setUuid(DataHelper.createUuid());
			community.setName(I18nProperties.getCaption(Captions.defaultCommunity, "Default Community"));
			if (district == null) {
				district = districtService.getAll().get(0);
			}
			community.setDistrict(district);
			communityService.ensurePersisted(community);
			district.getCommunities().add(community);
		}

		// Facility
		Facility facility;
		FacilityCriteria facilityCriteria = new FacilityCriteria();
		if (facilityFacade.count(facilityCriteria) == 0) {
			facility = new Facility();
			facility.setUuid(DataHelper.createUuid());
			facility.setType(FacilityType.HOSPITAL);
			facility.setName(I18nProperties.getCaption(Captions.defaultFacility, "Default Health Facility"));
			if (community == null) {
				community = communityService.getAll().get(0);
			}
			facility.setCommunity(community);
			if (district == null) {
				district = districtService.getAll().get(0);
			}
			facility.setDistrict(district);
			if (region == null) {
				region = regionService.getAll().get(0);
			}
			facility.setRegion(region);
			facilityService.ensurePersisted(facility);
		}

		// Laboratory
		Facility laboratory;
		facilityCriteria.type(FacilityType.LABORATORY);
		if (facilityFacade.count(facilityCriteria) == 0) {
			laboratory = new Facility();
			laboratory.setUuid(DataHelper.createUuid());
			laboratory.setName(I18nProperties.getCaption(Captions.defaultLaboratory, "Default Laboratory"));
			if (community == null) {
				community = communityService.getAll().get(0);
			}
			laboratory.setCommunity(community);
			if (district == null) {
				district = districtService.getAll().get(0);
			}
			laboratory.setDistrict(district);
			if (region == null) {
				region = regionService.getAll().get(0);
			}
			laboratory.setRegion(region);
			laboratory.setType(FacilityType.LABORATORY);
			facilityService.ensurePersisted(laboratory);
		}

		// Point of Entry
		PointOfEntry pointOfEntry;
		if (pointOfEntryService.count() == 0) {
			pointOfEntry = new PointOfEntry();
			pointOfEntry.setUuid(DataHelper.createUuid());
			pointOfEntry.setName(I18nProperties.getCaption(Captions.defaultPointOfEntry, "Default Point Of Entry"));
			if (district == null) {
				district = districtService.getAll().get(0);
			}
			pointOfEntry.setDistrict(district);
			if (region == null) {
				region = regionService.getAll().get(0);
			}
			pointOfEntry.setRegion(region);
			pointOfEntry.setPointOfEntryType(PointOfEntryType.AIRPORT);
			pointOfEntryService.ensurePersisted(pointOfEntry);
		}
	}

	private void createDefaultUsers() {

		if (userService.count() == 0) {

			// Create Admin
			User admin = MockDataGenerator.createUser(UserRole.ADMIN, "ad", "min", "sadmin");
			admin.setUserName("admin");
			userService.persist(admin);
			userUpdateEvent.fire(new UserUpdateEvent(admin));

			if (!configFacade.isCreateDefaultUsers()) {
				// return if getCreateDefaultUsers() is false
				logger.info("Skipping the creation of default users");
				return;
			}

			Region region = regionService.getAll().get(0);
			District district = region.getDistricts().get(0);
			Community community = district.getCommunities().get(0);
			List<Facility> healthFacilities = facilityService.getActiveFacilitiesByCommunityAndType(community, FacilityType.HOSPITAL, false, false);
			Facility facility = healthFacilities.size() > 0 ? healthFacilities.get(0) : null;
			List<Facility> laboratories = facilityService.getAllActiveLaboratories(false);
			Facility laboratory = laboratories.size() > 0 ? laboratories.get(0) : null;
			PointOfEntry pointOfEntry = pointOfEntryService.getAllActive().get(0);

			logger.info("Create default users");

			// Create Surveillance Supervisor
			User surveillanceSupervisor = MockDataGenerator.createUser(UserRole.SURVEILLANCE_SUPERVISOR, "Surveillance", "Supervisor", "SurvSup");
			surveillanceSupervisor.setUserName("SurvSup");
			surveillanceSupervisor.setRegion(region);
			userService.persist(surveillanceSupervisor);
			userUpdateEvent.fire(new UserUpdateEvent(surveillanceSupervisor));

			// Create Case Supervisor
			User caseSupervisor = MockDataGenerator.createUser(UserRole.CASE_SUPERVISOR, "Case", "Supervisor", "CaseSup");
			caseSupervisor.setUserName("CaseSup");
			caseSupervisor.setRegion(region);
			userService.persist(caseSupervisor);
			userUpdateEvent.fire(new UserUpdateEvent(caseSupervisor));

			// Create Contact Supervisor
			User contactSupervisor = MockDataGenerator.createUser(UserRole.CONTACT_SUPERVISOR, "Contact", "Supervisor", "ContSup");
			contactSupervisor.setUserName("ContSup");
			contactSupervisor.setRegion(region);
			userService.persist(contactSupervisor);
			userUpdateEvent.fire(new UserUpdateEvent(contactSupervisor));

			// Create Point of Entry Supervisor
			User poeSupervisor = MockDataGenerator.createUser(UserRole.POE_SUPERVISOR, "Point of Entry", "Supervisor", "PoeSup");
			poeSupervisor.setUserName("PoeSup");
			poeSupervisor.setRegion(region);
			userService.persist(poeSupervisor);
			userUpdateEvent.fire(new UserUpdateEvent(poeSupervisor));

			// Create Laboratory Officer
			User laboratoryOfficer = MockDataGenerator.createUser(UserRole.LAB_USER, "Laboratory", "Officer", "LabOff");
			laboratoryOfficer.setUserName("LabOff");
			laboratoryOfficer.setLaboratory(laboratory);
			userService.persist(laboratoryOfficer);
			userUpdateEvent.fire(new UserUpdateEvent(laboratoryOfficer));

			// Create Event Officer
			User eventOfficer = MockDataGenerator.createUser(UserRole.EVENT_OFFICER, "Event", "Officer", "EveOff");
			eventOfficer.setUserName("EveOff");
			eventOfficer.setRegion(region);
			userService.persist(eventOfficer);
			userUpdateEvent.fire(new UserUpdateEvent(eventOfficer));

			// Create National User
			User nationalUser = MockDataGenerator.createUser(UserRole.NATIONAL_USER, "National", "User", "NatUser");
			nationalUser.setUserName("NatUser");
			userService.persist(nationalUser);
			userUpdateEvent.fire(new UserUpdateEvent(nationalUser));

			// Create National Clinician
			User nationalClinician = MockDataGenerator.createUser(UserRole.NATIONAL_CLINICIAN, "National", "Clinician", "NatClin");
			nationalClinician.setUserName("NatClin");
			userService.persist(nationalClinician);
			userUpdateEvent.fire(new UserUpdateEvent(nationalClinician));

			// Create Surveillance Officer
			User surveillanceOfficer = MockDataGenerator.createUser(UserRole.SURVEILLANCE_OFFICER, "Surveillance", "Officer", "SurvOff");
			surveillanceOfficer.setUserName("SurvOff");
			surveillanceOfficer.setRegion(region);
			surveillanceOfficer.setDistrict(district);
			userService.persist(surveillanceOfficer);
			userUpdateEvent.fire(new UserUpdateEvent(surveillanceOfficer));

			// Create Hospital Informant
			User hospitalInformant = MockDataGenerator.createUser(UserRole.HOSPITAL_INFORMANT, "Hospital", "Informant", "HospInf");
			hospitalInformant.setUserName("HospInf");
			hospitalInformant.setRegion(region);
			hospitalInformant.setDistrict(district);
			hospitalInformant.setHealthFacility(facility);
			hospitalInformant.setAssociatedOfficer(surveillanceOfficer);
			userService.persist(hospitalInformant);
			userUpdateEvent.fire(new UserUpdateEvent(hospitalInformant));

			User poeInformant = MockDataGenerator.createUser(UserRole.POE_INFORMANT, "Poe", "Informant", "PoeInf");
			poeInformant.setUserName("PoeInf");
			poeInformant.setRegion(region);
			poeInformant.setDistrict(district);
			poeInformant.setPointOfEntry(pointOfEntry);
			poeInformant.setAssociatedOfficer(surveillanceOfficer);
			userService.persist(poeInformant);
			userUpdateEvent.fire(new UserUpdateEvent(poeInformant));
		}
	}

	private void createOrUpdateSormasToSormasUser() {
		serverAccessDataService.getServerAccessData().ifPresent((serverAccessData -> {
			String sormasToSormasUserPassword = serverAccessData.getRestUserPassword();
			createOrUpdateDefaultUser(
				Collections.singleton(UserRole.SORMAS_TO_SORMAS_CLIENT),
				SORMAS_TO_SORMAS_USER_NAME,
				sormasToSormasUserPassword,
				"Sormas to Sormas",
				"Client");
		}));
	}

	private void createOrUpdateSymptomJournalUser() {
		SymptomJournalConfig symptomJournalConfig = configFacade.getSymptomJournalConfig();
		UserConfig userConfig = symptomJournalConfig.getDefaultUser();
		if (userConfig == null) {
			logger.debug("Symptom journal default user not configured");
			return;
		}

		createOrUpdateDefaultUser(
			Collections.singleton(UserRole.REST_USER),
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
			new HashSet<>(Arrays.asList(UserRole.REST_USER, UserRole.REST_EXTERNAL_VISITS_USER)),
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

		if (existingUser == null) {
			if (!DataHelper.isNullOrEmpty(password)) {
				User newUser = MockDataGenerator.createUser(userRoles, firstName, lastName, password);
				newUser.setUserName(username);

				userService.persist(newUser);
				userUpdateEvent.fire(new UserUpdateEvent(newUser));
			}
		} else if (!DataHelper.equal(existingUser.getPassword(), PasswordHelper.encodePassword(password, existingUser.getSeed()))) {
			existingUser.setSeed(PasswordHelper.createPass(16));
			existingUser.setPassword(PasswordHelper.encodePassword(password, existingUser.getSeed()));

			userService.persist(existingUser);
			passwordResetEvent.fire(new PasswordResetEvent(existingUser));
		}

	}

	/**
	 * Checks if the PostgreSQL server is configured correctly to run SORMAS.
	 */
	private void checkDatabaseConfig(EntityManager entityManager) {

		List<String> errors = new ArrayList<>();

		// Check postgres version
		String versionRegexp = Stream.of("9\\.5", "9\\.6", "10\\.\\d+").collect(Collectors.joining(")|(", "(", ")"));
		String versionString = entityManager.createNativeQuery("SHOW server_version").getSingleResult().toString();
		if (!versionString.matches(versionRegexp)) {
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

	private void updateDatabase(EntityManager entityManager, String schemaFileName) {

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
					entityManager.createNativeQuery(nextUpdateBuilder.toString()).executeUpdate();
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
					contactService.updateFollowUpUntilAndStatus(contact);
					contactService.udpateContactStatus(contact);
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

	private void createImportTemplateFiles() {

		try {
			importFacade.generateCaseImportTemplateFile();
		} catch (IOException e) {
			logger.error("Could not create case import template .csv file.");
		}

		try {
			importFacade.generateCaseContactImportTemplateFile();
		} catch (IOException e) {
			logger.error("Could not create case contact import template .csv file.");
		}

		try {
			importFacade.generateContactImportTemplateFile();
		} catch (IOException e) {
			logger.error("Could not create contact import template .csv file.");
		}

		try {
			importFacade.generateCaseLineListingImportTemplateFile();
		} catch (IOException e) {
			logger.error("Could not create line listing import template .csv file.");
		}

		try {
			importFacade.generatePointOfEntryImportTemplateFile();
		} catch (IOException e) {
			logger.error("Could not create point of entry import template .csv file.");
		}

		try {
			importFacade.generatePopulationDataImportTemplateFile();
		} catch (IOException e) {
			logger.error("Could not create population data import template .csv file.");
		}

		try {
			importFacade.generateAreaImportTemplateFile();
		} catch (IOException e) {
			logger.error("Could not create area import template .csv file.");
		}

		try {
			importFacade.generateCountryImportTemplateFile();
		} catch (IOException e) {
			logger.error("Could not create country import template .csv file.");
		}
		try {
			importFacade.generateRegionImportTemplateFile();
		} catch (IOException e) {
			logger.error("Could not create region import template .csv file.");
		}
		try {
			importFacade.generateDistrictImportTemplateFile();
		} catch (IOException e) {
			logger.error("Could not create district import template .csv file.");
		}
		try {
			importFacade.generateCommunityImportTemplateFile();
		} catch (IOException e) {
			logger.error("Could not create community import template .csv file.");
		}
		try {
			importFacade.generateFacilityImportTemplateFile();
		} catch (IOException e) {
			logger.error("Could not create facility/laboratory import template .csv file.");
		}
		try {
			importFacade.generateEventParticipantImportTemplateFile();
		} catch (IOException e) {
			logger.error("Could not create eventparticipant import template .csv file.");
		}
	}

	private void createMissingDiseaseConfigurations() {

		List<DiseaseConfiguration> diseaseConfigurations = diseaseConfigurationService.getAll();
		List<Disease> configuredDiseases = diseaseConfigurations.stream().map(c -> c.getDisease()).collect(Collectors.toList());
		Arrays.stream(Disease.values()).filter(d -> !configuredDiseases.contains(d)).forEach(d -> {
			DiseaseConfiguration configuration = DiseaseConfiguration.build(d);
			diseaseConfigurationService.ensurePersisted(configuration);
		});
	}

	@PreDestroy
	public void shutdown() {

	}
}
