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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import com.vaadin.ui.Label;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
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
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.AbstractUiBeanTest;

class ExposureFormSmokeTest extends AbstractUiBeanTest {

	@Test
	void shouldControlTravelFieldsByExposureTypeForMalaria() {
		ExposureForm form = new ExposureForm(
			true,
			CaseDataDto.class,
			Collections.emptyList(),
			FieldVisibilityCheckers.withDisease(Disease.MALARIA),
			UiFieldAccessCheckers.getNoop(),
			Disease.MALARIA,
			Collections.emptyList(),
			Collections.emptyMap());

		ExposureDto travelExposure = ExposureDto.build(ExposureType.TRAVEL);
		travelExposure.setExposureCategory(ExposureCategory.VECTOR_BORNE);
		travelExposure.setExposureSetting(ExposureSetting.MOSQUITO_BORNE);

		assertDoesNotThrow(() -> form.setValue(travelExposure));

		Field<?> prophylaxisField = form.getField(ExposureDto.PROPHYLAXIS_ADHERENCE);
		Field<?> travelPurposeField = form.getField(ExposureDto.TRAVEL_PURPOSE);

		assertTrue(prophylaxisField.isVisible());
		assertTrue(travelPurposeField.isVisible());

		ComboBox exposureTypeField = (ComboBox) form.getField(ExposureDto.EXPOSURE_TYPE);
		exposureTypeField.setValue(ExposureType.WORK);

		assertFalse(prophylaxisField.isVisible());
		assertFalse(travelPurposeField.isVisible());
	}

	@Test
	void shouldApplyDiseaseSpecificVisibilityForTravelFields() {
		ExposureForm form = new ExposureForm(
			true,
			CaseDataDto.class,
			Collections.emptyList(),
			FieldVisibilityCheckers.withDisease(Disease.DENGUE),
			UiFieldAccessCheckers.getNoop(),
			Disease.DENGUE,
			Collections.emptyList(),
			Collections.emptyMap());

		ExposureDto travelExposure = ExposureDto.build(ExposureType.TRAVEL);

		assertDoesNotThrow(() -> form.setValue(travelExposure));

		Field<?> prophylaxisField = form.getField(ExposureDto.PROPHYLAXIS_ADHERENCE);
		Field<?> travelPurposeField = form.getField(ExposureDto.TRAVEL_PURPOSE);

		assertFalse(prophylaxisField.isVisible());
		assertTrue(travelPurposeField.isVisible());
	}

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

	@Test
	void shouldLoadDeprecatedCategoryAndShowWarning() {
		ExposureForm form = createMalariaForm();

		ExposureDto exposure = ExposureDto.build(ExposureType.WORK);
		exposure.setExposureCategory(ExposureCategory.AIR_BORNE);
		exposure.setExposureSetting(ExposureSetting.INDOOR);

		assertDoesNotThrow(() -> form.setValue(exposure));

		assertEquals(ExposureCategory.AIR_BORNE, form.getField(ExposureDto.EXPOSURE_CATEGORY).getValue());
		assertEquals(ExposureSetting.INDOOR, form.getField(ExposureDto.EXPOSURE_SETTING).getValue());
		assertTrue(isDeprecatedValuesWarningVisible(form));
	}

	@Test
	void shouldBlockSaveWhenDeprecatedCategoryIsSelected() {
		ExposureForm form = createMalariaForm();

		ExposureDto exposure = ExposureDto.build(ExposureType.WORK);
		exposure.setExposureCategory(ExposureCategory.AIR_BORNE);
		exposure.setExposureSetting(ExposureSetting.INDOOR);
		form.setValue(exposure);

		Validator.InvalidValueException exception = assertThrows(Validator.InvalidValueException.class, () -> form.preCommit(null));
		assertEquals(I18nProperties.getString(Strings.messageExposureContainsDeprecatedValues), exception.getMessage());

		// The commit button of the popup commits the field group, which must fail with the same message
		FieldGroup.CommitException commitException = assertThrows(FieldGroup.CommitException.class, () -> form.getFieldGroup().commit());
		assertTrue(commitException.getCause() instanceof Validator.InvalidValueException);
		assertEquals(I18nProperties.getString(Strings.messageExposureContainsDeprecatedValues), commitException.getCause().getMessage());
	}

