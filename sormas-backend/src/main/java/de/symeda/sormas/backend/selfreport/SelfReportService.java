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

package de.symeda.sormas.backend.selfreport;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.selfreport.SelfReportCriteria;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;

@Stateless
@LocalBean
public class SelfReportService extends AbstractCoreAdoService<SelfReport, SelfReportJoins> {

	public SelfReportService() {
		super(SelfReport.class, DeletableEntityType.SELF_REPORT);
	}

	@Override
	protected Predicate createUserFilterInternal(CriteriaBuilder cb, CriteriaQuery cq, From<?, SelfReport> from) {
		return null;
	}

	public Predicate createUserFilter(SelfReportQueryContext queryContext) {
		return null;
	}

	@Override
	protected SelfReportJoins toJoins(From<?, SelfReport> adoPath) {
		return new SelfReportJoins(adoPath);
	}

	@Override
	public Predicate inJurisdictionOrOwned(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, SelfReport> from) {
		return cb.conjunction();
	}

	public Predicate buildCriteriaFilter(SelfReportCriteria criteria, SelfReportQueryContext selfReportQueryContext) {
		if (criteria == null) {
			return null;
		}

		CriteriaBuilder cb = selfReportQueryContext.getCriteriaBuilder();
		From<?, SelfReport> from = selfReportQueryContext.getRoot();

		Predicate filter = null;

		if (criteria.getRelevanceStatus() != null) {
			if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter = CriteriaBuilderHelper
					.and(cb, filter, cb.or(cb.equal(from.get(SelfReport.ARCHIVED), false), cb.isNull(from.get(SelfReport.ARCHIVED))));
			} else if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(SelfReport.ARCHIVED), true));
			} else if (criteria.getRelevanceStatus() == EntityRelevanceStatus.DELETED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(SelfReport.DELETED), true));
			}
		}
		if (criteria.getRelevanceStatus() != EntityRelevanceStatus.DELETED) {
			filter = CriteriaBuilderHelper.and(cb, filter, createDefaultFilter(cb, from));
		}

		if (criteria.getInvestigationStatus() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(SelfReport.INVESTIGATION_STATUS), criteria.getInvestigationStatus()));
		}

		return filter;
	}

	public Predicate createDefaultFilter(CriteriaBuilder cb, From<?, SelfReport> root) {
		return cb.isFalse(root.get(SelfReport.DELETED));
	}
}
