package de.symeda.sormas.backend.infrastructure.facility;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityCriteria;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityFacade;
import de.symeda.sormas.api.infrastructure.facility.FacilityIndexDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;

class FacilityFacadeEjbTest extends AbstractBeanTest {

	@Test
	void testGetAllByRegionAfter() throws InterruptedException {

		RDCF rdcf = creator.createRDCF();
		getFacilityService().doFlush();

		Date date = new Date();
		List<FacilityDto> results = getFacilityFacade().getAllByRegionAfter(rdcf.region.getUuid(), date);

		// List should be empty
		assertEquals(0, results.size());

		Thread.sleep(1); // delay to ignore known rounding issues in change date filter
		String facilityName = "facility2";
		creator.createFacility(facilityName, rdcf.region, rdcf.district, rdcf.community);
		results = getFacilityFacade().getAllByRegionAfter(rdcf.region.getUuid(), date);

		// List should have one entry
		assertEquals(1, results.size());
		assertEquals(facilityName, results.get(0).getName());
		assertEquals(rdcf.community.getUuid(), results.get(0).getCommunity().getUuid());
		assertEquals(rdcf.region.getUuid(), results.get(0).getRegion().getUuid());
	}

	@Test
	void testGetAllWithoutRegionAfter() throws InterruptedException {

		FacilityDto facility = FacilityDto.build();
		facility.setName("facility");
		facility.setType(FacilityType.LABORATORY); // only lab can be saved without region
		getFacilityFacade().save(facility);
		getFacilityService().doFlush();

		Date date = new Date();
		List<FacilityDto> results = getFacilityFacade().getAllWithoutRegionAfter(date);

		// List should be empty
		assertEquals(0, results.size());

		Thread.sleep(1); // delay to ignore known rounding issues in change date filter
		String facilityName = "facility2";
		facility = FacilityDto.build();
		facility.setName(facilityName);
		facility.setType(FacilityType.LABORATORY);
		getFacilityFacade().save(facility);
		results = getFacilityFacade().getAllWithoutRegionAfter(date);

		// List should have one entry
		assertEquals(1, results.size());
		assertEquals(facilityName, results.get(0).getName());
	}

	@Test
	void testGetActiveHealthFacilitiesByCommunity() {

		Region r = creator.createRegion("r");
		District d = creator.createDistrict("d", r);
		Community c = creator.createCommunity("c", d);
		creator.createFacility("f1", r, d, c);
		Facility f2 = creator.createFacility("f2", r, d, c);
		creator.createFacility("f3", FacilityType.OTHER_MEDICAL_FACILITY, r, d, c);
		getFacilityFacade().archive(f2.getUuid());

		assertEquals(
			1,
			getFacilityFacade()
				.getActiveFacilitiesByCommunityAndType(new CommunityReferenceDto(c.getUuid(), null, null), FacilityType.HOSPITAL, false, false)
				.size());
	}

	@Test
	void testGetActiveHealthFacilitiesByDistrict() {

		Region r = creator.createRegion("r");
		District d = creator.createDistrict("d", r);
		Community c = creator.createCommunity("c", d);
		creator.createFacility("f1", r, d, c);
		Facility f2 = creator.createFacility("f2", r, d, c);
		creator.createFacility("f3", FacilityType.OTHER_MEDICAL_FACILITY, r, d, c);
		getFacilityFacade().archive(f2.getUuid());

		assertEquals(
			1,
			getFacilityFacade()
				.getActiveFacilitiesByDistrictAndType(new DistrictReferenceDto(d.getUuid(), null, null), FacilityType.HOSPITAL, false, false)
				.size());
	}

	@Test
	void testGetAllActiveLaboratories() {

		RDCF rdcf = creator.createRDCF("r", "d", "c", "f");
		FacilityDto f1 = getFacilityFacade().getByUuid(rdcf.facility.getUuid());
		getFacilityFacade().archive(f1.getUuid());
		f1 = getFacilityFacade().getByUuid(f1.getUuid());
		f1.setType(FacilityType.LABORATORY);
		getFacilityFacade().save(f1);
		FacilityDto f2 = creator.createFacility("f2", rdcf.region, rdcf.district, rdcf.community);
		f2 = getFacilityFacade().getByUuid(f2.getUuid());
		f2.setType(FacilityType.LABORATORY);
		getFacilityFacade().save(f2);

		assertEquals(1, getFacilityFacade().getAllActiveLaboratories(false).size());
	}

	/**
	 * If no facilities are present, no parent of them is archived.
	 */
	@Test
	void testHasArchivedParentInfrastructureNoFacilities() {

		assertFalse(getFacilityFacade().hasArchivedParentInfrastructure(Collections.emptyList()));
	}

