package de.symeda.sormas.backend.contact;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;

import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.backend.common.QueryContext;

public class ContactQueryContext<T> extends QueryContext<T, Contact> {

	public static final String PERSON_PHONE_SUBQUERY = "personPhoneSubquery";
	public static final String PERSON_PHONE_OWNER_SUBQUERY = "personPhoneOwnerSubquery";
	public static final String PERSON_EMAIL_SUBQUERY = "personEmailSubquery";

	public ContactQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, From<T, Contact> root) {
		super(cb, query, root, new ContactJoins(root));
	}

	@Override
	protected Expression<?> createExpression(String name) {
		if (name.equals(PERSON_PHONE_SUBQUERY)) {
			return addSubqueryExpression(
				PERSON_PHONE_SUBQUERY,
				getPersonContactDetailSubquery(PersonContactDetailType.PHONE, ((ContactJoins) getJoins()).getPerson()));
		} else if (name.equals(PERSON_EMAIL_SUBQUERY)) {
			return addSubqueryExpression(
				PERSON_EMAIL_SUBQUERY,
				getPersonContactDetailSubquery(PersonContactDetailType.EMAIL, ((ContactJoins) getJoins()).getPerson()));
		} else if (name.equals(PERSON_PHONE_OWNER_SUBQUERY)) {
			return addSubqueryExpression(PERSON_PHONE_OWNER_SUBQUERY, phoneOwnerSubquery(((ContactJoins) getJoins()).getPerson()));
		}
		throw new IllegalArgumentException("No such subquery expression defined!");
	}
}
