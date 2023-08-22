package de.symeda.sormas.app.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class EnumMapKeySerializer<T extends Enum<T>> implements JsonDeserializer<T>, JsonSerializer<T> {
	private final Class<T> enumType;

	public EnumMapKeySerializer(Class<T> enumType) {
		this.enumType = enumType;
	}

	@Override
	public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return Enum.valueOf(enumType, json.getAsString());
	}

	@Override
	public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
		return context.serialize(src.name());
	}
}
