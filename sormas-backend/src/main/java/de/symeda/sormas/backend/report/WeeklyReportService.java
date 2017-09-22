package de.symeda.sormas.backend.report;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.report.WeeklyReportSummaryDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class WeeklyReportService extends AbstractAdoService<WeeklyReport> {

	public WeeklyReportService() {
		super(WeeklyReport.class);
	}

	public long getNumberOfWeeklyReportsByFacility(Facility facility, EpiWeek epiWeek) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<WeeklyReport> from = cq.from(getElementClass());

		cq.select(cb.count(from));
		Predicate filter = cb.equal(from.get(WeeklyReport.HEALTH_FACILITY), facility);
		filter = cb.and(filter, cb.equal(from.get(WeeklyReport.YEAR), epiWeek.getYear()));
		filter = cb.and(filter, cb.equal(from.get(WeeklyReport.EPI_WEEK), epiWeek.getWeek()));

		cq.where(filter);
		return em.createQuery(cq).getSingleResult();
	}

	public List<WeeklyReport> getByFacility(Facility facility, EpiWeek epiWeek) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<WeeklyReport> cq = cb.createQuery(getElementClass());
		Root<WeeklyReport> from = cq.from(getElementClass());

		Predicate filter = cb.equal(from.get(WeeklyReport.HEALTH_FACILITY), facility);
		filter = cb.and(filter, cb.equal(from.get(WeeklyReport.YEAR), epiWeek.getYear()));
		filter = cb.and(filter, cb.equal(from.get(WeeklyReport.EPI_WEEK), epiWeek.getWeek()));

		cq.orderBy(cb.asc(from.get(WeeklyReport.HEALTH_FACILITY)));
		return em.createQuery(cq).getResultList();
	}

	public List<WeeklyReportSummaryDto> getWeeklyReportSummariesPerDistrict(EpiWeek epiWeek) {
		// TODO replace with variables
//		Query query = em.createQuery("SELECT region_id, COUNT(fac) as facilities, SUM(missing) as missing, SUM(report) as report, SUM(zero) as zero "
//				+ "FROM (SELECT facility.id as fac, facility.region_id, "
//				+ "CASE WHEN COUNT(users.id) > COUNT(wr.id) THEN 1 ELSE 0 END as missing, "
//				+ "CASE WHEN SUM(wr.totalnumberofcases) > 0 AND COUNT(users.id) = COUNT(wr.id) THEN 1 ELSE 0 END as report, "
//				+ "CASE WHEN SUM(wr.totalnumberofcases) = 0 AND COUNT(users.id) = COUNT(wr.id) THEN 1 ELSE 0 END as zero "
//				+ "FROM users "
//				+ "INNER JOIN userroles ON userroles.user_id = users.id "
//				+ "INNER JOIN facility ON users.healthfacility_id = facility.id "
//				+ "LEFT JOIN (SELECT * FROM weeklyreport WHERE epiweek = 41) as wr ON wr.informant_id = users.id "
//				+ "WHERE userroles.userrole = 'INFORMANT' "
//				+ "GROUP BY facility.id) as inner_query "
//				+ "GROUP BY region_id;");
		
		return null;
	}

	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<WeeklyReport, WeeklyReport> from, User user) {
		// National users can access all reports in the system
		if (user.getUserRoles().contains(UserRole.NATIONAL_USER)) {
			return null;
		}

		// Whoever created the weekly report is allowed to access it
		Predicate filter = cb.equal(from.get(WeeklyReport.INFORMANT), user);

		// Allow access based on user role
		for (UserRole userRole : user.getUserRoles()) {
			switch (userRole) {
			case SURVEILLANCE_SUPERVISOR:
			case CONTACT_SUPERVISOR:
			case CASE_SUPERVISOR:
				// Supervisors see all reports from facilities in their region
				if (user.getRegion() != null) {
					Join<WeeklyReport, User> informant = from.join(WeeklyReport.INFORMANT, JoinType.LEFT);
					filter = cb.or(filter, cb.equal(informant.join(User.HEALTH_FACILITY, JoinType.LEFT).get(Facility.REGION), user.getRegion()));
				}
				break;
			case SURVEILLANCE_OFFICER:
			case CONTACT_OFFICER:
			case CASE_OFFICER:
				// Officers see all reports from facilities in their district
				if (user.getDistrict() != null) {
					Join<WeeklyReport, User> informant = from.join(WeeklyReport.INFORMANT, JoinType.LEFT);
					filter = cb.or(filter, cb.equal(informant.join(User.HEALTH_FACILITY, JoinType.LEFT).get(Facility.DISTRICT), user.getDistrict()));
				}
				break;
			default:
				break;
			}
		}

		return filter;
	}

}
