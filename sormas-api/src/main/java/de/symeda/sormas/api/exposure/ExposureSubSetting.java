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

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.Diseases;

public enum ExposureSubSetting {

	@Deprecated
	CLOSED_POORLY_VENTILATED(ExposureCategory.AIR_BORNE, ExposureSetting.INDOOR, true),
	@Deprecated
	SHARED_HIGH_OCCUPANCY(ExposureCategory.AIR_BORNE, ExposureSetting.INDOOR, true),
	@Deprecated
	ENCLOSED_LIMITED_CIRCULATION(ExposureCategory.AIR_BORNE, ExposureSetting.INDOOR, true),
	@Deprecated
	VEHICLES(ExposureCategory.AIR_BORNE, ExposureSetting.INDOOR, true),
	@Deprecated
	HEALTHCARE_SETTINGS(ExposureCategory.AIR_BORNE, ExposureSetting.INDOOR, true),
	@Deprecated
	TEMPORARY_SHELTERS(ExposureCategory.AIR_BORNE, ExposureSetting.INDOOR, true),

	@Deprecated
	CROWDED_OUTDOOR_LIMITED_AIRFLOW(ExposureCategory.AIR_BORNE, ExposureSetting.OUTDOOR, true),

	@Deprecated
	CLOSE_PHYSICAL_CONTACT(ExposureCategory.DIRECT_CONTACT, ExposureSetting.PERSON_TO_PERSON, true),
	@Deprecated
	HIGH_TOUCH_ENVIRONMENTS(ExposureCategory.DIRECT_CONTACT, ExposureSetting.PERSON_TO_PERSON, true),
	@Deprecated
	SEXUAL_ACTIVITY(ExposureCategory.DIRECT_CONTACT, ExposureSetting.PERSON_TO_PERSON, true),

	@Deprecated
	BLOOD_TRANSFUSION_RECIPIENT(ExposureCategory.DIRECT_CONTACT, ExposureSetting.OTHER_DIRECT_CONTACT, true),
	@Deprecated
	BONE_MARROW_TRANSPLANT_RECIPIENT(ExposureCategory.DIRECT_CONTACT, ExposureSetting.OTHER_DIRECT_CONTACT, true),

	@Deprecated
	STANDING_WATER_AREAS(ExposureCategory.VECTOR_BORNE, ExposureSetting.MOSQUITO_BORNE, true),
	@Deprecated
	HIGH_MOSQUITO_ACTIVITY_REGIONS(ExposureCategory.VECTOR_BORNE, ExposureSetting.MOSQUITO_BORNE, true),
	@Deprecated
	TRAVELED_ABROAD(ExposureCategory.VECTOR_BORNE, ExposureSetting.MOSQUITO_BORNE, true),

	@Deprecated
	FORESTED_GRASSY_RURAL(ExposureCategory.VECTOR_BORNE, ExposureSetting.TICK_BORNE, true),
	@Deprecated
	WILDLIFE_RESERVOIR_AREAS(ExposureCategory.VECTOR_BORNE, ExposureSetting.TICK_BORNE, true),

	SYMPTOMATIC_RESPIRATORY_CASE(ExposureCategory.RESPIRATORY, null),
	RESPIRATORY_SECRETIONS_DROPLETS(ExposureCategory.RESPIRATORY, null),
	AEROSOLS(ExposureCategory.RESPIRATORY, null),
	SHARED_INDOOR_ENVIRONMENT(ExposureCategory.RESPIRATORY, null),

	SYMPTOMATIC_CONFIRMED_CASE(ExposureCategory.PERSON_TO_PERSON, null),
	ASYMPTOMATIC_PERSON(ExposureCategory.PERSON_TO_PERSON, null),
	HOUSEHOLD_MEMBER(ExposureCategory.PERSON_TO_PERSON, null),
	COLLEAGUE_CLASSMATE(ExposureCategory.PERSON_TO_PERSON, null),
	CAREGIVER(ExposureCategory.PERSON_TO_PERSON, null),
	OTHER_PERSON(ExposureCategory.PERSON_TO_PERSON, null),

	PUBLIC_POOL(ExposureCategory.WATER_BORNE, ExposureSetting.RECREATIONAL_WATER),
	PRIVATE_POOL(ExposureCategory.WATER_BORNE, ExposureSetting.RECREATIONAL_WATER),
	LAKE(ExposureCategory.WATER_BORNE, ExposureSetting.RECREATIONAL_WATER),
	RIVER(ExposureCategory.WATER_BORNE, ExposureSetting.RECREATIONAL_WATER),
	OCEAN(ExposureCategory.WATER_BORNE, ExposureSetting.RECREATIONAL_WATER),
	WATER_PARK(ExposureCategory.WATER_BORNE, ExposureSetting.RECREATIONAL_WATER),

