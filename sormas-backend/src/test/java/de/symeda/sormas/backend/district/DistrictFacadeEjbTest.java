package de.symeda.sormas.backend.district;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.region.Region;

public class DistrictFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetAllAfter() throws InterruptedException {
		
		Region region = creator.createRegion("region");
		creator.createDistrict("district1", region);
		getDistrictService().doFlush();
		Date date = new Date();
		List<DistrictDto> results = getDistrictFacade().getAllAfter(date);

		// List should be empty
		assertEquals(0, results.size());

		Thread.sleep(1); // delay to ignore known rounding issues in change date filter
		String districtName = "district2";
		creator.createDistrict(districtName, region);
		results = getDistrictFacade().getAllAfter(date);

		// List should have one entry
		assertEquals(1, results.size());

		assertEquals(districtName, results.get(0).getName());
		assertEquals(region.getUuid(), results.get(0).getRegion().getUuid());
	}

}
