package de.symeda.sormas.api.sample;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
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
		PathogenTestType.INCUBATION_TIME,
		PathogenTestType.MICROSCOPY,
		PathogenTestType.CQ_VALUE_DETECTION,
		PathogenTestType.SEQUENCING,
		// Superseded by the per-Ig-class ELISA split (#13951); kept so historic records still render and
		// case-classification rules referencing this constant keep working.
		PathogenTestType.ENZYME_LINKED_IMMUNOSORBENT_ASSAY,
		// Superseded by the merged CULTURE entry (#13951); kept so historic records still render.
		PathogenTestType.BACTERIAL_CULTURE,
		PathogenTestType.FUNGAL_CULTURE,
		// Superseded by the merged ISOLATION entry (#13951); kept so historic records still render.
		PathogenTestType.VIRAL_ISOLATION,
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
		List<PathogenTestType> actualLegacy =
			Arrays.stream(PathogenTestType.values()).filter(t -> !PathogenTestType.isSelectableForNewTests(t)).collect(Collectors.toList());
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
			PathogenTestType.CULTURE,
			PathogenTestType.ISOLATION,
			PathogenTestType.DIRECT_MICROSCOPY,
			PathogenTestType.IGM_SERUM_ANTIBODY,
			PathogenTestType.IGG_SERUM_ANTIBODY,
			PathogenTestType.IGA_SERUM_ANTIBODY,
			PathogenTestType.MALDI_TOF,
			PathogenTestType.ACID_FAST_STAIN,
			PathogenTestType.GENOTYPIC_RESISTANCE_TEST,
			PathogenTestType.FLOW_CYTOMETRY,
			PathogenTestType.ANTIGEN_DETECTION,
			PathogenTestType.LATEX_AGGLUTINATION,
			PathogenTestType.RSV_SUBTYPING);
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
		assertThat(PathogenTestType.getCategory(PathogenTestType.ENZYME_LINKED_IMMUNOSORBENT_ASSAY), is(PathogenTestCategory.SEROLOGICAL_TESTS));
		assertThat(PathogenTestType.getCategory(PathogenTestType.IGG_SERUM_ANTIBODY), is(PathogenTestCategory.SEROLOGICAL_TESTS));
		assertThat(PathogenTestType.getCategory(PathogenTestType.RDT), is(PathogenTestCategory.ANTIGEN_DETECTION));
		assertThat(PathogenTestType.getCategory(PathogenTestType.CULTURE), is(PathogenTestCategory.CULTURE_AND_ISOLATION));
		assertThat(PathogenTestType.getCategory(PathogenTestType.ISOLATION), is(PathogenTestCategory.CULTURE_AND_ISOLATION));
		assertThat(PathogenTestType.getCategory(PathogenTestType.DIRECT_MICROSCOPY), is(PathogenTestCategory.MICROSCOPY_AND_STAINING));
		assertThat(PathogenTestType.getCategory(PathogenTestType.ACID_FAST_STAIN), is(PathogenTestCategory.MICROSCOPY_AND_STAINING));
		assertThat(
			PathogenTestType.getCategory(PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY),
			is(PathogenTestCategory.ANTIMICROBIAL_SUSCEPTIBILITY_TESTING));
		assertThat(PathogenTestType.getCategory(PathogenTestType.IGRA), is(PathogenTestCategory.FUNCTIONAL_IMMUNE_ASSAYS));
	}

	@Test
	public void everyMethodResolvesToAtLeastOneResultValueType() {
		// getResultValueTypes never returns null/empty (an unannotated value defaults to QUALITATIVE), with one
		// documented exception: Antibiotic Susceptibility Testing has no result value type of its own (its result
		// is the drug-susceptibility grid), so it is explicitly annotated with an empty set.
		for (PathogenTestType type : PathogenTestType.values()) {
			Set<ResultValueType> valueTypes = PathogenTestType.getResultValueTypes(type);
			if (type == PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY) {
				assertTrue(valueTypes.isEmpty(), "ANTIBIOTIC_SUSCEPTIBILITY must have no result value type");
			} else {
				assertFalse(valueTypes.isEmpty(), "no result value type for " + type.name());
			}
		}
		assertThat(PathogenTestType.getResultValueTypes(null), is(EnumSet.of(ResultValueType.QUALITATIVE)));
		// A value with no annotation (e.g. OTHER) falls back to qualitative.
		assertThat(PathogenTestType.getResultValueTypes(PathogenTestType.OTHER), is(EnumSet.of(ResultValueType.QUALITATIVE)));
	}

	@Test
	public void resultValueTypesMatchKnownAnchors() {
		assertThat(
			PathogenTestType.getResultValueTypes(PathogenTestType.PCR_RT_PCR),
			is(EnumSet.of(ResultValueType.QUALITATIVE, ResultValueType.NUMERIC)));
		assertThat(PathogenTestType.getResultValueTypes(PathogenTestType.SANGER_SEQUENCING), is(EnumSet.of(ResultValueType.TEXT)));
		assertThat(PathogenTestType.getResultValueTypes(PathogenTestType.ACID_FAST_STAIN), is(EnumSet.of(ResultValueType.SMEAR_GRADE)));
		assertThat(PathogenTestType.getResultValueTypes(PathogenTestType.LINE_PROBE_ASSAY), is(EnumSet.of(ResultValueType.BOOLEAN)));
		assertThat(
			PathogenTestType.getResultValueTypes(PathogenTestType.WESTERN_BLOT),
			is(EnumSet.of(ResultValueType.TEXT, ResultValueType.WESTERN_BLOT)));
		assertThat(PathogenTestType.getResultValueTypes(PathogenTestType.FLOW_CYTOMETRY), is(EnumSet.of(ResultValueType.NUMERIC)));
		// Reciprocal titres ('1:160') are recorded as text, not a Float numeric value.
		assertThat(PathogenTestType.getResultValueTypes(PathogenTestType.NEUTRALIZING_ANTIBODIES), is(EnumSet.of(ResultValueType.TEXT)));
		assertThat(PathogenTestType.getResultValueTypes(PathogenTestType.HEMAGGLUTINATION_INHIBITION), is(EnumSet.of(ResultValueType.TEXT)));
	}

	@Test
	public void everyMethodMapsToItsExpectedResultValueTypes() {
		// Exhaustive map of method -> result value type(s). Any method missing here, or whose annotation
		// drifts from this expectation, fails the test — so a new/changed @ResultValueTypeRel is noticed.
		Map<PathogenTestType, Set<ResultValueType>> expected = new EnumMap<>(PathogenTestType.class);

		// Qualitative only (Positive/Negative)
		for (PathogenTestType t : new PathogenTestType[] {
			PathogenTestType.MULTIPLEX_PCR,
			PathogenTestType.LAMP,
			PathogenTestType.NASBA,
			PathogenTestType.TMA,
			PathogenTestType.CRISPR_DIAGNOSTICS,
			PathogenTestType.DNA_MICROARRAY,
			PathogenTestType.RAPID_ANTIBODY_TEST,
			PathogenTestType.INDIRECT_FLUORESCENT_ANTIBODY,
			PathogenTestType.DIRECT_FLUORESCENT_ANTIBODY,
			PathogenTestType.RAPID_ANTIGEN_DETECTION,
			PathogenTestType.RAPID_TEST,
			PathogenTestType.LATERAL_FLOW_ASSAY,
			PathogenTestType.IMMUNOFLUORESCENCE_ASSAY,
			PathogenTestType.SLIDE_AGGLUTINATION,
			PathogenTestType.QUELLUNG_REACTION,
			PathogenTestType.RDT,
			PathogenTestType.VIRAL_ISOLATION,
			PathogenTestType.ISOLATION,
			PathogenTestType.DIRECT_MICROSCOPY,
			PathogenTestType.DARK_FIELD_MICROSCOPY,
			PathogenTestType.IMMUNOHISTOCHEMISTRY,
			PathogenTestType.ELECTRON_MICROSCOPY,
			PathogenTestType.IGRA,
			PathogenTestType.TST }) {
			expected.put(t, EnumSet.of(ResultValueType.QUALITATIVE));
		}

		// Text only (sequence / variant / organism / morphology / reciprocal titre)
		for (PathogenTestType t : new PathogenTestType[] {
			PathogenTestType.SANGER_SEQUENCING,
			PathogenTestType.WHOLE_GENOME_SEQUENCING,
			PathogenTestType.MULTILOCUS_SEQUENCE_TYPING,
			PathogenTestType.CGMLST,
			PathogenTestType.SNP_TYPING,
			PathogenTestType.SEROTYPING,
			PathogenTestType.SEROGROUPING,
			PathogenTestType.GENOTYPING,
			PathogenTestType.BEIJINGGENOTYPING,
			PathogenTestType.SPOLIGOTYPING,
			PathogenTestType.MIRU_PATTERN_CODE,
			PathogenTestType.NEUTRALIZING_ANTIBODIES,
			PathogenTestType.HEMAGGLUTINATION_INHIBITION,
			PathogenTestType.FUNGAL_CULTURE,
			PathogenTestType.MALDI_TOF,
			PathogenTestType.GRAM_STAIN,
			PathogenTestType.HISTOPATHOLOGY }) {
			expected.put(t, EnumSet.of(ResultValueType.TEXT));
		}

		// Qualitative + Numeric (Positive/Negative plus a Ct / titre / count)
		for (PathogenTestType t : new PathogenTestType[] {
			PathogenTestType.PCR_RT_PCR,
			PathogenTestType.Q_PCR,
			PathogenTestType.DIGITAL_PCR,
			PathogenTestType.NAAT,
			PathogenTestType.ENZYME_LINKED_IMMUNOSORBENT_ASSAY,
			PathogenTestType.IGM_SERUM_ANTIBODY,
			PathogenTestType.IGG_SERUM_ANTIBODY,
			PathogenTestType.IGA_SERUM_ANTIBODY,
			PathogenTestType.GIEMSA_STAIN }) {
			expected.put(t, EnumSet.of(ResultValueType.QUALITATIVE, ResultValueType.NUMERIC));
		}

		expected.put(PathogenTestType.LINE_PROBE_ASSAY, EnumSet.of(ResultValueType.BOOLEAN));
		expected.put(PathogenTestType.GENOTYPIC_RESISTANCE_TEST, EnumSet.of(ResultValueType.BOOLEAN));
		// Antibiotic Susceptibility Testing has no result value type of its own (its result is the
		// drug-susceptibility grid), so it maps to an empty set.
		expected.put(PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY, EnumSet.noneOf(ResultValueType.class));
		expected.put(PathogenTestType.FISH, EnumSet.of(ResultValueType.QUALITATIVE, ResultValueType.TEXT));
		expected.put(PathogenTestType.THICK_BLOOD_SMEAR, EnumSet.of(ResultValueType.QUALITATIVE, ResultValueType.TEXT));
		expected.put(PathogenTestType.WESTERN_BLOT, EnumSet.of(ResultValueType.TEXT, ResultValueType.WESTERN_BLOT));
		expected.put(PathogenTestType.BACTERIAL_CULTURE, EnumSet.of(ResultValueType.TEXT, ResultValueType.NUMERIC));
		expected.put(PathogenTestType.ACID_FAST_STAIN, EnumSet.of(ResultValueType.SMEAR_GRADE));
		expected
			.put(PathogenTestType.QUANTITATIVE_BUFFY_COAT, EnumSet.of(ResultValueType.QUALITATIVE, ResultValueType.TEXT, ResultValueType.NUMERIC));
		// Merged Culture entry: organism text + CFU count + Pos/Neg qualitative result.
		expected.put(PathogenTestType.CULTURE, EnumSet.of(ResultValueType.QUALITATIVE, ResultValueType.TEXT, ResultValueType.NUMERIC));
		expected.put(PathogenTestType.THIN_BLOOD_SMEAR, EnumSet.of(ResultValueType.NUMERIC, ResultValueType.TEXT));
		expected.put(PathogenTestType.FLOW_CYTOMETRY, EnumSet.of(ResultValueType.NUMERIC));

		expected.forEach(
			(type, valueTypes) -> assertThat("result value types for " + type.name(), PathogenTestType.getResultValueTypes(type), is(valueTypes)));

		// Every other method (legacy/hidden, the OTHER_* placeholders, OTHER) defaults to QUALITATIVE.
		for (PathogenTestType type : PathogenTestType.values()) {
			if (!expected.containsKey(type)) {
				assertThat(
					"unmapped method " + type.name() + " should default to QUALITATIVE",
					PathogenTestType.getResultValueTypes(type),
					is(EnumSet.of(ResultValueType.QUALITATIVE)));
			}
		}
	}

	@Test
	public void numericValueTypeIsAlwaysCombinedOrTextForTitres() {
		// A method that produces a numeric value either also has a qualitative component (Ct on a positive
		// PCR), is purely numeric (a true count like Flow Cytometry), or pairs with text (Bacterial
		// Culture organism + CFU). Reciprocal titres ('1:160') must NOT be NUMERIC — they are TEXT.
		assertThat(PathogenTestType.getResultValueTypes(PathogenTestType.NEUTRALIZING_ANTIBODIES), not(hasItem(ResultValueType.NUMERIC)));
		assertThat(PathogenTestType.getResultValueTypes(PathogenTestType.HEMAGGLUTINATION_INHIBITION), not(hasItem(ResultValueType.NUMERIC)));
		// The only purely-numeric method (no qualitative/text) is Flow Cytometry.
		List<PathogenTestType> pureNumeric = Arrays.stream(PathogenTestType.values())
			.filter(t -> PathogenTestType.getResultValueTypes(t).equals(EnumSet.of(ResultValueType.NUMERIC)))
			.collect(Collectors.toList());
		assertThat(pureNumeric, containsInAnyOrder(PathogenTestType.FLOW_CYTOMETRY));
	}

	@Test
	public void legacyTestTypeStillResolvesForExistingDiseaseConfiguration() {
		// SEQUENCING is a classification trigger for Invasive Pneumococcal Infection; it must remain a
		// visible value for that disease even though it is no longer selectable for brand-new tests.
		List<PathogenTestType> visibleForIpi = de.symeda.sormas.api.utils.Diseases.DiseasesConfiguration
			.getVisibleValues(PathogenTestType.class, Disease.INVASIVE_PNEUMOCOCCAL_INFECTION);
		assertThat(visibleForIpi, hasItem(PathogenTestType.SEQUENCING));
	}

	@Test
	public void diseaseHiddenLegacyMethodIsDroppedByVisibleValues() {
		// Guards the scenario behind the retainType re-add in FormComponent#updateTestTypeItemsByCategory
		// and the retainCategory re-add in #getVisibleTestCategories: a legacy method that is
		// @Diseases(hide = true) for a disease is excluded by getVisibleValues for that disease, so a
		// saved test recorded with it can only be kept selectable by adding it back explicitly.
		List<PathogenTestType> visibleForDengue =
			de.symeda.sormas.api.utils.Diseases.DiseasesConfiguration.getVisibleValues(PathogenTestType.class, Disease.DENGUE);
		assertFalse(
			visibleForDengue.contains(PathogenTestType.ANTIBODY_DETECTION),
			"ANTIBODY_DETECTION is hidden for DENGUE, so it must not be among the disease-visible values");
		// Its category is still resolvable from the method itself, independent of disease visibility.
		assertThat(PathogenTestType.getCategory(PathogenTestType.ANTIBODY_DETECTION), is(PathogenTestCategory.SEROLOGICAL_TESTS));
	}
}
