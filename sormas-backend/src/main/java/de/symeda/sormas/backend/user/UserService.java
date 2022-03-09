/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
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

import org.apache.commons.collections4.CollectionUtils;

import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserCriteria;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DefaultEntityHelper;
import de.symeda.sormas.api.utils.PasswordHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless
@LocalBean
public class UserService extends AdoServiceWithUserFilter<User> {

	@EJB
	private UserRoleConfigFacadeEjb.UserRoleConfigFacadeEjbLocal userRoleConfigFacade;
	@EJB
	private ConfigFacadeEjbLocal configFacade;

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

	public List<User> getAllByRegionAndUserRoles(Region region, UserRole... userRoles) {
		return getAllByRegionsAndUserRoles(Collections.singletonList(region), Arrays.asList(userRoles), null);
	}

	public List<User> getAllByRegionsAndUserRoles(List<Region> regions, UserRole... userRoles) {
		return getAllByRegionsAndUserRoles(regions, Arrays.asList(userRoles), null);
	}

	public List<User> getAllByRegionAndUserRolesInJurisdiction(Region region, UserRole... userRoles) {
		return getAllByRegionsAndUserRoles(Collections.singletonList(region), Arrays.asList(userRoles), this::createJurisdictionFilter);
	}

	/**
	 * @see #getReferenceListByRoles(List, List, List, boolean, boolean, List) This method is partly a duplication for getReferenceList,
	 *      but it's still in use for WeeklyReports and messageRecipients where more information of the user is needed
	 *      and method signatures rely on {@link User}.
	 */
	private List<User> getAllByRegionsAndUserRoles(
		List<Region> regions,
		Collection<UserRole> userRoles,
		BiFunction<CriteriaBuilder, Root<User>, Predicate> createExtraFilters) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(getElementClass());
		Root<User> from = cq.from(getElementClass());

		Predicate filter = createDefaultFilter(cb, from);
		if (regions != null) {
			filter = from.get(User.REGION).in(regions);
		}

		if (!userRoles.isEmpty()) {
			Join<User, UserRole> joinRoles = from.join(User.USER_ROLES, JoinType.LEFT);
			Predicate rolesFilter = joinRoles.in(userRoles);
			filter = CriteriaBuilderHelper.and(cb, filter, rolesFilter);
		}

