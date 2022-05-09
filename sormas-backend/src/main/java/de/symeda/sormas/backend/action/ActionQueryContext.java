package de.symeda.sormas.backend.action;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;

import de.symeda.sormas.backend.common.QueryContext;

public class ActionQueryContext extends QueryContext<Action, ActionJoins> {

	protected ActionQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, Action> root) {
		this(cb, query, new ActionJoins(root));
	}

	public ActionQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, ActionJoins joins) {
		super(cb, query, joins.getRoot(), joins);
	}

	@Override
	protected Expression<?> createExpression(String name) {
		return null;
	}
}
