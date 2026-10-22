package de.symeda.sormas.backend.importexport.hierarchical.caze;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ColumnSpec;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ImportNode;

final class ValidationIssues {

	private ValidationIssues() {
	}

	static void report(ImportNode node, String target, ValidationRuntimeException exception) {
		Map<List<String>, String> fieldErrors = exception.getPropertyErrors();
		if (fieldErrors == null || fieldErrors.isEmpty()) {
			node.reportIssue(null, exception.getMessage());
			return;
		}
		fieldErrors.entrySet()
			.stream()
			.map(error -> new String[] {
				column(node, target, error.getKey()),
				error.getValue() })
			.sorted(Comparator.comparing(error -> String.valueOf(error[0])))
			.forEach(error -> node.reportIssue(error[0], error[1]));
	}

	private static String column(ImportNode node, String target, List<String> path) {
		if (path == null || path.isEmpty()) {
			return null;
		}
		String[] propertyPath = path.toArray(new String[0]);
		return node.cells()
			.keySet()
			.stream()
			.map(CaseTemplateSchema::resolve)
			.flatMap(Optional::stream)
			.filter(column -> column.target().equals(target) && Arrays.equals(column.propertyPath(), propertyPath))
			.map(ColumnSpec::header)
			.findFirst()
			.orElse(target + "." + String.join(".", path));
	}
}