		if (createExtraFilters != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, createExtraFilters.apply(cb, from));
		}

		if (filter != null) {
			cq.where(filter);
		}

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

		Predicate filter = createDefaultFilter(cb, from);
		if (districts != null) {
			filter = from.get(User.DISTRICT).in(districts);
		}

		filter = CriteriaBuilderHelper.and(cb, filter, from.get(User.COMMUNITY).isNull());
		filter = CriteriaBuilderHelper.and(cb, filter, from.get(User.HEALTH_FACILITY).isNull());
		filter = CriteriaBuilderHelper.and(cb, filter, from.get(User.POINT_OF_ENTRY).isNull());

		if (!userRights.isEmpty()) {
			Join<User, UserRight> rightJoin = from.join(User.USER_RIGHTS, JoinType.LEFT);
			Predicate predicate = rightJoin.in(userRights);
			filter = CriteriaBuilderHelper.and(cb, filter, predicate);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.distinct(true).orderBy(cb.asc(from.get(AbstractDomainObject.ID)));

		return em.createQuery(cq).getResultList();
	}

	/**
	 * Loads users filtered by combinable filter conditions.<br />
	 * Condition combination if parameter is set:<br />
	 * {@code ((regionUuids & districtUuids & filterByJurisdiction & userRoles) | includeSupervisors) & activeOnly}
	 * 
	 * @see #createJurisdictionFilter(CriteriaBuilder, From)
	 * @param regionUuids
	 * @param districtUuids
	 * @param filterByJurisdiction
	 * @param activeOnly
	 * @param userRights
	 */
	public List<UserReference> getReferenceListByRights(
		List<String> regionUuids,
		List<String> districtUuids,
		boolean filterByJurisdiction,
		boolean activeOnly,
		UserRight... userRights) {

		return getReferenceListByRights(regionUuids, districtUuids, null, filterByJurisdiction, activeOnly, Arrays.asList(userRights));
	}

	/**
	 * Loads users filtered by combinable filter conditions.<br />
	 * Condition combination if parameter is set:<br />
	 * {@code ((regionUuids & districtUuids & communityUuids & filterByJurisdiction & userRoles) | includeSupervisors) & activeOnly}
	 *
	 * @see #createJurisdictionFilter(CriteriaBuilder, From)
	 * @param regionUuids
	 * @param districtUuids
	 * @param communityUuids
	 * @param filterByJurisdiction
	 * @param activeOnly
	 * @param userRights
	 */
	public List<UserReference> getReferenceListByRights(
		List<String> regionUuids,
		List<String> districtUuids,
		List<String> communityUuids,
		boolean filterByJurisdiction,
		boolean activeOnly,
		List<UserRight> userRights) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UserReference> cq = cb.createQuery(UserReference.class);
		Root<UserReference> root = cq.from(UserReference.class);
		Join<UserReference, UserRight> rightsJoin = root.join(User.USER_RIGHTS, JoinType.LEFT);
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
			filter = CriteriaBuilderHelper.and(cb, filter, cb.in(districtJoin.get(AbstractDomainObject.UUID)).value(districtUuids));
			userEntityJoinUsed = true;
		}
		if (filterByJurisdiction) {
			filter = CriteriaBuilderHelper.and(cb, filter, createJurisdictionFilter(cb, userRoot));
			userEntityJoinUsed = true;
		}
		if (CollectionUtils.isNotEmpty(userRights)) {
			filter = CriteriaBuilderHelper.and(cb, filter, rightsJoin.in(userRights));
		}
		if (userEntityJoinUsed) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(root.get(UserReference.ID), userRoot.get(AbstractDomainObject.ID)));
		}

		// WHERE outer AND
		if (activeOnly) {
			filter = CriteriaBuilderHelper.and(cb, filter, createDefaultFilter(cb, root));
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

		return em.createQuery(cq).setHint(ModelConstants.HINT_HIBERNATE_READ_ONLY, true).getResultList();
	}

	/**
	 * Loads users filtered by combinable filter conditions.<br />
	 * Condition combination if parameter is set:<br />
	 * {@code ((regionUuids & districtUuids & communityUuids & filterByJurisdiction & userRoles) | includeSupervisors) & activeOnly}
	 *
	 * @see #createJurisdictionFilter(CriteriaBuilder, From)
	 * @param regionUuids
	 * @param districtUuids
	 * @param communityUuids
	 * @param filterByJurisdiction
	 * @param activeOnly
	 * @param userRoles
	 */
	public List<UserReference> getReferenceListByRoles(
		List<String> regionUuids,
		List<String> districtUuids,
		List<String> communityUuids,
		boolean filterByJurisdiction,
		boolean activeOnly,
		List<UserRole> userRoles) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UserReference> cq = cb.createQuery(UserReference.class);
		Root<UserReference> root = cq.from(UserReference.class);
		Join<UserReference, UserRight> rolesJoin = root.join(User.USER_ROLES, JoinType.LEFT);
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
			filter = CriteriaBuilderHelper.and(cb, filter, cb.in(districtJoin.get(AbstractDomainObject.UUID)).value(districtUuids));
			userEntityJoinUsed = true;
		}
		if (filterByJurisdiction) {
			filter = CriteriaBuilderHelper.and(cb, filter, createJurisdictionFilter(cb, userRoot));
			userEntityJoinUsed = true;
		}
		if (CollectionUtils.isNotEmpty(userRoles)) {
			filter = CriteriaBuilderHelper.and(cb, filter, rolesJoin.in(userRoles));
		}
		if (userEntityJoinUsed) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(root.get(UserReference.ID), userRoot.get(AbstractDomainObject.ID)));
		}

		// WHERE outer AND
		if (activeOnly) {
			filter = CriteriaBuilderHelper.and(cb, filter, createDefaultFilter(cb, root));
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

		return em.createQuery(cq).setHint(ModelConstants.HINT_HIBERNATE_READ_ONLY, true).getResultList();
	}

	public List<UserReference> getUserReferencesByIds(Collection<Long> userIds) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UserReference> cq = cb.createQuery(UserReference.class);
		Root<UserReference> root = cq.from(UserReference.class);

		cq.where(root.get(UserReference.ID).in(userIds));

		return em.createQuery(cq).setHint(ModelConstants.HINT_HIBERNATE_READ_ONLY, true).getResultList();
	}

	public User getRandomUser(District district, UserRole... userRoles) {

		return getRandomUser(
			getReferenceListByRoles(null, Collections.singletonList(district.getUuid()), null, false, true, Arrays.asList(userRoles)));
	}

	public User getRandomUser(District district, UserRight... userRights) {

		return getRandomUser(getReferenceListByRights(null, Collections.singletonList(district.getUuid()), false, true, userRights));
	}

	public User getRandomUser(Region region, UserRight... userRights) {

		return getRandomUser(getReferenceListByRights(Collections.singletonList(region.getUuid()), null, false, true, userRights));
	}

	public User getRandomUser(Region region, UserRole... userRoles) {

		return getRandomUser(getReferenceListByRoles(Collections.singletonList(region.getUuid()), null, null, false, true, Arrays.asList(userRoles)));
	}

	public User getRandomUser(List<UserReference> candidates) {

		if (CollectionUtils.isEmpty(candidates)) {
			return null;
		}

		UserReference chosenUser = candidates.get(new Random().nextInt(candidates.size()));
		return getByUuid(chosenUser.getUuid());
	}

	public List<User> getInformantsOfFacility(Facility facility) {

		if (facility == null || !FacilityType.HOSPITAL.equals(facility.getType())) {
			throw new IllegalArgumentException("Facility needs to be a hospital");
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(getElementClass());
		Root<User> from = cq.from(getElementClass());
		Join<User, UserRole> joinRoles = from.join(User.USER_ROLES, JoinType.LEFT);

		Predicate filter = cb.and(
			createDefaultFilter(cb, from),
			cb.equal(from.get(User.HEALTH_FACILITY), facility),
			joinRoles.in(Collections.singletonList(UserRole.HOSPITAL_INFORMANT)));

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
		Join<User, UserRight> joinRoles = from.join(User.USER_RIGHTS, JoinType.LEFT);

		Predicate filter = cb.and(createDefaultFilter(cb, from), cb.equal(from.get(User.LABORATORY), facility), joinRoles.in(UserRight.SAMPLE_EDIT));
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

	public boolean isLoginUnique(String uuid, String userName) {
		User user = getByUserName(userName);
		return user == null || user.getUuid().equals(uuid);
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

	public Predicate buildCriteriaFilter(UserCriteria userCriteria, CriteriaBuilder cb, Root<User> from) {

		Predicate filter = null;
		if (userCriteria.getActive() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(User.ACTIVE), userCriteria.getActive()));
		}
		if (userCriteria.getUserRole() != null) {
			Join<User, UserRole> joinRoles = from.join(User.USER_ROLES, JoinType.LEFT);
			filter = CriteriaBuilderHelper.and(cb, filter, joinRoles.in(Collections.singletonList(userCriteria.getUserRole())));
		}
		if (userCriteria.getRegion() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(from.join(Case.REGION, JoinType.LEFT).get(AbstractDomainObject.UUID), userCriteria.getRegion().getUuid()));
		}
		if (userCriteria.getDistrict() != null) {
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				cb.equal(from.join(Case.DISTRICT, JoinType.LEFT).get(AbstractDomainObject.UUID), userCriteria.getDistrict().getUuid()));
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

	/**
	 * Caution: Because this filter joins the users_userroles table, using it can result in duplicate results if the
	 * user in question has more than one user role.
	 */
	public Predicate createJurisdictionFilter(CriteriaBuilder cb, From<?, User> from) {
		if (hasRight(UserRight.SEE_PERSONAL_DATA_OUTSIDE_JURISDICTION)) {
			return cb.conjunction();
		}

		User currentUser = getCurrentUser();

		Predicate regionalOrNationalFilter =
			from.join(User.USER_ROLES, JoinType.LEFT).in(UserRole.getWithJurisdictionLevels(JurisdictionLevel.NATION));

		Predicate jurisdictionFilter = cb.conjunction();
		if (currentUser.getHealthFacility() != null) {
			jurisdictionFilter = cb.equal(from.get(User.HEALTH_FACILITY), currentUser.getHealthFacility());
		} else if (currentUser.getPointOfEntry() != null) {
			jurisdictionFilter = cb.equal(from.get(User.POINT_OF_ENTRY), currentUser.getPointOfEntry());
		} else if (currentUser.getLaboratory() != null) {
			jurisdictionFilter = cb.equal(from.get(User.LABORATORY), currentUser.getLaboratory());
		} else if (currentUser.getCommunity() != null) {
			jurisdictionFilter = cb.equal(from.get(User.COMMUNITY), currentUser.getCommunity());
		} else if (currentUser.getDistrict() != null) {
			jurisdictionFilter = cb.equal(from.get(User.DISTRICT), currentUser.getDistrict());
		} else if (currentUser.getRegion() != null) {
			jurisdictionFilter = cb.equal(from.get(User.REGION), currentUser.getRegion());
		}

		return CriteriaBuilderHelper.or(cb, regionalOrNationalFilter, jurisdictionFilter);
	}

	public Predicate buildUserRightsFilter(Root<User> from, Collection<UserRight> userRights) {

		if (!userRights.isEmpty()) {
			Join<User, UserRight> rightJoin = from.join(User.USER_RIGHTS, JoinType.LEFT);
			return rightJoin.in(Collections.singletonList(userRights));
		}

		return null;
	}

	public Long countByAssignedOfficer(User officer, UserRight userRights) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<User> from = cq.from(getElementClass());
		Predicate filter = cb.equal(from.get(User.ASSOCIATED_OFFICER), officer);
		filter = CriteriaBuilderHelper.and(cb, filter, buildUserRightsFilter(from, Arrays.asList(userRights)));
		cq.where(filter);
		cq.select(cb.count(from));
		return em.createQuery(cq).getSingleResult();
	}

	/**
	 * 
	 * @param districts
	 * @param userRight
	 * @return Number of users with specified UserRight on district level in the specified districts
	 */
	public Long countByDistricts(List<District> districts, UserRight userRight) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<User> from = cq.from(getElementClass());
		Predicate filter = from.get(User.DISTRICT).in(districts);
		filter = CriteriaBuilderHelper.and(cb, filter, from.get(User.COMMUNITY).isNull());
		filter = CriteriaBuilderHelper.and(cb, filter, from.get(User.HEALTH_FACILITY).isNull());
		filter = CriteriaBuilderHelper.and(cb, filter, from.get(User.POINT_OF_ENTRY).isNull());
		filter = CriteriaBuilderHelper.and(cb, filter, buildUserRightsFilter(from, Arrays.asList(userRight)));
		cq.where(filter);
		cq.select(cb.count(from));
		return em.createQuery(cq).getSingleResult();
	}

	/**
	 * 
	 * @param communities
	 * @param facilities
	 * @param pointOfEntries
	 * @return Number of users with specified UserRight on community level in the specified communities
	 */
	public Long countByInformants(List<Community> communities, List<Facility> facilities, List<PointOfEntry> pointOfEntries) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<User> from = cq.from(getElementClass());

		Predicate filter = buildUserRightsFilter(from, Arrays.asList(UserRight.WEEKLYREPORT_CREATE));

		Predicate community = communities != null && !communities.isEmpty() ? from.get(User.COMMUNITY).in(communities) : null;
		Predicate facility = facilities != null && !facilities.isEmpty() ? from.get(User.HEALTH_FACILITY).in(facilities) : null;
		Predicate poe = pointOfEntries != null && !pointOfEntries.isEmpty() ? from.get(User.POINT_OF_ENTRY).in(pointOfEntries) : null;

		if (!(community == null && facility == null && poe == null)) {
			filter = CriteriaBuilderHelper.and(cb, filter, CriteriaBuilderHelper.or(cb, community, facility, poe));
		}

		cq.where(filter);
		cq.select(cb.count(from));
		return em.createQuery(cq).getSingleResult();
	}

	public boolean hasRole(UserRole userRoleName) {
		return getCurrentUser().getUserRoles().contains(userRoleName);
	}

	public boolean hasRight(UserRight right) {
		User currentUser = getCurrentUser();
		return userRoleConfigFacade.getEffectiveUserRights(currentUser.getUserRoles().toArray(new UserRole[0])).contains(right);
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
}
