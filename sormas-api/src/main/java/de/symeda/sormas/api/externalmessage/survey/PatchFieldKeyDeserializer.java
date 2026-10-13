package de.symeda.sormas.api.externalmessage.survey;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

public class PatchFieldKeyDeserializer extends KeyDeserializer {

	@Override
	public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
		if (key == null || key.isBlank()) {
			return null;
		}

		int atIndex = key.lastIndexOf('@');
		if (atIndex < 0) {
			return PatchField.of(key);
		}

		String field = key.substring(0, atIndex);
		String indexPart = key.substring(atIndex + 1);

		if (field.isBlank()) {
			throw ctxt.weirdKeyException(PatchField.class, key, "Field part is empty");
		}

		try {
			Integer groupIndex = Integer.valueOf(indexPart);
			return PatchField.of(field, groupIndex);
		} catch (NumberFormatException e) {
			throw ctxt.weirdKeyException(PatchField.class, key, String.format("groupIndex must be an integer, was. [%s]", indexPart));
		}
	}
}
