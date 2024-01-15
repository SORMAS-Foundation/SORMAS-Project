package de.symeda.sormas.backend.environment;

import java.sql.Timestamp;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.RequestContextHolder;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.environment.EnvironmentCriteria;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;
import de.symeda.sormas.backend.common.ChangeDateBuilder;
import de.symeda.sormas.backend.common.ChangeDateFilterBuilder;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.common.DeletableAdo;
import de.symeda.sormas.backend.environment.environmentsample.EnvironmentSample;
import de.symeda.sormas.backend.environment.environmentsample.EnvironmentSampleService;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;

@Stateless
@LocalBean
public class EnvironmentService extends AbstractCoreAdoService<Environment, EnvironmentJoins> {

	private static final double ALLOWED_GPS_SIMILARITY_VARIANCE = 0.2d;

	@EJB
	private UserService userService;
	@EJB
	private EnvironmentSampleService environmentSampleService;

	public EnvironmentService() {
		super(Environment.class, DeletableEntityType.ENVIRONMENT);
	}

	public String getSimilarEnvironmentUuid(EnvironmentCriteria criteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Environment> root = cq.from(Environment.class);
		EnvironmentQueryContext queryContext = new EnvironmentQueryContext(cb, cq, root);

		cq.select(root.get(Environment.UUID));
		cq.where(buildSimilarityFilters(criteria, cb, root, queryContext));

		List<String> results = em.createQuery(cq).getResultList();
		return results.isEmpty() ? null : results.get(0);
	}

	@Override
	protected Predicate createUserFilterInternal(CriteriaBuilder cb, CriteriaQuery cq, From<?, Environment> from) {
		return createUserFilter(new EnvironmentQueryContext(cb, cq, from));
	}

