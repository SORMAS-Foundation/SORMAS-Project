/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 */

package de.symeda.sormas.ui.symptoms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.symptoms.ClinicalManifestation;
import de.symeda.sormas.api.symptoms.CongenitalHeartDiseaseType;
import de.symeda.sormas.api.symptoms.DiagnosisType;
import de.symeda.sormas.api.symptoms.InfectionSite;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.DependantOn;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.utils.SymptomGroup;
import de.symeda.sormas.api.utils.SymptomGrouping;
import de.symeda.sormas.api.utils.YesNoUnknown;

/**
 * Builds the grouped layout of the {@link SymptomsForm}: the visible symptom fields are placed in one section per
 * {@link SymptomGroup}, two fields per row, and every field that is {@link DependantOn} another field is shown below its parent
 * only while the parent has a "show" value.
 * <h2>How the layout is built</h2>
 * <ol>
 * <li><b>Collect</b> - only the fields that are visible when {@link #build} is called are laid out. They are ordered by the
 * {@code Order} annotation of their getter in {@link SymptomsDto} (fields without one come last, in declaration order) and
 * grouped by the {@link SymptomGrouping} of their DTO field. Fields without a group are not laid out by this class.</li>
 * <li><b>Group sections</b> - for every group with visible fields a section is created in the order of
 * {@link #GROUP_DISPLAY_ORDER}: a title, optional extra components (the symptoms hint and the clear/set buttons for
 * {@link SymptomGroup#CLINICAL_MEASUREMENTS}) and an empty rows container. All sections exist before any field is placed, so
 * a dependent can always be put into another group.</li>
 * <li><b>Dependents</b> - the visible {@link DependantOn} children of each field are collected. A child is never placed as a
 * top-level field, it goes into a slot instead:
 * <ul>
 * <li>child in the same group as its parent: a slot directly below the parent's row,</li>
 * <li>child in another group: the "extras" slot at the end of the child's own group,</li>
 * <li>child of a child (nested dependent): the slot of its parent, directly after the parent.</li>
 * </ul>
 * </li>
 * <li><b>Rows</b> - the remaining fields of each group are placed two per row. A {@link TextArea} gets a row of its own, and a
 * single field in the last row is followed by a spacer, so it keeps its half width.</li>
 * <li><b>Slots</b> - every slot is created hidden and attached exactly once: the slot of a parent directly below the parent's
 * row, an extras slot at the end of its group after all groups are populated. Showing or hiding dependents later only fills
 * and shows or empties and hides a slot; it never moves it.</li>
 * </ol>
 * <h2>Showing and hiding dependents</h2>
 * A parent shows its children while its value is {@code YES} ({@link SymptomState} or {@link YesNoUnknown}); drop-downs like
 * {@code clinicalManifestation}, {@code majorSite} or {@code congenitalHeartDiseaseType} use the conditions in
 * {@link #SHOW_CONDITION_BY_PARENT} instead. Whenever a parent value changes, only the slots of its children are rebuilt: the shown
 * children are paired into rows again (text fields and fields with a companion component, e.g. a monkeypox picture, get a row of
 * their own), a slot without shown children is hidden, and a group whose rows hold no visible field is hidden. A child hidden by
 * a user change is cleared, so no stale value is saved. The field objects themselves are never recreated, so their values and
 * listeners are kept.
 * <h2>Example</h2>
 * A Diphtheria case shows these {@link SymptomGroup#CLINICAL_PRESENTATION} fields, ordered by their {@code Order} value;
 * {@code otherClinicalCriteriaText} is {@code DependantOn(OTHER_CLINICAL_CRITERIA)}:
 *
 * <pre>
 * asymptomatic, conjunctivalPresentation, cutaneousPresentation, genitalPresentation, nasalDischargeClearToBloody,
 * otherClinicalCriteria, otherClinicalCriteriaText, respiratoryAndCutaneousPresentation,
 * respiratoryWithMembrane, respiratoryWithoutMembrane, unknownClinicalCriteria
 * </pre>
 *
 * They are laid out as follows; the text field is not paired, because it is a dependent, and it is only shown while
 * "Other clinical criteria" is set to Yes:
 *
 * <pre>
 * Clinical Presentation
 * +-------------------------------------+-------------------------------------+
 * | Asymptomatic                        | Conjunctival presentation           |
 * | Cutaneous presentation              | Genital presentation                |
 * | Uni- or bilateral nasal discharge   | Other clinical criteria             |
 * +-------------------------------------+-------------------------------------+
 * | Specify other clinical criteria   (slot of "Other", only while Yes)       |
 * +-------------------------------------+-------------------------------------+
 * | Respiratory and cutaneous present.  | Classic respiratory with membrane   |
 * | Respiratory with no membrane        | Unknown                             |
 * +-------------------------------------+-------------------------------------+
 * </pre>
 *
 * Other cases handled the same way:
 * <ul>
 * <li>cross-group: {@code jaundiceWithin24HoursOfBirth} (METABOLIC) depends on {@code jaundice} (GASTROINTESTINAL), so it is
 * shown in the extras slot at the end of the Metabolic group,</li>
 * <li>nested: {@code otherHemorrhagicSymptomsText} depends on {@code otherHemorrhagicSymptoms}, which depends on
 * {@code unexplainedBleeding}; the text field is shown directly after its parent, and only while both parents are Yes.</li>
 * </ul>
 * <h2>Usage</h2>
 * Create one instance per form and call {@link #build} once, after all fields are added and their initial visibilities are
 * set:
 *
 * <pre>
 * {@code
 * new SymptomGroupLayout(mainLayout, getContent(), dependentCompanions).build(getFieldGroup().getFields(), symptomsHint, buttonLayout);
 * }
 * </pre>
 *
 * A field that is hidden when {@link #build} runs is never placed in the layout, so making it visible later (e.g. with
 * {@code FieldHelper.setVisibleWhen}) has no effect. Show and hide dependents through {@link DependantOn} instead.
 */
final class SymptomGroupLayout {

	/**
	 * Order of the group sections in the form, from top to bottom. Only groups with visible fields get a section, so the
	 * order of the remaining groups is kept when a group is left out. A group that is not listed here sorts before all listed
	 * groups, because its index is -1.
	 * <p>
	 * Example: a Diphtheria case with visible fields in GENERAL, RESPIRATORY, CLINICAL_PRESENTATION and SYSTEM_DISORDER shows
	 * the sections in this order:
	 *
	 * <pre>
	 * 1. Clinical Presentation  2. General  3. Respiratory  4. Clinical diagnosis/disorder
	 * </pre>
	 */
	private static final List<SymptomGroup> GROUP_DISPLAY_ORDER = List.of(
		SymptomGroup.CLINICAL_MEASUREMENTS,
		SymptomGroup.LOCALISATION,
		SymptomGroup.CLINICAL_PRESENTATION,
		SymptomGroup.GENERAL,
		SymptomGroup.RESPIRATORY,
		SymptomGroup.CARDIOVASCULAR,
		SymptomGroup.GASTROINTESTINAL,
		SymptomGroup.URINARY,
		SymptomGroup.NERVOUS_SYSTEM,
		SymptomGroup.SKIN,
		SymptomGroup.ENT,
		SymptomGroup.EYE,
		SymptomGroup.HAEMATOLOGICAL,
		SymptomGroup.METABOLIC,
		SymptomGroup.MUSCULOSKELETAL,
		SymptomGroup.GENITAL,
		SymptomGroup.STAGE,
		SymptomGroup.SITE_OF_INFECTION,
		SymptomGroup.CONCURRENT_STI,
		SymptomGroup.SYSTEM_DISORDER,
		SymptomGroup.OTHER,
		SymptomGroup.CLINICAL_ASSESSMENT_AND_COURSE);

	/**
	 * The group of a visible top-level field that has no {@link SymptomGrouping}; see {@link #fallbackGroup}. Example: a
	 * field that is added to {@link SymptomsDto} without {@code @SymptomGrouping} is shown in the "Other" section.
	 */
	private static final SymptomGroup FALLBACK_GROUP = SymptomGroup.OTHER;

	/**
	 * The {@link SymptomGrouping} of every {@link SymptomsDto} field that has one, by field name. Read once by reflection when
	 * the class is loaded, so opening a form needs no reflection. A field without the annotation is not in the map; as a
	 * top-level field it is placed in {@link #FALLBACK_GROUP}, as a dependent in its parent's group. The map is immutable.
	 * <p>
	 * Example: {@code SYMPTOM_GROUP_BY_FIELD.get("jaundice")} is {@code GASTROINTESTINAL},
	 * {@code SYMPTOM_GROUP_BY_FIELD.get("otherClinicalCriteriaText")} is {@code CLINICAL_PRESENTATION}.
	 */
	private static final Map<String, SymptomGroup> SYMPTOM_GROUP_BY_FIELD;

