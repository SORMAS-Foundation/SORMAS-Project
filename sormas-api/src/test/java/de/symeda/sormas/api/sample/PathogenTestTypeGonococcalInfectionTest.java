package de.symeda.sormas.api.sample;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;

public class PathogenTestTypeGonococcalInfectionTest extends AbstractPathogenTest {

	public static final PathogenTestType[] ALLOWED_PATHOGEN_TYPES = new PathogenTestType[] {
		PathogenTestType.CULTURE,
		PathogenTestType.ISOLATION,
		PathogenTestType.MICROSCOPY,
		PathogenTestType.NON_AMPLIFIED_NUCLEIC_ACID_PROBE_TEST,
		PathogenTestType.NAAT,
		PathogenTestType.GENOTYPING,
		PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY,
		PathogenTestType.PENICILLINASE_ACTIVITY,
		PathogenTestType.OTHER };

	@Test
	public void resultMetadataMatchesTheGonococcalForms() {
		assertThat(
			PathogenTestType.getResultValueTypes(PathogenTestType.NON_AMPLIFIED_NUCLEIC_ACID_PROBE_TEST),
			containsInAnyOrder(ResultValueType.QUALITATIVE));
		assertThat(
			PathogenTestType.getResultValueTypes(PathogenTestType.NAAT),
			containsInAnyOrder(ResultValueType.QUALITATIVE, ResultValueType.NUMERIC));
		assertThat(PathogenTestType.getResultValueTypes(PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY), is(empty()));
		assertThat(
			PathogenTestType.getCategory(PathogenTestType.PENICILLINASE_ACTIVITY),
			is(PathogenTestCategory.ANTIMICROBIAL_SUSCEPTIBILITY_TESTING));
		assertTrue(PathogenTestType.cqInputApplies(Disease.GONOCOCCAL_INFECTION, PathogenTestType.NAAT));
	}

	@Override
	protected PathogenTestType[] getAllowedPathogenTests() {
		return ALLOWED_PATHOGEN_TYPES;
	}

	@Override
	protected Disease getDisease() {
		return Disease.GONOCOCCAL_INFECTION;
	}
}
