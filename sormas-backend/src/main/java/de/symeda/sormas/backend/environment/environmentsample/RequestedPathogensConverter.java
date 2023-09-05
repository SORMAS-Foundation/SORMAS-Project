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

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.environment.environmentsample.Pathogen;
import de.symeda.sormas.backend.disease.PathogenConverter;

public class RequestedPathogensConverter implements AttributeConverter<Set<Pathogen>, String> {

	private final PathogenConverter pathogenConverter = new PathogenConverter();

	@Override
	public String convertToDatabaseColumn(Set<Pathogen> pathogens) {
		return pathogens != null
			? String.join(",", pathogens.stream().map(pathogenConverter::convertToDatabaseColumn).collect(Collectors.toSet()))
			: null;
	}

	@Override
	public Set<Pathogen> convertToEntityAttribute(String pathogensText) {
		return pathogensText != null
			? Stream.of(StringUtils.split(pathogensText, ",")).map(pathogenConverter::convertToEntityAttribute).collect(Collectors.toSet())
			: null;
	}
}