	/**
	 * The position of every {@link SymptomsDto} field in the form, by field name; {@link #build} sorts the visible fields by
	 * it. The position is the {@link Order} value of the field's getter ({@code get...} or {@code is...}). A field whose getter
	 * has no {@code @Order} gets {@code Integer.MAX_VALUE - fieldCount + declarationIndex}, so all of them come after every
	 * ordered field and keep their declaration order among themselves. The map is immutable.
	 * <p>
	 * Example: {@code getTemperature()} has {@code @Order(0)}, {@code getAsymptomatic()} {@code @Order(25)} and
	 * {@code getOtherClinicalCriteria()} {@code @Order(520)}, so {@code FIELD_POSITION} maps {@code "temperature" -> 0},
	 * {@code "asymptomatic" -> 25} and {@code "otherClinicalCriteria" -> 520}, and Temperature is laid out before Asymptomatic.
	 */
	private static final Map<String, Integer> FIELD_POSITION;

	static {
		Map<String, Integer> getterOrder = Arrays.stream(SymptomsDto.class.getDeclaredMethods())
			.filter(method -> method.getParameterCount() == 0 && method.isAnnotationPresent(Order.class))
			.collect(Collectors.toMap(java.lang.reflect.Method::getName, method -> method.getAnnotation(Order.class).value()));

		List<java.lang.reflect.Field> dtoFields = Arrays.stream(SymptomsDto.class.getDeclaredFields())
			.filter(field -> !java.lang.reflect.Modifier.isStatic(field.getModifiers()))
			.collect(Collectors.toList());

		SYMPTOM_GROUP_BY_FIELD = Collections.unmodifiableMap(
			dtoFields.stream()
				.filter(field -> field.isAnnotationPresent(SymptomGrouping.class))
				.collect(Collectors.toMap(java.lang.reflect.Field::getName, field -> field.getAnnotation(SymptomGrouping.class).value())));

		// fields whose getter has no @Order are placed after all ordered ones, in declaration order
		FIELD_POSITION = Collections.unmodifiableMap(
			IntStream.range(0, dtoFields.size())
				.boxed()
				.collect(
					Collectors.toMap(
						i -> dtoFields.get(i).getName(),
						i -> Stream.of("get", "is")
							.map(prefix -> getterOrder.get(prefix + StringUtils.capitalize(dtoFields.get(i).getName())))
							.filter(Objects::nonNull)
							.findFirst()
							.orElse(Integer.MAX_VALUE - dtoFields.size() + i))));
	}

	/**
	 * The "show" conditions of the drop-down parents, by the property id of the parent. A parent listed here shows its
	 * dependents when its condition matches the value; every other parent uses the Yes rule of {@link #isYes}. The map is
	 * immutable.
	 * <p>
	 * Example: {@code SHOW_CONDITION_BY_PARENT.get(SymptomsDto.MAJOR_SITE).test(InfectionSite.OTHER)} is true, so its dependent
	 * {@code otherMajorSiteDetails} is shown; for {@code InfectionSite.GENITO_URINARY} it is false.
	 */
	private static final Map<String, Predicate<Object>> SHOW_CONDITION_BY_PARENT = Map.of(
		SymptomsDto.CLINICAL_MANIFESTATION,
		value -> value instanceof Set<?> ? ((Set<?>) value).contains(ClinicalManifestation.OTHER) : value == ClinicalManifestation.OTHER,
		SymptomsDto.DIAGNOSIS,
		value -> value == DiagnosisType.EXTRAPULMONARY,
		SymptomsDto.MINOR_SITE,
		value -> value == InfectionSite.OTHER,
		SymptomsDto.MAJOR_SITE,
		value -> value == InfectionSite.OTHER,
		SymptomsDto.CONGENITAL_HEART_DISEASE_TYPE,
		value -> value == CongenitalHeartDiseaseType.OTHER);

	/**
	 * The vertical layout of the form that receives all group sections, one below the other, in the order they are created
	 * ({@link #GROUP_DISPLAY_ORDER}). It is created by {@code SymptomsForm.initContent()}; the form adds the symptoms hint and the
	 * clear/set buttons to it before {@link #build} appends the sections. If the Clinical Measurements group has visible fields,
	 * {@link #createGroupSection} moves the hint and the buttons below its title; otherwise they stay at the top.
	 * <p>
	 * Example: Diphtheria hides the clinical measurements, so after building a Diphtheria form it holds
	 * {@code [symptoms hint, buttons, Clinical Presentation section, General section, Respiratory section, ...]}. For a disease
	 * with a visible temperature it holds {@code [Clinical Measurements section (with hint and buttons), ...]}.
	 */
	private final VerticalLayout mainLayout;

	/**
	 * The form's {@link CustomLayout}. {@code addField()} adds every field to it under its property id, but the template
	 * ({@code <div location="content"></div>}) only defines the {@code content} location, so a field parked there is never
	 * rendered. A dependent field has to be removed
	 * from it before it is placed in a slot; otherwise its first render happens off-layout and a ComboBox keeps a wrong
	 * width.
	 * <p>
	 * Example: {@code otherClinicalCriteriaText} is still parked in this layout until {@link #wireDependents} removes it and
	 * registers it in the slot of {@code otherClinicalCriteria}.
	 */
	private final CustomLayout parkingLayout;

	/**
	 * Components shown in the same row as a dependent field, by the property id of the field. They are provided by the form,
	 * because only the form knows which pictures belong to which field. A field with a companion gets a row of its own, with
	 * the companion as the second column.
	 * <p>
	 * Example: for Monkeypox the form puts {@code "lesionsResembleImg1" -> Image(img/monkeypox-lesions-1.png)} into the map
	 * (and the same for pictures 2 to 4), so "Does the rash resemble the photo below?" is shown next to the first picture.
	 */
	private final Map<String, Component> dependentCompanions;

	/**
	 * The rows container of each group section, by group. Created by {@link #createGroupSection} and filled by
	 * {@link #populateGroupRows} and the dependent slots. It is also used to check whether a group already has a section,
	 * so no group gets two sections.
	 * <p>
	 * Example: {@code CLINICAL_PRESENTATION} maps to the container holding the rows
	 * {@code | Asymptomatic | Conjunctival presentation |}, {@code | Cutaneous presentation | ... |} and so on.
	 */
	private final Map<SymptomGroup, VerticalLayout> rowsContainerByGroup = new EnumMap<>(SymptomGroup.class);

	/**
	 * The extras slot of each group, by group: the slot at the end of the group that holds the dependents of parents from
	 * other groups. It is created hidden by {@link #getOrCreateExtrasSlot}, only for groups that actually receive such a
	 * dependent, and appended to its group by {@link #attachExtrasSlots}.
	 * <p>
	 * Example: {@code METABOLIC} maps to the slot holding {@code jaundiceWithin24HoursOfBirth}, whose parent
	 * {@code jaundice} is in GASTROINTESTINAL.
	 */
	private final Map<SymptomGroup, VerticalLayout> extrasSlotByGroup = new EnumMap<>(SymptomGroup.class);

	/**
	 * The slot each dependent field is placed in, by the property id of the field. A parent's value change only knows its
	 * dependent fields; this map tells which slots have to be rebuilt for them.
	 * <p>
	 * Example: {@code "otherClinicalCriteriaText"} maps to the slot below the row of "Other clinical criteria",
	 * {@code "jaundiceWithin24HoursOfBirth"} to the extras slot of METABOLIC.
	 */
	private final Map<String, VerticalLayout> slotByDependentId = new HashMap<>();

	/**
	 * All fields registered in each slot, in display order, whether currently shown or not. A slot is rebuilt from scratch on
	 * every change, so this fixed order makes sure the fields always come back in the same positions. The order is the
	 * declaration order in {@link SymptomsDto} (as returned by {@code DependencyConfiguration.getChildren}); nested dependents
	 * are inserted directly after their parent.
	 * <p>
	 * Example: for Dengue the slot of {@code unexplainedBleeding} has the order
	 * {@code [eyesBleeding, gumsBleeding, injectionSiteBleeding, noseBleeding, otherHemorrhagicSymptoms,
	 * otherHemorrhagicSymptomsText, scantHemorrhage, acuteBleeding]}, when all of them are visible.
	 */
	private final Map<VerticalLayout, List<Field<?>>> registeredFieldsBySlot = new HashMap<>();

