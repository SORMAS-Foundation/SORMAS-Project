/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.adverseeventsfollowingimmunization;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;

import de.symeda.sormas.backend.adverseeventsfollowingimmunization.entity.AefiInvestigation;
import de.symeda.sormas.backend.adverseeventsfollowingimmunization.entity.AefiInvestigationJoins;
import de.symeda.sormas.backend.common.QueryContext;

public class AefiInvestigationQueryContext extends QueryContext<AefiInvestigation, AefiInvestigationJoins> {

	public AefiInvestigationQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, AefiInvestigation> root) {
		super(cb, query, root, new AefiInvestigationJoins(root));
	}

	public AefiInvestigationQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, AefiInvestigationJoins joins) {
		super(cb, query, joins.getRoot(), joins);
	}

	public AefiInvestigationQueryContext(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, AefiInvestigation> root, AefiInvestigationJoins joins) {
		super(cb, query, root, joins);
	}

	@Override
	protected Expression<?> createExpression(String name) {
		return null;
	}
}
