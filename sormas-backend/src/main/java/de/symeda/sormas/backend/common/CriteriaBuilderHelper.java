package de.symeda.sormas.backend.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.ExtendedPostgreSQL94Dialect;
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

	public static Predicate greaterThanAndNotNull(CriteriaBuilder cb, Expression<? extends Date> path, Expression<? extends Date> date) {
		return cb.and(cb.greaterThan(path, date), cb.isNotNull(path));
	}

	public static Predicate andEquals(
		CriteriaBuilder cb,
		From<?, ? extends AbstractDomainObject> entityFrom,
		Predicate filter,
		Object filterValue,
		String entityProperty) {
		if (filterValue != null) {
			filter = and(cb, filter, cb.equal(entityFrom.get(entityProperty), filterValue));
		}
		return filter;
	}

	public static Predicate andEqualsReferenceDto(
		CriteriaBuilder cb,
		Join<? extends AbstractDomainObject, ? extends AbstractDomainObject> from,
		Predicate filter,
		ReferenceDto referenceDto) {
		if (referenceDto != null) {
			filter = andEquals(cb, from, filter, referenceDto.getUuid(), AbstractDomainObject.UUID);
		}
		return filter;
	}

	public static Predicate andInValues(Collection<String> values, Predicate filter, CriteriaBuilder cb, Path<Object> path) {
		if (CollectionUtils.isEmpty(values)) {
			return filter;
		}

		Predicate or = null;
		for (List<String> batch : ListUtils.partition(new ArrayList<>(values), ModelConstants.PARAMETER_LIMIT)) {
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
}
