package de.symeda.sormas.backend.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ValidationProblem {

	private final String validationKey;
	private final String message;
	private final Object[] messageArgs;
	private final List<String> propertyPaths;

	public ValidationProblem(String validationKey, String message, String... propertyPaths) {
		this(validationKey, message, new Object[0], propertyPaths);
	}

	public ValidationProblem(String validationKey, String message, Object[] messageArgs, String... propertyPaths) {
		this.validationKey = validationKey;
		this.message = message;
		this.messageArgs = messageArgs.clone();
		this.propertyPaths = Collections.unmodifiableList(Arrays.asList(propertyPaths));
	}

	public String getValidationKey() {
		return validationKey;
	}

	public String getMessage() {
		return message;
	}

	public Object[] getMessageArgs() {
		return messageArgs.clone();
	}

	public List<String> getPropertyPaths() {
		return propertyPaths;
	}
}
