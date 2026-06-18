/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.backend.json;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import de.symeda.sormas.api.externalmessage.survey.PatchField;
import de.symeda.sormas.api.externalmessage.survey.PatchFieldKeyDeserializer;

/**
 * Permits unified access to a pre-configured {@link ObjectMapper} instance.
 */
public final class ObjectMapperProvider {

	private static final Logger logger = LoggerFactory.getLogger(ObjectMapperProvider.class);

	private static volatile ObjectMapper instance;

	private ObjectMapperProvider() {
		if (instance != null) {
			throw new IllegalStateException("ObjectMapper instance already created");
		}
	}

	public static ObjectMapper getInstance() {
		if (instance == null) {
			synchronized (ObjectMapperProvider.class) {
				if (instance == null) {
					instance = createObjectMapper();
				}
			}
		}
		return instance;
	}

	/**
	 * Produces JSON or swallows error and return null, meant for logging purposes, were null as fallback is not business-critical.
	 * 
	 * @param object
	 *            to be serialized as JSON
	 * @return JSON representation or null if exception.
	 */
	@Nullable
	public static String writeValueAsStringFailSafe(Object object) {
		try {
			return getInstance().writeValueAsString(object);
		} catch (JsonProcessingException e) {
			logger.error("Couldn't write JSON of: [{}] of type: [{}]", object, object.getClass(), e);
			return null;
		}
	}

	private static ObjectMapper createObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();

		mapper.registerModule(new JavaTimeModule());

		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addKeyDeserializer(PatchField.class, new PatchFieldKeyDeserializer());
		mapper.registerModule(simpleModule);

		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		return mapper;
	}
}
