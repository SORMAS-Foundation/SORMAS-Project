package de.symeda.sormas.api.importexport.hierarchical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.symeda.sormas.api.audit.AuditedClass;

@AuditedClass
public final class ImportSheet implements Serializable {

	private static final long serialVersionUID = 1L;

	private final List<String> headers;
	private final List<ImportSheetRow> rows;

	public ImportSheet(List<String> headers, List<ImportSheetRow> rows) {
		this.headers = Collections.unmodifiableList(headers.stream().map(ImportSheetRow::normalize).collect(Collectors.toList()));
		this.rows = Collections.unmodifiableList(new ArrayList<>(rows));
	}

	public List<String> getHeaders() {
		return headers;
	}

	public List<ImportSheetRow> getRows() {
		return rows;
	}
}
