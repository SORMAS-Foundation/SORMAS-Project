package de.symeda.sormas.backend.environment;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;

import de.symeda.sormas.backend.common.QueryContext;

public class EnvironmentQueryContext extends QueryContext<Environment, EnvironmentJoins> {

	public EnvironmentQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, Environment> root, EnvironmentJoins joins) {
		super(cb, query, root, joins);
	}

	@Override
	protected Expression<?> createExpression(String name) {
		return null;
	}
}
