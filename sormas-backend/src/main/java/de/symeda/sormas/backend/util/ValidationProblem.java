package de.symeda.sormas.backend.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ValidationProblem {

	private final String validationKey;
	private final String message;
	private final List<String> propertyPaths;

	public ValidationProblem(String validationKey, String message, String... propertyPaths) {
		this.validationKey = validationKey;
		this.message = message;
		this.propertyPaths = Collections.unmodifiableList(Arrays.asList(propertyPaths));
	}

	public String getValidationKey() {
		return validationKey;
	}

	public String getMessage() {
		return message;
	}

	public List<String> getPropertyPaths() {
		return propertyPaths;
	}
}
