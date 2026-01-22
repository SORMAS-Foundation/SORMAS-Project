package de.symeda.sormas.backend.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class ObjectMapperProvider {

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

	private static ObjectMapper createObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();

		mapper.registerModule(new JavaTimeModule());

		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		return mapper;
	}
}
