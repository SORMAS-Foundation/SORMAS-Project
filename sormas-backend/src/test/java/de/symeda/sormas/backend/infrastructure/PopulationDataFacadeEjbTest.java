package de.symeda.sormas.backend.infrastructure;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCFEntities;

public class PopulationDataFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetProjectedRegionPopulation() {

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		RegionDto region = getRegionFacade().getByUuid(rdcf.region.getUuid());
		region.setGrowthRate(2.7f);
		getRegionFacade().save(region);
		creator
			.createPopulationData(new RegionReferenceDto(rdcf.region.getUuid(), null, null), null, 450000, DateHelper.subtractYears(new Date(), 3));

		assertEquals(new Integer(487440), getPopulationDataFacade().getProjectedRegionPopulation(rdcf.region.getUuid()));
	}

	@Test
	public void testGetProjectedDistrictPopulation() {

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		DistrictDto district = getDistrictFacade().getByUuid(rdcf.district.getUuid());
		district.setGrowthRate(2.7f);
		getDistrictFacade().save(district);
		creator.createPopulationData(
			new RegionReferenceDto(rdcf.region.getUuid(), null, null),
			new DistrictReferenceDto(rdcf.district.getUuid(), null, null),
			450000,
			DateHelper.subtractYears(new Date(), 3));

		assertEquals(new Integer(487440), getPopulationDataFacade().getProjectedDistrictPopulation(rdcf.district.getUuid()));
	}
}
