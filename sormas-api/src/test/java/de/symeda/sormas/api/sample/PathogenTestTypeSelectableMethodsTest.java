package de.symeda.sormas.api.sample;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.CountryHelper;
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

	@Test
	public void retiredMethodsAreNotRequestable() {
		List<PathogenTestType> requestable = PathogenTestType.requestableMethods(FieldVisibilityCheckers.withDisease(Disease.CORONAVIRUS), Set.of());
		assertThat(requestable, not(hasItem(PathogenTestType.IMMUNOFLUORESCENCE_ASSAY)));
		assertThat(requestable, not(hasItem(PathogenTestType.BACTERIAL_CULTURE)));
		assertThat(requestable, not(hasItem(PathogenTestType.OTHER)));
	}

	@Test
	public void savedRetiredRequestStaysListed() {
		List<PathogenTestType> requestable = PathogenTestType
			.requestableMethods(FieldVisibilityCheckers.withDisease(Disease.CORONAVIRUS), Set.of(PathogenTestType.IMMUNOFLUORESCENCE_ASSAY));
		assertThat(requestable, hasItem(PathogenTestType.IMMUNOFLUORESCENCE_ASSAY));
	}

	@Test
	public void savedOtherRequestStaysListed() {
		List<PathogenTestType> requestable =
			PathogenTestType.requestableMethods(FieldVisibilityCheckers.withDisease(Disease.CORONAVIRUS), Set.of(PathogenTestType.OTHER));
		assertThat(requestable, hasItem(PathogenTestType.OTHER));
	}

	@Test
	public void requestsFollowTheCountryRule() {
		List<PathogenTestType> requestable = PathogenTestType
			.requestableMethods(FieldVisibilityCheckers.withDisease(Disease.MALARIA).andWithCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG), null);
		assertThat(requestable, not(hasItem(PathogenTestType.QUANTITATIVE_BUFFY_COAT)));
	}
}
