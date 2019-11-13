package de.symeda.sormas.backend.infrastructure;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;

public class PointOfEntryFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetAllAfter() {

		Region region = creator.createRegion("region");
		District district = creator.createDistrict("district", region);
		creator.createPointOfEntry("pointOfEntry1", region, district);
		Date date = new Date();
		List<PointOfEntryDto> results = getPointOfEntryFacade().getAllAfter(date);

		// List should be empty
		assertEquals(0, results.size());

		String pointOfEntryName = "pointOfEntry2";
		creator.createPointOfEntry(pointOfEntryName, region, district);
		results = getPointOfEntryFacade().getAllAfter(date);

		// List should have one entry
		assertEquals(1, results.size());

		assertEquals(pointOfEntryName, results.get(0).getName());
		assertEquals(district.getUuid(), results.get(0).getDistrict().getUuid());
		assertEquals(region.getUuid(), results.get(0).getRegion().getUuid());
	}
}
