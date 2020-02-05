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
package de.symeda.sormas.backend.region;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.lang.annotation.Annotation;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.symeda.sormas.api.region.DistrictFacade;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.GeoLatLon;
import de.symeda.sormas.api.region.GeoShapeProvider;
import de.symeda.sormas.api.region.RegionFacade;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.region.DistrictFacadeEjb.DistrictFacadeEjbLocal;
import de.symeda.sormas.backend.region.GeoShapeProviderEjb.GeoShapeProviderEjbLocal;
import de.symeda.sormas.backend.region.RegionFacadeEjb.RegionFacadeEjbLocal;
import info.novatec.beantest.api.BeanProviderHelper;

public class GeoShapeProviderEjbTest {
	
    private static BeanProviderHelper bm;
    
    @BeforeClass
    public static void initialize() {
        bm = BeanProviderHelper.getInstance();
        
		RegionService regionService = getBean(RegionService.class);
		DistrictService districtService = getBean(DistrictService.class);

		String countryName = getBean(ConfigFacadeEjbLocal.class).getCountryName();
		List<Region> regions = regionService.getAll();

		regionService.importRegions(countryName, regions);
		districtService.importDistricts(countryName, regions);
    }

    @AfterClass
    public static void cleanUp() {
        bm.shutdown();
    }
    
    protected static <T> T getBean(Class<T> beanClass, Annotation... qualifiers) {
        return bm.getBean(beanClass, qualifiers);
    }
    

	
	@Test
	public void testGetRegionShape() throws Exception {
		GeoShapeProvider geoShapeProvider = getBean(GeoShapeProviderEjbLocal.class);
		RegionFacade regionFacade = getBean(RegionFacadeEjbLocal.class);
		
		List<RegionReferenceDto> regions = regionFacade.getAllActiveAsReference();
		assertThat(regions.size(), greaterThan(1)); // make sure we have some regions
		for (RegionReferenceDto region : regions) {
			GeoLatLon[][] regionShape = geoShapeProvider.getRegionShape(region);
			assertNotNull(regionShape);
		}
	}

	@Test
	public void testGetRegionByCoord() throws Exception {
		GeoShapeProvider geoShapeProvider = getBean(GeoShapeProviderEjbLocal.class);
		RegionReferenceDto region = geoShapeProvider.getRegionByCoord(new GeoLatLon(9.076344, 7.276929));
		assertEquals("FCT", region.getCaption());
	}

	@Test
	public void testGetDistrictShape() throws Exception {
		GeoShapeProvider geoShapeProvider = getBean(GeoShapeProviderEjbLocal.class);

		RegionReferenceDto region = geoShapeProvider.getRegionByCoord(new GeoLatLon(9.076344, 7.276929));
		
		DistrictFacade districtFacade = getBean(DistrictFacadeEjbLocal.class);
		List<DistrictReferenceDto> districts = districtFacade.getAllActiveByRegion(region.getUuid());
		assertThat(districts.size(), greaterThan(1)); // make sure we have some districts
		
		for (DistrictReferenceDto district : districts) {
			GeoLatLon[][] districtShape = geoShapeProvider.getDistrictShape(district);
			assertNotNull(districtShape);
		}
	}

	@Test
	public void testGetDistrictByCoord() throws Exception {
		GeoShapeProvider geoShapeProvider = getBean(GeoShapeProviderEjbLocal.class);
		DistrictReferenceDto district = geoShapeProvider.getDistrictByCoord(new GeoLatLon(9.076344, 7.276929));
		assertEquals("Abuja Municipal", district.getCaption());
	}

	@Test
	public void testBuildCountryShape() throws Exception {
		GeoShapeProvider geoShapeProvider = getBean(GeoShapeProviderEjbLocal.class);

		GeoLatLon[][] countryShape = geoShapeProvider.getCountryShape();
		assertEquals(4, countryShape.length);
	}
}
