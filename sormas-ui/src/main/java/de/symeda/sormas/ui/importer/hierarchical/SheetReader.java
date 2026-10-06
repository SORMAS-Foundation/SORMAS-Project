package de.symeda.sormas.ui.importer.hierarchical;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.input.BOMInputStream;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheetRow;
import de.symeda.sormas.api.utils.CSVUtils;

public final class SheetReader {

	private SheetReader() {
	}

	public static ImportSheet read(InputStream input, String fileName, char csvSeparator, Map<String, String> captionsByHeader) throws IOException {
		List<List<String>> table = fileName.toLowerCase(Locale.ROOT).endsWith(".xlsx") ? readXlsx(input) : readCsv(input, csvSeparator);
		dropTrailingBlankRows(table);
		if (table.isEmpty()) {
			return new ImportSheet(List.of(), List.of());
		}
		dropErrorColumn(table);
		List<String> headers = table.get(0);
		int firstDataRow = isCaptionRow(table, headers, captionsByHeader) ? 2 : 1;
		List<ImportSheetRow> rows = new ArrayList<>();
		for (int i = firstDataRow; i < table.size(); i++) {
			rows.add(new ImportSheetRow(i + 1, table.get(i)));
		}
		return new ImportSheet(headers, rows);
	}

	private static List<List<String>> readCsv(InputStream input, char separator) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new BOMInputStream(input), StandardCharsets.UTF_8));
		try (CSVReader csvReader = CSVUtils.createCSVReader(reader, separator)) {
			List<List<String>> table = new ArrayList<>();
			String[] line;
			while ((line = csvReader.readNext()) != null) {
				table.add(new ArrayList<>(Arrays.asList(line)));
			}
			return table;
		} catch (CsvValidationException e) {
			throw new IOException(e);
		}
	}

	private static List<List<String>> readXlsx(InputStream input) throws IOException {
		try (Workbook workbook = new XSSFWorkbook(input)) {
			Sheet sheet = workbook.getSheetAt(0);
			DataFormatter formatter = new DataFormatter(Locale.ROOT);
			FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			Row headerRow = sheet.getRow(sheet.getFirstRowNum());
			int width = headerRow == null ? 0 : headerRow.getLastCellNum();
			List<List<String>> table = new ArrayList<>();
			for (int r = sheet.getFirstRowNum(); r <= sheet.getLastRowNum(); r++) {
				Row row = sheet.getRow(r);
				List<String> cells = new ArrayList<>();
				for (int c = 0; c < width; c++) {
					cells.add(row == null ? "" : cellText(row.getCell(c), formatter, evaluator));
				}
				table.add(cells);
			}
			return table;
		}
	}

	private static String cellText(Cell cell, DataFormatter formatter, FormulaEvaluator evaluator) {
		if (cell == null) {
			return "";
		}
		if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
			Language language = I18nProperties.getUserLanguage();
			boolean hasTime = cell.getNumericCellValue() % 1 != 0;
			return new SimpleDateFormat(hasTime ? language.getDateTimeFormat() : language.getDateFormat()).format(cell.getDateCellValue());
		}
		return formatter.formatCellValue(cell, evaluator);
	}

	private static void dropTrailingBlankRows(List<List<String>> table) {
		while (!table.isEmpty() && new ImportSheetRow(0, table.get(table.size() - 1)).isBlank()) {
			table.remove(table.size() - 1);
		}
	}

	private static void dropErrorColumn(List<List<String>> table) {
		String errorColumn = I18nProperties.getCaption(Captions.importErrorDescription);
		int index = -1;
		List<String> headers = table.get(0);
		for (int i = 0; i < headers.size(); i++) {
			if (ImportSheetRow.normalize(headers.get(i)).equals(errorColumn)) {
				index = i;
			}
		}
		if (index < 0) {
			return;
		}
		for (List<String> row : table) {
			if (index < row.size()) {
				row.remove(index);
			}
		}
	}

	private static boolean isCaptionRow(List<List<String>> table, List<String> headers, Map<String, String> captionsByHeader) {
		if (table.size() < 2) {
			return false;
		}
		ImportSheetRow second = new ImportSheetRow(0, table.get(1));
		ImportSheetRow header = new ImportSheetRow(0, headers);
		boolean anyFilled = false;
		for (int i = 0; i < table.get(1).size(); i++) {
			String value = second.cell(i);
			if (value.isEmpty()) {
				continue;
			}
			anyFilled = true;
			if (!value.equals(captionsByHeader.get(header.cell(i)))) {
				return false;
			}
		}
		return anyFilled;
	}
}
