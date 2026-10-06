package de.symeda.sormas.api.importexport.hierarchical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ImportSheetRow implements Serializable {

	private static final long serialVersionUID = 1L;

	private final int lineNumber;
	private final List<String> cells;

	public ImportSheetRow(int lineNumber, List<String> cells) {
		this.lineNumber = lineNumber;
		this.cells = Collections.unmodifiableList(new ArrayList<>(cells));
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public List<String> getCells() {
		return cells;
	}

	public String cell(int index) {
		return index < cells.size() ? normalize(cells.get(index)) : "";
	}

	public boolean isBlank() {
		return cells.stream().allMatch(value -> normalize(value).isEmpty());
	}

	public static String normalize(String value) {
		return value == null ? "" : value.replace("\uFEFF", "").replace('\u00A0', ' ').strip();
	}
}
