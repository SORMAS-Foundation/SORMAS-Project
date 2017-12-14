package de.symeda.sormas.backend.common;

import java.sql.Timestamp;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.user.UserHelper;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.epidata.EpiData;
import de.symeda.sormas.backend.epidata.EpiDataService;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.hospitalization.Hospitalization;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.symptoms.SymptomsService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.MockDataGenerator;

@Singleton(name = "StartupShutdownService")
@Startup
@RunAs(UserRole._SYSTEM)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class StartupShutdownService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private ConfigService configService;
	@EJB
	private UserService userService;
	@EJB
	private CaseService caseService;
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

	@PostConstruct
	public void startup() {
		
		String countryName = configService.getCountryName();
		
		importAdministrativeDivisions(countryName);
		
		facilityService.importFacilities(countryName);

		initUserMockData();
		
		// TODO enable/disable via config?
		fixMissingEntities();
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


	private void initUserMockData() {
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
			surveillanceSupervisor.setRegion(region);
			userService.persist(surveillanceSupervisor);
	
			User surveillanceOfficer = MockDataGenerator.createUser(UserRole.SURVEILLANCE_OFFICER, "Sanaa", "Obasanjo",
					"Sanaa");
			surveillanceOfficer.setRegion(region);
			surveillanceOfficer.setDistrict(district);
			userService.persist(surveillanceOfficer);
	
			User informant = MockDataGenerator.createUser(UserRole.INFORMANT, "Sangodele", "Ibori", "Sango");
			informant.setRegion(region);
			informant.setDistrict(district);
			informant.setHealthFacility(facility);
			informant.setAssociatedOfficer(surveillanceOfficer);
			userService.persist(informant);
		}
	
		User supervisor = userService.getByUserName(UserHelper.getSuggestedUsername("Sunkanmi", "Sesay"));
		if (!supervisor.getUserRoles().contains(UserRole.CONTACT_SUPERVISOR)
				|| !supervisor.getUserRoles().contains(UserRole.CASE_SUPERVISOR)) {
			supervisor.getUserRoles().add(UserRole.CONTACT_SUPERVISOR);
			supervisor.getUserRoles().add(UserRole.CASE_SUPERVISOR);
		}
	}

	private void fixMissingEntities() {
		
		// don't do this when heuristically everything looks fine
		if (caseService.count() == epiDataService.count()) {
			return;
		}
		
		List<Case> cases = caseService.getAll();
		for (Case caze : cases) {
			Person person = caze.getPerson();
			if (person.getAddress().getId() == null) 
				person.setAddress(new Location());
			if (caze.getSymptoms().getId() == null)
				caze.setSymptoms(new Symptoms());
			if (caze.getHospitalization().getId() == null)
				caze.setHospitalization(new Hospitalization());
			if (caze.getEpiData().getId() == null)
				caze.setEpiData(new EpiData());
			caseService.persist(caze);
		}
	}

	@PreDestroy
	public void shutdown() {

	}
}