package de.symeda.sormas.backend.region;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.empty;

import org.junit.Test;

import de.symeda.sormas.backend.AbstractBeanTest;

public class RegionServiceTest extends AbstractBeanTest {

	@Test
	public void testGetByName() throws Exception {
		creator.createRegion("Region");
		
		assertThat(getRegionService().getByName("Region"), hasSize(1));
		assertThat(getRegionService().getByName(" Region "), hasSize(1));
		assertThat(getRegionService().getByName("region"), hasSize(1));
		assertThat(getRegionService().getByName("REGION"), hasSize(1));
		assertThat(getRegionService().getByName("The Hinterlands"), empty());
	}

}
