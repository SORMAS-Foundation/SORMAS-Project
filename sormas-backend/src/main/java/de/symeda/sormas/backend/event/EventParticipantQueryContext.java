package de.symeda.sormas.backend.event;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;

import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.DeletableAdo;
import de.symeda.sormas.backend.common.QueryContext;
import de.symeda.sormas.backend.sample.Sample;

public class EventParticipantQueryContext extends QueryContext<EventParticipant, EventParticipantJoins> {

	private Join<EventParticipant, Sample> samples;

	protected EventParticipantQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, EventParticipant> root) {
		this(cb, query, new EventParticipantJoins(root));
	}

	public EventParticipantQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, EventParticipantJoins joins) {
		super(cb, query, joins.getRoot(), joins);
	}

	public Join<EventParticipant, Sample> getSamplesJoin() {
		CriteriaBuilder cb = getCriteriaBuilder();
		if (samples == null) {
			samples = getJoins().getSamples();
			From<?, EventParticipant> eventParticipant = getJoins().getRoot();
			samples.on(
				cb.and(
					cb.isFalse(samples.get(DeletableAdo.DELETED)),
					cb.equal(samples.get(Sample.ASSOCIATED_EVENT_PARTICIPANT), eventParticipant.get(AbstractDomainObject.ID))));
		}
		return samples;
	}

	@Override
	protected Expression<?> createExpression(String name) {
		return null;
	}
}
