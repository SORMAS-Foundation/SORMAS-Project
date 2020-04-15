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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.report;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.common.AbstractAdoService;

@Stateless
@LocalBean
public class WeeklyReportEntryService extends AbstractAdoService<WeeklyReportEntry> {

	@EJB
	WeeklyReportService weeklyReportService;
	
	public WeeklyReportEntryService() {
		super(WeeklyReportEntry.class);
	}
	
	public long getNumberOfNonZeroEntries(WeeklyReport report) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<WeeklyReportEntry> from = cq.from(getElementClass());
		
		cq.select(cb.count(from));
		cq.where(cb.equal(from.get(WeeklyReportEntry.WEEKLY_REPORT), report));
		cq.where(cb.greaterThan(from.get(WeeklyReportEntry.NUMBER_OF_CASES), 0));
		return em.createQuery(cq).getSingleResult();
	}

	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<WeeklyReportEntry, WeeklyReportEntry> from) {
		return weeklyReportService.createUserFilter(cb, cq, from.join(WeeklyReportEntry.WEEKLY_REPORT, JoinType.LEFT));
	}

}
