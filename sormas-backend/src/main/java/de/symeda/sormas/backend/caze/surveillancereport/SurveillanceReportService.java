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

package de.symeda.sormas.backend.caze.surveillancereport;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportCriteria;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseJoins;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilterAndJurisdiction;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless
@LocalBean
public class SurveillanceReportService extends AdoServiceWithUserFilterAndJurisdiction<SurveillanceReport> {

	@EJB
	private CaseService caseService;

	public SurveillanceReportService() {
		super(SurveillanceReport.class);
	}

	public Predicate buildCriteriaFilter(SurveillanceReportCriteria criteria, CriteriaBuilder cb, Root<SurveillanceReport> root) {
		Predicate filter = null;
		if (criteria.getCaze() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(root.join(SurveillanceReport.CAZE, JoinType.LEFT).get(Case.UUID), criteria.getCaze().getUuid()));
		}

		return filter;
	}

	public List<SurveillanceReport> getByCaseUuids(List<String> caseUuids) {

		List<SurveillanceReport> reports = new ArrayList<>();
		IterableHelper.executeBatched(caseUuids, ModelConstants.PARAMETER_LIMIT, batchedCaseUuids -> {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<SurveillanceReport> cq = cb.createQuery(SurveillanceReport.class);
			Root<SurveillanceReport> reportRoot = cq.from(SurveillanceReport.class);
			Join<SurveillanceReport, Case> caseJoin = reportRoot.join(SurveillanceReport.CAZE, JoinType.LEFT);

			cq.where(caseJoin.get(AbstractDomainObject.UUID).in(batchedCaseUuids));

			reports.addAll(em.createQuery(cq).getResultList());
		});

		return reports;
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, SurveillanceReport> from) {
		return null;
	}

	@Override
	public boolean inJurisdictionOrOwned(SurveillanceReport entity) {
		return fulfillsCondition(entity, this::inJurisdictionOrOwned);
	}

	@Override
	public List<Long> getInJurisdictionIds(List<SurveillanceReport> entities) {
		return getIdList(entities, this::inJurisdictionOrOwned);
	}

	private Predicate inJurisdictionOrOwned(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, SurveillanceReport> from) {

		return caseService.inJurisdictionOrOwned(new CaseQueryContext(cb, query, new CaseJoins(from.join(SurveillanceReport.CAZE))));
	}

	public boolean isEditAllowed(SurveillanceReport report) {
		return caseService.isEditAllowed(report.getCaze());
	}
}
