package de.symeda.sormas.backend.region;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;

import org.junit.Test;

import de.symeda.sormas.backend.AbstractBeanTest;

public class RegionServiceTest extends AbstractBeanTest {

	@Test
	public void testGetByName() {

		creator.createRegion("Region");
		assertThat(getRegionService().getByName("Region", null, true), hasSize(1));
		assertThat(getRegionService().getByName(" Region ", null, true), hasSize(1));
		assertThat(getRegionService().getByName("region", null, true), hasSize(1));
		assertThat(getRegionService().getByName("REGION", null, true), hasSize(1));
		assertThat(getRegionService().getByName("The Hinterlands", null, true), empty());
	}
}
