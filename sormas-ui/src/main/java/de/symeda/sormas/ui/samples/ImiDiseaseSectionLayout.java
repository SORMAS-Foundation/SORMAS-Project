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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.ui.CustomLayout;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.ui.AbstractField;

import de.symeda.sormas.api.CountryHelper;
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

/**
 * Disease section for INVASIVE_MENINGOCOCCAL_INFECTION (IMI):
 * wires seroGroupSpecification + seroGroupSpecificationText visibility,
 * and owns the DrugSusceptibilityForm for IMI.
 * Fields live in the common layout; this section owns the visibility registrations.
 */
public class ImiDiseaseSectionLayout implements DiseaseSectionLayout {

	private static final List<String> FIELD_IDS = Collections.unmodifiableList(
		Arrays.asList(PathogenTestDto.SERO_GROUP_SPECIFICATION, PathogenTestDto.SERO_GROUP_SPECIFICATION_TEXT, PathogenTestDto.DRUG_SUSCEPTIBILITY));

	private DrugSusceptibilityForm drugSusceptibilityField;

	@Override
	public String getHtmlLayout() {
		return "";
	}

	@Override
	public Collection<String> getFieldIds() {
		return FIELD_IDS;
	}

	@Override
	public void bindFields(FieldGroup fieldGroup, CustomLayout panel, Disease disease) {
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
		FieldHelper.setVisibleWhen(fieldGroup, PathogenTestDto.SERO_GROUP_SPECIFICATION, imiSeroTypingDependencies, true);
		FieldHelper.setVisibleWhen(
			fieldGroup,
			PathogenTestDto.SERO_GROUP_SPECIFICATION_TEXT,
			PathogenTestDto.SERO_GROUP_SPECIFICATION,
			SeroGroupSpecification.OTHER,
			true);

		drugSusceptibilityField = new DrugSusceptibilityForm(
			FieldVisibilityCheckers.getNoop(),
			UiFieldAccessCheckers.getDefault(true, FacadeProvider.getConfigFacade().getCountryLocale()));
		drugSusceptibilityField.setCaption(null);
		fieldGroup.bind(drugSusceptibilityField, PathogenTestDto.DRUG_SUSCEPTIBILITY);
	}

	@Override
	public void unbindFields(FieldGroup fieldGroup, CustomLayout panel) {
		if (drugSusceptibilityField != null) {
			fieldGroup.unbind(drugSusceptibilityField);
			panel.removeComponent(drugSusceptibilityField);
			drugSusceptibilityField = null;
		}
	}

	@Override
	public void onTestTypeChanged(PathogenTestType testType, Disease disease, AbstractField<PathogenTestResultType> testResultField) {
		if (drugSusceptibilityField != null) {
			drugSusceptibilityField.updateFieldsVisibility(disease, testType);
		}

		if (testType == null || !FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)) {
			return;
		}

		if (disease == Disease.INVASIVE_MENINGOCOCCAL_INFECTION && testType == PathogenTestType.ANTIBIOTIC_SUSCEPTIBILITY) {
			testResultField.setValue(PathogenTestResultType.POSITIVE);
		}
	}

	@Override
	public Disease[] getDiseases() {
		return new Disease[] {
			Disease.INVASIVE_MENINGOCOCCAL_INFECTION };
	}
}
