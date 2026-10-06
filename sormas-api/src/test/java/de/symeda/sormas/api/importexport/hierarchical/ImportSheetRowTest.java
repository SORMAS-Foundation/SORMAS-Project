package de.symeda.sormas.api.importexport.hierarchical;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class ImportSheetRowTest {

	@Test
	void normalizesNonBreakingSpacesAndBom() {
		ImportSheetRow row = new ImportSheetRow(2, Arrays.asList("  A ", "\u00A0 \u00A0", null, "\uFEFFcase.uuid"));

		assertEquals("A", row.cell(0));
		assertEquals("", row.cell(1));
		assertEquals("", row.cell(2));
		assertEquals("case.uuid", row.cell(3));
		assertEquals("", row.cell(9));
	}

	@Test
	void detectsBlankRows() {
		assertTrue(new ImportSheetRow(3, Arrays.asList("", " ", "\u00A0", null)).isBlank());
		assertFalse(new ImportSheetRow(3, List.of("", "x")).isBlank());
	}

	@Test
	void sheetNormalizesHeaders() {
		ImportSheet sheet = new ImportSheet(List.of("\uFEFFcase.uuid ", " sample.fieldSampleID"), List.of());

		assertEquals(List.of("case.uuid", "sample.fieldSampleID"), sheet.getHeaders());
	}
}
