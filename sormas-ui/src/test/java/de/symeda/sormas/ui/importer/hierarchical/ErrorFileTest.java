package de.symeda.sormas.ui.importer.hierarchical;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueKind;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheetRow;

class ErrorFileTest {

	@Test
	void keepsEveryRowOfAFailedBlockWithItsMessages() {
		ImportSheet sheet = new ImportSheet(
			List.of("CaseData.epidNumber", "Sample.fieldSampleID"),
			List.of(
				new ImportSheetRow(2, List.of("A", "S1")),
				new ImportSheetRow(3, List.of("", "S2")),
				new ImportSheetRow(4, List.of("B", "S3")),
				new ImportSheetRow(5, Arrays.asList("", null))));
		ImportIssueDto issue = new ImportIssueDto(ImportIssueKind.BLOCK, 3, "Sample.fieldSampleID", "bad sample");
		ImportIssueDto commitIssue = new ImportIssueDto(ImportIssueKind.COMMIT, null, null, "changed");

		List<String[]> rows = ErrorFile.rows(sheet, List.of(new ErrorFile.FailedBlock(2, 3, List.of(issue, commitIssue))), List.of());

		assertArrayEquals(new String[] { "", "CaseData", "Sample" }, rows.get(0));
		assertArrayEquals(new String[] { I18nProperties.getCaption(Captions.importErrorDescription), "epidNumber", "fieldSampleID" }, rows.get(1));
		assertEquals(4, rows.size());
		assertArrayEquals(new String[] { "changed", "A", "S1" }, rows.get(2));
		assertArrayEquals(new String[] { "bad sample", "", "S2" }, rows.get(3));
	}

	@Test
	void addsRowsOfLooseIssues() {
		ImportSheet sheet = new ImportSheet(List.of("PathogenTest.testType"), List.of(new ImportSheetRow(2, List.of("PCR"))));
		ImportIssueDto orphan = new ImportIssueDto(ImportIssueKind.ROW, 2, null, "orphan");

		List<String[]> rows = ErrorFile.rows(sheet, List.of(), List.of(orphan));

		assertArrayEquals(new String[] { "orphan", "PCR" }, rows.get(2));
	}
}
