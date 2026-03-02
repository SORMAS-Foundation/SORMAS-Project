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

import java.util.Collection;
import java.util.Collections;

import com.vaadin.ui.CustomLayout;
import com.vaadin.v7.data.fieldgroup.FieldGroup;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.ui.utils.FieldHelper;

/**
 * Disease section for CORONAVIRUS: wires PCR_TEST_SPECIFICATION visibility.
 * Field lives in the common layout; this section owns the visibility registration.
 */
public class CoronavirusDiseaseSectionLayout implements DiseaseSectionLayout {

	private static final Collection<String> FIELD_IDS = Collections.singletonList(PathogenTestDto.PCR_TEST_SPECIFICATION);

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
		FieldHelper
			.setVisibleWhen(fieldGroup, PathogenTestDto.PCR_TEST_SPECIFICATION, PathogenTestForm.PCR_TEST_SPECIFICATION_VISIBILITY_CONDITIONS, true);
	}

	@Override
	public void unbindFields(FieldGroup fieldGroup, CustomLayout panel) {
		// The PCR_TEST_SPECIFICATION visibility condition is keyed on TESTED_DISEASE=CORONAVIRUS,
		// so it is inert when any other disease is active. No listener removal needed here;
		// Phase 5 will consolidate all common-layout registrations.
	}

	@Override
	public Disease[] getDiseases() {
		return new Disease[] {
			Disease.CORONAVIRUS };
	}
}
