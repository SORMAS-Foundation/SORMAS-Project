/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.Maps;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.JsonDataEntry;
import de.symeda.sormas.api.utils.OutdatedEntityException;
import de.symeda.sormas.backend.common.AbstractDomainObject;

public final class DtoHelper {

	private DtoHelper() {
		// Hide Utility Class Constructor
	}

	/**
	 * some inaccuracy is ok, because of the rest conversion
	 */
	public static final int CHANGE_DATE_TOLERANCE_MS = 1000;

	public static void validateDto(EntityDto dto, AbstractDomainObject entity, boolean checkChangeDate) {

		if (checkChangeDate
			&& entity.getChangeDate() != null
			&& (dto.getChangeDate() == null || dto.getChangeDate().getTime() + CHANGE_DATE_TOLERANCE_MS < entity.getChangeDate().getTime())) {
			throw new OutdatedEntityException(dto.getUuid(), dto.getClass(), dto.getChangeDate(), entity.getChangeDate());
		}
	}

	public static void fillDto(EntityDto dto, AbstractDomainObject entity) {
		dto.setCreationDate(entity.getCreationDate());
		dto.setChangeDate(entity.getChangeDate());
		dto.setUuid(entity.getUuid());
	}

	/**
	 * @param overrideValues
	 *            Note: Existing references are NOT overridden
	 */
	@SuppressWarnings({
		"unchecked",
		"rawtypes" })
	public static <T extends EntityDto> T copyDtoValues(T target, T source, boolean overrideValues) {

		try {
			PropertyDescriptor[] pds = Introspector.getBeanInfo(target.getClass(), EntityDto.class).getPropertyDescriptors();

			for (PropertyDescriptor pd : pds) {
				// Skip properties without a read or write method
				if (pd.getReadMethod() == null || pd.getWriteMethod() == null) {
					continue;
				}

				Object targetValue = pd.getReadMethod().invoke(target);
				Object sourceValue = pd.getReadMethod().invoke(source);

				if (sourceValue == null) {
					continue;
				}

				if (EntityDto.class.isAssignableFrom(pd.getPropertyType())) {

					if (targetValue == null) {
						targetValue = sourceValue.getClass().newInstance();
						pd.getWriteMethod().invoke(target, targetValue);
					}

					// If both entities have the same UUID, assign a new one to targetValue to create a new entity
					if (((EntityDto) targetValue).getUuid().equals(((EntityDto) sourceValue).getUuid())) {
						((EntityDto) targetValue).setUuid(DataHelper.createUuid());
					}

					// entity: just fill the existing one with the source
					copyDtoValues((EntityDto) targetValue, (EntityDto) sourceValue, overrideValues);
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
								throw new UnsupportedOperationException(pd.getPropertyType().getName() + " is not supported as a list entry type.");
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
					} else if (targetValue == null || override) {
						if (DataHelper.isValueType(pd.getPropertyType()) || ReferenceDto.class.isAssignableFrom(pd.getPropertyType())) {
							pd.getWriteMethod().invoke(target, sourceValue);
						} else {
							// Other objects are not supported
							throw new UnsupportedOperationException(pd.getPropertyType().getName() + " is not supported as a property type.");
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

	// todo this really should return void. Taking a target/receiver argument and then return it anyways does not make sense
	// FIXME(#6880)
	public static <T extends AbstractDomainObject> T fillOrBuildEntity(EntityDto source, T target, Supplier<T> newEntity, boolean checkChangeDate) {
		if (target == null) {
			target = newEntity.get();

			String uuid = source.getUuid() != null ? source.getUuid() : DataHelper.createUuid();
			target.setUuid(uuid);

			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}

		DtoHelper.validateDto(source, target, checkChangeDate);

		return target;
	}
}
