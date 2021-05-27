/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.common;

import java.util.Date;
import java.util.stream.Stream;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

public class ChangeDateFilterBuilder {

	private final CriteriaBuilder cb;
	private final Stream.Builder<Predicate> filters;
	private Date date;
	private Expression<? extends Date> dateExpression;

	private ChangeDateFilterBuilder(CriteriaBuilder cb) {
		this.cb = cb;
		this.filters = Stream.builder();
	}

	public ChangeDateFilterBuilder(CriteriaBuilder cb, Date date) {
		this(cb);
		this.date = date;
	}

	public ChangeDateFilterBuilder(CriteriaBuilder cb, Expression<? extends Date> dateExpression) {
		this(cb);
		this.dateExpression = dateExpression;
	}

	public <C> ChangeDateFilterBuilder add(From<?, C> path, String... joinFields) {
		filters.add(changeDateFilter(cb, path, joinFields));
		return this;
	}

	public Predicate build() {
		return cb.or(filters.build().toArray(Predicate[]::new));
	}

	private <C> Predicate changeDateFilter(CriteriaBuilder cb, From<?, C> path, String... joinFields) {
		From<?, ?> parent = path;
		for (String joinField : joinFields) {
			parent = parent.join(joinField, JoinType.LEFT);
		}

		if (dateExpression == null) {
			return CriteriaBuilderHelper.greaterThanAndNotNull(cb, parent.get(AbstractDomainObject.CHANGE_DATE), date);
		} else {
			return CriteriaBuilderHelper.greaterThanAndNotNull(cb, parent.get(AbstractDomainObject.CHANGE_DATE), dateExpression);
		}
	}
}
