package de.symeda.sormas.api.externalmessage.survey;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

public class PatchFieldKeyDeserializer extends KeyDeserializer {

	@Override
	public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
		return PatchFieldDeserializer.parse(key, message -> ctxt.weirdKeyException(PatchField.class, key, message));
	}
}
