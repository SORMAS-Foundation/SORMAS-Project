/*
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
 */

package de.symeda.sormas.ui.symptoms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.symptoms.InfectionSite;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.SymptomGroup;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.utils.NullableOptionGroup;

/**
 * Tests {@link SymptomGroupLayout} on its own, with hand-built fields, so every layout rule is checked without the disease
 * and country logic of {@link SymptomsForm}. The groups, the order and the dependencies come from the annotations of
 * {@link SymptomsDto}, exactly as in the real form.
 */
class SymptomGroupLayoutTest extends AbstractUiBeanTest {

	private VerticalLayout mainLayout;
	private CustomLayout parkingLayout;
	private Map<String, Component> companions;

	@BeforeEach
	void createLayouts() throws IOException {
		mainLayout = new VerticalLayout();
		parkingLayout = new CustomLayout(new ByteArrayInputStream("<div location=\"content\"></div>".getBytes(StandardCharsets.UTF_8)));
		companions = new HashMap<>();
	}

	// --- Group sections -------------------------------------------------------------------------------------------------

	@Test
	void shouldCreateSectionsInGroupPriorityOrder() {
		// passed in a mixed order on purpose
		build(
			symptom(SymptomsDto.NO_COMPLICATION_DIAGNOSIS),
			symptom(SymptomsDto.CONJUNCTIVAL_PRESENTATION),
			symptom(SymptomsDto.COUGH),
			symptom(SymptomsDto.FEVER));

		assertEquals(
			Arrays.asList(
				caption(SymptomGroup.CLINICAL_PRESENTATION),
				caption(SymptomGroup.GENERAL),
				caption(SymptomGroup.RESPIRATORY),
				caption(SymptomGroup.SYSTEM_DISORDER)),
			sectionTitles());
	}

	@Test
	void shouldCreateOneSectionPerGroup() {
		build(symptom(SymptomsDto.CONJUNCTIVAL_PRESENTATION), symptom(SymptomsDto.CUTANEOUS_PRESENTATION));

		assertEquals(Arrays.asList(caption(SymptomGroup.CLINICAL_PRESENTATION)), sectionTitles());
	}

	@Test
	void shouldNotPlaceFieldsHiddenBeforeBuild() {
		Field<?> fever = symptom(SymptomsDto.FEVER);
		Field<?> cough = symptom(SymptomsDto.COUGH);
		cough.setVisible(false);

		build(fever, cough);

		assertTrue(isAttached(fever));
		assertFalse(isAttached(cough));
		// a group whose only field is hidden gets no section
		assertFalse(sectionTitles().contains(caption(SymptomGroup.RESPIRATORY)));

		// making it visible afterwards does not place it - use @DependantOn instead of setVisibleWhen
		cough.setVisible(true);
		assertFalse(isAttached(cough));
	}

	@Test
	void shouldPlaceFieldWithoutSymptomGroupingInOtherSection() {
		// e.g. patientIllLocation: no @SymptomGrouping and no parent, so it is shown in "Other" instead of staying unrendered
		TextField notGrouped = new TextField();
		notGrouped.setId("notASymptomField");
		// addField() of the form parks every field in the CustomLayout
		parkingLayout.addComponent(notGrouped, "notASymptomField");

		build(symptom(SymptomsDto.FEVER), notGrouped);

		assertTrue(isAttached(notGrouped));
		assertEquals(caption(SymptomGroup.OTHER), titleOfSectionHolding(notGrouped));
	}

	@Test
	void shouldNotMoveFieldWithoutSymptomGroupingThatTheFormPlacedElsewhere() {
		TextField notGrouped = new TextField();
		notGrouped.setId("notASymptomField");
		VerticalLayout placedByForm = new VerticalLayout(notGrouped);

		build(symptom(SymptomsDto.FEVER), notGrouped);

		assertSame(placedByForm, notGrouped.getParent());
		assertFalse(sectionTitles().contains(caption(SymptomGroup.OTHER)));
	}

	@Test
	void shouldPutCustomComponentsIntoClinicalMeasurementsSection() {
		Label hint = new Label("hint");
		Field<?> temperature = new ComboBox();
		temperature.setId(SymptomsDto.TEMPERATURE);

		new SymptomGroupLayout(mainLayout, parkingLayout, companions).build(Arrays.asList(temperature), hint);

		VerticalLayout section = (VerticalLayout) hint.getParent();
		assertSame(mainLayout, section.getParent());
		assertEquals(caption(SymptomGroup.CLINICAL_MEASUREMENTS), ((Label) section.getComponent(0)).getValue());
	}

