package de.symeda.sormas.backend.region;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.region.ContinentCriteria;
import de.symeda.sormas.api.region.ContinentDto;
import de.symeda.sormas.api.region.SubcontinentCriteria;
import de.symeda.sormas.api.region.SubcontinentDto;
import de.symeda.sormas.api.region.SubcontinentIndexDto;
import de.symeda.sormas.api.region.SubcontinentReferenceDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.AbstractBeanTest;

public class ContinentFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetByUuid() {
		final Continent expected = creator.createContinent("Europe");
		final ContinentDto actual = getContinentFacade().getByUuid(expected.getUuid());
		assertEquals(expected.getDefaultName(), actual.getDefaultName());
	}

	@Test
	public void testSaveAndGetByDefaultName() {
		final ContinentDto continentDto = new ContinentDto();
		continentDto.setDefaultName("EUROPE");
		getContinentFacade().save(continentDto);
		assertEquals(1, getContinentFacade().getByDefaultName("EUROPE", false).size());
	}

	@Test
	public void testSubcontinent() {
		final Continent continent = creator.createContinent("EUROPE");
		final SubcontinentDto subcontinentDto = new SubcontinentDto();
		subcontinentDto.setDefaultName("CENTRAL_EUROPE");
		subcontinentDto.setContinent(continent.toReference());
		getSubcontinentFacade().save(subcontinentDto);
		final List<SubcontinentReferenceDto> subcontinents = getSubcontinentFacade().getByDefaultName("CENTRAL_EUROPE", false);
		assertEquals(1, subcontinents.size());
		final SubcontinentDto savedSubcontinentDto = getSubcontinentFacade().getByUuid(subcontinents.get(0).getUuid());
		assertEquals("CENTRAL_EUROPE", savedSubcontinentDto.getDefaultName());
		assertEquals("EUROPE", savedSubcontinentDto.getContinent().getCaption());

		final List<SubcontinentIndexDto> indexList =
			getSubcontinentFacade().getIndexList(new SubcontinentCriteria().continent(continent.toReference()), null, null, null);
		assertEquals(1, indexList.size());
		final SubcontinentIndexDto subcontinentIndexDto = indexList.get(0);
		assertEquals("CENTRAL_EUROPE", subcontinentIndexDto.getDefaultName());
		assertEquals("EUROPE", subcontinentIndexDto.getContinent().getCaption());
		assertEquals("Central Europe", subcontinentIndexDto.getDisplayName());
	}

	@Test(expected = ValidationRuntimeException.class)
	public void testSaveContinentExists() {
		final ContinentDto continentDto = new ContinentDto();
		continentDto.setDefaultName("Europe");
		getContinentFacade().save(continentDto);
		getContinentFacade().save(continentDto);
	}

	@Test
	public void testGetIndexListAndCount() {
		creator.createContinent("Europe");
		creator.createContinent("Africa");
		creator.createContinent("Asia");
		creator.createContinent("North America");
		creator.createContinent("South America");
		creator.createContinent("Australia");
		creator.createContinent("Antarctica");

		assertEquals(7, getContinentFacade().getIndexList(null, null, null, null).size());
		assertEquals(7, getContinentFacade().count(new ContinentCriteria()));
		assertEquals(1, getContinentFacade().getIndexList(new ContinentCriteria().nameLike("Asia"), null, null, null).size());
	}
}
