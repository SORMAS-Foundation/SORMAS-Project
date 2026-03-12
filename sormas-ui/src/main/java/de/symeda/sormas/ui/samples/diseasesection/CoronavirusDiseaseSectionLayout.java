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
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.ui.utils.FieldHelper;

/**
 * Disease section for CORONAVIRUS: owns PCR_TEST_SPECIFICATION field and wires its visibility.
 */
public class CoronavirusDiseaseSectionLayout implements DiseaseSectionLayout {

	//@formatter:off
	private static final String HTML = fluidRowLocs(PathogenTestDto.PCR_TEST_SPECIFICATION, "");
	//@formatter:on

	private static final Collection<String> FIELD_IDS = Collections.singletonList(PathogenTestDto.PCR_TEST_SPECIFICATION);

	static final Map<Object, List<Object>> PCR_TEST_SPECIFICATION_VISIBILITY_CONDITIONS = Collections.unmodifiableMap(new HashMap<>() {

		{
			put(PathogenTestDto.TESTED_DISEASE, Collections.unmodifiableList(Arrays.asList(Disease.CORONAVIRUS)));
			put(PathogenTestDto.TEST_TYPE, Collections.unmodifiableList(Arrays.asList(PathogenTestType.PCR_RT_PCR)));
		}
	});

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
		ComboBox pcrTestSpecification =
			fieldGroup.buildAndBind(PathogenTestDto.PCR_TEST_SPECIFICATION, (Object) PathogenTestDto.PCR_TEST_SPECIFICATION, ComboBox.class);
		pcrTestSpecification.setId(PathogenTestDto.PCR_TEST_SPECIFICATION);
		pcrTestSpecification.setVisible(false);
		panel.addComponent(pcrTestSpecification, PathogenTestDto.PCR_TEST_SPECIFICATION);

		FieldHelper.setVisibleWhen(fieldGroup, PathogenTestDto.PCR_TEST_SPECIFICATION, PCR_TEST_SPECIFICATION_VISIBILITY_CONDITIONS, true);
	}

	@Override
	public void unbindFields(FieldGroup fieldGroup, CustomLayout panel) {
		Field<?> field = fieldGroup.getField(PathogenTestDto.PCR_TEST_SPECIFICATION);
		if (field != null) {
			fieldGroup.unbind(field);
			panel.removeComponent(field);
		}
	}
}
