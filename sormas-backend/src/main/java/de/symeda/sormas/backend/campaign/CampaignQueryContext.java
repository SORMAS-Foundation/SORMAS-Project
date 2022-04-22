package de.symeda.sormas.backend.campaign;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;

import de.symeda.sormas.backend.common.QueryContext;

public class CampaignQueryContext extends QueryContext<Campaign, CampaignJoins> {

	protected CampaignQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, Campaign> root) {
		this(cb, query, new CampaignJoins(root));
	}

	public CampaignQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, CampaignJoins joins) {
		super(cb, query, joins.getRoot(), joins);
	}

	@Override
	protected Expression<?> createExpression(String name) {
		return null;
	}
}
