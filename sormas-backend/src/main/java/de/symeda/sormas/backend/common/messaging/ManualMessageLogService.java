package de.symeda.sormas.backend.common.messaging;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.messaging.MessageType;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.person.Person;

@Stateless
@LocalBean
public class ManualMessageLogService extends BaseAdoService<ManualMessageLog> {

	public static final int MANUAL_MESSAGE_LOG_LIMIT = 5;

	public ManualMessageLogService() {
		super(ManualMessageLog.class);
	}

	public List<ManualMessageLog> getByPersonUuid(@NotNull String personUuid, MessageType messageType) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<ManualMessageLog> cq = cb.createQuery(ManualMessageLog.class);
		final Root<ManualMessageLog> manualMessageLogRoot = cq.from(ManualMessageLog.class);

		final Predicate filter = cb.and(
			cb.equal(manualMessageLogRoot.get(ManualMessageLog.RECIPIENT_PERSON).get(Person.UUID), personUuid),
			cb.equal(manualMessageLogRoot.get(ManualMessageLog.MESSAGE_TYPE), messageType));

		cq.where(filter);
		cq.orderBy(cb.desc(manualMessageLogRoot.get(ManualMessageLog.SENT_DATE)));

		return em.createQuery(cq).setMaxResults(MANUAL_MESSAGE_LOG_LIMIT).getResultList();
	}

	public List<ManualMessageLog> getByPersonUuid(@NotNull String personUuid) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<ManualMessageLog> cq = cb.createQuery(ManualMessageLog.class);
		final Root<ManualMessageLog> manualMessageLogRoot = cq.from(ManualMessageLog.class);

		cq.where(cb.equal(manualMessageLogRoot.get(ManualMessageLog.RECIPIENT_PERSON).get(Person.UUID), personUuid));
		cq.orderBy(cb.desc(manualMessageLogRoot.get(ManualMessageLog.SENT_DATE)));

		return em.createQuery(cq).setMaxResults(MANUAL_MESSAGE_LOG_LIMIT).getResultList();
	}

	public List<String> getUuidsByPersonUuids(@NotNull List<String> personUuids) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<String> cq = cb.createQuery(String.class);
		final Root<ManualMessageLog> manualMessageLogRoot = cq.from(ManualMessageLog.class);

		cq.where(manualMessageLogRoot.get(ManualMessageLog.RECIPIENT_PERSON).get(Person.UUID).in(personUuids));
		cq.select(manualMessageLogRoot.get(ManualMessageLog.UUID));
		cq.orderBy(cb.desc(manualMessageLogRoot.get(ManualMessageLog.SENT_DATE)));

		return em.createQuery(cq).getResultList();
	}

	public void deletePersonMessageLogs(List<String> personUuids) {

		// CAREFUL: This logic needs to be revisited if deletePermanent is overridden for manual message logs
		// because it might be necessary to consider additional logic.

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<ManualMessageLog> cd = cb.createCriteriaDelete(ManualMessageLog.class);
		final Root<ManualMessageLog> manualMessageLogRoot = cd.from(ManualMessageLog.class);

		List<String> messageLogs = getUuidsByPersonUuids(personUuids);

		if (!messageLogs.isEmpty()) {
			cd.where(manualMessageLogRoot.get(ManualMessageLog.UUID).in(messageLogs));
			em.createQuery(cd).executeUpdate();
		}
	}

}
