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
import java.util.Arrays;
import java.util.List;

import javax.persistence.AttributeConverter;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.customizableenum.CustomizableEnumTranslation;

/**
 * JPA Converter that converts a JSON String stored in the database to a list of {@link CustomizableEnumTranslation} which stores the
 * caption of an individual enum value for a specific language code, and vice versa.
 */
public class CustomizableEnumTranslationConverter implements AttributeConverter<List<CustomizableEnumTranslation>, String> {

	@Override
	public String convertToDatabaseColumn(List<CustomizableEnumTranslation> customizableEnumTranslations) {

		if (CollectionUtils.isEmpty(customizableEnumTranslations)) {
			return null;
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(customizableEnumTranslations);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Content of enumTranslations could not be parsed to JSON String");
		}
	}

	@Override
	public List<CustomizableEnumTranslation> convertToEntityAttribute(String enumTranslationsJson) {

		if (StringUtils.isBlank(enumTranslationsJson)) {
			return null;
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			return Arrays.asList(mapper.readValue(enumTranslationsJson, CustomizableEnumTranslation[].class));
		} catch (IOException e) {
			throw new RuntimeException("Content of enumTranslationsJson could not be parsed to List<EnumTranslation>: " + enumTranslationsJson);
		}
	}
}
