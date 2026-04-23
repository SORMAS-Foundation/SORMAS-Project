/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.exposure;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum ExposureSubSetting {

	CLOSED_POORLY_VENTILATED(ExposureCategory.AIR_BORNE, ExposureSetting.INDOOR),
	SHARED_HIGH_OCCUPANCY(ExposureCategory.AIR_BORNE, ExposureSetting.INDOOR),
	ENCLOSED_LIMITED_CIRCULATION(ExposureCategory.AIR_BORNE, ExposureSetting.INDOOR),
	VEHICLES(ExposureCategory.AIR_BORNE, ExposureSetting.INDOOR),
	HEALTHCARE_SETTINGS(ExposureCategory.AIR_BORNE, ExposureSetting.INDOOR),
	TEMPORARY_SHELTERS(ExposureCategory.AIR_BORNE, ExposureSetting.INDOOR),

	CROWDED_OUTDOOR_LIMITED_AIRFLOW(ExposureCategory.AIR_BORNE, ExposureSetting.OUTDOOR),

	CLOSE_PHYSICAL_CONTACT(ExposureCategory.DIRECT_CONTACT, ExposureSetting.PERSON_TO_PERSON),
	HIGH_TOUCH_ENVIRONMENTS(ExposureCategory.DIRECT_CONTACT, ExposureSetting.PERSON_TO_PERSON),
	SEXUAL_ACTIVITY(ExposureCategory.DIRECT_CONTACT, ExposureSetting.PERSON_TO_PERSON),

	BLOOD_TRANSFUSION_RECIPIENT(ExposureCategory.DIRECT_CONTACT, ExposureSetting.OTHER_DIRECT_CONTACT),
	BONE_MARROW_TRANSPLANT_RECIPIENT(ExposureCategory.DIRECT_CONTACT, ExposureSetting.OTHER_DIRECT_CONTACT),

	STANDING_WATER_AREAS(ExposureCategory.VECTOR_BORNE, ExposureSetting.MOSQUITO_BORNE),
	HIGH_MOSQUITO_ACTIVITY_REGIONS(ExposureCategory.VECTOR_BORNE, ExposureSetting.MOSQUITO_BORNE),
	TRAVELED_ABROAD(ExposureCategory.VECTOR_BORNE, ExposureSetting.MOSQUITO_BORNE),

	FORESTED_GRASSY_RURAL(ExposureCategory.VECTOR_BORNE, ExposureSetting.TICK_BORNE),
	WILDLIFE_RESERVOIR_AREAS(ExposureCategory.VECTOR_BORNE, ExposureSetting.TICK_BORNE),

	EATING_AT_HOME(ExposureCategory.FOOD_BORNE, null),
	EATING_OUTSIDE(ExposureCategory.FOOD_BORNE, null),

	UNKNOWN(null, null),
	OTHER(null, null);

	private final ExposureCategory category;
	private final ExposureSetting setting;

	ExposureSubSetting(ExposureCategory category, ExposureSetting setting) {
		this.category = category;
		this.setting = setting;
	}

	public ExposureCategory getCategory() {
		return category;
	}

	public ExposureSetting getSetting() {
		return setting;
	}

	public static List<ExposureSubSetting> getValues(ExposureCategory category, ExposureSetting setting) {
		if (category == null) {
			return Collections.emptyList();
		}

		if (category.hasNoSubSetting()) {
			return Collections.emptyList();
		}
		if (setting == null) {
			return Collections.emptyList();
		}
		boolean hasSpecific = Arrays.stream(values()).anyMatch(s -> s.category == category && s.setting == setting);
		if (!hasSpecific) {
			return Collections.emptyList();
		}
		return Arrays.stream(values())
			.filter(s -> (s.category == category && s.setting == setting) || s.category == null)
			.collect(Collectors.toList());
	}

	public static List<ExposureSubSetting> getValuesForCategoryOnly(ExposureCategory category) {
		if (category == null) {
			return Collections.emptyList();
		}

		if (category.hasNoSubSetting()) {
			return Collections.emptyList();
		}

		// Check if this category has subsettings with null setting
		boolean hasCategoryOnlySubSettings = Arrays.stream(values()).anyMatch(s -> s.category == category && s.setting == null);

		if (!hasCategoryOnlySubSettings) {
			return Collections.emptyList();
		}

		return Arrays.stream(values()).filter(s -> (s.category == category && s.setting == null) || s.category == null).collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
