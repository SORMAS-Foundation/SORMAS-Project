package de.symeda.sormas.backend.patch.customizablefield.mappers;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import de.symeda.sormas.api.customizablefield.CustomizableFieldType;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.backend.AbstractUnitTest;
import de.symeda.sormas.backend.patch.customizablefield.CustomizableFieldValuePatchRequest;
import de.symeda.sormas.backend.patch.mapping.impl.valuemapper.DatePatchMapper;

class DateCustomizableFieldValuePatchMapperTest extends AbstractUnitTest {

	@Spy
	private DatePatchMapper datePatchMapper = new DatePatchMapper();

	@InjectMocks
	private DateCustomizableFieldValuePatchMapper victim;

	@Test
	void getSupportedTypes_containsDateAndDateTime() {
		assertEquals(Set.of(CustomizableFieldType.DATE, CustomizableFieldType.DATE_TIME), victim.getSupportedTypes());
	}

	// --- DATE ---

	@Test
	void map_date_parsesValueAndSetsOnDto() {
		// PREPARE
		CustomizableFieldValueDto dto = new CustomizableFieldValueDto();
		CustomizableFieldValuePatchRequest request = buildRequest("2024-06-15", CustomizableFieldType.DATE, dto);

		// EXECUTE
		ValueMappingResult<CustomizableFieldValueDto> result = victim.map(request);

		// CHECK
		assertNull(result.getDataPatchFailureCause());
		assertSame(dto, result.getData());
		assertEquals(LocalDate.of(2024, 6, 15), dto.getValueAsDate());
	}

	@Test
	void map_date_invalidString_returnsFailure() {
		// PREPARE
		CustomizableFieldValuePatchRequest request = buildRequest("not-a-date", CustomizableFieldType.DATE, new CustomizableFieldValueDto());

		// EXECUTE
		ValueMappingResult<CustomizableFieldValueDto> result = victim.map(request);

		// CHECK
		assertEquals(DataPatchFailureCause.INVALID_VALUE_TYPE, result.getDataPatchFailureCause());
	}

	// --- DATE_TIME ---

	@Test
	void map_dateTime_parsesValueAndSetsOnDto() {
		// PREPARE
		CustomizableFieldValueDto dto = new CustomizableFieldValueDto();
		CustomizableFieldValuePatchRequest request = buildRequest("2024-06-15T14:30:00", CustomizableFieldType.DATE_TIME, dto);

		// EXECUTE
		ValueMappingResult<CustomizableFieldValueDto> result = victim.map(request);

		// CHECK
		assertNull(result.getDataPatchFailureCause());
		assertSame(dto, result.getData());
		assertEquals(LocalDateTime.of(2024, 6, 15, 14, 30, 0), dto.getValueAsDateTime());
	}

	@Test
	void map_dateTime_invalidString_returnsFailure() {
		// PREPARE
		CustomizableFieldValuePatchRequest request = buildRequest("not-a-datetime", CustomizableFieldType.DATE_TIME, new CustomizableFieldValueDto());

		// EXECUTE
		ValueMappingResult<CustomizableFieldValueDto> result = victim.map(request);

		// CHECK
		assertEquals(DataPatchFailureCause.INVALID_VALUE_TYPE, result.getDataPatchFailureCause());
	}

	private static CustomizableFieldValuePatchRequest buildRequest(Object value, CustomizableFieldType type, CustomizableFieldValueDto dto) {
		return new CustomizableFieldValuePatchRequest().setValue(value).setTargetType(type).setCustomizableFieldValueDto(dto);
	}
}
