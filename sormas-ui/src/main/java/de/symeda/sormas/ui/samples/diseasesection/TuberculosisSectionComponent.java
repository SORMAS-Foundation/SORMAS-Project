
/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.samples.diseasesection;

import java.util.Locale;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.sample.PathogenSpecie;
import de.symeda.sormas.api.sample.PathogenStrainCallStatus;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestScale;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.samples.events.DiseaseChangedEvent;
import de.symeda.sormas.ui.samples.events.SetTestResultEvent;
import de.symeda.sormas.ui.samples.events.TestResultChangedEvent;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;
import de.symeda.sormas.ui.therapy.DrugSusceptibilityForm;
import de.symeda.sormas.ui.utils.StringToFloatNullableConverter;

public class TuberculosisSectionComponent extends AbstractDiseaseSectionComponent {

	private RadioButtonGroup<YesNoUnknown> rifampicinResistant;
	private RadioButtonGroup<YesNoUnknown> isoniazidResistant;
	private ComboBox<PathogenTestScale> testScale;
	private ComboBox<PathogenStrainCallStatus> strainCallStatus;
	private ComboBox<PathogenSpecie> specie;
	private TextField patternProfile;
	private DrugSusceptibilityForm drugSusceptibilityField;

	// Tube fields
	private TextField tubeNil;
	private RadioButtonGroup<Boolean> tubeNilGT10;
	private TextField tubeAgTb1;
	private RadioButtonGroup<Boolean> tubeAgTb1GT10;
	private TextField tubeAgTb2;
	private RadioButtonGroup<Boolean> tubeAgTb2GT10;
	private TextField tubeMitogene;
	private RadioButtonGroup<Boolean> tubeMitogeneGT10;

	private PathogenTestType currentTestType;
	private PathogenTestResultType currentResult;

	@Override
	protected void buildLayout() {

		// Rifampicin / Isoniazid resistant
		rifampicinResistant = createEnumRadioButtonGroup(PathogenTestDto.RIFAMPICIN_RESISTANT, YesNoUnknown.class);
		rifampicinResistant.setVisible(false);

		isoniazidResistant = createEnumRadioButtonGroup(PathogenTestDto.ISONIAZID_RESISTANT, YesNoUnknown.class);
		isoniazidResistant.setVisible(false);

		addRow(rifampicinResistant, isoniazidResistant);

		// Test scale
		testScale = createComboBox(PathogenTestDto.TEST_SCALE);
		testScale.setItems(PathogenTestScale.values());
		testScale.setItemCaptionGenerator(PathogenTestScale::toString);
		testScale.setVisible(false);
		addRow(testScale);

		// Strain call status
		strainCallStatus = createComboBox(PathogenTestDto.STRAIN_CALL_STATUS);
		strainCallStatus.setItemCaptionGenerator(PathogenStrainCallStatus::toString);
		strainCallStatus.setVisible(false);
		updateStrainCallStatusItems(disease);
		addRow(strainCallStatus);

		// Specie
		specie = createComboBox(PathogenTestDto.SPECIE);
		specie.setItemCaptionGenerator(PathogenSpecie::toString);
		specie.setVisible(false);
		updateComboBoxByDiseaseAndTestType(specie, PathogenSpecie.class, disease, currentTestType);
		addRow(specie);

		// Pattern profile
		patternProfile = createTextField(PathogenTestDto.PATTERN_PROFILE);
		patternProfile.setVisible(false);
		addRow(patternProfile);

		// Bind non-tube fields
		binder.forField(rifampicinResistant).bind(PathogenTestDto::getRifampicinResistant, PathogenTestDto::setRifampicinResistant);
		binder.forField(isoniazidResistant).bind(PathogenTestDto::getIsoniazidResistant, PathogenTestDto::setIsoniazidResistant);
		binder.forField(testScale).bind(PathogenTestDto::getTestScale, PathogenTestDto::setTestScale);
		binder.forField(strainCallStatus).bind(PathogenTestDto::getStrainCallStatus, PathogenTestDto::setStrainCallStatus);
		binder.forField(specie).bind(PathogenTestDto::getSpecie, PathogenTestDto::setSpecie);
		binder.forField(patternProfile).bind(PathogenTestDto::getPatternProfile, PathogenTestDto::setPatternProfile);

		// DrugSusceptibilityForm — legacy v7
		drugSusceptibilityField = new DrugSusceptibilityForm(
			FieldVisibilityCheckers.getNoop(),
			UiFieldAccessCheckers.getDefault(true, FacadeProvider.getConfigFacade().getCountryLocale()));
		drugSusceptibilityField.setCaption(null);
		fieldGroup.bind(drugSusceptibilityField, PathogenTestDto.DRUG_SUSCEPTIBILITY);
		addDrugSusceptibilityField(drugSusceptibilityField);

		// Tube fields
		buildTubeFields();
	}

