package de.symeda.sormas.api.sample;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;

public class PathogenTestTypeSelectableMethodsTest {

	@Test
	public void methodsSortIgnoringCapitals() {
		List<PathogenTestType> tb = PathogenTestType.selectableMethods(FieldVisibilityCheckers.withDisease(Disease.TUBERCULOSIS), null);
		assertThat(tb.indexOf(PathogenTestType.MICROSCOPY), lessThan(tb.indexOf(PathogenTestType.MIRU_PATTERN_CODE)));

		List<PathogenTestType> diphtheria = PathogenTestType.selectableMethods(FieldVisibilityCheckers.withDisease(Disease.DIPHTHERIA), null);
		assertThat(diphtheria.indexOf(PathogenTestType.Q_PCR), lessThan(diphtheria.indexOf(PathogenTestType.WHOLE_GENOME_SEQUENCING)));
	}

	@Test
	public void retiredMethodsAreNotListed() {
		for (PathogenTestType type : PathogenTestType.selectableMethods(FieldVisibilityCheckers.withDisease(Disease.CORONAVIRUS), null)) {
			assertTrue(PathogenTestType.isSelectableForNewTests(type), type + " is retired and must not be offered");
		}
	}

	@Test
	public void retainedMethodIsListedEvenWhenRetiredOrHidden() {
		List<PathogenTestType> methods =
			PathogenTestType.selectableMethods(FieldVisibilityCheckers.withDisease(Disease.DENGUE), PathogenTestType.INCUBATION_TIME);
		assertThat(methods, hasItem(PathogenTestType.INCUBATION_TIME));
	}
}
