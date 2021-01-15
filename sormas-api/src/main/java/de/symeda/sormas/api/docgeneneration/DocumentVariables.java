package de.symeda.sormas.api.docgeneneration;

import java.util.Set;

public class DocumentVariables {

	private final Set<String> variables;
	private final Set<String> nullableVariables;

	public DocumentVariables(Set<String> variables, Set<String> nullableVariables) {
		this.variables = variables;
		this.nullableVariables = nullableVariables;
	}

	public Set<String> getVariables() {
		return variables;
	}

	public Set<String> getNullableVariables() {
		return nullableVariables;
	}

	public boolean isNullableVariable(String variable) {
		return variables.contains(variable) && nullableVariables.contains(variable);
	}
}
