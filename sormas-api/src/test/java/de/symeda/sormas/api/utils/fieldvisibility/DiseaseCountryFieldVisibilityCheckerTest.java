package de.symeda.sormas.api.utils.fieldvisibility;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.HideForDiseasesInCountries;
import de.symeda.sormas.api.utils.HideForDiseasesInCountriesExcept;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.DiseaseCountryFieldVisibilityChecker;

public class DiseaseCountryFieldVisibilityCheckerTest {

	private static final String LU = CountryHelper.COUNTRY_CODE_LUXEMBOURG;
	private static final String DE = CountryHelper.COUNTRY_CODE_GERMANY;

	private enum Probe {

		@HideForDiseasesInCountries(diseases = Disease.MALARIA, countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
		HIDDEN_ON_LUX,
		@HideForDiseasesInCountriesExcept(diseases = Disease.MALARIA, countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
		ONLY_ON_LUX,
		@HideForDiseasesInCountries(diseases = Disease.MALARIA, countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
		@HideForDiseasesInCountriesExcept(diseases = Disease.DENGUE, countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
		BOTH,
		PLAIN
	}

	private static boolean visible(Probe probe, Disease disease, String country) throws NoSuchFieldException {
		return new DiseaseCountryFieldVisibilityChecker(disease, country).isVisible(Probe.class.getField(probe.name()));
	}

	@Test
	public void hiddenOnLuxembourgForTheListedDisease() throws Exception {
		assertFalse(visible(Probe.HIDDEN_ON_LUX, Disease.MALARIA, LU));
		assertTrue(visible(Probe.HIDDEN_ON_LUX, Disease.MALARIA, DE));
		assertTrue(visible(Probe.HIDDEN_ON_LUX, Disease.DENGUE, LU));
	}

	@Test
	public void shownOnLuxembourgOnlyForTheListedDisease() throws Exception {
		assertTrue(visible(Probe.ONLY_ON_LUX, Disease.MALARIA, LU));
		assertFalse(visible(Probe.ONLY_ON_LUX, Disease.MALARIA, DE));
		assertTrue(visible(Probe.ONLY_ON_LUX, Disease.DENGUE, DE));
	}

	@Test
	public void unannotatedFieldIsAlwaysVisible() throws Exception {
		assertTrue(visible(Probe.PLAIN, Disease.MALARIA, LU));
		assertTrue(visible(Probe.PLAIN, Disease.MALARIA, DE));
	}

	@Test
	public void nullCountryCountsAsNoListedCountry() throws Exception {
		assertTrue(visible(Probe.HIDDEN_ON_LUX, Disease.MALARIA, null));
		assertFalse(visible(Probe.ONLY_ON_LUX, Disease.MALARIA, null));
	}

	@Test
	public void bothAnnotationsApplyTogether() throws Exception {
		assertFalse(visible(Probe.BOTH, Disease.MALARIA, LU));
		assertTrue(visible(Probe.BOTH, Disease.MALARIA, DE));
		assertFalse(visible(Probe.BOTH, Disease.DENGUE, DE));
		assertTrue(visible(Probe.BOTH, Disease.DENGUE, LU));
	}

	@Test
	public void nullDiseaseIsNeverListed() throws Exception {
		assertTrue(visible(Probe.HIDDEN_ON_LUX, null, LU));
		assertTrue(visible(Probe.ONLY_ON_LUX, null, DE));
	}

	@Test
	public void fieldVisibilityCheckersAddTheRuleInEitherOrder() {
		assertFalse(FieldVisibilityCheckers.withDisease(Disease.MALARIA).andWithCountry(LU).isVisible(Probe.class, Probe.HIDDEN_ON_LUX.name()));
		assertFalse(FieldVisibilityCheckers.withCountry(LU).andWithDisease(Disease.MALARIA).isVisible(Probe.class, Probe.HIDDEN_ON_LUX.name()));
		assertTrue(FieldVisibilityCheckers.withDisease(Disease.MALARIA).isVisible(Probe.class, Probe.ONLY_ON_LUX.name()));
	}
}
