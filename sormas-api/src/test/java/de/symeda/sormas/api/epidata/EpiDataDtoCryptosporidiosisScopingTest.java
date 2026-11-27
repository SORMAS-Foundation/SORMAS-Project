package de.symeda.sormas.api.epidata;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;

class EpiDataDtoCryptosporidiosisScopingTest {

	private static final FieldVisibilityCheckers CRYPTO_CHECKERS =
		FieldVisibilityCheckers.withCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG).andWithDisease(Disease.CRYPTOSPORIDIOSIS);

	@Test
	void cryptosporidiosisShowsImportedCaseAndCountry() {
		assertTrue(CRYPTO_CHECKERS.isVisible(EpiDataDto.class, EpiDataDto.IMPORTED_CASE));
		assertTrue(CRYPTO_CHECKERS.isVisible(EpiDataDto.class, EpiDataDto.COUNTRY));
	}

	@Test
	void cryptosporidiosisDoesNotShowTheMeaslesImportStatus() {
		assertFalse(CRYPTO_CHECKERS.isVisible(EpiDataDto.class, EpiDataDto.CASE_IMPORTED_STATUS));
	}
}
