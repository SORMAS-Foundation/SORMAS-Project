package de.symeda.sormas.backend.region;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.backend.AbstractBeanTest;

public class RegionFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetAllAfter() {

		creator.createRegion("region1");
		Date date = new Date();
		List<RegionDto> results = getRegionFacade().getAllAfter(date);

		// List should be empty
		assertEquals(0, results.size());

		String regionName = "region2";
		creator.createRegion(regionName);
		results = getRegionFacade().getAllAfter(date);

		// List should have one entry
		assertEquals(1, results.size());

		assertEquals(regionName, results.get(0).getName());
	}

}
