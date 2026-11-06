package de.symeda.sormas.backend.importexport.hierarchical.caze;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.importexport.hierarchical.engine.ImportNode;
import de.symeda.sormas.backend.util.ValidationProblem;

final class ValidationIssues {

	private static final String COLUMN_SEPARATOR = ", ";

	private ValidationIssues() {
	}

	static void report(ImportNode node, String target, ValidationRuntimeException exception, ColumnHeaders headers) {
		Map<List<String>, String> fieldErrors = exception.getPropertyErrors();
		if (fieldErrors == null || fieldErrors.isEmpty()) {
			node.reportIssue(null, exception.getMessage());
			return;
		}
		List<PlacedProblem> problems = fieldErrors.entrySet()
			.stream()
			.map(error -> place(target, new ValidationProblem(null, error.getValue(), dottedPaths(error.getKey())), headers))
			.sorted(Comparator.comparing(PlacedProblem::column, Comparator.nullsLast(Comparator.naturalOrder())))
			.collect(Collectors.toList());
		report(node, problems);
	}

	static void report(ImportNode node, String target, List<ValidationProblem> problems, ColumnHeaders headers) {
		report(node, problems.stream().map(problem -> place(target, problem, headers)).collect(Collectors.toList()));
	}

	private static void report(ImportNode node, List<PlacedProblem> problems) {
		Set<String> columnsWithIssues = RejectedRecords.columnsWithIssues(node)
			.flatMap(column -> Arrays.stream(column.split(COLUMN_SEPARATOR)))
			.collect(Collectors.toSet());
		problems.stream()
			.filter(problem -> problem.columns.isEmpty() || !columnsWithIssues.containsAll(problem.columns))
			.forEach(problem -> node.reportIssue(problem.column(), ImportProblemWording.reword(problem.problem)));
	}

	private static PlacedProblem place(String target, ValidationProblem problem, ColumnHeaders headers) {
		List<String> columns = problem.getPropertyPaths()
			.stream()
			.map(path -> headers.headerFor(target, path))
			.flatMap(Optional::stream)
			.distinct()
			.collect(Collectors.toList());
		return new PlacedProblem(problem, columns);
	}

	private static String[] dottedPaths(List<String> path) {
		return path == null || path.isEmpty() ? new String[0] : new String[] {
			String.join(".", path) };
	}

	private static final class PlacedProblem {

		private final ValidationProblem problem;
		private final List<String> columns;

		private PlacedProblem(ValidationProblem problem, List<String> columns) {
			this.problem = problem;
			this.columns = columns;
		}

		private String column() {
			return columns.isEmpty() ? null : String.join(COLUMN_SEPARATOR, columns);
		}
	}
}
