package de.symeda.sormas.api.sample;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;

public class CountryScopedMatrixCellsTest {

	static Stream<Arguments> cells() {
		return Stream.of(
			Arguments.of(SampleMaterial.class, "LUNG_TISSUE", Disease.NEW_INFLUENZA, false, true),
			Arguments.of(SampleMaterial.class, "CONJUNCTIVAL_SWAB", Disease.GONOCOCCAL_INFECTION, false, true),
			Arguments.of(SampleMaterial.class, "EYE", Disease.GONOCOCCAL_INFECTION, true, false),
			Arguments.of(PathogenTestType.class, "INDIRECT_FLUORESCENT_ANTIBODY", Disease.MALARIA, false, true),
			Arguments.of(PathogenTestType.class, "QUANTITATIVE_BUFFY_COAT", Disease.MALARIA, false, true),
			Arguments.of(PathogenTestType.class, "WHOLE_GENOME_SEQUENCING", Disease.MALARIA, false, true),
			Arguments.of(PathogenTestType.class, "SANGER_SEQUENCING", Disease.RUBELLA, false, true),
			Arguments.of(PathogenTestType.class, "MALDI_TOF", Disease.LATENT_TUBERCULOSIS, true, false),
			Arguments.of(PathogenTestType.class, "WESTERN_BLOT", Disease.INFLUENZA, true, false));
	}

	@ParameterizedTest(name = "{1} for {2}: Luxembourg {3}, elsewhere {4}")
	@MethodSource("cells")
	public void cellFollowsTheMatrixPerServer(Class<?> type, String constant, Disease disease, boolean onLux, boolean elsewhere) {
		assertEquals(
			onLux,
			FieldVisibilityCheckers.withDisease(disease).andWithCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG).isVisible(type, constant),
			constant + " on Luxembourg");
		assertEquals(
			elsewhere,
			FieldVisibilityCheckers.withDisease(disease).andWithCountry(CountryHelper.COUNTRY_CODE_GERMANY).isVisible(type, constant),
			constant + " on other servers");
	}
}