	/**
	 * The fields currently shown in each slot. Together with {@link #registeredFieldsBySlot} it decides what {@link #rebuildSlot} lays
	 * out, and {@link #isShownInSlot} uses it to show a nested dependent only while its parent is shown.
	 * <p>
	 * Example: while "Other clinical criteria" is Yes, the slot of {@code otherClinicalCriteria} maps to
	 * {@code {otherClinicalCriteriaText}}; after it is set to No, the set is empty and the slot is hidden.
	 */
	private final Map<VerticalLayout, Set<Field<?>>> shownFieldsBySlot = new HashMap<>();

	/**
	 * The fields each slot currently lays out, in display order, as {@link #rebuildSlot} last placed them. {@link #rebuildSlot}
	 * compares the fields to show with this list and leaves the slot untouched when they are the same, so its components are
	 * not removed and added again and the browser does not redraw them. A slot that was never rebuilt has no entry.
	 * <p>
	 * Example: while "Other clinical criteria" is Yes, the slot of {@code otherClinicalCriteria} maps to
	 * {@code [otherClinicalCriteriaText]}; after it is set to No, it maps to {@code []}. Changing it from No to Unknown afterwards
	 * still shows nothing, so the slot is not rebuilt.
	 */
	private final Map<VerticalLayout, List<Field<?>>> laidOutFieldsBySlot = new HashMap<>();

	/**
	 * The section (title and rows) of each group, by its rows container. Used by {@link #updateSectionVisibility} to hide a
	 * section while its rows hold no visible field. Sections with custom components (Clinical measurements with the symptoms
	 * hint and buttons) are not registered, so they are never hidden.
	 * <p>
	 * Example: if all visible fields of a group are dependents of an unanswered parent, its rows container has no visible
	 * field, and the section, including its title, stays hidden until the parent is set to Yes.
	 */
	private final Map<VerticalLayout, VerticalLayout> sectionByRowsContainer = new HashMap<>();

	/**
	 * The visible dependents of each parent field, by the group they belong to: parent field -> {@code @SymptomGrouping} of the
	 * dependent (the parent's group if it has none) -> visible dependent fields. Filled once by {@link #build}. Only parents with
	 * at least one visible dependent are keys; a dependent that has dependents itself is also a key. The group tells where a
	 * dependent is placed: in the parent's own slot below its row if it is the parent's group, otherwise in the extras slot at
	 * the end of that group. Every listed field is left out of the top-level rows.
	 * <p>
	 * Example, when all these fields are visible; groups and fields are in declaration order in {@link SymptomsDto}, as returned
	 * by {@code DependencyConfiguration.getChildren} ({@code scantHemorrhage} and {@code acuteBleeding} exist for Dengue only):
	 *
	 * <pre>
	 * jaundice                 -> {METABOLIC -> [jaundiceWithin24HoursOfBirth]}
	 * unexplainedBleeding      -> {GENITAL -> [bleedingVagina],
	 *                              URINARY -> [bloodUrine],
	 *                              GASTROINTESTINAL -> [bloodyBlackStool, digestedBloodVomit, redBloodVomit, stomachBleeding],
	 *                              RESPIRATORY -> [coughingBlood],
	 *                              HAEMATOLOGICAL -> [eyesBleeding, gumsBleeding, injectionSiteBleeding, noseBleeding,
	 *                                                 otherHemorrhagicSymptoms, scantHemorrhage, acuteBleeding],
	 *                              SKIN -> [skinBruising]}
	 * otherHemorrhagicSymptoms -> {HAEMATOLOGICAL -> [otherHemorrhagicSymptomsText]}
	 * </pre>
	 *
	 * So when "Bleeding or bruising" is set to Yes, the HAEMATOLOGICAL dependents appear in the slot below its row, and the
	 * others in the extras slots at the end of the Genital, Urinary, Gastrointestinal, Respiratory and Skin groups.
	 */
	private final Map<Field<?>, Map<SymptomGroup, List<Field<?>>>> dependentsByParent = new LinkedHashMap<>();

	/**
	 * Creates the layout builder of one form. Nothing is laid out until {@link #build} is called.
	 * <p>
	 * Example: {@code new SymptomGroupLayout(mainLayout, getContent(), dependentCompanions)} in {@code SymptomsForm}.
	 *
	 * @param mainLayout
	 *            the vertical layout that receives the group sections (see {@link #mainLayout})
	 * @param parkingLayout
	 *            the form's CustomLayout, where {@code addField()} parks the fields (see {@link #parkingLayout})
	 * @param dependentCompanions
	 *            components shown next to a dependent field, by its property id; may be empty (see {@link #dependentCompanions})
	 */
	SymptomGroupLayout(VerticalLayout mainLayout, CustomLayout parkingLayout, Map<String, Component> dependentCompanions) {
		this.mainLayout = mainLayout;
		this.parkingLayout = parkingLayout;
		this.dependentCompanions = dependentCompanions;
	}

	/**
	 * Builds the whole layout. Call it once per instance, after all fields are added and their visibilities are set. Steps:
	 * <ol>
	 * <li>Keep the visible fields, sorted by {@link #FIELD_POSITION}.</li>
	 * <li>Group them by {@link #SYMPTOM_GROUP_BY_FIELD}; a field without a group goes to {@link #FALLBACK_GROUP} (see
	 * {@link #fallbackGroup}).</li>
	 * <li>Pass 1: create one section per group, in the order of {@link #GROUP_DISPLAY_ORDER}. The Clinical Measurements section gets
	 * the {@code clinicalMeasurementsComponents}.</li>
	 * <li>Collect the visible dependents of every field into {@link #dependentsByParent}.</li>
	 * <li>Pass 2: fill every section with its rows ({@link #populateGroupRows}); this also creates and wires the slots.</li>
	 * <li>Append the extras slots at the end of their groups ({@link #attachExtrasSlots}).</li>
	 * <li>Hide every section that holds no visible field yet.</li>
	 * </ol>
	 * Example: for a Diphtheria case the Clinical Presentation fields become the section shown in the class documentation;
	 * "Specify other clinical criteria" is placed in a hidden slot below the row of "Other clinical criteria" and is shown as
	 * soon as that field is set to Yes.
	 *
	 * @param fields
	 *            all fields of the form; only the visible ones are laid out
	 * @param clinicalMeasurementsComponents
	 *            components placed below the title of the clinical measurements group (symptoms hint, clear/set buttons)
	 */
	void build(Collection<Field<?>> fields, Component... clinicalMeasurementsComponents) {

		Map<String, Field<?>> visibleFields = fields.stream()
			.filter(field -> field.isVisible())
			.sorted(Comparator.comparingInt(field -> FIELD_POSITION.getOrDefault(field.getId(), Integer.MAX_VALUE)))
			.collect(Collectors.toMap(field -> field.getId(), field -> field, (a, b) -> a, LinkedHashMap::new));

		// Group the visible fields by their SymptomGroup
		Map<SymptomGroup, Map<String, Field<?>>> visibleFieldsByGroup = new LinkedHashMap<>();

		for (Map.Entry<String, Field<?>> entry : visibleFields.entrySet()) {
			String fieldId = entry.getKey();
			Field<?> field = entry.getValue();
			// Expectation is to map each symptom to a group
			SymptomGroup group = SYMPTOM_GROUP_BY_FIELD.get(fieldId);
			if (group == null) {
				group = fallbackGroup(fieldId, field);
			}
			if (group == null) {
				continue;
			}
			visibleFieldsByGroup.computeIfAbsent(group, g -> new LinkedHashMap<>()).put(fieldId, field);
		}

		List<Map.Entry<SymptomGroup, Map<String, Field<?>>>> groupsInDisplayOrder = visibleFieldsByGroup.entrySet()
			.stream()
			.sorted(Comparator.comparingInt(e -> GROUP_DISPLAY_ORDER.indexOf(e.getKey())))
			.collect(Collectors.toList());
		// Pass 1: build every group's section (title, buttons, empty rows container) first,
		// so cross-group dependent lookups in Pass 2 always find a valid target,
		// regardless of where that group sits in GROUP_DISPLAY_ORDER.
		for (Map.Entry<SymptomGroup, Map<String, Field<?>>> groupEntry : groupsInDisplayOrder) {
			SymptomGroup group = groupEntry.getKey();
			if (SymptomGroup.CLINICAL_MEASUREMENTS.equals(group)) {
				createGroupSection(I18nProperties.getEnumCaption(group), group, clinicalMeasurementsComponents);
			} else {
				createGroupSection(I18nProperties.getEnumCaption(group), group, null);
			}
		}
		// collect the visible dependents of each parent field, by the group they belong to
		for (Map.Entry<SymptomGroup, Map<String, Field<?>>> parentGroupEntry : visibleFieldsByGroup.entrySet()) {
			SymptomGroup parentGroup = parentGroupEntry.getKey();
			for (Field<?> parentField : parentGroupEntry.getValue().values()) {
				List<String> dependentIds = DependantOn.DependencyConfiguration.getChildren(SymptomsDto.class, parentField.getId());
				if (dependentIds == null || dependentIds.isEmpty()) {
					continue;
				}

				Map<SymptomGroup, List<Field<?>>> dependentsByGroup = new LinkedHashMap<>();

				for (String dependentId : dependentIds) {
					// Skip dependents that aren't visible for the current disease; an invisible sibling
					// still corrupts the even/odd pairing of the ones that ARE shown.
					if (!visibleFields.containsKey(dependentId)) {
						continue;
					}
					Field<?> dependentField = visibleFields.get(dependentId);
					SymptomGroup dependentGroup = SYMPTOM_GROUP_BY_FIELD.get(dependentId); // Look up via field annotation mapping
					if (dependentGroup == null) {
						// a dependent with no @SymptomGrouping renders next to
						// its parent instead of being silently dropped.
						dependentGroup = parentGroup;
					}
					if (dependentField != null && dependentGroup != null) {
						dependentsByGroup.computeIfAbsent(dependentGroup, g -> new ArrayList<>()).add(dependentField);
					}
				}

				if (!dependentsByGroup.isEmpty()) {
					dependentsByParent.put(parentField, dependentsByGroup);
				}
			}
		}

		// Pass 2: populate each group's fields, now that every group's container exists
		for (Map.Entry<SymptomGroup, Map<String, Field<?>>> groupEntry : groupsInDisplayOrder) {
			SymptomGroup group = groupEntry.getKey();
			Map<String, Field<?>> fieldsInGroup = groupEntry.getValue();
			populateGroupRows(group, fieldsInGroup);
		}
		// extras slots are attached only now, so they come after all rows of their group
		attachExtrasSlots();
		// hide groups that have nothing to show yet, e.g. only dependents whose parent is not answered
		sectionByRowsContainer.keySet().forEach(this::updateSectionVisibility);
	}

