package de.symeda.sormas.backend.event;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;

import de.symeda.sormas.backend.common.QueryContext;

public class EventQueryContext extends QueryContext<Event, EventJoins> {

	public EventQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, Event> root) {
		this(cb, query, new EventJoins(root));
	}

	public EventQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, EventJoins eventJoins) {
		super(cb, query, eventJoins.getRoot(), eventJoins);
	}

	@Override
	protected Expression<?> createExpression(String name) {
		return null;
	}
}
