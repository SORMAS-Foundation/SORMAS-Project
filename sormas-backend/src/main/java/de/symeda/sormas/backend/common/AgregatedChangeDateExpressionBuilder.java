package de.symeda.sormas.backend.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;

public class AgregatedChangeDateExpressionBuilder implements ChangeDateBuilder<AgregatedChangeDateExpressionBuilder> {

	private final CriteriaBuilder cb;
	private List<Expression<? extends Date>> dateExpressions;

	public AgregatedChangeDateExpressionBuilder(CriteriaBuilder cb) {
		this.cb = cb;
		this.dateExpressions = new ArrayList<>();
	}

	public Expression<Date> build() {
		return cb.function("greatest", Date.class, dateExpressions.toArray(new Expression[] {}));
	}

	public <C> AgregatedChangeDateExpressionBuilder add(From<?, C> path, String... joinFields) {
		dateExpressions.add(changeDateExpression(path, joinFields));
		return this;
	}

	private <C> Expression<? extends Date> changeDateExpression(From<?, C> path, String... joinFields) {
		From<?, ?> parent = path;
		for (String joinField : joinFields) {
			parent = parent.join(joinField, JoinType.LEFT);
		}
		return parent.get(AbstractDomainObject.CHANGE_DATE);
	}

}
