package de.symeda.sormas.backend.facility;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;

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
}
