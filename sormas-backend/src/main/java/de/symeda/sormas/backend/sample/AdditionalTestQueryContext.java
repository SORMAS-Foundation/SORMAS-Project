package de.symeda.sormas.backend.sample;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;

import org.apache.poi.ss.formula.functions.T;

import de.symeda.sormas.backend.common.QueryContext;

public class AdditionalTestQueryContext extends QueryContext<T, AdditionalTest> {

	public AdditionalTestQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, AdditionalTest> root) {
		super(cb, query, root, new AdditionalTestJoins(root));
	}

	@Override
	protected Expression<?> createExpression(String name) {
		return null;
	}
}
