package de.symeda.sormas.backend.common;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonContactDetail;
import de.symeda.sormas.backend.util.AbstractDomainObjectJoins;

public abstract class QueryContext<T, ADO extends AbstractDomainObject> {

	private CriteriaQuery<?> query;
	private CriteriaBuilder criteriaBuilder;
	private From<?, ADO> root;
	private AbstractDomainObjectJoins<T, ADO> joins;
	private Map<String, Expression<?>> subqueryExpressions;

	public QueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, ADO> root, AbstractDomainObjectJoins<T, ADO> joins) {
		this.root = root;
		this.joins = joins;
		this.subqueryExpressions = new HashMap<>();
		this.query = query;
		this.criteriaBuilder = cb;
	}

	protected Expression addSubqueryExpression(String name, Expression<?> expression) {
		subqueryExpressions.put(name, expression);
		return expression;
	}

	public Expression<?> getSubqueryExpression(String name) {
		if (subqueryExpressions.containsKey(name)) {
			return subqueryExpressions.get(name);
		}
		return createExpression(name);
	}

	protected abstract Expression<?> createExpression(String name);

	public From<?, ADO> getRoot() {
		return root;
	}

	public CriteriaQuery<?> getQuery() {
		return query;
	}

	public CriteriaBuilder getCriteriaBuilder() {
		return criteriaBuilder;
	}

	public AbstractDomainObjectJoins<T, ADO> getJoins() {
		return joins;
	}


	protected Subquery<String> getPersonContactDetailSubquery(PersonContactDetailType personContactDetailType, From<?, Person> from) {
		final Subquery<String> phoneSubQuery = query.subquery(String.class);
		final Root<PersonContactDetail> phoneRoot = phoneSubQuery.from(PersonContactDetail.class);
		phoneSubQuery.where(
				criteriaBuilder.and(
						criteriaBuilder.equal(phoneRoot.get(PersonContactDetail.PERSON), from),
						criteriaBuilder.isTrue(phoneRoot.get(PersonContactDetail.PRIMARY)),
						criteriaBuilder.equal(phoneRoot.get(PersonContactDetail.PERSON_CONTACT_DETAIL_TYPE), personContactDetailType)));
		phoneSubQuery.select(phoneRoot.get(PersonContactDetail.CONTACT_INFORMATION));
		return phoneSubQuery;
	}

	protected Subquery<Object> phoneOwnerSubquery(From<?, Person> from) {
		final Subquery<Object> phoneOwnerSubQuery = getQuery().subquery(Object.class);
		final Root<PersonContactDetail> phoneRoot = phoneOwnerSubQuery.from(PersonContactDetail.class);
		CriteriaBuilder cb = getCriteriaBuilder();
		phoneOwnerSubQuery.where(
				cb.and(
						cb.equal(phoneRoot.get(PersonContactDetail.PERSON), from),
						cb.isTrue(phoneRoot.get(PersonContactDetail.PRIMARY)),
						cb.equal(phoneRoot.get(PersonContactDetail.PERSON_CONTACT_DETAIL_TYPE), PersonContactDetailType.PHONE)));
		phoneOwnerSubQuery.select(
				cb.selectCase()
						.when(cb.isTrue(phoneRoot.get(PersonContactDetail.THIRD_PARTY)), phoneRoot.get(PersonContactDetail.THIRD_PARTY_NAME))
						.otherwise(cb.literal(Captions.PersonContactDetail_thisPerson)));
		return phoneOwnerSubQuery;
	}
}
