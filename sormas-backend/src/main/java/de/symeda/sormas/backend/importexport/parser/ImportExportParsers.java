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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import de.symeda.sormas.api.importexport.ImportErrorException;

public abstract class ImportExportParsers<T> {

	private final Map<T, Parser<?>> parsers;

	public ImportExportParsers(Map<T, Parser<?>> parsers) {
		this.parsers = parsers;
	}

	protected Set<T> types() {
		return parsers.keySet();
	}

	protected Parser<?> get(T type) {
		return parsers.get(type);
	}

	abstract Optional<? extends Parser<?>> getParser(T type);

	abstract boolean hasParser(T type);

	public static class Builder<T, C extends ImportExportParsers<T>> {

		private final Map<T, Parser<?>> parsers;
		private final Class<C> type;

		private Builder(Class<C> type) {
			parsers = new HashMap<>();
			this.type = type;
		}

		public static <T, C extends ImportExportParsers<T>> Builder<T, C> of(Class<C> type) {
			return new Builder<>(type);
		}

		public <V> Builder<T, C> withParser(T type, Parser<V> parser) {
			parsers.put(type, parser);

			return this;
		}

		public C build() {
			try {
				return type.getConstructor(Map.class).newInstance(parsers);
			} catch (Exception e) {
				throw new RuntimeException("Unable to create parser of type [" + type.getSimpleName() + "]");
			}
		}
	}

	public interface Parser<T> {

		T parse(String value, Class<T> valueType, String propertyPath) throws ImportErrorException;
	}
}
