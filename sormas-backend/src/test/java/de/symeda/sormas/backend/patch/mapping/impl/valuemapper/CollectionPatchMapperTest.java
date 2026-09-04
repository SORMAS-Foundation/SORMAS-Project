package de.symeda.sormas.backend.patch.mapping.impl.valuemapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import de.symeda.sormas.api.patch.DataPatchFailureCause;
import de.symeda.sormas.api.patch.mapping.ValueMappingResult;
import de.symeda.sormas.api.patch.mapping.ValuePatchRequest;
import de.symeda.sormas.backend.AbstractUnitTest;
import de.symeda.sormas.backend.patch.mapping.ValueMapperRegistry;

class CollectionPatchMapperTest extends AbstractUnitTest {

	@Mock
	private ValueMapperRegistry valueMapperRegistry;

	@InjectMocks
	private CollectionPatchMapper victim;

	/**
	 * Makes sure no type is forgotten, to be able to collect the values to the appropriate type.
	 */
	@Test
	void supportedTypes_allHaveACollectorDictionaryEntry() {
		for (Class<?> supportedType : CollectionPatchMapper.SUPPORTED_TYPES) {
			assertTrue(
				CollectionPatchMapper.COLLECTOR_DICTIONARY.containsKey(supportedType),
				"COLLECTOR_DICTIONARY is missing an entry for supported type: " + supportedType);
		}
	}

	@Test
	@SuppressWarnings({
		"unchecked",
		"rawtypes" })
	void map_setTarget_splitsCommaSeparatedValueAndCollectsIntoSet() {
		// PREPARE — registry echoes back whatever single value it was asked to map
		when(valueMapperRegistry.map(any(ValuePatchRequest.class)))
			.thenAnswer(invocation -> ValueMappingResult.withData(((ValuePatchRequest) invocation.getArgument(0)).getValue()));

		ValuePatchRequest request = new ValuePatchRequest().setValue("A,B,C").setTargetType(Set.class).setCollectionSubType(String.class);

		// EXECUTE
		ValueMappingResult<Set> result = victim.map(request);

		// CHECK
		assertEquals(Set.of("A", "B", "C"), result.getData());
	}

	@Test
	@SuppressWarnings({
		"unchecked",
		"rawtypes" })
	void map_listTarget_splitsCommaSeparatedValueAndCollectsIntoListPreservingOrder() {
		// PREPARE
		when(valueMapperRegistry.map(any(ValuePatchRequest.class)))
			.thenAnswer(invocation -> ValueMappingResult.withData(((ValuePatchRequest) invocation.getArgument(0)).getValue()));

		ValuePatchRequest request = new ValuePatchRequest().setValue("C,A,B").setTargetType(List.class).setCollectionSubType(String.class);

		// EXECUTE
		ValueMappingResult<List> result = victim.map(request);

		// CHECK
		assertEquals(List.of("C", "A", "B"), result.getData());
	}

	@Test
	@SuppressWarnings({
		"unchecked",
		"rawtypes" })
	void map_delegatesEachSplitValueToRegistryWithCollectionSubTypeAsTargetType() {
		// PREPARE
		when(valueMapperRegistry.map(any(ValuePatchRequest.class)))
			.thenAnswer(invocation -> ValueMappingResult.withData(((ValuePatchRequest) invocation.getArgument(0)).getTargetType()));

		ValuePatchRequest request = new ValuePatchRequest().setValue("A,B").setTargetType(Set.class).setCollectionSubType(Integer.class);

		// EXECUTE
		ValueMappingResult<Set> result = victim.map(request);

		// CHECK — each delegated request was resolved against the element type, not Set.class
		assertEquals(Set.of(Integer.class), result.getData());
	}

	@Test
	@SuppressWarnings("rawtypes")
	void map_nullCollectionSubType_returnsTechnicalFailure() {
		// PREPARE
		ValuePatchRequest request = new ValuePatchRequest().setValue("A,B").setTargetType(Set.class);

		// EXECUTE
		ValueMappingResult<Set> result = victim.map(request);

		// CHECK
		assertEquals(DataPatchFailureCause.TECHNICAL, result.getDataPatchFailureCause());
	}

	@Test
	@SuppressWarnings("rawtypes")
	void map_nonStringValue_returnsInvalidValueType() {
		// PREPARE
		ValuePatchRequest request = new ValuePatchRequest().setValue(42).setTargetType(Set.class).setCollectionSubType(String.class);

		// EXECUTE
		ValueMappingResult<Set> result = victim.map(request);

		// CHECK
		assertEquals(DataPatchFailureCause.INVALID_VALUE_TYPE, result.getDataPatchFailureCause());
	}

	@Test
	@SuppressWarnings({
		"unchecked",
		"rawtypes" })
	void map_oneElementFailsToMap_propagatesFailureCause() {
		// PREPARE — "B" is rejected by the (mocked) downstream mapper
		when(valueMapperRegistry.map(any(ValuePatchRequest.class))).thenAnswer(invocation -> {
			Object value = ((ValuePatchRequest) invocation.getArgument(0)).getValue();
			if ("B".equals(value)) {
				return ValueMappingResult.withCause(DataPatchFailureCause.NOT_PRESENT_IN_REFERENCE_DATA_LIST);
			}
			return ValueMappingResult.withData(value);
		});

		ValuePatchRequest request = new ValuePatchRequest().setValue("A,B,C").setTargetType(Set.class).setCollectionSubType(String.class);

		// EXECUTE
		ValueMappingResult<Set> result = victim.map(request);

		// CHECK
		assertEquals(DataPatchFailureCause.NOT_PRESENT_IN_REFERENCE_DATA_LIST, result.getDataPatchFailureCause());
	}

	@Test
	void getSupportedTypes_containsSetAndList() {
		assertEquals(Set.of(Set.class, List.class), victim.getSupportedTypes());
	}
}
