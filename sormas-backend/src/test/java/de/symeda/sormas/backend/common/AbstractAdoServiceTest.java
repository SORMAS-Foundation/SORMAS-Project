package de.symeda.sormas.backend.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Selection;

import org.hamcrest.Matchers;
import org.junit.Test;

public class AbstractAdoServiceTest {

	@Test
	public void testReduce() {

		//all null
		assertThat(AbstractAdoService.reduce(null), nullValue());
		assertThat(AbstractAdoService.reduce(null, (Predicate) null), nullValue());
		assertThat(AbstractAdoService.reduce(null, null, null), nullValue());

		DummyPredicate p0 = new DummyPredicate();

		//one non-null
		assertThat(AbstractAdoService.reduce(null, p0), sameInstance(p0));
		assertThat(AbstractAdoService.reduce(null, p0, null), sameInstance(p0));
		assertThat(AbstractAdoService.reduce(null, null, p0), sameInstance(p0));
		assertThat(AbstractAdoService.reduce(null, null, null, null, null, p0, null, null, null), sameInstance(p0));

		DummyPredicate p1 = new DummyPredicate();
		DummyPredicate p2 = new DummyPredicate();
		DummyPredicate p3 = new DummyPredicate();

		DummyPredicate pr = new DummyPredicate();
		List<Predicate> restrictions = new ArrayList<>();

		Function<Predicate[], Predicate> op = a -> {
			restrictions.clear();
			restrictions.addAll(Arrays.asList(a));
			return pr;
		};

		//two Arguments
		assertThat(AbstractAdoService.reduce(op, p0, p1), sameInstance(pr));
		assertThat(restrictions, Matchers.contains(p0, p1));

		assertThat(AbstractAdoService.reduce(op, null, null, p0, null, p1, null, null), sameInstance(pr));
		assertThat(restrictions, Matchers.contains(p0, p1));

		//4 Arguments
		assertThat(AbstractAdoService.reduce(op, null, null, p0, null, p1, null, p2, p3, null), sameInstance(pr));
		assertThat(restrictions, Matchers.contains(p0, p1, p2, p3));
	}

	private static class DummyPredicate implements Predicate {

		@Override
		public Predicate isNull() {
			return null;
		}

		@Override
		public Predicate isNotNull() {
			return null;
		}

		@Override
		public Predicate in(Object... values) {
			return null;
		}

		@Override
		public Predicate in(Expression<?>... values) {
			return null;
		}

		@Override
		public Predicate in(Collection<?> values) {
			return null;
		}

		@Override
		public Predicate in(Expression<Collection<?>> values) {
			return null;
		}

		@Override
		public <X> Expression<X> as(Class<X> type) {
			return null;
		}

		@Override
		public Selection<Boolean> alias(String name) {
			return null;
		}

		@Override
		public boolean isCompoundSelection() {
			return false;
		}

		@Override
		public List<Selection<?>> getCompoundSelectionItems() {
			return null;
		}

		@Override
		public Class<? extends Boolean> getJavaType() {
			return null;
		}

		@Override
		public String getAlias() {
			return null;
		}

		@Override
		public BooleanOperator getOperator() {
			return null;
		}

		@Override
		public boolean isNegated() {
			return false;
		}

		@Override
		public List<Expression<Boolean>> getExpressions() {
			return null;
		}

		@Override
		public Predicate not() {
			return null;
		}
	}
}
