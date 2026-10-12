/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.symptoms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.utils.Complication;
import de.symeda.sormas.api.utils.DependantOn;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.HideForCountries;
import de.symeda.sormas.api.utils.HideForCountriesExcept;
import de.symeda.sormas.api.utils.Outbreaks;
import de.symeda.sormas.api.utils.SymptomGroup;
import de.symeda.sormas.api.utils.SymptomGrouping;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.DiseaseFieldVisibilityChecker;

/**
 * Verifies that {@link SymptomsDto} follows the symptom regrouping agreed in {@code symptom_grouping_mapping.json}
 * (exported from SymptomGroupingLatest.xlsx): every mapped symptom exists, has the proposed {@link SymptomGrouping}
 * and is visible for exactly the diseases listed for it.
 * <p>
 * Differences that are known and still to be decided are listed in the {@code KNOWN_*} constants. The tests fail when a
 * new difference appears, and also when a listed one has been resolved, so the lists stay accurate.
 * <p>
 * Symptoms marked "Remove" in the mapping are not deleted but deprecated (see {@link #DEPRECATED_SYMPTOMS}): they stay in
 * the DTO for older app versions and REST clients, are hidden for every disease, and their replacing symptom takes over the
 * diseases the mapping lists for them. Example: {@code jointPain} is listed for Measles, so {@code arthralgia} has to be
 * visible for Measles, while {@code jointPain} itself is hidden for it.
 */
class SymptomGroupingMappingTest {

    private static final String MAPPING_RESOURCE = "symptom_grouping_mapping.json";

    /**
     * Proposed grouping labels used in the mapping, mapped to the {@link SymptomGroup} the DTO field has to carry.
     */
    private static final Map<String, SymptomGroup> GROUP_LABELS = new HashMap<>();
    static {
        GROUP_LABELS.put("General", SymptomGroup.GENERAL);
        GROUP_LABELS.put("Respiratory", SymptomGroup.RESPIRATORY);
        GROUP_LABELS.put("Cardiovascular", SymptomGroup.CARDIOVASCULAR);
        GROUP_LABELS.put("Gastrointestinal", SymptomGroup.GASTROINTESTINAL);
        GROUP_LABELS.put("Urinary / Renal", SymptomGroup.URINARY);
        GROUP_LABELS.put("Nervous system", SymptomGroup.NERVOUS_SYSTEM);
        GROUP_LABELS.put("Skin", SymptomGroup.SKIN);
        GROUP_LABELS.put("Other", SymptomGroup.OTHER);
        GROUP_LABELS.put("Clinical assessment and course", SymptomGroup.CLINICAL_ASSESSMENT_AND_COURSE);
        GROUP_LABELS.put("Clinical measurements", SymptomGroup.CLINICAL_MEASUREMENTS);
        GROUP_LABELS.put("Clinical presentation", SymptomGroup.CLINICAL_PRESENTATION);
        GROUP_LABELS.put("Ear, Nose and Throat", SymptomGroup.ENT);
        GROUP_LABELS.put("Eye", SymptomGroup.EYE);
        GROUP_LABELS.put("Haemorrhagic / Haematological", SymptomGroup.HAEMATOLOGICAL);
        GROUP_LABELS.put("Metabolic", SymptomGroup.METABOLIC);
        GROUP_LABELS.put("Musculoskeletal", SymptomGroup.MUSCULOSKELETAL);
        GROUP_LABELS.put("Reproductive / Genital", SymptomGroup.GENITAL);
        GROUP_LABELS.put("Localisation", SymptomGroup.LOCALISATION);
        GROUP_LABELS.put("Stage", SymptomGroup.STAGE);
        GROUP_LABELS.put("system disorders", SymptomGroup.SYSTEM_DISORDER);
        GROUP_LABELS.put("Site of infection", SymptomGroup.SITE_OF_INFECTION);
        GROUP_LABELS.put("Concurrent sexually transmitted infection at time of diagnosis", SymptomGroup.CONCURRENT_STI);
    }

    /**
     * Proposed grouping labels meaning "this field is not rendered in any symptom group" (no {@link SymptomGrouping}).
     */
    private static final Set<String> NO_GROUP_LABELS = Set.of("N/A", "?", "move to epidata");

    /**
     * Labels of the grouping before the regrouping.
     */
    private static final Set<String> OLD_GROUP_LABELS = Set.of(
        "General",
        "Respiratory",
        "Cardiovascular",
        "Gastrointestinal",
        "Urinary",
        "Nervous system",
        "Skin",
        "Other",
        "Reproductive / Genital",
        "N/A",
        "Remove");

    /**
     * Symptoms whose DTO grouping differs from the proposed one, with the grouping the DTO currently has. Empty while the
     * DTO follows the mapping for every symptom.
     */
    private static final Map<String, SymptomGroup> KNOWN_GROUP_DEVIATIONS = Map.of();