	@Test
	void shouldLeaveCustomComponentsAloneWithoutClinicalMeasurementFields() {
		// e.g. Diphtheria: no visible clinical measurement, so the hint and buttons stay where the form put them
		Label hint = new Label("hint");
		mainLayout.addComponent(hint);

		new SymptomGroupLayout(mainLayout, parkingLayout, companions).build(Arrays.asList(symptom(SymptomsDto.FEVER)), hint);

		assertSame(mainLayout, hint.getParent());
		assertFalse(sectionTitles().contains(caption(SymptomGroup.CLINICAL_MEASUREMENTS)));
	}

	// --- Rows -----------------------------------------------------------------------------------------------------------

	@Test
	void shouldPairFieldsTwoPerRowInOrderAnnotationOrder() {
		Field<?> conjunctival = symptom(SymptomsDto.CONJUNCTIVAL_PRESENTATION); // @Order(516)
		Field<?> cutaneous = symptom(SymptomsDto.CUTANEOUS_PRESENTATION); // @Order(517)
		Field<?> genital = symptom(SymptomsDto.GENITAL_PRESENTATION); // @Order(518)

		build(genital, cutaneous, conjunctival);

		HorizontalLayout firstRow = rowOf(conjunctival);
		assertSame(firstRow, rowOf(cutaneous));
		assertEquals(Arrays.asList(conjunctival, cutaneous), fieldsOf(firstRow));

		// a trailing single field is followed by a spacer, so it keeps its half width
		HorizontalLayout secondRow = rowOf(genital);
		assertEquals(2, secondRow.getComponentCount());
		assertTrue(secondRow.getComponent(1) instanceof Label);
		assertTrue(indexInGroup(firstRow) < indexInGroup(secondRow));
	}

	@Test
	void shouldGiveTextAreaAFullWidthRowOfItsOwn() {
		// both are in CLINICAL_ASSESSMENT_AND_COURSE
		Field<?> onsetSymptom = new ComboBox();
		onsetSymptom.setId(SymptomsDto.ONSET_SYMPTOM);
		TextArea comments = new TextArea();
		comments.setId(SymptomsDto.SYMPTOMS_COMMENTS);

		build(onsetSymptom, comments);

		assertEquals(1, rowOf(comments).getComponentCount());
		// the field next to it is not stretched: it is closed with a spacer
		assertNotEquals(rowOf(onsetSymptom), rowOf(comments));
		assertEquals(2, rowOf(onsetSymptom).getComponentCount());
		assertTrue(rowOf(onsetSymptom).getComponent(1) instanceof Label);
	}

	// --- Dependents in the same group -----------------------------------------------------------------------------------

	@Test
	void shouldNotPlaceDependentAtTopLevel() {
		NullableOptionGroup otherClinicalCriteria = symptom(SymptomsDto.OTHER_CLINICAL_CRITERIA);
		TextField text = text(SymptomsDto.OTHER_CLINICAL_CRITERIA_TEXT);

		build(otherClinicalCriteria, text);

		// not paired with its parent, and hidden while the parent is not answered
		assertFalse(fieldsOf(rowOf(otherClinicalCriteria)).contains(text));
		assertFalse(isAttached(text));
	}

	@Test
	void shouldShowDependentDirectlyBelowParentRowWhenParentIsYes() {
		NullableOptionGroup otherClinicalCriteria = symptom(SymptomsDto.OTHER_CLINICAL_CRITERIA);
		Field<?> respiratoryAndCutaneous = symptom(SymptomsDto.RESPIRATORY_AND_CUTANEOUS_PRESENTATION);
		Field<?> respiratoryWithMembrane = symptom(SymptomsDto.RESPIRATORY_WITH_MEMBRANE);
		TextField text = text(SymptomsDto.OTHER_CLINICAL_CRITERIA_TEXT);

		build(otherClinicalCriteria, respiratoryAndCutaneous, respiratoryWithMembrane, text);
		otherClinicalCriteria.setNullableValue(SymptomState.YES);

		assertTrue(isAttached(text));
		// | Other clinical criteria | Respiratory and cutaneous |
		// |   slot: Specify other clinical criteria (full width) |
		// | Respiratory with membrane | (spacer)                 |
		Component slot = rowOf(text).getParent();
		assertEquals(indexInGroup(rowOf(otherClinicalCriteria)) + 1, indexInGroup(slot));
		assertTrue(indexInGroup(slot) < indexInGroup(rowOf(respiratoryWithMembrane)));
		assertEquals(1, rowOf(text).getComponentCount());
	}

