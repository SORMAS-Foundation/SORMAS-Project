package de.symeda.sormas.backend.news;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.common.QueryContext;

public class NewsQueryContext extends QueryContext<News, NewsJoin> {

	public NewsQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, NewsJoin newsJoin) {
		super(cb, query, newsJoin.getRoot(), newsJoin);
	}

	public NewsQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, Root<News> root) {
		this(cb, query, new NewsJoin(root));
	}

	@Override
	protected Expression<?> createExpression(String name) {
		return null;
	}
}
