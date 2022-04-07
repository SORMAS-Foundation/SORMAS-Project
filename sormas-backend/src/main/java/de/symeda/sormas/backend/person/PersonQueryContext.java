package de.symeda.sormas.backend.person;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;

import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.backend.common.QueryContext;

public class PersonQueryContext extends QueryContext<Person, PersonJoins> {

	public static final String PERSON_PHONE_SUBQUERY = "personPhoneSubquery";
	public static final String PERSON_EMAIL_SUBQUERY = "personEmailSubquery";
	public static final String PERSON_PHONE_OWNER_SUBQUERY = "personPhoneOwnerSubquery";
	public static final String PERSON_OTHER_CONTACT_DETAILS_SUBQUERY = "personOtherContactDetailsSubQuery";

	public PersonQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, Person> root) {
		super(cb, query, root, new PersonJoins(root));
	}

	@Override
	protected Expression<?> createExpression(String name) {

		switch (name) {
		case PERSON_PHONE_SUBQUERY:
			return addSubqueryExpression(PERSON_PHONE_SUBQUERY, getPersonContactDetailSubquery(PersonContactDetailType.PHONE, getRoot()));
		case PERSON_EMAIL_SUBQUERY:
			return addSubqueryExpression(PERSON_EMAIL_SUBQUERY, getPersonContactDetailSubquery(PersonContactDetailType.EMAIL, getRoot()));
		case PERSON_PHONE_OWNER_SUBQUERY:
			return addSubqueryExpression(PERSON_PHONE_OWNER_SUBQUERY, phoneOwnerSubquery(getRoot()));
		case PERSON_OTHER_CONTACT_DETAILS_SUBQUERY:
			return addSubqueryExpression(PERSON_OTHER_CONTACT_DETAILS_SUBQUERY, getPersonOtherContactDetailsSubQuery(getRoot()));
		default:
			throw new IllegalArgumentException("No such subquery expression defined!");
		}
	}
}
