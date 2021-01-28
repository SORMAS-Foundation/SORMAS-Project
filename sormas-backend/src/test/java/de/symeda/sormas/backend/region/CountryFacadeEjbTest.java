package de.symeda.sormas.backend.region;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.junit.Test;

import com.google.common.collect.Lists;

import de.symeda.sormas.api.region.CountryCriteria;
import de.symeda.sormas.api.region.CountryDto;
import de.symeda.sormas.api.region.CountryIndexDto;
import de.symeda.sormas.api.region.CountryReferenceDto;
import de.symeda.sormas.api.utils.EmptyValueException;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.AbstractBeanTest;

public class CountryFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetCountryByUuid() {
		Country expected = creator.createCountry("Romania", "ROU", "642");
		getCountryService().doFlush();

		CountryDto actual = getCountryFacade().getCountryByUuid(expected.getUuid());
		assertTrue(entityIsEqualToDto(expected, actual));
	}

	@Test
	public void testGetByDefaultName() {
		Country expected = creator.createCountry("Romania", "ROU", "642");
		getCountryService().doFlush();

		List<CountryReferenceDto> actualList = getCountryFacade().getByDefaultName(expected.getDefaultName(), false);
		CountryReferenceDto actual = actualList.get(0);
		assertEquals(expected.getUuid(), actual.getUuid());
	}

	@Test
	public void testGetIndexList() {
		Country expected = creator.createCountry("Romania", "ROU", "642");
		creator.createCountry("Germany", "DEU", "276");
		CountryCriteria criteria = new CountryCriteria().nameCodeLike("ROU");
		List<CountryIndexDto> actualList = getCountryFacade().getIndexList(criteria, null, null, null);
		assertEquals(1, actualList.size());
		CountryIndexDto actual = actualList.get(0);
		assertEquals(expected.getUuid(), actual.getUuid());
		assertEquals(expected.getIsoCode(), actual.getIsoCode());
	}

	@Test
	public void testGetAllAfter() {
		Country country1 = creator.createCountry("Romania", "ROU", "642");
		Country country2 = creator.createCountry("Germany", "DEU", "276");
		country2.setChangeDate(Timestamp.from(Instant.now().minus(2, DAYS)));
		getCountryService().doFlush();

		List<CountryDto> actualList = getCountryFacade().getAllAfter(Timestamp.from(Instant.now().minus(1, DAYS)));
		CountryDto actual = actualList.get(0);
		assertTrue(entityIsEqualToDto(country1, actual));
	}

	@Test
	public void testGetByUuids() {
		Country country1 = creator.createCountry("Romania", "ROU", "642");
		Country country2 = creator.createCountry("Germany", "DEU", "276");
		creator.createCountry("France", "FRA", "123");
		getCountryService().doFlush();
		List<String> uuids = Lists.newArrayList(country1.getUuid(), country2.getUuid());

		List<String> actualList = getCountryFacade().getByUuids(uuids).stream().map(CountryDto::getUuid).collect(toList());
		assertTrue(uuids.containsAll(actualList));
	}

	@Test
	public void testGetUuids() {
		Country country1 = creator.createCountry("Romania", "ROU", "642");
		Country country2 = creator.createCountry("Germany", "DEU", "276");
		Country country3 = creator.createCountry("France", "FRA", "123");
		getCountryService().doFlush();
		List<String> uuids = Lists.newArrayList(country1.getUuid(), country2.getUuid(), country3.getUuid());
		List<String> actualList = getCountryFacade().getAllUuids();
		assertTrue(uuids.containsAll(actualList));
	}

	@Test
	public void testCount() {
		creator.createCountry("Romania", "ROU", "642");
		creator.createCountry("Germany", "DEU", "276");
		CountryCriteria criteria = new CountryCriteria().nameCodeLike("ROU");
		long count = getCountryFacade().count(criteria);
		assertEquals(1, count);
	}

	@Test
	public void testSaveCountrySuccessful() throws Exception {
		CountryDto expected = new CountryDto();
		expected.setDefaultName("Romania");
		expected.setIsoCode("ROU");
		expected.setUnoCode("642");
		String uuid = getCountryFacade().saveCountry(expected);
		expected.setUuid(uuid);
		Country actual = getCountryService().getByIsoCode("ROU", false).orElseThrow(() -> new Exception("Country not found"));
		assertTrue(entityIsEqualToDto(actual, expected));
	}

	@Test(expected = EmptyValueException.class)
	public void testSaveCountryIsoCodeEmpty() {
		CountryDto country = new CountryDto();
		country.setDefaultName("Romania");
		getCountryFacade().saveCountry(country);
	}

	@Test(expected = ValidationRuntimeException.class)
	public void testSaveCountryIsoCodeExists() {
		creator.createCountry("Romania", "ROU", "642");
		CountryDto duplicate = new CountryDto();
		duplicate.setDefaultName("Romania");
		duplicate.setIsoCode("ROU");
		getCountryFacade().saveCountry(duplicate);
	}

	@Test(expected = ValidationRuntimeException.class)
	public void testSaveCountryUnoCodeExists() {
		creator.createCountry("Romania", "ROU", "642");
		CountryDto duplicate = new CountryDto();
		duplicate.setDefaultName("Germany");
		duplicate.setIsoCode("DEU");
		duplicate.setUnoCode("642");
		getCountryFacade().saveCountry(duplicate);
	}

	@Test
	public void testArchive() {
		Country country = creator.createCountry("Romania", "ROU", "642");
		getCountryFacade().archive(country.getUuid());
		CountryDto actual = getCountryFacade().getCountryByUuid(country.getUuid());
		assertTrue(actual.isArchived());
	}

	@Test
	public void testDearchive() {
		Country country = creator.createCountry("Romania", "ROU", "642");
		country.setArchived(true);
		getCountryFacade().dearchive(country.getUuid());
		CountryDto actual = getCountryFacade().getCountryByUuid(country.getUuid());
		assertFalse(actual.isArchived());
	}

	private boolean entityIsEqualToDto(Country entity, CountryDto dto) {
		return Objects.equals(entity.getUuid(), (dto.getUuid()))
			&& Objects.equals(entity.isArchived(), dto.isArchived())
			&& Objects.equals(entity.getExternalId(), (dto.getExternalId()))
			&& Objects.equals(entity.getDefaultName(), (dto.getDefaultName()))
			&& Objects.equals(entity.getIsoCode(), (dto.getIsoCode()))
			&& Objects.equals(entity.getUnoCode(), (dto.getUnoCode()));
	}
}
