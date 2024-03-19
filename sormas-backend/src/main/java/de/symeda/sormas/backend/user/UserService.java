/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package de.symeda.sormas.backend.user;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.collections4.CollectionUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.infrastructure.InfrastructureHelper;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.NotificationProtocol;
import de.symeda.sormas.api.user.NotificationType;
import de.symeda.sormas.api.user.UserCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.api.user.UserRoleReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DefaultEntityHelper;
import de.symeda.sormas.api.utils.PasswordHelper;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilterAndJurisdiction;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.infrastructure.InfrastructureAdo;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.community.CommunityService;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.facility.FacilityService;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntryService;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.user.event.UserUpdateEvent;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless
@LocalBean
public class UserService extends AdoServiceWithUserFilterAndJurisdiction<User> {

	@EJB
	private UserRoleFacadeEjb.UserRoleFacadeEjbLocal userRoleFacade;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private CommunityService communityService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private PointOfEntryService pointOfEntryService;
	@Inject
	private javax.enterprise.event.Event<UserUpdateEvent> userUpdateEvent;

	public UserService() {
		super(User.class);
	}

	public User createUser() {

		User user = new User();
		// dummy password to make sure no one can login with this user
		String password = PasswordHelper.createPass(12);
		user.setSeed(PasswordHelper.createPass(16));
		user.setPassword(PasswordHelper.encodePassword(password, user.getSeed()));
		return user;
	}

	/**
	 * Fetches a use from the DB by its username. The check is done case-insensitive.
	 * 
	 * @param userName
	 *            The username in any casing.
	 * @return The corresponding User object from the DB.
	 */
	public User getByUserName(String userName) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		ParameterExpression<String> userNameParam = cb.parameter(String.class, User.USER_NAME);
		CriteriaQuery<User> cq = cb.createQuery(getElementClass());
		Root<User> from = cq.from(getElementClass());

		// lowercase everything for case-insensitive check
		Expression<String> userNameExpression = cb.lower(from.get(User.USER_NAME));
		String userNameParamValue = userName.toLowerCase();

		cq.where(cb.equal(userNameExpression, userNameParam));

		TypedQuery<User> q = em.createQuery(cq).setParameter(userNameParam, userNameParamValue);

