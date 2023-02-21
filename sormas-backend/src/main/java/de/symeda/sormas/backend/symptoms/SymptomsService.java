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
package de.symeda.sormas.backend.symptoms;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.clinicalcourse.ClinicalVisit;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.visit.Visit;

@Stateless
@LocalBean
public class SymptomsService extends BaseAdoService<Symptoms> {

	public SymptomsService() {
		super(Symptoms.class);
	}

	public List<String> getOrphanSymptoms() {
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Symptoms> symptomsRoot = cq.from(Symptoms.class);

		Subquery<Long> visitSubquery = cq.subquery(Long.class);
		Root<Visit> visitSubqueryRoot = visitSubquery.from(Visit.class);
		visitSubquery.where(cb.equal(visitSubqueryRoot.get(Visit.SYMPTOMS), symptomsRoot.get(Symptoms.ID)));
		visitSubquery.select(visitSubqueryRoot.get(Visit.ID));

		Subquery<Long> caseSubquery = cq.subquery(Long.class);
		Root<Case> caseSubqueryRoot = caseSubquery.from(Case.class);
		caseSubquery.where(cb.equal(caseSubqueryRoot.get(Case.SYMPTOMS), symptomsRoot.get(Symptoms.ID)));
		caseSubquery.select(caseSubqueryRoot.get(Case.ID));

		Subquery<Long> clinicalVisitSubquery = cq.subquery(Long.class);
		Root<ClinicalVisit> clinicalVisitRoot = clinicalVisitSubquery.from(ClinicalVisit.class);
		clinicalVisitSubquery.where(cb.equal(clinicalVisitRoot.get(ClinicalVisit.SYMPTOMS), symptomsRoot.get(Symptoms.ID)));
		clinicalVisitSubquery.select(clinicalVisitRoot.get(Case.ID));

		cq.where(cb.not(cb.or(cb.exists(visitSubquery), cb.exists(caseSubquery), cb.exists(clinicalVisitSubquery))));
		cq.select(symptomsRoot.get(Symptoms.UUID));

		return em.createQuery(cq).getResultList();
	}

}
