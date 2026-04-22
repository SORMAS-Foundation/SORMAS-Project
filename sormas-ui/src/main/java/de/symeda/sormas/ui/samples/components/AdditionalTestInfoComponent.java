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
package de.symeda.sormas.ui.samples.components;

import com.vaadin.ui.RadioButtonGroup;

import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.ui.utils.FormComponent;

/**
 * Additional test information fields: performed by reference laboratory and retest requested.
 * Applicable to all diseases.
 */
public class AdditionalTestInfoComponent extends FormComponent<PathogenTestDto> {

	private static final long serialVersionUID = 1L;

	private RadioButtonGroup<Boolean> performedByReferenceLaboratory;
	private RadioButtonGroup<Boolean> retestRequested;

	public AdditionalTestInfoComponent() {
		super(PathogenTestDto.class);
		buildLayout();
		bindFields();
	}

	private void buildLayout() {
		performedByReferenceLaboratory =
			createBooleanRadioGroup(PathogenTestDto.PERFORMED_BY_REFERENCE_LABORATORY, PathogenTestDto.I18N_PREFIX);
		retestRequested =
			createBooleanRadioGroup(PathogenTestDto.RETEST_REQUESTED, PathogenTestDto.I18N_PREFIX);
		addRow(performedByReferenceLaboratory, retestRequested);
	}

	private void bindFields() {
		binder.forField(performedByReferenceLaboratory)
			.bind(PathogenTestDto::getPerformedByReferenceLaboratory, PathogenTestDto::setPerformedByReferenceLaboratory);
		binder.forField(retestRequested)
			.bind(PathogenTestDto::getRetestRequested, PathogenTestDto::setRetestRequested);
	}
}
