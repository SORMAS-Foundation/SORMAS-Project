package de.symeda.sormas.backend.common.sort;

import java.util.Arrays;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import org.jetbrains.annotations.NotNull;

/**
 * Utility class to be able to create sorting criterias on entities without using switch cases.
 *
 * Example:
 *
 * public static final Map<String, BiFunction<CriteriaBuilder, Root<DiseaseConfiguration>, Expression<?>>> SORTABLE_FIELDS_DICTIONARY =
 * EntitySortUtils.defaultSort(
 * DiseaseConfiguration.DISEASE,
 * DiseaseConfiguration.ACTIVE,
 * DiseaseConfiguration.PRIMARY_DISEASE,
 * DiseaseConfiguration.CASE_SURVEILLANCE_ENABLED,
 * DiseaseConfiguration.AGGREGATE_REPORTING_ENABLED,
 * DiseaseConfiguration.FOLLOW_UP_ENABLED,
 * DiseaseConfiguration.FOLLOW_UP_DURATION,
 * DiseaseConfiguration.CASE_FOLLOW_UP_DURATION,
 * DiseaseConfiguration.EVENT_PARTICIPANT_FOLLOW_UP_DURATION,
 * DiseaseConfiguration.EXTENDED_CLASSIFICATION,
 * DiseaseConfiguration.EXTENDED_CLASSIFICATION_MULTI,
 * DiseaseConfiguration.AUTOMATIC_SAMPLE_ASSIGNMENT_THRESHOLD);
 */
public class EntitySortUtils {

	private EntitySortUtils() {
	}

	/**
	 * Will use the field name itself for sorting without any transformation.
	 * 
	 * @param fieldNames
	 *            name of the filterable fields (must be exhaustive)
	 * @return dictionary with key being a field and value being a bifunction returning the value without transformations.
	 * @param <T>
	 *            Type information is important for Hibernate to prepare the adequate statements
	 */
	public static <T> Map<String, BiFunction<CriteriaBuilder, Root<T>, Expression<?>>> defaultSort(String... fieldNames) {
		return Arrays.stream(fieldNames).collect(Collectors.toMap(fieldName -> fieldName, defaultSort()));
	}

	/**
	 * Will use the field name itself for sorting by returning the lowercase values of the fields.
	 *
	 * @param fieldNames
	 *            name of the filterable fields (must be exhaustive)
	 * @return dictionary with key being a field and value being a bifunction returning the lower case value.
	 * @param <T>
	 *            Type information is important for Hibernate to prepare the adequate statements
	 */
	public static <T> Map<String, BiFunction<CriteriaBuilder, Root<T>, Expression<?>>> lowerCaseSort(String... fieldNames) {
		return Arrays.stream(fieldNames).collect(Collectors.toMap(fieldName -> fieldName, lowerCaseSort()));
	}

	public static @NotNull <T> Function<String, BiFunction<CriteriaBuilder, Root<T>, Expression<?>>> defaultSort() {
		return fieldName -> (criteriaBuilder, root) -> root.get(fieldName);
	}

	public static @NotNull <T> Function<String, BiFunction<CriteriaBuilder, Root<T>, Expression<?>>> lowerCaseSort() {
		return fieldName -> (criteriaBuilder, root) -> criteriaBuilder.lower(root.get(fieldName));
	}
}
