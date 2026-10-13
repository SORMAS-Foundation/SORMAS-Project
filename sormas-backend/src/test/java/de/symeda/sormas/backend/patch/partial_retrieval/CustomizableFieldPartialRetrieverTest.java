package de.symeda.sormas.backend.patch.partial_retrieval;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldContext;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.patch.partial_retrieval.FieldInfo;
import de.symeda.sormas.api.patch.partial_retrieval.PartialRetrievalFailureCause;
import de.symeda.sormas.api.utils.Tuple;
import de.symeda.sormas.backend.AbstractUnitTest;
import de.symeda.sormas.backend.customizablefield.CustomizableFieldMetadataFacadeEjb;
import de.symeda.sormas.backend.customizablefield.CustomizableFieldValueFacadeEjb;
import de.symeda.sormas.backend.patch.PathFailureCause;

class CustomizableFieldPartialRetrieverTest extends AbstractUnitTest {

	@Mock
	private CustomizableFieldValueFacadeEjb.CustomizableFieldValueFacadeEjbLocal valueFacade;

	@Mock
	private CustomizableFieldMetadataFacadeEjb.CustomizableFieldMetadataFacadeEjbLocal metaDataFacade;

	@Mock
	private CaseDataDto caseData;

	@Mock
	private EpiDataDto epiData;

	@InjectMocks
	private CustomizableFieldPartialRetriever victim;

	private static final String CASE_UUID = "case-uuid-001";
	private static final String EPI_DATA_UUID = "epi-data-uuid-001";

	@BeforeEach
	void setUp() {
		Mockito.lenient().when(caseData.getUuid()).thenReturn(CASE_UUID);
		Mockito.lenient().when(caseData.getEpiData()).thenReturn(epiData);
		Mockito.lenient().when(epiData.getUuid()).thenReturn(EPI_DATA_UUID);
	}

	// --- Empty / null inputs ---

	@Test
	void retrieve_emptyList_returnsEmptyList() {
		assertTrue(victim.retrieve(List.of(), caseData).isEmpty());
		verifyNoInteractions(metaDataFacade, valueFacade);
	}

	@Test
	void retrieve_nullList_returnsEmptyList() {
		assertTrue(victim.retrieve(null, caseData).isEmpty());
		verifyNoInteractions(metaDataFacade, valueFacade);
	}

	// --- Path parsing failures ---

	@Test
	void retrieve_preExistingPathFailureCause_propagatesAsRetrievalFailureCause() {
		// PREPARE
		String path = "Custom.CaseData.someField";
		Tuple<String, PathFailureCause> tuple = Tuple.of(path, PathFailureCause.FORBIDDEN_FIELD);

		// EXECUTE
		List<Tuple<String, Tuple<FieldInfo, PartialRetrievalFailureCause>>> result = victim.retrieve(List.of(tuple), caseData);

		// CHECK
		assertEquals(1, result.size());
		assertEquals(path, result.get(0).getFirst());
		assertNull(result.get(0).getSecond().getFirst());
		assertEquals(PathFailureCause.FORBIDDEN_FIELD.getRelatedRetrieveFailureCause(), result.get(0).getSecond().getSecond());
		verifyNoInteractions(metaDataFacade, valueFacade);
	}

	@Test
	void retrieve_pathTwoSegments_returnsInvalidPathFormat() {
		// PREPARE — missing leaf field segment
		Tuple<String, PathFailureCause> tuple = Tuple.of("Custom.CaseData", null);

		// EXECUTE
		List<Tuple<String, Tuple<FieldInfo, PartialRetrievalFailureCause>>> result = victim.retrieve(List.of(tuple), caseData);

		// CHECK
		assertEquals(PartialRetrievalFailureCause.INVALID_PATH_FORMAT, result.get(0).getSecond().getSecond());
		verifyNoInteractions(metaDataFacade, valueFacade);
	}

	@Test
	void retrieve_pathFourSegments_returnsInvalidPathFormat() {
		// PREPARE — one extra segment
		Tuple<String, PathFailureCause> tuple = Tuple.of("Custom.CaseData.field.extra", null);

		// EXECUTE
		List<Tuple<String, Tuple<FieldInfo, PartialRetrievalFailureCause>>> result = victim.retrieve(List.of(tuple), caseData);

		// CHECK
		assertEquals(PartialRetrievalFailureCause.INVALID_PATH_FORMAT, result.get(0).getSecond().getSecond());
		verifyNoInteractions(metaDataFacade, valueFacade);
	}

	@Test
	void retrieve_unknownContextPrefix_returnsUnsupportedPrefix() {
		// PREPARE
		Tuple<String, PathFailureCause> tuple = Tuple.of("Custom.UnknownContext.field", null);

		// EXECUTE
		List<Tuple<String, Tuple<FieldInfo, PartialRetrievalFailureCause>>> result = victim.retrieve(List.of(tuple), caseData);

		// CHECK
		assertEquals(PartialRetrievalFailureCause.UNSUPPORTED_PREFIX, result.get(0).getSecond().getSecond());
		verifyNoInteractions(metaDataFacade, valueFacade);
	}

