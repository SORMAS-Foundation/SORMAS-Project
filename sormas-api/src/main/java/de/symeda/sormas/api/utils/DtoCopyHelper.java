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

package de.symeda.sormas.api.utils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;

public final class DtoCopyHelper {

	private DtoCopyHelper() {
	}

	public static <T extends EntityDto> T copyDtoValues(T target, T source, boolean overrideValues) {
		return DtoCopyHelper.copyDtoValues(target, source, overrideValues, null);
	}

	/**
	 * @param overrideValues
	 *            Note: Existing references are NOT overridden
	 */
	@SuppressWarnings({
		"unchecked",
		"rawtypes" })
	public static <T extends EntityDto> T copyDtoValues(T target, T source, boolean overrideValues, String... skippedFields) {

		try {
			PropertyDescriptor[] pds = Introspector.getBeanInfo(target.getClass(), EntityDto.class).getPropertyDescriptors();

			for (PropertyDescriptor pd : pds) {
				// Skip properties without a read or write method
				if (pd.getReadMethod() == null || pd.getWriteMethod() == null || pd.getWriteMethod().isAnnotationPresent(JsonIgnore.class)) {
					continue;
				}

				Object targetValue = pd.getReadMethod().invoke(target);
				Object sourceValue = pd.getReadMethod().invoke(source);

				if (sourceValue == null) {
					continue;
				}

				if (skippedFields == null || Arrays.stream(skippedFields).noneMatch(field -> field.equals(pd.getName()))) {
					if (EntityDto.class.isAssignableFrom(pd.getPropertyType())) {

						if (targetValue == null) {
							targetValue = sourceValue.getClass().newInstance();
							pd.getWriteMethod().invoke(target, targetValue);
						}

						// If both entities have the same UUID, assign a new one to targetValue to create a new entity
						if (DataHelper.equal(((EntityDto) targetValue).getUuid(), (((EntityDto) sourceValue).getUuid()))) {
							((EntityDto) targetValue).setUuid(DataHelper.createUuid());
						}

						// entity: just fill the existing one with the source
						DtoCopyHelper.copyDtoValues((EntityDto) targetValue, (EntityDto) sourceValue, overrideValues);
					} else {
						boolean override = overrideValues && !ReferenceDto.class.isAssignableFrom(pd.getPropertyType());
						// should we write into the target property?
						if (Collection.class.isAssignableFrom(pd.getPropertyType())) {

							if (targetValue == null) {
								targetValue = sourceValue.getClass().newInstance();
								pd.getWriteMethod().invoke(target, targetValue);
							}

							Collection targetCollection = (Collection) targetValue;

							for (Object sourceEntry : (Collection) sourceValue) {
								if (sourceEntry instanceof EntityDto) {
									EntityDto newEntry = ((EntityDto) sourceEntry).clone();
									newEntry.setUuid(DataHelper.createUuid());
									newEntry.setCreationDate(null);
									copyDtoValues(newEntry, (EntityDto) sourceEntry, true);
									targetCollection.add(newEntry);
								} else if (DataHelper.isValueType(sourceEntry.getClass())
									|| sourceEntry instanceof ReferenceDto
									|| sourceEntry instanceof JsonDataEntry) {
									targetCollection.add(sourceEntry);
								} else {
									throw new UnsupportedOperationException(
										pd.getPropertyType().getName() + " is not supported as a list entry type.");
								}
							}
						} else if (Map.class.isAssignableFrom(pd.getPropertyType())) {

							if (targetValue == null) {
								if (sourceValue.getClass() == EnumMap.class) {
									// Enum map needs to be initialized with the content of the source map because it does not have an init method
									targetValue = Maps.newEnumMap((EnumMap) sourceValue);
									((EnumMap) targetValue).clear();
								} else {
									targetValue = sourceValue.getClass().newInstance();
								}
								pd.getWriteMethod().invoke(target, targetValue);
							}

							Map targetMap = (Map) targetValue;

							for (Object sourceKey : ((Map) sourceValue).keySet()) {
								if (override || !targetMap.containsKey(sourceKey)) {
									targetMap.put(sourceKey, ((Map) sourceValue).get(sourceKey));
								}
							}
						} else if (targetValue == null
							|| override
							|| (pd.getPropertyType().equals(String.class)
								&& StringUtils.isBlank((String) targetValue)
								&& StringUtils.isNotBlank((String) sourceValue))
							|| (pd.getPropertyType().equals(boolean.class) && ((boolean) sourceValue) && !((boolean) targetValue))) {
							if (DataHelper.isValueType(pd.getPropertyType()) || ReferenceDto.class.isAssignableFrom(pd.getPropertyType())) {
								pd.getWriteMethod().invoke(target, sourceValue);
							} else {
								// Other objects are not supported
								throw new UnsupportedOperationException(pd.getPropertyType().getName() + " is not supported as a property type.");
							}
						}
					}
				}
			}
		} catch (IntrospectionException
			| InvocationTargetException
			| IllegalAccessException
			| CloneNotSupportedException
			| InstantiationException e) {
			throw new RuntimeException("Exception when trying to fill dto: " + e.getMessage(), e.getCause());
		}
		return target;
	}
}
