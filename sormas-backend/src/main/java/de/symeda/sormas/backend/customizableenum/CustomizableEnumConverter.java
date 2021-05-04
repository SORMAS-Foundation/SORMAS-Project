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

import de.symeda.sormas.api.customizableenum.CustomizableEnum;
import de.symeda.sormas.api.customizableenum.CustomizableEnumFacade;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;

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