	@Test
	void shouldHideAndClearDependentWhenParentIsNoLongerYes() {
		NullableOptionGroup otherClinicalCriteria = symptom(SymptomsDto.OTHER_CLINICAL_CRITERIA);
		TextField text = text(SymptomsDto.OTHER_CLINICAL_CRITERIA_TEXT);

		build(otherClinicalCriteria, text);
		otherClinicalCriteria.setNullableValue(SymptomState.YES);
		text.setValue("Swollen neck");

		otherClinicalCriteria.setNullableValue(SymptomState.NO);

		assertFalse(isAttached(text));
		assertTrue(text.isEmpty());
	}

	@Test
	void shouldShowDependentAgainAfterParentIsSetBackToYes() {
		NullableOptionGroup otherClinicalCriteria = symptom(SymptomsDto.OTHER_CLINICAL_CRITERIA);
		TextField text = text(SymptomsDto.OTHER_CLINICAL_CRITERIA_TEXT);

		build(otherClinicalCriteria, text);
		otherClinicalCriteria.setNullableValue(SymptomState.YES);
		VerticalLayout rowsContainer = (VerticalLayout) rowOf(otherClinicalCriteria).getParent();
		int componentsWhileShown = rowsContainer.getComponentCount();

		otherClinicalCriteria.setNullableValue(SymptomState.UNKNOWN);
		otherClinicalCriteria.setNullableValue(SymptomState.YES);

		assertTrue(isAttached(text));
		// the slot is attached again in the same place, not added a second time
		assertEquals(componentsWhileShown, rowsContainer.getComponentCount());
		assertEquals(indexInGroup(rowOf(otherClinicalCriteria)) + 1, indexInGroup(rowOf(text).getParent()));
	}

	@Test
	void shouldKeepLoadedDependentValueOnBuild() {
		NullableOptionGroup otherClinicalCriteria = symptom(SymptomsDto.OTHER_CLINICAL_CRITERIA);
		TextField text = text(SymptomsDto.OTHER_CLINICAL_CRITERIA_TEXT);
		otherClinicalCriteria.setNullableValue(SymptomState.YES);
		text.setValue("Swollen neck");

		build(otherClinicalCriteria, text);

		assertTrue(isAttached(text));
		assertEquals("Swollen neck", text.getValue());
	}

	@Test
	void shouldRemoveDependentFromParkingLayout() {
		NullableOptionGroup otherClinicalCriteria = symptom(SymptomsDto.OTHER_CLINICAL_CRITERIA);
		TextField text = text(SymptomsDto.OTHER_CLINICAL_CRITERIA_TEXT);
		// addField() of the form parks every field in the CustomLayout
		parkingLayout.addComponent(text, SymptomsDto.OTHER_CLINICAL_CRITERIA_TEXT);

		build(otherClinicalCriteria, text);

		assertNull(text.getParent());
		otherClinicalCriteria.setNullableValue(SymptomState.YES);
		assertTrue(isAttached(text));
	}

	@Test
	void shouldSkipDependentHiddenBeforeBuild() {
		NullableOptionGroup otherClinicalCriteria = symptom(SymptomsDto.OTHER_CLINICAL_CRITERIA);
		TextField text = text(SymptomsDto.OTHER_CLINICAL_CRITERIA_TEXT);
		text.setVisible(false);

		build(otherClinicalCriteria, text);
		otherClinicalCriteria.setNullableValue(SymptomState.YES);

		assertFalse(isAttached(text));
	}

	// --- Dependents in another group ------------------------------------------------------------------------------------

