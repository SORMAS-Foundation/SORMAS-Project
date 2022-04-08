package de.symeda.sormas.backend.immunization;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;

import de.symeda.sormas.backend.common.QueryContext;
import de.symeda.sormas.backend.immunization.entity.DirectoryImmunization;

public class DirectoryImmunizationQueryContext extends QueryContext<DirectoryImmunization, DirectoryImmunizationJoins> {

	public DirectoryImmunizationQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, DirectoryImmunization> root) {
		super(cb, query, root, new DirectoryImmunizationJoins(root));
	}

	@Override
	protected Expression<?> createExpression(String name) {
		return null;
	}
}
