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

package de.symeda.sormas.ui.exposure;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.exposure.AnimalLocation;
import de.symeda.sormas.api.exposure.ExposureCategory;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.exposure.ExposureSetting;
import de.symeda.sormas.api.exposure.ExposureSubSetting;
import de.symeda.sormas.api.exposure.ExposureType;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.AbstractUiBeanTest;

class ExposureFormSmokeTest extends AbstractUiBeanTest {

	@Test
	void shouldShowTravelAndProphylaxisFieldsForMalariaLegacyTravelPath() {
		ExposureForm form = new ExposureForm(
			true,
			CaseDataDto.class,
			Collections.emptyList(),
			FieldVisibilityCheckers.withDisease(Disease.MALARIA),
			UiFieldAccessCheckers.getNoop(),
			Disease.MALARIA,
			Collections.emptyList(),
			Collections.emptyMap());

		ExposureDto exposure = ExposureDto.build(ExposureType.TRAVEL);
		exposure.setExposureCategory(ExposureCategory.VECTOR_BORNE);
		exposure.setExposureSetting(ExposureSetting.MOSQUITO_BORNE);
		exposure.setSubSettings(EnumSet.of(ExposureSubSetting.TRAVELED_ABROAD));

		assertDoesNotThrow(() -> form.setValue(exposure));

		Field<?> prophylaxisField = form.getField(ExposureDto.PROPHYLAXIS_ADHERENCE);
		Field<?> travelPurposeField = form.getField(ExposureDto.TRAVEL_PURPOSE);

		assertTrue(prophylaxisField.isVisible());
		assertTrue(travelPurposeField.isVisible());
	}

	@Test
	void shouldAllowLegacyCategoryOnlyUntilDeselected() {
		ExposureForm form = new ExposureForm(
			true,
			CaseDataDto.class,
			Collections.emptyList(),
			FieldVisibilityCheckers.withDisease(Disease.MALARIA),
			UiFieldAccessCheckers.getNoop(),
			Disease.MALARIA,
			Collections.emptyList(),
			Collections.emptyMap());

		ExposureDto exposure = ExposureDto.build(ExposureType.WORK);
		exposure.setExposureCategory(ExposureCategory.AIR_BORNE);
		exposure.setExposureSetting(ExposureSetting.INDOOR);

		assertDoesNotThrow(() -> form.setValue(exposure));

		ComboBox categoryField = (ComboBox) form.getField(ExposureDto.EXPOSURE_CATEGORY);
		ComboBox settingField = (ComboBox) form.getField(ExposureDto.EXPOSURE_SETTING);
		assertEquals("deprecated-select-item", categoryField.getItemStyleGenerator().getStyle(categoryField, ExposureCategory.AIR_BORNE));
		assertTrue(categoryField.getStyleName().contains("deprecated-select-value"));
		assertTrue(categoryField.getItemIds().contains(ExposureCategory.AIR_BORNE));
		assertTrue(settingField.getItemIds().contains(ExposureSetting.INDOOR));
		assertEquals(ExposureSetting.INDOOR, settingField.getValue());

		categoryField.setValue(ExposureCategory.RESPIRATORY);

		assertFalse(categoryField.getItemIds().contains(ExposureCategory.AIR_BORNE));
		assertFalse(categoryField.getStyleName().contains("deprecated-select-value"));
	}

	@Test
	void shouldDisableLegacySubSettingAfterDeselection() {
		ExposureForm form = new ExposureForm(
			true,
			CaseDataDto.class,
			Collections.emptyList(),
			FieldVisibilityCheckers.withDisease(Disease.MALARIA),
			UiFieldAccessCheckers.getNoop(),
			Disease.MALARIA,
			Collections.emptyList(),
			Collections.emptyMap());

		ExposureDto exposure = ExposureDto.build(ExposureType.TRAVEL);
		exposure.setExposureCategory(ExposureCategory.VECTOR_BORNE);
		exposure.setExposureSetting(ExposureSetting.MOSQUITO_BORNE);
		exposure.setSubSettings(EnumSet.of(ExposureSubSetting.TRAVELED_ABROAD));

		assertDoesNotThrow(() -> form.setValue(exposure));

		OptionGroup subSettingsField = (OptionGroup) form.getField(ExposureDto.SUB_SETTINGS);
		assertTrue(subSettingsField.getItemCaption(ExposureSubSetting.TRAVELED_ABROAD).contains("line-through"));
		assertTrue(subSettingsField.getItemIds().contains(ExposureSubSetting.TRAVELED_ABROAD));

		subSettingsField.setValue(Collections.<ExposureSubSetting> emptySet());

		assertFalse(subSettingsField.getItemIds().contains(ExposureSubSetting.TRAVELED_ABROAD));
		@SuppressWarnings("unchecked")
		Set<ExposureSubSetting> remainingSubSettings = (Set<ExposureSubSetting>) subSettingsField.getValue();
		assertTrue(remainingSubSettings == null || !remainingSubSettings.contains(ExposureSubSetting.TRAVELED_ABROAD));
	}

	@Test
	void shouldShowAnimalLocationDetailsOnlyForOtherLocation() {
		ExposureForm form = new ExposureForm(
			true,
			CaseDataDto.class,
			Collections.emptyList(),
			FieldVisibilityCheckers.withDisease(Disease.MALARIA),
			UiFieldAccessCheckers.getNoop(),
			Disease.MALARIA,
			Collections.emptyList(),
			Collections.emptyMap());

		ExposureDto exposure = ExposureDto.build(ExposureType.WORK);
		exposure.setExposureCategory(ExposureCategory.ANIMAL_CONTACT);
		exposure.setAnimalLocation(AnimalLocation.OTHER);
		exposure.setAnimalLocationText("Legacy indoor location");

		assertDoesNotThrow(() -> form.setValue(exposure));

		Field<?> detailsField = form.getField(ExposureDto.ANIMAL_LOCATION_TEXT);
		assertTrue(detailsField.isVisible());

		OptionGroup animalLocationField = (OptionGroup) form.getField(ExposureDto.ANIMAL_LOCATION);
		animalLocationField.setValue(AnimalLocation.FOREST);

		assertFalse(detailsField.isVisible());
		assertTrue(detailsField.getValue() == null || ((String) detailsField.getValue()).isEmpty());
	}
}
