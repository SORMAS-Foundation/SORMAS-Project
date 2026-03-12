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

import java.util.Collection;
import java.util.Collections;

import com.vaadin.ui.CustomLayout;
import com.vaadin.v7.data.fieldgroup.FieldGroup;

import de.symeda.sormas.api.Disease;

/**
 * No-op section for diseases that have no extra fields beyond the common layout.
 */
public class DefaultDiseaseSectionLayout implements DiseaseSectionLayout {

	@Override
	public String getHtmlLayout() {
		return "";
	}

	@Override
	public Collection<String> getFieldIds() {
		return Collections.emptyList();
	}

	@Override
	public void bindFields(FieldGroup fieldGroup, CustomLayout panel, Disease disease, PathogenTestFormConfig config) {
		// no disease-specific fields
	}

	@Override
	public void unbindFields(FieldGroup fieldGroup, CustomLayout panel) {
		// nothing to remove
	}

}
