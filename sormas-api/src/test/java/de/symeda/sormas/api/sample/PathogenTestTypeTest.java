package de.symeda.sormas.api.sample;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;

public class PathogenTestTypeTest {

	/**
	 * Legacy / merged methods that must stay in the enum (still referenced by classification, mapping
	 * and existing records) but must no longer be offered when adding a new test.
	 */
	private static final Set<PathogenTestType> EXPECTED_LEGACY = EnumSet.of(
		PathogenTestType.ANTIBODY_DETECTION,
		PathogenTestType.ANTIGEN_DETECTION,
		PathogenTestType.CULTURE,
		PathogenTestType.ISOLATION,
		PathogenTestType.IGM_SERUM_ANTIBODY,
		PathogenTestType.IGG_SERUM_ANTIBODY,
		PathogenTestType.IGA_SERUM_ANTIBODY,
		PathogenTestType.INCUBATION_TIME,
		PathogenTestType.MICROSCOPY,
		PathogenTestType.LATEX_AGGLUTINATION,
		PathogenTestType.CQ_VALUE_DETECTION,
		PathogenTestType.SEQUENCING,
		// "Other <category>" placeholders: superseded but kept hidden so existing records still load
		PathogenTestType.OTHER_ANTIGEN_DETECTION_TEST,
		PathogenTestType.OTHER_MOLECULAR_ASSAY,
		PathogenTestType.OTHER_SEROLOGICAL_TEST);

	@Test
	public void everySelectableMethodHasACategory() {
		List<PathogenTestType> orphans = Arrays.stream(PathogenTestType.values())
			.filter(PathogenTestType::isSelectableForNewTests)
			.filter(t -> t != PathogenTestType.OTHER)
			.filter(t -> PathogenTestType.getCategory(t) == null)
			.collect(Collectors.toList());
		assertThat("every selectable method (except OTHER) must declare a category", orphans, is(empty()));
	}

	@Test
	public void otherHasNoCategoryAndIsNotCategorised() {
		assertNull(PathogenTestType.getCategory(PathogenTestType.OTHER));
		assertNull(PathogenTestType.getCategory(null));
	}

	@Test
	public void legacyMethodsAreNotSelectableButAreKept() {
		List<PathogenTestType> actualLegacy = Arrays.stream(PathogenTestType.values())
			.filter(t -> !PathogenTestType.isSelectableForNewTests(t))
			.collect(Collectors.toList());
		assertThat(actualLegacy, containsInAnyOrder(EXPECTED_LEGACY.toArray(new PathogenTestType[0])));
	}

	@Test
	public void legacyMethodsStillRenderTheirCaption() {
		// Existing records must keep displaying — toString must not be blank for a hidden method.
		for (PathogenTestType legacy : EXPECTED_LEGACY) {
			assertFalse(legacy.toString().isBlank(), "legacy method " + legacy.name() + " must still render a caption");
		}
	}

	@Test
	public void newMethodsAreSelectable() {
		// A representative sample of the methods added in this change.
		List<PathogenTestType> newlyAdded = Arrays.asList(
			PathogenTestType.MULTIPLEX_PCR,
			PathogenTestType.DIGITAL_PCR,
			PathogenTestType.SANGER_SEQUENCING,
			PathogenTestType.WESTERN_BLOT,
			PathogenTestType.RAPID_ANTIBODY_TEST,
			PathogenTestType.LATERAL_FLOW_ASSAY,
			PathogenTestType.RDT,
			PathogenTestType.BACTERIAL_CULTURE,
			PathogenTestType.MALDI_TOF,
			PathogenTestType.ACID_FAST_STAIN,
			PathogenTestType.GENOTYPIC_RESISTANCE_TEST,
			PathogenTestType.FLOW_CYTOMETRY);
		for (PathogenTestType type : newlyAdded) {
			assertTrue(PathogenTestType.isSelectableForNewTests(type), type.name() + " should be selectable for new tests");
			assertFalse(EXPECTED_LEGACY.contains(type), type.name() + " is a new method, not legacy");
		}
	}

	@Test
	public void allSevenCategoriesAreRepresentedBySelectableMethods() {
		Set<PathogenTestCategory> represented = Arrays.stream(PathogenTestType.values())
			.filter(PathogenTestType::isSelectableForNewTests)
			.map(PathogenTestType::getCategory)
			.filter(c -> c != null)
			.collect(Collectors.toSet());
		assertThat(represented, containsInAnyOrder(PathogenTestCategory.values()));
	}

	@Test
	public void categoryMatchesAcrossKnownAnchors() {
		assertThat(PathogenTestType.getCategory(PathogenTestType.PCR_RT_PCR), is(PathogenTestCategory.MOLECULAR_ASSAYS));
		assertThat(
			PathogenTestType.getCategory(PathogenTestType.ENZYME_LINKED_IMMUNOSORBENT_ASSAY),
			is(PathogenTestCategory.SEROLOGICAL_TESTS));
		assertThat(PathogenTestType.getCategory(PathogenTestType.RDT), is(PathogenTestCategory.ANTIGEN_DETECTION));
		assertThat(PathogenTestType.getCategory(PathogenTestType.BACTERIAL_CULTURE), is(PathogenTestCategory.CULTURE_AND_ISOLATION));
		assertThat(PathogenTestType.getCategory(PathogenTestType.ACID_FAST_STAIN), is(PathogenTestCategory.MICROSCOPY_AND_STAINING));
		assertThat(
			PathogenTestType.getCategory(PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY),
			is(PathogenTestCategory.ANTIMICROBIAL_SUSCEPTIBILITY_TESTING));
		assertThat(PathogenTestType.getCategory(PathogenTestType.IGRA), is(PathogenTestCategory.FUNCTIONAL_IMMUNE_ASSAYS));
	}

	@Test
	public void legacyTestTypeStillResolvesForExistingDiseaseConfiguration() {
		// SEQUENCING is a classification trigger for Invasive Pneumococcal Infection; it must remain a
		// visible value for that disease even though it is no longer selectable for brand-new tests.
		List<PathogenTestType> visibleForIpi = de.symeda.sormas.api.utils.Diseases.DiseasesConfiguration
			.getVisibleValues(PathogenTestType.class, Disease.INVASIVE_PNEUMOCOCCAL_INFECTION);
		assertThat(visibleForIpi, hasItem(PathogenTestType.SEQUENCING));
	}
}
