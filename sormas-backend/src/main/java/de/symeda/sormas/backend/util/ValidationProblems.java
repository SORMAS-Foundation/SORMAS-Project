package de.symeda.sormas.backend.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

public final class ValidationProblems {

	private final List<ValidationProblem> problems = new ArrayList<>();
	private final boolean failFast;

	private ValidationProblems(boolean failFast) {
		this.failFast = failFast;
	}

	public static ValidationProblems collecting() {
		return new ValidationProblems(false);
	}

	public static ValidationProblems failingFast() {
		return new ValidationProblems(true);
	}

	public List<ValidationProblem> asList() {
		return problems;
	}

	public boolean isUnreported(String... paths) {
		List<String> candidates = Arrays.asList(paths);
		return problems.stream().noneMatch(problem -> problem.getPropertyPaths().stream().anyMatch(candidates::contains));
	}

	public void add(String validationKey, String... propertyPaths) {
		add(validationKey, new Object[0], propertyPaths);
	}

	public void add(String validationKey, Object[] messageArgs, String... propertyPaths) {
		add(new ValidationProblem(validationKey, I18nProperties.getValidationError(validationKey, messageArgs), messageArgs, propertyPaths));
	}

	public void add(ValidationProblem problem) {
		if (failFast) {
			throw new ValidationRuntimeException(problem.getMessage());
		}
		problems.add(problem);
	}
}
