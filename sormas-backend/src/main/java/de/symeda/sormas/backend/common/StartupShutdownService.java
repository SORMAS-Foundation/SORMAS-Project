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

import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.user.UserHelper;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.facility.FacilityService;
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
import de.symeda.sormas.backend.util.InfrastructureDataImporter;
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
		importRegionAndFacilityData();
		initLaboratoriesMockData();
		initUserMockData();
	}

	private void initUserMockData() {
		if (userService.getAll().isEmpty()) {
			
			Region region = regionService.getAll().get(0);

			User admin = MockDataGenerator.createUser(UserRole.ADMIN, "ad", "min", "sadmin");
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
		}
		
		User supervisor = userService.getByUserName(UserHelper.getSuggestedUsername("Sunkanmi", "Sesay"));
		if (!supervisor.getUserRoles().contains(UserRole.CONTACT_SUPERVISOR)
				|| !supervisor.getUserRoles().contains(UserRole.CASE_SUPERVISOR)) {
			supervisor.getUserRoles().add(UserRole.CONTACT_SUPERVISOR);
			supervisor.getUserRoles().add(UserRole.CASE_SUPERVISOR);
		}
	}

	private void importRegionAndFacilityData() {
		List<Region> regions = regionService.getAll();
		if (regions.isEmpty()) {
	    	regions = Arrays.asList(
	    			InfrastructureDataImporter.importRegion("Kano")
	    			);
	    	
			for (Region region : regions) {
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
				List<Facility> facilities = InfrastructureDataImporter.importFacilities(region);
				for (Facility facility : facilities) {
					if (facility.getLocation().getDistrict() == null) {
						throw new NullPointerException("Facility should have a district defined: " + facility.getName());
					}
					facilityService.persist(facility);
				}
			}
		}
	}
	
	private void initLaboratoriesMockData() {
		
		if (facilityService.getAllByFacilityType(FacilityType.LABORATORY).isEmpty()) {
			List<Region> regions = regionService.getAll();
			List<Facility> labs = InfrastructureDataImporter.importLaboratories(regions);
			for (Facility lab : labs) {
				facilityService.persist(lab);
			}
		}
	}

	@PreDestroy
	public void shutdown() {

	}
}