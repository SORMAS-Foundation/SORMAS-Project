/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.rest;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import de.symeda.sormas.api.utils.LegacyEnumHelper;
import de.symeda.sormas.api.utils.LegacyEnumNames;

/**
 * Teaches Gson the {@link LegacyEnumNames} aliases that {@code @JsonCreator} gives Jackson on the server, for every
 * enum that declares them. Without this a server that has not yet run the enum migration sends a retired name and
 * Gson silently yields {@code null}.
 */
public class LegacyEnumTypeAdapterFactory implements TypeAdapterFactory {

	@Override
	@SuppressWarnings({
		"unchecked",
		"rawtypes" })
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {

		Class<?> rawType = type.getRawType();
		if (!Enum.class.isAssignableFrom(rawType) || rawType == Enum.class) {
			return null;
		}
		// a constant with a body is an anonymous subclass, the enum itself is its superclass
		if (!rawType.isEnum()) {
			rawType = rawType.getSuperclass();
		}

		Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>) rawType;
		if (!LegacyEnumHelper.hasLegacyNames(enumType)) {
			return null;
		}

		return (TypeAdapter<T>) new TypeAdapter<Enum>() {

			@Override
			public void write(JsonWriter out, Enum value) throws IOException {
				out.value(value == null ? null : value.name());
			}

			@Override
			public Enum read(JsonReader in) throws IOException {
				if (in.peek() == JsonToken.NULL) {
					in.nextNull();
					return null;
				}
				// a name from a newer peer degrades to null, as Gson's built-in enum adapter does
				return LegacyEnumHelper.resolveOrNull((Class) enumType, in.nextString());
			}
		};
	}
}
