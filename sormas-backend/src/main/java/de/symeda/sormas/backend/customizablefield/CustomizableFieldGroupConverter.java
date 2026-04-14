/*
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
 */

package de.symeda.sormas.backend.customizablefield;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import de.symeda.sormas.api.customizablefield.CustomizableFieldGroup;

@Converter(autoApply = false)
public class CustomizableFieldGroupConverter implements AttributeConverter<CustomizableFieldGroup, String> {

	@Override
	public String convertToDatabaseColumn(CustomizableFieldGroup attribute) {
		return attribute != null ? attribute.getKey() : null;
	}

	@Override
	public CustomizableFieldGroup convertToEntityAttribute(String dbData) {
		return dbData != null ? CustomizableFieldGroup.fromKey(dbData) : null;
	}
}