	@Test
	void shouldShowCrossGroupDependentAtTheEndOfItsOwnGroup() {
		// jaundice is GASTROINTESTINAL, jaundiceWithin24HoursOfBirth is METABOLIC
		NullableOptionGroup jaundice = symptom(SymptomsDto.JAUNDICE);
		NullableOptionGroup within24Hours = yesNoUnknown(SymptomsDto.JAUNDICE_WITHIN_24_HOURS_OF_BIRTH);

		build(jaundice, within24Hours);
		jaundice.setNullableValue(SymptomState.YES);

		assertTrue(isAttached(within24Hours));
		assertEquals(caption(SymptomGroup.METABOLIC), titleOfSectionHolding(within24Hours));
	}

	@Test
	void shouldHideSectionWhileItHoldsOnlyUnansweredDependents() {
		NullableOptionGroup jaundice = symptom(SymptomsDto.JAUNDICE);
		NullableOptionGroup within24Hours = yesNoUnknown(SymptomsDto.JAUNDICE_WITHIN_24_HOURS_OF_BIRTH);

		build(jaundice, within24Hours);
		assertFalse(isSectionDisplayed(SymptomGroup.METABOLIC));

		jaundice.setNullableValue(SymptomState.YES);
		assertTrue(isSectionDisplayed(SymptomGroup.METABOLIC));

		within24Hours.setNullableValue(YesNoUnknown.YES);
		jaundice.setNullableValue(SymptomState.NO);
		assertFalse(isSectionDisplayed(SymptomGroup.METABOLIC));
		assertNull(within24Hours.getNullableValue());
	}

	// --- One parent, dependents in several other groups (unexplainedBleeding) -------------------------------------------
	// unexplainedBleeding (HAEMATOLOGICAL) -> bleedingVagina (GENITAL), bloodUrine (URINARY),
	// bloodyBlackStool + stomachBleeding (GASTROINTESTINAL), otherHemorrhagicSymptoms (HAEMATOLOGICAL, same group)

	@Test
	void shouldPlaceEachCrossGroupDependentInItsOwnGroupSection() {
		NullableOptionGroup bleeding = symptom(SymptomsDto.UNEXPLAINED_BLEEDING);
		NullableOptionGroup vagina = symptom(SymptomsDto.BLEEDING_VAGINA);
		NullableOptionGroup urine = symptom(SymptomsDto.BLOOD_URINE);
		NullableOptionGroup stool = symptom(SymptomsDto.BLOODY_BLACK_STOOL);
		NullableOptionGroup otherHemorrhagic = symptom(SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS);

		build(bleeding, vagina, urine, stool, otherHemorrhagic, symptom(SymptomsDto.DIARRHEA));
		bleeding.setNullableValue(SymptomState.YES);

		assertEquals(caption(SymptomGroup.GENITAL), titleOfSectionHolding(vagina));
		assertEquals(caption(SymptomGroup.URINARY), titleOfSectionHolding(urine));
		assertEquals(caption(SymptomGroup.GASTROINTESTINAL), titleOfSectionHolding(stool));
		// a dependent in the parent's own group stays in the parent's section
		assertEquals(caption(SymptomGroup.HAEMATOLOGICAL), titleOfSectionHolding(otherHemorrhagic));
		assertSame(sectionHolding(bleeding), sectionHolding(otherHemorrhagic));
	}

	@Test
	void shouldPlaceSameGroupDependentBelowParentButCrossGroupDependentAtTheEndOfItsGroup() {
		NullableOptionGroup bleeding = symptom(SymptomsDto.UNEXPLAINED_BLEEDING);
		NullableOptionGroup stool = symptom(SymptomsDto.BLOODY_BLACK_STOOL);
		NullableOptionGroup otherHemorrhagic = symptom(SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS);
		Field<?> diarrhea = symptom(SymptomsDto.DIARRHEA);
		Field<?> nausea = symptom(SymptomsDto.NAUSEA);

		build(bleeding, stool, otherHemorrhagic, diarrhea, nausea);
		bleeding.setNullableValue(SymptomState.YES);

		// same group: the slot directly below the parent's row
		assertEquals(indexInGroup(rowOf(bleeding)) + 1, indexInGroup(rowOf(otherHemorrhagic).getParent()));

		// other group: the extras slot after all own rows of Gastrointestinal
		VerticalLayout gastroRows = (VerticalLayout) rowOf(diarrhea).getParent();
		Component extrasSlot = rowOf(stool).getParent();
		assertSame(gastroRows, extrasSlot.getParent());
		assertEquals(gastroRows.getComponentCount() - 1, gastroRows.getComponentIndex(extrasSlot));
		assertTrue(indexInGroup(rowOf(diarrhea)) < indexInGroup(extrasSlot));
		assertTrue(indexInGroup(rowOf(nausea)) < indexInGroup(extrasSlot));
	}

