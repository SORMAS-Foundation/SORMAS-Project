package de.symeda.sormas.backend.region;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.region.DistrictReferenceDto;
import org.junit.Test;

import de.symeda.sormas.api.caze.CaseLogic;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.backend.AbstractBeanTest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

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

	@Test
	public void testGetFullEpidCodeForDistrict() {
		District district = creator.createDistrict("abcdef", creator.createRegion("ghijkl"));
		String epidNumberPrefix = getDistrictFacade().getFullEpidCodeForDistrict(district.getUuid()) + "-34-";
		assertTrue(CaseLogic.isEpidNumberPrefix(epidNumberPrefix));
	}

	@Test
	public void testGetAllActiveAsReference() throws Exception {
		Region r = creator.createRegion("r");
		creator.createDistrict("d1", r);
		District d2 = creator.createDistrict("d2", r);
		getDistrictFacade().archive(d2.getUuid());
		
		assertEquals(1, getDistrictFacade().getAllActiveAsReference().size());
	}

	@Test
	public void testGetAllActiveByRegion() throws Exception {
		Region r = creator.createRegion("r");
		creator.createDistrict("d1", r);
		District d2 = creator.createDistrict("d2", r);
		getDistrictFacade().archive(d2.getUuid());
		
		assertEquals(1, getDistrictFacade().getAllActiveByRegion(r.getUuid()).size());
	}

	@Test
    public void testGetRegionUuidsForDistricts() {
		Region r1 = creator.createRegion("r1");
		Region r2 = creator.createRegion("r2");
		District d1 = creator.createDistrict("d1", r1);
		District d2 = creator.createDistrict("d2", r2);
		District d3 = creator.createDistrict("d3", r2);

		Map<String, String> districtRegions = getDistrictFacade().getRegionUuidsForDistricts(Arrays.asList(
				new DistrictReferenceDto(d1.getUuid()),
				new DistrictReferenceDto(d2.getUuid()),
				new DistrictReferenceDto(d3.getUuid())
		));

		assertThat(districtRegions.get(d1.getUuid()), is(r1.getUuid()));
		assertThat(districtRegions.get(d2.getUuid()), is(r2.getUuid()));
		assertThat(districtRegions.get(d3.getUuid()), is(r2.getUuid()));
    }
}
