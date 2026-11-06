package de.symeda.sormas.backend.importexport.hierarchical.caze;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import de.symeda.sormas.backend.importexport.hierarchical.engine.ColumnSpec;

final class ColumnHeaders {

	private final Supplier<List<ColumnSpec>> columns;
	private Map<String, String> headersByProperty;

	private ColumnHeaders(Supplier<List<ColumnSpec>> columns) {
		this.columns = columns;
	}

	static ColumnHeaders of(List<ColumnSpec> columns) {
		return new ColumnHeaders(() -> columns);
	}

	static ColumnHeaders lazily(Supplier<List<ColumnSpec>> columns) {
		return new ColumnHeaders(columns);
	}

	Optional<String> headerFor(String target, String dottedPath) {
		return Optional.ofNullable(headersByProperty().get(key(target, dottedPath)));
	}

	private Map<String, String> headersByProperty() {
		if (headersByProperty == null) {
			headersByProperty = new LinkedHashMap<>();
			columns.get()
				.stream()
				.filter(ColumnSpec::isProperty)
				.forEach(column -> headersByProperty.putIfAbsent(key(column.target(), String.join(".", column.propertyPath())), column.header()));
		}
		return headersByProperty;
	}

	private static String key(String target, String dottedPath) {
		return target + ":" + dottedPath;
	}
}
