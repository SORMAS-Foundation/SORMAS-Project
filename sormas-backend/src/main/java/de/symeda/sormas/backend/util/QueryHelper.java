package de.symeda.sormas.backend.util;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * Helper methods for building JDBC queries.
 */
public final class QueryHelper {

	private QueryHelper() {
		// Hide Utility Class Constructor
	}

	public static <T> StringBuilder appendInFilterValues(
		StringBuilder filterBuilder,
		List<Object> filterBuilderParameters,
		List<T> values,
		Function<T, ?> valueMapper) {

		filterBuilder.append("(");
		boolean first = true;
		for (T value : values) {
			if (first) {
				filterBuilder.append("?");
				first = false;
			} else {
				filterBuilder.append(",?");
			}
			filterBuilder.append(filterBuilderParameters.size() + 1);
			filterBuilderParameters.add(valueMapper.apply(value));
		}
		filterBuilder.append(")");
		return filterBuilder;
	}

	/**
	 * Joins a list of {@link String} values to a single {@link String} with proper syntax for native SQL IN clauses.
	 */
	public static String concatStrings(List<String> stringValues) {

		String valuesString = stringValues.stream().map(e -> StringUtils.wrap(e, "'")).collect(Collectors.joining(","));
		return valuesString;
	}

	/**
	 * Joins a list of {@link Long} values to a single {@link String} with proper syntax for native SQL IN clauses.
	 */
	public static String concatLongs(List<Long> longValues) {

		String valuesString = StringUtils.join(longValues, ",");
		return valuesString;
	}
}
