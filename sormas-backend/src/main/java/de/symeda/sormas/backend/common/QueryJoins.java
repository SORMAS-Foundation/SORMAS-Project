package de.symeda.sormas.backend.common;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

public class QueryJoins<Y extends AbstractDomainObject> {

	private From<?, Y> root;

	public QueryJoins(From<?, Y> root) {
		this.root = root;
	}

	public From<?, Y> getRoot() {
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

	protected <A extends AbstractDomainObject, J extends QueryJoins<A>> J getOrCreate(J joins, Supplier<J> joinsSupplier, Consumer<J> setValue) {

		final J result;
		if (joins == null) {
			result = joinsSupplier.get();
			setValue.accept(result);
		} else {
			result = joins;
		}

		return result;
	}
}
