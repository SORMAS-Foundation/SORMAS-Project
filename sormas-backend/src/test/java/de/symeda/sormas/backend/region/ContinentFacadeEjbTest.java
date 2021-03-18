package de.symeda.sormas.backend.region;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.region.ContinentCriteria;
import de.symeda.sormas.api.region.ContinentDto;
import de.symeda.sormas.api.region.SubContinentCriteria;
import de.symeda.sormas.api.region.SubContinentDto;
import de.symeda.sormas.api.region.SubContinentIndexDto;
import de.symeda.sormas.api.region.SubContinentReferenceDto;
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
	public void testSubContinent() {
		final Continent continent = creator.createContinent("EUROPE");
		final SubContinentDto subContinentDto = new SubContinentDto();
		subContinentDto.setDefaultName("CENTRAL_EUROPE");
		subContinentDto.setContinent(continent.toReference());
		getSubContinentFacade().save(subContinentDto);
		final List<SubContinentReferenceDto> subContinents = getSubContinentFacade().getByDefaultName("CENTRAL_EUROPE", false);
		assertEquals(1, subContinents.size());
		final SubContinentDto savedSubContinentDto = getSubContinentFacade().getByUuid(subContinents.get(0).getUuid());
		assertEquals("CENTRAL_EUROPE", savedSubContinentDto.getDefaultName());
		assertEquals("EUROPE", savedSubContinentDto.getContinent().getCaption());

		final List<SubContinentIndexDto> indexList =
			getSubContinentFacade().getIndexList(new SubContinentCriteria().continent(continent.toReference()), null, null, null);
		assertEquals(1, indexList.size());
		final SubContinentIndexDto subContinentIndexDto = indexList.get(0);
		assertEquals("CENTRAL_EUROPE", subContinentIndexDto.getDefaultName());
		assertEquals("EUROPE", subContinentIndexDto.getContinent().getCaption());
		assertEquals("Central Europe", subContinentIndexDto.getDisplayName());
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
