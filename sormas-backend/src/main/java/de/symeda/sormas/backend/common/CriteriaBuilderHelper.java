package de.symeda.sormas.backend.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.uuid.HasUuid;
import de.symeda.sormas.backend.ExtendedPostgreSQL94Dialect;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.ModelConstants;

public class CriteriaBuilderHelper {

	public static Predicate and(CriteriaBuilder cb, Predicate... predicates) {
		return reduce(cb::and, predicates);
	}

	@SafeVarargs
	public static Optional<Predicate> and(CriteriaBuilder cb, Optional<Predicate>... predicates) {
		return reduce(cb::and, Arrays.stream(predicates));
	}

	public static Predicate or(CriteriaBuilder cb, Predicate... predicates) {
		return reduce(cb::or, predicates);
	}

	@SafeVarargs
	public static Optional<Predicate> or(CriteriaBuilder cb, Optional<Predicate>... predicates) {
		return reduce(cb::or, Arrays.stream(predicates));
	}

	static Optional<Predicate> reduce(Function<Predicate[], Predicate> op, Stream<Optional<Predicate>> predicates) {

		Predicate[] cleaned = predicates.filter(Optional::isPresent).map(Optional::get).toArray(Predicate[]::new);
		switch (cleaned.length) {
		case 0:
			return Optional.empty();
		case 1:
			return Optional.of(cleaned[0]);
		default:
			return Optional.of(op.apply(cleaned));
		}
	}

	static Predicate reduce(Function<Predicate[], Predicate> op, Predicate... predicates) {
		return reduce(op, Arrays.stream(predicates).map(Optional::ofNullable)).orElse(null);
	}

	public static Predicate greaterThanAndNotNull(CriteriaBuilder cb, Expression<? extends Date> path, Date date) {
		return cb.and(cb.greaterThan(path, date), cb.isNotNull(path));
	}

	public static Predicate greaterThanOrEqualToAndNotNull(CriteriaBuilder cb, Expression<? extends Date> path, Date date) {
		return cb.and(cb.greaterThanOrEqualTo(path, date), cb.isNotNull(path));
	}

	public static Predicate greaterThanAndNotNull(CriteriaBuilder cb, Expression<? extends Date> path, Expression<? extends Date> date) {
		return cb.and(cb.greaterThan(path, date), cb.isNotNull(path));
	}

	/**
	 * @param cb
	 *            The builder of the query to filter.
	 * @param entityPath
	 *            The path in which {@code entityProperty} will to be applied.
	 * @param filter
	 *            The filter to amend.
	 * @param filterValue
	 *            The value to filter by.
	 * @param entityProperty
	 *            The property on which to filter.
	 * @return The original filter if {@code filterValue == null} or the amended filter combined with AND.
	 */
	public static Predicate andEquals(CriteriaBuilder cb, Path<?> entityPath, Predicate filter, Object filterValue, String entityProperty) {

		return filterValue == null ? filter : and(cb, filter, cb.equal(entityPath.get(entityProperty), filterValue));
	}

	/**
	 * @param cb
	 *            The builder of the query to filter.
	 * @param joinSupplier
	 *            Provides a join that is called if {@code hasUuid != null}. Not executing the {@link Supplier} prevents a superfluous join.
	 * @param filter
	 *            The filter to amend.
	 * @param hasUuid
	 *            The entity or reference object on which to filter.
	 * @return The original filter if {@code hasUuid == null} or the amended filter combined with AND.
	 */
	public static Predicate andEquals(CriteriaBuilder cb, Supplier<Join<?, ?>> joinSupplier, Predicate filter, HasUuid hasUuid) {

		return hasUuid == null ? filter : andEquals(cb, joinSupplier.get(), filter, hasUuid.getUuid(), AbstractDomainObject.UUID);
	}

	public static Predicate andInValues(Collection<?> values, Predicate filter, CriteriaBuilder cb, Path<Object> path) {
		if (CollectionUtils.isEmpty(values)) {
			return filter;
		}

		Predicate or = null;
		for (List<?> batch : ListUtils.partition(new ArrayList<>(values), ModelConstants.PARAMETER_LIMIT)) {
			if (CollectionUtils.isNotEmpty(batch)) {
				or = CriteriaBuilderHelper.or(cb, or, cb.in(path).value(batch));
			}
		}
		return CriteriaBuilderHelper.and(cb, filter, or);
	}

	public static Predicate unaccentedIlike(CriteriaBuilder cb, Expression<String> valueExpression, String pattern) {
		return unaccentedIlike(cb, valueExpression, cb.literal("%" + pattern + "%"));
	}

