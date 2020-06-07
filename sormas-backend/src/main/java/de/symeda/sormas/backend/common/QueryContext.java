package de.symeda.sormas.backend.common;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryContext<ADO extends AbstractDomainObject> {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private CriteriaQuery<?> query;
	private CriteriaBuilder criteriaBuilder;
	private From<ADO, ADO> root;
	private Map<String, Join<?, ?>> joins;
	private Map<String, Expression<?>> subqueryExpressions;
	private Map<String, Path<?>> paths;

	public QueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, From<ADO, ADO> root) {
		this.root = root;
		this.joins = new HashMap<>();
		this.subqueryExpressions = new HashMap<>();
		this.paths = new HashMap<>();
		this.query = query;
		this.criteriaBuilder = cb;
	}

	public <JE, JWE> Join<JE, JWE> addJoin(Supplier<Join<JE, JWE>> joinSupplier) {
		return addJoin(joinSupplier, null);
	}

	@SuppressWarnings("unchecked")
	public <JE, JWE> Join<JE, JWE> addJoin(Supplier<Join<JE, JWE>> joinSupplier, String alias) {

		final Join<JE, JWE> join = joinSupplier.get();
		final Class<JE> joinEntityClass = (Class<JE>) join.getParent().getJavaType();
		final Class<JWE> joinWithEntityClass = (Class<JWE>) join.getJavaType();
		return addJoin(joinEntityClass, joinWithEntityClass, join, alias);
	}

	private <JE, JWE> Join<JE, JWE> addJoin(Class<JE> joinEntity, Class<JWE> joinWithEntity, Join<JE, JWE> join, final String alias) {

		final String joinEntitySimpleName = joinEntity.getSimpleName();
		final String joinWithEntitySimpleName = joinWithEntity.getSimpleName();
		final Join<JE, JWE> existingJoin = getJoin(joinEntity, joinWithEntity, alias);
		if (existingJoin == null) {
			final String joinName = joinEntitySimpleName + joinWithEntitySimpleName + (StringUtils.isNotEmpty(alias) ? alias : "");
			joins.put(joinName, join);
			return join;
		}
		logger.warn(
			"Joining of entities [{}] and [{}] is already defined for this query, returning existing join!",
			joinEntitySimpleName,
			joinWithEntitySimpleName);
		return existingJoin;
	}

	@SuppressWarnings("rawtypes")
	public Expression addExpression(String name, Expression<?> expression) {
		subqueryExpressions.put(name, expression);
		return expression;
	}

	@SuppressWarnings("unchecked")
	public <JE, JWE> Join<JE, JWE> getJoin(Class<JE> joinEntity, Class<JWE> joinWithEntity) {
		return (Join<JE, JWE>) joins.get(joinEntity.getSimpleName() + joinWithEntity.getSimpleName());
	}

	@SuppressWarnings("unchecked")
	public <JE, JWE> Join<JE, JWE> getJoin(Class<JE> joinEntity, Class<JWE> joinWithEntity, final String aliasSuffix) {
		if (StringUtils.isNotEmpty(aliasSuffix)) {
			return (Join<JE, JWE>) joins.get(joinEntity.getSimpleName() + joinWithEntity.getSimpleName() + aliasSuffix);
		}
		return (Join<JE, JWE>) joins.get(joinEntity.getSimpleName() + joinWithEntity.getSimpleName());
	}

	public Expression<?> getExpression(String name) {
		return subqueryExpressions.get(name);
	}

	public From<?, ?> getRoot() {
		return root;
	}

	@SuppressWarnings("rawtypes")
	public Path addPath(String pathName, Path<?> path) {
		paths.put(pathName, path);
		return path;
	}

	public Path<?> getPath(String pathName) {
		return paths.get(pathName);
	}

	public CriteriaQuery<?> getQuery() {
		return query;
	}

	public CriteriaBuilder getCriteriaBuilder() {
		return criteriaBuilder;
	}
}
