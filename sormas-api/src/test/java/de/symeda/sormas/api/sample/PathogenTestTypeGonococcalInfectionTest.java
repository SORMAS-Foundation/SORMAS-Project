package de.symeda.sormas.api.sample;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.DiseaseFieldVisibilityChecker;

public class PathogenTestTypeGonococcalInfectionTest {

	private final DiseaseFieldVisibilityChecker checker = new DiseaseFieldVisibilityChecker(Disease.GONOCOCCAL_INFECTION);

	@Test
	public void requiredTestTypesAreSelectable() {
		for (PathogenTestType type : new PathogenTestType[] {
			PathogenTestType.NON_AMPLIFIED_NUCLEIC_ACID_PROBE_TEST,
			PathogenTestType.NAAT,
			PathogenTestType.GENOTYPING,
			PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY,
			PathogenTestType.PENICILLINASE_ACTIVITY }) {
			assertTrue(checker.isVisible(PathogenTestType.class, type.name()), type + " must be visible for Gonococcal infection");
			assertTrue(PathogenTestType.isSelectableForNewTests(type), type + " must be selectable for new tests");
		}
	}

	@Test
	public void resultMetadataMatchesTheGonococcalForms() {
		assertThat(
			PathogenTestType.getResultValueTypes(PathogenTestType.NON_AMPLIFIED_NUCLEIC_ACID_PROBE_TEST),
			containsInAnyOrder(ResultValueType.QUALITATIVE));
		assertThat(PathogenTestType.getResultValueTypes(PathogenTestType.NAAT), containsInAnyOrder(ResultValueType.QUALITATIVE, ResultValueType.NUMERIC));
		assertThat(PathogenTestType.getResultValueTypes(PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY), is(empty()));
		assertThat(
			PathogenTestType.getCategory(PathogenTestType.PENICILLINASE_ACTIVITY),
			is(PathogenTestCategory.ANTIMICROBIAL_SUSCEPTIBILITY_TESTING));
		assertTrue(PathogenTestType.cqInputApplies(Disease.GONOCOCCAL_INFECTION, PathogenTestType.NAAT));
	}
}