	private void buildTubeFields() {
		tubeNil = createTubeTextField(PathogenTestDto.TUBE_NIL);
		tubeNilGT10 = createRadioButtonGroup(PathogenTestDto.TUBE_NIL_GT10);
		tubeNilGT10.setVisible(false);
		tubeNil.setVisible(false);
		addRow(tubeNil, tubeNilGT10);

		tubeAgTb1 = createTubeTextField(PathogenTestDto.TUBE_AG_TB1);
		tubeAgTb1GT10 = createRadioButtonGroup(PathogenTestDto.TUBE_AG_TB1_GT10);
		tubeAgTb1GT10.setVisible(false);
		tubeAgTb1.setVisible(false);
		addRow(tubeAgTb1, tubeAgTb1GT10);

		tubeAgTb2 = createTubeTextField(PathogenTestDto.TUBE_AG_TB2);
		tubeAgTb2GT10 = createRadioButtonGroup(PathogenTestDto.TUBE_AG_TB2_GT10);
		tubeAgTb2GT10.setVisible(false);
		tubeAgTb2.setVisible(false);
		addRow(tubeAgTb2, tubeAgTb2GT10);

		tubeMitogene = createTubeTextField(PathogenTestDto.TUBE_MITOGENE);
		tubeMitogeneGT10 = createRadioButtonGroup(PathogenTestDto.TUBE_MITOGENE_GT10);
		tubeMitogeneGT10.setVisible(false);
		tubeMitogene.setVisible(false);
		addRow(tubeMitogene, tubeMitogeneGT10);

		// Bind and sync tube fields
		bindTubePair(
			tubeNil,
			PathogenTestDto::getTubeNil,
			PathogenTestDto::setTubeNil,
			tubeNilGT10,
			PathogenTestDto::getTubeNilGT10,
			PathogenTestDto::setTubeNilGT10);
		bindTubePair(
			tubeAgTb1,
			PathogenTestDto::getTubeAgTb1,
			PathogenTestDto::setTubeAgTb1,
			tubeAgTb1GT10,
			PathogenTestDto::getTubeAgTb1GT10,
			PathogenTestDto::setTubeAgTb1GT10);
		bindTubePair(
			tubeAgTb2,
			PathogenTestDto::getTubeAgTb2,
			PathogenTestDto::setTubeAgTb2,
			tubeAgTb2GT10,
			PathogenTestDto::getTubeAgTb2GT10,
			PathogenTestDto::setTubeAgTb2GT10);
		bindTubePair(
			tubeMitogene,
			PathogenTestDto::getTubeMitogene,
			PathogenTestDto::setTubeMitogene,
			tubeMitogeneGT10,
			PathogenTestDto::getTubeMitogeneGT10,
			PathogenTestDto::setTubeMitogeneGT10);
	}

	private TextField createTubeTextField(String propertyId) {
		return createTextField(propertyId);
	}

	@Override
	protected void wireVisibility() {
		// Drug susceptibility, tube fields, and auto-set apply regardless of Luxembourg
		track(eventBus.on(TestTypeChangedEvent.class, event -> {
			currentTestType = event.getTestType();

			if (config.isLuxembourg) {
				updateFieldVisibility();
			}

			updateComboBoxByDiseaseAndTestType(specie, PathogenSpecie.class, disease, currentTestType);
			updateDrugSusceptibility(currentTestType);
			setTubeFieldsVisible(currentTestType);

			// Auto-set test result (Luxembourg only)
			if (currentTestType != null && config.isLuxembourg) {
				if (currentTestType == PathogenTestType.BEIJINGGENOTYPING
					|| currentTestType == PathogenTestType.MIRU_PATTERN_CODE
					|| currentTestType == PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY) {
					eventBus.fire(new SetTestResultEvent(PathogenTestResultType.NOT_APPLICABLE));
				} else if (currentTestType == PathogenTestType.SPOLIGOTYPING) {
					eventBus.fire(new SetTestResultEvent(PathogenTestResultType.POSITIVE));
				} else {
					eventBus.fire(new SetTestResultEvent(null));
				}
			} else if (currentTestType != null) {
				eventBus.fire(new SetTestResultEvent(null));
			}
		}));

		track(eventBus.on(TestResultChangedEvent.class, event -> {
			currentResult = event.getTestResult();
			if (config.isLuxembourg) {
				updateFieldVisibility();
			}
		}));

		track(eventBus.on(DiseaseChangedEvent.class, event -> {
			disease = event.getDisease();
			if (config.isLuxembourg) {
				updateFieldVisibility();
				updateStrainCallStatusItems(disease);
			}
			setTubeFieldsVisible(currentTestType);
			updateDrugSusceptibility(currentTestType);
		}));
	}

