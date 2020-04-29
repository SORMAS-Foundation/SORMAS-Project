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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
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

	public static void validateDto(EntityDto dto, AbstractDomainObject entity) {
		if (entity.getChangeDate() != null && (dto.getChangeDate() == null
				|| dto.getChangeDate().getTime() + CHANGE_DATE_TOLERANCE_MS < entity.getChangeDate().getTime())) {
			throw new OutdatedEntityException(dto.getUuid(), dto.getClass());
		}
	}

	public static void fillDto(EntityDto dto, AbstractDomainObject entity) {
		dto.setCreationDate(entity.getCreationDate());
		dto.setChangeDate(entity.getChangeDate());
		dto.setUuid(entity.getUuid());
	}

	/**
	 * @param overrideValues Note: Existing references are NOT overridden
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends EntityDto> void fillDto(T target, T source, boolean overrideValues) {

		try {
			PropertyDescriptor[] pds = Introspector.getBeanInfo(target.getClass(), EntityDto.class).getPropertyDescriptors();

			for (PropertyDescriptor pd : pds) {
				// Skip properties without a read or write method
				if (pd.getReadMethod() == null || pd.getWriteMethod() == null) {
					continue;
				}

				Object targetValue = pd.getReadMethod().invoke(target);
				Object sourceValue = pd.getReadMethod().invoke(source);
				
				if (EntityDto.class.isAssignableFrom(pd.getPropertyType())) {
					
					if (targetValue == null) {
						targetValue = sourceValue.getClass().newInstance();
						pd.getWriteMethod().invoke(target, targetValue);
					}
					
					// entity: just fill the existing one with the source
					fillDto((EntityDto) targetValue, (EntityDto) sourceValue, overrideValues);
				}
				else {
					boolean targetIsEmpty = targetValue == null 
							|| (Collection.class.isAssignableFrom(pd.getPropertyType()) && ((Collection<?>) targetValue).isEmpty());
					boolean override = overrideValues && !ReferenceDto.class.isAssignableFrom(pd.getPropertyType());
					// should we write into the target property?
					if (targetIsEmpty || override) {
						
						if (Collection.class.isAssignableFrom(pd.getPropertyType()) && sourceValue != null) {
							
							if (targetValue == null) {
								targetValue = sourceValue.getClass().newInstance();
								pd.getWriteMethod().invoke(target, targetValue);
							}

							Collection targetCollection = (Collection)targetValue;
							targetCollection.clear();
							
							for (Object sourceEntry : (Collection) sourceValue) {
								
								if (sourceEntry instanceof EntityDto) {
									EntityDto newEntry = ((EntityDto)sourceEntry).clone();
									newEntry.setUuid(DataHelper.createUuid());
									newEntry.setCreationDate(null);
									fillDto(newEntry, (EntityDto)sourceEntry, true);
									targetCollection.add(newEntry);
								} else if (DataHelper.isValueType(sourceEntry.getClass())
										|| sourceEntry instanceof ReferenceDto) {
									targetCollection.add(sourceEntry);
								} else {
				                    throw new UnsupportedOperationException(pd.getPropertyType().getName() + " is not supported as a list entry type.");
								}
							}
							
						} else if (DataHelper.isValueType(pd.getPropertyType())
								|| ReferenceDto.class.isAssignableFrom(pd.getPropertyType())) {

							pd.getWriteMethod().invoke(target, sourceValue);
							
						} else {

							// Other objects are not supported
		                    throw new UnsupportedOperationException(pd.getPropertyType().getName() + " is not supported as a property type.");
		                }
					}
				}
			}
		} catch (IntrospectionException | InvocationTargetException | IllegalAccessException | CloneNotSupportedException | InstantiationException e) {
			throw new RuntimeException("Exception when trying to fill dto: " + e.getMessage(), e.getCause());
		}
	}
}