	@Test
	void retrieve_exposureContext_returnsUnsupportedPrefix() {
		// PREPARE — Exposure is intentionally excluded from partial retrieval
		Tuple<String, PathFailureCause> tuple = Tuple.of("Custom.Exposure.someField", null);

		// EXECUTE
		List<Tuple<String, Tuple<FieldInfo, PartialRetrievalFailureCause>>> result = victim.retrieve(List.of(tuple), caseData);

		// CHECK
		assertEquals(PartialRetrievalFailureCause.UNSUPPORTED_PREFIX, result.get(0).getSecond().getSecond());
		verifyNoInteractions(metaDataFacade, valueFacade);
	}

	// --- Successful retrieval ---

	@Test
	void retrieve_caseFieldWithSavedValue_returnsFieldInfoWithValue() {
		// PREPARE
		String fieldName = "myTextField";
		String fieldValue = "hello world";
		String path = "Custom.CaseData." + fieldName;

		CustomizableFieldMetadataDto metadata = buildMetadata(fieldName);
		CustomizableFieldValueDto valueDto = buildValueDto(fieldValue);

		when(metaDataFacade.getActiveFieldsForContext(CustomizableFieldContext.CASE)).thenReturn(List.of(metadata));
		when(valueFacade.getValuesForEntity(CASE_UUID, CustomizableFieldContext.CASE)).thenReturn(Map.of(metadata, valueDto));

		// EXECUTE
		List<Tuple<String, Tuple<FieldInfo, PartialRetrievalFailureCause>>> result = victim.retrieve(List.of(Tuple.of(path, null)), caseData);

		// CHECK
		assertEquals(1, result.size());
		assertEquals(path, result.get(0).getFirst());
		assertNull(result.get(0).getSecond().getSecond());
		FieldInfo fieldInfo = result.get(0).getSecond().getFirst();
		assertNotNull(fieldInfo);
		assertEquals(fieldName, fieldInfo.getTranslatedFieldName());
		assertEquals(fieldValue, fieldInfo.getFieldValue());
		assertEquals(String.class, fieldInfo.getFieldType());
	}

	@Test
	void retrieve_epiDataFieldWithSavedValue_returnsFieldInfoWithValue() {
		// PREPARE
		String fieldName = "epiCustomField";
		String fieldValue = "epi-value";
		String path = "Custom.EpiData." + fieldName;

		CustomizableFieldMetadataDto metadata = buildMetadata(fieldName);
		CustomizableFieldValueDto valueDto = buildValueDto(fieldValue);

		when(metaDataFacade.getActiveFieldsForContext(CustomizableFieldContext.EPIDATA)).thenReturn(List.of(metadata));
		when(valueFacade.getValuesForEntity(EPI_DATA_UUID, CustomizableFieldContext.EPIDATA)).thenReturn(Map.of(metadata, valueDto));

		// EXECUTE
		List<Tuple<String, Tuple<FieldInfo, PartialRetrievalFailureCause>>> result = victim.retrieve(List.of(Tuple.of(path, null)), caseData);

		// CHECK
		assertNull(result.get(0).getSecond().getSecond());
		FieldInfo fieldInfo = result.get(0).getSecond().getFirst();
		assertNotNull(fieldInfo);
		assertEquals(fieldName, fieldInfo.getTranslatedFieldName());
		assertEquals(fieldValue, fieldInfo.getFieldValue());
	}

	@Test
	void retrieve_fieldWithNoSavedValue_returnsFieldInfoWithNullValue() {
		// PREPARE — metadata exists but no value has been stored for this entity
		String fieldName = "emptyField";
		CustomizableFieldMetadataDto metadata = buildMetadata(fieldName);

		when(metaDataFacade.getActiveFieldsForContext(CustomizableFieldContext.CASE)).thenReturn(List.of(metadata));
		when(valueFacade.getValuesForEntity(CASE_UUID, CustomizableFieldContext.CASE)).thenReturn(Map.of());

		// EXECUTE
		List<Tuple<String, Tuple<FieldInfo, PartialRetrievalFailureCause>>> result =
			victim.retrieve(List.of(Tuple.of("Custom.CaseData." + fieldName, null)), caseData);

		// CHECK
		assertNull(result.get(0).getSecond().getSecond());
		FieldInfo fieldInfo = result.get(0).getSecond().getFirst();
		assertNotNull(fieldInfo);
		assertEquals(fieldName, fieldInfo.getTranslatedFieldName());
		assertNull(fieldInfo.getFieldValue());
	}

