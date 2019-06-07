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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.disease.DiseaseConfiguration;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.disease.DiseaseConfigurationService;
import de.symeda.sormas.backend.epidata.EpiDataService;
import de.symeda.sormas.backend.event.EventParticipantService;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.importexport.ImportFacadeEjb.ImportFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.symptoms.SymptomsService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.MockDataGenerator;
import de.symeda.sormas.backend.util.ModelConstants;

@Singleton(name = "StartupShutdownService")
@Startup
@RunAs(UserRole._SYSTEM)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class StartupShutdownService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;
	@EJB
	private ConfigFacadeEjbLocal configFacade;
	@EJB
	private UserService userService;
	@EJB
	private CaseService caseService;
	@EJB
	private ContactService contactService;
	@EJB
	private EventParticipantService eventParticipantService;
	@EJB
	private EpiDataService epiDataService;
	@EJB
	private SymptomsService symptomsService;
	@EJB
	private PersonService personService;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private CommunityService communityService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private ImportFacadeEjbLocal importFacade;
	@EJB
	private DiseaseConfigurationFacadeEjbLocal diseaseConfigurationFacade;
	@EJB
	private DiseaseConfigurationService diseaseConfigurationService;

	@PostConstruct
	public void startup() {
		updateDatabase();
		
		String countryName = configFacade.getCountryName();
		
		I18nProperties.setLocale(configFacade.getCountryLocale());
		
		importAdministrativeDivisions(countryName);
		
		facilityService.importFacilities(countryName);

		initDefaultUsers();
		
		upgrade();
		
		createImportTemplateFiles();
		
		createMissingDiseaseConfigurations();
		
		configFacade.validateAppUrls();
	}
	
	public void importAdministrativeDivisions(String countryName) {
		Timestamp latestRegionChangeDate = regionService.getLatestChangeDate();
		if (latestRegionChangeDate != null
				// last change made to district data for nigeria
				// TODO replace with solution that reads the change date from the file or something else
				&& latestRegionChangeDate.after(DateHelper.getDateZero(2017, 11, 22))) {
			return;
		}		

		List<Region> regions = regionService.getAll();
		
		regionService.importRegions(countryName, regions);
		
		districtService.importDistricts(countryName, regions);

		communityService.importCommunities(countryName, regions);		
	}


	private void initDefaultUsers() {
		if (userService.getAll().isEmpty()) {
	
			Region region = regionService.getAll().get(0);
			District district = region.getDistricts().get(0);
			Community community = district.getCommunities().get(0);
			List<Facility> healthFacilities = facilityService.getHealthFacilitiesByCommunity(community, false);
			Facility facility = healthFacilities.size() > 0 ? healthFacilities.get(0) : null;
	
			User admin = MockDataGenerator.createUser(UserRole.ADMIN, "ad", "min", "sadmin");
			userService.persist(admin);
	
			User surveillanceSupervisor = MockDataGenerator.createUser(UserRole.SURVEILLANCE_SUPERVISOR, "Sunkanmi",
					"Sesay", "Sunkanmi");
			surveillanceSupervisor.getUserRoles().add(UserRole.CONTACT_SUPERVISOR);
			surveillanceSupervisor.getUserRoles().add(UserRole.CASE_SUPERVISOR);
			surveillanceSupervisor.setRegion(region);
			userService.persist(surveillanceSupervisor);
	
			User surveillanceOfficer = MockDataGenerator.createUser(UserRole.SURVEILLANCE_OFFICER, "Sanaa", "Obasanjo",
					"Sanaa");
			surveillanceOfficer.setRegion(region);
			surveillanceOfficer.setDistrict(district);
			userService.persist(surveillanceOfficer);
	
			User informant = MockDataGenerator.createUser(UserRole.HOSPITAL_INFORMANT, "Sangodele", "Ibori", "Sango");
			informant.setRegion(region);
			informant.setDistrict(district);
			informant.setHealthFacility(facility);
			informant.setAssociatedOfficer(surveillanceOfficer);
			userService.persist(informant);
		}
	}
	
	private void updateDatabase() {
		logger.info("Starting automatic database update.");
		
		Integer currentVersion = (Integer) em.createNativeQuery("SELECT version_number FROM schema_version ORDER BY changedate DESC LIMIT 1").getSingleResult();		
		File schemaFile = new File(getClass().getClassLoader().getResource("sormas_schema.sql").getFile());
		Scanner scanner = null;
		
		try {
			scanner = new Scanner(schemaFile);
			StringBuilder nextUpdateBuilder = new StringBuilder();
			boolean currentVersionReached = currentVersion == null;
			
			while (scanner.hasNextLine()) {
				String nextLine = scanner.nextLine();
				
				if (nextLine.isEmpty()) {
					continue;
				}
				
				if (!currentVersionReached) {
					if (nextLine.contains("INSERT INTO schema_version (version_number, comment) VALUES (" + currentVersion)) {
						currentVersionReached = true;
					}

					continue;
				}
				
				// Add the line to the StringBuilder
				nextUpdateBuilder.append(nextLine).append("\n");
				
				// Perform the current update when the INSERT INTO schema_version statement is reached
				if (nextLine.contains("INSERT INTO schema_version")) {
					String newVersion = nextLine.substring(61, nextLine.indexOf(",", 61));
					logger.info("Updating database to version " + newVersion + "...");
					em.createNativeQuery(nextUpdateBuilder.toString()).executeUpdate();
				}
			}
		} catch (FileNotFoundException e) {
			logger.error("Could not find sormas_schema.sql file. Database update not performed.");
			throw new RuntimeException(e);
		} finally {
			scanner.close();
			logger.info("Database update completed.");
		}
	}

	private void upgrade() {
		@SuppressWarnings("unchecked")
		List<Integer> versionsNeedingUpgrade = em
				.createNativeQuery("SELECT version_number FROM schema_version WHERE upgradeNeeded")
				.getResultList();
		
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
			
			int updatedRows = em
				.createNativeQuery("UPDATE schema_version SET upgradeNeeded=false WHERE version_number=" + versionNeedingUpgrade)
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