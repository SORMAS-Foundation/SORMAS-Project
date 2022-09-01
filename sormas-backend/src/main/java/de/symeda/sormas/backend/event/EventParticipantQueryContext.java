package de.symeda.sormas.backend.event;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;

import de.symeda.sormas.backend.common.QueryContext;

public class EventParticipantQueryContext extends QueryContext<EventParticipant, EventParticipantJoins> {

	protected EventParticipantQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, EventParticipant> root) {
		this(cb, query, new EventParticipantJoins(root));
	}

	public EventParticipantQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, EventParticipantJoins joins) {
		super(cb, query, joins.getRoot(), joins);
	}

	@Override
	protected Expression<?> createExpression(String name) {
		return null;
	}
}