	@Test
	void shouldPairTwoCrossGroupDependentsOfTheSameGroupInOneRow() {
		NullableOptionGroup bleeding = symptom(SymptomsDto.UNEXPLAINED_BLEEDING);
		NullableOptionGroup stool = symptom(SymptomsDto.BLOODY_BLACK_STOOL);
		NullableOptionGroup stomach = symptom(SymptomsDto.STOMACH_BLEEDING);

		build(bleeding, stool, stomach, symptom(SymptomsDto.DIARRHEA));
		bleeding.setNullableValue(SymptomState.YES);

		// both are Gastrointestinal, so they share one extras slot and are paired like any other fields
		assertSame(rowOf(stool), rowOf(stomach));
		assertEquals(2, rowOf(stool).getComponentCount());
	}

	@Test
	void shouldKeepALoneCrossGroupDependentAtHalfWidth() {
		NullableOptionGroup bleeding = symptom(SymptomsDto.UNEXPLAINED_BLEEDING);
		NullableOptionGroup vagina = symptom(SymptomsDto.BLEEDING_VAGINA);

		build(bleeding, vagina);
		bleeding.setNullableValue(SymptomState.YES);

		HorizontalLayout row = rowOf(vagina);
		assertEquals(2, row.getComponentCount());
		assertTrue(row.getComponent(1) instanceof Label);
	}

	@Test
	void shouldKeepCrossGroupSectionsInGroupPriorityOrder() {
		// the sections of groups that only hold dependents are created in pass 1 with all other sections,
		// so they are not appended at the end when the dependents are shown later
		NullableOptionGroup bleeding = symptom(SymptomsDto.UNEXPLAINED_BLEEDING);

		build(
			symptom(SymptomsDto.BLEEDING_VAGINA),
			symptom(SymptomsDto.BLOOD_URINE),
			symptom(SymptomsDto.BLOODY_BLACK_STOOL),
			bleeding,
			symptom(SymptomsDto.FEVER));
		bleeding.setNullableValue(SymptomState.YES);

		assertEquals(
			Arrays.asList(
				caption(SymptomGroup.GENERAL),
				caption(SymptomGroup.GASTROINTESTINAL),
				caption(SymptomGroup.URINARY),
				caption(SymptomGroup.HAEMATOLOGICAL),
				caption(SymptomGroup.GENITAL)),
			sectionTitles());
	}

	@Test
	void shouldShowAndHideSectionsThatOnlyHoldCrossGroupDependents() {
		// Genital and Urinary only hold dependents; Gastrointestinal also has diarrhea of its own
		NullableOptionGroup bleeding = symptom(SymptomsDto.UNEXPLAINED_BLEEDING);
		NullableOptionGroup stool = symptom(SymptomsDto.BLOODY_BLACK_STOOL);

		build(bleeding, symptom(SymptomsDto.BLEEDING_VAGINA), symptom(SymptomsDto.BLOOD_URINE), stool, symptom(SymptomsDto.DIARRHEA));

		assertFalse(isSectionDisplayed(SymptomGroup.GENITAL));
		assertFalse(isSectionDisplayed(SymptomGroup.URINARY));
		assertTrue(isSectionDisplayed(SymptomGroup.GASTROINTESTINAL));
		assertTrue(isSectionDisplayed(SymptomGroup.HAEMATOLOGICAL));

		bleeding.setNullableValue(SymptomState.YES);
		assertTrue(isSectionDisplayed(SymptomGroup.GENITAL));
		assertTrue(isSectionDisplayed(SymptomGroup.URINARY));
		assertTrue(isAttached(stool));

		bleeding.setNullableValue(SymptomState.NO);
		assertFalse(isSectionDisplayed(SymptomGroup.GENITAL));
		assertFalse(isSectionDisplayed(SymptomGroup.URINARY));
		// a section with own fields stays, only the dependent leaves it
		assertTrue(isSectionDisplayed(SymptomGroup.GASTROINTESTINAL));
		assertFalse(isAttached(stool));
	}