	/**
	 * The group a visible field without {@code @SymptomGrouping} is placed in, so that it is shown instead of being left in
	 * the {@link #parkingLayout}, where it is never rendered. Returns {@code null} (the field is not laid out here) for
	 * <ul>
	 * <li>a dependent: it is placed next to its {@link DependantOn} parent instead,</li>
	 * <li>a field the form has already placed somewhere else, i.e. one that is no longer in the parking layout.</li>
	 * </ul>
	 * <p>
	 * Example: a field that is added to {@link SymptomsDto} without {@code @SymptomGrouping} and without parent is shown in
	 * the "Other" section; {@code otherHemorrhagicSymptomsText} without a group would still be placed below
	 * {@code otherHemorrhagicSymptoms}.
	 *
	 * @param fieldId
	 *            the property id of the field
	 * @param field
	 *            the field
	 * @return {@link #FALLBACK_GROUP}, or {@code null} if the field is not to be laid out as a top-level field
	 */
	private SymptomGroup fallbackGroup(String fieldId, Field<?> field) {
		if (fieldId == null || field == null) {
			return null;
		}
		if (DependantOn.DependencyConfiguration.getParent(SymptomsDto.class, fieldId) != null) {
			return null;
		}
		if (field.getParent() != null && field.getParent() != parkingLayout) {
			return null;
		}
		return FALLBACK_GROUP;
	}

	/**
	 * Decides whether the dependents of a parent field are shown for the parent's value. A parent listed in
	 * {@link #SHOW_CONDITION_BY_PARENT} (a drop-down) uses its own condition; every other parent shows its dependents when the value
	 * is Yes (see {@link #isYes}).
	 * <p>
	 * Examples:
	 *
	 * <pre>
	 * shouldShowDependents("otherClinicalCriteria", {YES})    = true   (Yes/No/Unknown option group)
	 * shouldShowDependents("otherClinicalCriteria", {NO})     = false
	 * shouldShowDependents("majorSite", InfectionSite.OTHER)  = true   (custom condition)
	 * shouldShowDependents("majorSite", InfectionSite.GENITO_URINARY) = false
	 * shouldShowDependents(null, {YES})                       = true   (no id: Yes rule, no exception)
	 * </pre>
	 *
	 * @param parentFieldId
	 *            the property id of the parent field; {@code null} falls back to the Yes rule
	 * @param value
	 *            the current value of the parent field
	 * @return true if the dependents of the parent are to be shown
	 */
	private boolean shouldShowDependents(String parentFieldId, Object value) {
		if (parentFieldId == null) {
			// defensive - Map.of() throws a NullPointerException for a null key
			return isYes(value);
		}
		Predicate<Object> condition = SHOW_CONDITION_BY_PARENT.get(parentFieldId);
		if (condition != null) {
			return condition.test(value);
		}
		return isYes(value); // default rule for plain Yes/No/Unknown fields
	}

	/**
	 * Returns the slot at the end of a group that holds the dependents of parents from other groups, e.g.
	 * {@code jaundiceWithin24HoursOfBirth} (METABOLIC) of {@code jaundice} (GASTROINTESTINAL). The slot is created on first use
	 * and stays hidden while it shows no field. It is not attached here, because the rows of its group may not be populated
	 * yet; {@link #attachExtrasSlots} appends it at the end of the group once all rows exist. If the group has no section yet,
	 * one is created.
	 *
	 * @param group
	 *            the group the dependents belong to
	 * @return the extras slot of the group
	 */
	private VerticalLayout getOrCreateExtrasSlot(SymptomGroup group) {
		return extrasSlotByGroup.computeIfAbsent(group, newGroup -> {
			// If target group doesn't exist yet, build a new group section dynamically
			if (!rowsContainerByGroup.containsKey(newGroup)) {
				createGroupSection(I18nProperties.getEnumCaption(newGroup), newGroup);
			}

			VerticalLayout extras = new VerticalLayout();
			extras.setWidth(100, Unit.PERCENTAGE);
			extras.setSpacing(true);
			extras.setMargin(false);
			extras.setVisible(false);
			return extras;
		});
	}

	/**
	 * Appends every extras slot at the end of the rows of its group. Called once by {@link #build}, after all groups are
	 * populated, so the slot comes after the last row of its group. A slot whose group has no rows container is skipped, and
	 * a slot that is already attached is not added again.
	 * <p>
	 * Example: the extras slot of METABOLIC (holding {@code jaundiceWithin24HoursOfBirth}) is created while GASTROINTESTINAL
	 * is populated, before any Metabolic row exists. It is appended here, after the last Metabolic row.
	 */
	private void attachExtrasSlots() {
		for (Map.Entry<SymptomGroup, VerticalLayout> entry : extrasSlotByGroup.entrySet()) {
			VerticalLayout rowsContainer = rowsContainerByGroup.get(entry.getKey());
			VerticalLayout extras = entry.getValue();
			if (rowsContainer == null || extras == null || rowsContainer.getComponentIndex(extras) != -1) {
				continue;
			}
			rowsContainer.addComponent(extras);
		}
	}

