package de.symeda.sormas.ui.importer.hierarchical;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;

class SheetReaderTest {

	@Test
	void joinsTheEntityAndPropertyRowsAndSkipsCommentRows() throws IOException {
		ImportSheet sheet = read(
			"﻿CaseData,Person,Sample,PathogenTest\r\n" + "disease,person.firstName,lab,lab\r\n" + "##Disease,First name,Laboratory,Laboratory\r\n"
				+ "##Text,Text,Text,Text\r\n" + "TUBERCULOSIS,Jean,Lab A,Lab B\r\n" + ",,,Lab C\r\n",
			',');

		assertEquals(List.of("CaseData.disease", "Person.person.firstName", "Sample.lab", "PathogenTest.lab"), sheet.getHeaders());
		assertEquals(2, sheet.getRows().size());
		assertEquals(5, sheet.getRows().get(0).getLineNumber());
		assertEquals(6, sheet.getRows().get(1).getLineNumber());
		assertEquals("Lab C", sheet.getRows().get(1).cell(3));
	}

	@Test
	void readsItsSeparator() throws IOException {
		ImportSheet sheet = read("CaseData;Sample\nepidNumber;fieldSampleID\nABC;S1\n;S2\n", ';');

		assertEquals(List.of("CaseData.epidNumber", "Sample.fieldSampleID"), sheet.getHeaders());
		assertEquals("S2", sheet.getRows().get(1).cell(1));
	}

	@Test
	void dropsTrailingBlankRows() throws IOException {
		ImportSheet sheet = read("CaseData,Sample\nepidNumber,fieldSampleID\nABC,S1\n,\n,\n", ',');

		assertEquals(1, sheet.getRows().size());
	}

	@Test
	void dropsTheErrorColumnOfAnErrorFile() throws IOException {
		ImportSheet sheet = read(
			",CaseData,Sample\n" + I18nProperties.getCaption(Captions.importErrorDescription) + ",disease,fieldSampleID\n" + "bad date,TUBERCULOSIS,S1\n",
			',');

		assertEquals(List.of("CaseData.disease", "Sample.fieldSampleID"), sheet.getHeaders());
		assertEquals("TUBERCULOSIS", sheet.getRows().get(0).cell(0));
		assertEquals(3, sheet.getRows().get(0).getLineNumber());
	}

	@Test
	void aFileWithOnlyOneHeaderRowHasNoRows() throws IOException {
		ImportSheet sheet = read("CaseData\n", ',');

		assertEquals(List.of(), sheet.getRows());
	}

	private static ImportSheet read(String content, char separator) throws IOException {
		return SheetReader.read(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)), separator);
	}
}
