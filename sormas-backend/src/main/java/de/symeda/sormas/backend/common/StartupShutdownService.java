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
		importEpidCodes();
		initLaboratoriesMockData();
		initUserMockData();
	}

	private void initUserMockData() {
		if (userService.getAll().isEmpty()) {

			Region region = regionService.getAll().get(0);
			District district = region.getDistricts().get(0);
			Community community = district.getCommunities().get(0);
			Facility facility = facilityService.getHealthFacilitiesByCommunity(community, false).get(0);

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

	private void importRegionAndFacilityData() {

		List<Region> regions = regionService.getAll();

		// TODO just go through all files in directory

		if (!regions.stream().anyMatch(r -> "Abia".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Abia"));
		}
		if (!regions.stream().anyMatch(r -> "Adamawa".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Adamawa"));
		}
		if (!regions.stream().anyMatch(r -> "Akwa-Ibom".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Akwa-Ibom"));
		}
		if (!regions.stream().anyMatch(r -> "Anambra".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Anambra"));
		}
		if (!regions.stream().anyMatch(r -> "Bauchi".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Bauchi"));
		}
		if (!regions.stream().anyMatch(r -> "Bayelsa".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Bayelsa"));
		}
		if (!regions.stream().anyMatch(r -> "Benue".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Benue"));
		}
		if (!regions.stream().anyMatch(r -> "Borno".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Borno"));
		}
		if (!regions.stream().anyMatch(r -> "Cross River".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Cross River"));
		}
		if (!regions.stream().anyMatch(r -> "Delta".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Delta"));
		}
		if (!regions.stream().anyMatch(r -> "Ebonyi".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Ebonyi"));
		}
		if (!regions.stream().anyMatch(r -> "Edo".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Edo"));
		}
		if (!regions.stream().anyMatch(r -> "Ekiti".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Ekiti"));
		}
		if (!regions.stream().anyMatch(r -> "Enugu".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Enugu"));
		}
		if (!regions.stream().anyMatch(r -> "FCT".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("FCT"));
		}
		if (!regions.stream().anyMatch(r -> "Gombe".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Gombe"));
		}
		if (!regions.stream().anyMatch(r -> "Imo".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Imo"));
		}
		if (!regions.stream().anyMatch(r -> "Jigawa".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Jigawa"));
		}
		if (!regions.stream().anyMatch(r -> "Kaduna".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Kaduna"));
		}
		if (!regions.stream().anyMatch(r -> "Kano".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Kano"));
		}
		if (!regions.stream().anyMatch(r -> "Katsina".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Katsina"));
		}
		if (!regions.stream().anyMatch(r -> "Kebbi".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Kebbi"));
		}
		if (!regions.stream().anyMatch(r -> "Kogi".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Kogi"));
		}
		if (!regions.stream().anyMatch(r -> "Kwara".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Kwara"));
		}
		if (!regions.stream().anyMatch(r -> "Lagos".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Lagos"));
		}
		if (!regions.stream().anyMatch(r -> "Nasarawa".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Nasarawa"));
		}
		if (!regions.stream().anyMatch(r -> "Niger".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Niger"));
		}
		if (!regions.stream().anyMatch(r -> "Ogun".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Ogun"));
		}
		if (!regions.stream().anyMatch(r -> "Ondo".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Ondo"));
		}
		if (!regions.stream().anyMatch(r -> "Osun".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Osun"));
		}
		if (!regions.stream().anyMatch(r -> "Oyo".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Oyo"));
		}
		if (!regions.stream().anyMatch(r -> "Plateau".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Plateau"));
		}
		if (!regions.stream().anyMatch(r -> "Rivers".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Rivers"));
		}
		if (!regions.stream().anyMatch(r -> "Sokoto".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Sokoto"));
		}
		if (!regions.stream().anyMatch(r -> "Taraba".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Taraba"));
		}
		if (!regions.stream().anyMatch(r -> "Yobe".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Yobe"));
		}
		if (!regions.stream().anyMatch(r -> "Zamfara".equals(r.getName()))) {
			importDataForRegion(InfrastructureDataImporter.importRegion("Zamfara"));
		}

		if (facilityService.getByUuid(facilityService.getOtherFacilityUuid()) == null) {
			// Add 'Other' health facility with a constant UUID that is not
			// associated with a specific region
			Facility otherFacility = new Facility();
			otherFacility.setName("Other");
			otherFacility.setUuid(facilityService.getOtherFacilityUuid());
			facilityService.persist(otherFacility);
		}
	}

	private void importEpidCodes() {
		List<Region> regions = regionService.getAllWithoutEpidCode();
		List<District> districts = districtService.getAllWithoutEpidCode();
		
		// Import the EPID codes
		InfrastructureDataImporter.importEpidCodes(regions, districts);
		
		// Refresh the updated database instances
		for (Region region : regions) {
			regionService.ensurePersisted(region);
		}
		
		for (District district : districts) {
			districtService.ensurePersisted(district);
		}
	}

	private void importDataForRegion(Region region) {
		for (District district : region.getDistricts()) {
			for (Community community : district.getCommunities()) {
				communityService.persist(community);
			}
			districtService.persist(district);
		}
		regionService.persist(region);

		List<Facility> facilities = InfrastructureDataImporter.importFacilities(region);
		for (Facility facility : facilities) {
			if (facility.getDistrict() == null) {
				throw new NullPointerException("Facility should have a district defined: " + facility.getName());
			}
			facilityService.persist(facility);
		}
	}

	private void initLaboratoriesMockData() {

		if (facilityService.getAllLaboratories().isEmpty()) {
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