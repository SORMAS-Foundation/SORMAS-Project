package de.symeda.sormas.backend.caze;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;

import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.backend.common.QueryContext;
import de.symeda.sormas.utils.CaseJoins;

public class CaseQueryContext<T> extends QueryContext<T, Case> {

	public static final String PERSON_PHONE_SUBQUERY = "personPhoneSubquery";
	public static final String PERSON_PHONE_OWNER_SUBQUERY = "personPhoneOwnerSubquery";
	public static final String PERSON_EMAIL_SUBQUERY = "personEmailSubquery";

	public CaseQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, From<T, Case> root) {
		super(cb, query, root, new CaseJoins<>(root));
	}

	@Override
	protected Expression<?> createExpression(String name) {

		if (name.equals(PERSON_PHONE_SUBQUERY)) {
			return addSubqueryExpression(
				PERSON_PHONE_SUBQUERY,
				getPersonContactDetailSubquery(PersonContactDetailType.PHONE, ((CaseJoins) getJoins()).getPerson()));
		} else if (name.equals(PERSON_EMAIL_SUBQUERY)) {
			return addSubqueryExpression(
				PERSON_EMAIL_SUBQUERY,
				getPersonContactDetailSubquery(PersonContactDetailType.EMAIL, ((CaseJoins) getJoins()).getPerson()));
		} else if (name.equals(PERSON_PHONE_OWNER_SUBQUERY)) {
			return addSubqueryExpression(
				PERSON_PHONE_OWNER_SUBQUERY,
					phoneOwnerSubquery(((CaseJoins) getJoins()).getPerson()));
		}
		throw new IllegalArgumentException("No such subquery exression defined!");
	}
}
