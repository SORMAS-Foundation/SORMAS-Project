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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.symptoms.ClinicalPresentationStatus;
import de.symeda.sormas.api.symptoms.CongenitalHeartDiseaseType;
import de.symeda.sormas.api.symptoms.DiagnosisType;
import de.symeda.sormas.api.symptoms.InfectionSite;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsContext;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.SymptomGroup;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.ViewMode;

/**
 * Tests the symptom grouping of {@link SymptomsForm} (built by {@link SymptomGroupLayout}) and the Diphtheria specific
 * behaviour. A field counts as displayed only when it and all its parents up to the form are visible; a hidden dependent
 * is not hidden with setVisible(false), its slot is detached from the group instead.
 */
class SymptomsFormTest extends AbstractUiBeanTest {

	// --- Diphtheria: Clinical presentation and System disorders ---------------------------------------------------------

	@Test
	void shouldDisplayClinicalPresentationSectionForDiphtheria() {
		SymptomsForm form = createForm(Disease.DIPHTHERIA);

		assertTrue(isLabelDisplayed(form, I18nProperties.getEnumCaption(SymptomGroup.CLINICAL_PRESENTATION)));
		assertTrue(isDisplayed(form, form.getField(SymptomsDto.CONJUNCTIVAL_PRESENTATION)));
		assertTrue(isDisplayed(form, form.getField(SymptomsDto.CUTANEOUS_PRESENTATION)));
		assertTrue(isDisplayed(form, form.getField(SymptomsDto.RESPIRATORY_WITH_MEMBRANE)));
		assertTrue(isDisplayed(form, form.getField(SymptomsDto.OTHER_CLINICAL_CRITERIA)));
		assertTrue(isDisplayed(form, form.getField(SymptomsDto.UNKNOWN_CLINICAL_CRITERIA)));
	}

	@Test
	void shouldDisplayComplicationDiagnosisFieldsForDiphtheria() {
		SymptomsForm form = createForm(Disease.DIPHTHERIA);

		assertTrue(isLabelDisplayed(form, I18nProperties.getEnumCaption(SymptomGroup.SYSTEM_DISORDER)));
		assertTrue(isDisplayed(form, form.getField(SymptomsDto.NO_COMPLICATION_DIAGNOSIS)));
		assertTrue(isDisplayed(form, form.getField(SymptomsDto.CARDIAC_DISORDER)));
		assertTrue(isDisplayed(form, form.getField(SymptomsDto.NEUROLOGICAL_COMPLICATION_DIAGNOSIS)));
		assertTrue(isDisplayed(form, form.getField(SymptomsDto.OTHER_COMPLICATION_DIAGNOSIS)));
		assertTrue(isDisplayed(form, form.getField(SymptomsDto.UNKNOWN_COMPLICATION_DIAGNOSIS)));
	}

	@Test
	void shouldNotOfferDiphtheriaFieldsForOtherDiseases() {
		SymptomsForm form = createForm(Disease.PERTUSSIS);

		// Clinical presentation is still shown for Pertussis because of asymptomatic, so only System disorders is Diphtheria specific
		assertFalse(isLabelDisplayed(form, I18nProperties.getEnumCaption(SymptomGroup.SYSTEM_DISORDER)));
		assertFalse(isDisplayed(form, form.getField(SymptomsDto.CONJUNCTIVAL_PRESENTATION)));
		assertFalse(isDisplayed(form, form.getField(SymptomsDto.NO_COMPLICATION_DIAGNOSIS)));
	}

	@Test
	void shouldShowOtherClinicalCriteriaTextOnlyWhenOtherClinicalCriteriaIsYes() {
		SymptomsForm form = createForm(Disease.DIPHTHERIA);
		NullableOptionGroup otherClinicalCriteria = form.getField(SymptomsDto.OTHER_CLINICAL_CRITERIA);
		TextField otherClinicalCriteriaText = form.getField(SymptomsDto.OTHER_CLINICAL_CRITERIA_TEXT);

		assertFalse(isDisplayed(form, otherClinicalCriteriaText));

		otherClinicalCriteria.setNullableValue(YesNoUnknown.YES);
		assertTrue(isDisplayed(form, otherClinicalCriteriaText));

		otherClinicalCriteriaText.setValue("Swollen neck");
		otherClinicalCriteria.setNullableValue(YesNoUnknown.NO);
		assertFalse(isDisplayed(form, otherClinicalCriteriaText));
		// a value the user can no longer see must not be saved
		assertTrue(otherClinicalCriteriaText.isEmpty());
	}

	@Test
	void shouldShowOtherComplicationDiagnosisTextOnlyWhenOtherComplicationDiagnosisIsYes() {
		SymptomsForm form = createForm(Disease.DIPHTHERIA);
		NullableOptionGroup otherComplicationDiagnosis = form.getField(SymptomsDto.OTHER_COMPLICATION_DIAGNOSIS);
		Field<?> otherComplicationDiagnosisText = form.getField(SymptomsDto.OTHER_COMPLICATION_DIAGNOSIS_TEXT);

		assertFalse(isDisplayed(form, otherComplicationDiagnosisText));

		otherComplicationDiagnosis.setNullableValue(YesNoUnknown.YES);
		assertTrue(isDisplayed(form, otherComplicationDiagnosisText));

		otherComplicationDiagnosis.setNullableValue(YesNoUnknown.UNKNOWN);
		assertFalse(isDisplayed(form, otherComplicationDiagnosisText));
	}

	@Test
	void shouldKeepSavedOtherClinicalCriteriaTextWhenLoadingExistingSymptoms() {
		SymptomsForm form = createForm(Disease.DIPHTHERIA);
		SymptomsDto symptoms = SymptomsDto.build();
		symptoms.setOtherClinicalCriteria(YesNoUnknown.YES);
		symptoms.setOtherClinicalCriteriaText("Swollen neck");

		assertDoesNotThrow(() -> form.setValue(symptoms));

		Field<?> otherClinicalCriteriaText = form.getField(SymptomsDto.OTHER_CLINICAL_CRITERIA_TEXT);
		assertTrue(isDisplayed(form, otherClinicalCriteriaText));
		assertTrue("Swollen neck".equals(otherClinicalCriteriaText.getValue()));
	}

	// --- Diphtheria: Clinical measurements ------------------------------------------------------------------------------

	@Test
	void shouldHideClinicalMeasurementsButKeepHintAndButtonsForDiphtheria() {
		SymptomsForm form = createForm(Disease.DIPHTHERIA);

		assertFalse(isDisplayed(form, form.getField(SymptomsDto.TEMPERATURE)));
		assertFalse(isDisplayed(form, form.getField(SymptomsDto.TEMPERATURE_SOURCE)));
		assertFalse(isLabelDisplayed(form, I18nProperties.getEnumCaption(SymptomGroup.CLINICAL_MEASUREMENTS)));

		assertTrue(isLabelDisplayed(form, I18nProperties.getString(Strings.messageSymptomsVisitHint)));
		assertTrue(isButtonDisplayed(form, I18nProperties.getCaption(Captions.actionClearAll)));
		assertTrue(isButtonDisplayed(form, I18nProperties.getCaption(Captions.symptomsSetClearedToNo)));
		assertTrue(isButtonDisplayed(form, I18nProperties.getCaption(Captions.symptomsSetClearedToUnknown)));
	}

	// --- First symptom drop-down ----------------------------------------------------------------------------------------

