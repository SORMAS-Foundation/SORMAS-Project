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
package de.symeda.sormas.ui.samples;

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.ui.CustomLayout;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.sample.PathogenStrainCallStatus;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.therapy.DrugSusceptibilityForm;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;

/**
 * Disease-specific section for TUBERCULOSIS and LATENT_TUBERCULOSIS.
 * Fully self-contained: no dependency on PathogenTestForm.
 */
public class TuberculosisDiseaseSectionLayout implements DiseaseSectionLayout {

	//@formatter:off
	private static final String HTML =
		fluidRowLocs(PathogenTestDto.RIFAMPICIN_RESISTANT, PathogenTestDto.ISONIAZID_RESISTANT, "", "") +
		fluidRowLocs(PathogenTestDto.TEST_SCALE, "") +
		fluidRowLocs(PathogenTestDto.STRAIN_CALL_STATUS, "") +
		fluidRowLocs(PathogenTestDto.SPECIE, "") +
		fluidRowLocs(PathogenTestDto.PATTERN_PROFILE, "") +
		fluidRowLocs(PathogenTestDto.DRUG_SUSCEPTIBILITY) +
		fluidRowLocs(PathogenTestDto.TUBE_NIL, PathogenTestDto.TUBE_NIL_GT10) +
		fluidRowLocs(PathogenTestDto.TUBE_AG_TB1, PathogenTestDto.TUBE_AG_TB1_GT10) +
		fluidRowLocs(PathogenTestDto.TUBE_AG_TB2, PathogenTestDto.TUBE_AG_TB2_GT10) +
		fluidRowLocs(PathogenTestDto.TUBE_MITOGENE, PathogenTestDto.TUBE_MITOGENE_GT10);
	//@formatter:on

	private static final List<String> FIELD_IDS = Arrays.asList(
		PathogenTestDto.RIFAMPICIN_RESISTANT,
		PathogenTestDto.ISONIAZID_RESISTANT,
		PathogenTestDto.TEST_SCALE,
		PathogenTestDto.STRAIN_CALL_STATUS,
		PathogenTestDto.SPECIE,
		PathogenTestDto.PATTERN_PROFILE,
		PathogenTestDto.DRUG_SUSCEPTIBILITY,
		PathogenTestDto.TUBE_NIL,
		PathogenTestDto.TUBE_NIL_GT10,
		PathogenTestDto.TUBE_AG_TB1,
		PathogenTestDto.TUBE_AG_TB1_GT10,
		PathogenTestDto.TUBE_AG_TB2,
		PathogenTestDto.TUBE_AG_TB2_GT10,
		PathogenTestDto.TUBE_MITOGENE,
		PathogenTestDto.TUBE_MITOGENE_GT10);

	// Held for onTestTypeChanged and unbindFields
	private DrugSusceptibilityForm drugSusceptibilityField;
	private List<String> tubeFieldIds;

	// Listeners registered on shared FieldGroup source fields — removed in unbindFields
	private Property.ValueChangeListener testTypeListener;
	private Property.ValueChangeListener testResultListener;
	private Property.ValueChangeListener diseaseListener;

	@Override
	public String getHtmlLayout() {
		return HTML;
	}

	@Override
	public Collection<String> getFieldIds() {
		return FIELD_IDS;
	}

