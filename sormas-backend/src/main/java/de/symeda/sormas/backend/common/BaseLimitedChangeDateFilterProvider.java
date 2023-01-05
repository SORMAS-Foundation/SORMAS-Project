package de.symeda.sormas.backend.common;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

public interface BaseLimitedChangeDateFilterProvider<ADO extends AbstractDomainObject> {

	default Predicate createLimitedChangeDateFilter(CriteriaBuilder cb, From<?, ADO> from, boolean featureEnabled, Integer maxChangeDatePeriod) {
		return createEmptyLimitedChangeDateFilter();
	}

	default Predicate createEmptyLimitedChangeDateFilter() {
		return null;
	}

	default boolean hasLimitedChangeDateFilterImplementation() {
		return false;
	}
}