	@Test
	void retrieve_unknownFieldName_returnsFieldDoesNotExist() {
		// PREPARE — active metadata only contains "otherField", not the requested field
		CustomizableFieldMetadataDto metadata = buildMetadata("otherField");

		when(metaDataFacade.getActiveFieldsForContext(CustomizableFieldContext.CASE)).thenReturn(List.of(metadata));
		when(valueFacade.getValuesForEntity(CASE_UUID, CustomizableFieldContext.CASE)).thenReturn(Map.of());

		// EXECUTE
		List<Tuple<String, Tuple<FieldInfo, PartialRetrievalFailureCause>>> result =
			victim.retrieve(List.of(Tuple.of("Custom.CaseData.nonExistentField", null)), caseData);

		// CHECK
		assertNull(result.get(0).getSecond().getFirst());
		assertEquals(PartialRetrievalFailureCause.FIELD_DOES_NOT_EXIST, result.get(0).getSecond().getSecond());
	}

	@Test
	void retrieve_noActiveMetadata_returnsFieldDoesNotExist() {
		// PREPARE — no active metadata fields registered for this context
		when(metaDataFacade.getActiveFieldsForContext(CustomizableFieldContext.CASE)).thenReturn(List.of());
		when(valueFacade.getValuesForEntity(CASE_UUID, CustomizableFieldContext.CASE)).thenReturn(Map.of());

		// EXECUTE
		List<Tuple<String, Tuple<FieldInfo, PartialRetrievalFailureCause>>> result =
			victim.retrieve(List.of(Tuple.of("Custom.CaseData.anyField", null)), caseData);

		// CHECK
		assertNull(result.get(0).getSecond().getFirst());
		assertEquals(PartialRetrievalFailureCause.FIELD_DOES_NOT_EXIST, result.get(0).getSecond().getSecond());
	}

	// --- Batching behavior ---

	@Test
	void retrieve_twoFieldsSameContext_fetchesMetadataAndValuesOnlyOnce() {
		// PREPARE — two fields in the CASE context
		CustomizableFieldMetadataDto metadata1 = buildMetadata("field1");
		CustomizableFieldMetadataDto metadata2 = buildMetadata("field2");
		CustomizableFieldValueDto valueDto1 = buildValueDto("value-1");
		CustomizableFieldValueDto valueDto2 = buildValueDto("value-2");

		when(metaDataFacade.getActiveFieldsForContext(CustomizableFieldContext.CASE)).thenReturn(List.of(metadata1, metadata2));
		when(valueFacade.getValuesForEntity(CASE_UUID, CustomizableFieldContext.CASE)).thenReturn(Map.of(metadata1, valueDto1, metadata2, valueDto2));

		List<Tuple<String, PathFailureCause>> tuples = List.of(Tuple.of("Custom.CaseData.field1", null), Tuple.of("Custom.CaseData.field2", null));

		// EXECUTE
		List<Tuple<String, Tuple<FieldInfo, PartialRetrievalFailureCause>>> result = victim.retrieve(tuples, caseData);

		// CHECK — both resolved correctly, in order
		assertEquals(2, result.size());
		assertEquals("value-1", result.get(0).getSecond().getFirst().getFieldValue());
		assertEquals("value-2", result.get(1).getSecond().getFirst().getFieldValue());
		// metadata and values fetched exactly once despite two fields (batching)
		verify(metaDataFacade, times(1)).getActiveFieldsForContext(CustomizableFieldContext.CASE);
		verify(valueFacade, times(1)).getValuesForEntity(CASE_UUID, CustomizableFieldContext.CASE);
	}

	// --- getEntityUuid ---

	@Test
	void getEntityUuid_caseContext_returnsCaseUuid() {
		assertEquals(Optional.of(CASE_UUID), victim.getEntityUuid(CustomizableFieldContext.CASE, caseData));
	}

	@Test
	void getEntityUuid_epiDataContext_returnsEpiDataUuid() {
		assertEquals(Optional.of(EPI_DATA_UUID), victim.getEntityUuid(CustomizableFieldContext.EPIDATA, caseData));
	}

	@Test
	void getEntityUuid_exposureContext_returnsEmpty() {
		// Exposure is handled by the default branch → no UUID is resolved
		assertEquals(Optional.empty(), victim.getEntityUuid(CustomizableFieldContext.EXPOSURE, caseData));
	}

	@Test
	void assert_all_entites_are_supported() {
		CaseDataDto caseDataDto = new CaseDataDto();
		caseDataDto.setUuid("fiowej");

		caseDataDto.setEpiData(EpiDataDto.build());

		Assertions.assertAll(
			CustomizableFieldPartialRetriever.SUPPORTED_CONTEXTS.stream()
				.map(context -> (Executable) () -> Assertions.assertNotNull(CustomizableFieldPartialRetriever.getEntityUuid(context, caseDataDto)))
				.collect(Collectors.toList()));
	}

	// --- Helpers ---

	private static CustomizableFieldMetadataDto buildMetadata(String name) {
		CustomizableFieldMetadataDto dto = new CustomizableFieldMetadataDto();
		dto.setUuid("metadata-uuid-" + name);
		dto.setName(name);
		return dto;
	}

	private static CustomizableFieldValueDto buildValueDto(String value) {
		CustomizableFieldValueDto dto = new CustomizableFieldValueDto();
		dto.setValue(value);
		return dto;
	}
}
