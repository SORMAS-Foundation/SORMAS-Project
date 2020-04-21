package de.symeda.sormas.backend.region;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.empty;

import org.junit.Test;

import de.symeda.sormas.backend.AbstractBeanTest;

public class DistrictServiceTest extends AbstractBeanTest {

	@Test
	public void testGetByName() throws Exception {
		Region region = creator.createRegion("Region");
		Region otherRegion = creator.createRegion("Other Region");
		creator.createDistrict("District", region);
		
		assertThat(getDistrictService().getByName("District", region, true), hasSize(1));
		assertThat(getDistrictService().getByName(" District ", region, true), hasSize(1));
		assertThat(getDistrictService().getByName("district", region, true), hasSize(1));
		assertThat(getDistrictService().getByName("DISTRICT", region, true), hasSize(1));
		assertThat(getDistrictService().getByName("District", otherRegion, true), empty());
		assertThat(getDistrictService().getByName("Redcliffe", region, true), empty());
	}

}