	@Test
	void testGetIndexListMappedSorting() {

		FacilityCriteria facilityCriteria = new FacilityCriteria();

		// 0. No sortProperties
		List<FacilityIndexDto> result = getFacilityFacade().getIndexList(facilityCriteria, null, null, new ArrayList<>());
		assertThat(result, is(empty()));

		List<SortProperty> allSortProperties = new ArrayList<>();
		allSortProperties.add(new SortProperty(FacilityDto.NAME));
		allSortProperties.add(new SortProperty(FacilityDto.TYPE));
		allSortProperties.add(new SortProperty(FacilityDto.REGION));
		allSortProperties.add(new SortProperty(FacilityDto.DISTRICT));
		allSortProperties.add(new SortProperty(FacilityDto.COMMUNITY));
		allSortProperties.add(new SortProperty(FacilityDto.CITY));
		allSortProperties.add(new SortProperty(FacilityDto.LATITUDE));
		allSortProperties.add(new SortProperty(FacilityDto.LONGITUDE));
		allSortProperties.add(new SortProperty(FacilityDto.EXTERNAL_ID));

		// 1. Sort by every property
		for (SortProperty sortProperty : allSortProperties) {
			getFacilityFacade().getIndexList(facilityCriteria, null, null, Collections.singletonList(sortProperty));
			assertThat(sortProperty.toString(), result, is(empty()));
		}

		// 2. Sort by all properties at once
		getFacilityFacade().getIndexList(facilityCriteria, null, null, allSortProperties);
		assertThat(result, is(empty()));
	}

	@Test
	void testCount() {

		getFacilityService().createConstantFacilities();
		FacilityFacade facilityFacade = getFacilityFacade();
		assertEquals(0, facilityFacade.count(null));
		assertEquals(0, facilityFacade.count(new FacilityCriteria()));

		Region region = creator.createRegion("Region1");
		District district = creator.createDistrict("District1", region);
		Community community = creator.createCommunity("Community1", district);

		creator.createFacility("lab", FacilityType.LABORATORY, region, district, community);
		assertEquals(1, facilityFacade.count(null));
		assertEquals(1, facilityFacade.count(new FacilityCriteria()));

		creator.createFacility("hospital", FacilityType.HOSPITAL, region, district, community);
		assertEquals(2, facilityFacade.count(null));
		assertEquals(2, facilityFacade.count(new FacilityCriteria()));

		assertEquals(1, facilityFacade.count(new FacilityCriteria().type(FacilityType.LABORATORY)));
	}

	@Test
	void testConstantFacilitiesAreNeverExported() {

		getFacilityService().createConstantFacilities();
		FacilityFacade facilityFacade = getFacilityFacade();
		assertEquals(0, facilityFacade.getExportList(null, Collections.emptyList(), 0, 100).size());
		assertEquals(0, facilityFacade.getExportList(new FacilityCriteria(), Collections.emptyList(), 0, 100).size());

		Region region = creator.createRegion("Region1");
		District district = creator.createDistrict("District1", region);
		Community community = creator.createCommunity("Community1", district);

		Facility lab = creator.createFacility("lab", FacilityType.LABORATORY, region, district, community);
		creator.createFacility("lab", FacilityType.LABORATORY, region, district, community);
		creator.createFacility("hospital", FacilityType.HOSPITAL, region, district, community);

		assertEquals(3, facilityFacade.getExportList(null, Collections.emptyList(), 0, 100).size());
		assertEquals(3, facilityFacade.getExportList(new FacilityCriteria(), Collections.emptyList(), 0, 100).size());
		assertEquals(2, facilityFacade.getExportList(new FacilityCriteria().type(FacilityType.LABORATORY), Collections.emptyList(), 0, 100).size());
		assertEquals(
			1,
			facilityFacade.getExportList(null, Collections.singletonList(getFacilityFacade().getByUuid(lab.getUuid()).getUuid()), 0, 100).size());
	}

	@Test
	public void testGetByAddress() {

		RDCF rdcf = creator.createRDCF();
		FacilityDto fac1 = creator.createFacility("Fac1", rdcf.region, rdcf.district, facility -> {
			facility.setStreet("Street1");
			facility.setPostalCode("PostalCode1");
			facility.setCity("City1");
		});
		FacilityDto fac2 = creator.createFacility("Fac2", rdcf.region, rdcf.district, facility -> {
			facility.setStreet("Street2");
			facility.setPostalCode("PostalCode2");
			facility.setCity("City2");
		});
		FacilityDto fac3 = creator.createFacility("Fac3", rdcf.region, rdcf.district, facility -> {
			facility.setStreet("Street3");
			facility.setPostalCode("PostalCode2");
			facility.setCity("City2");
		});
		FacilityDto fac4 = creator.createFacility("Fac4", rdcf.region, rdcf.district, facility -> {
			facility.setStreet("Street2");
			facility.setPostalCode("PostalCode2");
			facility.setCity("City2");
		});

		FacilityFacade facade = getFacilityFacade();
		assertEquals(fac1.getUuid(), facade.getByAddress("Street1", "PostalCode1", "City1").getUuid());
		assertEquals(fac3.getUuid(), facade.getByAddress("Street3", "PostalCode2", "City2").getUuid());
		assertNull(facade.getByAddress("Street5", "PostalCode5", "City5"));
		assertNull(facade.getByAddress("Street2", "PostalCode2", "City2"));
	}
}