    /**
     * Diseases the DTO shows a mapped symptom for, although the mapping does not list them. These diseases were added to
     * SORMAS after the spreadsheet was exported.
     */
    private static final Map<String, Set<Disease>> KNOWN_ADDITIONAL_DISEASES = new HashMap<>();
    static {
        KNOWN_ADDITIONAL_DISEASES.put(SymptomsDto.ARTHRALGIA, EnumSet.of(Disease.RUBELLA));
        KNOWN_ADDITIONAL_DISEASES.put(SymptomsDto.ARTHRITIS, EnumSet.of(Disease.GONOCOCCAL_INFECTION, Disease.RUBELLA));
        KNOWN_ADDITIONAL_DISEASES.put(SymptomsDto.CONJUNCTIVITIS, EnumSet.of(Disease.RUBELLA));
        KNOWN_ADDITIONAL_DISEASES.put(SymptomsDto.ENCEPHALITIS, EnumSet.of(Disease.CONGENITAL_RUBELLA, Disease.RUBELLA));
        KNOWN_ADDITIONAL_DISEASES.put(SymptomsDto.FEVER, EnumSet.of(Disease.RUBELLA));
        KNOWN_ADDITIONAL_DISEASES.put(SymptomsDto.HEPATOMEGALY, EnumSet.of(Disease.CONGENITAL_RUBELLA));
        KNOWN_ADDITIONAL_DISEASES.put(SymptomsDto.HEPATOSPLENOMEGALY, EnumSet.of(Disease.CONGENITAL_RUBELLA));
        KNOWN_ADDITIONAL_DISEASES.put(SymptomsDto.LYMPHADENOPATHY_CERVICAL, EnumSet.of(Disease.RUBELLA));
        KNOWN_ADDITIONAL_DISEASES.put(SymptomsDto.ONSET_DATE, EnumSet.of(Disease.GONOCOCCAL_INFECTION));
        KNOWN_ADDITIONAL_DISEASES.put(SymptomsDto.OTHER_CLINICAL_PRESENTATION, EnumSet.of(Disease.CONGENITAL_RUBELLA, Disease.GONOCOCCAL_INFECTION));
        KNOWN_ADDITIONAL_DISEASES
            .put(SymptomsDto.OTHER_CLINICAL_PRESENTATION_TEXT, EnumSet.of(Disease.CONGENITAL_RUBELLA, Disease.GONOCOCCAL_INFECTION));
        KNOWN_ADDITIONAL_DISEASES.put(SymptomsDto.OTHER_COMPLICATIONS, EnumSet.of(Disease.CONGENITAL_RUBELLA, Disease.RUBELLA));
        KNOWN_ADDITIONAL_DISEASES.put(SymptomsDto.OTHER_COMPLICATIONS_TEXT, EnumSet.of(Disease.CONGENITAL_RUBELLA, Disease.RUBELLA));
        KNOWN_ADDITIONAL_DISEASES.put(SymptomsDto.OTHER_GENERAL_SYMPTOMS, EnumSet.of(Disease.RUBELLA));
        KNOWN_ADDITIONAL_DISEASES.put(SymptomsDto.OTHER_GENERAL_SYMPTOMS_TEXT, EnumSet.of(Disease.RUBELLA));
        KNOWN_ADDITIONAL_DISEASES.put(SymptomsDto.OTHER_NEUROLOGICAL_SYMPTOMS, EnumSet.of(Disease.RUBELLA));
        KNOWN_ADDITIONAL_DISEASES.put(SymptomsDto.OTHER_NEUROLOGICAL_SYMPTOMS_TEXT, EnumSet.of(Disease.RUBELLA));
        KNOWN_ADDITIONAL_DISEASES.put(SymptomsDto.OTITIS_MEDIA, EnumSet.of(Disease.RUBELLA));
        KNOWN_ADDITIONAL_DISEASES.put(SymptomsDto.SKIN_RASH, EnumSet.of(Disease.RUBELLA));
        KNOWN_ADDITIONAL_DISEASES.put(SymptomsDto.SORE_THROAT, EnumSet.of(Disease.GONOCOCCAL_INFECTION));
        KNOWN_ADDITIONAL_DISEASES.put(SymptomsDto.UNKNOWN_SYMPTOM, EnumSet.of(Disease.GONOCOCCAL_INFECTION));
        // weightLoss is mapped to Syphilis, so its amount is shown there too
        KNOWN_ADDITIONAL_DISEASES.put(SymptomsDto.WEIGHT_LOSS_AMOUNT, EnumSet.of(Disease.SYPHILIS));
    }

    /**
     * Mapped symptoms that the DTO hides for diseases the mapping shows them for: either for some of the listed diseases, or,
     * for a symptom without a disease list ("N/A": shown for every disease), for some diseases at all.
     * <p>
     * Example: the mapping lists {@code onsetSymptom} for Diphtheria, but the first symptom dropdown is disabled for
     * Diphtheria, so the DTO hides it there.
     */
    private static final Map<String, Set<Disease>> KNOWN_HIDDEN_FOR_DISEASES = Map.of(
        SymptomsDto.ASYMPTOMATIC,
        EnumSet.of(Disease.TUBERCULOSIS),
        // the first symptom dropdown is disabled for Diphtheria
        SymptomsDto.ONSET_SYMPTOM,
        EnumSet.of(Disease.DIPHTHERIA));

