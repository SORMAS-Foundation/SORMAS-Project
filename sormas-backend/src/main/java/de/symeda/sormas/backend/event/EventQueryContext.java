package de.symeda.sormas.backend.event;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;

import de.symeda.sormas.backend.common.QueryContext;
import de.symeda.sormas.utils.EventJoins;

public class EventQueryContext<T> extends QueryContext<T, Event> {

	public EventQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, From<T, Event> root) {
		super(cb, query, root, new EventJoins<>(root));
	}

	@Override
	protected Expression<?> createExpression(String name) {
		return null;
	}
}
