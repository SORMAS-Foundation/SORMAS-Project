package de.symeda.sormas.api.externalmessage.survey;

import java.io.IOException;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

public class PatchFieldDeserializer extends JsonDeserializer<PatchField> {

	@Override
	public PatchField deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
		if (!parser.hasToken(JsonToken.VALUE_STRING)) {
			return (PatchField) ctxt.handleUnexpectedToken(PatchField.class, parser);
		}

		String value = parser.getText();
		return parse(value, message -> ctxt.weirdStringException(value, PatchField.class, message));
	}

	static PatchField parse(String value, Function<String, JsonMappingException> invalidValueException) throws JsonMappingException {
		if (value == null || value.isBlank()) {
			return null;
		}

		int atIndex = value.lastIndexOf('@');
		if (atIndex < 0) {
			return PatchField.of(value);
		}

		String field = value.substring(0, atIndex);
		String indexPart = value.substring(atIndex + 1);
		if (field.isBlank()) {
			throw invalidValueException.apply("Field part is empty");
		}

		try {
			return PatchField.of(field, Integer.valueOf(indexPart));
		} catch (NumberFormatException e) {
			throw invalidValueException.apply(String.format("groupIndex must be an integer, was. [%s]", indexPart));
		}
	}
}
