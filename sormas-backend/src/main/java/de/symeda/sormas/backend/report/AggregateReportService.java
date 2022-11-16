package de.symeda.sormas.backend.report;

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

import de.symeda.sormas.api.report.AggregateReportCriteria;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilterAndJurisdiction;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserRole;
import de.symeda.sormas.backend.user.UserService;

@Stateless
@LocalBean
public class AggregateReportService extends AdoServiceWithUserFilterAndJurisdiction<AggregateReport> {

	@EJB
	private UserService userService;

	public AggregateReportService() {
		super(AggregateReport.class);
	}

	@Override
	protected Predicate createLimitedChangeDateFilter(CriteriaBuilder cb, From<?, AggregateReport> from) {
		return null;
	}

	@Override
	protected Predicate createLimitedChangeDateFilterForObsoleteEntities(CriteriaBuilder cb, From<?, AggregateReport> from) {
		return null;
	}

	public Predicate createCriteriaFilter(
		AggregateReportCriteria criteria,
		CriteriaBuilder cb,
		CriteriaQuery<?> cq,
		From<AggregateReport, AggregateReport> from) {

		Predicate filter = null;
		boolean considerNullJurisdictionCheck = Boolean.TRUE.equals(criteria.isConsiderNullJurisdictionCheck());

		if (criteria.getRegion() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(from.join(AggregateReport.REGION, JoinType.LEFT).get(Region.UUID), criteria.getRegion().getUuid()));
		} else if (considerNullJurisdictionCheck) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isNull(from.get(AggregateReport.REGION)));
		}
		if (criteria.getDistrict() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(from.join(AggregateReport.DISTRICT, JoinType.LEFT).get(District.UUID), criteria.getDistrict().getUuid()));
		} else if (considerNullJurisdictionCheck) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isNull(from.get(AggregateReport.DISTRICT)));
		}
		if (criteria.getHealthFacility() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(from.join(AggregateReport.HEALTH_FACILITY, JoinType.LEFT).get(Facility.UUID), criteria.getHealthFacility().getUuid()));
		} else if (considerNullJurisdictionCheck) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isNull(from.get(AggregateReport.HEALTH_FACILITY)));
		}
		if (criteria.getPointOfEntry() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(from.join(AggregateReport.POINT_OF_ENTRY, JoinType.LEFT).get(PointOfEntry.UUID), criteria.getPointOfEntry().getUuid()));
		} else if (considerNullJurisdictionCheck) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isNull(from.get(AggregateReport.POINT_OF_ENTRY)));
		}
		if (criteria.getEpiWeekFrom() != null || criteria.getEpiWeekTo() != null) {
			if (criteria.getEpiWeekFrom() == null) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.le(from.get(AggregateReport.YEAR), criteria.getEpiWeekTo().getYear()));
				filter = CriteriaBuilderHelper.and(cb, filter, cb.le(from.get(AggregateReport.EPI_WEEK), criteria.getEpiWeekTo().getWeek()));
			} else if (criteria.getEpiWeekTo() == null) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.ge(from.get(AggregateReport.YEAR), criteria.getEpiWeekFrom().getYear()));
				filter = CriteriaBuilderHelper.and(cb, filter, cb.ge(from.get(AggregateReport.EPI_WEEK), criteria.getEpiWeekFrom().getWeek()));
			} else {
				filter = CriteriaBuilderHelper.and(
					cb,
					filter,
					cb.between(from.get(AggregateReport.YEAR), criteria.getEpiWeekFrom().getYear(), criteria.getEpiWeekTo().getYear()));
				filter = CriteriaBuilderHelper.and(
					cb,
					filter,
					cb.between(from.get(AggregateReport.EPI_WEEK), criteria.getEpiWeekFrom().getWeek(), criteria.getEpiWeekTo().getWeek()));
			}
		}

		if (criteria.getDisease() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(AggregateReport.DISEASE), criteria.getDisease()));
		}

		if (criteria.getReportingUser() != null) {
			Join<AggregateReport, User> aggregateReportUserJoin = from.join(AggregateReport.REPORTING_USER, JoinType.INNER);
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(aggregateReportUserJoin.get(User.UUID), criteria.getReportingUser().getUuid()));
		}

		return filter;
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, AggregateReport> from) {
		return createUserFilter(new AggregateReportQueryContext(cb, cq, from));
	}

	@SuppressWarnings("rawtypes")
	public Predicate createUserFilter(AggregateReportQueryContext queryContext) {

		CriteriaBuilder cb = queryContext.getCriteriaBuilder();
		From<?, AggregateReport> from = queryContext.getRoot();
		AggregateReportJoins joins = queryContext.getJoins();

		User currentUser = getCurrentUser();
		if (currentUser == null) {
			return null;
		}

		final JurisdictionLevel jurisdictionLevel = currentUser.getJurisdictionLevel();
		if ((jurisdictionLevel == JurisdictionLevel.NATION && !UserRole.isPortHealthUser(currentUser.getUserRoles()))) {
			return null;
		}

		// Whoever created the weekly report is allowed to access it
		Join<AggregateReport, User> reportingUser = joins.getReportingUser();
		Predicate filter = cb.equal(reportingUser, currentUser);
		boolean isPortHealthUser = userService.isPortHealthUser();
		switch (jurisdictionLevel) {
		case REGION:
			final Region region = currentUser.getRegion();
			if (region != null) {
				Predicate regionPredicate = cb.equal(from.get(AggregateReport.REGION), region);
				if (isPortHealthUser) {
					Join<AggregateReport, PointOfEntry> pointOfEntryJoin = from.join(AggregateReport.POINT_OF_ENTRY, JoinType.LEFT);
					regionPredicate = cb.and(regionPredicate, cb.equal(pointOfEntryJoin.get(PointOfEntry.REGION), region));
				}
				filter = cb.or(filter, regionPredicate);
			}
			break;
		case DISTRICT:
			final District district = currentUser.getDistrict();
			if (district != null) {
				Predicate districtPredicate = cb.equal(from.get(AggregateReport.DISTRICT), district);
				if (isPortHealthUser) {
					Join<AggregateReport, PointOfEntry> pointOfEntryJoin = from.join(AggregateReport.POINT_OF_ENTRY, JoinType.LEFT);
					districtPredicate = cb.and(
						districtPredicate,
						cb.equal(pointOfEntryJoin.get(PointOfEntry.REGION), currentUser.getRegion()),
						cb.equal(pointOfEntryJoin.get(PointOfEntry.DISTRICT), district));
				}
				filter = cb.or(filter, districtPredicate);
			}
			break;
		case HEALTH_FACILITY:
			final Facility healthFacility = currentUser.getHealthFacility();
			if (healthFacility != null) {
				filter = cb.or(filter, cb.equal(from.get(AggregateReport.HEALTH_FACILITY), healthFacility));
			}
			break;
		case POINT_OF_ENTRY:
			final PointOfEntry pointOfEntry = currentUser.getPointOfEntry();
			if (pointOfEntry != null) {
				filter = cb.or(filter, cb.equal(from.get(AggregateReport.POINT_OF_ENTRY), pointOfEntry));
			}
			break;
		case NATION:
			if (isPortHealthUser) {
				filter = cb.or(filter, cb.isNotNull(from.get(AggregateReport.POINT_OF_ENTRY)));
			}
			break;
		default:
		}

		return filter;
	}

	public List<AggregateReport> findBy(AggregateReportCriteria aggregateReportCriteria, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AggregateReport> cq = cb.createQuery(getElementClass());
		Root<AggregateReport> from = cq.from(getElementClass());

		Predicate filter = createCriteriaFilter(aggregateReportCriteria, cb, cq, from);

		if (user != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(cb, cq, from));
		}
		if (filter != null) {
			cq.where(filter);
		}

		return em.createQuery(cq).getResultList();
	}
}
