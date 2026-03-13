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

import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.ui.CustomLayout;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.GenoTypeResult;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.Registration;

/**
 * Disease section for MEASLES: wires genotype result + genotype result text visibility.
 */
public class MeaslesDiseaseSectionLayout implements DiseaseSectionLayout {

	//@formatter:off
	private static final String HTML =
			fluidRowLocs(4, PathogenTestDto.GENOTYPE_RESULT, 6, PathogenTestDto.GENOTYPE_RESULT_TEXT);
	//@formatter:on

	private static final List<String> FIELD_IDS =
		Collections.unmodifiableList(Arrays.asList(PathogenTestDto.GENOTYPE_RESULT, PathogenTestDto.GENOTYPE_RESULT_TEXT));

	private Property.ValueChangeListener testTypeListener;
	private Registration visibilityRegistration;

	@Override
	public String getHtmlLayout() {
		return HTML;
	}

	@Override
	public Collection<String> getFieldIds() {
		return FIELD_IDS;
	}

	@Override
	public void bindFields(FieldGroup fieldGroup, CustomLayout panel, Disease disease, PathogenTestFormConfig config) {
		ComboBox genoTypeResult = buildAndAdd(fieldGroup, panel, PathogenTestDto.GENOTYPE_RESULT, ComboBox.class);
		genoTypeResult.setVisible(false);

		TextField genoTypeResultText = buildAndAdd(fieldGroup, panel, PathogenTestDto.GENOTYPE_RESULT_TEXT, TextField.class);
		genoTypeResultText.setVisible(false);

		Map<Object, List<Object>> genoTypingDependencies = new HashMap<>();
		genoTypingDependencies.put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.MEASLES, Disease.CRYPTOSPORIDIOSIS));
		genoTypingDependencies.put(PathogenTestDto.TEST_TYPE, Arrays.asList(PathogenTestType.GENOTYPING));
		genoTypingDependencies.put(PathogenTestDto.TEST_RESULT, Arrays.asList(PathogenTestResultType.POSITIVE));
		Registration r1 = FieldHelper.setVisibleWhen(fieldGroup, PathogenTestDto.GENOTYPE_RESULT, genoTypingDependencies, true);
		Map<Object, List<Object>> genotypeTextDependencies = new HashMap<>(genoTypingDependencies);
		genotypeTextDependencies.put(PathogenTestDto.GENOTYPE_RESULT, Arrays.asList(GenoTypeResult.OTHER));
		Registration r2 = FieldHelper.setVisibleWhen(fieldGroup, PathogenTestDto.GENOTYPE_RESULT_TEXT, genotypeTextDependencies, true);
		visibilityRegistration = Registration.combine(r1, r2);

		// Populate genotype items for the current disease
		FieldHelper.updateItems(disease, genoTypeResult, GenoTypeResult.class);

		// Update genotype items when test type changes
		testTypeListener = e -> {
			Field<?> genoTypingCB = fieldGroup.getField(PathogenTestDto.GENOTYPE_RESULT);
			if (genoTypingCB instanceof ComboBox) {
				Disease currentDisease = (Disease) fieldGroup.getField(PathogenTestDto.TESTED_DISEASE).getValue();
				FieldHelper.updateItems(currentDisease, (ComboBox) genoTypingCB, GenoTypeResult.class);
			}
		};
		Field<?> testTypeField = fieldGroup.getField(PathogenTestDto.TEST_TYPE);
		if (testTypeField != null) {
			testTypeField.addValueChangeListener(testTypeListener);
		}
	}

	@Override
	public void unbindFields(FieldGroup fieldGroup, CustomLayout panel) {
		if (visibilityRegistration != null) {
			visibilityRegistration.remove();
			visibilityRegistration = null;
		}
		if (testTypeListener != null) {
			Field<?> testTypeField = fieldGroup.getField(PathogenTestDto.TEST_TYPE);
			if (testTypeField != null) {
				testTypeField.removeValueChangeListener(testTypeListener);
			}
			testTypeListener = null;
		}

		for (String id : FIELD_IDS) {
			Field<?> field = fieldGroup.getField(id);
			if (field != null) {
				fieldGroup.unbind(field);
				panel.removeComponent(field);
			}
		}
	}

	@Override
	public void onTestTypeChanged(
		PathogenTestType testType,
		Disease disease,
		AbstractField<PathogenTestResultType> testResultField,
		PathogenTestFormConfig config) {
		// genotype item update is handled by the registered testTypeListener
	}

}
