package de.symeda.sormas.backend.util;

import de.symeda.sormas.backend.common.AbstractDomainObject;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

public class AbstractDomainObjectJoins<ADO extends AbstractDomainObject> {
	private Root<ADO> root;

	public AbstractDomainObjectJoins(Root<ADO> root) {
		this.root = root;
	}

	public Root<ADO> getRoot() {
		return root;
	}

	protected  <T> Join<ADO, T> getOrCreate(Join<ADO, T> join, String attribute, JoinType joinType) {
		return getOrCreate(join, attribute, joinType, root);
	}

	protected <P, T> Join<P, T> getOrCreate(Join<P, T> join, String attribute, JoinType joinType, From<?, P> parent) {
		if (join != null) {
			return join;
		}

		return join = parent.join(attribute, joinType);
	}

}
