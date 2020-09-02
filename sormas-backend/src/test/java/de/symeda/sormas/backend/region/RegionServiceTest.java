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
		assertThat(getRegionService().getByName("Region", true), hasSize(1));
		assertThat(getRegionService().getByName(" Region ", true), hasSize(1));
		assertThat(getRegionService().getByName("region", true), hasSize(1));
		assertThat(getRegionService().getByName("REGION", true), hasSize(1));
		assertThat(getRegionService().getByName("The Hinterlands", true), empty());
	}
}
