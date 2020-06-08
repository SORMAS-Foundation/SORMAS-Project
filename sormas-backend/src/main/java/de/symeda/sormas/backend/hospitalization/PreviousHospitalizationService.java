/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.hospitalization;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractAdoService;

@Stateless
@LocalBean
public class PreviousHospitalizationService extends AbstractAdoService<PreviousHospitalization> {

	public PreviousHospitalizationService() {
		super(PreviousHospitalization.class);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<PreviousHospitalization, PreviousHospitalization> from) {
		// A user should not directly query for this
		throw new UnsupportedOperationException();
	}

	public List<Object[]> getFirstPreviousHospitalizations(List<Long> caseIds) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<PreviousHospitalization> prevHospRoot = cq.from(getElementClass());
		Join<PreviousHospitalization, Hospitalization> hospitalizationJoin =
			prevHospRoot.join(PreviousHospitalization.HOSPITALIZATION, JoinType.LEFT);
		Root<Case> caseRoot = cq.from(Case.class);
		Join<Case, Hospitalization> caseHospitalizationJoin = caseRoot.join(Case.HOSPITALIZATION, JoinType.LEFT);

		cq.multiselect(caseRoot.get(Case.ID), prevHospRoot.get(PreviousHospitalization.ID));

		Expression<String> caseIdsExpression = caseRoot.get(Case.ID);
		cq.where(
			cb.and(
				caseIdsExpression.in(caseIds),
				cb.equal(hospitalizationJoin.get(Hospitalization.ID), caseHospitalizationJoin.get(Hospitalization.ID))));
		cq.orderBy(cb.asc(caseRoot.get(Case.ID)), cb.asc(prevHospRoot.get(PreviousHospitalization.ADMISSION_DATE)));

		return em.createQuery(cq).getResultList();
	}
}
