package de.symeda.sormas.backend.util;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.i18n.I18nProperties;

public final class ValidationProblems {

	private ValidationProblems() {
	}

	public static boolean isUnreported(List<ValidationProblem> problems, String... paths) {
		List<String> candidates = Arrays.asList(paths);
		return problems.stream().noneMatch(problem -> problem.getPropertyPaths().stream().anyMatch(candidates::contains));
	}

	public static void addProblem(List<ValidationProblem> problems, String validationKey, String... propertyPaths) {
		addProblem(problems, validationKey, new Object[0], propertyPaths);
	}

	public static void addProblem(List<ValidationProblem> problems, String validationKey, Object[] messageArgs, String... propertyPaths) {
		problems.add(new ValidationProblem(validationKey, I18nProperties.getValidationError(validationKey, messageArgs), propertyPaths));
	}
}