	/**
	 * Fills the rows container of a group with its top-level fields, two per row, and adds the dependent slots below each row.
	 * <ul>
	 * <li>Dependents are skipped here; they are placed in a slot by their parent.</li>
	 * <li>A {@link TextArea} gets a full-width row of its own. A half-filled row before it is closed with a spacer first.</li>
	 * <li>A single field in the last row is followed by a spacer, so it keeps its half width.</li>
	 * <li>As soon as a row is complete, a slot is added below it for every field of the row that has dependents.</li>
	 * </ul>
	 * Example: the group GENERAL with the visible fields {@code fever, headache, otherGeneralSymptoms,
	 * otherGeneralSymptomsText} ({@code otherGeneralSymptomsText} depends on {@code otherGeneralSymptoms}) is laid out as:
	 *
	 * <pre>
	 * | Fever                  | Headache          |
	 * | Other general symptoms | (spacer)          |
	 * |   slot: Specify other general symptoms     |   (hidden slot, shown only while "Other general symptoms" is Yes)
	 * </pre>
	 *
	 * {@code otherGeneralSymptomsText} is not placed as a top-level field, because it is a dependent.
	 *
	 * In the group CLINICAL_ASSESSMENT_AND_COURSE, {@code symptomsComments} is a text area, so it gets a full-width row of its
	 * own, and the field before it keeps its half width.
	 *
	 * @param group
	 *            the group to populate
	 * @param groupFields
	 *            the visible fields of the group, by property id, in display order
	 */
	private void populateGroupRows(SymptomGroup group, Map<String, Field<?>> groupFields) {
		if (groupFields == null || groupFields.isEmpty()) {
			return;
		}
		VerticalLayout rowsContainer = rowsContainerByGroup.get(group);
		if (rowsContainer == null) {
			return;
		}

		// Collect all dependent field IDs across all groups to prevent rendering them at top level
		Set<String> allDependentFieldIds = dependentsByParent.values()
			.stream()
			.flatMap(groupMap -> groupMap.values().stream())
			.flatMap(List::stream)
			.map(Field::getId)
			.collect(Collectors.toSet());

		// Filter fields to place: keep standalone fields and parents, exclude dependent child fields
		List<Field<?>> fieldsToPlace = groupFields.values()
			.stream()
			.filter(field -> field != null && !allDependentFieldIds.contains(field.getId()))
			.collect(Collectors.toList());

		HorizontalLayout row = null;
		List<Field<?>> currentRowFields = new ArrayList<>();
		int column = 0;

		for (Field<?> field : fieldsToPlace) {
			// Text areas always get their own full-width row and are never paired with another field
			if (field instanceof TextArea) {
				// close a half-filled row first, so the field before the text area keeps its half width
				if (column == 1) {
					closeRowWithSpacer(rowsContainer, currentRowFields, group, row);
					column = 0;
				}
				addFullWidthRow(rowsContainer, field);
				continue;
			}
			if (column == 0) {
				row = new HorizontalLayout();
				row.setWidth(100, Unit.PERCENTAGE);
				row.setSpacing(true);
				rowsContainer.addComponent(row);
				currentRowFields = new ArrayList<>();
			}
			field.setWidth(100, Unit.PERCENTAGE);
			row.addComponent(field);
			row.setExpandRatio(field, 1);
			currentRowFields.add(field);
			column++;

			if (column == 2) {
				addDependentSlotsForRow(rowsContainer, currentRowFields, group, row);
				column = 0;
			}
		}

		// trailing single field - keep it from stretching full width
		if (column == 1 && row != null) {
			closeRowWithSpacer(rowsContainer, currentRowFields, group, row);
		}
	}

	/**
	 * Closes a row that holds only one field: fills the empty second column with a spacer, so the field keeps its half
	 * width, and adds the dependent slots of the row below it.
	 * <p>
	 * Example: GENERAL with {@code fever, headache, otherGeneralSymptoms} ends with a row holding only "Other general symptoms";
	 * it becomes {@code | Other general symptoms | (spacer) |}, followed by the hidden slot for "Specify other general symptoms".
	 *
	 * @param rowsContainer
	 *            the container of the group's rows
	 * @param rowFields
	 *            the fields placed in the row
	 * @param group
	 *            the group the row belongs to
	 * @param row
	 *            the row to close
	 */
	private void closeRowWithSpacer(VerticalLayout rowsContainer, List<Field<?>> rowFields, SymptomGroup group, HorizontalLayout row) {

		Label spacer = new Label("");
		spacer.setWidth(100, Unit.PERCENTAGE);
		row.addComponent(spacer);
		row.setExpandRatio(spacer, 1);
		addDependentSlotsForRow(rowsContainer, rowFields, group, row);
	}

	/**
	 * Creates an empty slot below the row for every field of the row that has dependents, and wires the dependents into it
	 * (see {@link #wireDependents}). Fields without dependents get no slot. The slots are attached once, directly below the
	 * row and in the order of the row's fields, and stay hidden while they show no field. A parent whose dependents all belong
	 * to other groups (e.g. {@code jaundice}) still gets a slot, but it stays empty and hidden, because its dependents go to
	 * the extras slots of their groups.
	 * <p>
	 * Example: in the Diphtheria row {@code | Uni- or bilateral nasal discharge ... | Other clinical criteria |} only "Other
	 * clinical criteria" has a dependent, so one slot is created for it. It is attached below this row and holds "Specify other
	 * clinical criteria":
	 *
	 * <pre>
	 * | Uni- or bilateral nasal discharge ... | Other clinical criteria |   (anchor row)
	 * |   slot of otherClinicalCriteria: Specify other clinical criteria  |   (hidden until "Other clinical criteria" is Yes)
	 * </pre>
	 *
	 * If both fields of a row had dependents, the slot of the left field would come first, directly below the row, and the
	 * slot of the right field below it.
	 *
	 * @param rowsContainer
	 *            the container of the group's rows
	 * @param rowFields
	 *            the fields of the row
	 * @param currentGroup
	 *            the group the row belongs to
	 * @param anchorRow
	 *            the row the slots are inserted after
	 */
	private void addDependentSlotsForRow(
		VerticalLayout rowsContainer,
		List<Field<?>> rowFields,
		SymptomGroup currentGroup,
		HorizontalLayout anchorRow) {

		// each slot is inserted after the previous one, so the slots keep the order of the row's fields
		Component insertAfter = anchorRow;
		for (Field<?> parentField : rowFields) {
			// Look up registered dependents for this parent field
			Map<SymptomGroup, List<Field<?>>> dependentsByGroup = dependentsByParent.get(parentField);
			if (dependentsByGroup == null || dependentsByGroup.isEmpty()) {
				continue;
			}

			// Create a local dependent slot container anchored under the parent's row
			VerticalLayout parentSlot = new VerticalLayout();
			parentSlot.setWidth(100, Unit.PERCENTAGE);
			parentSlot.setSpacing(false);
			parentSlot.setMargin(false);
			parentSlot.setVisible(false);

			int anchorIndex = rowsContainer.getComponentIndex(insertAfter);
			if (anchorIndex == -1) {
				// defensive - the row should always be in the container; append instead of failing
				rowsContainer.addComponent(parentSlot);
			} else {
				rowsContainer.addComponent(parentSlot, anchorIndex + 1);
			}
			insertAfter = parentSlot;

			wireDependents(parentField, currentGroup, dependentsByGroup, parentSlot);
		}
	}

