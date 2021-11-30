package de.symeda.sormas.backend.labmessage;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.labmessage.LabMessageCriteria;

import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;
import de.symeda.sormas.backend.common.CoreAdo;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.sample.Sample;

import java.util.List;

@Stateless
@LocalBean
public class LabMessageService extends AbstractCoreAdoService<LabMessage> {

	public LabMessageService() {
		super(LabMessage.class);
	}

	/**
	 * Creates a default filter that should be used as the basis of queries in this service..
	 * This essentially removes {@link CoreAdo#deleted} lab messages from the queries.
	 */
	public Predicate createDefaultFilter(CriteriaBuilder cb, Root<LabMessage> root) {
		return cb.isFalse(root.get(LabMessage.DELETED));
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, LabMessage> from) {
		return null;
	}

	public Predicate buildCriteriaFilter(CriteriaBuilder cb, Root<LabMessage> labMessage, LabMessageCriteria criteria) {
		Predicate filter = null;
		if (criteria.getUuid() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(labMessage.get(LabMessage.UUID), criteria.getUuid()));
		}
		if (criteria.getLabMessageStatus() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(labMessage.get(LabMessage.STATUS), criteria.getLabMessageStatus()));
		}
		if (criteria.getSample() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.equal(labMessage.get(LabMessage.SAMPLE).get(Sample.UUID), criteria.getSample().getUuid()));
		}
		return filter;
	}

	public List<LabMessage> getForSample(SampleReferenceDto sample) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<LabMessage> cq = cb.createQuery(LabMessage.class);
		Root<LabMessage> labMessageRoot = cq.from(LabMessage.class);

		LabMessageCriteria criteria = new LabMessageCriteria();
		criteria.setSample(sample);

		Predicate filter = createDefaultFilter(cb, labMessageRoot);
		filter = CriteriaBuilderHelper.and(cb, filter, buildCriteriaFilter(cb, labMessageRoot, criteria));

		cq.where(filter);
		cq.distinct(true);

		cq.orderBy(cb.desc(labMessageRoot.get(LabMessage.CREATION_DATE)));

		return em.createQuery(cq).getResultList();
	}
}