	@Deprecated
	EATING_AT_HOME(ExposureCategory.FOOD_BORNE, null, true),
	@Deprecated
	EATING_OUTSIDE(ExposureCategory.FOOD_BORNE, null, true),
	@Diseases({
		Disease.SHIGELLOSIS })
	@Deprecated
	HOUSEHOLD_CONTACT(ExposureCategory.DIRECT_CONTACT, ExposureSetting.PERSON_TO_PERSON, true),
	@Diseases({
		Disease.SALMONELLOSIS })
	@Deprecated
	SHOPPING_FOR_FOOD(ExposureCategory.FOOD_BORNE, null, true),

	UNKNOWN(null, null),
	OTHER(null, null);

	private final ExposureCategory category;
	private final ExposureSetting setting;

	private final boolean deprecated;

	ExposureSubSetting(ExposureCategory category, ExposureSetting setting, boolean deprecated) {
		this.category = category;
		this.setting = setting;
		this.deprecated = deprecated;
	}

	ExposureSubSetting(ExposureCategory category, ExposureSetting setting) {
		this(category, setting, false);
	}

	public ExposureCategory getCategory() {
		return category;
	}

	public ExposureSetting getSetting() {
		return setting;
	}

	public static List<ExposureSubSetting> getValues(ExposureCategory category, ExposureSetting setting) {
		return getValues(category, setting, false);
	}

	public static List<ExposureSubSetting> getValues(ExposureCategory category, ExposureSetting setting, boolean includeDeprecated) {
		if (category == null) {
			return Collections.emptyList();
		}

		if (category.hasNoSubSetting() || ExposureCategory.OTHER == category || ExposureCategory.UNKNOWN == category) {
			return Collections.emptyList();
		}
		if (!category.hasNoSetting() && setting == null || ExposureSetting.OTHER == setting || ExposureSetting.UNKNOWN == setting) {
			return Collections.emptyList();
		}

		boolean hasSpecific = Arrays.stream(values())
			.anyMatch(s -> s.category == category && (s.setting == null || s.setting == setting) && (includeDeprecated || !s.isDeprecated()));
		if (!hasSpecific) {
			return Collections.emptyList();
		}
		return Arrays.stream(values())
			.filter(s -> (s.category == category && (s.setting == null || s.setting == setting)) || s.category == null)
			.filter(s -> includeDeprecated || !s.isDeprecated())
			.collect(Collectors.toList());
	}

	/**
	 * Disease-aware overload: filters the values returned by {@link #getValues(ExposureCategory, ExposureSetting)}
	 * to those whose {@code @Diseases} annotation matches the given disease (or values with no annotation,
	 * which apply to every disease).
	 */
	public static List<ExposureSubSetting> getValues(ExposureCategory category, ExposureSetting setting, Disease disease) {
		return getValues(category, setting, false, disease);
	}

	public static List<ExposureSubSetting> getValues(ExposureCategory category, ExposureSetting setting, boolean includeDeprecated, Disease disease) {
		return getValues(category, setting, includeDeprecated).stream().filter(s -> isVisibleForDisease(s, disease)).collect(Collectors.toList());
	}

	public static List<ExposureSubSetting> getValuesForCategoryOnly(ExposureCategory category) {
		return getValuesForCategoryOnly(category, false);
	}

	public static List<ExposureSubSetting> getValuesForCategoryOnly(ExposureCategory category, boolean includeDeprecated) {
		if (category == null) {
			return Collections.emptyList();
		}

		if (category.hasNoSubSetting()) {
			return Collections.emptyList();
		}

		// Check if this category has subsettings with null setting
		boolean hasCategoryOnlySubSettings =
			Arrays.stream(values()).anyMatch(s -> s.category == category && s.setting == null && (includeDeprecated || !s.isDeprecated()));

		if (!hasCategoryOnlySubSettings) {
			return Collections.emptyList();
		}

		return Arrays.stream(values())
			.filter(s -> (s.category == category && s.setting == null) || s.category == null)
			.filter(s -> includeDeprecated || !s.isDeprecated())
			.collect(Collectors.toList());
	}

	/**
	 * Disease-aware overload: filters the values returned by {@link #getValuesForCategoryOnly(ExposureCategory)}
	 * to those whose {@code @Diseases} annotation matches the given disease (or values with no annotation).
	 */
	public static List<ExposureSubSetting> getValuesForCategoryOnly(ExposureCategory category, Disease disease) {
		return getValuesForCategoryOnly(category, false, disease);
	}

	public static List<ExposureSubSetting> getValuesForCategoryOnly(ExposureCategory category, boolean includeDeprecated, Disease disease) {
		return getValuesForCategoryOnly(category, includeDeprecated).stream()
			.filter(s -> isVisibleForDisease(s, disease))
			.collect(Collectors.toList());
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	private static boolean isVisibleForDisease(ExposureSubSetting subSetting, Disease disease) {
		return Diseases.DiseasesConfiguration.isMissing(ExposureSubSetting.class, subSetting.name())
			|| Diseases.DiseasesConfiguration.isDefined(ExposureSubSetting.class, subSetting.name(), disease);
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
