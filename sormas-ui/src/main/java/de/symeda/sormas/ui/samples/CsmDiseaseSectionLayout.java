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

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.ui.utils.FieldHelper;

/**
 * Disease section for CSM: wires serotype visibility (positive result only).
 * Field lives in the common layout; this section owns the visibility registration.
 */
public class CsmDiseaseSectionLayout implements DiseaseSectionLayout {

	private static final List<String> FIELD_IDS = Collections.singletonList(PathogenTestDto.SEROTYPE);

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
		Map<Object, List<Object>> serotypeVisibilityDependencies = new HashMap<>();
		serotypeVisibilityDependencies.put(PathogenTestDto.TESTED_DISEASE, Arrays.asList(Disease.CSM));
		serotypeVisibilityDependencies.put(PathogenTestDto.TEST_RESULT, Arrays.asList(PathogenTestResultType.POSITIVE));
		FieldHelper.setVisibleWhen(fieldGroup, Arrays.asList(PathogenTestDto.SEROTYPE), serotypeVisibilityDependencies, true);
	}

	@Override
	public void unbindFields(FieldGroup fieldGroup, CustomLayout panel) {
		// setVisibleWhen listeners are attached to TESTED_DISEASE and TEST_RESULT source fields.
		// Those source fields remain in the FieldGroup across section swaps and their
		// multi-condition visibility logic is harmless when this disease is not active,
		// because the TESTED_DISEASE condition will never match CSM for another section.
	}

	@Override
	public Disease[] getDiseases() {
		return new Disease[] {
			Disease.CSM };
	}
}
