package de.symeda.sormas.backend.patch.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import javax.enterprise.inject.Instance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.api.patch.mapping.ValuePatchMapper;
import de.symeda.sormas.api.patch.mapping.ValuePatchRequest;
import de.symeda.sormas.backend.AbstractUnitTest;

class ValueMapperRegistryTest extends AbstractUnitTest {

	@Mock
	private Instance<ValuePatchMapper> instances;

	@Mock
	private ValuePatchMapper mapperA;

	@Mock
	private ValuePatchMapper mapperB;

	@InjectMocks
	private ValueMapperRegistry victim;

	@BeforeEach
	void setUp() {
		victim.init();
		Mockito.lenient().when(instances.stream()).thenAnswer(ignored -> Stream.of(mapperA, mapperB));
	}

	@Test
	void map_nullValue_returnsNullResult() {
		// PREPARE
		ValuePatchRequest<String> request = new ValuePatchRequest<>();
		request.setValue(null);

		// EXECUTE
		ValueMappingResult<String> result = victim.map(request);

		// CHECK
		assertNull(result.getData());
	}

	@Test
	void map_valueAlreadyTargetType_returnsCastValue() {
		// PREPARE
		ValuePatchRequest<String> request = new ValuePatchRequest<>();
		String value = "test";
		request.setValue(value);
		request.setTargetType(String.class);

		// EXECUTE
		ValueMappingResult<String> result = victim.map(request);

		// CHECK
		assertSame(value, result.getData());
	}

	@Test
	void map_firstSupportingMapperUsed() {
		// PREPARE
		ValuePatchRequest<Integer> request = new ValuePatchRequest<>();
		request.setValue("42");
		request.setTargetType(Integer.class);

		when(mapperA.supports(Integer.class)).thenReturn(false);
		when(mapperB.supports(Integer.class)).thenReturn(true);
		ValueMappingResult<Integer> mapperResult = ValueMappingResult.withData(42);
		when(mapperB.map(request)).thenReturn(mapperResult);

		// EXECUTE
		ValueMappingResult<Integer> result = victim.map(request);

		// CHECK
		assertEquals(42, result.getData());
	}

	@Test
	void map_skipsNonSupportingMappers() {
		// PREPARE
		ValuePatchRequest<Integer> request = new ValuePatchRequest<>();
		request.setValue("42");
		request.setTargetType(Integer.class);

		Mockito.lenient().when(mapperA.supports(Integer.class)).thenReturn(false);
		Mockito.lenient().when(mapperB.supports(Integer.class)).thenReturn(true);
		ValueMappingResult<Integer> mapperResult = ValueMappingResult.withData(42);
		Mockito.lenient().when(mapperB.map(request)).thenReturn(mapperResult);

		// EXECUTE
		victim.map(request);

		// CHECK
		verify(mapperA, never()).map(any());
	}

	@Test
	void map_noSupportingMapper_returnsUnsupportedTargetType() {
		// PREPARE
		ValuePatchRequest<ObjectMapper> request = new ValuePatchRequest<>();
		request.setValue("unsupported");
		request.setTargetType(ObjectMapper.class);

		when(mapperA.supports(Object.class)).thenReturn(false);
		when(mapperB.supports(Object.class)).thenReturn(false);

		// EXECUTE
		ValueMappingResult<ObjectMapper> result = victim.map(request);

		// CHECK
		assertEquals(DataPatchFailureCause.UNSUPPORTED_TARGET_TYPE, result.getDataPatchFailureCause());
	}

	@Test
	void map_noMappers_returnsUnsupportedTargetType() {
		// PREPARE
		ValuePatchRequest<UnsupportedOperationException> request = new ValuePatchRequest<>();
		request.setValue("test");
		request.setTargetType(UnsupportedOperationException.class);

		victim = new ValueMapperRegistry(instances);
		victim.init();
		Mockito.lenient().when(instances.stream()).thenReturn(Stream.of());

		// EXECUTE
		ValueMappingResult<UnsupportedOperationException> result = victim.map(request);

		// CHECK
		assertEquals(DataPatchFailureCause.UNSUPPORTED_TARGET_TYPE, result.getDataPatchFailureCause());
	}

	@Test
	void map_mapperReturnsFailure_passesThrough() {
		// PREPARE
		ValuePatchRequest<Integer> request = new ValuePatchRequest<>();
		request.setValue("42");
		request.setTargetType(Integer.class);

		when(mapperA.supports(Integer.class)).thenReturn(true);
		ValueMappingResult<Integer> failureResult = ValueMappingResult.withCause(DataPatchFailureCause.INVALID_VALUE_TYPE);
		when(mapperA.map(request)).thenReturn(failureResult);

		victim.init();

		// EXECUTE
		ValueMappingResult<Integer> result = victim.map(request);

		// CHECK
		assertEquals(DataPatchFailureCause.INVALID_VALUE_TYPE, result.getDataPatchFailureCause());
	}
}
