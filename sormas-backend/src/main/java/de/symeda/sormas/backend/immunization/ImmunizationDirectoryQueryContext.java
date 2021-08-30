package de.symeda.sormas.backend.immunization;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;

import de.symeda.sormas.backend.common.QueryContext;
import de.symeda.sormas.backend.immunization.entity.ImmunizationDirectory;
import de.symeda.sormas.backend.immunization.joins.ImmunizationDirectoryJoins;

public class ImmunizationDirectoryQueryContext<T> extends QueryContext<T, ImmunizationDirectory> {

	public ImmunizationDirectoryQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, ImmunizationDirectory> root) {
		super(cb, query, root, new ImmunizationDirectoryJoins(root));
	}

	@Override
	protected Expression<?> createExpression(String name) {
		return null;
	}
}
