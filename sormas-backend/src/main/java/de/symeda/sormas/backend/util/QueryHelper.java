package de.symeda.sormas.backend.util;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * Helper methods for building JDBC queries.
 */
public final class QueryHelper {

	private QueryHelper() {
		// Hide Utility Class Constructor
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
