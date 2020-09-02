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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.region;

public class GeoShapeProviderEjbTest {

//TODO: Re-implement relevant unit tests once shape files can be imported from the ui

//    private static BeanProviderHelper bm;
//    
//    @BeforeClass
//    public static void initialize() {
//        bm = BeanProviderHelper.getInstance();
//        
//		RegionService regionService = getBean(RegionService.class);
//		DistrictService districtService = getBean(DistrictService.class);
//
//		String countryName = getBean(ConfigFacadeEjbLocal.class).getCountryName();
//		List<Region> regions = regionService.getAll();
//
//		regionService.importRegions(countryName, regions);
//		districtService.importDistricts(countryName, regions);
//    }
//
//    @AfterClass
//    public static void cleanUp() {
//        bm.shutdown();
//    }
//    
//    protected static <T> T getBean(Class<T> beanClass, Annotation... qualifiers) {
//        return bm.getBean(beanClass, qualifiers);
//    }
//    
//
//	
//	@Test
//	public void testGetRegionShape() {
//		GeoShapeProvider geoShapeProvider = getBean(GeoShapeProviderEjbLocal.class);
//		RegionFacade regionFacade = getBean(RegionFacadeEjbLocal.class);
//		
//		List<RegionReferenceDto> regions = regionFacade.getAllActiveAsReference();
//		assertThat(regions.size(), greaterThan(1)); // make sure we have some regions
//		for (RegionReferenceDto region : regions) {
//			GeoLatLon[][] regionShape = geoShapeProvider.getRegionShape(region);
//			assertNotNull(regionShape);
//		}
//	}
//
//	@Test
//	public void testGetRegionByCoord() {
//		GeoShapeProvider geoShapeProvider = getBean(GeoShapeProviderEjbLocal.class);
//		RegionReferenceDto region = geoShapeProvider.getRegionByCoord(new GeoLatLon(9.076344, 7.276929));
//		assertEquals("FCT", region.getCaption());
//	}
//
//	@Test
//	public void testGetDistrictShape() {
//		GeoShapeProvider geoShapeProvider = getBean(GeoShapeProviderEjbLocal.class);
//
//		RegionReferenceDto region = geoShapeProvider.getRegionByCoord(new GeoLatLon(9.076344, 7.276929));
//		
//		DistrictFacade districtFacade = getBean(DistrictFacadeEjbLocal.class);
//		List<DistrictReferenceDto> districts = districtFacade.getAllActiveByRegion(region.getUuid());
//		assertThat(districts.size(), greaterThan(1)); // make sure we have some districts
//		
//		for (DistrictReferenceDto district : districts) {
//			GeoLatLon[][] districtShape = geoShapeProvider.getDistrictShape(district);
//			assertNotNull(districtShape);
//		}
//	}
//
//	@Test
//	public void testGetDistrictByCoord() {
//		GeoShapeProvider geoShapeProvider = getBean(GeoShapeProviderEjbLocal.class);
//		DistrictReferenceDto district = geoShapeProvider.getDistrictByCoord(new GeoLatLon(9.076344, 7.276929));
//		assertEquals("Abuja Municipal", district.getCaption());
//	}
//
//	@Test
//	public void testBuildCountryShape() {
//		GeoShapeProvider geoShapeProvider = getBean(GeoShapeProviderEjbLocal.class);
//
//		GeoLatLon[][] countryShape = geoShapeProvider.getCountryShape();
//		assertEquals(4, countryShape.length);
//	}
}
