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

package de.symeda.sormas.backend.importexport.parser;

import java.util.Map;
import java.util.Optional;

public class PropertyTypeBasedParsers extends ImportExportParsers<Class<?>> {

	public PropertyTypeBasedParsers(Map<Class<?>, Parser<?>> parsers) {
		super(parsers);
	}

	public Optional<? extends Parser<?>> getParser(Class<?> type) {
		return types().stream().filter(c -> c.isAssignableFrom(type)).map(this::get).findFirst();
	}

	public boolean hasParser(Class<?> type) {
		return types().stream().anyMatch(c -> c.isAssignableFrom(type));
	}
}
