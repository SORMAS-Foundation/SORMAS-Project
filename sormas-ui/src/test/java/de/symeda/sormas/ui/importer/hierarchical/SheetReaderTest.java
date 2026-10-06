package de.symeda.sormas.ui.importer.hierarchical;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;

class SheetReaderTest {

	private static final Map<String, String> CAPTIONS = Map.of("case.uuid", "Case ID", "sample.fieldSampleID", "Field sample ID");

	@Test
	void readsCsvWithItsSeparatorAndBom() throws IOException {
		ImportSheet sheet = read("\uFEFFcase.uuid;sample.fieldSampleID\nABC;S1\n;S2\n", "upload.csv", ';');

		assertEquals(List.of("case.uuid", "sample.fieldSampleID"), sheet.getHeaders());
		assertEquals(2, sheet.getRows().size());
		assertEquals(2, sheet.getRows().get(0).getLineNumber());
		assertEquals("S2", sheet.getRows().get(1).cell(1));
	}

	@Test
	void skipsTheTemplateCaptionRow() throws IOException {
		ImportSheet sheet = read("case.uuid,sample.fieldSampleID\nCase ID,Field sample ID\nABC,S1\n", "upload.csv", ',');

		assertEquals(1, sheet.getRows().size());
		assertEquals("ABC", sheet.getRows().get(0).cell(0));
		assertEquals(3, sheet.getRows().get(0).getLineNumber());
	}

	@Test
	void dropsTrailingBlankRows() throws IOException {
		ImportSheet sheet = read("case.uuid,sample.fieldSampleID\nABC,S1\n,\n,\n", "upload.csv", ',');

		assertEquals(1, sheet.getRows().size());
	}

	@Test
	void dropsTheErrorDescriptionColumn() throws IOException {
		String errorColumn = I18nProperties.getCaption(Captions.importErrorDescription);

		ImportSheet sheet = read(errorColumn + ",case.uuid\nbad date,ABC\n", "errors.csv", ',');

		assertEquals(List.of("case.uuid"), sheet.getHeaders());
		assertEquals("ABC", sheet.getRows().get(0).cell(0));
	}

	@Test
	void readsXlsxNumbersDatesAndMergedCells() throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet();
			Row header = sheet.createRow(0);
			header.createCell(0).setCellValue("case.externalID");
			header.createCell(1).setCellValue("sample.fieldSampleID");
			header.createCell(2).setCellValue("sample.sampleDateTime");
			Row first = sheet.createRow(1);
			first.createCell(0).setCellValue("A");
			first.createCell(1).setCellValue(1201);
			CellStyle dateStyle = workbook.createCellStyle();
			dateStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("m/d/yy"));
			first.createCell(2).setCellValue(java.sql.Date.valueOf("2026-09-02"));
			first.getCell(2).setCellStyle(dateStyle);
			Row second = sheet.createRow(2);
			second.createCell(0).setCellValue("ignored by merge");
			second.createCell(1).setCellValue("1202");
			sheet.addMergedRegion(new CellRangeAddress(1, 2, 0, 0));
			workbook.write(bytes);
		}

		ImportSheet sheet = SheetReader.read(new ByteArrayInputStream(bytes.toByteArray()), "upload.xlsx", ',', CAPTIONS);

		assertEquals("A", sheet.getRows().get(0).cell(0));
		assertEquals("1201", sheet.getRows().get(0).cell(1));
		assertEquals("9/2/2026", sheet.getRows().get(0).cell(2));
		assertEquals("ignored by merge", sheet.getRows().get(1).cell(0));
	}

	private static ImportSheet read(String content, String fileName, char separator) throws IOException {
		return SheetReader.read(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)), fileName, separator, CAPTIONS);
	}
}
