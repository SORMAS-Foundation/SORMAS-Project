package de.symeda.sormas.backend.common;

import java.io.Serializable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

@FunctionalInterface
public interface FilterProvider<ADO extends AbstractDomainObject> extends Serializable {

	@SuppressWarnings("rawtypes")
	Predicate provide(CriteriaBuilder cb, CriteriaQuery cq, From<?, ADO> from);
}
