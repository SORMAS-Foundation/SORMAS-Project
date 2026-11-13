/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.infrastructure.country;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.country.CountryCriteria;
import de.symeda.sormas.api.infrastructure.country.CountryDto;
import de.symeda.sormas.api.infrastructure.country.CountryIndexDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.AbstractBeanTest;

/**
 * Tests the country codes added for EpiPulse: the duplicate check of the NUTS code in {@link CountryFacadeEjb}, the new
 * codes ISO2, ISO numeric and EpiPulse location code, and the search of the countries view by these codes.
 *
 * <p>
 * The countries are saved through the facade, as the country form and "Import default countries" do, e.g. Greece with
 * ISO3 {@code GRC}, ISO2 {@code GR}, ISO numeric {@code 300}, NUTS code {@code EL} and EpiPulse location code {@code EL}.
 */
public class CountryFacadeEjbTest extends AbstractBeanTest {

	/**
	 * A NUTS code that another country already has is rejected with a validation message (shown in yellow), not with the
	 * database error of the unique constraint {@code country_nutscode_pk}; case and surrounding spaces do not matter.
	 *
	 * <p>
	 * Example: Antigua and Barbuda has {@code "AG"}, Argentina is saved with {@code "ag "} → "The NUTS code AG is already
	 * used by the country Antigua and Barbuda…".
	 */
	@Test
	@DisplayName("a NUTS code of another country is rejected with a readable message")
	public void duplicateNutsCodeIsRejected() {

		saveCountry("Antigua and Barbuda", "ATG", "AG");

		ValidationRuntimeException exception = assertThrows(ValidationRuntimeException.class, () -> saveCountry("Argentina", "ARG", "ag "));

		assertThat(
			exception.getMessage(),
			is(
				I18nProperties
					.getValidationError(Validations.countryNutsCodeAlreadyExists, "AG", "Antigua and Barbuda", "Antigua and Barbuda")));
	}

	/**
	 * A country saved again with its own NUTS code is not a duplicate of itself.
	 *
	 * <p>
	 * Example: Antigua and Barbuda with {@code "AG"}, its default name changed and saved again → saved.
	 */
	@Test
	@DisplayName("a country can be saved again with its own NUTS code")
	public void countryCanKeepItsOwnNutsCode() {

		CountryDto antigua = saveCountry("Antigua and Barbuda", "ATG", "AG");
		antigua.setDefaultName("Antigua & Barbuda");

		CountryDto saved = getCountryFacade().save(antigua);

		assertThat(saved.getNutsCode(), is("AG"));
		assertThat(saved.getDefaultName(), is("Antigua & Barbuda"));
	}

	/**
	 * An archived country still holds its NUTS code, so the code cannot be given to another country.
	 *
	 * <p>
	 * Example: Antigua and Barbuda with {@code "AG"} archived, Argentina saved with {@code "AG"} → rejected.
	 */
	@Test
	@DisplayName("the NUTS code of an archived country is still taken")
	public void nutsCodeOfArchivedCountryIsTaken() {

		CountryDto antigua = saveCountry("Antigua and Barbuda", "ATG", "AG");
		getCountryFacade().archive(antigua.getUuid());

		assertThrows(ValidationRuntimeException.class, () -> saveCountry("Argentina", "ARG", "AG"));
	}

	/**
	 * Empty NUTS codes are stored as {@code null}, so any number of countries can be without one.
	 *
	 * <p>
	 * Example: Argentina with {@code ""} and Austria with {@code "  "} → both saved, both without NUTS code.
	 */
	@Test
	@DisplayName("countries without NUTS code do not block each other")
	public void emptyNutsCodesAreStoredAsNull() {

		CountryDto argentina = saveCountry("Argentina", "ARG", "");
		CountryDto austria = saveCountry("Austria", "AUT", "  ");

		assertThat(getCountryFacade().getByUuid(argentina.getUuid()).getNutsCode(), is(nullValue()));
		assertThat(getCountryFacade().getByUuid(austria.getUuid()).getNutsCode(), is(nullValue()));
	}

