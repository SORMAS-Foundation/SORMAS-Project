package de.symeda.sormas.api.importexport.hierarchical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RecordOperationDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String level;
	private final OperationType type;
	private final String label;
	private final int lineNumber;
	private final List<FieldChangeDto> changes;

	public RecordOperationDto(String level, OperationType type, String label, int lineNumber, List<FieldChangeDto> changes) {
		this.level = level;
		this.type = type;
		this.label = label;
		this.lineNumber = lineNumber;
		this.changes = Collections.unmodifiableList(new ArrayList<>(changes));
	}

	public String getLevel() {
		return level;
	}

	public OperationType getType() {
		return type;
	}

	public String getLabel() {
		return label;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public List<FieldChangeDto> getChanges() {
		return changes;
	}
}
