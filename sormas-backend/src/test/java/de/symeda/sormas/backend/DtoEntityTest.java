package de.symeda.sormas.backend;

import org.junit.Test;

import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.utils.EntityDtoTooOldException;

public class DtoEntityTest extends AbstractBeanTest {

	@Test(expected = EntityDtoTooOldException.class)
	public void testDtoTooOld() {

		RegionDto region = RegionDto.build();
		region.setName("Region1");
		getRegionFacade().saveRegion(region);
		
		region.setName("Region2");
		getRegionFacade().saveRegion(region);
	}
}
