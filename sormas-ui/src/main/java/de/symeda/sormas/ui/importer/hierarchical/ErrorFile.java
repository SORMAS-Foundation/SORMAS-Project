package de.symeda.sormas.ui.importer.hierarchical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.importexport.hierarchical.CaseTemplateLayout;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheetRow;

public final class ErrorFile {

	public static final class FailedBlock {

		private final int firstLine;
		private final int lastLine;
		private final List<ImportIssueDto> issues;

		public FailedBlock(int firstLine, int lastLine, List<ImportIssueDto> issues) {
			this.firstLine = firstLine;
			this.lastLine = lastLine;
			this.issues = issues;
		}
	}

	private ErrorFile() {
	}

	public static List<String[]> rows(ImportSheet sheet, List<FailedBlock> failedBlocks, List<ImportIssueDto> looseIssues) {
		Map<Integer, List<String>> messages = new HashMap<>();
		Set<Integer> lines = new LinkedHashSet<>();
		for (FailedBlock block : failedBlocks) {
			for (int line = block.firstLine; line <= block.lastLine; line++) {
				lines.add(line);
			}
			for (ImportIssueDto issue : block.issues) {
				int line = issue.getLineNumber() != null ? issue.getLineNumber() : block.firstLine;
				messages.computeIfAbsent(line, l -> new ArrayList<>()).add(issue.getMessage());
			}
		}
		for (ImportIssueDto issue : looseIssues) {
			lines.add(issue.getLineNumber());
			messages.computeIfAbsent(issue.getLineNumber(), l -> new ArrayList<>()).add(issue.getMessage());
		}

		List<String[]> rows = new ArrayList<>();
		List<String> headers = sheet.getHeaders();
		rows.add(withMessage("", headers.stream().map(CaseTemplateLayout::entityOf).collect(Collectors.toList())));
		rows.add(
			withMessage(
				I18nProperties.getCaption(Captions.importErrorDescription),
				headers.stream().map(CaseTemplateLayout::propertyOf).collect(Collectors.toList())));
		for (ImportSheetRow row : sheet.getRows()) {
			if (lines.contains(row.getLineNumber())) {
				List<String> cells = new ArrayList<>();
				for (int i = 0; i < sheet.getHeaders().size(); i++) {
					cells.add(row.cell(i));
				}
				rows.add(withMessage(String.join(" / ", messages.getOrDefault(row.getLineNumber(), List.of())), cells));
			}
		}
		return rows;
	}

	private static String[] withMessage(String message, List<String> cells) {
		String[] row = new String[cells.size() + 1];
		row[0] = message;
		for (int i = 0; i < cells.size(); i++) {
			row[i + 1] = cells.get(i);
		}
		return row;
	}
}
