package de.symeda.sormas.backend.common;

import de.symeda.sormas.api.common.DeleteDetails;

import java.sql.Timestamp;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

public abstract class AbstractDeletableAdoService<ADO extends DeletableAdo> extends AdoServiceWithUserFilter<ADO> {

	protected AbstractDeletableAdoService(Class<ADO> elementClass) {
		super(elementClass);
	}

	public void delete(ADO ado, DeleteDetails deleteDetails) {

		ado.setDeleteReason(deleteDetails.getDeleteReason());
		ado.setOtherDeleteReason(deleteDetails.getOtherDeleteReason());
		ado.setDeleted(true);
		em.persist(ado);
		em.flush();
	}

	protected <C> Predicate changeDateFilter(CriteriaBuilder cb, Timestamp date, From<?, C> path, String... joinFields) {
		From<?, ?> parent = path;
		for (String joinField : joinFields) {
			parent = parent.join(joinField, JoinType.LEFT);
		}
		return CriteriaBuilderHelper.greaterThanAndNotNull(cb, parent.get(AbstractDomainObject.CHANGE_DATE), date);
	}
}
