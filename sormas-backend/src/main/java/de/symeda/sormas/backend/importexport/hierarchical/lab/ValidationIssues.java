package de.symeda.sormas.backend.importexport.hierarchical.lab;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.utils.ValidationRuntimeException;
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
				column(target, error.getKey()),
				error.getValue() })
			.sorted(Comparator.comparing(error -> String.valueOf(error[0])))
			.forEach(error -> node.reportIssue(error[0], error[1]));
	}

	private static String column(String target, List<String> path) {
		return path == null || path.isEmpty() ? null : target + "." + String.join(".", path);
	}
}
