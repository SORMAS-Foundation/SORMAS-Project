package de.symeda.sormas.backend.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.user.UserHelper;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
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
import de.symeda.sormas.backend.util.InfrastructureDataImporter;
import de.symeda.sormas.backend.util.InfrastructureDataImporter.CommunityConsumer;
import de.symeda.sormas.backend.util.InfrastructureDataImporter.DistrictConsumer;
import de.symeda.sormas.backend.util.InfrastructureDataImporter.FacilityConsumer;
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
		
		importFacilities(countryName);

		initUserMockData();
		
		// TODO enable/disable via config?
		fixMissingEntities();
	}

	private void importAdministrativeDivisions(String countryName) {

		if (regionService.count() > 0) {
			return;
		}		

		List<Region> regions = regionService.getAll();
		
		importRegions(countryName, regions);
		
		importDistricts(countryName, regions);

		importCommunities(countryName, regions);		
	}

	private void importRegions(String countryName, List<Region> regions) {
		
		InfrastructureDataImporter.importRegions(countryName, 
			(regionName, epidCode, population, growthRate) -> {
				
				Optional<Region> regionResult = regions.stream()
						.filter(r -> r.getName().equals(regionName))
						.findFirst();
				
				Region region;
				if (regionResult.isPresent()) {
					region = regionResult.get();
				} else {
					region = new Region();
					regions.add(region);
					region.setName(regionName);
				}
				
				region.setEpidCode(epidCode);
				region.setPopulation(population);
				region.setGrowthRate(growthRate);
	
				regionService.persist(region);
			});
	}

	private void importDistricts(String countryName, List<Region> regions) {
		InfrastructureDataImporter.importDistricts(countryName, new DistrictConsumer() {
			
			private Region cachedRegion = null;
			
			@Override
			public void consume(String regionName, String districtName, String epidCode) {
					
					if (cachedRegion == null || !cachedRegion.getName().equals(regionName)) {
						Optional<Region> regionResult = regions.stream()
								.filter(r -> r.getName().equals(regionName))
								.findFirst();
	
						if (regionResult.isPresent()) {
							cachedRegion = regionResult.get();
						} else {
							logger.warn("Could not find region '" + regionName + "' for district '" + districtName + "'");
							return;
						}
						
						if (cachedRegion.getDistricts() == null) {
							cachedRegion.setDistricts(new ArrayList<District>());
						}
					}
					Optional<District> districtResult = cachedRegion.getDistricts().stream()
							.filter(r -> r.getName().equals(districtName))
							.findFirst();
					
					District district;
					if (districtResult.isPresent()) {
						district = districtResult.get();
					} else {
						district = new District();
						cachedRegion.getDistricts().add(district);
						district.setName(districtName);
						district.setRegion(cachedRegion);
					}
					
					district.setEpidCode(epidCode);
	
					districtService.persist(district);
			}
		});
	}

	private void importCommunities(String countryName, List<Region> regions) {
		
		InfrastructureDataImporter.importCommunities(countryName, new CommunityConsumer() {
			
			private Region cachedRegion = null;
			private District cachedDistrict = null;
			
			@Override
			public void consume(String regionName, String districtName, String communityName) {
					
					if (cachedRegion == null || !cachedRegion.getName().equals(regionName)) {
						Optional<Region> regionResult = regions.stream()
								.filter(r -> r.getName().equals(regionName))
								.findFirst();

						if (regionResult.isPresent()) {
							cachedRegion = regionResult.get();
						} else {
							logger.warn("Could not find region '" + regionName + "' for district '" + districtName + "' in community '" + communityName + "'");
							return;
						}
					}
					
					if (cachedDistrict == null || !cachedDistrict.getName().equals(districtName)) {
						Optional<District> districtResult = cachedRegion.getDistricts().stream()
								.filter(r -> r.getName().equals(districtName))
								.findFirst();

						if (districtResult.isPresent()) {
							cachedDistrict = districtResult.get();
						} else {
							logger.warn("Could not find district '" + districtName + "' for community '" + communityName + "'");
							return;
						}
						
						if (cachedDistrict.getCommunities() == null) {
							cachedDistrict.setCommunities(new ArrayList<Community>());
						}
					}

					Optional<Community> communityResult = cachedDistrict.getCommunities().stream()
							.filter(r -> r.getName().equals(communityName))
							.findFirst();
					
					Community community;
					if (communityResult.isPresent()) {
						community = communityResult.get();
					} else {
						community = new Community();
						cachedDistrict.getCommunities().add(community);
						community.setName(communityName);
						community.setDistrict(cachedDistrict);
					}
					
					communityService.persist(community);
			}
		});
	}

	private void importFacilities(String countryName) {
		
		if (facilityService.count() > 0) {
			return;
		}
		
		List<Region> regions = regionService.getAll();
		List<Facility> facilities = facilityService.getAll();
		
		for (Region region : regions) {
			
			InfrastructureDataImporter.importFacilities(countryName, region, new FacilityConsumer() {
				
				private District cachedDistrict = null;
				private Community cachedCommunity = null;				

				@Override
				public void consume(String regionName, String districtName, String communityName, String facilityName,
						String city, String address, Double latitude, Double longitude, FacilityType type,
						boolean publicOwnership) {

					if (cachedDistrict == null || !cachedDistrict.getName().equals(districtName)) {
						Optional<District> districtResult = region.getDistricts().stream()
								.filter(r -> r.getName().equals(districtName))
								.findFirst();

						if (districtResult.isPresent()) {
							cachedDistrict = districtResult.get();
						} else {
							logger.warn("Could not find district '" + districtName + "' for facility '" + facilityName + "'");
							return;
						}
					}
					
					if (cachedCommunity == null || !cachedCommunity.getName().equals(communityName)) {
						Optional<Community> communityResult = cachedDistrict.getCommunities().stream()
								.filter(r -> r.getName().equals(communityName))
								.findFirst();

						if (communityResult.isPresent()) {
							cachedCommunity = communityResult.get();
						} else {
							logger.warn("Could not find community '" + communityName + "' for facility '" + facilityName + "'");
							return;
						}
					}

					Optional<Facility> facilityResult = facilities.stream()
							.filter(r -> r.getName().equals(facilityName)
									&& DataHelper.equal(r.getRegion(), region)
									&& DataHelper.equal(r.getDistrict(), cachedDistrict)
									&& DataHelper.equal(r.getCommunity(), cachedCommunity))
							.findFirst();
					
					Facility facility;
					if (facilityResult.isPresent()) {
						facility = facilityResult.get();
					} else {
						facility = new Facility();
						facility.setName(facilityName);
						facility.setRegion(region);
						facility.setDistrict(cachedDistrict);
						facility.setCommunity(cachedCommunity);
					}
					
					facility.setCity(city);
					facility.setLatitude(latitude);
					facility.setLongitude(longitude);
					facility.setPublicOwnership(publicOwnership);
					facility.setType(type);
					
					facilityService.persist(facility);
				}
			});
		}		

		// Add 'Other' health facility with a constant UUID that is not
		// associated with a specific region
		if (facilityService.getByUuid(FacilityDto.OTHER_FACILITY_UUID) == null) {
			Facility otherFacility = new Facility();
			otherFacility.setName("OTHER_FACILITY");
			otherFacility.setUuid(FacilityDto.OTHER_FACILITY_UUID);
			facilityService.persist(otherFacility);
		}

		// Add 'None' health facility with a constant UUID that is not
		// associated with a specific region
		if (facilityService.getByUuid(FacilityDto.NONE_FACILITY_UUID) == null) {
			Facility noneFacility = new Facility();
			noneFacility.setName("NO_FACILITY");
			noneFacility.setUuid(FacilityDto.NONE_FACILITY_UUID);
			facilityService.persist(noneFacility);
		}
		
		importLaboratories(countryName, regions, facilities);
	}

	private void importLaboratories(String countryName, List<Region> regions, List<Facility> facilities) {

		InfrastructureDataImporter.importLaboratories(countryName, new FacilityConsumer() {
			
			private Region cachedRegion = null;
			private District cachedDistrict = null;
			private Community cachedCommunity = null;				

			@Override
			public void consume(String regionName, String districtName, String communityName, String facilityName,
					String city, String address, Double latitude, Double longitude, FacilityType type,
					boolean publicOwnership) {

				if (DataHelper.isNullOrEmpty(regionName)) {
					cachedRegion = null; // no region is ok
				} else if (cachedRegion == null || !cachedRegion.getName().equals(regionName)) {
					Optional<Region> regionResult = regions.stream()
							.filter(r -> r.getName().equals(regionName))
							.findFirst();

					if (regionResult.isPresent()) {
						cachedRegion = regionResult.get();
					} else {
						logger.warn("Could not find region '" + regionName + "' for facility '" + facilityName + "'");
						cachedRegion = null;
					}
				}	
				
				if (cachedRegion == null || DataHelper.isNullOrEmpty(districtName)) {
					cachedDistrict = null; // no district is ok
				} else if (cachedDistrict == null || !cachedDistrict.getName().equals(districtName)) {
					Optional<District> districtResult = cachedRegion.getDistricts().stream()
							.filter(r -> r.getName().equals(districtName))
							.findFirst();

					if (districtResult.isPresent()) {
						cachedDistrict = districtResult.get();
					} else {
						logger.warn("Could not find district '" + districtName + "' for facility '" + facilityName + "'");
						cachedDistrict = null;
					}
				}
				
				if (cachedDistrict == null || DataHelper.isNullOrEmpty(communityName)) {
					cachedCommunity = null; // no community is ok
				} else if (cachedCommunity == null || !cachedCommunity.getName().equals(communityName)) {
					Optional<Community> communityResult = cachedDistrict.getCommunities().stream()
							.filter(r -> r.getName().equals(communityName))
							.findFirst();

					if (communityResult.isPresent()) {
						cachedCommunity = communityResult.get();
					} else {
						logger.warn("Could not find community '" + communityName + "' for facility '" + facilityName + "'");
						cachedCommunity = null;
					}
				}

				Optional<Facility> facilityResult = facilities.stream()
						.filter(r -> r.getName().equals(facilityName)
								&& DataHelper.equal(r.getRegion(), cachedRegion)
								&& DataHelper.equal(r.getDistrict(), cachedDistrict)
								&& DataHelper.equal(r.getCommunity(), cachedCommunity))
						.findFirst();
				
				Facility facility;
				if (facilityResult.isPresent()) {
					facility = facilityResult.get();
				} else {
					facility = new Facility();
					facility.setName(facilityName);
					facility.setRegion(cachedRegion);
					facility.setDistrict(cachedDistrict);
					facility.setCommunity(cachedCommunity);
				}
				
				facility.setCity(city);
				facility.setLatitude(latitude);
				facility.setLongitude(longitude);
				facility.setPublicOwnership(publicOwnership);
				facility.setType(type);
				
				facilityService.persist(facility);
			}
		});
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