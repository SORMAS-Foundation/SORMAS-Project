package de.symeda.sormas.api.sample;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.LegacyEnumNames;

public class PathogenTestTypeTest {

	/**
	 * Legacy / merged methods that must stay in the enum (still referenced by classification, mapping
	 * and existing records) but must no longer be offered when adding a new test.
	 */
	private static final Set<PathogenTestType> EXPECTED_LEGACY = EnumSet.of(
		PathogenTestType.INCUBATION_TIME,
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
			PathogenTestType.LATERAL_FLOW_ASSAY,
			PathogenTestType.CULTURE,
			PathogenTestType.ISOLATION,
			PathogenTestType.IGM_SERUM_ANTIBODY,
			PathogenTestType.IGG_SERUM_ANTIBODY,
			PathogenTestType.IGA_SERUM_ANTIBODY,
			PathogenTestType.MALDI_TOF,
			PathogenTestType.ACID_FAST_STAIN,
			PathogenTestType.GENOTYPIC_RESISTANCE_TEST,
			PathogenTestType.FLOW_CYTOMETRY,
			PathogenTestType.LATEX_AGGLUTINATION,
			PathogenTestType.RSV_SUBTYPING,
			// Legacy flag lifted: both are offered for new tests again.
			PathogenTestType.ANTIBODY_DETECTION,
			PathogenTestType.MICROSCOPY);
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
		assertThat(PathogenTestType.getCategory(PathogenTestType.LATERAL_FLOW_ASSAY), is(PathogenTestCategory.ANTIGEN_DETECTION));
		assertThat(PathogenTestType.getCategory(PathogenTestType.CULTURE), is(PathogenTestCategory.CULTURE_AND_ISOLATION));
		assertThat(PathogenTestType.getCategory(PathogenTestType.ISOLATION), is(PathogenTestCategory.CULTURE_AND_ISOLATION));
		assertThat(PathogenTestType.getCategory(PathogenTestType.MICROSCOPY), is(PathogenTestCategory.MICROSCOPY_AND_STAINING));
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
			PathogenTestType.ANTIBODY_DETECTION,
			PathogenTestType.INDIRECT_FLUORESCENT_ANTIBODY,
			PathogenTestType.DIRECT_FLUORESCENT_ANTIBODY,
			PathogenTestType.LATERAL_FLOW_ASSAY,
			PathogenTestType.IMMUNOFLUORESCENCE_ASSAY,
			PathogenTestType.SLIDE_AGGLUTINATION,
			PathogenTestType.QUELLUNG_REACTION,
			PathogenTestType.VIRAL_ISOLATION,
			PathogenTestType.ISOLATION,
			PathogenTestType.MICROSCOPY,
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
			PathogenTestType.FUNGAL_CULTURE,
			PathogenTestType.MALDI_TOF,
			PathogenTestType.GRAM_STAIN }) {
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
		// Positive/Negative interpretation plus free-text tissue-pathology description (#14096).
		expected.put(PathogenTestType.HISTOPATHOLOGY, EnumSet.of(ResultValueType.QUALITATIVE, ResultValueType.TEXT));
		// Positive/Negative interpretation plus the reciprocal titre as text (#14105).
		expected.put(PathogenTestType.HEMAGGLUTINATION_INHIBITION, EnumSet.of(ResultValueType.QUALITATIVE, ResultValueType.TEXT));
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
		// PCR), is purely numeric (a true count like Flow Cytometry), or pairs with text (Bacterial Culture
		// organism + CFU). Reciprocal titres ('1:160') must NOT be NUMERIC — they are TEXT.
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
	public void diseaseHiddenMethodIsDroppedByVisibleValues() {
		// Guards the scenario behind the retainType re-add in FormComponent#updateTestTypeItemsByCategory
		// and the retainCategory re-add in #getVisibleTestCategories: a method that is @Diseases(hide = true)
		// for a disease is excluded by getVisibleValues for that disease, so a saved test recorded with it can
		// only be kept selectable by adding it back explicitly. ANTIBODY_DETECTION carries such a hide-list
		// covering DENGUE. INCUBATION_TIME is the same case for a method that is also legacy.
		List<PathogenTestType> visibleForDengue =
			de.symeda.sormas.api.utils.Diseases.DiseasesConfiguration.getVisibleValues(PathogenTestType.class, Disease.DENGUE);
		assertFalse(
			visibleForDengue.contains(PathogenTestType.ANTIBODY_DETECTION),
			"ANTIBODY_DETECTION is @Diseases(hide = true) for DENGUE, so it must not be among the disease-visible values");
		assertFalse(
			visibleForDengue.contains(PathogenTestType.INCUBATION_TIME),
			"INCUBATION_TIME is a legacy method hidden for DENGUE, so it must not be among the disease-visible values");
		// Its category is still resolvable from the method itself, independent of disease visibility.
		assertThat(PathogenTestType.getCategory(PathogenTestType.ANTIBODY_DETECTION), is(PathogenTestCategory.SEROLOGICAL_TESTS));
	}

	@Test
	public void lateralFlowAssayCoversTheDiseasesOfTheMethodsMergedIntoIt() {
		// ANTIGEN_DETECTION, RAPID_TEST, RAPID_ANTIGEN_DETECTION and RDT were merged into LATERAL_FLOW_ASSAY
		// and their records migrated onto it, so it must be visible everywhere they collectively were.
		for (Disease disease : new Disease[] {
			Disease.CORONAVIRUS,
			Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
			Disease.MEASLES,
			Disease.GIARDIASIS,
			Disease.SALMONELLOSIS,
			Disease.MALARIA,
			Disease.DENGUE }) {
			assertThat(
				"LATERAL_FLOW_ASSAY must be visible for " + disease.name(),
				de.symeda.sormas.api.utils.Diseases.DiseasesConfiguration.getVisibleValues(PathogenTestType.class, disease),
				hasItem(PathogenTestType.LATERAL_FLOW_ASSAY));
		}
		// SHIGELLOSIS is the one disease all four merged methods excluded, and the only entry in the new hide-list.
		assertThat(
			de.symeda.sormas.api.utils.Diseases.DiseasesConfiguration.getVisibleValues(PathogenTestType.class, Disease.SHIGELLOSIS),
			not(hasItem(PathogenTestType.LATERAL_FLOW_ASSAY)));
	}

	@Test
	public void retiredMethodNamesStillResolveToTheMethodThatAbsorbedThem() {
		// The retired names still arrive from un-upgraded peers, external messages and old CSVs, which no
		// database migration can reach. They must translate rather than throw.
		assertThat(PathogenTestType.fromLegacyName("ANTIGEN_DETECTION"), is(PathogenTestType.LATERAL_FLOW_ASSAY));
		assertThat(PathogenTestType.fromLegacyName("RAPID_TEST"), is(PathogenTestType.LATERAL_FLOW_ASSAY));
		assertThat(PathogenTestType.fromLegacyName("RAPID_ANTIGEN_DETECTION"), is(PathogenTestType.LATERAL_FLOW_ASSAY));
		assertThat(PathogenTestType.fromLegacyName("RDT"), is(PathogenTestType.LATERAL_FLOW_ASSAY));
		// No successor: these keep their caption in the free-text field rather than adopting a method that is
		// scoped differently and drives classification rules they never satisfied.
		assertThat(PathogenTestType.fromLegacyName("DIRECT_MICROSCOPY"), is(PathogenTestType.OTHER));
		assertThat(PathogenTestType.fromLegacyName("RAPID_ANTIBODY_TEST"), is(PathogenTestType.OTHER));
		// Current names are unaffected; a blank or unknown name fails loudly rather than deserializing to null.
		assertThat(PathogenTestType.fromLegacyName("PCR_RT_PCR"), is(PathogenTestType.PCR_RT_PCR));
		assertThrows(IllegalArgumentException.class, () -> PathogenTestType.fromLegacyName(" "));
		assertThrows(IllegalArgumentException.class, () -> PathogenTestType.fromLegacyName("NOT_A_REAL_TYPE"));
	}

	@Test
	public void everyRetiredNameIsDeclaredOnTheConstantThatAbsorbedIt() throws NoSuchFieldException {
		// @LegacyEnumNames is the Java source of truth for the retired names. That it agrees with the two SQL
		// migrations is asserted by PathogenTestTypeRetirementTest, which reads them.
		Set<String> declared = new java.util.HashSet<>();
		for (PathogenTestType type : PathogenTestType.values()) {
			LegacyEnumNames annotation = PathogenTestType.class.getField(type.name()).getAnnotation(LegacyEnumNames.class);
			if (annotation != null) {
				declared.addAll(Arrays.asList(annotation.value()));
			}
		}
		assertThat(
			declared,
			containsInAnyOrder("ANTIGEN_DETECTION", "RAPID_TEST", "RAPID_ANTIGEN_DETECTION", "RDT", "DIRECT_MICROSCOPY", "RAPID_ANTIBODY_TEST"));

		// and none of them is a live constant again
		Set<String> live = Arrays.stream(PathogenTestType.values()).map(Enum::name).collect(Collectors.toSet());
		for (String retired : declared) {
			assertFalse(live.contains(retired), retired + " was retired and must not be re-added without a migration");
		}
	}

	@Test
	public void jacksonTranslatesRetiredNamesAndRejectsBadOnes() throws Exception {
		// This is the production path: an un-upgraded sormas-to-sormas peer, an external lab message or a REST
		// client sends a retired name, and no database migration can reach it. Exercising fromLegacyName directly
		// would not prove @JsonCreator is bound.
		ObjectMapper mapper = new ObjectMapper();
		assertThat(mapper.readValue("\"RAPID_TEST\"", PathogenTestType.class), is(PathogenTestType.LATERAL_FLOW_ASSAY));
		assertThat(mapper.readValue("\"DIRECT_MICROSCOPY\"", PathogenTestType.class), is(PathogenTestType.OTHER));
		assertThat(mapper.readValue("\"PCR_RT_PCR\"", PathogenTestType.class), is(PathogenTestType.PCR_RT_PCR));
		assertNull(mapper.readValue("null", PathogenTestType.class));

		// a blank or unknown value must still be rejected, not coerced to null
		assertThrows(ValueInstantiationException.class, () -> mapper.readValue("\"\"", PathogenTestType.class));
		assertThrows(ValueInstantiationException.class, () -> mapper.readValue("\"NOT_A_REAL_TYPE\"", PathogenTestType.class));

		// serialization is still by name()
		assertThat(mapper.writeValueAsString(PathogenTestType.LATERAL_FLOW_ASSAY), is("\"LATERAL_FLOW_ASSAY\""));
	}

	@Test
	public void otherAbsorbedTheSuccessorlessMethodsAndStaysVisibleEverywhere() {
		// DIRECT_MICROSCOPY and RAPID_ANTIBODY_TEST migrate onto OTHER, which carries no @Diseases, so a migrated
		// record renders for every disease. The LATERAL_FLOW_ASSAY merge has the same guard above.
		for (Disease disease : Disease.values()) {
			assertThat(
				"OTHER must be visible for " + disease.name(),
				de.symeda.sormas.api.utils.Diseases.DiseasesConfiguration.getVisibleValues(PathogenTestType.class, disease),
				hasItem(PathogenTestType.OTHER));
		}
	}
}
