package de.symeda.sormas.api.importexport.hierarchical;

import java.io.Serializable;

public final class ImportIssueDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private final ImportIssueKind kind;
	private final Integer lineNumber;
	private final String column;
	private final String message;

	public ImportIssueDto(ImportIssueKind kind, Integer lineNumber, String column, String message) {
		this.kind = kind;
		this.lineNumber = lineNumber;
		this.column = column;
		this.message = message;
	}

	public ImportIssueKind getKind() {
		return kind;
	}

	public Integer getLineNumber() {
		return lineNumber;
	}

	public String getColumn() {
		return column;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return kind + "@" + lineNumber + " " + column + ": " + message;
	}
}
