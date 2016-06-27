package de.symeda.sormas.backend.common;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.user.Permission;
import de.symeda.sormas.backend.util.MockDataGenerator;

@Singleton(name = "StartupShutdownService")
@Startup
@RunAs(Permission._SYSTEM_ROLE)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class StartupShutdownService {

	@EJB
	private CaseService caseService;
	@EJB
	private PersonService personService;
	@EJB
	private RegionService regionService;
	@EJB
	private FacilityService facilityService;
	
	@PostConstruct
	public void startup() {
		initCaseMockData();
		initRegionMockData();
	}

	private void initCaseMockData() {
		if (caseService.getAll().isEmpty()) {
	    	List<Case> cases = MockDataGenerator.createCases();
	    	
			for (Case caze : cases) {
				Person person = MockDataGenerator.createPerson();
				personService.persist(person);

				caze.setPerson(person);
				caseService.persist(caze);
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
				for (District district : region.getDistricts()) {
					for (Community community : district.getCommunities()) {
						regionService.persist(community);
					}
					regionService.persist(district);
				}
				regionService.persist(region);
			}
		}
		
		if (facilityService.getAll().isEmpty()) {
			for (Region region : regions) {
				List<Facility> facilities = MockDataGenerator.importFacilities(region);
				for (Facility facility : facilities) {
					facilityService.persist(facility);
				}
			}
		}
	}

	@PreDestroy
	public void shutdown() {

	}

}