    /**
     * Dependent fields that the DTO shows for a disease although their {@link DependantOn} parent is hidden for it.
     */
    private static final Map<String, Set<Disease>> KNOWN_DEPENDENTS_WITHOUT_PARENT =
        // Luxembourg Tuberculosis records the other clinical presentation as free text only
        Map.of(SymptomsDto.OTHER_CLINICAL_PRESENTATION_TEXT, EnumSet.of(Disease.TUBERCULOSIS));

    /**
     * DTO fields with a {@link SymptomGrouping} or {@link Diseases} annotation that are not part of the mapping yet. Empty
     * while every annotated field is mapped.
     */
    private static final Set<String> KNOWN_UNMAPPED_FIELDS = Set.of();

    /**
     * Diseases that no mapped symptom lists yet.
     */
    private static final Set<Disease> KNOWN_UNMAPPED_DISEASES = EnumSet.of(Disease.LATENT_TUBERCULOSIS);

    /**
     * #13399 - symptoms the mapping marks "Remove", by the symptom that replaces them. They are deprecated instead of deleted:
     * the field, its getter, setter and property constant stay, but the field is hidden for every disease
     * ({@code @Diseases({})}). The replacing symptom has to be visible for every disease the mapping lists for the deprecated
     * one.
     * <p>
     * Example: {@code conjunctivalInjection -> conjunctivitis}; the mapping lists conjunctivalInjection for COVID-19, so
     * conjunctivitis is checked to be visible for COVID-19.
     */
    private static final Map<String, String> DEPRECATED_SYMPTOMS = new LinkedHashMap<>();
    static {
        DEPRECATED_SYMPTOMS.put(SymptomsDto.ACUTE_ENCEPHALITIS, SymptomsDto.ENCEPHALITIS);
        DEPRECATED_SYMPTOMS.put(SymptomsDto.CONFUSION, SymptomsDto.CONFUSED_DISORIENTED);
        DEPRECATED_SYMPTOMS.put(SymptomsDto.CONJUNCTIVAL_INJECTION, SymptomsDto.CONJUNCTIVITIS);
        DEPRECATED_SYMPTOMS.put(SymptomsDto.CONVULSION, SymptomsDto.SEIZURES);
        DEPRECATED_SYMPTOMS.put(SymptomsDto.COUGH_WITH_HEAMOPTYSIS, SymptomsDto.COUGHING_BLOOD);
        DEPRECATED_SYMPTOMS.put(SymptomsDto.FEELING_ILL, SymptomsDto.MALAISE);
        DEPRECATED_SYMPTOMS.put(SymptomsDto.ALTERED_LEVEL_OF_CONSCIOUSNESS, SymptomsDto.ALTERED_CONSCIOUSNESS);
        DEPRECATED_SYMPTOMS.put(SymptomsDto.JOINT_PAIN, SymptomsDto.ARTHRALGIA);
        DEPRECATED_SYMPTOMS.put(SymptomsDto.MACULOPAPULAR_RASH, SymptomsDto.SKIN_RASH);
        DEPRECATED_SYMPTOMS.put(SymptomsDto.PALPABLE_LIVER, SymptomsDto.HEPATOMEGALY);
        DEPRECATED_SYMPTOMS.put(SymptomsDto.PALPABLE_SPLEEN, SymptomsDto.SPLENOMEGALY);
    }

    /**
     * #13399 - symptoms the mapping marks "move to epidata", by the {@link EpiDataDto} field that takes them over. Like the
     * {@link #DEPRECATED_SYMPTOMS} they stay deprecated and hidden in {@link SymptomsDto}; the epi data field has to be
     * visible for exactly the diseases the mapping lists for the symptom.
     * <p>
     * Example: {@code patientIllLocation -> patientIllLocation}; the mapping lists it for AFP, Guinea worm, Monkeypox, Polio,
     * Undefined and Other, so {@code EpiDataDto.patientIllLocation} is checked to be visible for these six and hidden for
     * all other diseases.
     */
    private static final Map<String, String> MOVED_TO_EPI_DATA = Map.of(SymptomsDto.PATIENT_ILL_LOCATION, EpiDataDto.PATIENT_ILL_LOCATION);

    /**
     * Annotations that control where and how a symptom is shown; a deprecated symptom must carry none of them, so that only
     * {@code @Diseases({})} decides that it is hidden.
     */
    private static final List<Class<? extends Annotation>> VISIBILITY_ANNOTATIONS =
        List.of(SymptomGrouping.class, Complication.class, Outbreaks.class, HideForCountries.class, HideForCountriesExcept.class, DependantOn.class);

    private static List<MappingEntry> mapping;
    private static Map<String, MappingEntry> mappingBySymptom;
    private static Map<String, Field> dtoFields;

