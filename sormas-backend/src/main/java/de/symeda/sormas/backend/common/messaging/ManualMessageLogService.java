package de.symeda.sormas.backend.common.messaging;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.messaging.MessageType;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.person.Person;

@Stateless
@LocalBean
public class ManualMessageLogService extends AbstractAdoService<ManualMessageLog> {

	public static final int MANUAL_MESSAGE_LOG_LIMIT = 5;

	public ManualMessageLogService() {
		super(ManualMessageLog.class);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, ManualMessageLog> from) {
		throw new UnsupportedOperationException();
	}

	public List<ManualMessageLog> getByCaseUuid(@NotNull String caseUuid, MessageType messageType) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<ManualMessageLog> cq = cb.createQuery(ManualMessageLog.class);
		final Root<ManualMessageLog> manualMessageLogRoot = cq.from(ManualMessageLog.class);

		final Subquery<Person> casePersonIdSubQuery = cq.subquery(Person.class);
		final Root<Case> caseRoot = casePersonIdSubQuery.from(Case.class);
		casePersonIdSubQuery.where(cb.equal(caseRoot.get(Case.UUID), caseUuid));
		casePersonIdSubQuery.select(caseRoot.get(Case.PERSON).get(Person.ID));

		Predicate filter = cb.and(
			cb.equal(manualMessageLogRoot.get(ManualMessageLog.RECIPIENT_PERSON).get(Person.ID), casePersonIdSubQuery),
			cb.equal(manualMessageLogRoot.get(ManualMessageLog.MESSAGE_TYPE), messageType));

		cq.where(filter);
		cq.orderBy(cb.desc(manualMessageLogRoot.get(ManualMessageLog.SENT_DATE)));

		return em.createQuery(cq).setMaxResults(MANUAL_MESSAGE_LOG_LIMIT).getResultList();
	}
}
