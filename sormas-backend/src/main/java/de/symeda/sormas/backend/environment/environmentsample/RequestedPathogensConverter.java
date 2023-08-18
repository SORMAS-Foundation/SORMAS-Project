/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.environment.environmentsample;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.AttributeConverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.environment.environmentsample.Pathogen;
import de.symeda.sormas.backend.disease.PathogenConverter;

public class RequestedPathogensConverter implements AttributeConverter<Set<Pathogen>, String> {

	private final PathogenConverter pathogenConverter = new PathogenConverter();
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(Set<Pathogen> pathogens) {
		if (pathogens == null) {
			return null;
		}

		Set<String> pathogenValues = pathogens.stream().map(pathogenConverter::convertToDatabaseColumn).collect(Collectors.toSet());
		try {
			return objectMapper.writeValueAsString(pathogenValues);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Can't create JSON from the set of pathogens [" + String.join(", ", pathogenValues) + "]", e);
		}
	}

	@Override
	public Set<Pathogen> convertToEntityAttribute(String pathogensJson) {
		if (pathogensJson == null) {
			return null;
		}

		try {
			return Stream.of(objectMapper.readValue(pathogensJson, String[].class))
				.map(pathogenConverter::convertToEntityAttribute)
				.collect(Collectors.toSet());
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Can't parse set of pathogens '" + pathogensJson + "'", e);
		}
	}
}
