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

public enum ExposureContactFactor {

	@Deprecated
	DURATION_OF_EXPOSURE(ExposureCategory.AIR_BORNE, true, ExposureSetting.INDOOR),

	@Deprecated
	PROXIMITY_TO_SOURCE(ExposureCategory.AIR_BORNE, true, ExposureSetting.INDOOR),

	@Deprecated
	TYPE_OF_ACTIVITY(ExposureCategory.AIR_BORNE, true, ExposureSetting.INDOOR),

	@Deprecated
	POOR_VENTILATION(ExposureCategory.AIR_BORNE, true, ExposureSetting.INDOOR),

	@Deprecated
	PROXIMITY_AND_DURATION(ExposureCategory.AIR_BORNE, true, ExposureSetting.OUTDOOR),

	@Deprecated
	WIND_AND_AIRFLOW(ExposureCategory.AIR_BORNE, true, ExposureSetting.OUTDOOR),

	@Deprecated
	DENSITY_OF_PEOPLE(ExposureCategory.AIR_BORNE, true, ExposureSetting.OUTDOOR),

	CLOSE_CONTACT(ExposureCategory.RESPIRATORY),
	PROLONGED_CONTACT(ExposureCategory.RESPIRATORY),
	SKIN_TO_SKIN_CONTACT(ExposureCategory.RESPIRATORY),
	CONTACT_WITH_SECRETIONS_BODY_FLUIDS(ExposureCategory.RESPIRATORY),
	SHARED_ACCOMMODATION_ACTIVITIES(ExposureCategory.RESPIRATORY),
	SKIN_CONTACT(ExposureCategory.PERSON_TO_PERSON),
	BODY_FLUIDS(ExposureCategory.PERSON_TO_PERSON),
	CLOSE_OR_PROLONGED_CONTACT(ExposureCategory.PERSON_TO_PERSON),
	SHARED_ACCOMMODATION_ACTIVITIES_PERSON(ExposureCategory.PERSON_TO_PERSON),

	BUTCHERING(ExposureCategory.ANIMAL_CONTACT),
	COOKING(ExposureCategory.ANIMAL_CONTACT),
	TOUCHING_CONTACT_WITH_FLUIDS(ExposureCategory.ANIMAL_CONTACT),
	SCRATCHES_BITES_LICKING(ExposureCategory.ANIMAL_CONTACT),
	HANDLING(ExposureCategory.ANIMAL_CONTACT),
	FEEDING(ExposureCategory.ANIMAL_CONTACT),
	CLEANING_PROVIDING_CARE(ExposureCategory.ANIMAL_CONTACT),

	SHARED_SURFACES(ExposureCategory.FOMITE_TRANSMISSION),

	@Deprecated
	OUTDOOR_ACTIVITIES(ExposureCategory.VECTOR_BORNE, true, ExposureSetting.OUTDOOR),

	STANDING_WATER_PROXIMITY(ExposureCategory.VECTOR_BORNE),

	@Deprecated
	HIGH_MOSQUITO_ACTIVITY_REGIONS(ExposureCategory.VECTOR_BORNE, true, ExposureSetting.OUTDOOR),

	@Deprecated
	UNPROTECTED_HOUSEHOLD(ExposureCategory.VECTOR_BORNE, true, ExposureSetting.INDOOR),

	@Deprecated
	MOSQUITO_ACTIVITY_TIME_OF_DAY(ExposureCategory.VECTOR_BORNE, true, ExposureSetting.MOSQUITO_BORNE),

	@Deprecated
	CLOTHING_COVERAGE(ExposureCategory.VECTOR_BORNE, true, ExposureSetting.MOSQUITO_BORNE),

	@Deprecated
	DURATION_OUTDOORS(ExposureCategory.VECTOR_BORNE, true, ExposureSetting.TICK_BORNE),

	@Deprecated
	EXPOSED_SKIN(ExposureCategory.VECTOR_BORNE, true, ExposureSetting.TICK_BORNE),

	REPEATED_BITES(ExposureCategory.VECTOR_BORNE),
	INDOOR_EXPOSURE(ExposureCategory.VECTOR_BORNE),
	OUTDOOR_EXPOSURE(ExposureCategory.VECTOR_BORNE),
	NIGHT_TIME_EXPOSURE(ExposureCategory.VECTOR_BORNE),
	VEGETATION(ExposureCategory.VECTOR_BORNE),
	PROLONGED_FREQUENT_EXPOSURE(ExposureCategory.VECTOR_BORNE),
	AREA_HIGH_RISK_TRANSMISSION(ExposureCategory.VECTOR_BORNE),

	DRINKING_CONTAMINATED_WATER(ExposureCategory.WATER_BORNE, ExposureSetting.DRINKING_WATER),
	ICE_AND_FOOD_PREPARATION(ExposureCategory.WATER_BORNE, ExposureSetting.DRINKING_WATER),

