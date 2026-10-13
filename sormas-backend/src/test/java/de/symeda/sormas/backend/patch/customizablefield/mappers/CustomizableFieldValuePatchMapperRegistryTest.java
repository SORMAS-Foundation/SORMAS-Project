package de.symeda.sormas.backend.patch.customizablefield.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.stream.Stream;

import javax.enterprise.inject.Instance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import de.symeda.sormas.api.customizablefield.CustomizableFieldType;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.backend.AbstractUnitTest;
import de.symeda.sormas.backend.patch.customizablefield.CustomizableFieldValuePatchRequest;

class CustomizableFieldValuePatchMapperRegistryTest extends AbstractUnitTest {

	@Mock
	private Instance<CustomizableFieldValuePatchMapper> instances;

	@Mock
	private CustomizableFieldValuePatchMapper mapperA;

	@Mock
	private CustomizableFieldValuePatchMapper mapperB;

	@InjectMocks
	private CustomizableFieldValuePatchMapperRegistry victim;

	@BeforeEach
	void setUp() {
		Mockito.lenient().when(instances.stream()).thenAnswer(ignored -> Stream.of(mapperA, mapperB));
		victim.init();
	}

	@Test
	void map_firstSupportingMapperUsed() {
		// PREPARE
		CustomizableFieldValuePatchRequest request = buildRequest("42", CustomizableFieldType.NUMBER);

		when(mapperA.supports(CustomizableFieldType.NUMBER)).thenReturn(false);
		when(mapperB.supports(CustomizableFieldType.NUMBER)).thenReturn(true);

		CustomizableFieldValueDto dto = new CustomizableFieldValueDto();
		ValueMappingResult<CustomizableFieldValueDto> expected = ValueMappingResult.withData(dto);
		when(mapperB.map(request)).thenReturn(expected);

		// EXECUTE
		ValueMappingResult<CustomizableFieldValueDto> result = victim.map(request);

		// CHECK
		assertEquals(expected, result);
	}

	@Test
	void map_skipsNonSupportingMapper() {
		// PREPARE
		CustomizableFieldValuePatchRequest request = buildRequest("42", CustomizableFieldType.NUMBER);

		Mockito.lenient().when(mapperA.supports(CustomizableFieldType.NUMBER)).thenReturn(false);
		Mockito.lenient().when(mapperB.supports(CustomizableFieldType.NUMBER)).thenReturn(true);
		Mockito.lenient().when(mapperB.map(request)).thenReturn(ValueMappingResult.withData(new CustomizableFieldValueDto()));

		// EXECUTE
		victim.map(request);

		// CHECK
		verify(mapperA, never()).map(any());
	}

	@Test
	void map_noSupportingMapper_returnsUnsupportedTargetType() {
		// PREPARE
		CustomizableFieldValuePatchRequest request = buildRequest("someValue", CustomizableFieldType.COMBOBOX);

		when(mapperA.supports(CustomizableFieldType.COMBOBOX)).thenReturn(false);
		when(mapperB.supports(CustomizableFieldType.COMBOBOX)).thenReturn(false);

		// EXECUTE
		ValueMappingResult<CustomizableFieldValueDto> result = victim.map(request);

		// CHECK
		assertEquals(DataPatchFailureCause.UNSUPPORTED_TARGET_TYPE, result.getDataPatchFailureCause());
	}

	@Test
	void map_mapperReturnsFailure_passesThrough() {
		// PREPARE
		CustomizableFieldValuePatchRequest request = buildRequest("badValue", CustomizableFieldType.DATE);

		when(mapperA.supports(CustomizableFieldType.DATE)).thenReturn(true);
		ValueMappingResult<CustomizableFieldValueDto> failure = ValueMappingResult.withCause(DataPatchFailureCause.INVALID_VALUE_TYPE);
		when(mapperA.map(request)).thenReturn(failure);

		// EXECUTE
		ValueMappingResult<CustomizableFieldValueDto> result = victim.map(request);

		// CHECK
		assertEquals(DataPatchFailureCause.INVALID_VALUE_TYPE, result.getDataPatchFailureCause());
	}

	@Test
	void map_noMappers_returnsUnsupportedTargetType() {
		// PREPARE
		victim = new CustomizableFieldValuePatchMapperRegistry(instances);
		when(instances.stream()).thenReturn(Stream.of());
		victim.init();

		CustomizableFieldValuePatchRequest request = buildRequest("text", CustomizableFieldType.TEXT);

		// EXECUTE
		ValueMappingResult<CustomizableFieldValueDto> result = victim.map(request);

		// CHECK
		assertEquals(DataPatchFailureCause.UNSUPPORTED_TARGET_TYPE, result.getDataPatchFailureCause());
	}

	private static CustomizableFieldValuePatchRequest buildRequest(Object value, CustomizableFieldType type) {
		return new CustomizableFieldValuePatchRequest().setValue(value)
			.setTargetType(type)
			.setCustomizableFieldValueDto(new CustomizableFieldValueDto());
	}
}
