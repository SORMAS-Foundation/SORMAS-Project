/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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
package de.symeda.sormas.backend.epidata;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.AttributeConverter;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.exposure.InfectionSource;

/**
 * JPA {@link AttributeConverter} that persists a {@link Set} of {@link InfectionSource} values
 * as a single comma-separated {@link String} database column, and reconstructs the set when the
 * entity is loaded.
 *
 * <p>
 * This allows a multi-select infection source field (e.g. rendered as checkboxes in the UI) to be
 * stored in a single text column instead of a separate join table.
 * </p>
 */
public class InfectionSourceSetConverter implements AttributeConverter<Set<InfectionSource>, String> {

	/**
	 * Joins the given infection sources into a comma-separated string for storage.
	 *
	 * @param infectionSources
	 *            the infection sources to persist, or {@code null}
	 * @return a comma-separated string of {@link InfectionSource} names, or {@code null} if
	 *         {@code infectionSources} is {@code null}
	 */
	@Override
	public String convertToDatabaseColumn(Set<InfectionSource> infectionSources) {
		return infectionSources != null
			? String.join(",", infectionSources.stream().map(InfectionSource::name).collect(Collectors.toSet()))
			: null;
	}

	/**
	 * Splits the given comma-separated database value back into a set of infection sources.
	 *
	 * @param infectionSourcesText
	 *            a comma-separated string of {@link InfectionSource} names, or {@code null}
	 * @return the parsed set of {@link InfectionSource} values, or {@code null} if
	 *         {@code infectionSourcesText} is {@code null}
	 */
	@Override
	public Set<InfectionSource> convertToEntityAttribute(String infectionSourcesText) {
		return infectionSourcesText != null
			? Stream.of(StringUtils.split(infectionSourcesText, ",")).map(InfectionSource::valueOf).collect(Collectors.toSet())
			: null;
	}
}
