package de.symeda.sormas.backend.importexport.hierarchical.caze;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.symeda.sormas.backend.importexport.hierarchical.engine.ColumnSpec;

final class ColumnHeaders {

	private final Map<String, String> headersByProperty = new LinkedHashMap<>();

	private ColumnHeaders(List<ColumnSpec> columns) {
		columns.stream()
			.filter(ColumnSpec::isProperty)
			.forEach(column -> headersByProperty.putIfAbsent(key(column.target(), String.join(".", column.propertyPath())), column.header()));
	}

	static ColumnHeaders of(List<ColumnSpec> columns) {
		return new ColumnHeaders(columns);
	}

	Optional<String> headerFor(String target, String dottedPath) {
		return Optional.ofNullable(headersByProperty.get(key(target, dottedPath)));
	}

	private static String key(String target, String dottedPath) {
		return target + ":" + dottedPath;
	}
}