	private void updateFieldVisibility() {
		// Rifampicin resistant: PCR_RT_PCR + POSITIVE
		boolean showRifampicin = currentTestType == PathogenTestType.PCR_RT_PCR && currentResult == PathogenTestResultType.POSITIVE;
		rifampicinResistant.setVisible(showRifampicin);
		if (!showRifampicin) {
			rifampicinResistant.clear();
		}

		// Isoniazid follows same conditions as rifampicin
		isoniazidResistant.setVisible(showRifampicin);
		if (!showRifampicin) {
			isoniazidResistant.clear();
		}

		// Test scale: MICROSCOPY
		boolean showTestScale = currentTestType == PathogenTestType.MICROSCOPY;
		testScale.setVisible(showTestScale);
		if (!showTestScale) {
			testScale.clear();
		}

		// Strain call status: BEIJINGGENOTYPING
		boolean showStrain = currentTestType == PathogenTestType.BEIJINGGENOTYPING;
		strainCallStatus.setVisible(showStrain);
		if (!showStrain) {
			strainCallStatus.clear();
		}

		// Specie: SPOLIGOTYPING + POSITIVE
		boolean showSpecie = currentTestType == PathogenTestType.SPOLIGOTYPING && currentResult == PathogenTestResultType.POSITIVE;
		specie.setVisible(showSpecie);
		if (!showSpecie) {
			specie.clear();
		}

		// Pattern profile: MIRU_PATTERN_CODE
		boolean showPattern = currentTestType == PathogenTestType.MIRU_PATTERN_CODE;
		patternProfile.setVisible(showPattern);
		if (!showPattern) {
			patternProfile.clear();
		}
		updateRowAndSelfVisibility();
	}

	private void setTubeFieldsVisible(PathogenTestType testType) {
		boolean showTubes = testType == PathogenTestType.IGRA && config.isLuxembourg;
		TextField[] numericFields = {
			tubeNil,
			tubeAgTb1,
			tubeAgTb2,
			tubeMitogene };
		@SuppressWarnings("unchecked")
		RadioButtonGroup<Boolean>[] gt10Fields = new RadioButtonGroup[] {
			tubeNilGT10,
			tubeAgTb1GT10,
			tubeAgTb2GT10,
			tubeMitogeneGT10 };

		for (int i = 0; i < numericFields.length; i++) {
			numericFields[i].setVisible(showTubes);
			gt10Fields[i].setVisible(showTubes);
			if (!showTubes) {
				numericFields[i].clear();
				gt10Fields[i].clear();
			}
		}
		updateRowAndSelfVisibility();
	}

	private void updateStrainCallStatusItems(Disease disease) {
		updateComboBoxByDisease(strainCallStatus, PathogenStrainCallStatus.class, disease);
	}

	private void updateDrugSusceptibility(PathogenTestType testType) {
		if (drugSusceptibilityField != null) {
			boolean visible = drugSusceptibilityField.updateFieldsVisibility(disease, testType);
			setDrugSusceptibilityRowVisible(visible);
		}
	}

	@Override
	protected void clearOwnedFields() {
		PathogenTestDto dto = binder.getBean();
		if (dto == null) {
			return;
		}
		dto.setRifampicinResistant(null);
		dto.setIsoniazidResistant(null);
		dto.setTestScale(null);
		dto.setStrainCallStatus(null);
		dto.setSpecie(null);
		dto.setPatternProfile(null);
		dto.setTubeNil(null);
		dto.setTubeNilGT10(null);
		dto.setTubeAgTb1(null);
		dto.setTubeAgTb1GT10(null);
		dto.setTubeAgTb2(null);
		dto.setTubeAgTb2GT10(null);
		dto.setTubeMitogene(null);
		dto.setTubeMitogeneGT10(null);
		dto.setDrugSusceptibility(null);
	}

	@Override
	protected void unbindLegacyFields() {
		if (drugSusceptibilityField != null) {
			fieldGroup.unbind(drugSusceptibilityField);
			drugSusceptibilityField = null;
		}
	}

	private void bindTubePair(
		TextField tubeField,
		ValueProvider<PathogenTestDto, Float> tubeGetter,
		Setter<PathogenTestDto, Float> tubeSetter,
		RadioButtonGroup<Boolean> gt10Field,
		ValueProvider<PathogenTestDto, Boolean> gt10Getter,
		Setter<PathogenTestDto, Boolean> gt10Setter) {

		binder.forField(tubeField).withConverter(new StringToFloatNullableConverter(tubeField.getCaption())).bind(tubeGetter, tubeSetter);
		binder.forField(gt10Field).bind(gt10Getter, gt10Setter);

		track(tubeField.addValueChangeListener(e -> {
			Locale locale = tubeField.getLocale() != null ? tubeField.getLocale() : Locale.getDefault();
			StringToFloatNullableConverter.parseLocaleAware(e.getValue(), locale).ifPresent(f -> gt10Field.setValue(f > 10));
		}));

		track(gt10Field.addValueChangeListener(e -> {
			Locale locale = tubeField.getLocale() != null ? tubeField.getLocale() : Locale.getDefault();
			if (Boolean.TRUE.equals(e.getValue())) {
				StringToFloatNullableConverter.parseLocaleAware(tubeField.getValue(), locale).filter(f -> f <= 10).ifPresent(f -> tubeField.clear());
			} else if (Boolean.FALSE.equals(e.getValue())) {
				StringToFloatNullableConverter.parseLocaleAware(tubeField.getValue(), locale).filter(f -> f > 10).ifPresent(f -> tubeField.clear());
			}
		}));
	}
}