		return q.getResultList().stream().findFirst().orElse(null);
	}

	public List<User> getAllByRegionsAndNotificationTypes(
		List<Region> regions,
		NotificationProtocol notificationProtocol,
		Collection<NotificationType> notificationTypes,
		boolean fetchNotificationTypes) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(getElementClass());
		Root<User> from = cq.from(getElementClass());

		Predicate filter = from.get(User.REGION).in(regions);
		if (!notificationTypes.isEmpty()) {
			Join<User, UserRole> rolesJoin = from.join(User.USER_ROLES, JoinType.LEFT);
			Join<UserRole, NotificationType> notificationsJoin = rolesJoin.join(
				NotificationProtocol.EMAIL.equals(notificationProtocol) ? UserRole.EMAIL_NOTIFICATIONS : UserRole.SMS_NOTIFICATIONS,
				JoinType.LEFT);
			Predicate notificationsFilter = notificationsJoin.in(notificationTypes);
			filter = CriteriaBuilderHelper.and(cb, filter, notificationsFilter);
		}
		if (fetchNotificationTypes) {
			from.fetch(User.USER_ROLES, JoinType.LEFT)
				.fetch(
					NotificationProtocol.EMAIL.equals(notificationProtocol) ? UserRole.EMAIL_NOTIFICATIONS : UserRole.SMS_NOTIFICATIONS,
					JoinType.LEFT);
		}
		cq.where(CriteriaBuilderHelper.and(cb, filter, createDefaultFilter(cb, from)));

		cq.distinct(true).orderBy(cb.asc(from.get(AbstractDomainObject.ID)));

		return em.createQuery(cq).getResultList();
	}

	/**
	 * @param districts
	 * @param userRights
	 * @return List of users with specified UserRights on district level in the specified districts
	 */
	public List<User> getAllByDistrictsAndUserRights(List<District> districts, Collection<UserRight> userRights) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(getElementClass());
		Root<User> from = cq.from(getElementClass());

		Predicate filter = from.get(User.DISTRICT).in(districts);
		filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(User.JURISDICTION_LEVEL), JurisdictionLevel.DISTRICT));
		filter = CriteriaBuilderHelper.and(cb, filter, buildUserRightsFilter(from, userRights));
		cq.where(CriteriaBuilderHelper.and(cb, filter, createDefaultFilter(cb, from)));
		cq.distinct(true).orderBy(cb.asc(from.get(AbstractDomainObject.ID)));

		return em.createQuery(cq).getResultList();
	}

	/**
	 * Loads users filtered by combinable filter conditions.<br />
	 * Condition combination if parameter is set:<br />
	 * {@code ((regionUuids & districtUuids & filterByJurisdiction & userRoles) | includeSupervisors) & activeOnly}
	 * 
	 * @see #createCurrentUserJurisdictionFilter(CriteriaBuilder, From)
	 * @param regionUuids
	 * @param districtUuids
	 * @param filterByCurrentUserJurisdiction
	 * @param activeOnly
	 * @param userRights
	 */
	public List<UserReference> getUserReferences(List<String> regionUuids, List<String> districtUuids, boolean activeOnly, UserRight... userRights) {

		return getUserReferences(regionUuids, districtUuids, null, activeOnly, userRights);
	}

	public List<UserReference> getUserReferences(
		List<String> regionUuids,
		List<String> districtUuids,
		List<String> communityUuids,
		boolean activeOnly,
		UserRight... userRights) {
		return getUserReferences(regionUuids, districtUuids, communityUuids, activeOnly, null, userRights);
	}

	public List<UserReference> getUserReferences(
		List<String> regionUuids,
		List<String> districtUuids,
		List<String> communityUuids,
		boolean activeOnly,
		Disease limitedDisease,
		UserRight... userRights) {
		return getUserReferences(regionUuids, districtUuids, communityUuids, activeOnly, limitedDisease, false, Arrays.asList(userRights));
	}

	/**
	 * Loads users filtered by combinable filter conditions.<br />
	 * Condition combination if parameter is set:<br />
	 * {@code ((regionUuids & districtUuids & communityUuids & filterByJurisdiction & userRoles) | includeSupervisors) & activeOnly}
	 *
	 * @see #createCurrentUserJurisdictionFilter(CriteriaBuilder, From)
	 * @param regionUuids
	 * @param districtUuids
	 * @param communityUuids
	 * @param filterByJurisdiction
	 * @param activeOnly
	 * @param userRights
	 */
	public List<UserReference> getUserReferences(
		List<String> regionUuids,
		List<String> districtUuids,
		List<String> communityUuids,
		boolean activeOnly,
		Disease limitedDisease,
		boolean excludeLimitedDiseaseUsers,
		List<UserRight> userRights) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UserReference> cq = cb.createQuery(UserReference.class);
		Root<UserReference> root = cq.from(UserReference.class);
		Root<User> userRoot = cq.from(User.class);
		cq.select(root);

		// WHERE inner AND
		Predicate filter = null;
		boolean userEntityJoinUsed = false;

		if (CollectionUtils.isNotEmpty(regionUuids)) {
			Join<User, Region> regionJoin = userRoot.join(User.REGION, JoinType.LEFT);
			filter = CriteriaBuilderHelper.and(cb, filter, cb.in(regionJoin.get(AbstractDomainObject.UUID)).value(regionUuids));
			userEntityJoinUsed = true;
		}
		if (CollectionUtils.isNotEmpty(districtUuids)) {
			Join<User, District> districtJoin = userRoot.join(User.DISTRICT, JoinType.LEFT);
			Join<User, Region> userRegionJoin = userRoot.join(User.REGION, JoinType.LEFT);
			Join<Region, District> districtRegionJoin = userRegionJoin.join(Region.DISTRICTS, JoinType.LEFT);

			Predicate districtFilter = cb.or(
				cb.in(districtJoin.get(AbstractDomainObject.UUID)).value(districtUuids),
				cb.and(
					cb.in(districtRegionJoin.get(AbstractDomainObject.UUID)).value(districtUuids),
					cb.equal(root.get(UserReference.JURISDICTION_LEVEL), JurisdictionLevel.REGION)));

			filter = CriteriaBuilderHelper.and(cb, filter, districtFilter);
			userEntityJoinUsed = true;
		}
		if (!hasRight(UserRight.SEE_PERSONAL_DATA_OUTSIDE_JURISDICTION)) {
			filter = CriteriaBuilderHelper.and(cb, filter, createCurrentUserJurisdictionFilter(cb, userRoot));
			userEntityJoinUsed = true;
		}

		filter = CriteriaBuilderHelper.and(cb, filter, buildUserRightsFilter(userRoot, userRights));

		if (userEntityJoinUsed) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(root.get(UserReference.ID), userRoot.get(AbstractDomainObject.ID)));
		}

		// WHERE outer AND
		if (activeOnly) {
			filter = CriteriaBuilderHelper.and(cb, filter, createDefaultFilter(cb, root));
		}

		// eliminate users that are limited to others diseases
		if (limitedDisease != null) {
			Predicate restrictOtherLimitedDiseaseUsers = cb.or(
				cb.isNull(userRoot.get(User.LIMITED_DISEASES)),
				cb.like(userRoot.get(User.LIMITED_DISEASES).as(String.class), "%" + limitedDisease.name() + '%'));
			filter = CriteriaBuilderHelper.and(cb, filter, restrictOtherLimitedDiseaseUsers);
		}

		//exlude users with limited diseases
		if (excludeLimitedDiseaseUsers) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isNull(userRoot.get(User.LIMITED_DISEASES)));
		}

		if (CollectionUtils.isNotEmpty(communityUuids)) {
			Join<User, Community> communityJoin = userRoot.join(User.COMMUNITY, JoinType.LEFT);
			filter = CriteriaBuilderHelper.and(cb, filter, cb.in(communityJoin.get(AbstractDomainObject.UUID)).value(communityUuids));
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.distinct(true);
		cq.orderBy(cb.asc(root.get(AbstractDomainObject.ID)));

		return em.createQuery(cq).setHint(ModelConstants.READ_ONLY, true).getResultList();
	}

	public List<UserReference> getUserRefsByInfrastructure(
		String infrastructureUuid,
		JurisdictionLevel jurisdictionLevel,
		JurisdictionLevel allowedJurisdictionLevel,
		Disease limitedDisease,
		UserRight... userRights) {

		if (jurisdictionLevel.getOrder() < allowedJurisdictionLevel.getOrder()) {
			return Collections.emptyList();
		}

		InfrastructureAdo baseInfrastructure;
		switch (jurisdictionLevel) {
		case HEALTH_FACILITY:
		case LABORATORY:
		case EXTERNAL_LABORATORY:
			baseInfrastructure = facilityService.getByUuid(infrastructureUuid);
			break;
		case POINT_OF_ENTRY:
			baseInfrastructure = pointOfEntryService.getByUuid(infrastructureUuid);
			break;
		case COMMUNITY:
			baseInfrastructure = communityService.getByUuid(infrastructureUuid);
			break;
		case DISTRICT:
			baseInfrastructure = districtService.getByUuid(infrastructureUuid);
			break;
		case REGION:
			baseInfrastructure = regionService.getByUuid(infrastructureUuid);
			break;
		case NATION:
			baseInfrastructure = null;
			break;
		default:
			throw new IllegalArgumentException("Unsupported jurisdiction level: " + jurisdictionLevel.name());
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UserReference> cq = cb.createQuery(UserReference.class);
		Root<UserReference> root = cq.from(UserReference.class);
		Root<User> userRoot = cq.from(User.class);
		UserQueryContext queryContext = new UserQueryContext(cb, cq, userRoot);
		UserJoins joins = queryContext.getJoins();
		cq.select(root);

		Predicate filter = CriteriaBuilderHelper.and(cb, createDefaultFilter(cb, root), cb.equal(root.get(UserReference.ID), userRoot.get(User.ID)));

		Predicate jurisdictionFilter = null;
		while (jurisdictionLevel.getOrder() >= allowedJurisdictionLevel.getOrder()) {
			Predicate jurisdictionPredicate = createUserRefsByInfrastructurePredicate(
				cb,
				joins,
				baseInfrastructure != null ? baseInfrastructure.getUuid() : null,
				jurisdictionLevel);
			jurisdictionFilter = CriteriaBuilderHelper.or(cb, jurisdictionFilter, jurisdictionPredicate);

			if (jurisdictionLevel.getOrder() > 1) {
				jurisdictionLevel = baseInfrastructure instanceof Facility && ((Facility) baseInfrastructure).getCommunity() != null
					? JurisdictionLevel.COMMUNITY
					: InfrastructureHelper.getSuperordinateJurisdiction(jurisdictionLevel);
				if (jurisdictionLevel.getOrder() > 1 && baseInfrastructure != null) {
					baseInfrastructure = JurisdictionHelper.getParentInfrastructure(baseInfrastructure, jurisdictionLevel);
				}
			} else {
				break;
			}
		}

		filter = CriteriaBuilderHelper.and(cb, filter, jurisdictionFilter);

		if (limitedDisease != null) {
			Predicate restrictOtherLimitedDiseaseUsers = cb.or(
				cb.isNull(userRoot.get(User.LIMITED_DISEASES)),
				cb.like(userRoot.get(User.LIMITED_DISEASES).as(String.class), "%" + limitedDisease.name() + "%"));
			filter = CriteriaBuilderHelper.and(cb, filter, restrictOtherLimitedDiseaseUsers);
		}

		filter = CriteriaBuilderHelper.and(cb, filter, buildUserRightsFilter(userRoot, Arrays.asList(userRights)));

		cq.where(filter);
		cq.distinct(true);
		cq.orderBy(cb.asc(root.get(AbstractDomainObject.ID)));

		return em.createQuery(cq).setHint(ModelConstants.READ_ONLY, true).getResultList();
	}

	private Predicate createUserRefsByInfrastructurePredicate(
		CriteriaBuilder cb,
		UserJoins joins,
		String infrastructureUuid,
		JurisdictionLevel jurisdictionLevel) {

		Predicate predicate = null;

		switch (jurisdictionLevel) {
		case HEALTH_FACILITY:
			predicate = cb.equal(joins.getHealthFacility().get(Facility.UUID), infrastructureUuid);
			break;
		case LABORATORY:
		case EXTERNAL_LABORATORY:
			predicate = cb.equal(joins.getLaboratory().get(Facility.UUID), infrastructureUuid);
			break;
		case COMMUNITY:
			predicate = cb.and(
				cb.isNull(joins.getHealthFacility()),
				cb.isNull(joins.getLaboratory()),
				cb.equal(joins.getCommunity().get(Community.UUID), infrastructureUuid));
			break;
		case POINT_OF_ENTRY:
			predicate = cb.equal(joins.getPointOfEntry().get(PointOfEntry.UUID), infrastructureUuid);
			break;
		case DISTRICT:
			predicate = cb.and(
				cb.isNull(joins.getCommunity()),
				cb.isNull(joins.getHealthFacility()),
				cb.isNull(joins.getLaboratory()),
				cb.isNull(joins.getPointOfEntry()),
				cb.equal(joins.getDistrict().get(District.UUID), infrastructureUuid));
			break;
		case REGION:
			predicate = cb.and(
				cb.isNull(joins.getDistrict()),
				cb.isNull(joins.getLaboratory()),
				cb.equal(joins.getRegion().get(Region.UUID), infrastructureUuid));
			break;
		case NATION:
			predicate = cb.and(cb.isNull(joins.getRegion()), cb.isNull(joins.getLaboratory()));
			break;
		default:
			break;
		}

		return predicate;
	}

	public List<UserReference> getUserReferencesByJurisdictions(
		List<String> regionUuids,
		List<String> districtUuids,
		List<String> communityUuids,
		Collection<JurisdictionLevel> jurisdictionLevels,
		Collection<UserRight> userRights) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UserReference> cq = cb.createQuery(UserReference.class);
		Root<UserReference> root = cq.from(UserReference.class);
		Root<User> userRoot = cq.from(User.class);
		cq.select(root);

		// WHERE inner AND
		Predicate filter = createDefaultFilter(cb, root);
		boolean userEntityJoinUsed = false;

		if (CollectionUtils.isNotEmpty(regionUuids)) {
			Join<User, Region> regionJoin = userRoot.join(User.REGION, JoinType.LEFT);
			filter = CriteriaBuilderHelper.and(cb, filter, cb.in(regionJoin.get(AbstractDomainObject.UUID)).value(regionUuids));
			userEntityJoinUsed = true;
		}
		if (CollectionUtils.isNotEmpty(districtUuids)) {
			Join<User, District> districtJoin = userRoot.join(User.DISTRICT, JoinType.LEFT);
			filter = CriteriaBuilderHelper.and(cb, filter, cb.in(districtJoin.get(AbstractDomainObject.UUID)).value(districtUuids));
			userEntityJoinUsed = true;
		}
		if (CollectionUtils.isNotEmpty(communityUuids)) {
			Join<User, Community> communityJoin = userRoot.join(User.COMMUNITY, JoinType.LEFT);
			filter = CriteriaBuilderHelper.and(cb, filter, cb.in(communityJoin.get(AbstractDomainObject.UUID)).value(communityUuids));
			userEntityJoinUsed = true;
		}
		if (CollectionUtils.isNotEmpty(jurisdictionLevels)) {
			filter = CriteriaBuilderHelper.and(cb, filter, root.get(UserReference.JURISDICTION_LEVEL).in(jurisdictionLevels));
		}
		Predicate userRightsFilter = buildUserRightsFilter(userRoot, userRights);
		if (userRightsFilter != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, userRightsFilter);
			userEntityJoinUsed = true;
		}

		if (userEntityJoinUsed) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(root.get(UserReference.ID), userRoot.get(AbstractDomainObject.ID)));
		}

		Join<Object, Object> rolesJoin = root.join(User.USER_ROLES, JoinType.LEFT);
		filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(rolesJoin.get(UserRole.RESTRICT_ACCESS_TO_ASSIGNED_ENTITIES), false));

		if (filter != null) {
			cq.where(filter);
		}

		cq.distinct(true);
		cq.orderBy(cb.asc(root.get(AbstractDomainObject.ID)));

		return em.createQuery(cq).setHint(ModelConstants.READ_ONLY, true).getResultList();
	}

	public List<UserReference> getUserReferencesByIds(Collection<Long> userIds) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UserReference> cq = cb.createQuery(UserReference.class);
		Root<UserReference> root = cq.from(UserReference.class);

		cq.where(root.get(UserReference.ID).in(userIds));

		return em.createQuery(cq).setHint(ModelConstants.READ_ONLY, true).getResultList();
	}

	public User getRandomDistrictUser(District district, UserRight... userRights) {

		return getRandomUser(
			getUserReferencesByJurisdictions(
				null,
				Collections.singletonList(district.getUuid()),
				null,
				Collections.singletonList(JurisdictionLevel.DISTRICT),
				Arrays.asList(userRights)));
	}

	public User getRandomRegionUser(Region region, UserRight... userRights) {

		return getRandomUser(
			getUserReferencesByJurisdictions(
				Collections.singletonList(region.getUuid()),
				null,
				null,
				Collections.singletonList(JurisdictionLevel.REGION),
				Arrays.asList(userRights)));
	}

	public User getRandomUser(List<UserReference> candidates) {

		if (CollectionUtils.isEmpty(candidates)) {
			return null;
		}

		UserReference chosenUser = candidates.get(new Random().nextInt(candidates.size()));
		return getByUuid(chosenUser.getUuid());
	}

	public List<User> getFacilityUsersOfHospital(Facility facility) {

		if (facility == null || !FacilityType.HOSPITAL.equals(facility.getType())) {
			throw new IllegalArgumentException("Facility needs to be a hospital");
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(getElementClass());
		Root<User> from = cq.from(getElementClass());

		Predicate filter = cb.and(
			createDefaultFilter(cb, from),
			cb.equal(from.get(User.HEALTH_FACILITY), facility),
			cb.equal(from.get(User.JURISDICTION_LEVEL), JurisdictionLevel.HEALTH_FACILITY));

		cq.where(filter).distinct(true);
		return em.createQuery(cq).getResultList();
	}

	public List<User> getLabUsersOfLab(Facility facility) {

		if (facility == null || facility.getType() != FacilityType.LABORATORY) {
			throw new IllegalArgumentException("Facility needs to be a laboratory");
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(getElementClass());
		Root<User> from = cq.from(getElementClass());

		Predicate filter = cb.and(
			createDefaultFilter(cb, from),
			cb.equal(from.get(User.LABORATORY), facility),
			from.get(User.JURISDICTION_LEVEL).in(JurisdictionLevel.LABORATORY, JurisdictionLevel.EXTERNAL_LABORATORY));

		cq.where(filter).distinct(true);

		return em.createQuery(cq).getResultList();
	}

	/**
	 * @param associatedOfficer
	 * @param userRights
	 * @return
	 */
	public List<User> getAllByAssociatedOfficer(User associatedOfficer, UserRight[] userRights) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(getElementClass());
		Root<User> from = cq.from(getElementClass());

		Predicate filter = cb.and(createDefaultFilter(cb, from), cb.equal(from.get(User.ASSOCIATED_OFFICER), associatedOfficer));
		filter = CriteriaBuilderHelper.and(cb, filter, buildUserRightsFilter(from, Arrays.asList(userRights)));
		cq.where(filter);

		cq.orderBy(cb.asc(from.get(AbstractDomainObject.ID)));

		return em.createQuery(cq).getResultList();
	}

	public Map<String, User> getResponsibleUsersByEventUuids(List<String> eventUuids) {

		Map<String, User> responsibleUserByEventUuid = new HashMap<>();
		IterableHelper.executeBatched(eventUuids, ModelConstants.PARAMETER_LIMIT, batchedEventUuids -> {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
			Root<Event> eventRoot = cq.from(Event.class);
			Join<Event, User> responsibleUserJoin = eventRoot.join(Event.RESPONSIBLE_USER, JoinType.LEFT);

			cq.where(cb.and(createDefaultFilter(cb, responsibleUserJoin), eventRoot.get(Event.UUID).in(batchedEventUuids)));
			cq.multiselect(eventRoot.get(Event.UUID), responsibleUserJoin);

			cq.orderBy(cb.asc(eventRoot.get(Event.UUID)));

			responsibleUserByEventUuid
				.putAll(em.createQuery(cq).getResultList().stream().collect(Collectors.toMap(row -> (String) row[0], row -> (User) row[1])));
		});
		return responsibleUserByEventUuid;
	}

	public boolean isLoginUnique(String excludedUuid, String userName) {
		User user = getByUserName(userName);
		return user == null || user.getUuid().equals(excludedUuid);
	}

	public String resetPassword(String userUuid) {
		User user = getByUuid(userUuid);
		if (user == null) {
			return null;
		}

		String password = PasswordHelper.createPass(12);
		user.setSeed(PasswordHelper.createPass(16));
		user.setPassword(PasswordHelper.encodePassword(password, user.getSeed()));
		ensurePersisted(user);

		return password;
	}

	public String generatePassword() {
      return PasswordHelper.generatePasswordWithSpecialChars(12);
	}

	public String updatePassword(String userUuid, String password) {
		User user = getByUuid(userUuid);

		if (user == null && password == null) {
			return null;
		}
		assert user != null;
		user.setPassword(PasswordHelper.encodePassword(password, user.getSeed()));
		ensurePersisted(user);
		return password;
	};

	public Predicate buildCriteriaFilter(UserCriteria userCriteria, CriteriaBuilder cb, Root<User> from) {

		Predicate filter = null;
		if (userCriteria.getActive() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(User.ACTIVE), userCriteria.getActive()));
		}
		if (userCriteria.getUserRole() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(from.join(User.USER_ROLES, JoinType.LEFT).get(AbstractDomainObject.UUID), userCriteria.getUserRole().getUuid()));
		}
		if (userCriteria.getRegion() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(from.join(User.REGION, JoinType.LEFT).get(AbstractDomainObject.UUID), userCriteria.getRegion().getUuid()));
		}
		if (userCriteria.getDistrict() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(from.join(User.DISTRICT, JoinType.LEFT).get(AbstractDomainObject.UUID), userCriteria.getDistrict().getUuid()));
		}
		if (userCriteria.getFreeText() != null) {
			String[] textFilters = userCriteria.getFreeText().split("\\s+");
			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				Predicate likeFilters = cb.or(
					CriteriaBuilderHelper.unaccentedIlike(cb, from.get(User.FIRST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, from.get(User.LAST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, from.get(User.USER_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, from.get(User.USER_EMAIL), textFilter),
					CriteriaBuilderHelper.ilike(cb, from.get(User.PHONE), textFilter),
					CriteriaBuilderHelper.ilike(cb, from.get(User.UUID), textFilter));
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}

		return filter;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, User> from) {
		// a user can read all other users
		return null;
	}

	public Predicate createCurrentUserJurisdictionFilter(CriteriaBuilder cb, From<?, User> from) {
		User currentUser = getCurrentUser();

		if (currentUser.getHealthFacility() != null) {
			return cb.equal(from.get(User.HEALTH_FACILITY), currentUser.getHealthFacility());
		} else if (currentUser.getPointOfEntry() != null) {
			return cb.equal(from.get(User.POINT_OF_ENTRY), currentUser.getPointOfEntry());
		} else if (currentUser.getLaboratory() != null) {
			return cb.equal(from.get(User.LABORATORY), currentUser.getLaboratory());
		} else if (currentUser.getCommunity() != null) {
			return cb.equal(from.get(User.COMMUNITY), currentUser.getCommunity());
		} else if (currentUser.getDistrict() != null) {
			return cb.equal(from.get(User.DISTRICT), currentUser.getDistrict());
		} else if (currentUser.getRegion() != null) {
			return cb.equal(from.get(User.REGION), currentUser.getRegion());
		} else {
			return cb.conjunction();
		}
	}

	public Predicate buildUserRightsFilter(Root<User> from, Collection<UserRight> userRights) {

		if (userRights != null && !userRights.isEmpty()) {
			Join<User, UserRole> rolesJoin = from.join(User.USER_ROLES, JoinType.LEFT);
			Join<UserRole, UserRight> rightsJoin = rolesJoin.join(UserRole.USER_RIGHTS, JoinType.LEFT);
			return rightsJoin.in(Collections.singletonList(userRights));
		}

		return null;
	}

	public Long countByAssignedOfficer(User officer, UserRight userRights) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<User> from = cq.from(getElementClass());
		Predicate filter = cb.equal(from.get(User.ASSOCIATED_OFFICER), officer);
		filter = CriteriaBuilderHelper.and(cb, filter, buildUserRightsFilter(from, Arrays.asList(userRights)));
		cq.where(CriteriaBuilderHelper.and(cb, filter, createDefaultFilter(cb, from)));
		cq.select(cb.count(from));
		return em.createQuery(cq).getSingleResult();
	}

	/**
	 *
	 * @param districts
	 * @param userRight
	 * @return Number of users with specified UserRight on district level
	 */
	public Long countByDistricts(List<District> districts, UserRight userRight) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<User> from = cq.from(getElementClass());
		Predicate filter = from.get(User.DISTRICT).in(districts);
		filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(User.JURISDICTION_LEVEL), JurisdictionLevel.DISTRICT));
		filter = CriteriaBuilderHelper.and(cb, filter, buildUserRightsFilter(from, Arrays.asList(userRight)));
		cq.where(CriteriaBuilderHelper.and(cb, filter, createDefaultFilter(cb, from)));
		cq.select(cb.count(from));
		return em.createQuery(cq).getSingleResult();
	}

	/**
	 *
	 * @param districts
	 * @return Number of users with specified UserRight on community level
	 */
	public Long countByCommunities(List<District> districts, UserRight userRight) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<User> from = cq.from(getElementClass());
		Predicate filter = from.get(User.COMMUNITY).get(Community.DISTRICT).in(districts);
		filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(User.JURISDICTION_LEVEL), JurisdictionLevel.COMMUNITY));
		filter = CriteriaBuilderHelper.and(cb, filter, buildUserRightsFilter(from, Arrays.asList(userRight)));
		cq.where(CriteriaBuilderHelper.and(cb, filter, createDefaultFilter(cb, from)));
		cq.select(cb.count(from));
		return em.createQuery(cq).getSingleResult();
	}

	/**
	 *
	 * @param districts
	 * @return Number of users with specified UserRight on health facility level
	 */
	public Long countByHealthFacilities(List<District> districts, UserRight userRight) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<User> from = cq.from(getElementClass());
		Predicate filter = from.get(User.HEALTH_FACILITY).get(Facility.DISTRICT).in(districts);
		filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(User.JURISDICTION_LEVEL), JurisdictionLevel.HEALTH_FACILITY));
		filter = CriteriaBuilderHelper.and(cb, filter, buildUserRightsFilter(from, Arrays.asList(userRight)));
		cq.where(CriteriaBuilderHelper.and(cb, filter, createDefaultFilter(cb, from)));
		cq.select(cb.count(from));
		return em.createQuery(cq).getSingleResult();
	}

	/**
	 *
	 * @param districts
	 * @return Number of users with specified UserRight on pointOfEntry level
	 */
	public Long countByPointOfEntries(List<District> districts, UserRight userRight) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<User> from = cq.from(getElementClass());
		Predicate filter = from.get(User.POINT_OF_ENTRY).get(PointOfEntry.DISTRICT).in(districts);
		filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(User.JURISDICTION_LEVEL), JurisdictionLevel.POINT_OF_ENTRY));
		filter = CriteriaBuilderHelper.and(cb, filter, buildUserRightsFilter(from, Arrays.asList(userRight)));
		cq.where(CriteriaBuilderHelper.and(cb, filter, createDefaultFilter(cb, from)));
		cq.select(cb.count(from));
		return em.createQuery(cq).getSingleResult();
	}

	public boolean hasRole(UserRole userRole) {
		return getCurrentUser().getUserRoles().contains(userRole);
	}

	public boolean hasRegion(RegionReferenceDto regionReference) {
		User currentUser = getCurrentUser();
		if (currentUser.getRegion() == null) {
			return false;
		}
		return currentUser.getRegion().getUuid().equals(regionReference.getUuid());
	}

	/**
	 * @param root
	 *            root to {@link User} or {@link UserReference}.
	 */
	private Predicate createDefaultFilter(CriteriaBuilder cb, From<?, ?> root) {
		return cb.isTrue(root.get(User.ACTIVE));
	}

	public List<User> getAllActive() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(getElementClass());
		Root<User> from = cq.from(getElementClass());
		cq.where(createDefaultFilter(cb, from));
		cq.orderBy(cb.asc(from.get(AbstractDomainObject.ID)));

		return em.createQuery(cq).getResultList();
	}

	public List<User> getAllDefaultUsers() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(getElementClass());
		Root<User> from = cq.from(getElementClass());
		cq.where(cb.and(createDefaultFilter(cb, from), from.get(User.USER_NAME).in(DefaultEntityHelper.getDefaultUserNames())));
		cq.orderBy(cb.asc(from.get(User.USER_NAME)));

		return em.createQuery(cq).getResultList();
	}

	public List<User> getAllByFacilityType(FacilityType facilityType) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(getElementClass());
		Root<User> from = cq.from(getElementClass());
		cq.where(cb.equal(from.join(User.HEALTH_FACILITY, JoinType.LEFT).get(Facility.TYPE), facilityType));

		return em.createQuery(cq).getResultList();
	}

	public long countWithRole(UserRoleReferenceDto userRoleRef) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<User> from = cq.from(getElementClass());

		cq.select(cb.count(from));
		cq.where(cb.equal(from.join(User.USER_ROLES, JoinType.LEFT).get(UserRole.UUID), userRoleRef.getUuid()));

		return em.createQuery(cq).getSingleResult();

	}

	public List<User> getAllWithRole(UserRole userRoleRef) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);
		Root<User> from = cq.from(getElementClass());

		cq.where(cb.equal(from.join(User.USER_ROLES, JoinType.LEFT).get(UserRole.UUID), userRoleRef.getUuid()));

		return em.createQuery(cq).getResultList();
	}

	public List<User> getAllWithOnlyRole(UserRoleReferenceDto userRoleRef) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);
		Root<User> from = cq.from(getElementClass());
		Join<Object, Object> rolesJoin = from.join(User.USER_ROLES, JoinType.LEFT);

		Subquery<Long> roleCount = cq.subquery(Long.class);
		Root<User> roleCountFrom = roleCount.from(User.class);

		roleCount.select(cb.count(roleCountFrom.join(User.USER_ROLES, JoinType.LEFT).get(UserRole.ID)));
		roleCount.where(cb.equal(from.get(User.ID), roleCountFrom.get(User.ID)));

		cq.where(cb.equal(rolesJoin.get(UserRole.UUID), userRoleRef.getUuid()), cb.equal(roleCount, 1));

		return em.createQuery(cq).getResultList();
	}

	/**
	 * Triggers the user sync asynchronously to not block the deployment step
	 */
	public void syncUserAsync(User user) {
		try {
			UserUpdateEvent event = new UserUpdateEvent(user);
			event.setExceptionCallback(exceptionMessage -> logger.error("Could not synchronize user {} due to {}", user.getUuid(), exceptionMessage));

			this.userUpdateEvent.fireAsync(event);
		} catch (Throwable e) {
			logger.error(MessageFormat.format("Unexpected exception when synchronizing user {0}", user.getUuid()), e);
		}
	}

	public boolean isPortHealthUser() {
		User user = getCurrentUser();
		Set<UserRoleDto> userRoleDtos = user.getUserRoles().stream().map(UserRoleFacadeEjb::toDto).collect(Collectors.toSet());
		return userRoleFacade.isPortHealthUser(userRoleDtos);
	}

	public Predicate inJurisdictionOrOwned(CriteriaBuilder cb, UserJoins joins) {
		return new UserJurisdictionPredicateValidator(cb, getCurrentUser(), null, joins).inJurisdictionOrOwned();
	}
}
