package de.symeda.sormas.backend.common;

import java.sql.Timestamp;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.util.IterableHelper;

public abstract class AbstractDeletableAdoService<ADO extends DeletableAdo> extends AdoServiceWithUserFilter<ADO> {

	private static final int DELETE_BATCH_SIZE = 1000;

	protected AbstractDeletableAdoService(Class<ADO> elementClass) {
		super(elementClass);
	}

	public void delete(ADO ado) {

		ado.setDeleted(true);
		em.persist(ado);
		em.flush();
	}

	public void executePermanentDeletion() {
		getAll((cb, root) -> cb.isTrue(root.get(DeletableAdo.DELETED))).forEach(ado -> deletePermanent(ado));
	}

//	public void executePermanentDeletion() {
//		IterableHelper.executeBatched(
//				getAllUuids((cb, root) -> cb.isTrue(root.get(DeletableAdo.DELETED))),
//				DELETE_BATCH_SIZE,
//				batchedUuids -> deleteBatch(batchedUuids));;
//	}
//
//	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
//	private void deleteBatch(List<String> uuids) {
//		final CriteriaBuilder cb = em.getCriteriaBuilder();
//		final CriteriaDelete<ADO> cu = cb.createCriteriaDelete(getElementClass());
//		final Root<ADO> root = cu.from(getElementClass());
//		cu.where(root.get(ADO.UUID).in(uuids));
//		em.createQuery(cu).executeUpdate();
//	}

	protected <C> Predicate changeDateFilter(CriteriaBuilder cb, Timestamp date, From<?, C> path, String... joinFields) {
		From<?, ?> parent = path;
		for (String joinField : joinFields) {
			parent = parent.join(joinField, JoinType.LEFT);
		}
		return CriteriaBuilderHelper.greaterThanAndNotNull(cb, parent.get(AbstractDomainObject.CHANGE_DATE), date);
	}
}
