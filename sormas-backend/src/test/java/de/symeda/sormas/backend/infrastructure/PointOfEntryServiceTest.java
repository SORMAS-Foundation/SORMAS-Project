package de.symeda.sormas.backend.infrastructure;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;

import org.junit.Test;

import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;

public class PointOfEntryServiceTest extends AbstractBeanTest {

	@Test
	public void testGetByName() {

		Region region = creator.createRegion("Region");
		District district = creator.createDistrict("District", region);
		District otherDistrict = creator.createDistrict("Other District", region);
		creator.createPointOfEntry("Point of Entry", region, district);

		assertThat(getPointOfEntryService().getByName("Point of Entry", district), hasSize(1));
		assertThat(getPointOfEntryService().getByName(" Point of Entry ", district), hasSize(1));
		assertThat(getPointOfEntryService().getByName("point of entry", district), hasSize(1));
		assertThat(getPointOfEntryService().getByName("POINT OF ENTRY", district), hasSize(1));
		assertThat(getPointOfEntryService().getByName("Point of Entry", otherDistrict), empty());
		assertThat(getPointOfEntryService().getByName("Redcliffe Harbour", district), empty());
	}
}