	@Deprecated

	SWALLOWING_WATER(ExposureCategory.WATER_BORNE, true, ExposureSetting.RECREATIONAL_WATER),
	CONTACT_WITH_OPEN_WOUNDS(ExposureCategory.WATER_BORNE,
		ExposureSetting.RECREATIONAL_WATER,
		ExposureSetting.HOUSEHOLD,
		ExposureSetting.OCCUPATIONAL_ENVIRONMENTAL),
	CONTACT_WITH_WASTEWATER(ExposureCategory.WATER_BORNE,
		ExposureSetting.RECREATIONAL_WATER,
		ExposureSetting.HOUSEHOLD,
		ExposureSetting.OCCUPATIONAL_ENVIRONMENTAL),
	CONTACT_WITH_FLOODWATER(ExposureCategory.WATER_BORNE,
		ExposureSetting.RECREATIONAL_WATER,
		ExposureSetting.HOUSEHOLD,
		ExposureSetting.OCCUPATIONAL_ENVIRONMENTAL),
	IMMERSION(ExposureCategory.WATER_BORNE,
		ExposureSetting.RECREATIONAL_WATER,
		ExposureSetting.HOUSEHOLD,
		ExposureSetting.OCCUPATIONAL_ENVIRONMENTAL),
	ACCIDENTAL_INGESTION(ExposureCategory.WATER_BORNE,
		ExposureSetting.RECREATIONAL_WATER,
		ExposureSetting.HOUSEHOLD,
		ExposureSetting.OCCUPATIONAL_ENVIRONMENTAL),
	SWIMMING_BATHING(ExposureCategory.WATER_BORNE,
		ExposureSetting.RECREATIONAL_WATER,
		ExposureSetting.HOUSEHOLD,
		ExposureSetting.OCCUPATIONAL_ENVIRONMENTAL),
	WATER_SPORTS(ExposureCategory.WATER_BORNE, ExposureSetting.RECREATIONAL_WATER, ExposureSetting.OCCUPATIONAL_ENVIRONMENTAL),

	@Deprecated
	EGG(ExposureCategory.FOOD_BORNE, true),

	@Deprecated
	MEAT(ExposureCategory.FOOD_BORNE, true),

	@Deprecated
	FISH_SEAFOOD(ExposureCategory.FOOD_BORNE, true),

	@Deprecated
	DAIRY(ExposureCategory.FOOD_BORNE, true),

	@Deprecated
	FRUIT(ExposureCategory.FOOD_BORNE, true),

	@Deprecated
	RAW_VEGETABLES(ExposureCategory.FOOD_BORNE, true),

	CONSUMED(ExposureCategory.FOOD_BORNE),
	TASTED(ExposureCategory.FOOD_BORNE),
	HANDLED(ExposureCategory.FOOD_BORNE),
	PREPARED(ExposureCategory.FOOD_BORNE),
	SHARED_MEAL(ExposureCategory.FOOD_BORNE),
	RAW_UNDERCOOKED_CONSUMPTION(ExposureCategory.FOOD_BORNE),
	INADEQUATE_STORAGE_TEMPERATURE(ExposureCategory.FOOD_BORNE),

	SKIN_CONTACT_ENV(ExposureCategory.ENVIRONMENTAL),
	INHALATION(ExposureCategory.ENVIRONMENTAL),
	OUTDOOR_EXPOSURE_ENV(ExposureCategory.ENVIRONMENTAL),
	WOUND_CONTAMINATION(ExposureCategory.ENVIRONMENTAL),
	HANDLING_ENV(ExposureCategory.ENVIRONMENTAL),
	OCCUPATIONAL_EXPOSURE_ENV(ExposureCategory.ENVIRONMENTAL),

