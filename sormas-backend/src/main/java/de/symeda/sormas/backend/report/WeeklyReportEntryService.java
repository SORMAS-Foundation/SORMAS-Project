package de.symeda.sormas.backend.report;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

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

	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<WeeklyReportEntry, WeeklyReportEntry> from, User user) {
		return weeklyReportService.createUserFilter(cb, cq, from.join(WeeklyReportEntry.WEEKLY_REPORT, JoinType.LEFT), user);
	}

}