	public Predicate createUserFilter(EnvironmentQueryContext queryContext) {

		User currentUser = getCurrentUser();
		if (currentUser == null) {
			return null;
		}
		final JurisdictionLevel jurisdictionLevel = currentUser.getJurisdictionLevel();
		Predicate filter = null;

		@SuppressWarnings("rawtypes")
		final CriteriaBuilder cb = queryContext.getCriteriaBuilder();
		final EnvironmentJoins environmentJoins = queryContext.getJoins();
		final From<?, Environment> environmentJoin = queryContext.getRoot();

		if (currentUserHasRestrictedAccessToAssignedEntities()) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.equal(environmentJoin.get(Environment.RESPONSIBLE_USER).get(User.ID), currentUser.getId()));
		} else {
			if (jurisdictionLevel != JurisdictionLevel.NATION) {
				switch (jurisdictionLevel) {
				case REGION:
					if (currentUser.getRegion() != null) {
						filter = CriteriaBuilderHelper
							.or(cb, filter, cb.equal(environmentJoins.getLocation().get(Location.REGION), currentUser.getRegion()));
					}
					break;
				case DISTRICT:
					if (currentUser.getDistrict() != null) {
						filter = CriteriaBuilderHelper
							.or(cb, filter, cb.equal(environmentJoins.getLocation().get(Location.DISTRICT), currentUser.getDistrict()));
					}
					break;
				case COMMUNITY:
					if (currentUser.getCommunity() != null) {
						filter = CriteriaBuilderHelper
							.or(cb, filter, cb.equal(environmentJoins.getLocation().get(Location.COMMUNITY), currentUser.getCommunity()));
					}
					break;
				default:
				}

				Predicate filterResponsible = cb.equal(environmentJoins.getRoot().get(Environment.REPORTING_USER), currentUser);
				filterResponsible = cb.or(filterResponsible, cb.equal(environmentJoins.getRoot().get(Environment.RESPONSIBLE_USER), currentUser));

				if (filter != null) {
					filter = CriteriaBuilderHelper.or(cb, filter, filterResponsible, createEnvironmentSampleFilter(queryContext));
				} else {
					filter = filterResponsible;
				}
			}
		}

		if (RequestContextHolder.isMobileSync()) {
			Predicate limitedChangeDatePredicate = CriteriaBuilderHelper.and(cb, createLimitedChangeDateFilter(cb, environmentJoin));
			if (limitedChangeDatePredicate != null) {
				filter = CriteriaBuilderHelper.and(cb, filter, limitedChangeDatePredicate);
			}
		}

		return filter;
	}

	private Predicate createEnvironmentSampleFilter(EnvironmentQueryContext context) {

		From<EnvironmentSample, Location> environmentSampleLocations = context.getJoins().getEnvironmentSampleJoins().getLocation();

		CriteriaBuilder cb = context.getCriteriaBuilder();

		final User currentUser = getCurrentUser();
		final JurisdictionLevel jurisdictionLevel = currentUser.getJurisdictionLevel();

		switch (jurisdictionLevel) {
		case REGION:
			return cb.equal(environmentSampleLocations.get(Location.REGION).get(Region.ID), currentUser.getRegion().getId());
		case DISTRICT:
			return cb.equal(environmentSampleLocations.get(Location.DISTRICT).get(District.ID), currentUser.getDistrict().getId());
		case COMMUNITY:
			return cb.equal(environmentSampleLocations.get(Location.COMMUNITY).get(Community.ID), currentUser.getCommunity().getId());
		default:
			return null;
		}
	}

	@Override
	protected EnvironmentJoins toJoins(From<?, Environment> adoPath) {
		return new EnvironmentJoins(adoPath);
	}

	@Override
	public Predicate inJurisdictionOrOwned(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, Environment> from) {
		return inJurisdictionOrOwned(new EnvironmentQueryContext(cb, query, from));
	}

	public Predicate inJurisdictionOrOwned(EnvironmentQueryContext qc) {
		return EnvironmentJurisdictionPredicateValidator.of(qc, userService.getCurrentUser()).inJurisdictionOrOwned();
	}

	@Override
	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<?, Environment> eventPath, Timestamp date) {
		return addChangeDates(new ChangeDateFilterBuilder(cb, date), toJoins(eventPath), false).build();
	}

	@Override
	protected <T extends ChangeDateBuilder<T>> T addChangeDates(T builder, EnvironmentJoins joins, boolean includeExtendedChangeDateFilters) {
		final From<?, Environment> environmentFrom = joins.getRoot();
		builder = super.addChangeDates(builder, joins, includeExtendedChangeDateFilters).add(environmentFrom, Environment.LOCATION);

		return builder;
	}

	public static Predicate buildGpsCoordinatesFilter(
		Double gpsLatFrom,
		Double gpsLatTo,
		Double gpsLonFrom,
		Double gpsLonTo,
		CriteriaBuilder cb,
		EnvironmentJoins joins) {
		Predicate filter = null;
		if (gpsLatFrom != null && gpsLatTo != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.between(joins.getLocation().get(Location.LATITUDE), gpsLatFrom, gpsLatTo));
		} else if (gpsLatFrom != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.greaterThanOrEqualTo(joins.getLocation().get(Location.LATITUDE), gpsLatFrom));
		} else if (gpsLatTo != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.lessThanOrEqualTo(joins.getLocation().get(Location.LATITUDE), gpsLatTo));
		}
		if (gpsLonFrom != null && gpsLonTo != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.between(joins.getLocation().get(Location.LONGITUDE), gpsLonFrom, gpsLonTo));
		} else if (gpsLonFrom != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.greaterThanOrEqualTo(joins.getLocation().get(Location.LONGITUDE), gpsLonFrom));
		} else if (gpsLonTo != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.lessThanOrEqualTo(joins.getLocation().get(Location.LONGITUDE), gpsLonTo));
		}
		return filter;
	}

	public Predicate buildCriteriaFilter(EnvironmentCriteria environmentCriteria, EnvironmentQueryContext environmentQueryContext) {

		if (environmentCriteria == null) {
			return null;
		}

		CriteriaBuilder cb = environmentQueryContext.getCriteriaBuilder();
		From<?, Environment> from = environmentQueryContext.getRoot();
		final EnvironmentJoins joins = environmentQueryContext.getJoins();

		Predicate filter = null;

		if (StringUtils.isNotEmpty(environmentCriteria.getFreeText())) {
			String[] textFilters = environmentCriteria.getFreeText().split("\\s+");
			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				Predicate likeFilters = cb.or(
					CriteriaBuilderHelper.ilike(cb, from.get(Environment.UUID), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, from.get(Environment.EXTERNAL_ID), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, from.get(Environment.ENVIRONMENT_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, from.get(Environment.DESCRIPTION), textFilter));
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}
		if (environmentCriteria.getRegion() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getRegion().get(Region.UUID), environmentCriteria.getRegion().getUuid()));
		}
		if (environmentCriteria.getDistrict() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getDistrict().get(District.UUID), environmentCriteria.getDistrict().getUuid()));
		}
		if (environmentCriteria.getCommunity() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(joins.getCommunity().get(Community.UUID), environmentCriteria.getCommunity().getUuid()));
		}
		if (environmentCriteria.getReportDateFrom() != null && environmentCriteria.getReportDateTo() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.between(from.get(Environment.REPORT_DATE), environmentCriteria.getReportDateFrom(), environmentCriteria.getReportDateTo()));
		} else if (environmentCriteria.getReportDateFrom() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.greaterThanOrEqualTo(from.get(Environment.REPORT_DATE), environmentCriteria.getReportDateFrom()));
		} else if (environmentCriteria.getReportDateTo() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.lessThanOrEqualTo(from.get(Environment.REPORT_DATE), environmentCriteria.getReportDateTo()));
		}
		if (environmentCriteria.getInvestigationStatus() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(from.get(Environment.INVESTIGATION_STATUS), environmentCriteria.getInvestigationStatus()));
		}
		if (environmentCriteria.getEnvironmentMedia() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Environment.ENVIRONMENT_MEDIA), environmentCriteria.getEnvironmentMedia()));
		}
		if (environmentCriteria.getRelevanceStatus() != null) {
			if (environmentCriteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter = CriteriaBuilderHelper
					.and(cb, filter, cb.or(cb.equal(from.get(Environment.ARCHIVED), false), cb.isNull(from.get(Environment.ARCHIVED))));
			} else if (environmentCriteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Environment.ARCHIVED), true));
			} else if (environmentCriteria.getRelevanceStatus() == EntityRelevanceStatus.DELETED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Environment.DELETED), true));
			}
		}
		if (environmentCriteria.getRelevanceStatus() != EntityRelevanceStatus.DELETED) {
			filter = CriteriaBuilderHelper.and(cb, filter, createDefaultFilter(cb, from));
		}
		if (environmentCriteria.getResponsibleUser() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(joins.getResponsibleUser().get(User.UUID), environmentCriteria.getResponsibleUser().getUuid()));
		}
		filter = CriteriaBuilderHelper.and(
			cb,
			filter,
			buildGpsCoordinatesFilter(
				environmentCriteria.getGpsLatFrom(),
				environmentCriteria.getGpsLatTo(),
				environmentCriteria.getGpsLonFrom(),
				environmentCriteria.getGpsLonTo(),
				cb,
				joins));

		return filter;
	}

	private Predicate buildSimilarityFilters(
		EnvironmentCriteria criteria,
		CriteriaBuilder cb,
		Root<Environment> root,
		EnvironmentQueryContext queryContext) {

		// Environment media and GPS coordinates must always be present for environments to be considered as duplicates
		if (criteria.getEnvironmentMedia() == null || criteria.getGpsLat() == null || criteria.getGpsLon() == null) {
			return cb.disjunction();
		}

		EnvironmentJoins joins = queryContext.getJoins();

		Predicate filter = createDefaultFilter(cb, root);

		// Environment media
		filter = CriteriaBuilderHelper.and(
			cb,
			filter,
			cb.and(
				cb.isNotNull(root.get(Environment.ENVIRONMENT_MEDIA)),
				cb.equal(root.get(Environment.ENVIRONMENT_MEDIA), criteria.getEnvironmentMedia())));

		// GPS coordinates
		filter = CriteriaBuilderHelper.and(
			cb,
			filter,
			cb.and(
				cb.isNotNull(joins.getLocation().get(Location.LATITUDE)),
				cb.greaterThanOrEqualTo(joins.getLocation().get(Location.LATITUDE), criteria.getGpsLat() - ALLOWED_GPS_SIMILARITY_VARIANCE),
				cb.lessThanOrEqualTo(joins.getLocation().get(Location.LATITUDE), criteria.getGpsLat() + ALLOWED_GPS_SIMILARITY_VARIANCE)));
		filter = CriteriaBuilderHelper.and(
			cb,
			filter,
			cb.and(
				cb.isNotNull(joins.getLocation().get(Location.LONGITUDE)),
				cb.greaterThanOrEqualTo(joins.getLocation().get(Location.LONGITUDE), criteria.getGpsLon() - ALLOWED_GPS_SIMILARITY_VARIANCE),
				cb.lessThanOrEqualTo(joins.getLocation().get(Location.LONGITUDE), criteria.getGpsLon() + ALLOWED_GPS_SIMILARITY_VARIANCE)));

		CountryReferenceDto criteriaCountry = criteria.getCountry();
		RegionReferenceDto criteriaRegion = criteria.getRegion();
		DistrictReferenceDto criteriaDistrict = criteria.getDistrict();
		// Country
		if (criteriaCountry != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.or(cb.isNull(joins.getCountry()), cb.equal(joins.getCountry().get(Country.UUID), criteria.getCountry().getUuid())));
		}
		// Region
		if (criteriaRegion != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.or(cb.isNull(joins.getRegion()), cb.equal(joins.getRegion().get(Region.UUID), criteria.getRegion().getUuid())));
		}
		// District
		if (criteriaDistrict != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.or(cb.isNull(joins.getDistrict()), cb.equal(joins.getDistrict().get(District.UUID), criteria.getDistrict().getUuid())));
		}

		// External ID
		if (StringUtils.isNotBlank(criteria.getExternalId())) {
			filter = CriteriaBuilderHelper.or(
				cb,
				cb.and(cb.isNull(root.get(Environment.EXTERNAL_ID)), filter),
				cb.equal(root.get(Environment.EXTERNAL_ID), criteria.getExternalId()));
		}

		return filter;
	}

	/**
	 * Creates a default filter that should be used as the basis of queries that do not use {@link EventCriteria}.
	 * This essentially removes {@link DeletableAdo#isDeleted()} events from the queries.
	 */
	public Predicate createDefaultFilter(CriteriaBuilder cb, From<?, Environment> root) {
		return cb.isFalse(root.get(Environment.DELETED));
	}

	public Predicate createActiveEnvironmentFilter(CriteriaBuilder cb, Path<Environment> root) {
		return cb.and(cb.isFalse(root.get(Environment.ARCHIVED)), cb.isFalse(root.get(Environment.DELETED)));
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected Predicate createRelevantDataFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Environment> from) {

		Predicate filter = createActiveEnvironmentFilter(cb, from);
		if (getCurrentUser() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, createUserFilterInternal(cb, cq, from));
		}

		return filter;
	}

	@Override
	public EditPermissionType getEditPermissionType(Environment environment) {
		if (!inJurisdictionOrOwned(environment)) {
			return EditPermissionType.OUTSIDE_JURISDICTION;
		}

		if (currentUserHasRestrictedAccessToAssignedEntities() && !DataHelper.equal(environment.getResponsibleUser(), getCurrentUser())) {
			return EditPermissionType.REFUSED;
		}

		return super.getEditPermissionType(environment);
	}

	public List<String> getAllActiveUuids(User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Environment> from = cq.from(getElementClass());
		EnvironmentQueryContext environmentQueryContext = new EnvironmentQueryContext(cb, cq, from);

		Predicate filter = createActiveEnvironmentFilter(cb, from);

		if (user != null) {
			Predicate userFilter = createUserFilter(environmentQueryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, userFilter);
		}

		if (RequestContextHolder.isMobileSync()) {
			Predicate predicate = createLimitedChangeDateFilter(cb, from);
			if (predicate != null) {
				filter = CriteriaBuilderHelper.and(cb, filter, predicate);
			}
		}

		cq.where(filter);
		cq.select(from.get(Environment.UUID));

		return em.createQuery(cq).getResultList();
	}

	@Override
	public void delete(Environment environment, DeletionDetails deletionDetails) {
		environment.getEnvironmentSamples().forEach(s -> environmentSampleService.delete(s, deletionDetails));
		super.delete(environment, deletionDetails);
	}

	@Override
	public void restore(Environment environment) {
		environment.getEnvironmentSamples().forEach(sample -> environmentSampleService.restore(sample));
		super.restore(environment);
	}
}
