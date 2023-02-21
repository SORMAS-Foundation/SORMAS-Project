package de.symeda.sormas.backend;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.api.utils.OutdatedEntityException;

public class DtoEntityTest extends AbstractBeanTest {

	@Test
	public void testDtoTooOld() {

		RegionDto region = RegionDto.build();
		region.setName("Region1");
		getRegionFacade().save(region);

		region.setName("Region2");
		assertThrows(OutdatedEntityException.class, () -> getRegionFacade().save(region));
	}
}
