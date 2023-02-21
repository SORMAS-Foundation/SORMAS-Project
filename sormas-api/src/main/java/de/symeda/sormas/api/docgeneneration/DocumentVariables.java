package de.symeda.sormas.api.docgeneneration;

import de.symeda.sormas.api.audit.AuditIncludeProperty;
import de.symeda.sormas.api.audit.AuditedClass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AuditedClass
public class DocumentVariables implements Serializable {

	private final Set<String> variables;
	private final Set<String> nullableVariables;
	private List<String> additionalVariables = new ArrayList<>();
	private Set<String> usedEntities = new HashSet<>();

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

	public void addUsedEntity(String entityName) {
		if (entityName != null) {
			usedEntities.add(entityName.toLowerCase());
		}
	}

	public boolean isUsedEntity(String entityName) {
		return entityName != null && usedEntities.contains(entityName.toLowerCase());
	}

	@AuditIncludeProperty
	public Set<String> getUsedEntities() {
		return usedEntities;
	}

	public void setAdditionalVariables(List<String> additionalVariables) {
		this.additionalVariables = additionalVariables;
	}

	public List<String> getAdditionalVariables() {
		return additionalVariables;
	}

	public boolean isNullableVariable(String variable) {
		return variables.contains(variable) && nullableVariables.contains(variable);
	}
}
