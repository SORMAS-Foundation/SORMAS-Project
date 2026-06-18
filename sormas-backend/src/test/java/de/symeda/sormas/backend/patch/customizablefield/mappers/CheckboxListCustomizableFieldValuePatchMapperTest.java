package de.symeda.sormas.backend.patch.customizablefield.mappers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.customizablefield.CustomizableFieldType;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.backend.AbstractUnitTest;
import de.symeda.sormas.backend.patch.customizablefield.CustomizableFieldValuePatchRequest;

class CheckboxListCustomizableFieldValuePatchMapperTest extends AbstractUnitTest {

	private final CheckboxListCustomizableFieldValuePatchMapper victim = new CheckboxListCustomizableFieldValuePatchMapper();

	@Test
	void getSupportedTypes_containsCheckboxList() {
		assertEquals(Set.of(CustomizableFieldType.CHECKBOX_LIST), victim.getSupportedTypes());
	}

	@Test
	void map_commaSeparatedValues_producesCorrectSet() {
		CustomizableFieldValueDto dto = mapAndGetDto("apple,peach,cherry");
		assertEquals(Set.of("apple", "peach", "cherry"), dto.getValueAsStringSet());
	}

	@Test
	void map_trimsWhitespaceAroundCommas() {
		CustomizableFieldValueDto dto = mapAndGetDto(" apple , peach , cherry ");
		assertEquals(Set.of("apple", "peach", "cherry"), dto.getValueAsStringSet());
	}

	@Test
	void map_singleValue_producesSingletonSet() {
		CustomizableFieldValueDto dto = mapAndGetDto("apple");
		assertEquals(Set.of("apple"), dto.getValueAsStringSet());
	}

	@Test
	void map_emptySegmentsAreIgnored() {
		CustomizableFieldValueDto dto = mapAndGetDto("apple,,cherry");
		assertEquals(Set.of("apple", "cherry"), dto.getValueAsStringSet());
	}

	@Test
	void map_returnsSameDtoInstanceFromRequest() {
		CustomizableFieldValueDto dto = new CustomizableFieldValueDto();
		CustomizableFieldValuePatchRequest request = buildRequest("apple,peach", dto);

		ValueMappingResult<CustomizableFieldValueDto> result = victim.map(request);

		assertNull(result.getDataPatchFailureCause());
		assertSame(dto, result.getData());
	}

	// --- Helpers ---

	private CustomizableFieldValueDto mapAndGetDto(String value) {
		CustomizableFieldValueDto dto = new CustomizableFieldValueDto();
		victim.map(buildRequest(value, dto));
		return dto;
	}

	private static CustomizableFieldValuePatchRequest buildRequest(String value, CustomizableFieldValueDto dto) {
		return new CustomizableFieldValuePatchRequest().setValue(value)
			.setTargetType(CustomizableFieldType.CHECKBOX_LIST)
			.setCustomizableFieldValueDto(dto);
	}
}
