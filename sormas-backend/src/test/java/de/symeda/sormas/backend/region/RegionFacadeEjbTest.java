package de.symeda.sormas.backend.region;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.backend.AbstractBeanTest;

public class RegionFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetAllAfter() throws InterruptedException {
		creator.createRegion("region1");
		getRegionService().doFlush();
		Date date = new Date();
		List<RegionDto> results = getRegionFacade().getAllAfter(date);

		// List should be empty
		assertEquals(0, results.size());

		Thread.sleep(1); // delay to ignore known rounding issues in change date filter
		String regionName = "region2";
		creator.createRegion(regionName);
		results = getRegionFacade().getAllAfter(date);

		// List should have one entry
		assertEquals(1, results.size());

		assertEquals(regionName, results.get(0).getName());
	}

	@Test
	public void testGetAllActiveAsReference() {
		creator.createRegion("r1");
		Region r2 = creator.createRegion("r2");
		getRegionFacade().archive(r2.getUuid());

		assertEquals(1, getRegionFacade().getAllActiveAsReference().size());
	}
}
