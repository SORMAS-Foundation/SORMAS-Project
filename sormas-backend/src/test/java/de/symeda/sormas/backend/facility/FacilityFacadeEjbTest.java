package de.symeda.sormas.backend.facility;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.facility.FacilityCriteria;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;

public class FacilityFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetAllByRegionAfter() throws InterruptedException {

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
	public void testGetAllWithoutRegionAfter() throws InterruptedException {

		FacilityDto facility = FacilityDto.build();
		facility.setName("facility");
		facility.setType(FacilityType.LABORATORY); // only lab can be saved without region
		getFacilityFacade().saveFacility(facility);
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
		getFacilityFacade().saveFacility(facility);
		results = getFacilityFacade().getAllWithoutRegionAfter(date);

		// List should have one entry
		assertEquals(1, results.size());
		assertEquals(facilityName, results.get(0).getName());
	}

	@Test
	public void testGetActiveHealthFacilitiesByCommunity() {

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
	public void testGetActiveHealthFacilitiesByDistrict() {

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
	public void testGetAllActiveLaboratories() {

		RDCF rdcf = creator.createRDCF("r", "d", "c", "f");
		FacilityDto f1 = getFacilityFacade().getByUuid(rdcf.facility.getUuid());
		getFacilityFacade().archive(f1.getUuid());
		f1 = getFacilityFacade().getByUuid(f1.getUuid());
		f1.setType(FacilityType.LABORATORY);
		getFacilityFacade().saveFacility(f1);
		FacilityDto f2 = creator.createFacility("f2", rdcf.region, rdcf.district, rdcf.community);
		f2 = getFacilityFacade().getByUuid(f2.getUuid());
		f2.setType(FacilityType.LABORATORY);
		getFacilityFacade().saveFacility(f2);

		assertEquals(1, getFacilityFacade().getAllActiveLaboratories(false).size());
	}

	/**
	 * If no facilities are present, no parent of them is archived.
	 */
	@Test
	public void testHasArchivedParentInfrastructureNoFacilities() {

		assertFalse(getFacilityFacade().hasArchivedParentInfrastructure(Collections.emptyList()));
	}

	@Test
	public void testGetIndexListMappedSorting() {

		FacilityCriteria facilityCriteria = new FacilityCriteria();

		// 0. No sortProperties
		List<FacilityDto> result = getFacilityFacade().getIndexList(facilityCriteria, null, null, new ArrayList<>());
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
}