	@Test
	void shouldClearEveryCrossGroupDependentWhenParentIsNoLongerYes() {
		NullableOptionGroup bleeding = symptom(SymptomsDto.UNEXPLAINED_BLEEDING);
		NullableOptionGroup vagina = symptom(SymptomsDto.BLEEDING_VAGINA);
		NullableOptionGroup urine = symptom(SymptomsDto.BLOOD_URINE);
		NullableOptionGroup stool = symptom(SymptomsDto.BLOODY_BLACK_STOOL);

		build(bleeding, vagina, urine, stool);
		bleeding.setNullableValue(SymptomState.YES);
		vagina.setNullableValue(SymptomState.YES);
		urine.setNullableValue(SymptomState.NO);
		stool.setNullableValue(SymptomState.UNKNOWN);

		bleeding.setNullableValue(SymptomState.UNKNOWN);

		assertNull(vagina.getNullableValue());
		assertNull(urine.getNullableValue());
		assertNull(stool.getNullableValue());
	}

	@Test
	void shouldShowLoadedCrossGroupDependentsOnBuild() {
		NullableOptionGroup bleeding = symptom(SymptomsDto.UNEXPLAINED_BLEEDING);
		NullableOptionGroup vagina = symptom(SymptomsDto.BLEEDING_VAGINA);
		bleeding.setNullableValue(SymptomState.YES);
		vagina.setNullableValue(SymptomState.YES);

		build(bleeding, vagina);

		assertTrue(isAttached(vagina));
		assertTrue(isSectionDisplayed(SymptomGroup.GENITAL));
		assertEquals(SymptomState.YES, vagina.getNullableValue());
	}

	@Test
	void shouldNotCreateSectionForCrossGroupDependentHiddenBeforeBuild() {
		// e.g. a bleeding dependent that is not available for the disease
		NullableOptionGroup bleeding = symptom(SymptomsDto.UNEXPLAINED_BLEEDING);
		NullableOptionGroup vagina = symptom(SymptomsDto.BLEEDING_VAGINA);
		vagina.setVisible(false);

		build(bleeding, vagina);
		bleeding.setNullableValue(SymptomState.YES);

		assertFalse(isAttached(vagina));
		assertFalse(sectionTitles().contains(caption(SymptomGroup.GENITAL)));
	}

	// --- Nested dependents ----------------------------------------------------------------------------------------------

	@Test
	void shouldPlaceNestedDependentAfterItsParentAndShowItOnlyWhileTheChainIsYes() {
		// unexplainedBleeding -> otherHemorrhagicSymptoms -> otherHemorrhagicSymptomsText
		NullableOptionGroup bleeding = symptom(SymptomsDto.UNEXPLAINED_BLEEDING);
		NullableOptionGroup otherHemorrhagic = symptom(SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS);
		TextField text = text(SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS_TEXT);

		build(bleeding, otherHemorrhagic, text);
		assertFalse(isAttached(otherHemorrhagic));

		bleeding.setNullableValue(SymptomState.YES);
		assertTrue(isAttached(otherHemorrhagic));
		assertFalse(isAttached(text));

		otherHemorrhagic.setNullableValue(SymptomState.YES);
		assertTrue(isAttached(text));
		VerticalLayout slot = (VerticalLayout) rowOf(text).getParent();
		assertSame(slot, rowOf(otherHemorrhagic).getParent());
		assertTrue(slot.getComponentIndex(rowOf(otherHemorrhagic)) < slot.getComponentIndex(rowOf(text)));

		bleeding.setNullableValue(SymptomState.NO);
		assertFalse(isAttached(otherHemorrhagic));
		assertFalse(isAttached(text));
	}

	// --- Custom show conditions -----------------------------------------------------------------------------------------

	@Test
	void shouldShowDropDownDependentOnlyForItsCustomValue() {
		// majorSite shows otherMajorSiteDetails only for InfectionSite.OTHER (customShowConditions)
		ComboBox majorSite = new ComboBox();
		majorSite.setId(SymptomsDto.MAJOR_SITE);
		majorSite.addItems((Object[]) InfectionSite.values());
		TextField details = text(SymptomsDto.OTHER_MAJOR_SITE_DETAILS);

		build(majorSite, details);

		majorSite.setValue(InfectionSite.GENITO_URINARY);
		assertFalse(isAttached(details));

		majorSite.setValue(InfectionSite.OTHER);
		assertTrue(isAttached(details));

		majorSite.setValue(null);
		assertFalse(isAttached(details));
	}

