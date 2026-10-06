package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueDto;
import de.symeda.sormas.api.importexport.hierarchical.ImportIssueKind;
import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;

final class HeaderCheck {

	private HeaderCheck() {
	}

	static List<ImportIssueDto> check(ImportDefinition definition, ImportSheet sheet) {
		Set<String> known = definition.columns().stream().map(ColumnSpec::header).collect(Collectors.toSet());
		Set<String> seen = new HashSet<>();
		List<ImportIssueDto> issues = new ArrayList<>();
		for (String header : sheet.getHeaders()) {
			if (header.isEmpty()) {
				continue;
			}
			if (!known.contains(header)) {
				issues.add(fileIssue(header, Validations.importHierarchicalUnknownColumn, header));
			} else if (!seen.add(header)) {
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
