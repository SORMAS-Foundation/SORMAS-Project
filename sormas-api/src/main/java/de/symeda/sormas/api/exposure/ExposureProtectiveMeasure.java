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
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.Diseases;

public enum ExposureProtectiveMeasure {

	WEARING_MASK(EnumSet.of(ExposureCategory.RESPIRATORY, ExposureCategory.AIR_BORNE), EnumSet.noneOf(ExposureSetting.class)),

	@Deprecated
	DISTANCE_1_5M(EnumSet.of(ExposureCategory.AIR_BORNE), EnumSet.of(ExposureSetting.INDOOR, ExposureSetting.OUTDOOR), true),

	PHYSICAL_DISTANCING(EnumSet.of(ExposureCategory.RESPIRATORY, ExposureCategory.PERSON_TO_PERSON), EnumSet.noneOf(ExposureSetting.class)),

	@Deprecated
	FACE_TO_FACE_15MIN(EnumSet.of(ExposureCategory.AIR_BORNE), EnumSet.of(ExposureSetting.INDOOR), true),

	@Deprecated
	VENTILATION_HEPA(EnumSet.of(ExposureCategory.AIR_BORNE), EnumSet.of(ExposureSetting.INDOOR), true),

	HAND_HYGIENE(EnumSet.of(
		ExposureCategory.RESPIRATORY,
		ExposureCategory.PERSON_TO_PERSON,
		ExposureCategory.FOMITE_TRANSMISSION,
		ExposureCategory.FOOD_BORNE,
		ExposureCategory.ENVIRONMENTAL,
		ExposureCategory.DIRECT_CONTACT), EnumSet.noneOf(ExposureSetting.class)),

	AVOID_TOUCHING_FACE(EnumSet.of(ExposureCategory.PERSON_TO_PERSON, ExposureCategory.DIRECT_CONTACT), EnumSet.noneOf(ExposureSetting.class)),

	SAFE_SEX(EnumSet.of(ExposureCategory.PERSON_TO_PERSON, ExposureCategory.DIRECT_CONTACT), EnumSet.noneOf(ExposureSetting.class)),

	WEARING_PPE(EnumSet.of(
		ExposureCategory.PERSON_TO_PERSON,
		ExposureCategory.ANIMAL_CONTACT,
		ExposureCategory.FOMITE_TRANSMISSION,
		ExposureCategory.ENVIRONMENTAL,
		ExposureCategory.BLOOD_PARENTERAL,
		ExposureCategory.DIRECT_CONTACT), EnumSet.noneOf(ExposureSetting.class)),

	VACCINATION(EnumSet.of(ExposureCategory.FOMITE_TRANSMISSION, ExposureCategory.VERTICAL_TRANSMISSION), EnumSet.noneOf(ExposureSetting.class)),

	CLEANING(EnumSet.of(ExposureCategory.FOMITE_TRANSMISSION), EnumSet.noneOf(ExposureSetting.class)),

	DISINFECTION(EnumSet.of(ExposureCategory.FOMITE_TRANSMISSION), EnumSet.noneOf(ExposureSetting.class)),

	VACCINATION_ANIMAL(EnumSet.of(ExposureCategory.ANIMAL_CONTACT), EnumSet.noneOf(ExposureSetting.class)),

	VACCINATION_CASE(EnumSet.of(ExposureCategory.ANIMAL_CONTACT), EnumSet.noneOf(ExposureSetting.class)),

	@Deprecated
	HAND_WASHING(EnumSet.of(ExposureCategory.ANIMAL_CONTACT, ExposureCategory.FOMITE_TRANSMISSION), EnumSet.noneOf(ExposureSetting.class), true),

	@Deprecated
	WOUND_WASHING(EnumSet.of(ExposureCategory.ANIMAL_CONTACT), EnumSet.noneOf(ExposureSetting.class), true),

	HAND_OR_WOUND_HYGIENE(EnumSet.of(ExposureCategory.ANIMAL_CONTACT), EnumSet.noneOf(ExposureSetting.class)),

	INSECT_REPELLENT(EnumSet.of(ExposureCategory.VECTOR_BORNE), EnumSet.noneOf(ExposureSetting.class)),

	@Deprecated
	HERBS(EnumSet.of(ExposureCategory.VECTOR_BORNE), EnumSet.of(ExposureSetting.OUTDOOR), true),

	COILS(EnumSet.of(ExposureCategory.VECTOR_BORNE), EnumSet.noneOf(ExposureSetting.class)),

	SLEEPING_UNDER_BEDNET(EnumSet.of(ExposureCategory.VECTOR_BORNE), EnumSet.noneOf(ExposureSetting.class)),

