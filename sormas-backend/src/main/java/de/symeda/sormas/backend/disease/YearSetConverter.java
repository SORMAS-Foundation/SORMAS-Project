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

package de.symeda.sormas.backend.disease;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.persistence.AttributeConverter;

/**
 * Stores a set of years as sorted comma-separated text, for example "2019,2024". Entries that are not whole numbers are
 * dropped when reading.
 */
public class YearSetConverter implements AttributeConverter<Set<Integer>, String> {

	@Override
	public String convertToDatabaseColumn(Set<Integer> years) {

		if (years == null || years.isEmpty()) {
			return null;
		}
		return new TreeSet<>(years).stream().map(String::valueOf).collect(Collectors.joining(","));
	}

	@Override
	public Set<Integer> convertToEntityAttribute(String yearsString) {

		Set<Integer> years = new TreeSet<>();
		if (yearsString == null || yearsString.isBlank()) {
			return years;
		}
		Arrays.stream(yearsString.split(",")).map(String::trim).filter(s -> s.matches("\\d{4}")).map(Integer::valueOf).forEach(years::add);
		return years;
	}
}
