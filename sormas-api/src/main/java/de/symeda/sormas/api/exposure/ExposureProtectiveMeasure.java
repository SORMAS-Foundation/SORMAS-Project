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

import static de.symeda.sormas.api.exposure.ExposureCategory.AIR_BORNE;
import static de.symeda.sormas.api.exposure.ExposureCategory.ANIMAL_CONTACT;
import static de.symeda.sormas.api.exposure.ExposureCategory.DIRECT_CONTACT;
import static de.symeda.sormas.api.exposure.ExposureCategory.FOMITE_TRANSMISSION;
import static de.symeda.sormas.api.exposure.ExposureCategory.FOOD_BORNE;
import static de.symeda.sormas.api.exposure.ExposureCategory.VECTOR_BORNE;
import static de.symeda.sormas.api.exposure.ExposureCategory.VERTICAL_TRANSMISSION;
import static de.symeda.sormas.api.exposure.ExposureCategory.WATER_BORNE;
import static de.symeda.sormas.api.exposure.ExposureSetting.DRINKING_WATER;
import static de.symeda.sormas.api.exposure.ExposureSetting.INDOOR;
import static de.symeda.sormas.api.exposure.ExposureSetting.MOSQUITO_BORNE;
import static de.symeda.sormas.api.exposure.ExposureSetting.OUTDOOR;
import static de.symeda.sormas.api.exposure.ExposureSetting.PERSON_TO_PERSON;
import static de.symeda.sormas.api.exposure.ExposureSetting.RECREATIONAL_WATER;
import static de.symeda.sormas.api.exposure.ExposureSetting.TICK_BORNE;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum ExposureProtectiveMeasure {

	WEARING_MASK(EnumSet.of(AIR_BORNE), EnumSet.of(INDOOR, OUTDOOR)),
	DISTANCE_1_5M(EnumSet.of(AIR_BORNE), EnumSet.of(INDOOR, OUTDOOR)),

	FACE_TO_FACE_15MIN(EnumSet.of(AIR_BORNE), EnumSet.of(INDOOR)),
	VENTILATION_HEPA(EnumSet.of(AIR_BORNE), EnumSet.of(INDOOR)),

	HAND_HYGIENE(EnumSet.of(DIRECT_CONTACT), EnumSet.of(PERSON_TO_PERSON)),
	AVOID_TOUCHING_FACE(EnumSet.of(DIRECT_CONTACT), EnumSet.of(PERSON_TO_PERSON)),
	SAFE_SEX(EnumSet.of(DIRECT_CONTACT), EnumSet.of(PERSON_TO_PERSON)),

	WEARING_PPE(EnumSet.of(DIRECT_CONTACT, ANIMAL_CONTACT, FOMITE_TRANSMISSION), EnumSet.of(PERSON_TO_PERSON)),
	VACCINATION(EnumSet.of(ANIMAL_CONTACT, FOMITE_TRANSMISSION, VERTICAL_TRANSMISSION), EnumSet.noneOf(ExposureSetting.class)),

	HAND_WASHING(EnumSet.of(ANIMAL_CONTACT, FOMITE_TRANSMISSION), EnumSet.noneOf(ExposureSetting.class)),
	WOUND_WASHING(EnumSet.of(ANIMAL_CONTACT), EnumSet.noneOf(ExposureSetting.class)),

	INSECT_REPELLENT(EnumSet.of(VECTOR_BORNE), EnumSet.of(OUTDOOR)),
	HERBS(EnumSet.of(VECTOR_BORNE), EnumSet.of(OUTDOOR)),
	COILS(EnumSet.of(VECTOR_BORNE), EnumSet.of(OUTDOOR)),

	SLEEPING_UNDER_BEDNET(EnumSet.of(VECTOR_BORNE), EnumSet.of(INDOOR)),
	INDOOR_SPRAYING(EnumSet.of(VECTOR_BORNE), EnumSet.of(INDOOR)),
	AIR_CONDITION(EnumSet.of(VECTOR_BORNE), EnumSet.of(INDOOR)),

	PROTECTIVE_CLOTHING(EnumSet.of(VECTOR_BORNE), EnumSet.of(MOSQUITO_BORNE, TICK_BORNE)),

	ENVIRONMENTAL_CONTROL(EnumSet.of(VECTOR_BORNE), EnumSet.of(MOSQUITO_BORNE)),

	TICK_PREVENTION(EnumSet.of(VECTOR_BORNE), EnumSet.of(TICK_BORNE)),
	REGULAR_CHECKS(EnumSet.of(VECTOR_BORNE), EnumSet.of(TICK_BORNE)),

	WATER_PURIFICATION(EnumSet.of(WATER_BORNE), EnumSet.of(DRINKING_WATER)),
	SAFE_WATER_SOURCES(EnumSet.of(WATER_BORNE), EnumSet.of(DRINKING_WATER)),
	AVOID_RAW_FOODS(EnumSet.of(WATER_BORNE), EnumSet.of(DRINKING_WATER)),

	NO_SWIMMING_CONTAMINATED(EnumSet.of(WATER_BORNE), EnumSet.of(RECREATIONAL_WATER)),
	SHOWER_AFTER_SWIMMING(EnumSet.of(WATER_BORNE), EnumSet.of(RECREATIONAL_WATER)),
	WOUND_COVERAGE(EnumSet.of(WATER_BORNE), EnumSet.of(RECREATIONAL_WATER)),
	BATHING_CONTAMINATED_WATER(EnumSet.of(WATER_BORNE), EnumSet.of(RECREATIONAL_WATER)),

	WELL_COOKED(EnumSet.of(FOOD_BORNE), EnumSet.noneOf(ExposureSetting.class)),
	COLD_HOT_CHAIN(EnumSet.of(FOOD_BORNE), EnumSet.noneOf(ExposureSetting.class)),
	WASHED(EnumSet.of(FOOD_BORNE), EnumSet.noneOf(ExposureSetting.class)),

	MEDICATION(EnumSet.of(VERTICAL_TRANSMISSION), EnumSet.noneOf(ExposureSetting.class)),
	C_SECTION(EnumSet.of(VERTICAL_TRANSMISSION), EnumSet.noneOf(ExposureSetting.class)),

	OTHER(EnumSet.noneOf(ExposureCategory.class), EnumSet.noneOf(ExposureSetting.class));

	private final Set<ExposureCategory> categories;
	private final Set<ExposureSetting> settings;

	ExposureProtectiveMeasure(Set<ExposureCategory> categories, Set<ExposureSetting> settings) {
		this.categories = categories;
		this.settings = settings;
	}

	public Set<ExposureCategory> getCategories() {
		return categories;
	}

	public Set<ExposureSetting> getSettings() {
		return settings;
	}

	public static List<ExposureProtectiveMeasure> getValues(ExposureCategory category, ExposureSetting setting) {
		if (category == null) {
			return Collections.emptyList();
		}
		return Arrays.stream(values()).filter(pm -> {
			if (pm.categories.isEmpty()) {
				return true;
			}
			if (!pm.categories.contains(category)) {
				return false;
			}
			return pm.settings.isEmpty() || pm.settings.contains(setting);
		}).collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
