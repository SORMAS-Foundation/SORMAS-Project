package de.symeda.sormas.backend.person;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;

import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.backend.common.QueryContext;

public class PersonQueryContext<T> extends QueryContext<T, Person> {

	public static final String PERSON_PHONE_SUBQUERY = "personPhoneSubquery";
	public static final String PERSON_EMAIL_SUBQUERY = "personEmailSubquery";

	public PersonQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, From<T, Person> root) {
		super(cb, query, root, null);
	}

	@Override
	protected Expression<?> createExpression(String name) {

		if (name.equals(PERSON_PHONE_SUBQUERY)) {
			return addSubqueryExpression(PERSON_PHONE_SUBQUERY, getPersonContactDetailSubquery(PersonContactDetailType.PHONE, getRoot()));
		} else if (name.equals(PERSON_EMAIL_SUBQUERY)) {
			return addSubqueryExpression(PERSON_EMAIL_SUBQUERY, getPersonContactDetailSubquery(PersonContactDetailType.EMAIL, getRoot()));
		}
		throw new IllegalArgumentException("No such subquery exression defined!");
	}
}
