package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueKind;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;

final class HeaderCheck {

	private HeaderCheck() {
	}

	static List<ImportIssueDto> check(ImportDefinition definition, ImportSheet sheet, SheetColumns sheetColumns) {
		List<ImportIssueDto> issues = new ArrayList<>();
		sheetColumns.unknown().forEach(header -> issues.add(fileIssue(header, Validations.importHierarchicalUnknownColumn, header)));
		Set<String> seen = new HashSet<>();
		for (String header : sheet.getHeaders()) {
			if (!header.isEmpty() && !seen.add(header)) {
				issues.add(fileIssue(header, Validations.importHierarchicalDuplicateColumn, header));
			}
		}
		if (sheet.getRows().size() > definition.maxRows()) {
			issues.add(fileIssue(null, Validations.importHierarchicalTooManyRows, sheet.getRows().size(), definition.maxRows()));
		}
		return issues;
	}

	private static ImportIssueDto fileIssue(String column, String key, Object... arguments) {
		return new ImportIssueDto(ImportIssueKind.FILE, null, column, I18nProperties.getValidationError(key, arguments));
	}
}
