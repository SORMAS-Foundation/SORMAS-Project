package de.symeda.sormas.backend.patch.customizablefield.mappers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import de.symeda.sormas.api.customizablefield.CustomizableFieldType;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.backend.AbstractUnitTest;
import de.symeda.sormas.backend.patch.customizablefield.CustomizableFieldValuePatchRequest;
import de.symeda.sormas.backend.patch.mapping.impl.valuemapper.PrimitivePatchMapper;

class PrimitiveCustomizableFieldValuePatchMapperTest extends AbstractUnitTest {

	@Spy
	private PrimitivePatchMapper primitivePatchMapper = new PrimitivePatchMapper();

	@InjectMocks
	private PrimitiveCustomizableFieldValuePatchMapper victim;

	@Test
	void getSupportedTypes_containsAllExpectedTypes() {
		assertEquals(
			Set.of(
				CustomizableFieldType.TEXT,
				CustomizableFieldType.TEXTAREA,
				CustomizableFieldType.NUMBER,
				CustomizableFieldType.DECIMAL,
				CustomizableFieldType.COMBOBOX,
				CustomizableFieldType.CHECKBOX,
				CustomizableFieldType.RADIO_BUTTON_LIST),
			victim.getSupportedTypes());
	}

	// --- String-backed types ---

	@Test
	void map_text_setsStringValue() {
		// EXECUTE & CHECK
		CustomizableFieldValueDto dto = mapAndGetDto("hello", CustomizableFieldType.TEXT);
		assertEquals("hello", dto.getValue());
	}

	@Test
	void map_textarea_setsStringValue() {
		// EXECUTE & CHECK
		CustomizableFieldValueDto dto = mapAndGetDto("some text", CustomizableFieldType.TEXTAREA);
		assertEquals("some text", dto.getValue());
	}

	@Test
	void map_number_setsStringValue() {
		// EXECUTE & CHECK
		CustomizableFieldValueDto dto = mapAndGetDto("42", CustomizableFieldType.NUMBER);
		assertEquals("42", dto.getValue());
	}

	@Test
	void map_decimal_setsStringValue() {
		// EXECUTE & CHECK
		CustomizableFieldValueDto dto = mapAndGetDto("3.14", CustomizableFieldType.DECIMAL);
		assertEquals("3.14", dto.getValue());
	}

	@Test
	void map_combobox_setsStringValue() {
		// EXECUTE & CHECK
		CustomizableFieldValueDto dto = mapAndGetDto("OPTION_A", CustomizableFieldType.COMBOBOX);
		assertEquals("OPTION_A", dto.getValue());
	}

	@Test
	void map_radioButtonList_setsStringValue() {
		// EXECUTE & CHECK
		CustomizableFieldValueDto dto = mapAndGetDto("OPTION_B", CustomizableFieldType.RADIO_BUTTON_LIST);
		assertEquals("OPTION_B", dto.getValue());
	}

	// --- Boolean-backed type ---

	@Test
	void map_checkbox_true_setsBooleanValue() {
		// EXECUTE & CHECK
		CustomizableFieldValueDto dto = mapAndGetDto("true", CustomizableFieldType.CHECKBOX);
		assertTrue(dto.getValueAsBoolean());
	}

	@Test
	void map_checkbox_false_setsBooleanValue() {
		// EXECUTE & CHECK
		CustomizableFieldValueDto dto = mapAndGetDto("false", CustomizableFieldType.CHECKBOX);
		assertFalse(dto.getValueAsBoolean());
	}

	// --- Cross-cutting behaviour ---

	@Test
	void map_text_trimsWhitespace() {
		// EXECUTE & CHECK
		CustomizableFieldValueDto dto = mapAndGetDto("  hello  ", CustomizableFieldType.TEXT);
		assertEquals("hello", dto.getValue());
	}

	@Test
	void map_returnsSameDtoInstanceFromRequest() {
		// PREPARE
		CustomizableFieldValueDto dto = new CustomizableFieldValueDto();
		CustomizableFieldValuePatchRequest request = buildRequest("value", CustomizableFieldType.TEXT, dto);

		// EXECUTE
		ValueMappingResult<CustomizableFieldValueDto> result = victim.map(request);

		// CHECK
		assertNull(result.getDataPatchFailureCause());
		assertSame(dto, result.getData());
	}

	// --- Helpers ---

	private CustomizableFieldValueDto mapAndGetDto(Object value, CustomizableFieldType type) {
		CustomizableFieldValueDto dto = new CustomizableFieldValueDto();
		victim.map(buildRequest(value, type, dto));
		return dto;
	}

	private static CustomizableFieldValuePatchRequest buildRequest(Object value, CustomizableFieldType type, CustomizableFieldValueDto dto) {
		return new CustomizableFieldValuePatchRequest().setValue(value).setTargetType(type).setCustomizableFieldValueDto(dto);
	}
}
