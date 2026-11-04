package de.symeda.sormas.backend.patch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.externalmessage.survey.PatchField;
import de.symeda.sormas.backend.AbstractUnitTest;
import de.symeda.sormas.backend.json.ObjectMapperProvider;

class PatchFieldSerializationTest extends AbstractUnitTest {

	@ParameterizedTest
	@ValueSource(strings = {
		"Person.firstName",
		"Person.firstName@0",
		"Person.firstName@12",
		"field@nested@2" })
	void deserializeSerializedValue(String value) throws Exception {
		// PREPARE
		ObjectMapper mapper = new ObjectMapper();
		int separator = value.lastIndexOf('@');
		PatchField expected =
			separator < 0 ? PatchField.of(value) : PatchField.of(value.substring(0, separator), Integer.valueOf(value.substring(separator + 1)));
		String json = mapper.writeValueAsString(expected);

		// EXECUTE
		PatchField result = mapper.readValue(json, PatchField.class);

		// CHECK
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"null",
		"\"\"",
		"\"   \"" })
	void deserializeEmptyValue(String json) throws Exception {
		// PREPARE
		ObjectMapper mapper = ObjectMapperProvider.getInstance();

		// EXECUTE
		PatchField result = mapper.readValue(json, PatchField.class);

		// CHECK
		assertNull(result);
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"field",
		"field@0",
		"field@12",
		"field@nested@2",
		"",
		"   " })
	void deserializeKeyMatchesValue(String value) throws Exception {
		// PREPARE
		ObjectMapper mapper = ObjectMapperProvider.getInstance();
		String json = mapper.writeValueAsString(value);

		// EXECUTE
		PatchField field = mapper.readValue(json, PatchField.class);
		Map<PatchField, Object> dictionary = mapper.readValue("{" + json + ":true}", new TypeReference<Map<PatchField, Object>>() {
		});

		// CHECK
		assertEquals(field, dictionary.keySet().iterator().next());
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"\"@1\"",
		"\"field@\"",
		"\"field@abc\"",
		"\"field@2147483648\"",
		"{}",
		"[]",
		"1",
		"true" })
	void deserializeInvalidValue(String json) {
		// PREPARE
		ObjectMapper mapper = ObjectMapperProvider.getInstance();

		// EXECUTE
		Exception result = assertThrows(JsonMappingException.class, () -> mapper.readValue(json, PatchField.class));

		// CHECK
		assertTrue(result.getMessage().contains(PatchField.class.getName()));
	}
}
