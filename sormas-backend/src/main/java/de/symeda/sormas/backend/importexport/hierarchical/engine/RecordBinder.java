package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.util.List;
import java.util.Map;

public final class RecordBinder {

	private final CellWriter writer;

	public RecordBinder(CellWriter writer) {
		this.writer = writer;
	}

	public void bind(ImportNode node, List<ColumnSpec> columns, Map<String, Object> targets) {
		for (ColumnSpec column : columns) {
			String value = node.cell(column.header());
			if (value.isEmpty() || !column.isProperty()) {
				continue;
			}
			try {
				writer.write(targets, column.target(), column.propertyPath(), column.header(), value);
			} catch (CellWriteException e) {
				node.reportIssue(column.header(), e.getMessage());
			}
		}
	}
}
