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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.sample.PathogenStrainCallStatus;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;

/**
 * Disease-specific section for TUBERCULOSIS and LATENT_TUBERCULOSIS.
 * Injects TB fields into PathogenTestForm's "diseaseSectionLoc" slot and removes them on unbind.
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

	private static final String[] FIELD_IDS = {
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
		PathogenTestDto.TUBE_MITOGENE_GT10, };

	@Override
	public String getHtmlLayout() {
		return HTML;
	}

	@Override
	public void bindFields(PathogenTestForm form) {
		NullableOptionGroup rifampicinResistant = form.addSectionField(PathogenTestDto.RIFAMPICIN_RESISTANT, NullableOptionGroup.class);
		rifampicinResistant.setVisible(false);

		NullableOptionGroup isoniazidResistant = form.addSectionField(PathogenTestDto.ISONIAZID_RESISTANT, NullableOptionGroup.class);
		isoniazidResistant.setVisible(false);

		ComboBox testScale = form.addSectionField(PathogenTestDto.TEST_SCALE, ComboBox.class);
		testScale.setVisible(false);

		ComboBox strainCallStatus = form.addSectionField(PathogenTestDto.STRAIN_CALL_STATUS, ComboBox.class);
		strainCallStatus.setItemCaptionMode(ItemCaptionMode.ID_TOSTRING);
		strainCallStatus.setVisible(false);

		ComboBox specie = form.addSectionField(PathogenTestDto.SPECIE, ComboBox.class);
		specie.setVisible(false);

		TextField patternProfile = form.addSectionField(PathogenTestDto.PATTERN_PROFILE, TextField.class);
		patternProfile.setVisible(false);

		form.addSectionDrugSusceptibilityField();

		form.addSectionTubeFields();

		// Luxembourg: wire visibility rules
		if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG)) {
			FieldHelper.setVisibleWhen(
				form.getFieldGroup(),
				PathogenTestDto.RIFAMPICIN_RESISTANT,
				PathogenTestForm.RIFAMPICIN_RESISTANT_VISIBILITY_CONDITIONS,
				true);
			FieldHelper.setVisibleWhen(form.getFieldGroup(), PathogenTestDto.TEST_SCALE, PathogenTestForm.TEST_SCALE_VISIBILITY_CONDITIONS, true);
			FieldHelper.setVisibleWhen(
				form.getFieldGroup(),
				PathogenTestDto.STRAIN_CALL_STATUS,
				PathogenTestForm.STRAIN_CALL_STATUS_VISIBILITY_CONDITIONS,
				true);
			FieldHelper.setVisibleWhen(form.getFieldGroup(), PathogenTestDto.SPECIE, PathogenTestForm.SPECIE_VISIBILITY_CONDITIONS, true);

			Map<Object, List<Object>> miruCode = new HashMap<>();
			miruCode.put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.TUBERCULOSIS, Disease.LATENT_TUBERCULOSIS));
			miruCode.put(PathogenTestDto.TEST_TYPE, Arrays.asList(PathogenTestType.MIRU_PATTERN_CODE));
			FieldHelper.setVisibleWhen(form.getFieldGroup(), PathogenTestDto.PATTERN_PROFILE, miruCode, true);

			FieldHelper.updateItems(
				strainCallStatus,
				Arrays.asList(PathogenStrainCallStatus.values()),
				FieldVisibilityCheckers.withDisease(form.getCurrentDisease()),
				PathogenStrainCallStatus.class);
		}
	}

	@Override
	public void unbindFields(PathogenTestForm form) {
		for (String id : FIELD_IDS) {
			form.removeSectionField(id);
		}
	}

	@Override
	public Disease[] getDiseases() {
		return new Disease[] {
			Disease.TUBERCULOSIS,
			Disease.LATENT_TUBERCULOSIS };
	}
}