    @BeforeAll
    static void loadMapping() throws IOException {

        try (InputStream in = SymptomGroupingMappingTest.class.getResourceAsStream(MAPPING_RESOURCE)) {
            assertNotNull(in, MAPPING_RESOURCE + " not found next to " + SymptomGroupingMappingTest.class.getSimpleName());
            mapping = new ObjectMapper().readValue(in, new TypeReference<List<MappingEntry>>() {
            });
        }

        mappingBySymptom = mapping.stream().collect(Collectors.toMap(e -> e.symptom, Function.identity(), (a, b) -> a, LinkedHashMap::new));

        dtoFields = Arrays.stream(SymptomsDto.class.getDeclaredFields())
            .filter(f -> !Modifier.isStatic(f.getModifiers()))
            .collect(Collectors.toMap(Field::getName, Function.identity()));
    }

    static Stream<Arguments> mappingEntries() throws IOException {
        loadMapping();
        return mapping.stream().map(e -> Arguments.of(Named.of(e.symptom, e)));
    }

    /**
     * Mapping entries of symptoms that are shown in the form, i.e. all entries except the {@link #DEPRECATED_SYMPTOMS} and
     * the symptoms {@link #MOVED_TO_EPI_DATA}.
     */
    static Stream<Arguments> activeMappingEntries() throws IOException {
        return mappingEntries().filter(a -> isShownInSymptomsForm(entryOf(a).symptom));
    }

    static Stream<Arguments> restrictedMappingEntries() throws IOException {
        return activeMappingEntries().filter(a -> !entryOf(a).diseases.isEmpty());
    }

    static Stream<Arguments> unrestrictedMappingEntries() throws IOException {
        return activeMappingEntries().filter(a -> entryOf(a).diseases.isEmpty());
    }

    static Stream<Arguments> deprecatedSymptoms() {
        return DEPRECATED_SYMPTOMS.keySet().stream().map(Arguments::of);
    }

    /**
     * The {@link #DEPRECATED_SYMPTOMS} and the symptoms {@link #MOVED_TO_EPI_DATA}: both stay in {@link SymptomsDto}, deprecated
     * and hidden for every disease.
     */
    static Stream<Arguments> deprecatedOrMovedSymptoms() {
        return Stream.concat(DEPRECATED_SYMPTOMS.keySet().stream(), MOVED_TO_EPI_DATA.keySet().stream()).map(Arguments::of);
    }

    static Stream<Arguments> movedSymptoms() {
        return MOVED_TO_EPI_DATA.keySet().stream().map(Arguments::of);
    }

    private static MappingEntry entryOf(Arguments arguments) {
        return (MappingEntry) ((Named<?>) arguments.get()[0]).getPayload();
    }

    // ---------------------------------------------------------------------------------------------------------------
    // Mapping file integrity
    // ---------------------------------------------------------------------------------------------------------------

    @Test
    void mappingIsNotEmpty() {
        assertFalse(mapping.isEmpty(), "The mapping must contain symptoms");
    }

    @Test
    void mappingHasNoDuplicateSymptoms() {
        Set<String> seen = new HashSet<>();
        Set<String> duplicates = mapping.stream().map(e -> e.symptom).filter(s -> !seen.add(s)).collect(Collectors.toCollection(TreeSet::new));
        assertTrue(duplicates.isEmpty(), "Symptoms listed more than once: " + duplicates);
    }

    @ParameterizedTest
    @MethodSource("mappingEntries")
    void entryHasAllAttributes(MappingEntry entry) {
        assertNotNull(entry.symptom, "symptom");
        assertFalse(entry.symptom.isBlank(), "symptom must not be blank");
        assertNotNull(entry.oldSymptomGroup, "oldSymptomGroup");
        assertNotNull(entry.proposedSymptomGroup, "proposedSymptomGroup");
        assertNotNull(entry.diseases, "diseases (use an empty list for symptoms shown for every disease)");
    }

    @ParameterizedTest
    @MethodSource("mappingEntries")
    void diseasesAreDiseaseConstants(MappingEntry entry) {
        Set<String> constants = Arrays.stream(Disease.values()).map(Enum::name).collect(Collectors.toSet());
        List<String> unknown = entry.diseases.stream().filter(d -> !constants.contains(d)).collect(Collectors.toList());
        assertTrue(unknown.isEmpty(), "Not a Disease constant: " + unknown);
    }

    @ParameterizedTest
    @MethodSource("mappingEntries")
    void diseasesAreListedOnce(MappingEntry entry) {
        assertEquals(new HashSet<>(entry.diseases).size(), entry.diseases.size(), "Duplicate diseases: " + entry.diseases);
    }

    @ParameterizedTest
    @MethodSource("mappingEntries")
    void oldGroupIsKnown(MappingEntry entry) {
        assertTrue(OLD_GROUP_LABELS.contains(entry.oldSymptomGroup.trim()), "Unknown old group: '" + entry.oldSymptomGroup + "'");
    }

    @ParameterizedTest
    @MethodSource("mappingEntries")
    void proposedGroupIsKnown(MappingEntry entry) {
        String label = entry.proposedSymptomGroup.trim();
        assertTrue(
            GROUP_LABELS.containsKey(label) || NO_GROUP_LABELS.contains(label),
            "Unknown proposed group: '" + entry.proposedSymptomGroup + "'");
    }

