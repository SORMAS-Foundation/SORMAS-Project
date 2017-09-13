package de.symeda.sormas.backend.report;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class WeeklyReportService extends AbstractAdoService<WeeklyReport> {

	public WeeklyReportService() {
		super(WeeklyReport.class);
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
