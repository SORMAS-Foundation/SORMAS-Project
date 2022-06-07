package de.symeda.sormas.backend.user;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;

import de.symeda.sormas.backend.common.QueryContext;

public class UserQueryContext extends QueryContext<User, UserJoins> {

	protected UserQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, User> root) {
		super(cb, query, root, new UserJoins(root));
	}

	@Override
	protected Expression<?> createExpression(String name) {
		return null;
	}

}
