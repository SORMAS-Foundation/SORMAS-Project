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

public enum ExposureContactFactor {

	DURATION_OF_EXPOSURE(ExposureCategory.AIR_BORNE, ExposureSetting.INDOOR),
	PROXIMITY_TO_SOURCE(ExposureCategory.AIR_BORNE, ExposureSetting.INDOOR),
	TYPE_OF_ACTIVITY(ExposureCategory.AIR_BORNE, ExposureSetting.INDOOR),
	POOR_VENTILATION(ExposureCategory.AIR_BORNE, ExposureSetting.INDOOR),

	PROXIMITY_AND_DURATION(ExposureCategory.AIR_BORNE, ExposureSetting.OUTDOOR),
	WIND_AND_AIRFLOW(ExposureCategory.AIR_BORNE, ExposureSetting.OUTDOOR),
	DENSITY_OF_PEOPLE(ExposureCategory.AIR_BORNE, ExposureSetting.OUTDOOR),

	SKIN_CONTACT(ExposureCategory.DIRECT_CONTACT, ExposureSetting.PERSON_TO_PERSON),
	BODY_FLUIDS(ExposureCategory.DIRECT_CONTACT, ExposureSetting.PERSON_TO_PERSON),

	BUTCHERING(ExposureCategory.ANIMAL_CONTACT, null),
	COOKING(ExposureCategory.ANIMAL_CONTACT, null),
	TOUCHING_CONTACT_WITH_FLUIDS(ExposureCategory.ANIMAL_CONTACT, null),
	SCRATCHES_BITES_LICKING(ExposureCategory.ANIMAL_CONTACT, null),

	SHARED_SURFACES(ExposureCategory.FOMITE_TRANSMISSION, null),

	OUTDOOR_ACTIVITIES(ExposureCategory.VECTOR_BORNE, ExposureSetting.OUTDOOR),
	STANDING_WATER_PROXIMITY(ExposureCategory.VECTOR_BORNE, ExposureSetting.OUTDOOR),
	HIGH_MOSQUITO_ACTIVITY_REGIONS(ExposureCategory.VECTOR_BORNE, ExposureSetting.OUTDOOR),

	UNPROTECTED_HOUSEHOLD(ExposureCategory.VECTOR_BORNE, ExposureSetting.INDOOR),

	MOSQUITO_ACTIVITY_TIME_OF_DAY(ExposureCategory.VECTOR_BORNE, ExposureSetting.MOSQUITO_BORNE),
	CLOTHING_COVERAGE(ExposureCategory.VECTOR_BORNE, ExposureSetting.MOSQUITO_BORNE),

	DURATION_OUTDOORS(ExposureCategory.VECTOR_BORNE, ExposureSetting.TICK_BORNE),
	EXPOSED_SKIN(ExposureCategory.VECTOR_BORNE, ExposureSetting.TICK_BORNE),

	DRINKING_CONTAMINATED_WATER(ExposureCategory.WATER_BORNE, ExposureSetting.DRINKING_WATER),
	ICE_AND_FOOD_PREPARATION(ExposureCategory.WATER_BORNE, ExposureSetting.DRINKING_WATER),

	SWALLOWING_WATER(ExposureCategory.WATER_BORNE, ExposureSetting.RECREATIONAL_WATER),
	CONTACT_WITH_OPEN_WOUNDS(ExposureCategory.WATER_BORNE, ExposureSetting.RECREATIONAL_WATER),

	EGG(ExposureCategory.FOOD_BORNE, null),
	MEAT(ExposureCategory.FOOD_BORNE, null),
	FISH_SEAFOOD(ExposureCategory.FOOD_BORNE, null),
	DAIRY(ExposureCategory.FOOD_BORNE, null),
	FRUIT(ExposureCategory.FOOD_BORNE, null),
	RAW_VEGETABLES(ExposureCategory.FOOD_BORNE, null),

	UNKNOWN(null, null),
	OTHER(null, null);

	private final ExposureCategory category;
	private final ExposureSetting setting;

	ExposureContactFactor(ExposureCategory category, ExposureSetting setting) {
		this.category = category;
		this.setting = setting;
	}

	public ExposureCategory getCategory() {
		return category;
	}

	public ExposureSetting getSetting() {
		return setting;
	}

	/**
	 * Returns contact factors for a given category and setting combination.
	 * <ul>
	 * <li>Factors with matching category AND setting are included.</li>
	 * <li>Factors with matching category and null setting (category-wide) are included.</li>
	 * <li>UNKNOWN and OTHER (null category) are always included.</li>
	 * </ul>
	 *
	 * Because INDOOR/OUTDOOR are shared settings, the category parameter
	 * ensures (AIR_BORNE, INDOOR) and (VECTOR_BORNE, INDOOR) return different factors.
	 */
	public static List<ExposureContactFactor> getValues(ExposureCategory category, ExposureSetting setting) {
		if (category == null) {
			return Collections.emptyList();
		}
		return Arrays.stream(values()).filter(cf -> {
			if (cf.category == null) {
				return true; // UNKNOWN, OTHER
			}
			if (cf.category != category) {
				return false;
			}
			return cf.setting == null || cf.setting == setting;
		}).collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
