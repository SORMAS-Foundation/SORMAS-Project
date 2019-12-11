package de.symeda.sormas.backend.report;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.api.report.AggregateReportCriteria;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class AggregateReportService extends AbstractAdoService<AggregateReport> {

	public AggregateReportService() {
		super(AggregateReport.class);
	}
	
	public Predicate createCriteriaFilter(AggregateReportCriteria criteria, CriteriaBuilder cb, CriteriaQuery<?> cq, From<AggregateReport, AggregateReport> from) {
		Predicate filter = null;
	
		if (criteria.getRegion() != null) {
			filter = and(cb, filter, cb.equal(from.join(AggregateReport.REGION, JoinType.LEFT).get(Region.UUID), criteria.getRegion().getUuid()));
		}
		if (criteria.getDistrict() != null) {
			filter = and(cb, filter, cb.equal(from.join(AggregateReport.DISTRICT, JoinType.LEFT).get(District.UUID), criteria.getDistrict().getUuid()));
		}
		if (criteria.getHealthFacility() != null) {
			filter = and(cb, filter, cb.equal(from.join(AggregateReport.HEALTH_FACILITY, JoinType.LEFT).get(Facility.UUID), criteria.getHealthFacility().getUuid()));
		}
		if (criteria.getPointOfEntry() != null) {
			filter = and(cb, filter, cb.equal(from.join(AggregateReport.POINT_OF_ENTRY, JoinType.LEFT).get(PointOfEntry.UUID), criteria.getPointOfEntry().getUuid()));
		}
		if (criteria.getYear() != null) {
			filter = and(cb, filter, cb.equal(from.get(AggregateReport.YEAR), criteria.getYear()));
		}
		if (criteria.getEpiWeek() != null) {
			filter = and(cb, filter, cb.equal(from.get(AggregateReport.EPI_WEEK), criteria.getEpiWeek()));
		}
		
		return filter;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<AggregateReport, AggregateReport> from, User user) {
		if (user == null || user.getUserRoles().contains(UserRole.NATIONAL_USER)
				|| user.getUserRoles().contains(UserRole.NATIONAL_CLINICIAN)
				|| user.getUserRoles().contains(UserRole.NATIONAL_OBSERVER)) {
			return null;
		}

		// Whoever created the weekly report is allowed to access it
		Join<AggregateReport, User> reportingUser = from.join(AggregateReport.REPORTING_USER, JoinType.LEFT);
		Predicate filter = cb.equal(reportingUser, user);

		// Allow access based on user role
		for (UserRole userRole : user.getUserRoles()) {
			switch (userRole) {
			case SURVEILLANCE_SUPERVISOR:
			case CONTACT_SUPERVISOR:
			case CASE_SUPERVISOR:
			case STATE_OBSERVER:
				// Supervisors see all reports from their region
				if (user.getRegion() != null) {
					filter = cb.or(filter, cb.equal(from.get(AggregateReport.REGION), user.getRegion()));
				}
				break;
			default:
				break;
			}
		}

		return filter;
		
	}
	
}
