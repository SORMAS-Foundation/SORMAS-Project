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

package de.symeda.sormas.backend.exposure;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.AttributeConverter;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.exposure.ExposureCategory;

public class ExposureCategorySetConverter implements AttributeConverter<Set<ExposureCategory>, String> {

	@Override
	public String convertToDatabaseColumn(Set<ExposureCategory> exposureCategories) {
		return exposureCategories != null
			? String.join(",", exposureCategories.stream().map(ExposureCategory::name).collect(Collectors.toSet()))
			: null;
	}

	@Override
	public Set<ExposureCategory> convertToEntityAttribute(String exposureCategorysText) {
		return exposureCategorysText != null
			? Stream.of(StringUtils.split(exposureCategorysText, ",")).map(ExposureCategory::valueOf).collect(Collectors.toSet())
			: null;
	}
}
