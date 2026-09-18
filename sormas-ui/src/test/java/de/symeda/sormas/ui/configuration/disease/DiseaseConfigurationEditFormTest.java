/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.ui.configuration.disease;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.disease.DiseaseConfigurationDto;
import de.symeda.sormas.api.exposure.ExposureCategory;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.utils.components.CheckboxSet;

class DiseaseConfigurationEditFormTest extends AbstractUiBeanTest {

	@Test
	void shouldHideLegacyCategoriesForNewConfiguration() {
		DiseaseConfigurationEditForm form = new DiseaseConfigurationEditForm();
		DiseaseConfigurationDto config = new DiseaseConfigurationDto();
		config.setAgeGroups(new ArrayList<>());

		form.setValue(config);

		@SuppressWarnings("unchecked")
		CheckboxSet<ExposureCategory> exposureCategoriesField =
			(CheckboxSet<ExposureCategory>) form.getField(DiseaseConfigurationDto.EXPOSURE_CATEGORIES);
		assertFalse(exposureCategoriesField.getItems().contains(ExposureCategory.AIR_BORNE));
	}

	@Test
	void shouldShowSelectedLegacyCategoryWithDeprecatedMarkerUntilDeselected() {
		DiseaseConfigurationEditForm form = new DiseaseConfigurationEditForm();
		DiseaseConfigurationDto config = new DiseaseConfigurationDto();
		config.setAgeGroups(new ArrayList<>());
		config.setExposureCategories(new HashSet<>(EnumSet.of(ExposureCategory.AIR_BORNE, ExposureCategory.RESPIRATORY)));

		form.setValue(config);

		@SuppressWarnings("unchecked")
		CheckboxSet<ExposureCategory> exposureCategoriesField =
			(CheckboxSet<ExposureCategory>) form.getField(DiseaseConfigurationDto.EXPOSURE_CATEGORIES);

		assertTrue(exposureCategoriesField.getItems().contains(ExposureCategory.AIR_BORNE));
		assertTrue(exposureCategoriesField.getItemCaption(ExposureCategory.AIR_BORNE).contains("line-through"));

		exposureCategoriesField.setValue(new HashSet<>(EnumSet.of(ExposureCategory.RESPIRATORY)));

		assertFalse(exposureCategoriesField.getItems().contains(ExposureCategory.AIR_BORNE));
	}
}
