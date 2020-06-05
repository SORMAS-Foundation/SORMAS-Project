package de.symeda.sormas.backend.common;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.criteria.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

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

    public <JE, JWE> Join<JE, JWE> addJoin(Supplier<Join<JE, JWE>> joinSupplier, String alias) {
        final Join<JE, JWE> join = joinSupplier.get();
        final Class<JE> joinEntityClass = (Class<JE>) join.getParent().getJavaType();
        final Class<JWE> joinWithEntityClass = (Class<JWE>) join.getJavaType();
        return addJoin(joinEntityClass, joinWithEntityClass, join, alias);
    }

    private <JE, JWE> Join<JE, JWE> addJoin(Class<JE> joinEntity, Class<JWE> joinWithEntity, Join<JE, JWE> join,
                                            final String alias) {
        final String joinEntitySimpleName = joinEntity.getSimpleName();
        final String joinWithEntitySimpleName = joinWithEntity.getSimpleName();
        final Join<JE, JWE> existingJoin = getJoin(joinEntity, joinWithEntity, alias);
        if (existingJoin == null) {
            final String joinName =
                    joinEntitySimpleName + joinWithEntitySimpleName + (StringUtils.isNotEmpty(alias) ? alias : "");
            joins.put(joinName, join);
            return join;
        }
        logger.warn("Joining of entities [{}] and [{}] is already defined for this query, returning existing join!",
                joinEntitySimpleName, joinWithEntitySimpleName);
        return existingJoin;
    }

    public Expression addExpression(String name, Expression<?> expression) {
        subqueryExpressions.put(name, expression);
        return expression;
    }

    public <JE, JWE> Join<JE, JWE> getJoin(Class<JE> joinEntity, Class<JWE> joinWithEntity) {
        return (Join<JE, JWE>) joins.get(joinEntity.getSimpleName() + joinWithEntity.getSimpleName());
    }

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
