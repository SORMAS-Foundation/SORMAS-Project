package de.symeda.sormas.ui.importer.hierarchical;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.symeda.sormas.api.importexport.hierarchical.CaseTemplateLayout;

public final class ColumnLabels {

	private static final String SEPARATOR = ", ";

	private final Map<String, String> labelsByHeader;

	private ColumnLabels(Map<String, String> labelsByHeader) {
		this.labelsByHeader = labelsByHeader;
	}

	public static ColumnLabels of(List<String> headers) {
		Map<String, Long> propertyCounts =
			headers.stream().map(CaseTemplateLayout::propertyOf).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		return new ColumnLabels(
			headers.stream()
				.distinct()
				.collect(
					Collectors.toMap(
						Function.identity(),
						header -> propertyCounts.get(CaseTemplateLayout.propertyOf(header)) == 1 ? CaseTemplateLayout.propertyOf(header) : header)));
	}

	public String label(String column) {
		if (column == null || column.isEmpty()) {
			return column;
		}
		return Arrays.stream(column.split(SEPARATOR)).map(header -> labelsByHeader.getOrDefault(header, header)).collect(Collectors.joining(SEPARATOR));
	}
}
