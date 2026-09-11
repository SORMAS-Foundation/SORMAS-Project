/*******************************************************************************
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
 *******************************************************************************/
package de.symeda.sormas.api.utils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.symptoms.SymptomState;

/**
 * Utility helper to detect whether DTOs contain user-defined values.
 * <p>
 * Only properties declared directly on the provided DTO class are inspected;
 * inherited properties are ignored by design.
 * </p>
 */
public final class DtoUserDefinedValuesHelper {

	private DtoUserDefinedValuesHelper() {
		// Utility class
	}

	public static boolean hasAnyUserDefinedValues(Object dto, Class<?> dtoClass) {
		return hasAnyUserDefinedValues(dto, dtoClass, Collections.emptySet());
	}

	public static boolean hasAnyUserDefinedValues(Object dto, Class<?> dtoClass, Collection<String> excludedPropertyNames) {
		return hasAnyUserDefinedValues(dto, dtoClass, excludedPropertyNames, false);
	}

	public static boolean hasAnyUserDefinedValuesIgnoringUnknown(Object dto, Class<?> dtoClass) {
		return hasAnyUserDefinedValuesIgnoringUnknown(dto, dtoClass, Collections.emptySet());
	}

	public static boolean hasAnyUserDefinedValuesIgnoringUnknown(Object dto, Class<?> dtoClass, Collection<String> excludedPropertyNames) {
		return hasAnyUserDefinedValues(dto, dtoClass, excludedPropertyNames, true);
	}

	private static boolean hasAnyUserDefinedValues(
		Object dto,
		Class<?> dtoClass,
		Collection<String> excludedPropertyNames,
		boolean ignoreUnknownValue) {
		if (dto == null) {
			return false;
		}

		Map<String, Class<?>> dtoFieldTypes = new HashMap<>();
		for (Field field : dtoClass.getDeclaredFields()) {
			dtoFieldTypes.put(field.getName(), field.getType());
		}

		Set<String> dtoFieldNames = dtoFieldTypes.keySet();
		Set<String> exclusions = excludedPropertyNames == null ? Collections.emptySet() : Set.copyOf(excludedPropertyNames);

		try {
			PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(dtoClass, Object.class).getPropertyDescriptors();
			for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
				String propertyName = propertyDescriptor.getName();
				if (propertyDescriptor.getReadMethod() == null || !dtoFieldNames.contains(propertyName) || exclusions.contains(propertyName)) {
					continue;
				}

				Object value = propertyDescriptor.getReadMethod().invoke(dto);
				if (hasAnyUserDefinedValue(value, dtoFieldTypes.get(propertyName), ignoreUnknownValue)) {
					return true;
				}
			}
		} catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
			throw new RuntimeException("Exception when checking user-defined values for " + dtoClass.getSimpleName() + ": " + e.getMessage(), e);
		}

		return false;
	}

	private static boolean hasAnyUserDefinedValue(Object value, Class<?> fieldType, boolean ignoreUnknownValue) {
		if (value == null) {
			return false;
		}

		if (ignoreUnknownValue && isUnknownValue(value)) {
			return false;
		}

		if (value instanceof String) {
			return StringUtils.isNotBlank((String) value);
		}

		if (value instanceof Collection<?>) {
			return !((Collection<?>) value).isEmpty();
		}

		if (value.getClass().isArray()) {
			return Array.getLength(value) > 0;
		}

		if (fieldType.isPrimitive()) {
			return !isPrimitiveDefaultValue(value, fieldType);
		}

		return true;
	}

	private static boolean isUnknownValue(Object value) {
		return value == YesNoUnknown.UNKNOWN || value == SymptomState.UNKNOWN;
	}

	private static boolean isPrimitiveDefaultValue(Object value, Class<?> fieldType) {
		if (boolean.class.equals(fieldType)) {
			return !((Boolean) value);
		}
		if (byte.class.equals(fieldType)) {
			return ((Byte) value) == 0;
		}
		if (short.class.equals(fieldType)) {
			return ((Short) value) == 0;
		}
		if (int.class.equals(fieldType)) {
			return ((Integer) value) == 0;
		}
		if (long.class.equals(fieldType)) {
			return ((Long) value) == 0L;
		}
		if (float.class.equals(fieldType)) {
			return ((Float) value) == 0f;
		}
		if (double.class.equals(fieldType)) {
			return ((Double) value) == 0d;
		}
		if (char.class.equals(fieldType)) {
			return ((Character) value) == '\u0000';
		}

		return false;
	}
}
