package de.symeda.sormas.backend.common.messaging;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.messaging.ManualMessageLogDto;
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
		return null;
	}

	public List<ManualMessageLogDto> getByPersonUuid(@NotNull String personUuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ManualMessageLogDto> cq = cb.createQuery(ManualMessageLogDto.class);
		Root<ManualMessageLog> manualMessageLogRoot = cq.from(ManualMessageLog.class);

		cq.multiselect(
			manualMessageLogRoot.get(ManualMessageLog.MESSAGE_TYPE),
			manualMessageLogRoot.get(ManualMessageLog.SENT_DATE),
			manualMessageLogRoot.get(ManualMessageLog.SENDING_USER),
			manualMessageLogRoot.get(ManualMessageLog.RECIPIENT_PERSON));

		Predicate filter = createUserFilter(cb, cq, manualMessageLogRoot);
		filter =
			AbstractAdoService.and(cb, filter, cb.equal(manualMessageLogRoot.get(ManualMessageLog.RECIPIENT_PERSON).get(Person.UUID), personUuid));

		cq.where(filter);
		cq.orderBy(cb.desc(manualMessageLogRoot.get(ManualMessageLog.SENT_DATE)));

		return em.createQuery(cq).setMaxResults(MANUAL_MESSAGE_LOG_LIMIT).getResultList();
	}
}
