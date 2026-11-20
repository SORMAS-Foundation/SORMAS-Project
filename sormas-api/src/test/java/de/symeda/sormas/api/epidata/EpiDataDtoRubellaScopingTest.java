package de.symeda.sormas.api.epidata;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;

class EpiDataDtoRubellaScopingTest {

	private static final List<String> CONCLUSION_FIELDS = List.of(
		EpiDataDto.IMPORTED_CASE,
		EpiDataDto.COUNTRY,
		EpiDataDto.CLUSTER_RELATED,
		EpiDataDto.CLUSTER_TYPE,
		EpiDataDto.CLUSTER_TYPE_TEXT,
		EpiDataDto.CLUSTER_IDENTIFIER,
		EpiDataDto.RESIDENCE_AT_ONSET);

	@Test
	void rubellaShowsTheConclusionFields() {
		FieldVisibilityCheckers checkers = luxembourgCheckersFor(Disease.RUBELLA);

		CONCLUSION_FIELDS.forEach(field -> assertTrue(checkers.isVisible(EpiDataDto.class, field), field));
	}

	@Test
	void congenitalRubellaDoesNotShowTheConclusionFields() {
		FieldVisibilityCheckers checkers = luxembourgCheckersFor(Disease.CONGENITAL_RUBELLA);

		CONCLUSION_FIELDS.forEach(field -> assertFalse(checkers.isVisible(EpiDataDto.class, field), field));
	}

	private static FieldVisibilityCheckers luxembourgCheckersFor(Disease disease) {
		return FieldVisibilityCheckers.withCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG).andWithDisease(disease);
	}
}
