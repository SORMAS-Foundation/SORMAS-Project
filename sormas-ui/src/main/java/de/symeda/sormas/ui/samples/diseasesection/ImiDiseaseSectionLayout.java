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
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SeroGroupSpecification;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.therapy.DrugSusceptibilityForm;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.Registration;

/**
 * Disease section for INVASIVE_MENINGOCOCCAL_INFECTION (IMI):
 * wires seroGroupSpecification + seroGroupSpecificationText visibility,
 * and owns the DrugSusceptibilityForm for IMI.
 */
public class ImiDiseaseSectionLayout implements DiseaseSectionLayout {

	//@formatter:off
	private static final String HTML =
			fluidRowLocs(PathogenTestDto.SERO_GROUP_SPECIFICATION, PathogenTestDto.SERO_GROUP_SPECIFICATION_TEXT) +
			fluidRowLocs(PathogenTestDto.DRUG_SUSCEPTIBILITY);
	//@formatter:on

	private static final List<String> FIELD_IDS = Collections.unmodifiableList(
		Arrays.asList(PathogenTestDto.SERO_GROUP_SPECIFICATION, PathogenTestDto.SERO_GROUP_SPECIFICATION_TEXT, PathogenTestDto.DRUG_SUSCEPTIBILITY));

	private DrugSusceptibilityForm drugSusceptibilityField;
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
		ComboBox seroGroupSpecification = buildAndAdd(fieldGroup, panel, PathogenTestDto.SERO_GROUP_SPECIFICATION, ComboBox.class);
		seroGroupSpecification.setVisible(false);

		TextField seroGroupSpecificationText = buildAndAdd(fieldGroup, panel, PathogenTestDto.SERO_GROUP_SPECIFICATION_TEXT, TextField.class);
		seroGroupSpecificationText.setVisible(false);

		Map<Object, List<Object>> imiSeroTypingDependencies = new HashMap<>();
		imiSeroTypingDependencies.put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.INVASIVE_MENINGOCOCCAL_INFECTION));
		imiSeroTypingDependencies.put(PathogenTestDto.TEST_RESULT, Arrays.asList(PathogenTestResultType.POSITIVE));
		imiSeroTypingDependencies.put(
			PathogenTestDto.TEST_TYPE,
			Arrays.asList(
				PathogenTestType.SEROGROUPING,
				PathogenTestType.MULTILOCUS_SEQUENCE_TYPING,
				PathogenTestType.SLIDE_AGGLUTINATION,
				PathogenTestType.WHOLE_GENOME_SEQUENCING));
		Registration r1 = FieldHelper.setVisibleWhen(fieldGroup, PathogenTestDto.SERO_GROUP_SPECIFICATION, imiSeroTypingDependencies, true);
		Registration r2 = FieldHelper.setVisibleWhen(
			fieldGroup,
			PathogenTestDto.SERO_GROUP_SPECIFICATION_TEXT,
			PathogenTestDto.SERO_GROUP_SPECIFICATION,
			SeroGroupSpecification.OTHER,
			true);
		visibilityRegistration = Registration.combine(r1, r2);

		drugSusceptibilityField = new DrugSusceptibilityForm(
			FieldVisibilityCheckers.getNoop(),
			UiFieldAccessCheckers.getDefault(true, FacadeProvider.getConfigFacade().getCountryLocale()));
		drugSusceptibilityField.setCaption(null);
		fieldGroup.bind(drugSusceptibilityField, PathogenTestDto.DRUG_SUSCEPTIBILITY);
		panel.addComponent(drugSusceptibilityField, PathogenTestDto.DRUG_SUSCEPTIBILITY);
	}

	@Override
	public void unbindFields(FieldGroup fieldGroup, CustomLayout panel) {
		if (visibilityRegistration != null) {
			visibilityRegistration.remove();
			visibilityRegistration = null;
		}

		for (String id : FIELD_IDS) {
			if (PathogenTestDto.DRUG_SUSCEPTIBILITY.equals(id)) {
				continue;
			}
			Field<?> field = fieldGroup.getField(id);
			if (field != null) {
				fieldGroup.unbind(field);
				panel.removeComponent(field);
			}
		}

		if (drugSusceptibilityField != null) {
			fieldGroup.unbind(drugSusceptibilityField);
			panel.removeComponent(drugSusceptibilityField);
			drugSusceptibilityField = null;
		}
	}

	@Override
	public void onTestTypeChanged(
		PathogenTestType testType,
		Disease disease,
		AbstractField<PathogenTestResultType> testResultField,
		PathogenTestFormConfig config) {
		if (drugSusceptibilityField != null) {
			drugSusceptibilityField.updateFieldsVisibility(disease, testType);
		}

		if (testType == null || !config.isLuxembourg) {
			return;
		}

		if (disease == Disease.INVASIVE_MENINGOCOCCAL_INFECTION && testType == PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY) {
			testResultField.setValue(PathogenTestResultType.POSITIVE);
		}
	}

}
