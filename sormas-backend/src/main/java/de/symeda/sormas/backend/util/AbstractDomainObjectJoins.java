package de.symeda.sormas.backend.util;

import java.util.function.Consumer;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.backend.common.AbstractDomainObject;

public class AbstractDomainObjectJoins<Z, Y extends AbstractDomainObject> {

	private From<Z, Y> root;

	public AbstractDomainObjectJoins(From<Z, Y> root) {
		this.root = root;
	}

	public From<Z, Y> getRoot() {
		return root;
	}

	protected <T> Join<Y, T> getOrCreate(Join<Y, T> join, String attribute, JoinType joinType, Consumer<Join<Y, T>> setValue) {
		return this.getOrCreate(join, attribute, joinType, root, setValue);
	}

	protected <P, T> Join<P, T> getOrCreate(Join<P, T> join, String attribute, JoinType joinType, From<?, P> parent, Consumer<Join<P, T>> setValue) {

		if (join == null) {
			join = parent.join(attribute, joinType);
			setValue.accept(join);
		} else if (join.getJoinType() != joinType) {
			throw new IllegalArgumentException("Join already defined with another join type");
		}

		return join;
	}
}
