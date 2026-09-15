package de.symeda.sormas.api.sample;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.DiseaseFieldVisibilityChecker;

public class PathogenTestTypeRubellaTest {

	private static final List<PathogenTestType> APPROVED = List.of(
		PathogenTestType.ISOLATION,
		PathogenTestType.IGG_SERUM_ANTIBODY,
		PathogenTestType.NEUTRALIZING_ANTIBODIES,
		PathogenTestType.IGM_SERUM_ANTIBODY,
		PathogenTestType.PCR_RT_PCR,
		PathogenTestType.GENOTYPING,
		PathogenTestType.INDIRECT_FLUORESCENT_ANTIBODY,
		PathogenTestType.OTHER);

	private final DiseaseFieldVisibilityChecker checker = new DiseaseFieldVisibilityChecker(Disease.RUBELLA);

	@Test
	public void everyApprovedTestTypeIsSelectableForRubella() {
		for (PathogenTestType testType : APPROVED) {
			assertTrue(
				checker.isVisible(PathogenTestType.class, testType.name()),
				testType.name() + " must be selectable for Rubella (#14293 test-type list)");
		}
	}

	@Test
	public void noUnapprovedTestTypeIsSelectableForRubella() {
		for (PathogenTestType testType : PathogenTestType.values()) {
			if (APPROVED.contains(testType)) {
				continue;
			}
			assertFalse(
				checker.isVisible(PathogenTestType.class, testType.name()),
				testType.name() + " leaks into the Rubella test-type picker — hide it (#14293)");
		}
	}
}
