package de.symeda.sormas.backend.common;

import java.io.Serializable;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Selection;

@FunctionalInterface
public interface SelectionProvider<ADO extends AbstractDomainObject> extends Serializable {

	@SuppressWarnings("rawtypes")
	List<Selection<?>> provide(CriteriaBuilder cb, CriteriaQuery cq, From<?, ADO> from);
}
