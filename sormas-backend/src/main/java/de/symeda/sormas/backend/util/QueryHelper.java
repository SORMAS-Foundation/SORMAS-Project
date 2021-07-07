package de.symeda.sormas.backend.util;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;

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

	/**
	 * Executes a query and returns the result. Can be selected down to a definite batch
	 * starting at {@code first} and limited by {@code max}.
	 * 
	 * @param <T>
	 *            Entity, DTO or simple type for the returned list.
	 * @param em
	 *            The {@link EntityManager} to be invoked.
	 * @param cq
	 *            The {@link CriteriaQuery} to be executed.
	 * @param first
	 *            The first entity to be returned (optional).
	 * @param max
	 *            The maximum number of entries to be fetched (optional).
	 * @return
	 */
	public static <T> List<T> getResultList(EntityManager em, CriteriaQuery<T> cq, Integer first, Integer max) {

		final List<T> resultList;
		if (first != null && max != null) {
			resultList = em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
		} else {
			resultList = em.createQuery(cq).getResultList();
		}

		return resultList;
	}

	/**
	 * Executes a query and returns the result. Can be selected down to a definite batch
	 * starting at {@code first} and limited by {@code max}.
	 * 
	 * @param <T>
	 *            Entity, DTO or simple type for the fetched list.
	 * @param <U>
	 *            Type for the returned list.
	 * @param em
	 *            The {@link EntityManager} to be invoked.
	 * @param cq
	 *            The {@link CriteriaQuery} to be executed.
	 * @param first
	 *            The first entity to be returned (optional).
	 * @param max
	 *            The maximum number of entries to be fetched (optional).
	 * @param converter
	 *            Converts the queried objects to another type before returning the list.
	 * @return
	 */
	public static <T, U> List<U> getResultList(EntityManager em, CriteriaQuery<T> cq, Integer first, Integer max, Function<T, U> converter) {

		List<T> resultList = getResultList(em, cq, first, max);
		return resultList.stream().map(converter).collect(Collectors.toList());
	}
}
