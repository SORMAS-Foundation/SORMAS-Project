package de.symeda.sormas.backend.externalmessage.survey;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashMap;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.symeda.sormas.api.externalmessage.survey.PatchDictionary;
import de.symeda.sormas.api.externalmessage.survey.PatchField;
import de.symeda.sormas.backend.AbstractUnitTest;
import de.symeda.sormas.backend.json.ObjectMapperProvider;

class PatchDictionarySerializationTest extends AbstractUnitTest {

	@Test
	void test_serialization_deserialization_works() throws JsonProcessingException {
		// PREPARE
		LinkedHashMap<PatchField, Object> dictionary = new LinkedHashMap<>();
		dictionary.put(PatchField.of("fieldNameWithoutGroup"), 5);
		dictionary.put(PatchField.of("fieldNameWithGroup", 2), "toeiwjew");
		PatchDictionary initial = new PatchDictionary().setDictionary(dictionary);

		// EXECUTE
		String json = ObjectMapperProvider.writeValueAsStringFailSafe(initial);

		PatchDictionary actual = ObjectMapperProvider.getInstance().readValue(json, PatchDictionary.class);

		// CHECK
		assertEquals(initial, actual);
	}
}
