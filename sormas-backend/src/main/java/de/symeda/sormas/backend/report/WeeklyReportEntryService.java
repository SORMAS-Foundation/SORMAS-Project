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
import de.symeda.sormas.backend.user.User;

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
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<WeeklyReportEntry, WeeklyReportEntry> from, User user) {
		return weeklyReportService.createUserFilter(cb, cq, from.join(WeeklyReportEntry.WEEKLY_REPORT, JoinType.LEFT), user);
	}

}
