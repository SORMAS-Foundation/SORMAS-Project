package de.symeda.sormas.backend.util;

import de.symeda.sormas.backend.common.AbstractDomainObject;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.util.function.Consumer;

public class AbstractDomainObjectJoins<ADO extends AbstractDomainObject> {
	private Root<ADO> root;

	public AbstractDomainObjectJoins(Root<ADO> root) {
		this.root = root;
	}

	public Root<ADO> getRoot() {
		return root;
	}

	protected  <T> Join<ADO, T> getOrCreate(Join<ADO, T> join, String attribute, JoinType joinType, Consumer<Join<ADO, T>> setValue) {
		return getOrCreate(join, attribute, joinType, root, setValue);
	}

	protected <P, T> Join<P, T> getOrCreate(Join<P, T> join, String attribute, JoinType joinType, From<?, P> parent, Consumer<Join<P, T>> setValue) {
		if (join == null) {
			join = parent.join(attribute, joinType);
			setValue.accept(join);
		}

		return join;
	}

}
