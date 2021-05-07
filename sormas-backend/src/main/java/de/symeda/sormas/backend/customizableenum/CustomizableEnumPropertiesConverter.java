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

import java.io.IOException;
import java.util.Map;

import javax.persistence.AttributeConverter;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JPA Converter that converts a JSON String stored in the database to a map with additional enum property names as its keys and the
 * associated values as its values, and vice versa. The content of this map can then be stored in an instance of
 * {@link de.symeda.sormas.api.customizableenum.CustomizableEnum} and subsequently accessed in the user interface to control logic that is
 * specific to the respective enum type.
 */
public class CustomizableEnumPropertiesConverter implements AttributeConverter<Map<String, Object>, String> {

	@Override
	public String convertToDatabaseColumn(Map<String, Object> enumProperties) {

		if (MapUtils.isEmpty(enumProperties)) {
			return null;
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(enumProperties);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Content of enumProperties could not be parsed to JSON String");
		}
	}

	@Override
	public Map<String, Object> convertToEntityAttribute(String enumPropertiesJson) {

		if (StringUtils.isBlank(enumPropertiesJson)) {
			return null;
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(enumPropertiesJson, new TypeReference<Map<String, Object>>() {
			});
		} catch (IOException e) {
			throw new RuntimeException("Content of enumPropertiesJson could not be parsed to Map<String, Object>: " + enumPropertiesJson);
		}
	}
}
