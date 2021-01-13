package de.symeda.sormas.backend.common;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

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
}
