package de.symeda.sormas.api.sample;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.DiseaseFieldVisibilityChecker;

public class PathogenTestTypeSyphilisTest {

	static final List<PathogenTestType> APPROVED = List.of(
		PathogenTestType.DARK_FIELD_MICROSCOPY,
		PathogenTestType.DIRECT_FLUORESCENT_ANTIBODY,
		PathogenTestType.IGM_SERUM_ANTIBODY,
		PathogenTestType.MICROSCOPY,
		PathogenTestType.NAAT,
		PathogenTestType.NON_TREPONEMAL_TESTS,
		PathogenTestType.OTHER,
		PathogenTestType.PCR_RT_PCR,
		PathogenTestType.Q_PCR,
		PathogenTestType.TREPONEMAL_TESTS,
		PathogenTestType.WESTERN_BLOT);

	static final List<PathogenTestType> TRIMMED = List.of(
		PathogenTestType.ACID_FAST_STAIN,
		PathogenTestType.ANTIBODY_DETECTION,
		PathogenTestType.CRISPR_DIAGNOSTICS,
		PathogenTestType.CULTURE,
		PathogenTestType.DIGITAL_PCR,
		PathogenTestType.DNA_MICROARRAY,
		PathogenTestType.ELECTRON_MICROSCOPY,
		PathogenTestType.FISH,
		PathogenTestType.FLOW_CYTOMETRY,
		PathogenTestType.GENOTYPIC_RESISTANCE_TEST,
		PathogenTestType.GIEMSA_STAIN,
		PathogenTestType.GRAM_STAIN,
		PathogenTestType.HEMAGGLUTINATION_INHIBITION,
		PathogenTestType.HISTOPATHOLOGY,
		PathogenTestType.IGA_SERUM_ANTIBODY,
		PathogenTestType.IGG_SERUM_ANTIBODY,
		PathogenTestType.IMMUNOFLUORESCENCE_ASSAY,
		PathogenTestType.IMMUNOHISTOCHEMISTRY,
		PathogenTestType.ISOLATION,
		PathogenTestType.LATERAL_FLOW_ASSAY,
		PathogenTestType.LINE_PROBE_ASSAY,
		PathogenTestType.MALDI_TOF,
		PathogenTestType.MULTIPLEX_PCR,
		PathogenTestType.NASBA,
		PathogenTestType.NEUTRALIZING_ANTIBODIES,
		PathogenTestType.QUANTITATIVE_BUFFY_COAT,
		PathogenTestType.QUELLUNG_REACTION,
		PathogenTestType.SANGER_SEQUENCING,
		PathogenTestType.TMA);

	private final DiseaseFieldVisibilityChecker checker = new DiseaseFieldVisibilityChecker(Disease.SYPHILIS);

	@Test
	public void everyApprovedMethodIsSelectableForSyphilis() {
		for (PathogenTestType type : APPROVED) {
			assertTrue(
				checker.isVisible(PathogenTestType.class, type.name()),
				type.name() + " must be visible for Syphilis (#14220 test list)");
			assertTrue(
				PathogenTestType.isSelectableForNewTests(type),
				type.name() + " must be offered when adding a new Syphilis test");
		}
	}

	@Test
	public void everyTrimmedMethodIsHiddenForSyphilis() {
		for (PathogenTestType type : TRIMMED) {
			assertFalse(
				checker.isVisible(PathogenTestType.class, type.name()),
				type.name() + " must not be offered for Syphilis (#14220 test list)");
		}
	}

	@Test
	public void noUnclassifiedMethodIsSelectableForSyphilis() {
		for (PathogenTestType type : PathogenTestType.values()) {
			if (APPROVED.contains(type) || !PathogenTestType.isSelectableForNewTests(type)) {
				continue;
			}
			assertFalse(
				checker.isVisible(PathogenTestType.class, type.name()),
				type.name() + " leaks into the Syphilis method picker — add it to APPROVED or to a hide-list");
		}
	}

	@Test
	public void bothSerologySubcategoriesAreSerologicalTests() {
		assertThat(PathogenTestType.getCategory(PathogenTestType.NON_TREPONEMAL_TESTS), is(PathogenTestCategory.SEROLOGICAL_TESTS));
		assertThat(PathogenTestType.getCategory(PathogenTestType.TREPONEMAL_TESTS), is(PathogenTestCategory.SEROLOGICAL_TESTS));
	}

	@Test
	public void onlyNonTreponemalTestsCarryATitre() {
		assertThat(
			PathogenTestType.getResultValueTypes(PathogenTestType.NON_TREPONEMAL_TESTS),
			containsInAnyOrder(ResultValueType.QUALITATIVE, ResultValueType.NUMERIC));
		assertThat(
			PathogenTestType.getResultValueTypes(PathogenTestType.TREPONEMAL_TESTS),
			containsInAnyOrder(ResultValueType.QUALITATIVE));
	}

	@Test
	public void bothSerologySubcategoriesAreScopedToSyphilis() {
		DiseaseFieldVisibilityChecker measles = new DiseaseFieldVisibilityChecker(Disease.MEASLES);
		assertFalse(measles.isVisible(PathogenTestType.class, PathogenTestType.NON_TREPONEMAL_TESTS.name()));
		assertFalse(measles.isVisible(PathogenTestType.class, PathogenTestType.TREPONEMAL_TESTS.name()));
	}
}