	@Override
	public void bindFields(FieldGroup fieldGroup, CustomLayout panel, Disease disease) {
		NullableOptionGroup rifampicinResistant = buildAndAdd(fieldGroup, panel, PathogenTestDto.RIFAMPICIN_RESISTANT, NullableOptionGroup.class);
		rifampicinResistant.setVisible(false);

		NullableOptionGroup isoniazidResistant = buildAndAdd(fieldGroup, panel, PathogenTestDto.ISONIAZID_RESISTANT, NullableOptionGroup.class);
		isoniazidResistant.setVisible(false);

		ComboBox testScale = buildAndAdd(fieldGroup, panel, PathogenTestDto.TEST_SCALE, ComboBox.class);
		testScale.setVisible(false);

		ComboBox strainCallStatus = buildAndAdd(fieldGroup, panel, PathogenTestDto.STRAIN_CALL_STATUS, ComboBox.class);
		strainCallStatus.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		strainCallStatus.setVisible(false);

		ComboBox specie = buildAndAdd(fieldGroup, panel, PathogenTestDto.SPECIE, ComboBox.class);
		specie.setVisible(false);

		TextField patternProfile = buildAndAdd(fieldGroup, panel, PathogenTestDto.PATTERN_PROFILE, TextField.class);
		patternProfile.setVisible(false);

		drugSusceptibilityField = new DrugSusceptibilityForm(
			FieldVisibilityCheckers.getNoop(),
			UiFieldAccessCheckers.getDefault(true, FacadeProvider.getConfigFacade().getCountryLocale()));
		drugSusceptibilityField.setCaption(null);
		fieldGroup.bind(drugSusceptibilityField, PathogenTestDto.DRUG_SUSCEPTIBILITY);
		panel.addComponent(drugSusceptibilityField, PathogenTestDto.DRUG_SUSCEPTIBILITY);

		// Tube fields — stub: built and added hidden; TubeFieldHandler wired in Phase 6
		tubeFieldIds = Arrays.asList(
			PathogenTestDto.TUBE_NIL,
			PathogenTestDto.TUBE_NIL_GT10,
			PathogenTestDto.TUBE_AG_TB1,
			PathogenTestDto.TUBE_AG_TB1_GT10,
			PathogenTestDto.TUBE_AG_TB2,
			PathogenTestDto.TUBE_AG_TB2_GT10,
			PathogenTestDto.TUBE_MITOGENE,
			PathogenTestDto.TUBE_MITOGENE_GT10);

		for (String tubeId : Arrays
			.asList(PathogenTestDto.TUBE_NIL, PathogenTestDto.TUBE_AG_TB1, PathogenTestDto.TUBE_AG_TB2, PathogenTestDto.TUBE_MITOGENE)) {
			buildAndAdd(fieldGroup, panel, tubeId, TextField.class).setVisible(false);
		}
		for (String gt10Id : Arrays.asList(
			PathogenTestDto.TUBE_NIL_GT10,
			PathogenTestDto.TUBE_AG_TB1_GT10,
			PathogenTestDto.TUBE_AG_TB2_GT10,
			PathogenTestDto.TUBE_MITOGENE_GT10)) {
			buildAndAdd(fieldGroup, panel, gt10Id, NullableOptionGroup.class).setVisible(false);
		}

		if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)) {
			bindLuxembourgVisibility(fieldGroup, strainCallStatus, disease);
		}
	}

	private void bindLuxembourgVisibility(FieldGroup fieldGroup, ComboBox strainCallStatus, Disease disease) {
		// Named listeners so they can be removed in unbindFields
		testTypeListener = e -> {
			Field<?> testTypeField = fieldGroup.getField(PathogenTestDto.TEST_TYPE);
			Field<?> testResultField = fieldGroup.getField(PathogenTestDto.TEST_RESULT);
			onValueChangedSetVisible(fieldGroup, testTypeField, testResultField);
		};
		testResultListener = e -> {
			Field<?> testTypeField = fieldGroup.getField(PathogenTestDto.TEST_TYPE);
			Field<?> testResultField = fieldGroup.getField(PathogenTestDto.TEST_RESULT);
			onValueChangedSetVisible(fieldGroup, testTypeField, testResultField);
		};
		diseaseListener = e -> {
			Field<?> testTypeField = fieldGroup.getField(PathogenTestDto.TEST_TYPE);
			Field<?> testResultField = fieldGroup.getField(PathogenTestDto.TEST_RESULT);
			onValueChangedSetVisible(fieldGroup, testTypeField, testResultField);

			Disease newDisease = (Disease) e.getProperty().getValue();
			FieldHelper.updateItems(
				strainCallStatus,
				Arrays.asList(PathogenStrainCallStatus.values()),
				FieldVisibilityCheckers.withDisease(newDisease),
				PathogenStrainCallStatus.class);
		};

		fieldGroup.getField(PathogenTestDto.TEST_TYPE).addValueChangeListener(testTypeListener);
		fieldGroup.getField(PathogenTestDto.TEST_RESULT).addValueChangeListener(testResultListener);
		fieldGroup.getField(PathogenTestDto.TESTED_DISEASE).addValueChangeListener(diseaseListener);

		// Wire standard multi-source visibility conditions
		FieldHelper
			.setVisibleWhen(fieldGroup, PathogenTestDto.RIFAMPICIN_RESISTANT, PathogenTestForm.RIFAMPICIN_RESISTANT_VISIBILITY_CONDITIONS, true);
		FieldHelper.setVisibleWhen(fieldGroup, PathogenTestDto.TEST_SCALE, PathogenTestForm.TEST_SCALE_VISIBILITY_CONDITIONS, true);
		FieldHelper.setVisibleWhen(fieldGroup, PathogenTestDto.STRAIN_CALL_STATUS, PathogenTestForm.STRAIN_CALL_STATUS_VISIBILITY_CONDITIONS, true);
		FieldHelper.setVisibleWhen(fieldGroup, PathogenTestDto.SPECIE, PathogenTestForm.SPECIE_VISIBILITY_CONDITIONS, true);

		Map<Object, List<Object>> miruCode = new HashMap<>();
		miruCode.put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.TUBERCULOSIS, Disease.LATENT_TUBERCULOSIS));
		miruCode.put(PathogenTestDto.TEST_TYPE, Arrays.asList(PathogenTestType.MIRU_PATTERN_CODE));
		FieldHelper.setVisibleWhen(fieldGroup, PathogenTestDto.PATTERN_PROFILE, miruCode, true);

		FieldHelper.updateItems(
			strainCallStatus,
			Arrays.asList(PathogenStrainCallStatus.values()),
			FieldVisibilityCheckers.withDisease(disease),
			PathogenStrainCallStatus.class);

		// Apply initial visibility
		onValueChangedSetVisible(fieldGroup, fieldGroup.getField(PathogenTestDto.TEST_TYPE), fieldGroup.getField(PathogenTestDto.TEST_RESULT));
	}

	private void onValueChangedSetVisible(FieldGroup fieldGroup, Field<?> testTypeField, Field<?> testResultField) {
		PathogenTestType testType = testTypeField != null ? (PathogenTestType) testTypeField.getValue() : null;
		boolean showTubes =
			testType == PathogenTestType.IGRA && FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		for (String tubeId : tubeFieldIds) {
			Field<?> f = fieldGroup.getField(tubeId);
			if (f != null) {
				f.setVisible(showTubes);
				if (!showTubes) {
					f.setValue(null);
				}
			}
		}
	}

	@Override
	public void unbindFields(FieldGroup fieldGroup, CustomLayout panel) {
		// Remove named listeners from shared source fields before unbinding section fields
		if (testTypeListener != null) {
			Field<?> f = fieldGroup.getField(PathogenTestDto.TEST_TYPE);
			if (f != null) {
				f.removeValueChangeListener(testTypeListener);
			}
			testTypeListener = null;
		}
		if (testResultListener != null) {
			Field<?> f = fieldGroup.getField(PathogenTestDto.TEST_RESULT);
			if (f != null) {
				f.removeValueChangeListener(testResultListener);
			}
			testResultListener = null;
		}
		if (diseaseListener != null) {
			Field<?> f = fieldGroup.getField(PathogenTestDto.TESTED_DISEASE);
			if (f != null) {
				f.removeValueChangeListener(diseaseListener);
			}
			diseaseListener = null;
		}

		for (String id : FIELD_IDS) {
			Field<?> field = fieldGroup.getField(id);
			if (field != null) {
				fieldGroup.unbind(field);
				panel.removeComponent(field);
			}
		}

		drugSusceptibilityField = null;
		tubeFieldIds = null;
	}

	@Override
	public void onTestTypeChanged(PathogenTestType testType, Disease disease, AbstractField<PathogenTestResultType> testResultField) {
		if (drugSusceptibilityField != null) {
			drugSusceptibilityField.updateFieldsVisibility(disease, testType);
		}

		if (testType == null || !FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)) {
			return;
		}

		boolean isTb = disease == Disease.TUBERCULOSIS || disease == Disease.LATENT_TUBERCULOSIS;
		if (!isTb) {
			return;
		}

		if (testType == PathogenTestType.BEIJINGGENOTYPING
			|| testType == PathogenTestType.MIRU_PATTERN_CODE
			|| testType == PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY) {
			testResultField.setValue(PathogenTestResultType.NOT_APPLICABLE);
		} else if (testType == PathogenTestType.SPOLIGOTYPING) {
			testResultField.setValue(PathogenTestResultType.POSITIVE);
		}
	}

	@Override
	public Disease[] getDiseases() {
		return new Disease[] {
			Disease.TUBERCULOSIS,
			Disease.LATENT_TUBERCULOSIS };
	}

	private <F extends Field<?>> F buildAndAdd(FieldGroup fieldGroup, CustomLayout panel, String propertyId, Class<F> fieldType) {
		F field = fieldGroup.buildAndBind(propertyId, (Object) propertyId, fieldType);
		field.setId(propertyId);
		panel.addComponent(field, propertyId);
		return field;
	}
}