	// --- Companions -----------------------------------------------------------------------------------------------------

	@Test
	void shouldShowDependentNextToItsCompanion() {
		NullableOptionGroup lesions = symptom(SymptomsDto.LESIONS);
		NullableOptionGroup resemblesImg1 = symptom(SymptomsDto.LESIONS_RESEMBLE_IMG1);
		Label picture = new Label("picture 1");
		companions.put(SymptomsDto.LESIONS_RESEMBLE_IMG1, picture);

		build(lesions, resemblesImg1);
		lesions.setNullableValue(SymptomState.YES);

		HorizontalLayout row = rowOf(resemblesImg1);
		assertEquals(2, row.getComponentCount());
		assertSame(picture, row.getComponent(1));
	}

	// --- Helpers --------------------------------------------------------------------------------------------------------

	private void build(Field<?>... fields) {
		new SymptomGroupLayout(mainLayout, parkingLayout, companions).build(Arrays.asList(fields));
	}

	private static NullableOptionGroup symptom(String propertyId) {
		NullableOptionGroup field = new NullableOptionGroup(propertyId, Arrays.asList(SymptomState.values()));
		field.setId(propertyId);
		return field;
	}

	private static NullableOptionGroup yesNoUnknown(String propertyId) {
		NullableOptionGroup field = new NullableOptionGroup(propertyId, Arrays.asList(YesNoUnknown.values()));
		field.setId(propertyId);
		return field;
	}

	private static TextField text(String propertyId) {
		TextField field = new TextField(propertyId);
		field.setId(propertyId);
		return field;
	}

	private static String caption(SymptomGroup group) {
		return I18nProperties.getEnumCaption(group);
	}

	/**
	 * True when the component is connected to the main layout and it and all its parents are visible. A dependent in a
	 * detached slot has no path to the main layout.
	 */
	private boolean isAttached(Component component) {
		for (Component current = component; current != null; current = current.getParent()) {
			if (!current.isVisible()) {
				return false;
			}
			if (current == mainLayout) {
				return true;
			}
		}
		return false;
	}

	/** The titles of all sections, top to bottom. A section is a VerticalLayout in the main layout starting with a Label. */
	private List<String> sectionTitles() {
		List<String> titles = new ArrayList<>();
		for (Component section : mainLayout) {
			if (section instanceof VerticalLayout
				&& ((VerticalLayout) section).getComponentCount() > 0
				&& ((VerticalLayout) section).getComponent(0) instanceof Label) {
				titles.add(((Label) ((VerticalLayout) section).getComponent(0)).getValue());
			}
		}
		return titles;
	}

	private boolean isSectionDisplayed(SymptomGroup group) {
		for (Component section : mainLayout) {
			if (section instanceof VerticalLayout
				&& ((VerticalLayout) section).getComponentCount() > 0
				&& ((VerticalLayout) section).getComponent(0) instanceof Label
				&& caption(group).equals(((Label) ((VerticalLayout) section).getComponent(0)).getValue())) {
				return section.isVisible();
			}
		}
		return false;
	}

	private String titleOfSectionHolding(Component component) {
		return ((Label) sectionHolding(component).getComponent(0)).getValue();
	}

	/** The section (direct child of the main layout) a placed component belongs to. */
	private VerticalLayout sectionHolding(Component component) {
		Component current = component;
		while (current.getParent() != mainLayout) {
			current = current.getParent();
		}
		return (VerticalLayout) current;
	}

	private static HorizontalLayout rowOf(Component field) {
		return (HorizontalLayout) field.getParent();
	}

	private static List<Field<?>> fieldsOf(HorizontalLayout row) {
		List<Component> components = new ArrayList<>();
		row.forEach(components::add);
		return components.stream().filter(c -> c instanceof Field).map(c -> (Field<?>) c).collect(Collectors.toList());
	}

	/** Index of a row or slot inside the rows container of its group. */
	private static int indexInGroup(Component rowOrSlot) {
		return ((VerticalLayout) rowOrSlot.getParent()).getComponentIndex(rowOrSlot);
	}
}
