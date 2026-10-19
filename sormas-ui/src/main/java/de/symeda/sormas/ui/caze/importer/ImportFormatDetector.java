package de.symeda.sormas.ui.caze.importer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.symeda.sormas.api.importexport.hierarchical.CaseTemplateLayout;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheetRow;

public final class ImportFormatDetector {

	public enum ImportFormat {
		CLASSIC,
		NESTED
	}

	private ImportFormatDetector() {
	}

	public static ImportFormat detect(ImportSheet sheet) {
		List<String> headers = sheet.getHeaders();
		if (repeatsAChildGroup(headers)) {
			return ImportFormat.CLASSIC;
		}
		List<Integer> caseColumns = columnsOf(headers, CaseTemplateLayout.CASE);
		List<Integer> childColumns = columnsOf(headers, CaseTemplateLayout.SAMPLE);
		childColumns.addAll(columnsOf(headers, CaseTemplateLayout.TEST));
		boolean continues = sheet.getRows().stream().anyMatch(row -> allBlank(row, caseColumns) && !allBlank(row, childColumns));
		return continues ? ImportFormat.NESTED : ImportFormat.CLASSIC;
	}

	private static boolean repeatsAChildGroup(List<String> headers) {
		Set<String> seen = new HashSet<>();
		return headers.stream().filter(header -> isChildLevel(levelOf(header))).anyMatch(header -> !seen.add(header));
	}

	private static boolean isChildLevel(String level) {
		return CaseTemplateLayout.SAMPLE.equals(level) || CaseTemplateLayout.TEST.equals(level);
	}

	private static List<Integer> columnsOf(List<String> headers, String level) {
		List<Integer> columns = new ArrayList<>();
		for (int i = 0; i < headers.size(); i++) {
			if (level.equals(levelOf(headers.get(i)))) {
				columns.add(i);
			}
		}
		return columns;
	}

	private static String levelOf(String header) {
		return CaseTemplateLayout.levelOf(CaseTemplateLayout.entityOf(header)).orElse("");
	}

	private static boolean allBlank(ImportSheetRow row, List<Integer> columns) {
		return columns.stream().allMatch(index -> row.cell(index).isEmpty());
	}
}