	INDOOR_SPRAYING(EnumSet.of(ExposureCategory.VECTOR_BORNE), EnumSet.noneOf(ExposureSetting.class)),

	AIR_CONDITION(EnumSet.of(ExposureCategory.VECTOR_BORNE), EnumSet.noneOf(ExposureSetting.class)),

	PROTECTIVE_CLOTHING(EnumSet.of(ExposureCategory.VECTOR_BORNE), EnumSet.noneOf(ExposureSetting.class)),

	ENVIRONMENTAL_CONTROL(EnumSet.of(ExposureCategory.VECTOR_BORNE, ExposureCategory.ENVIRONMENTAL), EnumSet.noneOf(ExposureSetting.class)),

	@Deprecated
	TICK_PREVENTION(EnumSet.of(ExposureCategory.VECTOR_BORNE), EnumSet.of(ExposureSetting.TICK_BORNE), true),

	REGULAR_CHECKS(EnumSet.of(ExposureCategory.VECTOR_BORNE), EnumSet.noneOf(ExposureSetting.class)),

	WATER_PURIFICATION(EnumSet.of(ExposureCategory.WATER_BORNE), EnumSet.of(ExposureSetting.DRINKING_WATER)),

	SAFE_WATER_SOURCES(EnumSet.of(ExposureCategory.WATER_BORNE), EnumSet.of(ExposureSetting.DRINKING_WATER)),

	AVOID_RAW_FOODS(EnumSet.of(ExposureCategory.WATER_BORNE), EnumSet.of(ExposureSetting.DRINKING_WATER)),

	NO_SWIMMING_CONTAMINATED(EnumSet.of(ExposureCategory.WATER_BORNE),
		EnumSet.of(ExposureSetting.RECREATIONAL_WATER, ExposureSetting.OCCUPATIONAL_ENVIRONMENTAL)),

	SHOWER_AFTER_SWIMMING(EnumSet.of(ExposureCategory.WATER_BORNE),
		EnumSet.of(ExposureSetting.RECREATIONAL_WATER, ExposureSetting.OCCUPATIONAL_ENVIRONMENTAL)),

	WOUND_COVERAGE(EnumSet.of(ExposureCategory.WATER_BORNE),
		EnumSet.of(ExposureSetting.RECREATIONAL_WATER, ExposureSetting.HOUSEHOLD, ExposureSetting.OCCUPATIONAL_ENVIRONMENTAL)),

	@Deprecated
	BATHING_CONTAMINATED_WATER(EnumSet.of(ExposureCategory.WATER_BORNE), EnumSet.of(ExposureSetting.RECREATIONAL_WATER), true),

	AVOIDING_INGESTION(EnumSet.of(ExposureCategory.WATER_BORNE), EnumSet.noneOf(ExposureSetting.class)),

	HYGIENE(EnumSet.of(ExposureCategory.WATER_BORNE), EnumSet.noneOf(ExposureSetting.class)),

	WELL_COOKED(EnumSet.of(ExposureCategory.FOOD_BORNE), EnumSet.noneOf(ExposureSetting.class)),

	COLD_HOT_CHAIN(EnumSet.of(ExposureCategory.FOOD_BORNE), EnumSet.noneOf(ExposureSetting.class)),

	WASHED(EnumSet.of(ExposureCategory.FOOD_BORNE), EnumSet.noneOf(ExposureSetting.class)),

	@Diseases(Disease.YERSINIOSIS)
	EXCLUSION_DIET_YERSINIOSIS(EnumSet.of(ExposureCategory.FOOD_BORNE), EnumSet.noneOf(ExposureSetting.class)),

	MEDICATION(EnumSet.of(ExposureCategory.VERTICAL_TRANSMISSION), EnumSet.of(ExposureSetting.PREGNANCY_OR_DELIVERY, ExposureSetting.BREASTFEEDING)),

	C_SECTION(EnumSet.of(ExposureCategory.VERTICAL_TRANSMISSION), EnumSet.of(ExposureSetting.PREGNANCY_OR_DELIVERY)),

	SAFE_DELIVERY(EnumSet.of(ExposureCategory.VERTICAL_TRANSMISSION), EnumSet.of(ExposureSetting.PREGNANCY_OR_DELIVERY)),

	BREASTFEEDING_PRECAUTIONS(EnumSet.of(ExposureCategory.VERTICAL_TRANSMISSION),
		EnumSet.of(ExposureSetting.PREGNANCY_OR_DELIVERY, ExposureSetting.BREASTFEEDING)),

