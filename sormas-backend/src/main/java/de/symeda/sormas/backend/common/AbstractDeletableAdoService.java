package de.symeda.sormas.backend.common;

import java.sql.Timestamp;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.common.DeletionDetails;

public abstract class AbstractDeletableAdoService<ADO extends DeletableAdo> extends AdoServiceWithUserFilterAndJurisdiction<ADO> {

	public AbstractDeletableAdoService(Class<ADO> elementClass) {
		super(elementClass);
	}

	public void delete(ADO ado, DeletionDetails deletionDetails) {

		ado.setDeletionReason(deletionDetails.getDeletionReason());
		ado.setOtherDeletionReason(deletionDetails.getOtherDeletionReason());
		ado.setDeleted(true);
		em.persist(ado);
		em.flush();
	}

	public void restore(ADO ado) {

		ado.setDeletionReason(null);
		ado.setOtherDeletionReason(null);
		ado.setDeleted(false);
		em.persist(ado);
		em.flush();
	}

	public boolean isDeleted(String uuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ADO> from = cq.from(getElementClass());

		cq.where(cb.and(cb.isTrue(from.get(DeletableAdo.DELETED)), cb.equal(from.get(AbstractDomainObject.UUID), uuid)));
		cq.select(cb.count(from));
		long count = em.createQuery(cq).getSingleResult();
		return count > 0;
	}

	protected <C> Predicate changeDateFilter(CriteriaBuilder cb, Timestamp date, From<?, C> path, String... joinFields) {
		From<?, ?> parent = path;
		for (String joinField : joinFields) {
			parent = parent.join(joinField, JoinType.LEFT);
		}
		return CriteriaBuilderHelper.greaterThanAndNotNull(cb, parent.get(AbstractDomainObject.CHANGE_DATE), date);
	}
}
