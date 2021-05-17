/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.disease;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.AttributeConverter;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.Disease;

/**
 * JPA Converter that converts a comma-separated String stored in the database to a list of diseases, and vice versa. The String needs to
 * consist of enum names contained in {@link Disease}, separated by commas without blank spaces.
 */
public class DiseaseListConverter implements AttributeConverter<List<Disease>, String> {

	@Override
	public String convertToDatabaseColumn(List<Disease> diseases) {
		if (CollectionUtils.isEmpty(diseases)) {
			return null;
		}

		return diseases.stream().map(Disease::getName).collect(Collectors.joining(","));
	}

	@Override
	public List<Disease> convertToEntityAttribute(String diseasesString) {
		if (StringUtils.isBlank(diseasesString)) {
			return null;
		}

		try {
			return Stream.of(diseasesString.split(",")).map(Disease::valueOf).collect(Collectors.toList());
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(
				"Content of diseaseString is not a valid list of diseases, or one of the diseases in the String does not match a valid disease: "
					+ diseasesString);
		}
	}
}
