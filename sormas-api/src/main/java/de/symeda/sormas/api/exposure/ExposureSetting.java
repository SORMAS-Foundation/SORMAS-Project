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

public enum ExposureSetting {

	@Deprecated
	INDOOR(true, ExposureCategory.AIR_BORNE, ExposureCategory.VECTOR_BORNE),
	@Deprecated
	OUTDOOR(true, ExposureCategory.AIR_BORNE, ExposureCategory.VECTOR_BORNE),

	@Deprecated
	PERSON_TO_PERSON(true, ExposureCategory.DIRECT_CONTACT),
	@Deprecated
	OTHER_DIRECT_CONTACT(true, ExposureCategory.DIRECT_CONTACT),

	@Deprecated
	MOSQUITO_BORNE(true, ExposureCategory.VECTOR_BORNE),
	@Deprecated
	TICK_BORNE(true, ExposureCategory.VECTOR_BORNE),

	HOUSEHOLD_SHARED_ACCOMMODATION(ExposureCategory.RESPIRATORY, ExposureCategory.PERSON_TO_PERSON),
	SCHOOL_CHILDCARE(ExposureCategory.RESPIRATORY, ExposureCategory.PERSON_TO_PERSON),
	WORKPLACE(ExposureCategory.RESPIRATORY, ExposureCategory.PERSON_TO_PERSON, ExposureCategory.SEXUAL),
	GATHERING_EVENT(ExposureCategory.RESPIRATORY, ExposureCategory.PERSON_TO_PERSON),
	HEALTHCARE(ExposureCategory.RESPIRATORY),
	TRANSPORT(ExposureCategory.RESPIRATORY),
	HEALTHCARE_INSTITUTIONAL(ExposureCategory.PERSON_TO_PERSON),

	URBAN_RESIDENTIAL(ExposureCategory.VECTOR_BORNE),
	RURAL_AGRICULTURAL(ExposureCategory.VECTOR_BORNE),
	FOREST_NATURAL(ExposureCategory.VECTOR_BORNE),
	WORKING_AT_AN_AIRPORT(ExposureCategory.VECTOR_BORNE),

	DRINKING_WATER(ExposureCategory.WATER_BORNE),
	RECREATIONAL_WATER(ExposureCategory.WATER_BORNE),
	HOUSEHOLD(ExposureCategory.WATER_BORNE, ExposureCategory.FOOD_BORNE),
	OCCUPATIONAL_ENVIRONMENTAL(ExposureCategory.WATER_BORNE),
	EATING_OUTSIDE(ExposureCategory.FOOD_BORNE),
	HOTEL(ExposureCategory.FOOD_BORNE),
	CAMPING_SITE(ExposureCategory.FOOD_BORNE),
	RENTED_APARTMENT_HOUSE(ExposureCategory.FOOD_BORNE),
	FRIENDS_FAMILY(ExposureCategory.FOOD_BORNE),

	PREGNANCY_OR_DELIVERY(ExposureCategory.VERTICAL_TRANSMISSION),
	BREASTFEEDING(ExposureCategory.VERTICAL_TRANSMISSION),

	NATURAL_ENVIRONMENT(ExposureCategory.ENVIRONMENTAL),
	OCCUPATIONAL_ENVIRONMENT(ExposureCategory.ENVIRONMENTAL),
	CONTAMINATED_PREMISES(ExposureCategory.ENVIRONMENTAL),

	TRANSFUSION(ExposureCategory.BLOOD_PARENTERAL),
	ORGAN_TISSUE_TRANSPLANTATION(ExposureCategory.BLOOD_PARENTERAL),
	INJECTION_DRUG_USE(ExposureCategory.BLOOD_PARENTERAL),
	HEALTHCARE_PROCEDURE(ExposureCategory.BLOOD_PARENTERAL),
	OCCUPATIONAL_EXPOSURE(ExposureCategory.BLOOD_PARENTERAL),

	HOUSEHOLD_PRIVATE_RESIDENCE(ExposureCategory.SEXUAL),
	HOUSEHOLD_OTHER_COUNTRY(ExposureCategory.SEXUAL),
	GATHERING_SOCIAL_SETTING(ExposureCategory.SEXUAL),
	LARGE_EVENT_WITH_SEXUAL_CONTACT(ExposureCategory.SEXUAL),

	OTHER,
	UNKNOWN;

	private final Set<ExposureCategory> categories;

	private final boolean deprecated;

	ExposureSetting(boolean deprecated, ExposureCategory... categories) {
		this.deprecated = deprecated;
		this.categories = categories.length > 0 ? EnumSet.copyOf(Arrays.asList(categories)) : EnumSet.noneOf(ExposureCategory.class);
	}

	ExposureSetting(ExposureCategory... categories) {
		this(false, categories);
	}

	public Set<ExposureCategory> getCategories() {
		return categories;
	}

	public static List<ExposureSetting> getValues(ExposureCategory category) {
		return getValues(category, false);
	}

	public static List<ExposureSetting> getValues(ExposureCategory category, boolean includeDeprecated) {
		if (category == null) {
			return Collections.emptyList();
		}

		return Arrays.stream(values())
			.filter(s -> s == OTHER || s == UNKNOWN || s.categories.contains(category))
			.filter(s -> includeDeprecated || !s.isDeprecated())
			.collect(Collectors.toList());
	}

	public static List<ExposureSetting> getValues(ExposureCategory category, Disease disease) {
		return getValues(category, false, disease);
	}

	public static List<ExposureSetting> getValues(ExposureCategory category, boolean includeDeprecated, Disease disease) {
		return getValues(category, includeDeprecated).stream().filter(s -> isVisibleForDisease(s, disease)).collect(Collectors.toList());
	}

	public boolean isDeprecated() {
		return deprecated;
	}

	private static boolean isVisibleForDisease(ExposureSetting setting, Disease disease) {
		return Diseases.DiseasesConfiguration.isMissing(ExposureSetting.class, setting.name())
			|| Diseases.DiseasesConfiguration.isDefined(ExposureSetting.class, setting.name(), disease);
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
