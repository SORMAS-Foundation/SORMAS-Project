package de.symeda.sormas.backend.geo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;

import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import org.junit.Test;

import de.symeda.sormas.backend.AbstractBeanTest;

public class CommunityServiceTest extends AbstractBeanTest {

	@Test
	public void testGetByName() {

		Region region = creator.createRegion("Region");
		District district = creator.createDistrict("District", region);
		District otherDistrict = creator.createDistrict("Other District", region);
		creator.createCommunity("Community", district);

		assertThat(getCommunityService().getByName("Community", district, true), hasSize(1));
		assertThat(getCommunityService().getByName(" Community ", district, true), hasSize(1));
		assertThat(getCommunityService().getByName("community", district, true), hasSize(1));
		assertThat(getCommunityService().getByName("COMMUNITY", district, true), hasSize(1));
		assertThat(getCommunityService().getByName("Community", otherDistrict, true), empty());
		assertThat(getCommunityService().getByName("Redcliffe Village", district, true), empty());
	}
}
