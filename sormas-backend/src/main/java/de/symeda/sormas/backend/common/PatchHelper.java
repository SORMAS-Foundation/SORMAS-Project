package de.symeda.sormas.backend.common;

import java.lang.reflect.Field;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.minidev.json.JSONObject;

public class PatchHelper {

	public static <T> T postUpdate(JSONObject jsonObject, Class<T> patchObjectClass, T existingObject) {
		ObjectMapper mapper = new ObjectMapper();

		try {
			T patchObject = mapper.readValue(jsonObject.toString(), patchObjectClass);

			Set<String> attributes = jsonObject.keySet();

			attributes.forEach(attribute -> {

				Field existingField = null;
				try {
					existingField = existingObject.getClass().getDeclaredField(attribute);

					Field updateField = patchObject.getClass().getDeclaredField(attribute);
					existingField.setAccessible(true);
					updateField.setAccessible(true);
					existingField.set(existingObject, updateField.get(patchObject));
					existingField.setAccessible(false);
					updateField.setAccessible(false);
				} catch (NoSuchFieldException | IllegalAccessException e) {
					e.printStackTrace();
					Logger logger = LoggerFactory.getLogger(patchObject.getClass());
					logger.error(
						String.format(
							"Failed to patch " + patchObject.getClass().getSimpleName() + " attribute name " + attribute + ". Error: "
								+ e.getMessage()));
				}

			});

			return existingObject;

		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
