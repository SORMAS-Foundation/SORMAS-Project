package de.symeda.sormas.backend.facility;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;

public class FacilityFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetAllByRegionAfter() {

		Region region = creator.createRegion("region");
		District district = creator.createDistrict("district", region);
		Community community = creator.createCommunity("community", district);
		creator.createFacility("facility", region, district, community);

		Date date = new Date();
		String regionUuid = region.getUuid();
		List<FacilityDto> results = getFacilityFacade().getAllByRegionAfter(regionUuid, date);

		// List should be empty
		assertEquals(0, results.size());

		String facilityName = "facility2";
		creator.createFacility(facilityName, region, district, community);
		results = getFacilityFacade().getAllByRegionAfter(regionUuid, date);

		// List should have one entry
		assertEquals(1, results.size());
		assertEquals(facilityName, results.get(0).getName());
		assertEquals(community.getUuid(), results.get(0).getCommunity().getUuid());
		assertEquals(regionUuid, results.get(0).getRegion().getUuid());
	}

	@Test
	public void testGetAllWithoutRegionAfter() {

		Facility facility = new Facility();
		facility.setUuid(DataHelper.createUuid());
		facility.setName("facility");
		facility.setType(FacilityType.PRIMARY);
		getFacilityService().ensurePersisted(facility);

		Date date = new Date();
		List<FacilityDto> results = getFacilityFacade().getAllWithoutRegionAfter(date);

		// List should be empty
		assertEquals(0, results.size());

		String facilityName = "facility2";
		facility = new Facility();
		facility.setUuid(DataHelper.createUuid());
		facility.setName(facilityName);
		FacilityType facilityType = FacilityType.LABORATORY;
		facility.setType(facilityType);
		getFacilityService().ensurePersisted(facility);
		results = getFacilityFacade().getAllWithoutRegionAfter(date);

		// List should have one entry
		assertEquals(1, results.size());
		assertEquals(facilityName, results.get(0).getName());
		assertEquals(facilityType, results.get(0).getType());
	}
}
