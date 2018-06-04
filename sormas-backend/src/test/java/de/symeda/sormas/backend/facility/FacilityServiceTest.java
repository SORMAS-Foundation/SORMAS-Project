package de.symeda.sormas.backend.facility;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.lang.annotation.Annotation;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionService;
import info.novatec.beantest.api.BeanProviderHelper;

public class FacilityServiceTest {

    private static BeanProviderHelper bm;
    
    @BeforeClass
    public static void initialize() {
        bm = BeanProviderHelper.getInstance();
        
		FacilityService facilityService = getBean(FacilityService.class);
		RegionService regionService = getBean(RegionService.class);
		DistrictService districtService = getBean(DistrictService.class);
		CommunityService communityService = getBean(CommunityService.class);

		String countryName = getBean(ConfigFacadeEjbLocal.class).getCountryName();

		List<Region> regions = regionService.getAll();

		regionService.importRegions(countryName, regions);
		districtService.importDistricts(countryName, regions);
		communityService.importCommunities(countryName, regions);
		
		facilityService.importFacilities(countryName);
    }

    @AfterClass
    public static void cleanUp() {
        bm.shutdown();
    }
    
    protected static <T> T getBean(Class<T> beanClass, Annotation... qualifiers) {
        return bm.getBean(beanClass, qualifiers);
    }

	@Test
	public void testImportHealthFacilities() {
		FacilityService facilityService = getBean(FacilityService.class);
		RegionService regionService = getBean(RegionService.class);

		List<Facility> healthFacilitiesByDistrict = facilityService.getHealthFacilitiesByDistrict(regionService.getAll().get(0).getDistricts().get(0), false);
		// List should have some entries
		assertThat(healthFacilitiesByDistrict.size(), greaterThan(1));
	}

	@Test
	public void testImportSpecialHealthFacilities() {
		FacilityService facilityService = getBean(FacilityService.class);
		Facility otherFacility = facilityService.getByUuid(FacilityDto.OTHER_FACILITY_UUID);
		assertNotNull(otherFacility);
		Facility noneFacility = facilityService.getByUuid(FacilityDto.NONE_FACILITY_UUID);
		assertNotNull(noneFacility);
	}

	@Test
	public void testImportLaboratories() {
		FacilityService facilityService = getBean(FacilityService.class);

		List<Facility> allLaboratories = facilityService.getAllLaboratories(true);
		// List should have some entries
		assertThat(allLaboratories.size(), greaterThan(1));
	}
}
