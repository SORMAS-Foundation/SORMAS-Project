package de.symeda.sormas.backend.common;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

public interface EmptyLimitedChangeDateFilterProvider<ADO extends AbstractDomainObject> extends LimitedChangeDateFilterProvider<ADO> {

	@Override
	default Predicate createLimitedChangeDateFilter(CriteriaBuilder cb, From<?, ADO> from, boolean featureEnabled, Integer maxChangeDatePeriod) {
		return createEmptyLimitedChangeDateFilter();
	}

	@Override
	default boolean hasLimitedChangeDateFilterImplementation() {
		return false;
	}
}