	@Test
	void shouldAllowSaveAfterDeprecatedCategoryIsReplaced() {
		ExposureForm form = createMalariaForm();

		ExposureDto exposure = ExposureDto.build(ExposureType.WORK);
		exposure.setExposureCategory(ExposureCategory.AIR_BORNE);
		exposure.setExposureSetting(ExposureSetting.INDOOR);
		form.setValue(exposure);

		ComboBox categoryField = (ComboBox) form.getField(ExposureDto.EXPOSURE_CATEGORY);
		categoryField.setValue(ExposureCategory.RESPIRATORY);

		assertFalse(isDeprecatedValuesWarningVisible(form));
		assertDoesNotThrow(() -> form.preCommit(null));
	}

	@Test
	void shouldLoadDeprecatedSubSettingAndBlockSave() {
		ExposureForm form = createMalariaForm();

		ExposureDto exposure = ExposureDto.build(ExposureType.TRAVEL);
		exposure.setExposureCategory(ExposureCategory.VECTOR_BORNE);
		exposure.setExposureSetting(ExposureSetting.MOSQUITO_BORNE);
		exposure.setSubSettings(EnumSet.of(ExposureSubSetting.TRAVELED_ABROAD));

		assertDoesNotThrow(() -> form.setValue(exposure));

		@SuppressWarnings("unchecked")
		Set<ExposureSubSetting> subSettings = (Set<ExposureSubSetting>) form.getField(ExposureDto.SUB_SETTINGS).getValue();
		assertTrue(subSettings.contains(ExposureSubSetting.TRAVELED_ABROAD));
		assertTrue(isDeprecatedValuesWarningVisible(form));
		assertThrows(Validator.InvalidValueException.class, () -> form.preCommit(null));
	}

	@Test
	void shouldBlockSaveUntilAllDeprecatedValuesAreRemoved() {
		ExposureForm form = createMalariaForm();

		// MOSQUITO_BORNE (setting) and TRAVELED_ABROAD (sub-setting) are both deprecated
		ExposureDto exposure = ExposureDto.build(ExposureType.TRAVEL);
		exposure.setExposureCategory(ExposureCategory.VECTOR_BORNE);
		exposure.setExposureSetting(ExposureSetting.MOSQUITO_BORNE);
		exposure.setSubSettings(EnumSet.of(ExposureSubSetting.TRAVELED_ABROAD));
		form.setValue(exposure);

		OptionGroup subSettingsField = (OptionGroup) form.getField(ExposureDto.SUB_SETTINGS);
		subSettingsField.setValue(Collections.<ExposureSubSetting> emptySet());

		// the deprecated setting is still selected, so the save must still be blocked
		assertTrue(isDeprecatedValuesWarningVisible(form));
		assertThrows(Validator.InvalidValueException.class, () -> form.preCommit(null));

		// switching to an active category clears the deprecated setting
		ComboBox categoryField = (ComboBox) form.getField(ExposureDto.EXPOSURE_CATEGORY);
		categoryField.setValue(ExposureCategory.RESPIRATORY);

		assertFalse(isDeprecatedValuesWarningVisible(form));
		assertDoesNotThrow(() -> form.preCommit(null));
	}

	@Test
	void shouldNotShowWarningOrBlockSaveWithoutDeprecatedValues() {
		ExposureForm form = createMalariaForm();

		ExposureDto exposure = ExposureDto.build(ExposureType.WORK);
		exposure.setExposureCategory(ExposureCategory.RESPIRATORY);

		assertDoesNotThrow(() -> form.setValue(exposure));

		assertFalse(isDeprecatedValuesWarningVisible(form));
		assertDoesNotThrow(() -> form.preCommit(null));
	}

	private static ExposureForm createMalariaForm() {
		return new ExposureForm(
			true,
			CaseDataDto.class,
			Collections.emptyList(),
			FieldVisibilityCheckers.withDisease(Disease.MALARIA),
			UiFieldAccessCheckers.getNoop(),
			Disease.MALARIA,
			Collections.emptyList(),
			Collections.emptyMap());
	}

	private static boolean isDeprecatedValuesWarningVisible(ExposureForm form) {
		String message = I18nProperties.getString(Strings.messageExposureContainsDeprecatedValues);
		AtomicBoolean visible = new AtomicBoolean(false);
		form.forEachComponent(component -> {
			if (component instanceof Label && ((Label) component).getValue().contains(message)) {
				visible.set(component.isVisible());
			}
		});
		return visible.get();
	}
}
