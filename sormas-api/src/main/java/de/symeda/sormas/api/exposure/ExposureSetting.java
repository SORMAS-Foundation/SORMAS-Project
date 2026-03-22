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

import de.symeda.sormas.api.i18n.I18nProperties;

public enum ExposureSetting {

	INDOOR(ExposureCategory.AIR_BORNE, ExposureCategory.VECTOR_BORNE),
	OUTDOOR(ExposureCategory.AIR_BORNE, ExposureCategory.VECTOR_BORNE),

	PERSON_TO_PERSON(ExposureCategory.DIRECT_CONTACT),

	MOSQUITO_BORNE(ExposureCategory.VECTOR_BORNE),
	TICK_BORNE(ExposureCategory.VECTOR_BORNE),

	DRINKING_WATER(ExposureCategory.WATER_BORNE),
	RECREATIONAL_WATER(ExposureCategory.WATER_BORNE),

	PREGNANCY_OR_DELIVERY(ExposureCategory.VERTICAL_TRANSMISSION),

	OTHER(ExposureCategory.AIR_BORNE),
	UNKNOWN(ExposureCategory.AIR_BORNE);

	private final Set<ExposureCategory> categories;

	ExposureSetting(ExposureCategory... categories) {
		this.categories = categories.length > 0 ? EnumSet.copyOf(Arrays.asList(categories)) : EnumSet.noneOf(ExposureCategory.class);
	}

	public Set<ExposureCategory> getCategories() {
		return categories;
	}

	public static List<ExposureSetting> getValues(ExposureCategory category) {
		if (category == null) {
			return Collections.emptyList();
		}
		return Arrays.stream(values()).filter(s -> s.categories.contains(category)).collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
