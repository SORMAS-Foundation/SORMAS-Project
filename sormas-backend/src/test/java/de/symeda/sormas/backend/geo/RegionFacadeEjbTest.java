package de.symeda.sormas.backend.geo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.infrastructure.region.Region;

public class RegionFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetAllAfter() throws InterruptedException {
		creator.createRegion("region1");
		getRegionService().doFlush();
		Date date = new Date();
		List<RegionDto> results = getRegionFacade().getAllAfter(date);

		// List should be empty
		assertEquals(0, results.size());

		Thread.sleep(1); // delay to ignore known rounding issues in change date filter
		String regionName = "region2";
		creator.createRegion(regionName);
		results = getRegionFacade().getAllAfter(date);

		// List should have one entry
		assertEquals(1, results.size());

		assertEquals(regionName, results.get(0).getName());
	}

	@Test
	public void testGetAllActiveAsReference() {
		creator.createRegion("r1");
		Region r2 = creator.createRegion("r2");
		getRegionFacade().archive(r2.getUuid());

		assertEquals(1, getRegionFacade().getAllActiveAsReference().size());
	}


	@Test
	public void testGetAllRegion() {
		// Arrange
		creator.createRegion("region1");
		creator.createRegion("region2");
		getRegionService().doFlush();

		// Act
		List<RegionDto> results = getRegionFacade().getAllRegion();

		// Assert
		assertEquals(2, results.size());

		RegionDto result1 = results.stream().filter(r -> r.getName().equals("region1")).findFirst().orElse(null);
		RegionDto result2 = results.stream().filter(r -> r.getName().equals("region2")).findFirst().orElse(null);

		assertEquals("region1", result1.getName());
		assertEquals("region2", result2.getName());
	}

	@Test
	public void testGetByName() {
		// Arrange
		creator.createRegion("region1");
		getRegionService().doFlush();

		// Act
		List<RegionDto> results = getRegionFacade().getByName("region1", true);

		// Assert
		assertEquals(1, results.size());

		RegionDto result1 = results.stream().filter(r -> r.getName().equals("region1")).findFirst().orElse(null);

		assertEquals("region1", result1.getName());
	}


}
