package de.symeda.sormas.backend.common;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.backend.util.ModelConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

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

	public static Predicate greaterThanAndNotNull(CriteriaBuilder cb, Expression<? extends Timestamp> path, Timestamp date) {
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
}
