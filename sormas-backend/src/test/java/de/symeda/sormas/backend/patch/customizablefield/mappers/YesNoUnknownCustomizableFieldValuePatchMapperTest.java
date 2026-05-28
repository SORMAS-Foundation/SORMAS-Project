package de.symeda.sormas.backend.patch.customizablefield.mappers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import de.symeda.sormas.api.customizablefield.CustomizableFieldType;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.AbstractUnitTest;
import de.symeda.sormas.backend.patch.customizablefield.CustomizableFieldValuePatchRequest;
import de.symeda.sormas.backend.patch.mapping.impl.valuemapper.EnumPatchMapper;

class YesNoUnknownCustomizableFieldValuePatchMapperTest extends AbstractUnitTest {

	@Spy
	private EnumPatchMapper enumPatchMapper = new EnumPatchMapper();

	@InjectMocks
	private YesNoUnknownCustomizableFieldValuePatchMapper victim;

	@Test
	void getSupportedTypes_containsYesNoUnknown() {
		assertEquals(Set.of(CustomizableFieldType.YES_NO_UNKNOWN), victim.getSupportedTypes());
	}

	@Test
	void map_yes_setsYesOnDto() {
		CustomizableFieldValueDto dto = mapAndGetDto("YES");
		assertEquals(YesNoUnknown.YES, dto.getValueAsYesNoUnknown());
	}

	@Test
	void map_no_setsNoOnDto() {
		CustomizableFieldValueDto dto = mapAndGetDto("NO");
		assertEquals(YesNoUnknown.NO, dto.getValueAsYesNoUnknown());
	}

	@Test
	void map_unknown_setsUnknownOnDto() {
		CustomizableFieldValueDto dto = mapAndGetDto("UNKNOWN");
		assertEquals(YesNoUnknown.UNKNOWN, dto.getValueAsYesNoUnknown());
	}

	@Test
	void map_caseInsensitive_setsValue() {
		CustomizableFieldValueDto dto = mapAndGetDto("yes");
		assertEquals(YesNoUnknown.YES, dto.getValueAsYesNoUnknown());
	}

	@Test
	void map_unrecognisedValue_returnsFailure() {
		ValueMappingResult<CustomizableFieldValueDto> result = victim.map(buildRequest("MAYBE", new CustomizableFieldValueDto()));
		assertEquals(DataPatchFailureCause.NOT_PRESENT_IN_REFERENCE_DATA_LIST, result.getDataPatchFailureCause());
	}

	@Test
	void map_returnsSameDtoInstanceFromRequest() {
		CustomizableFieldValueDto dto = new CustomizableFieldValueDto();
		ValueMappingResult<CustomizableFieldValueDto> result = victim.map(buildRequest("YES", dto));

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
			.setTargetType(CustomizableFieldType.YES_NO_UNKNOWN)
			.setCustomizableFieldValueDto(dto);
	}
}