	public static Predicate unaccentedIlikePrecise(CriteriaBuilder cb, Expression<String> valueExpression, String value) {
		return unaccentedIlike(cb, valueExpression, cb.literal(value));
	}

	public static Predicate unaccentedIlike(CriteriaBuilder cb, Expression<String> valueExpression, Expression<String> patternExpression) {
		Expression<String> unaccentedValueExpression = cb.function(ExtendedPostgreSQL94Dialect.UNACCENT, String.class, valueExpression);
		Expression<String> unaccentedPatternExpression = cb.function(ExtendedPostgreSQL94Dialect.UNACCENT, String.class, patternExpression);
		return ilike(cb, unaccentedValueExpression, unaccentedPatternExpression);
	}

	public static Predicate ilike(CriteriaBuilder cb, Expression<String> valueExpression, String pattern) {
		return ilike(cb, valueExpression, cb.literal("%" + pattern + "%"));
	}

	public static Predicate ilikePrecise(CriteriaBuilder cb, Expression<String> valueExpression, String value) {
		return ilike(cb, valueExpression, cb.literal(value));
	}

	public static Predicate ilike(CriteriaBuilder cb, Expression<String> valueExpression, Expression<String> patternExpression) {
		return cb.isTrue(cb.function(ExtendedPostgreSQL94Dialect.ILIKE, Boolean.class, valueExpression, patternExpression));
	}

	public static Expression<String> windowFirstValueDesc(
		CriteriaBuilder cb,
		Path<Object> valueProperty,
		Path<Object> partitionProperty,
		Path<Object> orderProperty) {
		return cb.function(ExtendedPostgreSQL94Dialect.WINDOW_FIRST_VALUE_DESC, String.class, valueProperty, partitionProperty, orderProperty);
	}

	public static Expression<String> windowCount(CriteriaBuilder cb, Path<Object> valueProperty, Path<Object> partitionProperty) {
		return cb.function(ExtendedPostgreSQL94Dialect.WINDOW_COUNT, String.class, valueProperty, partitionProperty);
	}

	public static Predicate buildFreeTextSearchPredicate(CriteriaBuilder cb, String searchTerm, Function<String, Predicate> createTextFilter) {
		Predicate predicate = cb.conjunction();

		String[] textFilters = searchTerm.split("\\s+");
		for (String textFilter : textFilters) {
			if (DataHelper.isNullOrEmpty(textFilter)) {
				continue;
			}

			predicate = CriteriaBuilderHelper.and(cb, predicate, createTextFilter.apply(textFilter));
		}

		return predicate;
	}

	@SafeVarargs
	public static <T> Expression<T> coalesce(CriteriaBuilder cb, Class<T> type, Expression<T>... expressions) {
		return cb.function("COALESCE", type, expressions);
	}

	@SafeVarargs
	public static Expression<String> coalesce(CriteriaBuilder cb, Expression<String>... expressions) {
		return coalesce(cb, String.class, expressions);
	}

	public static Predicate applyDateFilter(CriteriaBuilder cb, Predicate filter, Path path, Date fromDate, Date toDate) {
		if (fromDate != null && toDate != null) {
			filter = and(cb, filter, cb.between(path, fromDate, toDate));
		} else if (fromDate != null) {
			filter = and(cb, filter, cb.greaterThanOrEqualTo(path, fromDate));
		} else if (toDate != null) {
			filter = and(cb, filter, cb.lessThanOrEqualTo(path, toDate));
		}
		return filter;
	}

	public static Predicate limitedDiseasePredicate(CriteriaBuilder cb, User currentUser, Expression<?> diseaseExpression) {
		return limitedDiseasePredicate(cb, currentUser, diseaseExpression, null);
	}

	public static Predicate limitedDiseasePredicate(CriteriaBuilder cb, User currentUser, Expression<?> diseaseExpression, Predicate orElse) {
		if (currentUser == null || CollectionUtils.isEmpty(currentUser.getLimitedDiseases())) {
			return null;
		}

		return or(cb, diseaseExpression.in(currentUser.getLimitedDiseases()), orElse);
	}

	public static Expression<Double> dateDiff(CriteriaBuilder cb, Expression<?> date1, Expression<?> date2) {
		return cb.abs(
			cb.diff(
				cb.function("date_part", Double.class, cb.literal("epoch"), date1),
				cb.function("date_part", Double.class, cb.literal("epoch"), date2)));
	}
}