    @Test
    void everyDiseaseIsMappedToAtLeastOneSymptom() {
        Set<Disease> mapped = mapping.stream().flatMap(e -> e.diseases.stream()).map(Disease::valueOf).collect(Collectors.toSet());
        Set<Disease> unmapped =
            Arrays.stream(Disease.values()).filter(d -> !mapped.contains(d)).collect(Collectors.toCollection(() -> EnumSet.noneOf(Disease.class)));
        assertEquals(KNOWN_UNMAPPED_DISEASES, unmapped, "Diseases no mapped symptom lists (update KNOWN_UNMAPPED_DISEASES)");
    }

    // ---------------------------------------------------------------------------------------------------------------
    // Mapping vs. SymptomsDto: symptoms and groups
    // ---------------------------------------------------------------------------------------------------------------

    @ParameterizedTest
    @MethodSource("mappingEntries")
    void symptomIsSymptomsDtoField(MappingEntry entry) {
        assertTrue(dtoFields.containsKey(entry.symptom), "SymptomsDto has no field '" + entry.symptom + "'");
    }

    @ParameterizedTest
    @MethodSource("mappingEntries")
    void symptomHasPropertyConstant(MappingEntry entry) {
        boolean hasConstant = Arrays.stream(SymptomsDto.class.getDeclaredFields())
            .filter(f -> Modifier.isStatic(f.getModifiers()) && f.getType() == String.class)
            .anyMatch(f -> entry.symptom.equals(readStatic(f)));
        assertTrue(hasConstant, "SymptomsDto has no property name constant for '" + entry.symptom + "'");
    }

    @ParameterizedTest
    @MethodSource("activeMappingEntries")
    void dtoGroupMatchesProposedGroup(MappingEntry entry) {
        SymptomGroup expected = KNOWN_GROUP_DEVIATIONS.containsKey(entry.symptom)
            ? KNOWN_GROUP_DEVIATIONS.get(entry.symptom)
            : GROUP_LABELS.get(entry.proposedSymptomGroup.trim());
        assertEquals(expected, dtoGroup(entry.symptom), "@SymptomGrouping of " + entry.symptom);
    }

    @Test
    void knownGroupDeviationsStillDeviate() {
        KNOWN_GROUP_DEVIATIONS.forEach((symptom, dtoGroup) -> {
            MappingEntry entry = mappingBySymptom.get(symptom);
            assertNotNull(entry, symptom + " is not in the mapping any more - remove it from KNOWN_GROUP_DEVIATIONS");
            assertFalse(
                Objects.equals(GROUP_LABELS.get(entry.proposedSymptomGroup.trim()), dtoGroup),
                symptom + " now matches the mapping - remove it from KNOWN_GROUP_DEVIATIONS");
        });
    }

    @Test
    void annotatedDtoFieldsAreMapped() {
        Set<String> unmapped = dtoFields.values()
            .stream()
            .filter(f -> f.isAnnotationPresent(SymptomGrouping.class) || f.isAnnotationPresent(Diseases.class))
            .map(Field::getName)
            .filter(name -> !mappingBySymptom.containsKey(name))
            .collect(Collectors.toCollection(TreeSet::new));
        assertEquals(new TreeSet<>(KNOWN_UNMAPPED_FIELDS), unmapped, "Grouped fields missing from the mapping (update KNOWN_UNMAPPED_FIELDS)");
    }

    @Test
    void everyUsedGroupHasACaption() throws IOException {
        Properties captions = new Properties();
        try (InputStream in = SymptomGroupingMappingTest.class.getResourceAsStream("/enum.properties")) {
            assertNotNull(in, "enum.properties not found");
            captions.load(in);
        }

        Set<String> missing = dtoFields.values()
            .stream()
            .filter(f -> f.isAnnotationPresent(SymptomGrouping.class))
            .map(f -> f.getAnnotation(SymptomGrouping.class).value())
            .distinct()
            .map(group -> SymptomGroup.class.getSimpleName() + "." + group.name())
            .filter(key -> captions.getProperty(key, "").isBlank())
            .collect(Collectors.toCollection(TreeSet::new));
        assertTrue(missing.isEmpty(), "Missing captions in enum.properties: " + missing);
    }

    // ---------------------------------------------------------------------------------------------------------------
    // Mapping vs. SymptomsDto: diseases per symptom
    // ---------------------------------------------------------------------------------------------------------------

