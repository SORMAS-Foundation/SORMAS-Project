package de.symeda.sormas.backend.common;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.api.utils.DateHelper;

public interface LimitedChangeDateFilterProvider<ADO extends AbstractDomainObject> {

	default Predicate createLimitedChangeDateFilter(CriteriaBuilder cb, From<?, ADO> from, boolean featureEnabled, Integer maxChangeDatePeriod) {

		if (featureEnabled && maxChangeDatePeriod != null && maxChangeDatePeriod >= 0) {
			Date maxChangeDate = DateHelper.subtractDays(new Date(), maxChangeDatePeriod);
			Timestamp timestamp = Timestamp.from(DateHelper.getStartOfDay(maxChangeDate).toInstant());
			return CriteriaBuilderHelper.and(cb, cb.greaterThanOrEqualTo(from.get(ADO.CHANGE_DATE), timestamp));
		}

		return null;
	}

	default Predicate createEmptyLimitedChangeDateFilter() {
		return null;
	}

	default boolean hasLimitedChangeDateFilterImplementation() {
		return true;
	}

}
