package de.symeda.sormas.backend.person;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;

import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.backend.common.QueryContext;

public class PersonQueryContext extends QueryContext<Person, PersonJoins> {

	public static final String PERSON_PHONE_SUBQUERY = "personPhoneSubquery";
	public static final String PERSON_EMAIL_SUBQUERY = "personEmailSubquery";
	public static final String PERSON_PHONE_OWNER_SUBQUERY = "personPhoneOwnerSubquery";
	public static final String PERSON_PRIMARY_OTHER_SUBQUERY = "personPrimaryOtherSubquery";
	public static final String PERSON_OTHER_CONTACT_DETAILS_SUBQUERY = "personOtherContactDetailsSubQuery";

	private Join<Person, PersonContactDetail> phone;
	private Join<Person, PersonContactDetail> email;

	protected PersonQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, Person> root) {
		this(cb, query, new PersonJoins(root));
	}

	public PersonQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, PersonJoins personJoins) {
		super(cb, query, personJoins.getRoot(), personJoins);
	}

	@Override
	protected Expression<?> createExpression(String name) {

		switch (name) {
		case PERSON_PHONE_SUBQUERY:
			return addSubqueryExpression(PERSON_PHONE_SUBQUERY, getPersonContactDetailSubquery(PersonContactDetailType.PHONE, getRoot()));
		case PERSON_EMAIL_SUBQUERY:
			return addSubqueryExpression(PERSON_EMAIL_SUBQUERY, getPersonContactDetailSubquery(PersonContactDetailType.EMAIL, getRoot()));
		case PERSON_PRIMARY_OTHER_SUBQUERY:
			return addSubqueryExpression(PERSON_EMAIL_SUBQUERY, getPersonContactDetailSubquery(PersonContactDetailType.OTHER, getRoot()));
		case PERSON_PHONE_OWNER_SUBQUERY:
			return addSubqueryExpression(PERSON_PHONE_OWNER_SUBQUERY, phoneOwnerSubquery(getRoot()));
		case PERSON_OTHER_CONTACT_DETAILS_SUBQUERY:
			return addSubqueryExpression(PERSON_OTHER_CONTACT_DETAILS_SUBQUERY, getPersonOtherContactDetailsSubQuery(getRoot()));
		default:
			throw new IllegalArgumentException("No such subquery expression defined!");
		}
	}

	public Join<Person, PersonContactDetail> getPhoneJoin() {
		CriteriaBuilder cb = getCriteriaBuilder();
		if (phone == null) {
			phone = getJoins().getPhone();
			phone.on(
				cb.and(
					cb.isTrue(phone.get(PersonContactDetail.PRIMARY_CONTACT)),
					cb.equal(phone.get(PersonContactDetail.PERSON_CONTACT_DETAIL_TYPE), PersonContactDetailType.PHONE)));
		}
		return phone;
	}

	public Join<Person, PersonContactDetail> getEmailAddressJoin() {
		CriteriaBuilder cb = getCriteriaBuilder();
		if (email == null) {
			email = getJoins().getEmailAddress();
			email.on(
				cb.and(
					cb.isTrue(email.get(PersonContactDetail.PRIMARY_CONTACT)),
					cb.equal(email.get(PersonContactDetail.PERSON_CONTACT_DETAIL_TYPE), PersonContactDetailType.EMAIL)));
		}
		return email;
	}
}
