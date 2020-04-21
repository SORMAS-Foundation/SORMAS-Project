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
		if (criteria.getEpiWeekFrom() != null || criteria.getEpiWeekTo() != null) {
			if (criteria.getEpiWeekFrom() == null) {
				filter = and(cb, filter, cb.le(from.get(AggregateReport.YEAR), criteria.getEpiWeekTo().getYear()));
				filter = and(cb, filter, cb.le(from.get(AggregateReport.EPI_WEEK), criteria.getEpiWeekTo().getWeek()));
			} else if (criteria.getEpiWeekTo() == null) {
				filter = and(cb, filter, cb.ge(from.get(AggregateReport.YEAR), criteria.getEpiWeekFrom().getYear()));
				filter = and(cb, filter, cb.ge(from.get(AggregateReport.EPI_WEEK), criteria.getEpiWeekFrom().getWeek()));
			} else {
				filter = and(cb, filter, cb.between(from.get(AggregateReport.YEAR), criteria.getEpiWeekFrom().getYear(), criteria.getEpiWeekTo().getYear()));
				filter = and(cb, filter, cb.between(from.get(AggregateReport.EPI_WEEK), criteria.getEpiWeekFrom().getWeek(), criteria.getEpiWeekTo().getWeek()));
			}
		}
		
		return filter;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<AggregateReport, AggregateReport> from) {
		User currentUser = getCurrentUser();
		if (currentUser == null 
			|| currentUser.hasAnyUserRole(
				UserRole.NATIONAL_USER,
				UserRole.NATIONAL_CLINICIAN,
				UserRole.NATIONAL_OBSERVER)) {
			return null;
		}

		// Whoever created the weekly report is allowed to access it
		Join<AggregateReport, User> reportingUser = from.join(AggregateReport.REPORTING_USER, JoinType.LEFT);
		Predicate filter = cb.equal(reportingUser, currentUser);

		// Allow access based on user role
		if(currentUser.hasAnyUserRole(
				UserRole.SURVEILLANCE_SUPERVISOR,
				UserRole.CONTACT_SUPERVISOR,
				UserRole.CASE_SUPERVISOR,
				UserRole.STATE_OBSERVER) 
			&& currentUser.getRegion() != null) {
				// Supervisors see all reports from their region
				filter = cb.or(filter, cb.equal(from.get(AggregateReport.REGION), currentUser.getRegion()));
			}

		return filter;
	}
	
	public List<AggregateReport> findBy(AggregateReportCriteria aggregateReportCriteria, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AggregateReport> cq = cb.createQuery(getElementClass());
		Root<AggregateReport> from = cq.from(getElementClass());

		Predicate filter = createCriteriaFilter(aggregateReportCriteria, cb, cq, from);

		if (user != null) {
			filter = and(cb, filter, createUserFilter(cb, cq, from));
		}
		if (filter != null) {
			cq.where(filter);
		}

		List<AggregateReport> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}
}