	INJECTION(ExposureCategory.BLOOD_PARENTERAL),
	NEEDLESTICK(ExposureCategory.BLOOD_PARENTERAL),
	TRANSFUSION_EXPOSURE(ExposureCategory.BLOOD_PARENTERAL,
		ExposureSetting.TRANSFUSION,
		ExposureSetting.ORGAN_TISSUE_TRANSPLANTATION,
		ExposureSetting.INJECTION_DRUG_USE,
		ExposureSetting.HEALTHCARE_PROCEDURE),
	TRANSPLANTATION(ExposureCategory.BLOOD_PARENTERAL,
		ExposureSetting.ORGAN_TISSUE_TRANSPLANTATION,
		ExposureSetting.INJECTION_DRUG_USE,
		ExposureSetting.HEALTHCARE_PROCEDURE),
	SHARING_NEEDLES_SYRINGES(ExposureCategory.BLOOD_PARENTERAL,
		ExposureSetting.TRANSFUSION,
		ExposureSetting.ORGAN_TISSUE_TRANSPLANTATION,
		ExposureSetting.INJECTION_DRUG_USE,
		ExposureSetting.HEALTHCARE_PROCEDURE),
	INVASIVE_PROCEDURE(ExposureCategory.BLOOD_PARENTERAL,
		ExposureSetting.TRANSFUSION,
		ExposureSetting.ORGAN_TISSUE_TRANSPLANTATION,
		ExposureSetting.INJECTION_DRUG_USE,
		ExposureSetting.HEALTHCARE_PROCEDURE),
	PERCUTANEOUS_EXPOSURE(ExposureCategory.BLOOD_PARENTERAL,
		ExposureSetting.ORGAN_TISSUE_TRANSPLANTATION,
		ExposureSetting.INJECTION_DRUG_USE,
		ExposureSetting.HEALTHCARE_PROCEDURE,
		ExposureSetting.OCCUPATIONAL_EXPOSURE),
	MUCOSAL(ExposureCategory.BLOOD_PARENTERAL,
		ExposureSetting.ORGAN_TISSUE_TRANSPLANTATION,
		ExposureSetting.INJECTION_DRUG_USE,
		ExposureSetting.HEALTHCARE_PROCEDURE,
		ExposureSetting.OCCUPATIONAL_EXPOSURE),

	VAGINAL(ExposureCategory.SEXUAL),
	ANAL(ExposureCategory.SEXUAL),
	ORAL(ExposureCategory.SEXUAL),
	GENITAL_CONTACT(ExposureCategory.SEXUAL),
	SKIN_TO_SKIN(ExposureCategory.SEXUAL),
	MULTIPLE_PARTNERS(ExposureCategory.SEXUAL),
	ANONYMOUS_PARTNER(ExposureCategory.SEXUAL),
	TRANSACTIONAL_SEX(ExposureCategory.SEXUAL),
	BEING_A_SEX_WORKER(ExposureCategory.SEXUAL),
	CONTACT_WITH_SEX_WORKER(ExposureCategory.SEXUAL),

	UNKNOWN(null),
	OTHER(null);

	private final ExposureCategory category;
	private final Set<ExposureSetting> settings;
	private final boolean deprecated;

	ExposureContactFactor(ExposureCategory category, boolean deprecated, ExposureSetting... settings) {
		this.category = category;
		this.settings =
			Collections.unmodifiableSet(settings.length > 0 ? EnumSet.copyOf(Arrays.asList(settings)) : EnumSet.noneOf(ExposureSetting.class));
		this.deprecated = deprecated;
	}

	ExposureContactFactor(ExposureCategory category, ExposureSetting... settings) {
		this(category, false, settings);
	}

	public ExposureCategory getCategory() {
		return category;
	}

	public Set<ExposureSetting> getSettings() {
		return settings;
	}

	public static List<ExposureContactFactor> getValues(ExposureCategory category, ExposureSetting setting) {
		return getValues(category, setting, false);
	}

	/**
	 * Returns contact factors for a given category and setting combination.
	 * Deprecated factors are hidden by default.
	 *
	 */
	public static List<ExposureContactFactor> getValues(ExposureCategory category, ExposureSetting setting, boolean includeDeprecated) {
		if (category == null) {
			return Collections.emptyList();
		}
		if (!category.hasNoSetting() && setting == null) {
			return Collections.emptyList();
		}

		return Arrays.stream(values()).filter(cf -> {
			if (cf.category == null) {
				return true;
			}
			if (cf.category != category) {
				return false;
			}
			return matchesSetting(cf.settings, category, setting);
		}).filter(cf -> includeDeprecated || !cf.isDeprecated()).collect(Collectors.toList());
	}

	public static List<ExposureContactFactor> getValues(ExposureCategory category, ExposureSetting setting, Disease disease) {
		return getValues(category, setting, false, disease);
	}

	public static List<ExposureContactFactor> getValues(
		ExposureCategory category,
		ExposureSetting setting,
		boolean includeDeprecated,
		Disease disease) {
		return getValues(category, setting, includeDeprecated).stream().filter(cf -> isVisibleForDisease(cf, disease)).collect(Collectors.toList());
	}

	private static boolean matchesSetting(Set<ExposureSetting> factorSettings, ExposureCategory category, ExposureSetting selectedSetting) {
		if (factorSettings.isEmpty()) {
			if (category.hasNoSetting()) {
				return true;
			}
			return selectedSetting != ExposureSetting.OTHER && selectedSetting != ExposureSetting.UNKNOWN;
		}
		if (selectedSetting == null) {
			return false;
		}
		return factorSettings.contains(selectedSetting);
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	private static boolean isVisibleForDisease(ExposureContactFactor contactFactor, Disease disease) {
		return Diseases.DiseasesConfiguration.isDefinedOrMissing(ExposureContactFactor.class, contactFactor.name(), disease);
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
