package de.symeda.sormas.backend.common;

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
		if (regionService.getAll().isEmpty()) {
	    	List<Region> regions = MockDataGenerator.createRegions();
	    	
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
	}

	@PreDestroy
	public void shutdown() {

	}

}