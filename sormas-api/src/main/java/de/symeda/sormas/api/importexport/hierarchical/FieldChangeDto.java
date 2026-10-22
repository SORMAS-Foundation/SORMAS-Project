package de.symeda.sormas.api.importexport.hierarchical;

import java.io.Serializable;

public final class FieldChangeDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String field;
	private final String oldValue;
	private final String newValue;

	public FieldChangeDto(String field, String oldValue, String newValue) {
		this.field = field;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public String getField() {
		return field;
	}

	public String getOldValue() {
		return oldValue;
	}

	public String getNewValue() {
		return newValue;
	}
}
