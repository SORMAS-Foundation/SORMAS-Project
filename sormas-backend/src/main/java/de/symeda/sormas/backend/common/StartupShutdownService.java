package de.symeda.sormas.backend.common;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.user.Permission;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.MockDataGenerator;

@Singleton(name = "StartupShutdownService")
@Startup
@RunAs(Permission._SYSTEM_ROLE)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class StartupShutdownService {

	@EJB
	private UserService userService;
	@EJB
	private CaseService caseService;
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
		initRegionMockData();
		initUserMockData();
		//initCaseAndPersonMockData();
	}

	private void initUserMockData() {
		if (userService.getAll().isEmpty()) {
			
			Region region = regionService.getAll().get(0);

			User admin = MockDataGenerator.createUser(null, "ad", "min", "");
			userService.persist(admin);

			User surveillanceSupervisor = MockDataGenerator.createUser(UserRole.SURVEILLANCE_SUPERVISOR, "Sunkanmi", "Sesay", "Sunkanmi");
			surveillanceSupervisor.setRegion(region);
			userService.persist(surveillanceSupervisor);

			User surveillanceOfficer = MockDataGenerator.createUser(UserRole.SURVEILLANCE_OFFICER, "Sanaa", "Obasanjo", "Sanaa");
			surveillanceOfficer.setRegion(region);
			userService.persist(surveillanceOfficer);

			User informant = MockDataGenerator.createUser(UserRole.INFORMANT, "Sangodele", "Ibori", "Sango");
			informant.setRegion(region);
			informant.setAssociatedOfficer(surveillanceOfficer);
			userService.persist(informant);

			
//			userService.persist(MockDataGenerator.createUser(UserRole.SURVEILLANCE_OFFICER));
//			userService.persist(MockDataGenerator.createUser(UserRole.SURVEILLANCE_OFFICER));
//			userService.persist(MockDataGenerator.createUser(UserRole.SURVEILLANCE_OFFICER));
//			userService.persist(MockDataGenerator.createUser(UserRole.SURVEILLANCE_OFFICER));
//			userService.persist(MockDataGenerator.createUser(UserRole.SURVEILLANCE_OFFICER));
//			userService.persist(MockDataGenerator.createUser(UserRole.INFORMANT));
//			userService.persist(MockDataGenerator.createUser(UserRole.INFORMANT));
//			userService.persist(MockDataGenerator.createUser(UserRole.INFORMANT));
//			userService.persist(MockDataGenerator.createUser(UserRole.INFORMANT));
//			userService.persist(MockDataGenerator.createUser(UserRole.INFORMANT));
		}
	}

	private void initCaseAndPersonMockData() {
		
		Region region = regionService.getAll().get(0);
		
		if (caseService.getAll().isEmpty()) {
			Random random = new Random();
			List<User> informants = userService.getAllByRegionAndUserRoles(null, UserRole.INFORMANT);
			List<User> survOffs = userService.getAllByRegionAndUserRoles(null, UserRole.SURVEILLANCE_OFFICER);
			List<User> survSups = userService.getAllByRegionAndUserRoles(null, UserRole.SURVEILLANCE_SUPERVISOR);
			List<Facility> facilities = facilityService.getAll();
			
			List<Case> cases = MockDataGenerator.createCases();
	    	
			for (Case caze : cases) {
				
				caze.setReportingUser(informants.get(random.nextInt(informants.size())));
				
				caze.setRegion(region);
				caze.setSurveillanceOfficer(survOffs.get(random.nextInt(survOffs.size())));
				caze.setHealthFacility(facilities.get(random.nextInt(facilities.size())));

				Person person = MockDataGenerator.createPerson();
				personService.persist(person);

				caze.setPerson(person);
				caseService.persist(caze);
			}
			
			for (int i=0; i<5; i++) {
				// some dummy persons
				Person person = MockDataGenerator.createPerson();
				personService.persist(person);
			}
		}
	}

	private void initRegionMockData() {
		List<Region> regions = regionService.getAll();
		if (regions.isEmpty()) {
	    	regions = Arrays.asList(
	    			MockDataGenerator.importRegion("Abia")
	    			);
	    	
			for (Region region : regions) {
				// limit the test data to 5 districts
				while (region.getDistricts().size() > 5) {
					region.getDistricts().remove(5);
				}
				for (District district : region.getDistricts()) {
					for (Community community : district.getCommunities()) {
						communityService.persist(community);
					}
					districtService.persist(district);
				}
				regionService.persist(region);
			}
		}
		
		if (facilityService.getAll().isEmpty()) {
			for (Region region : regions) {
				List<Facility> facilities = MockDataGenerator.importFacilities(region);
				for (Facility facility : facilities) {
					// only save facilities whose districts exist
					if (facility.getLocation().getDistrict() != null) {
						facilityService.persist(facility);
					}
				}
			}
		}
	}

	@PreDestroy
	public void shutdown() {

	}
}