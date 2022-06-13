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

package de.symeda.sormas.backend.customizableenum;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.AttributeConverter;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.customizableenum.CustomizableEnum;
import de.symeda.sormas.api.customizableenum.CustomizableEnumFacade;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;

/**
 * JPA Converter that converts a JSON String stored in the database to an instance of {@link CustomizableEnum} and vice versa.
 * Uses the cache built in {@link CustomizableEnumFacadeEjb} to retrieve the internationalized caption based on the user's language
 * as well as optional properties.
 * 
 * This class is supposed to be extended for every supported customizable enum. This allows using the specific enum type, alongside
 * its specific converter extension, in the entities.
 * 
 * @param <T>
 *            The specific extension of {@link CustomizableEnum} for type safety
 */
public abstract class CustomizableEnumConverter<T extends CustomizableEnum> implements AttributeConverter<T, String> {

	private final Class<T> enumClass;
	private CustomizableEnumFacade customizableEnumFacade;

	public CustomizableEnumConverter(Class<T> enumClass) {
		this.enumClass = enumClass;
	}

	@Override
	public String convertToDatabaseColumn(T enumValue) {
		return enumValue != null ? enumValue.getValue() : null;
	}

	@Override
	public T convertToEntityAttribute(String enumString) {
		if (StringUtils.isBlank(enumString)) {
			return null;
		}

		try {
			if (customizableEnumFacade == null) {
				customizableEnumFacade = (CustomizableEnumFacade) new InitialContext().lookup("java:module/CustomizableEnumFacade");
			}

			CustomizableEnumType enumType = CustomizableEnumType.getByEnumClass(enumClass);
			if (enumType == null) {
				throw new RuntimeException("No CustomizableEnumType for given enumClass " + enumClass + "found");
			}

			return customizableEnumFacade.getEnumValue(CustomizableEnumType.getByEnumClass(enumClass), enumString);
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}
}
