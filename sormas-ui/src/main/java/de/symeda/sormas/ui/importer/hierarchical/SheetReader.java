package de.symeda.sormas.ui.importer.hierarchical;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.input.BOMInputStream;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.importexport.hierarchical.CaseTemplateLayout;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheetRow;
import de.symeda.sormas.api.utils.CSVCommentLineValidator;
import de.symeda.sormas.api.utils.CSVUtils;

public final class SheetReader {

	private SheetReader() {
	}

	public static ImportSheet read(InputStream input, char csvSeparator) throws IOException {
		List<List<String>> table = readCsv(input, csvSeparator);
		dropTrailingBlankRows(table);
		if (table.size() < 2) {
			return new ImportSheet(List.of(), List.of());
		}
		dropErrorColumn(table);
		List<String> entities = table.get(0);
		List<String> properties = table.get(1);
		List<String> headers = new ArrayList<>();
		for (int i = 0; i < properties.size(); i++) {
			String entity = i < entities.size() ? ImportSheetRow.normalize(entities.get(i)) : "";
			headers.add(CaseTemplateLayout.join(entity, ImportSheetRow.normalize(properties.get(i))));
		}
		List<ImportSheetRow> rows = new ArrayList<>();
		for (int i = 2; i < table.size(); i++) {
			if (!isCommentRow(table.get(i))) {
				rows.add(new ImportSheetRow(i + 1, table.get(i)));
			}
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

	private static boolean isCommentRow(List<String> row) {
		return !row.isEmpty() && ImportSheetRow.normalize(row.get(0)).startsWith(CSVCommentLineValidator.DEFAULT_COMMENT_LINE_PREFIX);
	}

	private static void dropTrailingBlankRows(List<List<String>> table) {
		while (!table.isEmpty() && new ImportSheetRow(0, table.get(table.size() - 1)).isBlank()) {
			table.remove(table.size() - 1);
		}
	}

	private static void dropErrorColumn(List<List<String>> table) {
		String errorColumn = I18nProperties.getCaption(Captions.importErrorDescription);
		List<String> properties = table.get(1);
		int index = -1;
		for (int i = 0; i < properties.size(); i++) {
			if (ImportSheetRow.normalize(properties.get(i)).equals(errorColumn)) {
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
}
