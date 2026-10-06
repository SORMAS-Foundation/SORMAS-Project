package de.symeda.sormas.backend.importexport.hierarchical.lab;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ColumnSpec;
import de.symeda.sormas.backend.importexport.hierarchical.engine.PropertyPaths;

class LabImportSchemaTest {

	private static final Map<String, Class<?>> TARGET_TYPES = Map.of(
		LabImportSchema.CASE,
		CaseDataDto.class,
		LabImportSchema.PERSON,
		PersonDto.class,
		LabImportSchema.SAMPLE,
		SampleDto.class,
		LabImportSchema.TEST,
		PathogenTestDto.class);

	@Test
	void everyPropertyColumnResolvesOnItsDto() {
		for (ColumnSpec column : LabImportSchema.columns()) {
			if (column.isProperty()) {
				Class<?> type = TARGET_TYPES.get(column.target());
				assertNotNull(PropertyPaths.descriptor(type, column.propertyPath()[0]).getWriteMethod(), column.header());
			}
		}
	}

	@Test
	void everyHeaderBelongsToALevelAndIsUnique() {
		List<String> headers = LabImportSchema.columns().stream().map(ColumnSpec::header).collect(Collectors.toList());

		headers.forEach(header -> assertTrue(LabImportSchema.SCHEMA.levelOf(header).isPresent(), header));
		assertEquals(headers.size(), headers.stream().distinct().count());
	}

	@Test
	void infrastructureColumnsComeInDependencyOrder() {
		List<String> headers = LabImportSchema.columns().stream().map(ColumnSpec::header).collect(Collectors.toList());

		assertTrue(headers.indexOf("case.responsibleRegion") < headers.indexOf("case.responsibleDistrict"));
		assertTrue(headers.indexOf("case.responsibleDistrict") < headers.indexOf("case.responsibleCommunity"));
		assertTrue(headers.indexOf("case.responsibleCommunity") < headers.indexOf("case.facilityType"));
		assertTrue(headers.indexOf("case.facilityType") < headers.indexOf("case.healthFacility"));
	}
}
