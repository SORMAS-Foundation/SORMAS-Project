package de.symeda.sormas.backend.util;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.person.Person;

public class BirthdateRangeFilterPredicate {

	public static Predicate createBirthdateRangeFilter(
		Date birthdateFrom,
		Date birthdateTo,
		boolean includePartialMatch,
		CriteriaBuilder cb,
		From<?, Person> personFrom,
		Predicate filter) {
		if (birthdateFrom != null) {
			Calendar calendarBirthdateFrom = Calendar.getInstance();
			calendarBirthdateFrom.setTime(birthdateFrom);
			int birthdateFromCriteriaYear = calendarBirthdateFrom.get(Calendar.YEAR);
			int birthdateFromCriteriaMonth = calendarBirthdateFrom.get(Calendar.MONTH) + 1;
			int birthdateFromCriteriaDay = calendarBirthdateFrom.get(Calendar.DAY_OF_MONTH);

			Predicate yearPredicate = cb.greaterThan(personFrom.get(Person.BIRTHDATE_YYYY), birthdateFromCriteriaYear);

			Predicate monthPredicate = cb.equal(personFrom.get(Person.BIRTHDATE_YYYY), birthdateFromCriteriaYear);
			monthPredicate = cb.and(monthPredicate, cb.greaterThan(personFrom.get(Person.BIRTHDATE_MM), birthdateFromCriteriaMonth));

			Predicate dayPredicate = cb.equal(personFrom.get(Person.BIRTHDATE_YYYY), birthdateFromCriteriaYear);
			dayPredicate = cb.and(dayPredicate, cb.equal(personFrom.get(Person.BIRTHDATE_MM), birthdateFromCriteriaMonth));
			dayPredicate = cb.and(dayPredicate, cb.greaterThanOrEqualTo(personFrom.get(Person.BIRTHDATE_DD), birthdateFromCriteriaDay));

			if (includePartialMatch) {
				Predicate sameYearPartialMatchPredicate = cb
					.and(cb.equal(personFrom.get(Person.BIRTHDATE_YYYY), birthdateFromCriteriaYear), cb.isNull(personFrom.get(Person.BIRTHDATE_MM)));
				Predicate sameYearMonthPartialMatchPredicate = cb.and(
					cb.equal(personFrom.get(Person.BIRTHDATE_YYYY), birthdateFromCriteriaYear),
					cb.equal(personFrom.get(Person.BIRTHDATE_MM), birthdateFromCriteriaMonth),
					cb.isNull(personFrom.get(Person.BIRTHDATE_DD)));
				filter = CriteriaBuilderHelper.and(
					cb,
					filter,
					cb.or(yearPredicate, monthPredicate, dayPredicate, sameYearPartialMatchPredicate, sameYearMonthPartialMatchPredicate));
			} else {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.or(yearPredicate, monthPredicate, dayPredicate));
			}
		}

		if (birthdateTo != null) {
			Calendar calendarBirthdateTo = Calendar.getInstance();
			calendarBirthdateTo.setTime(birthdateTo);
			int birthdateToCriteriaYear = calendarBirthdateTo.get(Calendar.YEAR);
			int birthdateToCriteriaMonth = calendarBirthdateTo.get(Calendar.MONTH) + 1;
			int birthdateToCriteriaDay = calendarBirthdateTo.get(Calendar.DAY_OF_MONTH);

			Predicate yearPredicate = cb.lessThan(personFrom.get(Person.BIRTHDATE_YYYY), birthdateToCriteriaYear);

			Predicate monthPredicate = cb.equal(personFrom.get(Person.BIRTHDATE_YYYY), birthdateToCriteriaYear);
			monthPredicate = cb.and(monthPredicate, cb.lessThan(personFrom.get(Person.BIRTHDATE_MM), birthdateToCriteriaMonth));

			Predicate dayPredicate = cb.equal(personFrom.get(Person.BIRTHDATE_YYYY), birthdateToCriteriaYear);
			dayPredicate = cb.and(dayPredicate, cb.equal(personFrom.get(Person.BIRTHDATE_MM), birthdateToCriteriaMonth));
			dayPredicate = cb.and(dayPredicate, cb.lessThanOrEqualTo(personFrom.get(Person.BIRTHDATE_DD), birthdateToCriteriaDay));

			if (includePartialMatch) {
				Predicate sameYearPartialMatchPredicate =
					cb.and(cb.equal(personFrom.get(Person.BIRTHDATE_YYYY), birthdateToCriteriaYear), cb.isNull(personFrom.get(Person.BIRTHDATE_MM)));
				Predicate sameYearMonthPartialMatchPredicate = cb.and(
					cb.equal(personFrom.get(Person.BIRTHDATE_YYYY), birthdateToCriteriaYear),
					cb.equal(personFrom.get(Person.BIRTHDATE_MM), birthdateToCriteriaMonth),
					cb.isNull(personFrom.get(Person.BIRTHDATE_DD)));
				filter = CriteriaBuilderHelper.and(
					cb,
					filter,
					cb.or(yearPredicate, monthPredicate, dayPredicate, sameYearPartialMatchPredicate, sameYearMonthPartialMatchPredicate));
			} else {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.or(yearPredicate, monthPredicate, dayPredicate));
			}
		}
		return filter;
	}
}