	/**
	 * ISO2, ISO numeric and EpiPulse location code are saved; the EpiPulse location code without surrounding spaces.
	 *
	 * <p>
	 * Example: Greece with {@code "GR"}, {@code "300"} and {@code " EL "} → {@code "GR"}, {@code "300"}, {@code "EL"}.
	 */
	@Test
	@DisplayName("the new country codes are saved")
	public void newCountryCodesAreSaved() {

		CountryDto greece = saveGreece(" EL ");

		CountryDto saved = getCountryFacade().getByUuid(greece.getUuid());
		assertThat(saved.getIso2Code(), is("GR"));
		assertThat(saved.getIsoNumericCode(), is("300"));
		assertThat(saved.getEpipulseLocationCode(), is("EL"));
	}

	/**
	 * {@link CountryService#getByNutsCode(String)} ignores case and surrounding spaces and finds archived countries too.
	 *
	 * <p>
	 * Example: Greece with NUTS code {@code "EL"} → {@code " el"} finds Greece, also when archived; {@code "XX"},
	 * {@code ""} or {@code null} → empty list.
	 */
	@Test
	@DisplayName("countries are found by NUTS code, archived or not")
	public void countriesAreFoundByNutsCode() {

		CountryDto greece = saveGreece("EL");
		getCountryFacade().archive(greece.getUuid());

		List<String> found = getCountryService().getByNutsCode(" el").stream().map(Country::getUuid).collect(Collectors.toList());

		assertThat(found, contains(greece.getUuid()));
		assertThat(getCountryService().getByNutsCode("XX"), is(empty()));
		assertThat(getCountryService().getByNutsCode(""), is(empty()));
		assertThat(getCountryService().getByNutsCode(null), is(empty()));
	}

	/**
	 * The search field of the countries view also finds a country by its ISO2 code, ISO numeric code and EpiPulse location
	 * code.
	 *
	 * <p>
	 * Example: Greece → found by {@code "GR"}, {@code "300"} and {@code "EL"}, as before by {@code "GRC"}; Austria
	 * ({@code AUT}, {@code AT}, {@code 040}) → not found by {@code "300"}.
	 */
	@Test
	@DisplayName("the country search finds a country by its new codes")
	public void searchFindsCountryByNewCodes() {

		CountryDto greece = saveGreece("EL");
		CountryDto austria = saveCountry("Austria", "AUT", "AT");
		austria.setIso2Code("AT");
		austria.setIsoNumericCode("040");
		getCountryFacade().save(austria);

		assertThat(search("GR"), hasItem(greece.getUuid()));
		assertThat(search("300"), hasItem(greece.getUuid()));
		assertThat(search("EL"), hasItem(greece.getUuid()));
		assertThat(search("GRC"), hasItem(greece.getUuid()));
		assertThat(search("300"), not(hasItem(austria.getUuid())));
	}

	/** Saves a country with its default name, ISO3 code and NUTS code, e.g. Argentina, {@code "ARG"}, {@code "AR"}. */
	private CountryDto saveCountry(String defaultName, String isoCode, String nutsCode) {

		CountryDto country = CountryDto.build();
		country.setDefaultName(defaultName);
		country.setIsoCode(isoCode);
		country.setNutsCode(nutsCode);
		return getCountryFacade().save(country);
	}

	/** Saves Greece with ISO3 {@code GRC}, ISO2 {@code GR}, ISO numeric {@code 300}, NUTS code {@code EL} and {@code locationCode}. */
	private CountryDto saveGreece(String locationCode) {

		CountryDto greece = CountryDto.build();
		greece.setDefaultName("Greece");
		greece.setIsoCode("GRC");
		greece.setIso2Code("GR");
		greece.setIsoNumericCode("300");
		greece.setNutsCode("EL");
		greece.setEpipulseLocationCode(locationCode);
		return getCountryFacade().save(greece);
	}

	/** The uuids of the countries the search field of the countries view finds for {@code text}, e.g. {@code "300"}. */
	private List<String> search(String text) {

		return getCountryFacade().getIndexList(new CountryCriteria().nameCodeLike(text), null, null, null)
			.stream()
			.map(CountryIndexDto::getUuid)
			.collect(Collectors.toList());
	}
}