	@Test
	void shouldNotOfferAsymptomaticAsFirstSymptom() {
		SymptomsForm form = createForm(Disease.PERTUSSIS);
		NullableOptionGroup asymptomatic = form.getField(SymptomsDto.ASYMPTOMATIC);
		NullableOptionGroup fever = form.getField(SymptomsDto.FEVER);
		ComboBox onsetSymptom = form.getField(SymptomsDto.ONSET_SYMPTOM);

		asymptomatic.setNullableValue(SymptomState.YES);
		assertFalse(onsetSymptom.getItemIds().contains(asymptomatic.getCaption()));

		// a real symptom is still offered
		fever.setNullableValue(SymptomState.YES);
		assertTrue(onsetSymptom.getItemIds().contains(fever.getCaption()));
	}

	// --- Parent/child layout --------------------------------------------------------------------------------------------

	@Test
	void shouldShowCrossGroupDependentInItsOwnGroup() {
		// jaundice is in GASTROINTESTINAL, its dependent jaundiceWithin24HoursOfBirth in METABOLIC
		SymptomsForm form = createForm(Disease.CONGENITAL_RUBELLA);
		NullableOptionGroup jaundice = form.getField(SymptomsDto.JAUNDICE);
		Field<?> jaundiceWithin24HoursOfBirth = form.getField(SymptomsDto.JAUNDICE_WITHIN_24_HOURS_OF_BIRTH);

		assertFalse(isDisplayed(form, jaundiceWithin24HoursOfBirth));

		jaundice.setNullableValue(SymptomState.YES);
		assertTrue(isDisplayed(form, jaundiceWithin24HoursOfBirth));

		jaundice.setNullableValue(SymptomState.NO);
		assertFalse(isDisplayed(form, jaundiceWithin24HoursOfBirth));
	}

	@Test
	void shouldClearCrossGroupDependentWhenParentIsNotYes() {
		SymptomsForm form = createForm(Disease.CONGENITAL_RUBELLA);
		NullableOptionGroup jaundice = form.getField(SymptomsDto.JAUNDICE);
		NullableOptionGroup jaundiceWithin24HoursOfBirth = form.getField(SymptomsDto.JAUNDICE_WITHIN_24_HOURS_OF_BIRTH);

		jaundice.setNullableValue(SymptomState.YES);
		jaundiceWithin24HoursOfBirth.setNullableValue(YesNoUnknown.YES);
		jaundice.setNullableValue(SymptomState.NO);

		assertTrue(jaundiceWithin24HoursOfBirth.getNullableValue() == null);
	}

	@Test
	void shouldShowNestedDependentOnlyWhileAllParentsAreYes() {
		// unexplainedBleeding -> otherHemorrhagicSymptoms -> otherHemorrhagicSymptomsText
		SymptomsForm form = createForm(Disease.EVD);
		NullableOptionGroup unexplainedBleeding = form.getField(SymptomsDto.UNEXPLAINED_BLEEDING);
		NullableOptionGroup otherHemorrhagicSymptoms = form.getField(SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS);
		Field<?> otherHemorrhagicSymptomsText = form.getField(SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS_TEXT);

		assertFalse(isDisplayed(form, otherHemorrhagicSymptoms));
		assertFalse(isDisplayed(form, otherHemorrhagicSymptomsText));

		unexplainedBleeding.setNullableValue(SymptomState.YES);
		assertTrue(isDisplayed(form, otherHemorrhagicSymptoms));
		assertFalse(isDisplayed(form, otherHemorrhagicSymptomsText));

		otherHemorrhagicSymptoms.setNullableValue(SymptomState.YES);
		assertTrue(isDisplayed(form, otherHemorrhagicSymptomsText));

		// hiding the top parent hides the whole chain
		unexplainedBleeding.setNullableValue(SymptomState.NO);
		assertFalse(isDisplayed(form, otherHemorrhagicSymptoms));
		assertFalse(isDisplayed(form, otherHemorrhagicSymptomsText));
	}

	@Test
	void shouldPlaceEveryVisibleDiphtheriaFieldInTheLayout() {
		// fields hidden before SymptomGroupLayout.build() are never placed (see the jaundice bug),
		// so every field that is visible and not a dependent must be reachable from the form
		SymptomsForm form = createForm(Disease.DIPHTHERIA);

		for (String fieldId : new String[] {
			SymptomsDto.CONJUNCTIVAL_PRESENTATION,
			SymptomsDto.GENITAL_PRESENTATION,
			SymptomsDto.NASAL_DISCHARGE_CLEAR_TO_BLOODY,
			SymptomsDto.RESPIRATORY_AND_CUTANEOUS_PRESENTATION,
			SymptomsDto.RESPIRATORY_WITHOUT_MEMBRANE,
			SymptomsDto.NEUROLOGICAL_AND_CARDIAC_DISORDER }) {
			assertTrue(isDisplayed(form, form.getField(fieldId)), fieldId + " is not displayed");
		}
	}

	// --- Skin rash onset date (replaces the commented SKIN_RASH listener in SymptomsForm) --------------------------------
	// skinRashOnsetDate: @Diseases(MEASLES), @HideForCountriesExcept(LUXEMBOURG), @DependantOn(SKIN_RASH), group SKIN

	@Test
	void shouldShowSkinRashOnsetDateOnlyWhileSkinRashIsYesForLuxembourgMeasles() {
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		SymptomsForm form = createForm(Disease.MEASLES);
		NullableOptionGroup skinRash = form.getField(SymptomsDto.SKIN_RASH);
		DateField skinRashOnsetDate = form.getField(SymptomsDto.SKIN_RASH_ONSET_DATE);

		assertTrue(isDisplayed(form, skinRash));
		assertFalse(isDisplayed(form, skinRashOnsetDate));

		skinRash.setNullableValue(SymptomState.YES);
		assertTrue(isDisplayed(form, skinRashOnsetDate));

		skinRash.setNullableValue(SymptomState.NO);
		assertFalse(isDisplayed(form, skinRashOnsetDate));

		skinRash.setNullableValue(SymptomState.UNKNOWN);
		assertFalse(isDisplayed(form, skinRashOnsetDate));
	}

	@Test
	void shouldClearSkinRashOnsetDateWhenSkinRashIsNoLongerYes() {
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		SymptomsForm form = createForm(Disease.MEASLES);
		NullableOptionGroup skinRash = form.getField(SymptomsDto.SKIN_RASH);
		DateField skinRashOnsetDate = form.getField(SymptomsDto.SKIN_RASH_ONSET_DATE);

		skinRash.setNullableValue(SymptomState.YES);
		skinRashOnsetDate.setValue(new Date());
		skinRash.setNullableValue(SymptomState.NO);

		assertNull(skinRashOnsetDate.getValue());
	}

	@Test
	void shouldShowSavedSkinRashOnsetDateWhenLoadingExistingSymptoms() {
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		SymptomsForm form = createForm(Disease.MEASLES);
		Date onsetDate = new Date();
		SymptomsDto symptoms = SymptomsDto.build();
		symptoms.setSkinRash(SymptomState.YES);
		symptoms.setSkinRashOnsetDate(onsetDate);

		assertDoesNotThrow(() -> form.setValue(symptoms));

		DateField skinRashOnsetDate = form.getField(SymptomsDto.SKIN_RASH_ONSET_DATE);
		assertTrue(isDisplayed(form, skinRashOnsetDate));
		assertEquals(onsetDate, skinRashOnsetDate.getValue());
	}

	@Test
	void shouldUseSkinRashOnsetDateCaptionOnTheFieldItself() {
		// the removed code toggled a separate caption label; the field now carries the caption
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		SymptomsForm form = createForm(Disease.MEASLES);
		NullableOptionGroup skinRash = form.getField(SymptomsDto.SKIN_RASH);
		skinRash.setNullableValue(SymptomState.YES);

		DateField skinRashOnsetDate = form.getField(SymptomsDto.SKIN_RASH_ONSET_DATE);
		assertEquals(I18nProperties.getCaption(Captions.Symptoms_skinRashOnsetDate), skinRashOnsetDate.getCaption());
	}