	/**
	 * Connects the dependents of a parent field to the parent:
	 * <ol>
	 * <li>Each dependent is registered in its slot: the parent's own slot if it is in the parent's group, otherwise the extras
	 * slot of its own group. It is detached from the form's CustomLayout first, so it is first rendered in its slot.</li>
	 * <li>A value change listener on the parent shows the dependents for a "show" value and hides and clears them otherwise.</li>
	 * <li>The dependents are shown or hidden once for the parent's current value, without clearing, so loaded values are kept
	 * when an existing case is opened.</li>
	 * <li>The dependents of the dependents are wired the same way (see {@link #wireNestedDependents}).</li>
	 * </ol>
	 * Example: the parent {@code jaundice} (GASTROINTESTINAL) has the dependent {@code jaundiceWithin24HoursOfBirth}
	 * (METABOLIC). The dependent is registered in the extras slot of the Metabolic group. Setting Jaundice to Yes shows it at the
	 * end of the Metabolic group; setting Jaundice to No hides it and clears its value.
	 *
	 * @param parentField
	 *            the parent field
	 * @param parentGroup
	 *            the group of the parent field
	 * @param dependentsByGroup
	 *            the visible dependents of the parent, by the group they belong to
	 * @param parentSlot
	 *            the slot below the parent's row
	 */
	private void wireDependents(
		Field<?> parentField,
		SymptomGroup parentGroup,
		Map<SymptomGroup, List<Field<?>>> dependentsByGroup,
		VerticalLayout parentSlot) {

		List<Field<?>> parentDependents = new ArrayList<>();

		// Process dependent fields grouped by their target SymptomGroup
		for (Map.Entry<SymptomGroup, List<Field<?>>> entry : dependentsByGroup.entrySet()) {
			SymptomGroup targetGroup = entry.getKey();
			List<Field<?>> dependentFields = entry.getValue();

			// Determine target layout container: local slot if same group, or an extras slot in the target group
			VerticalLayout targetSlot = targetGroup.equals(parentGroup) ? parentSlot : getOrCreateExtrasSlot(targetGroup);

			for (Field<?> dependentField : dependentFields) {
				// addField() parks every field in the CustomLayout under a location the template doesn't define.
				// A hidden dependent would stay there until its parent is set, so its first client-side render
				// happens off-layout and a ComboBox keeps a wrong width. Detach it so it is first rendered in its slot.
				if (dependentField.getParent() == parkingLayout) {
					parkingLayout.removeComponent(dependentField);
				}
				dependentField.setWidth(100, Unit.PERCENTAGE);
				slotByDependentId.put((String) dependentField.getId(), targetSlot);

				// Register position order in the target slot
				List<Field<?>> registeredFields = registeredFieldsBySlot.computeIfAbsent(targetSlot, t -> new ArrayList<>());
				if (!registeredFields.contains(dependentField)) {
					registeredFields.add(dependentField);
				}
				parentDependents.add(dependentField);
			}
		}

		// Attach ValueChangeListener if listener is supported
		if (!parentDependents.isEmpty() && parentField instanceof Property.ValueChangeNotifier) {
			((Property.ValueChangeNotifier) parentField).addValueChangeListener(event -> {
				boolean shouldShow = shouldShowDependents((String) parentField.getId(), event.getProperty().getValue());
				setDependentsShown(parentDependents, shouldShow, true);
			});
		}

		// Run initial sync for edit mode / pre-populated values
		boolean initialShow = shouldShowDependents((String) parentField.getId(), parentField.getValue());
		setDependentsShown(parentDependents, initialShow, false);

		// Dependents of these dependents (e.g. otherHemorrhagicSymptomsText) are never placed at top level,
		// so they are wired here, after the initial sync has decided which of their parents are shown.
		for (Field<?> dependentField : parentDependents) {
			wireNestedDependents(dependentField, slotByDependentId.get(dependentField.getId()));
		}
	}

	/**
	 * Wires the dependents of a dependent field (nested dependents). They are placed in the same slot as their parent, directly
	 * after it, and are only shown while the parent itself is shown in the slot and has a "show" value. This continues
	 * recursively for deeper levels.
	 * <p>
	 * Example: {@code unexplainedBleeding} has the dependent {@code otherHemorrhagicSymptoms}, which has the dependent
	 * {@code otherHemorrhagicSymptomsText}. The text field is placed right after "Other hemorrhagic symptoms" in the slot of
	 * "Bleeding or bruising" ({@code unexplainedBleeding}), and is shown only while both "Bleeding or bruising" and "Other
	 * hemorrhagic symptoms" are Yes. For Dengue, with both set to Yes, the slot shows:
	 *
	 * <pre>
	 * | Bleeding or bruising              | ...                           |   (row of the parent)
	 * |   | Bleeding from the eyes        | Gums bleeding                 |   (slot of unexplainedBleeding)
	 * |   | Bleeding from injection site  | Nose bleed (epistaxis)        |
	 * |   | Other hemorrhagic symptoms    | (spacer)                      |
	 * |   | Specify other hemorrhagic symptoms (full width)               |
	 * |   | scantHemorrhage               | acuteBleeding                 |
	 * </pre>
	 *
	 * Setting "Other hemorrhagic symptoms" to No hides and clears only the text field; setting "Bleeding or bruising" to No
	 * hides the whole slot, including the text field.
	 *
	 * @param parentField
	 *            the dependent field whose own dependents are wired
	 * @param slot
	 *            the slot the parent field is placed in
	 */
	private void wireNestedDependents(Field<?> parentField, VerticalLayout slot) {

		Map<SymptomGroup, List<Field<?>>> nestedDependentsByGroup = dependentsByParent.get(parentField);
		if (nestedDependentsByGroup == null || nestedDependentsByGroup.isEmpty()) {
			return;
		}

		List<Field<?>> children = nestedDependentsByGroup.values().stream().flatMap(List::stream).collect(Collectors.toList());
		List<Field<?>> registeredFields = registeredFieldsBySlot.computeIfAbsent(slot, s -> new ArrayList<>());
		int insertAt = registeredFields.indexOf(parentField) + 1;
		for (Field<?> child : children) {
			if (child.getParent() == parkingLayout) {
				parkingLayout.removeComponent(child);
			}
			child.setWidth(100, Unit.PERCENTAGE);
			slotByDependentId.put((String) child.getId(), slot);
			if (!registeredFields.contains(child)) {
				registeredFields.add(insertAt++, child);
			}
		}

		if (parentField instanceof Property.ValueChangeNotifier) {
			((Property.ValueChangeNotifier) parentField).addValueChangeListener(event -> {
				boolean shouldShow =
					isShownInSlot(parentField, slot) && shouldShowDependents((String) parentField.getId(), event.getProperty().getValue());
				setDependentsShown(children, shouldShow, true);
			});
		}

		boolean initialShow = isShownInSlot(parentField, slot) && shouldShowDependents((String) parentField.getId(), parentField.getValue());
		setDependentsShown(children, initialShow, false);

		for (Field<?> child : children) {
			wireNestedDependents(child, slot);
		}
	}

	/**
	 * Checks whether a field is currently shown in a slot. Used for nested dependents: they may only be shown while their
	 * parent is shown, e.g. "Specify other hemorrhagic symptoms" stays hidden while "Other hemorrhagic symptoms" is hidden
	 * because "Unexplained bleeding" is No, even if "Other hemorrhagic symptoms" still had the value Yes.
	 *
	 * @param field
	 *            the field to check
	 * @param slot
	 *            the slot the field is registered in
	 * @return true if the field is currently shown in the slot
	 */
	private boolean isShownInSlot(Field<?> field, VerticalLayout slot) {
		return shownFieldsBySlot.getOrDefault(slot, Collections.emptySet()).contains(field);
	}

	/**
	 * Marks the dependent fields as shown or hidden in their slots and rebuilds every affected slot once. When hiding with
	 * {@code clearOnHide}, the values of the fields are cleared, so a value the user can no longer see is not saved.
	 * <p>
	 * Example: the user changes "Other clinical criteria" from Yes to No. The listener calls
	 * {@code setDependentsShown([otherClinicalCriteriaText], false, true)}: the text field is removed from its slot,
	 * its text is cleared, and the now empty slot is hidden.
	 *
	 * @param dependentFields
	 *            the dependent fields to show or hide
	 * @param shouldShow
	 *            true to show the fields, false to hide them
	 * @param clearOnHide
	 *            true to clear the values of hidden fields; false for the initial sync, so loaded values are kept
	 */
	private void setDependentsShown(List<Field<?>> dependentFields, boolean shouldShow, boolean clearOnHide) {
		Set<VerticalLayout> affectedSlots = new HashSet<>();

		for (Field<?> dependentField : dependentFields) {
			VerticalLayout target = slotByDependentId.get(dependentField.getId());
			if (target == null) {
				// defensive - every dependent is registered in a slot before this is called
				continue;
			}
			Set<Field<?>> visibleSet = shownFieldsBySlot.computeIfAbsent(target, t -> new HashSet<>());
			if (shouldShow) {
				visibleSet.add(dependentField);
			} else {
				visibleSet.remove(dependentField);
				if (clearOnHide) {
					// Parent moved away from the "show" value interactively - don't leave a stale
					// answer behind on a field the user can no longer see or edit.
					dependentField.clear();
				}
			}
			affectedSlots.add(target);
		}

		for (VerticalLayout slot : affectedSlots) {
			rebuildSlot(slot);
		}
	}