    @ParameterizedTest
    @MethodSource("restrictedMappingEntries")
    void symptomIsVisibleForEveryMappedDisease(MappingEntry entry) {
        Set<Disease> notVisible = mappedDiseases(entry).stream()
            .filter(d -> !isVisible(entry.symptom, d))
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(Disease.class)));
        assertEquals(
            KNOWN_HIDDEN_FOR_DISEASES.getOrDefault(entry.symptom, EnumSet.noneOf(Disease.class)),
            notVisible,
            entry.symptom + " is hidden for mapped diseases (update KNOWN_HIDDEN_FOR_DISEASES)");
    }

    @ParameterizedTest
    @MethodSource("restrictedMappingEntries")
    void symptomIsHiddenForDiseasesNotMapped(MappingEntry entry) {
        Set<Disease> mapped = mappedDiseases(entry);
        Set<Disease> additional = Arrays.stream(Disease.values())
            .filter(d -> !mapped.contains(d) && isVisible(entry.symptom, d))
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(Disease.class)));
        assertEquals(
            KNOWN_ADDITIONAL_DISEASES.getOrDefault(entry.symptom, EnumSet.noneOf(Disease.class)),
            additional,
            entry.symptom + " is visible for diseases the mapping does not list (update KNOWN_ADDITIONAL_DISEASES)");
    }

    @ParameterizedTest
    @MethodSource("unrestrictedMappingEntries")
    void symptomWithoutDiseasesIsVisibleForEveryDisease(MappingEntry entry) {
        Set<Disease> hidden = Arrays.stream(Disease.values())
            .filter(d -> !isVisible(entry.symptom, d))
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(Disease.class)));
        assertEquals(
            KNOWN_HIDDEN_FOR_DISEASES.getOrDefault(entry.symptom, EnumSet.noneOf(Disease.class)),
            hidden,
            entry.symptom + " has no diseases in the mapping, but is hidden for some (update KNOWN_HIDDEN_FOR_DISEASES)");
    }

    @Test
    void knownDiseaseDeviationsRefersToMappedSymptoms() {
        Stream.of(KNOWN_ADDITIONAL_DISEASES.keySet(), KNOWN_HIDDEN_FOR_DISEASES.keySet(), KNOWN_DEPENDENTS_WITHOUT_PARENT.keySet())
            .flatMap(Set::stream)
            .forEach(symptom -> assertTrue(mappingBySymptom.containsKey(symptom), symptom + " is not in the mapping any more"));
    }

    // ---------------------------------------------------------------------------------------------------------------
    // Mapping vs. SymptomsDto: symptoms per disease
    // ---------------------------------------------------------------------------------------------------------------

    @ParameterizedTest
    @EnumSource(Disease.class)
    void mappedSymptomsOfDiseaseAreVisible(Disease disease) {
        Set<String> hidden = mapping.stream()
            .filter(e -> isShownInSymptomsForm(e.symptom))
            .filter(e -> mapsToDisease(e, disease))
            .map(e -> e.symptom)
            .filter(symptom -> !KNOWN_HIDDEN_FOR_DISEASES.getOrDefault(symptom, Collections.emptySet()).contains(disease))
            .filter(symptom -> !isVisible(symptom, disease))
            .collect(Collectors.toCollection(TreeSet::new));
        assertTrue(hidden.isEmpty(), "Mapped to " + disease + " but hidden: " + hidden);
    }

    @ParameterizedTest
    @EnumSource(Disease.class)
    void visibleGroupedSymptomsOfDiseaseAreMapped(Disease disease) {
        Set<String> notMapped = dtoFields.values()
            .stream()
            .filter(f -> f.isAnnotationPresent(SymptomGrouping.class))
            .map(Field::getName)
            .filter(name -> isVisible(name, disease))
            .filter(name -> !KNOWN_UNMAPPED_FIELDS.contains(name))
            .filter(name -> !mapsToDisease(mappingBySymptom.get(name), disease))
            .filter(name -> !KNOWN_ADDITIONAL_DISEASES.getOrDefault(name, Collections.emptySet()).contains(disease))
            .collect(Collectors.toCollection(TreeSet::new));
        assertTrue(notMapped.isEmpty(), "Visible for " + disease + " but not mapped to it: " + notMapped);
    }

    // ---------------------------------------------------------------------------------------------------------------
    // SymptomsDto consistency the grouped symptoms form relies on
    // ---------------------------------------------------------------------------------------------------------------

    @Test
    void dependantOnParentsAreSymptomsDtoFields() {
        Set<String> missing = dtoFields.values()
            .stream()
            .filter(f -> f.isAnnotationPresent(DependantOn.class))
            .filter(f -> !dtoFields.containsKey(f.getAnnotation(DependantOn.class).value()))
            .map(f -> f.getName() + " -> " + f.getAnnotation(DependantOn.class).value())
            .collect(Collectors.toCollection(TreeSet::new));
        assertTrue(missing.isEmpty(), "@DependantOn parents that are not SymptomsDto fields: " + missing);
    }

    @ParameterizedTest
    @EnumSource(Disease.class)
    void dependentsAreOnlyVisibleWithTheirParent(Disease disease) {
        Set<String> orphans = dtoFields.values()
            .stream()
            .filter(f -> f.isAnnotationPresent(DependantOn.class))
            .filter(f -> isVisible(f.getName(), disease))
            .filter(f -> !isVisible(f.getAnnotation(DependantOn.class).value(), disease))
            .filter(f -> !KNOWN_DEPENDENTS_WITHOUT_PARENT.getOrDefault(f.getName(), Collections.emptySet()).contains(disease))
            .map(f -> f.getName() + " -> " + f.getAnnotation(DependantOn.class).value())
            .collect(Collectors.toCollection(TreeSet::new));
        assertTrue(orphans.isEmpty(), "Visible for " + disease + " while their parent is hidden: " + orphans);
    }

    @Test
    void diseasesAreDeclaredOncePerField() {
        Set<String> duplicates = dtoFields.values().stream().filter(f -> f.isAnnotationPresent(Diseases.class)).filter(f -> {
            Disease[] declared = f.getAnnotation(Diseases.class).value();
            // a HashSet instead of EnumSet.copyOf, which fails for the empty list of a deprecated symptom (@Diseases({}))
            return new HashSet<>(Arrays.asList(declared)).size() != declared.length;
        }).map(Field::getName).collect(Collectors.toCollection(TreeSet::new));
        assertTrue(duplicates.isEmpty(), "Fields declaring a disease twice in @Diseases: " + duplicates);
    }

    // ---------------------------------------------------------------------------------------------------------------
    // Deprecated symptoms (#13399): kept, hidden and replaced
    // ---------------------------------------------------------------------------------------------------------------

    @ParameterizedTest
    @MethodSource("deprecatedOrMovedSymptoms")
    void deprecatedSymptomIsKeptAndMarkedDeprecated(String symptom) {
        Field field = dtoFields.get(symptom);
        assertNotNull(field, symptom + " was deleted from SymptomsDto - it must only be deprecated");
        assertTrue(field.isAnnotationPresent(Deprecated.class), "Field " + symptom + " is not @Deprecated");

        String accessorSuffix = Character.toUpperCase(symptom.charAt(0)) + symptom.substring(1);
        Method getter = findMethod("get" + accessorSuffix);
        assertNotNull(getter, "SymptomsDto.get" + accessorSuffix + "() was deleted - it must only be deprecated");
        assertTrue(getter.isAnnotationPresent(Deprecated.class), "SymptomsDto.get" + accessorSuffix + "() is not @Deprecated");
        Method setter = findMethod("set" + accessorSuffix, field.getType());
        assertNotNull(setter, "SymptomsDto.set" + accessorSuffix + "(..) was deleted - it must only be deprecated");
        assertTrue(setter.isAnnotationPresent(Deprecated.class), "SymptomsDto.set" + accessorSuffix + "(..) is not @Deprecated");

        Field constant = propertyConstant(symptom);
        assertNotNull(constant, "The property name constant of " + symptom + " was deleted - it must only be deprecated");
        assertTrue(constant.isAnnotationPresent(Deprecated.class), "SymptomsDto." + constant.getName() + " is not @Deprecated");
    }

    @ParameterizedTest
    @MethodSource("deprecatedOrMovedSymptoms")
    void deprecatedSymptomIsHiddenForEveryDisease(String symptom) {
        Field field = dtoFields.get(symptom);
        assertNotNull(field, symptom + " was deleted from SymptomsDto - it must only be deprecated");

        Diseases diseases = field.getAnnotation(Diseases.class);
        assertNotNull(diseases, symptom + " needs @Diseases({}) - without @Diseases it is visible for every disease");
        assertEquals(0, diseases.value().length, symptom + " must declare no disease: @Diseases({})");
        assertFalse(diseases.hide(), symptom + " must not use @Diseases(hide = true), which shows it for all other diseases");

        Set<Disease> visible =
            Arrays.stream(Disease.values()).filter(d -> isVisible(symptom, d)).collect(Collectors.toCollection(() -> EnumSet.noneOf(Disease.class)));
        assertTrue(visible.isEmpty(), symptom + " is deprecated but visible for " + visible);

        List<String> leftOver =
            VISIBILITY_ANNOTATIONS.stream().filter(field::isAnnotationPresent).map(Class::getSimpleName).collect(Collectors.toList());
        assertTrue(leftOver.isEmpty(), symptom + " is deprecated but still has " + leftOver);
    }

    @ParameterizedTest
    @MethodSource("deprecatedSymptoms")
    void replacingSymptomIsVisibleForTheDiseasesOfTheDeprecatedOne(String symptom) {
        String replacing = DEPRECATED_SYMPTOMS.get(symptom);
        assertTrue(dtoFields.containsKey(replacing), "SymptomsDto has no field '" + replacing + "' to replace " + symptom);
        assertFalse(DEPRECATED_SYMPTOMS.containsKey(replacing), replacing + " replaces " + symptom + " but is deprecated itself");

        MappingEntry deprecatedEntry = mappingBySymptom.get(symptom);
        assertNotNull(deprecatedEntry, symptom + " is not in the mapping, so the diseases its replacement has to take over are unknown");
        Set<Disease> diseases = deprecatedEntry.diseases.isEmpty()
            ? EnumSet.allOf(Disease.class)
            : deprecatedEntry.diseases.stream().map(Disease::valueOf).collect(Collectors.toCollection(() -> EnumSet.noneOf(Disease.class)));
        Set<Disease> notCovered =
            diseases.stream().filter(d -> !isVisible(replacing, d)).collect(Collectors.toCollection(() -> EnumSet.noneOf(Disease.class)));
        assertTrue(notCovered.isEmpty(), replacing + " replaces " + symptom + " but is hidden for its diseases " + notCovered);
    }

    // ---------------------------------------------------------------------------------------------------------------
    // Symptoms moved to the epi data (#13399)
    // ---------------------------------------------------------------------------------------------------------------

    @ParameterizedTest
    @MethodSource("movedSymptoms")
    void movedSymptomIsMarkedMoveToEpiDataInTheMapping(String symptom) {
        MappingEntry entry = mappingBySymptom.get(symptom);
        assertNotNull(entry, symptom + " is not in the mapping, so the diseases of its epi data field are unknown");
        assertEquals("move to epidata", entry.proposedSymptomGroup.trim(), "Proposed group of " + symptom);
    }

    @ParameterizedTest
    @MethodSource("movedSymptoms")
    void epiDataFieldIsVisibleForExactlyTheMappedDiseases(String symptom) {
        String epiDataField = MOVED_TO_EPI_DATA.get(symptom);
        boolean hasField = Arrays.stream(EpiDataDto.class.getDeclaredFields())
            .anyMatch(f -> !Modifier.isStatic(f.getModifiers()) && f.getName().equals(epiDataField));
        assertTrue(hasField, "EpiDataDto has no field '" + epiDataField + "' to take over " + symptom);

        MappingEntry entry = mappingBySymptom.get(symptom);
        assertNotNull(entry, symptom + " is not in the mapping, so the diseases of its epi data field are unknown");
        Set<Disease> expected = entry.diseases.isEmpty()
            ? EnumSet.allOf(Disease.class)
            : entry.diseases.stream().map(Disease::valueOf).collect(Collectors.toCollection(() -> EnumSet.noneOf(Disease.class)));
        Set<Disease> visible = Arrays.stream(Disease.values())
            .filter(d -> new DiseaseFieldVisibilityChecker(d).isVisible(EpiDataDto.class, epiDataField))
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(Disease.class)));
        assertEquals(expected, visible, "EpiDataDto." + epiDataField + " takes over " + symptom + " but is visible for other diseases");
    }

    // ---------------------------------------------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------------------------------------------

    private static boolean isVisible(String symptom, Disease disease) {
        return new DiseaseFieldVisibilityChecker(disease).isVisible(SymptomsDto.class, symptom);
    }

    /**
     * Returns whether the symptom is still shown in the symptoms form, i.e. it is neither one of the
     * {@link #DEPRECATED_SYMPTOMS} nor {@link #MOVED_TO_EPI_DATA}. Example: {@code fever} returns {@code true},
     * {@code palpableSpleen} and {@code patientIllLocation} return {@code false}.
     */
    private static boolean isShownInSymptomsForm(String symptom) {
        return !DEPRECATED_SYMPTOMS.containsKey(symptom) && !MOVED_TO_EPI_DATA.containsKey(symptom);
    }

    private static boolean mapsToDisease(MappingEntry entry, Disease disease) {
        return entry != null && (entry.diseases.isEmpty() || mappedDiseases(entry).contains(disease));
    }

    /**
     * Returns the diseases a mapping entry stands for: its own diseases and, for a symptom that replaces
     * {@link #DEPRECATED_SYMPTOMS}, also the diseases the mapping lists for the deprecated ones. An entry without diseases
     * stands for every disease; for it the result is empty, as callers check {@code entry.diseases.isEmpty()} first.
     * <p>
     * Example: the mapping lists arthralgia for Syphilis and jointPain, which arthralgia replaces, for Measles, Dengue and
     * others, so the result for arthralgia is Syphilis, Measles, Dengue and the other diseases of jointPain.
     *
     * @param entry
     *            the mapping entry; {@code null} returns an empty set
     * @return the diseases the entry stands for
     */
    private static Set<Disease> mappedDiseases(MappingEntry entry) {
        Set<Disease> diseases = EnumSet.noneOf(Disease.class);
        if (entry == null || entry.diseases.isEmpty()) {
            return diseases;
        }
        entry.diseases.stream().map(Disease::valueOf).forEach(diseases::add);
        DEPRECATED_SYMPTOMS.forEach((deprecated, replacing) -> {
            MappingEntry deprecatedEntry = mappingBySymptom.get(deprecated);
            if (replacing.equals(entry.symptom) && deprecatedEntry != null) {
                deprecatedEntry.diseases.stream().map(Disease::valueOf).forEach(diseases::add);
            }
        });
        return diseases;
    }

    private static Method findMethod(String name, Class<?>... parameterTypes) {
        try {
            return SymptomsDto.class.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static Field propertyConstant(String symptom) {
        return Arrays.stream(SymptomsDto.class.getDeclaredFields())
            .filter(f -> Modifier.isStatic(f.getModifiers()) && f.getType() == String.class)
            .filter(f -> symptom.equals(readStatic(f)))
            .findFirst()
            .orElse(null);
    }

    private static SymptomGroup dtoGroup(String symptom) {
        Field field = dtoFields.get(symptom);
        return field != null && field.isAnnotationPresent(SymptomGrouping.class) ? field.getAnnotation(SymptomGrouping.class).value() : null;
    }

    private static Object readStatic(Field field) {
        try {
            return field.get(null);
        } catch (IllegalAccessException e) {
            fail("Cannot read " + field.getName(), e);
            return null;
        }
    }

    static class MappingEntry {

        public String symptom;
        public String oldSymptomGroup;
        public String proposedSymptomGroup;
        public List<String> diseases;

        @Override
        public String toString() {
            return symptom;
        }
    }
}
