package de.symeda.sormas.backend.action;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;

import de.symeda.sormas.backend.common.QueryContext;

public class ActionQueryContext<T> extends QueryContext<T, Action> {
    public ActionQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, Action> root) {
        super(cb, query, root, new ActionJoins(root));
    }

    @Override
    protected Expression<?> createExpression(String name) {
        return null;
    }
}
