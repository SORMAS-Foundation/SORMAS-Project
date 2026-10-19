package de.symeda.sormas.backend.importexport.hierarchical.engine;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import de.symeda.sormas.api.importexport.hierarchical.ImportSheet;

public final class SheetColumns {

	private final List<ColumnSpec> columns;
	private final List<String> ignored;
	private final List<String> unknown;

	private SheetColumns(List<ColumnSpec> columns, List<String> ignored, List<String> unknown) {
		this.columns = columns;
		this.ignored = ignored;
		this.unknown = unknown;
	}

	public static SheetColumns of(ImportDefinition definition, ImportSheet sheet) {
		List<ColumnSpec> columns = new ArrayList<>(definition.columns());
		Set<String> known = columns.stream().map(ColumnSpec::header).collect(Collectors.toSet());
		List<String> ignored = new ArrayList<>();
		List<String> unknown = new ArrayList<>();
		for (String header : new LinkedHashSet<>(sheet.getHeaders())) {
			if (header.isEmpty() || known.contains(header)) {
				continue;
			}
			Optional<ColumnSpec> resolved = definition.resolveColumn(header);
			if (resolved.isPresent()) {
				columns.add(resolved.get());
			} else if (definition.ignoresUnknownColumns()) {
				ignored.add(header);
			} else {
				unknown.add(header);
			}
		}
		return new SheetColumns(columns, ignored, unknown);
	}

	public List<ColumnSpec> columns() {
		return columns;
	}

	public List<String> ignored() {
		return ignored;
	}

	List<String> unknown() {
		return unknown;
	}
}
