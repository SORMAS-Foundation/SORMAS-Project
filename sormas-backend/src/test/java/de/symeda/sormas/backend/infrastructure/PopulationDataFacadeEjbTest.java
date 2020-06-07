package de.symeda.sormas.backend.infrastructure;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCFEntities;

public class PopulationDataFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetProjectedRegionPopulation() {

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		RegionDto region = getRegionFacade().getRegionByUuid(rdcf.region.getUuid());
		region.setGrowthRate(2.7f);
		getRegionFacade().saveRegion(region);
		creator.createPopulationData(new RegionReferenceDto(rdcf.region.getUuid()), null, 450000, DateHelper.subtractYears(new Date(), 3));

		assertEquals(new Integer(487440), getPopulationDataFacade().getProjectedRegionPopulation(rdcf.region.getUuid()));
	}

	@Test
	public void testGetProjectedDistrictPopulation() {

		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
		DistrictDto district = getDistrictFacade().getDistrictByUuid(rdcf.district.getUuid());
		district.setGrowthRate(2.7f);
		getDistrictFacade().saveDistrict(district);
		creator.createPopulationData(
			new RegionReferenceDto(rdcf.region.getUuid()),
			new DistrictReferenceDto(rdcf.district.getUuid()),
			450000,
			DateHelper.subtractYears(new Date(), 3));

		assertEquals(new Integer(487440), getPopulationDataFacade().getProjectedDistrictPopulation(rdcf.district.getUuid()));
	}
}
