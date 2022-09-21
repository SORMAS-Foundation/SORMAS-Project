package de.symeda.sormas.backend.visit;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.common.QueryContext;

public class VisitQueryContext extends QueryContext<Visit, VisitJoins> {

	public VisitQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, Visit> root) {
		this(cb, query, new VisitJoins(root, JoinType.LEFT));
	}

	public VisitQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, VisitJoins joins) {
		super(cb, query, joins.getRoot(), joins);
	}

	@Override
	protected Expression<?> createExpression(String name) {
		return null;
	}
}