	AVOIDANCE(EnumSet.of(ExposureCategory.ANIMAL_CONTACT, ExposureCategory.ENVIRONMENTAL), EnumSet.noneOf(ExposureSetting.class)),

	GLOVES(EnumSet.of(ExposureCategory.BLOOD_PARENTERAL, ExposureCategory.FOMITE_TRANSMISSION), EnumSet.noneOf(ExposureSetting.class)),

	SAFE_INJECTION(EnumSet.of(ExposureCategory.BLOOD_PARENTERAL), EnumSet.noneOf(ExposureSetting.class)),

	SHARPS_SAFETY(EnumSet.of(ExposureCategory.BLOOD_PARENTERAL), EnumSet.noneOf(ExposureSetting.class)),

	BLOOD_SCREENING(EnumSet.of(ExposureCategory.BLOOD_PARENTERAL),
		EnumSet.of(
			ExposureSetting.TRANSFUSION,
			ExposureSetting.ORGAN_TISSUE_TRANSPLANTATION,
			ExposureSetting.INJECTION_DRUG_USE,
			ExposureSetting.HEALTHCARE_PROCEDURE)),

	CONDOM_BARRIER(EnumSet.of(ExposureCategory.SEXUAL), EnumSet.noneOf(ExposureSetting.class)),

	PARTNER_NOTIFICATION(EnumSet.of(ExposureCategory.SEXUAL), EnumSet.noneOf(ExposureSetting.class)),

	STI_PREVENTION(EnumSet.of(ExposureCategory.SEXUAL), EnumSet.noneOf(ExposureSetting.class)),

	UNKNOWN(EnumSet.noneOf(ExposureCategory.class), EnumSet.noneOf(ExposureSetting.class)),
	OTHER(EnumSet.noneOf(ExposureCategory.class), EnumSet.noneOf(ExposureSetting.class));

	private final Set<ExposureCategory> categories;
	private final Set<ExposureSetting> settings;
	private final boolean deprecated;

	ExposureProtectiveMeasure(Set<ExposureCategory> categories, Set<ExposureSetting> settings, boolean deprecated) {
		this.categories = categories;
		this.settings = settings;
		this.deprecated = deprecated;
	}

	ExposureProtectiveMeasure(Set<ExposureCategory> categories, Set<ExposureSetting> settings) {
		this(categories, settings, false);
	}

	public Set<ExposureCategory> getCategories() {
		return categories;
	}

	public Set<ExposureSetting> getSettings() {
		return settings;
	}

	public static List<ExposureProtectiveMeasure> getValues(ExposureCategory category, ExposureSetting setting) {
		return getValues(category, setting, false);
	}

	public static List<ExposureProtectiveMeasure> getValues(ExposureCategory category, ExposureSetting setting, boolean includeDeprecated) {
		if (category == null) {
			return Collections.emptyList();
		}
		if (!category.hasNoSetting() && setting == null) {
			return Collections.emptyList();
		}
		return Arrays.stream(values()).filter(pm -> {
			if (pm.categories.isEmpty()) {
				return true;
			}
			if (!pm.categories.contains(category)) {
				return false;
			}
			return matchesSetting(pm.settings, category, setting);
		}).filter(pm -> includeDeprecated || !pm.isDeprecated()).collect(Collectors.toList());
	}

	private static boolean matchesSetting(Set<ExposureSetting> measureSettings, ExposureCategory category, ExposureSetting selectedSetting) {
		if (measureSettings.isEmpty()) {
			if (category.hasNoSetting()) {
				return true;
			}
			return selectedSetting != ExposureSetting.OTHER && selectedSetting != ExposureSetting.UNKNOWN;
		}
		if (selectedSetting == null) {
			return false;
		}
		return measureSettings.contains(selectedSetting);
	}

	public static List<ExposureProtectiveMeasure> getValues(ExposureCategory category, ExposureSetting setting, Disease disease) {
		return getValues(category, setting, false, disease);
	}

	public static List<ExposureProtectiveMeasure> getValues(
		ExposureCategory category,
		ExposureSetting setting,
		boolean includeDeprecated,
		Disease disease) {
		return getValues(category, setting, includeDeprecated).stream().filter(pm -> isVisibleForDisease(pm, disease)).collect(Collectors.toList());
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	private static boolean isVisibleForDisease(ExposureProtectiveMeasure protectiveMeasure, Disease disease) {
		return Diseases.DiseasesConfiguration.isMissing(ExposureProtectiveMeasure.class, protectiveMeasure.name())
			|| Diseases.DiseasesConfiguration.isDefined(ExposureProtectiveMeasure.class, protectiveMeasure.name(), disease);
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
