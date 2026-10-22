package de.symeda.sormas.ui.caze.importer;

import static de.symeda.sormas.ui.caze.importer.ImportFormatDetector.ImportFormat.CLASSIC;
import static de.symeda.sormas.ui.caze.importer.ImportFormatDetector.ImportFormat.NESTED;
import static de.symeda.sormas.ui.caze.importer.ImportFormatDetector.detect;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheetRow;
import de.symeda.sormas.ui.importer.hierarchical.SheetReader;

class ImportFormatDetectorTest {

	@Test
	void oneRowPerCaseIsClassic() {
		assertEquals(CLASSIC, detect(sheet(List.of("CaseData.disease", "Sample.fieldSampleID"), row(5, "TB", "S1"), row(6, "TB", "S2"))));
	}

	@Test
	void aRowContinuingTheCaseAboveIsNested() {
		assertEquals(NESTED, detect(sheet(List.of("CaseData.disease", "Sample.fieldSampleID"), row(5, "TB", "S1"), row(6, "", "S2"))));
	}

	@Test
	void aTestOnlyRowIsNested() {
		assertEquals(
			NESTED,
			detect(
				sheet(
					List.of("CaseData.disease", "Sample.fieldSampleID", "PathogenTest.testType"),
					row(5, "TB", "S1", "PCR"),
					row(6, "", "", "CULTURE"))));
	}

	@Test
	void aRepeatedSampleGroupIsClassicEvenWithBlankCaseCells() {
		List<String> headers = List.of("CaseData.disease", "Sample.fieldSampleID", "Sample.fieldSampleID");
		assertEquals(CLASSIC, detect(sheet(headers, row(5, "TB", "S1", "S2"), row(6, "", "S3", ""))));
	}

	@Test
	void aBlankCaseFirstRowIsNestedSoItIsReportedAsAnOrphan() {
		assertEquals(NESTED, detect(sheet(List.of("CaseData.disease", "Sample.fieldSampleID"), row(5, "", "S1"))));
	}

	@Test
	void aFullyBlankRowIsIgnored() {
		assertEquals(CLASSIC, detect(sheet(List.of("CaseData.disease", "Sample.fieldSampleID"), row(5, "TB", "S1"), row(6, "", ""))));
	}

	@Test
	void theClassicTestFilesAreClassic() throws IOException {
		for (String resource : List.of("/sormas_case_import_test_samples.csv", "/sormas_case_import_test_pathogen_tests.csv")) {
			try (InputStream input = getClass().getResourceAsStream(resource)) {
				assertEquals(CLASSIC, detect(SheetReader.read(input, ',')), resource);
			}
		}
	}

	@Test
	void aReuploadedNestedErrorFileIsNestedAgain() throws IOException {
		String csv = ",CaseData,Sample\n" + I18nProperties.getCaption(Captions.importErrorDescription) + ",disease,fieldSampleID\n"
			+ "bad date,TB,S1\n,,S2\n";
		assertEquals(NESTED, detect(SheetReader.read(new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)), ',')));
	}

	private static ImportSheet sheet(List<String> headers, ImportSheetRow... rows) {
		return new ImportSheet(headers, List.of(rows));
	}

	private static ImportSheetRow row(int line, String... cells) {
		return new ImportSheetRow(line, List.of(cells));
	}
}
