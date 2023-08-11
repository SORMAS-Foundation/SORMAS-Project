package de.symeda.sormas.app.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import de.symeda.sormas.api.environment.WaterUse;

public class WaterUseSerializer implements JsonDeserializer<WaterUse>, JsonSerializer<WaterUse> {
	@Override
	public WaterUse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return WaterUse.valueOf(json.getAsString());
	}

	@Override
	public JsonElement serialize(WaterUse src, Type typeOfSrc, JsonSerializationContext context) {
		return context.serialize(src.name());
	}
}