	/**
	 * Lays out the slot again from scratch: all components are removed and the currently shown fields are paired into rows in
	 * their registered order (see {@link #pairFieldsIntoRows}). A slot without shown fields is hidden, a slot with shown fields
	 * is shown; the slot itself stays in its place in the group. Finally the group section is shown or hidden, depending on
	 * whether it still holds a visible field. A {@code null} slot is ignored.
	 * <p>
	 * Example: for Dengue the slot of {@code unexplainedBleeding} has the registered order {@code eyesBleeding, gumsBleeding,
	 * injectionSiteBleeding, noseBleeding, otherHemorrhagicSymptoms, otherHemorrhagicSymptomsText, scantHemorrhage,
	 * acuteBleeding}. When "Bleeding or bruising" is set to Yes, all of them except the text field are shown, in three rows of
	 * two and one row with a spacer, in this order; the text field joins only when "Other hemorrhagic symptoms" is Yes as well.
	 * When "Bleeding or bruising" is set back to No, the slot is emptied and hidden, but stays in its place below the row.
	 * <p>
	 * If the slot already lays out exactly these fields (see {@link #isLaidOut}), its components are kept and only its
	 * visibility and the group section are updated. Example: "Other hemorrhagic symptoms" (inside the slot above) is changed
	 * from No to Unknown; the text field stays hidden either way, so the slot is not emptied and the option group the user
	 * just clicked is not redrawn.
	 *
	 * @param slot
	 *            the slot to rebuild
	 */
	private void rebuildSlot(VerticalLayout slot) {
		if (slot == null) {
			return;
		}
		List<Field<?>> registeredFields = registeredFieldsBySlot.getOrDefault(slot, Collections.emptyList());
		Set<Field<?>> shownSet = shownFieldsBySlot.getOrDefault(slot, Collections.emptySet());

		List<Field<?>> shownFields = new ArrayList<>();
		for (Field<?> field : registeredFields) {
			if (shownSet.contains(field)) {
				shownFields.add(field);
			}
		}

		if (isLaidOut(slot, shownFields)) {
			// nothing to change - keep the components, so the browser does not redraw them
			slot.setVisible(!shownFields.isEmpty());
		} else {
			slot.removeAllComponents();
			if (shownFields.isEmpty()) {
				slot.setVisible(false);
			} else {
				pairFieldsIntoRows(slot, shownFields);
				slot.setVisible(true);
				// Refresh component tree to force Vaadin client-side update
				slot.markAsDirtyRecursive();
			}
			laidOutFieldsBySlot.put(slot, new ArrayList<>(shownFields));
		}

		// the parent of a slot is the rows container of its group; an extras slot has none until attachExtrasSlots()
		// has run, and build() updates all sections afterwards
		if (slot.getParent() instanceof VerticalLayout) {
			updateSectionVisibility((VerticalLayout) slot.getParent());
		}
	}

