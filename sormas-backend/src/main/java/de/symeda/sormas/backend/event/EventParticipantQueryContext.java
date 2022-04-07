package de.symeda.sormas.backend.event;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;

import de.symeda.sormas.backend.common.QueryContext;

public class EventParticipantQueryContext extends QueryContext<EventParticipant, EventParticipantJoins> {

	public EventParticipantQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, EventParticipant> root) {
		super(cb, query, root, new EventParticipantJoins(root));
	}

	@Override
	protected Expression<?> createExpression(String name) {
		return null;
	}
}
