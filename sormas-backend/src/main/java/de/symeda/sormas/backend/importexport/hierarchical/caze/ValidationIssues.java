package de.symeda.sormas.backend.importexport.hierarchical.caze;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ColumnSpec;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ImportNode;
import de.symeda.sormas.backend.util.ValidationProblem;

final class ValidationIssues {

	private ValidationIssues() {
	}

	static void report(ImportNode node, String target, ValidationRuntimeException exception, ColumnHeaders headers) {
		Map<List<String>, String> fieldErrors = exception.getPropertyErrors();
		if (fieldErrors == null || fieldErrors.isEmpty()) {
			node.reportIssue(null, exception.getMessage());
			return;
		}
		List<ValidationProblem> problems = fieldErrors.entrySet()
			.stream()
			.map(error -> new ValidationProblem(null, error.getValue(), dottedPaths(error.getKey())))
			.sorted(Comparator.comparing(problem -> String.valueOf(column(node, target, problem.getPropertyPaths(), headers))))
			.collect(Collectors.toList());
		report(node, target, problems, headers);
	}

	static void report(ImportNode node, String target, List<ValidationProblem> problems, ColumnHeaders headers) {
		Set<String> columnsWithIssues = RejectedRecords.columnsWithIssues(node).collect(Collectors.toSet());
		problems.forEach(problem -> {
			List<String> columns = columns(node, target, problem.getPropertyPaths(), headers);
			if (columns.stream().noneMatch(columnsWithIssues::contains)) {
				node.reportIssue(columns.isEmpty() ? null : String.join(", ", columns), problem.getMessage());
			}
		});
	}

	private static String[] dottedPaths(List<String> path) {
		return path == null || path.isEmpty() ? new String[0] : new String[] {
			String.join(".", path) };
	}

	private static String column(ImportNode node, String target, List<String> dottedPaths, ColumnHeaders headers) {
		List<String> columns = columns(node, target, dottedPaths, headers);
		return columns.isEmpty() ? null : String.join(", ", columns);
	}

	private static List<String> columns(ImportNode node, String target, List<String> dottedPaths, ColumnHeaders headers) {
		return dottedPaths.stream().map(path -> header(node, target, path, headers)).distinct().collect(Collectors.toList());
	}

	private static String header(ImportNode node, String target, String dottedPath, ColumnHeaders headers) {
		return headers.headerFor(target, dottedPath).or(() -> fileHeader(node, target, dottedPath)).orElse(target + "." + dottedPath);
	}

	private static Optional<String> fileHeader(ImportNode node, String target, String dottedPath) {
		String[] propertyPath = dottedPath.split("\\.");
		return node.cells()
			.keySet()
			.stream()
			.map(CaseTemplateSchema::resolve)
			.flatMap(Optional::stream)
			.filter(column -> column.target().equals(target) && Arrays.equals(column.propertyPath(), propertyPath))
			.map(ColumnSpec::header)
			.findFirst();
	}
}
