package de.symeda.sormas.backend.common;

import java.sql.Timestamp;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractCoreAdoService<ADO extends CoreAdo> extends AdoServiceWithUserFilter<ADO> {

	public AbstractCoreAdoService(Class<ADO> elementClass) {
		super(elementClass);
	}

	@Override
	public void delete(ADO deleteme) {

		deleteme.setDeleted(true);
		em.persist(deleteme);
		em.flush();
	}

	protected <C> Predicate changeDateFilter(CriteriaBuilder cb, Timestamp date, From<?, C> path, String... joinFields) {
		From<?, ?> parent = path;
		for (int i = 0; i < joinFields.length; i++) {
			parent = parent.join(joinFields[i], JoinType.LEFT);
		}
		return CriteriaBuilderHelper.greaterThanAndNotNull(cb, parent.get(AbstractDomainObject.CHANGE_DATE), date);
	}
}