	/**
	 * Checks whether a slot already lays out exactly the given fields, in the same order, so {@link #rebuildSlot} can keep its
	 * components. This is the case when the list {@link #rebuildSlot} last placed in the slot ({@link #laidOutFieldsBySlot})
	 * equals the given list and every field is still in a row of this slot. A slot that was never rebuilt, or whose fields
	 * were moved away by other code, is not laid out, so it is rebuilt.
	 * <p>
	 * Examples, for the slot of {@code otherClinicalCriteria}:
	 *
	 * <pre>
	 * last laid out [],                          fields [] -> true  (No changed to Unknown)
	 * last laid out [otherClinicalCriteriaText], fields [] -> false (Yes changed to No)
	 * never rebuilt,                             fields [] -> false
	 * </pre>
	 *
	 * @param slot
	 *            the slot to check
	 * @param fields
	 *            the fields that are to be shown in the slot, in display order
	 * @return true if the slot already shows exactly these fields
	 */
	private boolean isLaidOut(VerticalLayout slot, List<Field<?>> fields) {
		List<Field<?>> laidOutFields = laidOutFieldsBySlot.get(slot);
		if (laidOutFields == null || !laidOutFields.equals(fields)) {
			return false;
		}
		// defensive - each field sits in a row of the slot; if other code has moved it, the slot must be rebuilt
		for (Field<?> field : fields) {
			Component row = field.getParent();
			if (row == null || row.getParent() != slot) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Shows a symptom group (title and rows) only while it contains at least one visible field. A group can be empty
	 * when all its fields are dependents whose parent field has not been answered yet.
	 * <p>
	 * Example: for Congenital Rubella, if {@code jaundiceWithin24HoursOfBirth} were the only visible Metabolic field, the
	 * Metabolic section would be hidden until Jaundice is set to Yes, and hidden again when it is set back to No. Sections
	 * created with custom components (Clinical measurements) are not registered here and always stay visible.
	 *
	 * @param rowsContainer
	 *            the rows container of the group
	 */
	private void updateSectionVisibility(VerticalLayout rowsContainer) {
		VerticalLayout section = sectionByRowsContainer.get(rowsContainer);
		if (section != null) {
			section.setVisible(containsVisibleField(rowsContainer));
		}
	}

	/**
	 * Checks recursively whether a component is, or contains, a visible field. Spacers and labels alone do not count, and
	 * a hidden layout does not count even if it contains visible fields.
	 * <p>
	 * Example: a rows container holding only a hidden slot returns false, even if the slot still contained fields; once the
	 * slot is shown with a visible {@code TextField} in it, it returns true. A row holding only a spacer {@code Label} returns
	 * false as well.
	 *
	 * @param component
	 *            the component to check
	 * @return true if the component is a visible field, or a visible layout containing one
	 */
	private static boolean containsVisibleField(Component component) {
		if (!component.isVisible()) {
			return false;
		}
		if (component instanceof Field) {
			return true;
		}
		if (component instanceof HasComponents) {
			for (Component child : (HasComponents) component) {
				if (containsVisibleField(child)) {
					return true;
				}
			}
		}
		return false;
	}

	// generic 2-per-row pairing, reusable for both top-level fields and dependent slots

	/**
	 * Lays out the shown fields of a slot as rows:
	 * <ul>
	 * <li>a field with a companion component (e.g. a monkeypox picture) gets a row of its own, next to its companion,</li>
	 * <li>a {@link TextField} gets a full-width row of its own,</li>
	 * <li>all other fields are paired two per row in their order; a single field left over is followed by a spacer.</li>
	 * </ul>
	 * A pending single field is closed with a spacer before a full-width or companion row, so the order is kept.
	 * <p>
	 * Example: the Monkeypox fields {@code lesionsDeepProfound, lesionsResembleImg1, lesionsSameSize, lesionsSameState} (in this
	 * order), where {@code lesionsResembleImg1} has a picture, are laid out as:
	 *
	 * <pre>
	 * | Rash lesions deep and profound?             | (spacer)                                         |
	 * | Does the rash resemble the photo below?     | [picture 1]                                      |
	 * | All rash lesions the same size?             | All rash lesions in same state of development?   |
	 * </pre>
	 *
	 * "Rash lesions deep and profound?" is waiting for a partner when the picture row comes, so it is closed with a spacer
	 * first; the two fields after the picture row are paired again.
	 *
	 * @param container
	 *            the slot to add the rows to
	 * @param fields
	 *            the fields to lay out, in display order
	 */
	private void pairFieldsIntoRows(VerticalLayout container, List<Field<?>> fields) {
		if (fields == null || fields.isEmpty()) {
			return;
		}
		// Text/String dependents always get their own full-width row and are never paired;
		// all other dependents pair up 2-per-row among themselves, in their original order.
		// A lone non-text dependent (no pair available) still only occupies half the row width.
		List<Field<?>> pendingPair = new ArrayList<>();
		for (Field<?> field : fields) {
			Component companion = dependentCompanions.get((String) field.getId());
			if (companion != null) {
				flushTrailingSingle(container, pendingPair);
				addFieldWithCompanionRow(container, field, companion);
				continue;
			}
			if (field instanceof TextField) {
				flushTrailingSingle(container, pendingPair);
				addFullWidthRow(container, field);
				continue;
			}
			pendingPair.add(field);
			if (pendingPair.size() == 2) {
				addPairedRow(container, pendingPair.get(0), pendingPair.get(1));
				pendingPair.clear();
			}
		}
		flushTrailingSingle(container, pendingPair);
	}

	/**
	 * Adds a row with the field in the first column and its companion component in the second, both half width.
	 * <p>
	 * Example: {@code | Does the rash resemble the photo below? | [img/monkeypox-lesions-1.png] |}
	 *
	 * @param container
	 *            the slot to add the row to
	 * @param field
	 *            the field
	 * @param companion
	 *            the component shown next to the field, e.g. a picture
	 */
	private void addFieldWithCompanionRow(VerticalLayout container, Field<?> field, Component companion) {
		HorizontalLayout row = new HorizontalLayout();
		row.setWidth(100, Unit.PERCENTAGE);
		row.setSpacing(true);
		container.addComponent(row);

		applyFullWidth(field);
		row.addComponent(field);
		row.setExpandRatio(field, 1);
		row.addComponent(companion);
		row.setExpandRatio(companion, 1);
	}

	/**
	 * Closes a pending single field: if exactly one field is waiting for a partner, it is added as a row with a spacer and the
	 * list is cleared. With zero or two fields nothing happens.
	 * <p>
	 * Example: with {@code pendingPair = [otherHemorrhagicSymptoms]} it adds {@code | Other hemorrhagic symptoms | (spacer) |}
	 * and leaves {@code pendingPair} empty.
	 *
	 * @param container
	 *            the slot to add the row to
	 * @param pendingPair
	 *            the fields waiting for a partner; emptied if a row is added
	 */
	private void flushTrailingSingle(VerticalLayout container, List<Field<?>> pendingPair) {
		if (pendingPair.size() == 1) {
			addTrailingSingleRow(container, pendingPair.get(0));
			pendingPair.clear();
		}
	}

	/**
	 * Adds a row holding only the field, stretched over the full width. Used for text fields in slots and for text areas in
	 * the group rows.
	 * <p>
	 * Example: {@code | Specify other clinical criteria (full width) |}
	 *
	 * @param container
	 *            the slot or rows container to add the row to
	 * @param field
	 *            the field
	 */
	private void addFullWidthRow(VerticalLayout container, Field<?> field) {
		HorizontalLayout row = new HorizontalLayout();
		row.setWidth(100, Unit.PERCENTAGE);
		row.setSpacing(true);
		container.addComponent(row);

		applyFullWidth(field);
		row.addComponent(field);
		row.setExpandRatio(field, 1);
	}

	/**
	 * Adds a row with two fields, each half width.
	 * <p>
	 * Example: {@code | Bleeding from the eyes | Gums bleeding |}
	 *
	 * @param container
	 *            the slot to add the row to
	 * @param first
	 *            the field in the left column
	 * @param second
	 *            the field in the right column
	 */
	private void addPairedRow(VerticalLayout container, Field<?> first, Field<?> second) {
		HorizontalLayout row = new HorizontalLayout();
		row.setWidth(100, Unit.PERCENTAGE);
		row.setSpacing(true);
		container.addComponent(row);

		applyFullWidth(first);
		row.addComponent(first);
		row.setExpandRatio(first, 1);

		applyFullWidth(second);
		row.addComponent(second);
		row.setExpandRatio(second, 1);
	}

	/**
	 * Adds a row with the field in the left column and an empty spacer in the right one, so the field keeps its half width
	 * instead of stretching over the whole row.
	 * <p>
	 * Example: {@code | Other hemorrhagic symptoms | (spacer) |}
	 *
	 * @param container
	 *            the slot to add the row to
	 * @param field
	 *            the field
	 */
	private void addTrailingSingleRow(VerticalLayout container, Field<?> field) {
		HorizontalLayout row = new HorizontalLayout();
		row.setWidth(100, Unit.PERCENTAGE);
		row.setSpacing(true);
		container.addComponent(row);

		applyFullWidth(field);
		row.addComponent(field);
		row.setExpandRatio(field, 1);

		// Spacer prevents trailing odd fields from stretching across both columns
		Label spacer = new Label("");
		spacer.setWidth(100, Unit.PERCENTAGE);
		row.addComponent(spacer);
		row.setExpandRatio(spacer, 1);
	}

	/**
	 * Makes the field fill its column. The width is reset first, so a width set earlier by the form (e.g. a fixed pixel width)
	 * does not survive.
	 * <p>
	 * Example: a ComboBox created with a fixed width fills its whole half of the row after this call.
	 *
	 * @param field
	 *            the field to resize
	 */
	private void applyFullWidth(Field<?> field) {
		field.setWidthUndefined();
		field.setWidth(100, Unit.PERCENTAGE);
	}

	/**
	 * Creates the empty section of a group and adds it at the end of the main layout. The section consists of the group title,
	 * the custom components (aligned to the right) and an empty rows container that is filled later by
	 * {@link #populateGroupRows} and the dependent slots. Nothing happens if the group already has a section, so it is safe
	 * to call twice for the same group.
	 * <p>
	 * A section without custom components is registered to be hidden while its rows hold no visible field, e.g. a group whose
	 * only fields are dependents of an unanswered parent. A section with custom components always stays visible, so the
	 * components are never hidden with it.
	 * <p>
	 * Example: {@code createGroupSection("Clinical Measurements", CLINICAL_MEASUREMENTS, symptomsHint, buttonLayout)} creates
	 * the section below and moves the hint and the buttons into it (a component can have only one parent, so they leave
	 * {@link #mainLayout}):
	 *
	 * <pre>
	 * Clinical Measurements                                            (H3 title)
	 *                                            symptoms hint         (custom component)
	 *     Clear all | Set cleared to No | Set cleared to Unknown       (custom component)
	 * [ rows container - empty until populated ]
	 * </pre>
	 *
	 * @param groupTitle
	 *            the title of the section
	 * @param group
	 *            the group the section is created for
	 * @param customComponents
	 *            components placed below the title, aligned to the right; may be null or empty
	 */
	private void createGroupSection(String groupTitle, SymptomGroup group, Component... customComponents) {

		// Prevent duplicate sections for the same group
		if (rowsContainerByGroup.containsKey(group)) {
			return;
		}
		VerticalLayout section = new VerticalLayout();
		section.setWidth(100, Unit.PERCENTAGE);
		section.setSpacing(false);
		section.setMargin(false);

		Label groupTitleLabel = new Label(groupTitle);
		groupTitleLabel.addStyleName(ValoTheme.LABEL_H3);
		section.addComponent(groupTitleLabel);

		if (customComponents != null && customComponents.length > 0) {
			for (Component customComponent : customComponents) {
				section.addComponent(customComponent);
				section.setComponentAlignment(customComponent, Alignment.TOP_RIGHT);
			}
		}

		VerticalLayout rowsContainer = new VerticalLayout();
		rowsContainer.setWidth(100, Unit.PERCENTAGE);
		rowsContainer.setSpacing(true);
		rowsContainer.setMargin(false);
		section.addComponent(rowsContainer);

		mainLayout.addComponent(section);
		rowsContainerByGroup.put(group, rowsContainer);
		// sections holding custom components (symptoms hint, clear/set buttons) always stay visible
		if (customComponents == null || customComponents.length == 0) {
			sectionByRowsContainer.put(rowsContainer, section);
		}
	}

	/**
	 * Default "show" rule for a parent without a custom condition: checks whether the value is Yes. The value of a
	 * Yes/No/Unknown option group is a set, so a set is Yes if it contains Yes. Enum values are compared by constant, not by
	 * {@code toString()}, because {@link SymptomState} and {@link YesNoUnknown} return their caption there, not the constant
	 * name.
	 * <p>
	 * Any other type is never Yes. Every parent using this rule is a {@link SymptomState} or {@link YesNoUnknown} field; a parent
	 * of another type (e.g. a date or a drop-down) needs its own entry in {@link #SHOW_CONDITION_BY_PARENT}, otherwise its
	 * dependents are never shown.
	 * <p>
	 * Examples:
	 *
	 * <pre>
	 * isYes(null)                 = false
	 * isYes({SymptomState.YES})   = true
	 * isYes({YesNoUnknown.NO})    = false
	 * isYes(SymptomState.UNKNOWN) = false
	 * isYes(YesNoUnknown.YES)     = true
	 * isYes("YES")                = true
	 * isYes(Boolean.TRUE)         = true
	 * isYes(Boolean.FALSE)        = false
	 * isYes(new Date())           = false  (any other type is not Yes)
	 * </pre>
	 *
	 * @param value
	 *            the value of the parent field
	 * @return true if the value is Yes
	 */
	private boolean isYes(Object value) {
		if (value == null) {
			return false;
		}
		if (value instanceof Set<?>) {
			return ((Set<?>) value).stream().anyMatch(this::isYesValue);
		}
		return isYesValue(value);
	}

	/**
	 * Whether a single value is Yes: {@link SymptomState#YES}, {@link YesNoUnknown#YES}, {@link Boolean#TRUE} or the String
	 * "YES" (the constant name, case-insensitive). Any other value, including {@code null}, is not Yes.
	 * <p>
	 * Example: {@code isYesValue(SymptomState.YES)} is true, {@code isYesValue(SymptomState.NO)} and
	 * {@code isYesValue(new Date())} are false.
	 */
	private boolean isYesValue(Object value) {
		if (value == SymptomState.YES || value == YesNoUnknown.YES || Boolean.TRUE.equals(value)) {
			return true;
		}
		return value instanceof String
			&& (SymptomState.YES.name().equalsIgnoreCase((String) value) || YesNoUnknown.YES.name().equalsIgnoreCase((String) value));
	}
}
