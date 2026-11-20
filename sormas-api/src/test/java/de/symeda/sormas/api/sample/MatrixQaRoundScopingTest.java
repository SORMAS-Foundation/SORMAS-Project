package de.symeda.sormas.api.sample;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;

public class MatrixQaRoundScopingTest {

	private static final String LUXEMBOURG = CountryHelper.COUNTRY_CODE_LUXEMBOURG;
	private static final String GERMANY = CountryHelper.COUNTRY_CODE_GERMANY;

	private static boolean visible(Disease disease, Class<?> type, Enum<?> value) {
		return FieldVisibilityCheckers.withDisease(disease).isVisible(type, value.name());
	}

	private static boolean visibleOn(String country, Disease disease, Class<?> type, Enum<?> value) {
		return FieldVisibilityCheckers.withDisease(disease).andWithCountry(country).isVisible(type, value.name());
	}

	@Test
	public void intestinalFluidIsHiddenForTuberculosisOnly() {
		assertFalse(visible(Disease.TUBERCULOSIS, SampleMaterial.class, SampleMaterial.INTESTINAL_FLUID));
		assertTrue(visible(Disease.CRYPTOSPORIDIOSIS, SampleMaterial.class, SampleMaterial.INTESTINAL_FLUID));
	}

	@Test
	public void unknownIsHiddenForMumps() {
		assertFalse(visible(Disease.MUMPS, SampleMaterial.class, SampleMaterial.UNKNOWN));
	}

	@Test
	public void pusAndUnknownStayForSalmonellosis() {
		assertTrue(visible(Disease.SALMONELLOSIS, SampleMaterial.class, SampleMaterial.PUS));
		assertTrue(visible(Disease.SALMONELLOSIS, SampleMaterial.class, SampleMaterial.UNKNOWN));
		assertTrue(visible(Disease.CONGENITAL_RUBELLA, SampleMaterial.class, SampleMaterial.UNKNOWN));
	}

	@Test
	public void astIsOfferedForTuberculosisButNotLatentTbOrCryptosporidiosis() {
		assertTrue(visible(Disease.TUBERCULOSIS, PathogenTestType.class, PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY));
		assertFalse(visible(Disease.LATENT_TUBERCULOSIS, PathogenTestType.class, PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY));
		assertFalse(visible(Disease.CRYPTOSPORIDIOSIS, PathogenTestType.class, PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY));
	}

	@Test
	public void endotrachealAspirateShowsForCovidOnLuxembourg() {
		assertTrue(visibleOn(LUXEMBOURG, Disease.CORONAVIRUS, SampleMaterial.class, SampleMaterial.ENDOTRACHEAL_ASPIRATE));
		assertFalse(visibleOn(LUXEMBOURG, Disease.MEASLES, SampleMaterial.class, SampleMaterial.ENDOTRACHEAL_ASPIRATE));
	}

	@Test
	public void diphtheriaSwabsShowOnOtherServersOnly() {
		for (SampleMaterial swab : new SampleMaterial[] {
			SampleMaterial.ABSCESS_SWAB,
			SampleMaterial.ULCER_SWAB }) {
			assertFalse(visibleOn(LUXEMBOURG, Disease.DIPHTHERIA, SampleMaterial.class, swab), swab + " must stay off Luxembourg");
			assertTrue(visibleOn(GERMANY, Disease.DIPHTHERIA, SampleMaterial.class, swab), swab + " must show on other servers");
		}
	}
}
