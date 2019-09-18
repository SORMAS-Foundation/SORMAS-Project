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
import java.util.List;
import java.util.UUID;

import com.auth0.jwt.internal.com.fasterxml.jackson.databind.JsonMappingException.Reference;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.utils.OutdatedEntityException;
import de.symeda.sormas.backend.common.AbstractDomainObject;

public final class DtoHelper {

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

	@SuppressWarnings("unchecked")
	public static <T extends EntityDto> T mergeDto(T lead, T other, boolean cloning) {

		try {
			PropertyDescriptor[] pds = Introspector.getBeanInfo(lead.getClass(), EntityDto.class)
					.getPropertyDescriptors();

			for (PropertyDescriptor pd : pds) {
				// Skip properties without a read or write method
				if (pd.getReadMethod() == null || pd.getWriteMethod() == null) {
					continue;
				}

				Object leadProperty = pd.getReadMethod().invoke(lead);
				Object otherProperty = pd.getReadMethod().invoke(other);

				// Write other-property into lead-property, if lead-property is null or cloning
				// is active
				if (leadProperty == null
						|| (List.class.isAssignableFrom(pd.getPropertyType()) && ((List<?>) leadProperty).isEmpty())
						|| cloning && !ReferenceDto.class.isAssignableFrom(pd.getPropertyType())) {

					if (List.class.isAssignableFrom(pd.getPropertyType())) {

						for (EntityDto entry : (List<EntityDto>) otherProperty) {
							entry.setUuid(UUID.randomUUID().toString());
							pd.getWriteMethod().invoke(lead, otherProperty);
						}
					} else if (EntityDto.class.isAssignableFrom(pd.getPropertyType())) {

						pd.getWriteMethod().invoke(lead,
								mergeDto((EntityDto) leadProperty, (EntityDto) otherProperty, cloning));

					} else {

						pd.getWriteMethod().invoke(lead, otherProperty);
					}

				} else if (EntityDto.class.isAssignableFrom(pd.getPropertyType())) {

					pd.getWriteMethod().invoke(lead,
							mergeDto((EntityDto) leadProperty, (EntityDto) otherProperty, cloning));

				}
			}
		} catch (IntrospectionException | InvocationTargetException |

				IllegalAccessException e) {
			throw new RuntimeException("Exception when trying to merge or clone dto: " + e.getMessage(), e.getCause());
		}

		return lead;
	}
}