	@Test
	void shouldNeverShowSkinRashOnsetDateOutsideLuxembourg() {
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_FRANCE);
		SymptomsForm form = createForm(Disease.MEASLES);
		NullableOptionGroup skinRash = form.getField(SymptomsDto.SKIN_RASH);

		skinRash.setNullableValue(SymptomState.YES);

		assertFalse(isDisplayed(form, form.getField(SymptomsDto.SKIN_RASH_ONSET_DATE)));
	}

	@Test
	void shouldNeverShowSkinRashOnsetDateForOtherDiseasesInLuxembourg() {
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		SymptomsForm form = createForm(Disease.RUBELLA);
		NullableOptionGroup skinRash = form.getField(SymptomsDto.SKIN_RASH);

		skinRash.setNullableValue(SymptomState.YES);

		assertFalse(isDisplayed(form, form.getField(SymptomsDto.SKIN_RASH_ONSET_DATE)));
	}

	// --- Group headings (replaces the commented symptomGroupVisibility() in SymptomsForm) -------------------------------

	@Test
	void shouldOnlyDisplayGroupHeadingsThatHaveADisplayedField() {
		for (Disease disease : new Disease[] {
			Disease.DIPHTHERIA,
			Disease.MEASLES,
			Disease.CONGENITAL_RUBELLA,
			Disease.EVD }) {
			assertEveryDisplayedSectionHasADisplayedField(createForm(disease), disease.name());
		}
	}

	@Test
	void shouldOnlyDisplayGroupHeadingsThatHaveADisplayedFieldForLuxembourgTuberculosis() {
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);

		assertEveryDisplayedSectionHasADisplayedField(createForm(Disease.TUBERCULOSIS), "Luxembourg TUBERCULOSIS");
	}

	@Test
	void shouldHideSectionHeadingAgainWhenItsOnlyDependentIsHidden() {
		// Metabolic only holds jaundiceWithin24HoursOfBirth, a dependent of jaundice (Gastrointestinal)
		SymptomsForm form = createForm(Disease.CONGENITAL_RUBELLA);
		NullableOptionGroup jaundice = form.getField(SymptomsDto.JAUNDICE);
		String metabolic = I18nProperties.getEnumCaption(SymptomGroup.METABOLIC);

		assertFalse(isLabelDisplayed(form, metabolic));
		jaundice.setNullableValue(SymptomState.YES);
		assertTrue(isLabelDisplayed(form, metabolic));
		jaundice.setNullableValue(SymptomState.NO);
		assertFalse(isLabelDisplayed(form, metabolic));
	}

	// --- "Other" selected opens its text field --------------------------------------------------------------------------

	@Test
	void shouldOpenOtherClinicalPresentationTextWhenOtherClinicalPresentationIsYes() {
		SymptomsForm form = createForm(Disease.MEASLES);
		NullableOptionGroup otherClinicalPresentation = form.getField(SymptomsDto.OTHER_CLINICAL_PRESENTATION);
		TextField text = form.getField(SymptomsDto.OTHER_CLINICAL_PRESENTATION_TEXT);

		assertTrue(isDisplayed(form, otherClinicalPresentation));
		assertFalse(isDisplayed(form, text));

		otherClinicalPresentation.setNullableValue(SymptomState.YES);
		assertTrue(isDisplayed(form, text));

		text.setValue("Lymphadenopathy");
		otherClinicalPresentation.setNullableValue(SymptomState.NO);
		assertFalse(isDisplayed(form, text));
		assertTrue(text.isEmpty());
	}

	@Test
	void shouldAlwaysDisplayOtherClinicalPresentationTextForLuxembourgTuberculosis() {
		// otherClinicalPresentation is not available for TB, so its text has no parent and is a standalone field
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		SymptomsForm form = createForm(Disease.TUBERCULOSIS);

		assertFalse(isDisplayed(form, form.getField(SymptomsDto.OTHER_CLINICAL_PRESENTATION)));
		assertTrue(isDisplayed(form, form.getField(SymptomsDto.OTHER_CLINICAL_PRESENTATION_TEXT)));
	}

	@Test
	void shouldShowSpecifyOtherSymptomsUnderOtherGroupForLuxembourgTuberculosis() {
		// "Specify other symptoms" (@SymptomGrouping(OTHER)) is shown in the Other section, not in Clinical presentation
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		SymptomsForm form = createForm(Disease.TUBERCULOSIS);
		Field<?> specifyOtherSymptoms = form.getField(SymptomsDto.OTHER_CLINICAL_PRESENTATION_TEXT);

		assertInSection(form, SymptomsDto.OTHER_CLINICAL_PRESENTATION_TEXT, SymptomGroup.OTHER);
		assertTrue(isLabelDisplayed(form, I18nProperties.getEnumCaption(SymptomGroup.OTHER)));
		assertNotSame(sectionOf(form.getField(SymptomsDto.CLINICAL_PRESENTATION_STATUS)), sectionOf(specifyOtherSymptoms));
		assertEquals(
			I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, SymptomsDto.OTHER_CLINICAL_PRESENTATION_TEXT),
			specifyOtherSymptoms.getCaption());
	}

	@Test
	void shouldKeepSpecifyOtherSymptomsValueForLuxembourgTuberculosis() {
		// without a parent, nothing hides or clears the text: an entered or saved value stays
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		SymptomsForm form = createForm(Disease.TUBERCULOSIS);
		SymptomsDto symptoms = SymptomsDto.build();
		symptoms.setOtherClinicalPresentationText("Night sweats");

		assertDoesNotThrow(() -> form.setValue(symptoms));

		Field<?> specifyOtherSymptoms = form.getField(SymptomsDto.OTHER_CLINICAL_PRESENTATION_TEXT);
		assertTrue(isDisplayed(form, specifyOtherSymptoms));
		assertEquals("Night sweats", specifyOtherSymptoms.getValue());

		// changing other localisation fields does not touch it
		ComboBox diagnosis = form.getField(SymptomsDto.DIAGNOSIS);
		diagnosis.setValue(DiagnosisType.EXTRAPULMONARY);
		diagnosis.setValue(DiagnosisType.PULMONARY);
		assertEquals("Night sweats", specifyOtherSymptoms.getValue());
	}

	// --- Luxembourg Tuberculosis localisation (replaces the commented setVisibleWhen block after the layout build) --------
	// MAJOR_SITE when DIAGNOSIS = EXTRAPULMONARY       -> @DependantOn(DIAGNOSIS) + customShowConditions
	// OTHER_MAJOR_SITE_DETAILS when MAJOR_SITE = OTHER -> @DependantOn(MAJOR_SITE) + customShowConditions
	// OTHER_MINOR_SITE_DETAILS when MINOR_SITE = OTHER -> @DependantOn(MINOR_SITE) + customShowConditions
	// setVisibleWhen(..., true) also cleared the hidden field, so every test checks the value is cleared as well

	@Test
	void shouldOpenMajorSiteOnlyForExtrapulmonaryDiagnosisForLuxembourgTuberculosis() {
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		SymptomsForm form = createForm(Disease.TUBERCULOSIS);
		ComboBox diagnosis = form.getField(SymptomsDto.DIAGNOSIS);
		ComboBox majorSite = form.getField(SymptomsDto.MAJOR_SITE);

		assertTrue(isLabelDisplayed(form, I18nProperties.getEnumCaption(SymptomGroup.LOCALISATION)));
		assertTrue(isDisplayed(form, diagnosis));
		assertFalse(isDisplayed(form, majorSite));

		diagnosis.setValue(DiagnosisType.EXTRAPULMONARY);
		assertTrue(isDisplayed(form, majorSite));

		majorSite.setValue(InfectionSite.OTHER);
		diagnosis.setValue(DiagnosisType.PULMONARY);
		assertFalse(isDisplayed(form, majorSite));
		assertNull(majorSite.getValue());
	}

	@Test
	void shouldOpenOtherMajorSiteDetailsWhenMajorSiteIsOtherForLuxembourgTuberculosis() {
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		SymptomsForm form = createForm(Disease.TUBERCULOSIS);
		ComboBox diagnosis = form.getField(SymptomsDto.DIAGNOSIS);
		ComboBox majorSite = form.getField(SymptomsDto.MAJOR_SITE);
		TextField details = form.getField(SymptomsDto.OTHER_MAJOR_SITE_DETAILS);

		diagnosis.setValue(DiagnosisType.EXTRAPULMONARY);
		assertFalse(isDisplayed(form, details));

		majorSite.setValue(InfectionSite.OTHER);
		assertTrue(isDisplayed(form, details));

		details.setValue("Pericardium");
		majorSite.setValue(anotherSiteThanOther(majorSite));
		assertFalse(isDisplayed(form, details));
		assertTrue(details.isEmpty());

		// nested: hiding Major site also hides its details
		majorSite.setValue(InfectionSite.OTHER);
		diagnosis.setValue(DiagnosisType.PULMONARY);
		assertFalse(isDisplayed(form, details));
	}

	@Test
	void shouldOpenOtherMinorSiteDetailsWhenMinorSiteIsOtherForLuxembourgTuberculosis() {
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		SymptomsForm form = createForm(Disease.TUBERCULOSIS);
		ComboBox minorSite = form.getField(SymptomsDto.MINOR_SITE);
		TextField details = form.getField(SymptomsDto.OTHER_MINOR_SITE_DETAILS);

		assertTrue(isDisplayed(form, minorSite));
		assertFalse(isDisplayed(form, details));

		minorSite.setValue(InfectionSite.OTHER);
		assertTrue(isDisplayed(form, details));

		details.setValue("Skin");
		minorSite.setValue(anotherSiteThanOther(minorSite));
		assertFalse(isDisplayed(form, details));
		assertTrue(details.isEmpty());

		minorSite.setValue(InfectionSite.OTHER);
		assertTrue(isDisplayed(form, details));
		minorSite.setValue(null);
		assertFalse(isDisplayed(form, details));
	}

	@Test
	void shouldShowMinorSiteIndependentlyOfDiagnosisForLuxembourgTuberculosis() {
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		SymptomsForm form = createForm(Disease.TUBERCULOSIS);
		ComboBox diagnosis = form.getField(SymptomsDto.DIAGNOSIS);
		ComboBox minorSite = form.getField(SymptomsDto.MINOR_SITE);

		diagnosis.setValue(DiagnosisType.PULMONARY);
		assertTrue(isDisplayed(form, minorSite));

		diagnosis.setValue(DiagnosisType.EXTRAPULMONARY);
		assertTrue(isDisplayed(form, minorSite));
	}

	@Test
	void shouldShowSavedLocalisationWhenLoadingExistingTuberculosisSymptoms() {
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		SymptomsForm form = createForm(Disease.TUBERCULOSIS);
		SymptomsDto symptoms = SymptomsDto.build();
		symptoms.setDiagnosis(DiagnosisType.EXTRAPULMONARY);
		symptoms.setMajorSite(InfectionSite.OTHER);
		symptoms.setOtherMajorSiteDetails("Pericardium");
		symptoms.setMinorSite(InfectionSite.OTHER);
		symptoms.setOtherMinorSiteDetails("Skin");

		assertDoesNotThrow(() -> form.setValue(symptoms));

		assertTrue(isDisplayed(form, form.getField(SymptomsDto.MAJOR_SITE)));
		assertEquals(InfectionSite.OTHER, form.getField(SymptomsDto.MAJOR_SITE).getValue());
		assertTrue(isDisplayed(form, form.getField(SymptomsDto.OTHER_MAJOR_SITE_DETAILS)));
		assertEquals("Pericardium", form.getField(SymptomsDto.OTHER_MAJOR_SITE_DETAILS).getValue());
		assertTrue(isDisplayed(form, form.getField(SymptomsDto.OTHER_MINOR_SITE_DETAILS)));
		assertEquals("Skin", form.getField(SymptomsDto.OTHER_MINOR_SITE_DETAILS).getValue());
	}

	@Test
	void shouldHideMajorSiteWhenLoadingSavedPulmonaryDiagnosis() {
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		SymptomsForm form = createForm(Disease.TUBERCULOSIS);
		SymptomsDto symptoms = SymptomsDto.build();
		symptoms.setDiagnosis(DiagnosisType.PULMONARY);

		form.setValue(symptoms);

		assertFalse(isDisplayed(form, form.getField(SymptomsDto.MAJOR_SITE)));
		assertFalse(isDisplayed(form, form.getField(SymptomsDto.OTHER_MAJOR_SITE_DETAILS)));
	}

	@Test
	void shouldNotOfferLocalisationOutsideLuxembourg() {
		// the localisation fields are only added to the form for Luxembourg Tuberculosis
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_FRANCE);
		SymptomsForm form = createForm(Disease.TUBERCULOSIS);

		for (String fieldId : new String[] {
			SymptomsDto.DIAGNOSIS,
			SymptomsDto.MAJOR_SITE,
			SymptomsDto.MINOR_SITE,
			SymptomsDto.OTHER_MAJOR_SITE_DETAILS,
			SymptomsDto.OTHER_MINOR_SITE_DETAILS }) {
			assertFalse(isDisplayed(form, form.getField(fieldId)), fieldId + " is displayed");
		}
		assertFalse(isLabelDisplayed(form, I18nProperties.getEnumCaption(SymptomGroup.LOCALISATION)));
	}

	/** Any selectable site other than OTHER, taken from the drop-down itself, so the test follows InfectionSite.filter(). */
	private static InfectionSite anotherSiteThanOther(ComboBox siteField) {
		return siteField.getItemIds()
			.stream()
			.map(InfectionSite.class::cast)
			.filter(site -> site != InfectionSite.OTHER)
			.findFirst()
			.orElseThrow(() -> new AssertionError("no site other than OTHER in " + siteField.getId()));
	}

	private static void assertEveryDisplayedSectionHasADisplayedField(SymptomsForm form, String context) {
		Set<String> groupTitles = Arrays.stream(SymptomGroup.values()).map(I18nProperties::getEnumCaption).collect(Collectors.toSet());
		List<VerticalLayout> sections = new ArrayList<>();
		collectSections(form, groupTitles, sections);

		assertFalse(sections.isEmpty(), context + ": no group section found");
		for (VerticalLayout section : sections) {
			String title = ((Label) section.getComponent(0)).getValue();
			if (isDisplayed(form, section)) {
				assertTrue(anyDisplayed(form, section, c -> c instanceof Field), context + ": section '" + title + "' is shown without a field");
			}
		}
	}

	/** A group section is a VerticalLayout whose first component is a Label with the caption of a SymptomGroup. */
	private static void collectSections(Component root, Set<String> groupTitles, List<VerticalLayout> sections) {
		if (root instanceof VerticalLayout
			&& ((VerticalLayout) root).getComponentCount() > 0
			&& ((VerticalLayout) root).getComponent(0) instanceof Label
			&& groupTitles.contains(((Label) ((VerticalLayout) root).getComponent(0)).getValue())) {
			sections.add((VerticalLayout) root);
		}
		if (root instanceof HasComponents) {
			for (Component child : (HasComponents) root) {
				collectSections(child, groupTitles, sections);
			}
		}

	}

	// --- Luxembourg Tuberculosis form (replaces the old commented TB block with its heading labels) ----------------------
	// clinicalPresentationHeadingLabel / localisationHeadingLabel -> group sections of SymptomGroupLayout
	// clinicalMeasurementsHeadingLabel, respiratory and complications headings -> no section without a displayed field
	// symptomsHint and clear/set buttons -> hidden by the active isLuxTuberculosis code before the layout build
	// diagnosis / major / minor site -> see the localisation tests above

	@Test
	void shouldDisplayClinicalPresentationAndLocalisationSectionsForLuxembourgTuberculosis() {
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		SymptomsForm form = createForm(Disease.TUBERCULOSIS);

		assertTrue(isLabelDisplayed(form, I18nProperties.getEnumCaption(SymptomGroup.CLINICAL_PRESENTATION)));
		assertTrue(isDisplayed(form, form.getField(SymptomsDto.CLINICAL_PRESENTATION_STATUS)));
		assertTrue(isLabelDisplayed(form, I18nProperties.getEnumCaption(SymptomGroup.LOCALISATION)));
	}

	@Test
	void shouldHideClinicalMeasurementsForLuxembourgTuberculosis() {
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		SymptomsForm form = createForm(Disease.TUBERCULOSIS);

		assertFalse(isDisplayed(form, form.getField(SymptomsDto.TEMPERATURE)));
		assertFalse(isDisplayed(form, form.getField(SymptomsDto.TEMPERATURE_SOURCE)));
		assertFalse(isLabelDisplayed(form, I18nProperties.getEnumCaption(SymptomGroup.CLINICAL_MEASUREMENTS)));
	}

	@Test
	void shouldHideSymptomsHintAndClearSetButtonsForLuxembourgTuberculosis() {
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		SymptomsForm form = createForm(Disease.TUBERCULOSIS);

		assertFalse(isLabelDisplayed(form, I18nProperties.getString(Strings.messageSymptomsVisitHint)));
		assertFalse(isButtonDisplayed(form, I18nProperties.getCaption(Captions.actionClearAll)));
		assertFalse(isButtonDisplayed(form, I18nProperties.getCaption(Captions.symptomsSetClearedToNo)));
		assertFalse(isButtonDisplayed(form, I18nProperties.getCaption(Captions.symptomsSetClearedToUnknown)));
	}

	@Test
	void shouldKeepSymptomsHintAndClearSetButtonsForTuberculosisOutsideLuxembourg() {
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_FRANCE);
		SymptomsForm form = createForm(Disease.TUBERCULOSIS);

		assertTrue(isLabelDisplayed(form, I18nProperties.getString(Strings.messageSymptomsVisitHint)));
		assertTrue(isButtonDisplayed(form, I18nProperties.getCaption(Captions.actionClearAll)));
	}

	@Test
	void shouldNotOfferFirstSymptomForLuxembourgTuberculosis() {
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		SymptomsForm form = createForm(Disease.TUBERCULOSIS);

		assertFalse(isDisplayed(form, form.getField(SymptomsDto.ONSET_SYMPTOM)));
	}

	@Test
	void shouldEnableOnsetDateOnlyWhenDateOfOnsetIsKnownForLuxembourgTuberculosis() {
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		SymptomsForm form = createForm(Disease.TUBERCULOSIS);
		Field<YesNoUnknown> dateOfOnsetKnown = form.getField(SymptomsDto.DATE_OF_ONSET_KNOWN);
		DateField onsetDate = form.getField(SymptomsDto.ONSET_DATE);

		assertTrue(isDisplayed(form, dateOfOnsetKnown));
		assertFalse(onsetDate.isEnabled());

		dateOfOnsetKnown.setValue(YesNoUnknown.YES);
		assertTrue(onsetDate.isEnabled());

		onsetDate.setValue(new Date());
		dateOfOnsetKnown.setValue(YesNoUnknown.NO);
		assertFalse(onsetDate.isEnabled());
		// setEnabledWhen(..., true) clears the disabled date
		assertNull(onsetDate.getValue());
	}

	@Test
	void shouldUseDiseaseSpecificCaptionForCompatibleClinicalPresentationStatus() {
		MockProducer.mockProperty(ConfigFacadeEjb.COUNTRY_LOCALE, CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		SymptomsForm form = createForm(Disease.TUBERCULOSIS);
		ComboBox clinicalPresentationStatus = form.getField(SymptomsDto.CLINICAL_PRESENTATION_STATUS);

		assertEquals(
			ClinicalPresentationStatus.COMPATIBLE.buildCaption(Disease.TUBERCULOSIS.toShortString()),
			clinicalPresentationStatus.getItemCaption(ClinicalPresentationStatus.COMPATIBLE));
	}

	// --- Unexplained bleeding (replaces the commented unexpectedBleeding listener calling symptomGroupVisibility()) -----
	// The dependents of unexplainedBleeding (HAEMATOLOGICAL) belong to several groups, e.g. for EVD:
	// bleedingVagina -> GENITAL, bloodUrine -> URINARY, bloodyBlackStool -> GASTROINTESTINAL,
	// otherHemorrhagicSymptoms -> HAEMATOLOGICAL (same group as the parent)

	@Test
	void shouldShowBleedingDependentsInTheirOwnGroupWhenUnexplainedBleedingIsYes() {
		SymptomsForm form = createForm(Disease.EVD);
		NullableOptionGroup unexplainedBleeding = form.getField(SymptomsDto.UNEXPLAINED_BLEEDING);

		unexplainedBleeding.setNullableValue(SymptomState.YES);

		assertInSection(form, SymptomsDto.BLEEDING_VAGINA, SymptomGroup.GENITAL);
		assertInSection(form, SymptomsDto.BLOOD_URINE, SymptomGroup.URINARY);
		assertInSection(form, SymptomsDto.BLOODY_BLACK_STOOL, SymptomGroup.GASTROINTESTINAL);
		assertInSection(form, SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS, SymptomGroup.HAEMATOLOGICAL);
	}

	@Test
	void shouldHideAndClearBleedingDependentsWhenUnexplainedBleedingIsNoLongerYes() {
		SymptomsForm form = createForm(Disease.EVD);
		NullableOptionGroup unexplainedBleeding = form.getField(SymptomsDto.UNEXPLAINED_BLEEDING);
		NullableOptionGroup bleedingVagina = form.getField(SymptomsDto.BLEEDING_VAGINA);
		NullableOptionGroup bloodUrine = form.getField(SymptomsDto.BLOOD_URINE);

		assertFalse(isDisplayed(form, bleedingVagina));
		assertFalse(isDisplayed(form, bloodUrine));

		unexplainedBleeding.setNullableValue(SymptomState.YES);
		bleedingVagina.setNullableValue(SymptomState.YES);
		bloodUrine.setNullableValue(SymptomState.NO);

		unexplainedBleeding.setNullableValue(SymptomState.NO);

		assertFalse(isDisplayed(form, bleedingVagina));
		assertFalse(isDisplayed(form, bloodUrine));
		assertNull(bleedingVagina.getNullableValue());
		assertNull(bloodUrine.getNullableValue());
	}

	@Test
	void shouldUpdateGroupHeadingsWhenUnexplainedBleedingChanges() {
		// what symptomGroupVisibility() was called for: after every change no heading is shown without a field,
		// and the group of a shown bleeding dependent has its heading
		SymptomsForm form = createForm(Disease.EVD);
		NullableOptionGroup unexplainedBleeding = form.getField(SymptomsDto.UNEXPLAINED_BLEEDING);
		String genital = I18nProperties.getEnumCaption(SymptomGroup.GENITAL);

		assertEveryDisplayedSectionHasADisplayedField(form, "EVD before");

		unexplainedBleeding.setNullableValue(SymptomState.YES);
		assertEveryDisplayedSectionHasADisplayedField(form, "EVD bleeding Yes");
		assertTrue(isLabelDisplayed(form, genital));

		unexplainedBleeding.setNullableValue(SymptomState.NO);
		assertEveryDisplayedSectionHasADisplayedField(form, "EVD bleeding No");

		unexplainedBleeding.setNullableValue(SymptomState.UNKNOWN);
		assertEveryDisplayedSectionHasADisplayedField(form, "EVD bleeding Unknown");
	}

	@Test
	void shouldShowSavedBleedingDependentsWhenLoadingExistingSymptoms() {
		SymptomsForm form = createForm(Disease.EVD);
		SymptomsDto symptoms = SymptomsDto.build();
		symptoms.setUnexplainedBleeding(SymptomState.YES);
		symptoms.setBleedingVagina(SymptomState.YES);

		assertDoesNotThrow(() -> form.setValue(symptoms));

		assertInSection(form, SymptomsDto.BLEEDING_VAGINA, SymptomGroup.GENITAL);
		assertEquals(SymptomState.YES, ((NullableOptionGroup) form.getField(SymptomsDto.BLEEDING_VAGINA)).getNullableValue());
	}

	@Test
	void shouldShowDengueBleedingDependentsUnderUnexplainedBleeding() {
		SymptomsForm form = createForm(Disease.DENGUE);
		NullableOptionGroup unexplainedBleeding = form.getField(SymptomsDto.UNEXPLAINED_BLEEDING);

		assertFalse(isDisplayed(form, form.getField(SymptomsDto.SCANT_HEMORRHAGE)));
		assertFalse(isDisplayed(form, form.getField(SymptomsDto.ACUTE_BLEEDING)));

		unexplainedBleeding.setNullableValue(SymptomState.YES);

		assertInSection(form, SymptomsDto.SCANT_HEMORRHAGE, SymptomGroup.HAEMATOLOGICAL);
		assertInSection(form, SymptomsDto.ACUTE_BLEEDING, SymptomGroup.HAEMATOLOGICAL);
	}

	private static void assertInSection(SymptomsForm form, String fieldId, SymptomGroup group) {
		Field<?> field = form.getField(fieldId);
		assertTrue(isDisplayed(form, field), fieldId + " is not displayed");
		VerticalLayout section = sectionOf(field);
		assertTrue(section != null, fieldId + " is not in a group section");
		assertEquals(I18nProperties.getEnumCaption(group), ((Label) section.getComponent(0)).getValue(), fieldId);
	}

	// --- Lesions (replaces the commented lesions locations caption listener in SymptomsForm) ----------------------------
	// all lesion fields are @DependantOn(LESIONS), group SKIN; Monkeypox has them all, plus the resemblance pictures

	private static final String[] LESIONS_LOCATION_FIELD_IDS = {
		SymptomsDto.LESIONS_FACE,
		SymptomsDto.LESIONS_LEGS,
		SymptomsDto.LESIONS_SOLES_FEET,
		SymptomsDto.LESIONS_PALMS_HANDS,
		SymptomsDto.LESIONS_THORAX,
		SymptomsDto.LESIONS_ARMS,
		SymptomsDto.LESIONS_GENITALS,
		SymptomsDto.LESIONS_ALL_OVER_BODY };

	@Test
	void shouldShowLesionsLocationsOnlyWhileLesionsIsYes() {
		SymptomsForm form = createForm(Disease.MONKEYPOX);
		NullableOptionGroup lesions = form.getField(SymptomsDto.LESIONS);

		assertNoLesionsLocationDisplayed(form, "before an answer");

		lesions.setNullableValue(SymptomState.YES);
		for (String fieldId : LESIONS_LOCATION_FIELD_IDS) {
			assertTrue(isDisplayed(form, form.getField(fieldId)), fieldId + " is not displayed for Yes");
		}

		lesions.setNullableValue(SymptomState.NO);
		assertNoLesionsLocationDisplayed(form, "for No");

		lesions.setNullableValue(SymptomState.UNKNOWN);
		assertNoLesionsLocationDisplayed(form, "for Unknown");
	}

	@Test
	void shouldUncheckLesionsLocationsWhenLesionsIsNoLongerYes() {
		SymptomsForm form = createForm(Disease.MONKEYPOX);
		NullableOptionGroup lesions = form.getField(SymptomsDto.LESIONS);
		// the location fields are NullableOptionGroups: their raw value is a Set such as [true]
		NullableOptionGroup face = form.getField(SymptomsDto.LESIONS_FACE);
		NullableOptionGroup arms = form.getField(SymptomsDto.LESIONS_ARMS);

		lesions.setNullableValue(SymptomState.YES);
		face.setNullableValue(Boolean.TRUE);
		arms.setNullableValue(Boolean.TRUE);
		assertEquals(Boolean.TRUE, face.getNullableValue());

		lesions.setNullableValue(SymptomState.NO);

		assertNull(face.getNullableValue());
		assertNull(arms.getNullableValue());
	}

	@Test
	void shouldShowSavedLesionsLocationsWhenLoadingExistingSymptoms() {
		SymptomsForm form = createForm(Disease.MONKEYPOX);
		SymptomsDto symptoms = SymptomsDto.build();
		symptoms.setLesions(SymptomState.YES);
		symptoms.setLesionsFace(true);
		symptoms.setLesionsLegs(true);

		assertDoesNotThrow(() -> form.setValue(symptoms));

		assertTrue(isDisplayed(form, form.getField(SymptomsDto.LESIONS_FACE)));
		assertEquals(Boolean.TRUE, ((NullableOptionGroup) form.getField(SymptomsDto.LESIONS_FACE)).getNullableValue());
		assertEquals(Boolean.TRUE, ((NullableOptionGroup) form.getField(SymptomsDto.LESIONS_LEGS)).getNullableValue());
		assertFalse(Boolean.TRUE.equals(((NullableOptionGroup) form.getField(SymptomsDto.LESIONS_ARMS)).getNullableValue()));
	}

	@Test
	void shouldShowLesionsDetailsAndLocationsInTheSkinSectionOfLesions() {
		SymptomsForm form = createForm(Disease.MONKEYPOX);
		NullableOptionGroup lesions = form.getField(SymptomsDto.LESIONS);

		lesions.setNullableValue(SymptomState.YES);

		for (String fieldId : new String[] {
			SymptomsDto.LESIONS_SAME_STATE,
			SymptomsDto.LESIONS_SAME_SIZE,
			SymptomsDto.LESIONS_DEEP_PROFOUND,
			SymptomsDto.LESIONS_THAT_ITCH,
			SymptomsDto.LESIONS_FACE,
			SymptomsDto.LESIONS_ALL_OVER_BODY }) {
			assertInSection(form, fieldId, SymptomGroup.SKIN);
			assertSame(sectionOf(lesions), sectionOf(form.getField(fieldId)), fieldId);
		}
	}

	@Test
	void shouldShowMonkeypoxPicturesNextToTheirResemblanceFields() {
		SymptomsForm form = createForm(Disease.MONKEYPOX);
		NullableOptionGroup lesions = form.getField(SymptomsDto.LESIONS);

		lesions.setNullableValue(SymptomState.YES);

		for (String fieldId : new String[] {
			SymptomsDto.LESIONS_RESEMBLE_IMG1,
			SymptomsDto.LESIONS_RESEMBLE_IMG2,
			SymptomsDto.LESIONS_RESEMBLE_IMG3,
			SymptomsDto.LESIONS_RESEMBLE_IMG4 }) {
			Field<?> field = form.getField(fieldId);
			assertTrue(isDisplayed(form, field), fieldId + " is not displayed");
			HorizontalLayout row = (HorizontalLayout) field.getParent();
			assertEquals(2, row.getComponentCount(), fieldId);
			assertTrue(row.getComponent(1) instanceof Image, fieldId + " has no picture next to it");
		}
	}

	@Test
	void shouldHideLesionsLocationsAfterClearAll() {
		SymptomsForm form = createForm(Disease.MONKEYPOX);
		NullableOptionGroup lesions = form.getField(SymptomsDto.LESIONS);
		NullableOptionGroup face = form.getField(SymptomsDto.LESIONS_FACE);

		lesions.setNullableValue(SymptomState.YES);
		face.setNullableValue(Boolean.TRUE);
		findButton(form, I18nProperties.getCaption(Captions.actionClearAll)).click();

		assertNull(lesions.getNullableValue());
		assertFalse(isDisplayed(form, face));
		assertNull(face.getNullableValue());
	}

	private static void assertNoLesionsLocationDisplayed(SymptomsForm form, String situation) {
		for (String fieldId : LESIONS_LOCATION_FIELD_IDS) {
			assertFalse(isDisplayed(form, form.getField(fieldId)), fieldId + " is displayed " + situation);
		}
	}

	private static Button findButton(Component root, String caption) {
		if (root instanceof Button && caption.equals(root.getCaption())) {
			return (Button) root;
		}
		if (root instanceof HasComponents) {
			for (Component child : (HasComponents) root) {
				Button button = findButton(child, caption);
				if (button != null) {
					return button;
				}
			}
		}
		return null;
	}

	// --- Time off work (replaces the commented parentTimeOffWork listener in SymptomsForm) -------------------------------
	// timeOffWorkDays: @DependantOn("parentTimeOffWork"), RSV / Giardiasis / Yersiniosis / Cryptosporidiosis,
	// both in CLINICAL_ASSESSMENT_AND_COURSE

	@Test
	void shouldShowTimeOffWorkDaysOnlyWhileParentTimeOffWorkIsYes() {
		for (Disease disease : new Disease[] {
			Disease.RESPIRATORY_SYNCYTIAL_VIRUS,
			Disease.GIARDIASIS,
			Disease.YERSINIOSIS,
			Disease.CRYPTOSPORIDIOSIS }) {
			SymptomsForm form = createForm(disease);
			NullableOptionGroup parentTimeOffWork = form.getField(SymptomsDto.PARENT_TIME_OFF_WORK);
			Field<?> timeOffWorkDays = form.getField(SymptomsDto.TIME_OFF_WORK_DAYS);

			assertTrue(isDisplayed(form, parentTimeOffWork), disease + ": parentTimeOffWork is not displayed");
			assertFalse(isDisplayed(form, timeOffWorkDays), disease + ": timeOffWorkDays is displayed before an answer");

			parentTimeOffWork.setNullableValue(YesNoUnknown.YES);
			assertTrue(isDisplayed(form, timeOffWorkDays), disease + ": timeOffWorkDays is not displayed for Yes");

			parentTimeOffWork.setNullableValue(YesNoUnknown.NO);
			assertFalse(isDisplayed(form, timeOffWorkDays), disease + ": timeOffWorkDays is displayed for No");

			parentTimeOffWork.setNullableValue(YesNoUnknown.UNKNOWN);
			assertFalse(isDisplayed(form, timeOffWorkDays), disease + ": timeOffWorkDays is displayed for Unknown");
		}
	}

	@Test
	void shouldClearTimeOffWorkDaysWhenParentTimeOffWorkIsNoLongerYes() {
		// the removed listener set the value to null when the parent was not Yes
		SymptomsForm form = createForm(Disease.GIARDIASIS);
		NullableOptionGroup parentTimeOffWork = form.getField(SymptomsDto.PARENT_TIME_OFF_WORK);
		TextField timeOffWorkDays = form.getField(SymptomsDto.TIME_OFF_WORK_DAYS);

		parentTimeOffWork.setNullableValue(YesNoUnknown.YES);
		timeOffWorkDays.setValue("3");
		parentTimeOffWork.setNullableValue(YesNoUnknown.NO);

		assertTrue(timeOffWorkDays.isEmpty());
	}

	@Test
	void shouldKeepTimeOffWorkDaysWhileParentTimeOffWorkStaysYes() {
		// the removed listener kept the value when the parent was Yes
		SymptomsForm form = createForm(Disease.GIARDIASIS);
		NullableOptionGroup parentTimeOffWork = form.getField(SymptomsDto.PARENT_TIME_OFF_WORK);
		TextField timeOffWorkDays = form.getField(SymptomsDto.TIME_OFF_WORK_DAYS);

		parentTimeOffWork.setNullableValue(YesNoUnknown.YES);
		timeOffWorkDays.setValue("3");
		parentTimeOffWork.setNullableValue(YesNoUnknown.YES);

		assertTrue(isDisplayed(form, timeOffWorkDays));
		assertEquals("3", timeOffWorkDays.getValue());
	}

	@Test
	void shouldShowSavedTimeOffWorkDaysWhenLoadingExistingSymptoms() {
		SymptomsForm form = createForm(Disease.YERSINIOSIS);
		SymptomsDto symptoms = SymptomsDto.build();
		symptoms.setParentTimeOffWork(YesNoUnknown.YES);
		symptoms.setTimeOffWorkDays(3f);

		assertDoesNotThrow(() -> form.setValue(symptoms));

		TextField timeOffWorkDays = form.getField(SymptomsDto.TIME_OFF_WORK_DAYS);
		assertTrue(isDisplayed(form, timeOffWorkDays));
		assertEquals(3f, timeOffWorkDays.getConvertedValue());
	}

	@Test
	void shouldNotShowTimeOffWorkFieldsForOtherDiseases() {
		// the removed listener returned early while parentTimeOffWork was hidden
		SymptomsForm form = createForm(Disease.MEASLES);

		assertFalse(isDisplayed(form, form.getField(SymptomsDto.PARENT_TIME_OFF_WORK)));
		assertFalse(isDisplayed(form, form.getField(SymptomsDto.TIME_OFF_WORK_DAYS)));
	}

	@Test
	void shouldPlaceTimeOffWorkDaysInTheSameSectionAsParentTimeOffWork() {
		SymptomsForm form = createForm(Disease.RESPIRATORY_SYNCYTIAL_VIRUS);
		NullableOptionGroup parentTimeOffWork = form.getField(SymptomsDto.PARENT_TIME_OFF_WORK);
		Field<?> timeOffWorkDays = form.getField(SymptomsDto.TIME_OFF_WORK_DAYS);

		parentTimeOffWork.setNullableValue(YesNoUnknown.YES);

		assertSame(sectionOf(parentTimeOffWork), sectionOf(timeOffWorkDays));
		assertEquals(
			I18nProperties.getEnumCaption(SymptomGroup.CLINICAL_ASSESSMENT_AND_COURSE),
			((Label) sectionOf(timeOffWorkDays).getComponent(0)).getValue());
	}

	@Test
	void shouldUseTimeOffWorkOrSchoolCaptionsForGiardiasisCryptosporidiosisAndYersiniosis() {
		for (Disease disease : new Disease[] {
			Disease.GIARDIASIS,
			Disease.CRYPTOSPORIDIOSIS,
			Disease.YERSINIOSIS }) {
			SymptomsForm form = createForm(disease);

			assertEquals(
				I18nProperties.getCaption(Captions.Symptoms_timeOffWorkOrSchool),
				form.getField(SymptomsDto.PARENT_TIME_OFF_WORK).getCaption(),
				disease.name());
			assertEquals(
				I18nProperties.getCaption(Captions.Symptoms_timeOffWorkDaysDuration),
				form.getField(SymptomsDto.TIME_OFF_WORK_DAYS).getCaption(),
				disease.name());
		}
	}

	// --- @DependantOn rules (replace the commented setVisibleWhen calls in SymptomsForm) --------------------------------

	@Test
	void shouldShowOtherNonHemorrhagicSymptomsTextOnlyWhileOtherNonHemorrhagicSymptomsIsYes() {
		SymptomsForm form = createForm(Disease.EVD);
		NullableOptionGroup otherNonHemorrhagicSymptoms = form.getField(SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS);
		TextField otherNonHemorrhagicSymptomsText = form.getField(SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT);

		assertFalse(isDisplayed(form, otherNonHemorrhagicSymptomsText));

		otherNonHemorrhagicSymptoms.setNullableValue(SymptomState.YES);
		assertTrue(isDisplayed(form, otherNonHemorrhagicSymptomsText));
		assertSame(sectionOf(otherNonHemorrhagicSymptoms), sectionOf(otherNonHemorrhagicSymptomsText));

		otherNonHemorrhagicSymptomsText.setValue("Hiccups");
		otherNonHemorrhagicSymptoms.setNullableValue(SymptomState.NO);
		assertFalse(isDisplayed(form, otherNonHemorrhagicSymptomsText));
		assertTrue(otherNonHemorrhagicSymptomsText.isEmpty());
	}

	@Test
	void shouldShowOtherComplicationsTextOnlyWhileOtherComplicationsIsYes() {
		SymptomsForm form = createForm(Disease.EVD);
		NullableOptionGroup otherComplications = form.getField(SymptomsDto.OTHER_COMPLICATIONS);
		TextField otherComplicationsText = form.getField(SymptomsDto.OTHER_COMPLICATIONS_TEXT);

		assertFalse(isDisplayed(form, otherComplicationsText));

		otherComplications.setNullableValue(SymptomState.YES);
		assertTrue(isDisplayed(form, otherComplicationsText));

		otherComplicationsText.setValue("Hearing loss");
		otherComplications.setNullableValue(SymptomState.UNKNOWN);
		assertFalse(isDisplayed(form, otherComplicationsText));
		assertTrue(otherComplicationsText.isEmpty());
	}

	@Test
	void shouldShowLesionsOnsetDateOnlyWhileLesionsIsYes() {
		SymptomsForm form = createForm(Disease.MONKEYPOX);
		NullableOptionGroup lesions = form.getField(SymptomsDto.LESIONS);
		DateField lesionsOnsetDate = form.getField(SymptomsDto.LESIONS_ONSET_DATE);

		assertFalse(isDisplayed(form, lesionsOnsetDate));

		lesions.setNullableValue(SymptomState.YES);
		assertTrue(isDisplayed(form, lesionsOnsetDate));
		assertSame(sectionOf(lesions), sectionOf(lesionsOnsetDate));

		lesionsOnsetDate.setValue(new Date());
		lesions.setNullableValue(SymptomState.NO);
		assertFalse(isDisplayed(form, lesionsOnsetDate));
		assertNull(lesionsOnsetDate.getValue());
	}

	@Test
	void shouldShowCongenitalHeartDiseaseDetailsOnlyForTypeOther() {
		// congenitalHeartDisease -> congenitalHeartDiseaseType -> congenitalHeartDiseaseDetails (only for type OTHER)
		SymptomsForm form = createForm(Disease.CONGENITAL_RUBELLA);
		NullableOptionGroup congenitalHeartDisease = form.getField(SymptomsDto.CONGENITAL_HEART_DISEASE);
		ComboBox congenitalHeartDiseaseType = form.getField(SymptomsDto.CONGENITAL_HEART_DISEASE_TYPE);
		TextField congenitalHeartDiseaseDetails = form.getField(SymptomsDto.CONGENITAL_HEART_DISEASE_DETAILS);

		assertFalse(isDisplayed(form, congenitalHeartDiseaseType));
		assertFalse(isDisplayed(form, congenitalHeartDiseaseDetails));

		congenitalHeartDisease.setNullableValue(SymptomState.YES);
		assertTrue(isDisplayed(form, congenitalHeartDiseaseType));
		assertFalse(isDisplayed(form, congenitalHeartDiseaseDetails));

		congenitalHeartDiseaseType.setValue(CongenitalHeartDiseaseType.PDA);
		assertFalse(isDisplayed(form, congenitalHeartDiseaseDetails));

		congenitalHeartDiseaseType.setValue(CongenitalHeartDiseaseType.OTHER);
		assertTrue(isDisplayed(form, congenitalHeartDiseaseDetails));

		// hiding the top parent hides and clears the whole chain
		congenitalHeartDiseaseDetails.setValue("Aortic stenosis");
		congenitalHeartDisease.setNullableValue(SymptomState.NO);
		assertFalse(isDisplayed(form, congenitalHeartDiseaseType));
		assertFalse(isDisplayed(form, congenitalHeartDiseaseDetails));
		assertNull(congenitalHeartDiseaseType.getValue());
		assertTrue(congenitalHeartDiseaseDetails.isEmpty());
	}

	/** The group section (title, rows) a displayed component belongs to. */
	private static VerticalLayout sectionOf(Component component) {
		for (Component current = component; current != null; current = current.getParent()) {
			if (current instanceof VerticalLayout
				&& ((VerticalLayout) current).getComponentCount() > 0
				&& ((VerticalLayout) current).getComponent(0) instanceof Label
				&& Arrays.stream(SymptomGroup.values())
					.map(I18nProperties::getEnumCaption)
					.anyMatch(((Label) ((VerticalLayout) current).getComponent(0)).getValue()::equals)) {
				return (VerticalLayout) current;
			}
		}
		return null;
	}

	private static SymptomsForm createForm(Disease disease) {
		SymptomsForm form = new SymptomsForm(null, disease, null, null, SymptomsContext.VISIT, ViewMode.NORMAL, UiFieldAccessCheckers.getNoop());
		form.setValue(SymptomsDto.build());

		return form;
	}

	/**
	 * A component is displayed when it and every parent up to the form are visible. A dependent in a detached slot has no
	 * path to the form, so it is not displayed even though its own visibility is true.
	 */
	private static boolean isDisplayed(SymptomsForm form, Component component) {
		if (component == null) {
			return false;
		}
		for (Component current = component; current != null; current = current.getParent()) {
			if (!current.isVisible()) {
				return false;
			}
			if (current == form) {
				return true;
			}
		}
		return false;
	}

	private static boolean isLabelDisplayed(SymptomsForm form, String text) {
		return anyDisplayed(form, form, c -> c instanceof Label && text.equals(((Label) c).getValue()));
	}

	private static boolean isButtonDisplayed(SymptomsForm form, String caption) {
		return anyDisplayed(form, form, c -> c instanceof Button && caption.equals(c.getCaption()));
	}

	/**
	 * Walks the whole component tree below {@code root}; AbstractForm.forEachComponent only visits the direct children.
	 */
	private static boolean anyDisplayed(SymptomsForm form, Component root, Predicate<Component> matcher) {
		if (matcher.test(root) && isDisplayed(form, root)) {
			return true;
		}
		if (root instanceof HasComponents) {
			for (Component child : (HasComponents) root) {
				if (anyDisplayed(form, child, matcher)) {
					return true;
				}
			}
		}
		return false;
	}
}
