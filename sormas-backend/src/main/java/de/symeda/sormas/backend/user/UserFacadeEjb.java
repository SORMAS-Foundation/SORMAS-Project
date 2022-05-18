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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.validation.Valid;
import javax.validation.ValidationException;

import org.apache.commons.beanutils.BeanUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskContextIndex;
import de.symeda.sormas.api.travelentry.TravelEntryReferenceDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserCriteria;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserFacade;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserReferenceWithTaskNumbersDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.user.UserRole.UserRoleValidationException;
import de.symeda.sormas.api.user.UserSyncResult;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DefaultEntityHelper;
import de.symeda.sormas.api.utils.PasswordHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseJurisdictionPredicateValidator;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactJoins;
import de.symeda.sormas.backend.contact.ContactJurisdictionPredicateValidator;
import de.symeda.sormas.backend.contact.ContactQueryContext;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.event.EventJurisdictionPredicateValidator;
import de.symeda.sormas.backend.event.EventQueryContext;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.community.CommunityService;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.facility.FacilityService;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntryService;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.location.LocationFacadeEjb.LocationFacadeEjbLocal;
import de.symeda.sormas.backend.task.TaskFacadeEjb;
import de.symeda.sormas.backend.travelentry.TravelEntry;
import de.symeda.sormas.backend.travelentry.TravelEntryJoins;
import de.symeda.sormas.backend.travelentry.TravelEntryJurisdictionPredicateValidator;
import de.symeda.sormas.backend.travelentry.TravelEntryQueryContext;
import de.symeda.sormas.backend.user.UserRoleConfigFacadeEjb.UserRoleConfigFacadeEjbLocal;
import de.symeda.sormas.backend.user.event.PasswordResetEvent;
import de.symeda.sormas.backend.user.event.UserCreateEvent;
import de.symeda.sormas.backend.user.event.UserUpdateEvent;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless(name = "UserFacade")
public class UserFacadeEjb implements UserFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private UserService userService;
	@EJB
	private LocationFacadeEjbLocal locationFacade;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private CommunityService communityService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private CaseFacadeEjbLocal caseFacade;
	@EJB
	private TaskFacadeEjb.TaskFacadeEjbLocal taskFacade;
	@EJB
	private CaseService caseService;
	@EJB
	private ContactService contactService;
	@EJB
	private PointOfEntryService pointOfEntryService;
	@EJB
	private UserRoleConfigFacadeEjbLocal userRoleConfigFacade;
	@Inject
	private Event<UserCreateEvent> userCreateEvent;
	@Inject
	private Event<UserUpdateEvent> userUpdateEvent;
	@Inject
	private Event<PasswordResetEvent> passwordResetEvent;

	public static UserDto toDto(User source) {

		if (source == null) {
			return null;
		}

		UserDto target = new UserDto();
		DtoHelper.fillDto(target, source);

		target.setActive(source.isActive());
		target.setUserName(source.getUserName());
		target.setFirstName(source.getFirstName());
		target.setLastName(source.getLastName());
		target.setUserEmail(source.getUserEmail());
		target.setPhone(source.getPhone());
		target.setAddress(LocationFacadeEjb.toDto(source.getAddress()));

		target.setRegion(RegionFacadeEjb.toReferenceDto(source.getRegion()));
		target.setDistrict(DistrictFacadeEjb.toReferenceDto(source.getDistrict()));
		target.setCommunity(CommunityFacadeEjb.toReferenceDto(source.getCommunity()));
		target.setHealthFacility(FacilityFacadeEjb.toReferenceDto(source.getHealthFacility()));
		target.setAssociatedOfficer(toReferenceDto(source.getAssociatedOfficer()));
		target.setLaboratory(FacilityFacadeEjb.toReferenceDto(source.getLaboratory()));
		target.setPointOfEntry(PointOfEntryFacadeEjb.toReferenceDto(source.getPointOfEntry()));
		target.setLimitedDisease(source.getLimitedDisease());
		target.setLanguage(source.getLanguage());
		target.setHasConsentedToGdpr(source.isHasConsentedToGdpr());

		target.setUserRoles(new HashSet<>(source.getUserRoles()));
		return target;
	}

	public static UserReferenceDto toReferenceDto(User entity) {
		if (entity == null) {
			return null;
		}
		return new UserReferenceDto(entity.getUuid(), entity.getFirstName(), entity.getLastName(), entity.getUserRoles());
	}

	public static UserReferenceDto toReferenceDto(UserReference entity) {
		if (entity == null) {
			return null;
		}
		return new UserReferenceDto(entity.getUuid(), entity.getFirstName(), entity.getLastName(), entity.getUserRoles());
	}

	@Override
	public List<UserReferenceDto> getUsersByRegionAndRights(RegionReferenceDto regionRef, Disease limitedDisease, UserRight... userRights) {
		return userService
			.getUserReferences(
				regionRef != null ? Collections.singletonList(regionRef.getUuid()) : null,
				null,
				null,
				true,
				true,
				limitedDisease,
				userRights)
			.stream()
			.map(UserFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	@Override
	/*
	 * Get all users with the next higher jurisdiction, whose location contains the current users location
	 * For facility users, this includes district and community users, if their district/community is identical with that of the facility
	 */
	public List<UserReferenceDto> getUsersWithSuperiorJurisdiction(UserDto user) {
		JurisdictionLevel superordinateJurisdiction =
			JurisdictionHelper.getSuperordinateJurisdiction(UserRole.getJurisdictionLevel(user.getUserRoles()));

		List<UserReference> superiorUsersList = Collections.emptyList();
		switch (superordinateJurisdiction) {
		case NATION:
			superiorUsersList =
				userService.getUserReferencesByJurisdictions(null, null, null, Collections.singletonList(superordinateJurisdiction), null);
			break;
		case REGION:
			superiorUsersList = userService.getUserReferencesByJurisdictions(
				Collections.singletonList(user.getRegion().getUuid()),
				null,
				null,
				Collections.singletonList(superordinateJurisdiction),
				null);
			break;
		case DISTRICT:
			// if user is assigned to a facility, but that facility is not assigned to a district, show no superordinate users. Else, show users of the district (and community) in which the facility is located

			District district = null;
			Community community = null;
			Set<JurisdictionLevel> superordinateJurisdictions = new HashSet<>();
			superordinateJurisdictions.add(superordinateJurisdiction);
			if (user.getDistrict() != null) {
				district = districtService.getByReferenceDto(user.getDistrict());
			} else if (user.getHealthFacility() != null) {
				Facility facility = facilityService.getByReferenceDto(user.getHealthFacility());
				district = facility.getDistrict();
				community = facility.getCommunity();
				superordinateJurisdictions.add(JurisdictionLevel.COMMUNITY);
			}

			if (community == null) {
				superiorUsersList = userService
					.getUserReferencesByJurisdictions(null, Collections.singletonList(district.getUuid()), null, superordinateJurisdictions, null);
			} else if (district != null) {
				superiorUsersList = userService.getUserReferencesByJurisdictions(
					null,
					Collections.singletonList(district.getUuid()),
					Collections.singletonList(community.getUuid()),
					superordinateJurisdictions,
					null);
			}

			break;
		}

		return superiorUsersList.stream().map(UserFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	@Override
	public List<UserReferenceDto> getUserRefsByDistrict(DistrictReferenceDto districtRef, Disease limitedDisease, UserRight... userRights) {

		return userService
			.getUserReferences(
				null,
				districtRef != null ? Collections.singletonList(districtRef.getUuid()) : null,
				null,
				true,
				true,
				limitedDisease,
				userRights)
			.stream()
			.map(UserFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	@Override
	public List<UserReferenceDto> getUserRefsByDistrict(
		DistrictReferenceDto districtRef,
		boolean excludeLimitedDiseaseUsers,
		UserRight... userRights) {
		return userService
			.getUserReferences(
				null,
				districtRef != null ? Collections.singletonList(districtRef.getUuid()) : null,
				null,
				true,
				true,
				null,
				excludeLimitedDiseaseUsers,
				Arrays.asList(userRights))
			.stream()
			.map(UserFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	@Override
	public List<UserReferenceDto> getUserRefsByDistricts(List<DistrictReferenceDto> districtRefs, Disease limitedDisease, UserRight... userRights) {

		return userService
			.getUserReferences(
				null,
				districtRefs.stream().map(DistrictReferenceDto::getUuid).collect(Collectors.toList()),
				null,
				true,
				true,
				limitedDisease,
				userRights)
			.stream()
			.map(UserFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	@Override
	public List<UserReferenceDto> getAllUserRefs(boolean includeInactive) {

		return userService.getUserReferences(null, null, true, !includeInactive)
			.stream()
			.map(UserFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	private List<UserReferenceDto> getAssignableUsersBasedOnContext(TaskContextIndex taskContextIndex) {
		List<UserReferenceDto> availableUsers = new ArrayList<>();
		if (taskContextIndex.getUuid() == null) {
			taskContextIndex = new TaskContextIndex(TaskContext.GENERAL);
		}
		switch (taskContextIndex.getTaskContext()) {
		case CASE:
			availableUsers.addAll(getUsersHavingCaseInJurisdiction(new CaseReferenceDto(taskContextIndex.getUuid())));
			break;
		case CONTACT:
			availableUsers.addAll(getUsersHavingContactInJurisdiction(new ContactReferenceDto(taskContextIndex.getUuid())));
			break;
		case EVENT:
			availableUsers.addAll(getUsersHavingEventInJurisdiction(new EventReferenceDto(taskContextIndex.getUuid())));
			break;
		case TRAVEL_ENTRY:
			availableUsers.addAll(getUsersHavingTravelEntryInJurisdiction(new TravelEntryReferenceDto(taskContextIndex.getUuid())));
			break;
		default:
			availableUsers.addAll(getAllUserRefs(false));

		}
		return availableUsers;
	}

	public List<UserReferenceWithTaskNumbersDto> getAssignableUsersWithTaskNumbers(TaskContextIndex taskContextIndex) {

		List<UserReferenceDto> availableUsers = getAssignableUsersBasedOnContext(taskContextIndex);
		Map<String, Long> userTaskCounts =
			taskFacade.getPendingTaskCountPerUser(availableUsers.stream().map(UserReferenceDto::getUuid).collect(Collectors.toList()));

		return availableUsers.stream()
			.map(userReference -> new UserReferenceWithTaskNumbersDto(userReference, userTaskCounts.get(userReference.getUuid())))
			.collect(Collectors.toList());

	}

	@Override
	public List<UserDto> getUsersByAssociatedOfficer(UserReferenceDto associatedOfficerRef, UserRight... userRights) {

		User associatedOfficer = userService.getByReferenceDto(associatedOfficerRef);
		return userService.getAllByAssociatedOfficer(associatedOfficer, userRights).stream().map(UserFacadeEjb::toDto).collect(Collectors.toList());
	}

	@Override
	public List<UserReferenceDto> getUsersHavingCaseInJurisdiction(CaseReferenceDto caseReferenceDto) {

		return getUsersHavingEntityInJurisdiction((cb, cq, userRoot) -> {

			final Subquery<Case> caseJurisdictionSubquery = cq.subquery(Case.class);
			final Root<Case> caseRoot = caseJurisdictionSubquery.from(Case.class);
			final CaseJurisdictionPredicateValidator caseJurisdictionPredicateValidator =
				CaseJurisdictionPredicateValidator.of(new CaseQueryContext(cb, cq, caseRoot), userRoot);

			caseJurisdictionSubquery.select(caseRoot)
				.where(
					cb.and(
						cb.equal(caseRoot.get(Case.UUID), caseReferenceDto.getUuid()),
						cb.isTrue(caseJurisdictionPredicateValidator.inJurisdictionOrOwned()),
						cb.or(
							cb.isNull(userRoot.get(User.LIMITED_DISEASE)),
							cb.equal(userRoot.get(User.LIMITED_DISEASE), caseRoot.get(Case.DISEASE)))));
			return caseJurisdictionSubquery;
		});
	}

	@Override
	public List<UserReferenceDto> getUsersHavingContactInJurisdiction(ContactReferenceDto contactReferenceDto) {
		return getUsersHavingEntityInJurisdiction((cb, cq, userRoot) -> {

			final Subquery<Contact> contactJurisdictionSubquery = cq.subquery(Contact.class);
			final Root<Contact> contactRoot = contactJurisdictionSubquery.from(Contact.class);
			final ContactJurisdictionPredicateValidator contactJurisdictionPredicateValidator =
				ContactJurisdictionPredicateValidator.of(new ContactQueryContext(cb, cq, new ContactJoins(contactRoot)), userRoot);

			contactJurisdictionSubquery.select(contactRoot)
				.where(
					cb.and(
						cb.equal(contactRoot.get(AbstractDomainObject.UUID), contactReferenceDto.getUuid()),
						cb.isTrue(contactJurisdictionPredicateValidator.inJurisdictionOrOwned()),
						cb.or(
							cb.isNull(userRoot.get(User.LIMITED_DISEASE)),
							cb.equal(userRoot.get(User.LIMITED_DISEASE), contactRoot.get(Contact.DISEASE)))));
			return contactJurisdictionSubquery;
		});
	}

	@Override
	public List<UserReferenceDto> getUsersHavingEventInJurisdiction(EventReferenceDto eventReferenceDto) {

		return getUsersHavingEntityInJurisdiction((cb, cq, userRoot) -> {

			final Subquery<de.symeda.sormas.backend.event.Event> eventJurisdictionSubquery = cq.subquery(de.symeda.sormas.backend.event.Event.class);
			final Root<de.symeda.sormas.backend.event.Event> eventRoot = eventJurisdictionSubquery.from(de.symeda.sormas.backend.event.Event.class);
			final EventJurisdictionPredicateValidator eventJurisdictionPredicateValidator =
				EventJurisdictionPredicateValidator.of(new EventQueryContext(cb, cq, eventRoot), userRoot);

			eventJurisdictionSubquery.select(eventRoot)
				.where(
					cb.and(
						cb.equal(eventRoot.get(AbstractDomainObject.UUID), eventReferenceDto.getUuid()),
						cb.isTrue(eventJurisdictionPredicateValidator.inJurisdictionOrOwned()),
						cb.or(
							cb.isNull(userRoot.get(User.LIMITED_DISEASE)),
							cb.equal(userRoot.get(User.LIMITED_DISEASE), eventRoot.get(de.symeda.sormas.backend.event.Event.DISEASE)))));
			return eventJurisdictionSubquery;
		});
	}

	@Override
	public List<UserReferenceDto> getUsersHavingTravelEntryInJurisdiction(TravelEntryReferenceDto travelEntryReferenceDto) {

		return getUsersHavingEntityInJurisdiction((cb, cq, userRoot) -> {

			final Subquery<TravelEntry> travelEntrySubquery = cq.subquery(TravelEntry.class);
			final Root<TravelEntry> travelEntryRoot = travelEntrySubquery.from(TravelEntry.class);
			final TravelEntryJurisdictionPredicateValidator travelEntryJurisdictionPredicateValidator =
				TravelEntryJurisdictionPredicateValidator.of(new TravelEntryQueryContext(cb, cq, new TravelEntryJoins(travelEntryRoot)), userRoot);

			travelEntrySubquery.select(travelEntryRoot)
				.where(
					cb.and(
						cb.equal(travelEntryRoot.get(AbstractDomainObject.UUID), travelEntryReferenceDto.getUuid()),
						cb.isTrue(travelEntryJurisdictionPredicateValidator.inJurisdictionOrOwned()),
						cb.or(
							cb.isNull(userRoot.get(User.LIMITED_DISEASE)),
							cb.equal(userRoot.get(User.LIMITED_DISEASE), travelEntryRoot.get(TravelEntry.DISEASE)))));
			return travelEntrySubquery;
		});
	}

	public <ADO extends AbstractDomainObject> List<UserReferenceDto> getUsersHavingEntityInJurisdiction(
		JurisdictionOverEntitySubqueryBuilder<ADO> subqueryBuilder) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<User> cq = cb.createQuery(User.class);
		final Root<User> root = cq.from(User.class);
		cq.select(root);

		cq.where(CriteriaBuilderHelper.and(cb, cb.isTrue(root.get(User.ACTIVE)), cb.exists(subqueryBuilder.buildSubquery(cb, cq, root))));

		cq.distinct(true);
		cq.orderBy(cb.asc(root.get(AbstractDomainObject.ID)));
		List<User> resultList = em.createQuery(cq).setHint(ModelConstants.HINT_HIBERNATE_READ_ONLY, true).getResultList();
		return resultList.stream().map(UserFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	@Override
	public Page<UserDto> getIndexPage(UserCriteria userCriteria, int offset, int size, List<SortProperty> sortProperties) {
		List<UserDto> userIndexList = getIndexList(userCriteria, offset, size, sortProperties);
		long totalElementCount = count(userCriteria);
		return new Page<>(userIndexList, offset, size, totalElementCount);
	}

	@Override
	public List<UserDto> getAllAfter(Date date) {
		return userService.getAllAfter(date).stream().map(UserFacadeEjb::toDto).collect(Collectors.toList());
	}

	@Override
	public List<UserDto> getByUuids(List<String> uuids) {
		return userService.getByUuids(uuids).stream().map(UserFacadeEjb::toDto).collect(Collectors.toList());
	}

	@Override
	public List<String> getAllUuids() {

		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		return userService.getAllUuids();
	}

	@Override
	public UserDto getByUuid(String uuid) {
		return toDto(userService.getByUuid(uuid));
	}

	@Override
	public UserDto getByUserName(String userName) {
		return toDto(userService.getByUserName(userName));
	}

	@Override
	public UserDto saveUser(@Valid UserDto dto) {

		User oldUser = null;
		if (dto.getCreationDate() != null) {
			try {
				oldUser = (User) BeanUtils.cloneBean(userService.getByUuid(dto.getUuid()));
			} catch (Exception e) {
				throw new IllegalArgumentException("Invalid bean access", e);
			}
		}

		User user = fromDto(dto, true);

		try {
			UserRole.validate(user.getUserRoles());
		} catch (UserRoleValidationException e) {
			throw new ValidationException(e);
		}

		if (!isLoginUnique(oldUser == null ? null : oldUser.getUuid(), dto.getUserName())) {
			throw new ValidationException(I18nProperties.getValidationError(Validations.userNameNotUnique));
		}

		userService.ensurePersisted(user);

		if (oldUser == null) {
			userCreateEvent.fire(new UserCreateEvent(user));
		} else {
			userUpdateEvent.fire(new UserUpdateEvent(oldUser, user));
		}

		return toDto(user);
	}

	@Override
	public List<UserDto> getIndexList(UserCriteria userCriteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);
		Root<User> user = cq.from(User.class);
		Join<User, District> district = user.join(User.DISTRICT, JoinType.LEFT);
		Join<User, Location> address = user.join(User.ADDRESS, JoinType.LEFT);
		Join<User, Facility> facility = user.join(User.HEALTH_FACILITY, JoinType.LEFT);

		// TODO: We'll need a user filter for users at some point, to make sure that users can edit their own details,
		// but not those of others

		Predicate filter = null;

		if (userCriteria != null) {
			filter = userService.buildCriteriaFilter(userCriteria, cb, user);
		}

		if (filter != null) {
			/*
			 * No preemptive distinct because this does collide with
			 * ORDER BY User.location.address (which is not part of the SELECT clause).
			 */
			cq.where(filter);
		}

		if (sortProperties != null && !sortProperties.isEmpty()) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case EntityDto.UUID:
				case UserDto.ACTIVE:
				case UserDto.USER_NAME:
				case UserDto.USER_EMAIL:
					expression = user.get(sortProperty.propertyName);
					break;
				case UserDto.NAME:
					expression = user.get(User.FIRST_NAME);
					order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
					expression = user.get(User.LAST_NAME);
					break;
				case UserDto.DISTRICT:
					expression = district.get(District.NAME);
					break;
				case UserDto.ADDRESS:
					expression = address.get(Location.REGION);
					break;
				case UserDto.HEALTH_FACILITY:
					expression = facility.get(Facility.NAME);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.desc(user.get(AbstractDomainObject.CHANGE_DATE)));
		}

		cq.select(user);

		return QueryHelper.getResultList(em, cq, first, max, UserFacadeEjb::toDto);
	}

	@Override
	public long count(UserCriteria userCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<User> root = cq.from(User.class);

		Predicate filter = null;

		if (userCriteria != null) {
			filter = userService.buildCriteriaFilter(userCriteria, cb, root);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.count(root));
		return em.createQuery(cq).getSingleResult();
	}

	private User fromDto(UserDto source, boolean checkChangeDate) {

		User target = DtoHelper.fillOrBuildEntity(source, userService.getByUuid(source.getUuid()), userService::createUser, checkChangeDate);

		target.setActive(source.isActive());
		target.setFirstName(source.getFirstName());
		target.setLastName(source.getLastName());
		target.setPhone(source.getPhone());
		target.setAddress(locationFacade.fromDto(source.getAddress(), checkChangeDate));

		target.setUserName(source.getUserName());
		target.setUserEmail(source.getUserEmail());

		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setCommunity(communityService.getByReferenceDto(source.getCommunity()));
		target.setHealthFacility(facilityService.getByReferenceDto(source.getHealthFacility()));
		target.setAssociatedOfficer(userService.getByReferenceDto(source.getAssociatedOfficer()));
		target.setLaboratory(facilityService.getByReferenceDto(source.getLaboratory()));
		target.setPointOfEntry(pointOfEntryService.getByReferenceDto(source.getPointOfEntry()));
		target.setLimitedDisease(source.getLimitedDisease());
		target.setLanguage(source.getLanguage());
		target.setHasConsentedToGdpr(source.isHasConsentedToGdpr());

		target.setUserRoles(new HashSet<>(source.getUserRoles()));
		target.updateJurisdictionLevel();

		return target;
	}

	@Override
	public boolean isLoginUnique(String uuid, String userName) {
		return userService.isLoginUnique(uuid, userName);
	}

	@Override
	public String resetPassword(String uuid) {
		String resetPassword = userService.resetPassword(uuid);
		passwordResetEvent.fire(new PasswordResetEvent(userService.getByUuid(uuid)));
		return resetPassword;
	}

	@Override
	public UserDto getCurrentUser() {
		return toDto(userService.getCurrentUser());
	}

	@Override
	public UserReferenceDto getCurrentUserAsReference() {
		return new UserReferenceDto(userService.getCurrentUser().getUuid());
	}

	@Override
	public Set<UserRight> getValidLoginRights(String userName, String password) {

		User user = userService.getByUserName(userName);
		if (user != null && user.isActive()) {
			if (DataHelper.equal(user.getPassword(), PasswordHelper.encodePassword(password, user.getSeed()))) {
				return new HashSet<>(userRoleConfigFacade.getEffectiveUserRights(user.getUserRoles().toArray(new UserRole[] {})));
			}
		}
		return null;
	}

	@Override
	public void removeUserAsSurveillanceAndContactOfficer(String userUuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Case> caseQuery = cb.createQuery(Case.class);
		Root<Case> caseRoot = caseQuery.from(Case.class);
		Join<Case, User> surveillanceOfficerJoin = caseRoot.join(Case.SURVEILLANCE_OFFICER, JoinType.LEFT);

		caseQuery.where(cb.equal(surveillanceOfficerJoin.get(AbstractDomainObject.UUID), userUuid));
		List<Case> cases = em.createQuery(caseQuery).getResultList();
		cases.forEach(c -> {
			c.setSurveillanceOfficer(null);
			caseFacade.setCaseResponsible(c);
			caseService.ensurePersisted(c);
			caseFacade.reassignTasksOfCase(c, true);
		});

		CriteriaQuery<Contact> contactQuery = cb.createQuery(Contact.class);
		Root<Contact> contactRoot = contactQuery.from(Contact.class);
		Join<Contact, User> contactOfficerJoin = contactRoot.join(Contact.CONTACT_OFFICER, JoinType.LEFT);

		contactQuery.where(cb.equal(contactOfficerJoin.get(AbstractDomainObject.UUID), userUuid));
		List<Contact> contacts = em.createQuery(contactQuery).getResultList();
		contacts.forEach(c -> {
			c.setContactOfficer(null);
			contactService.ensurePersisted(c);
		});
	}

	@Override
	public UserSyncResult syncUser(String uuid) {
		User user = userService.getByUuid(uuid);

		UserSyncResult userSyncResult = new UserSyncResult();
		userSyncResult.setSuccess(true);

		UserUpdateEvent event = new UserUpdateEvent(user);
		event.setExceptionCallback(exceptionMessage -> {
			userSyncResult.setSuccess(false);
			userSyncResult.setErrorMessage(exceptionMessage);
		});

		this.userUpdateEvent.fire(event);

		return userSyncResult;
	}

	@Override
	public List<UserDto> getUsersWithDefaultPassword() {
		User currentUser = userService.getCurrentUser();
		if (currentUser.getUserRoles().stream().anyMatch(r -> r.hasDefaultRight(UserRight.USER_EDIT))) {
			// user is allowed to change all passwords
			// a list of all users with a default password is returned
			return userService.getAllDefaultUsers()
				.stream()
				.filter(user -> DefaultEntityHelper.usesDefaultPassword(user.getUserName(), user.getPassword(), user.getSeed()))
				.map(UserFacadeEjb::toDto)
				.collect(Collectors.toList());

		} else {
			// user has only access to himself
			// the list will include him/her or will be empty
			if (DefaultEntityHelper.isDefaultUser(currentUser.getUserName())
				&& DefaultEntityHelper.usesDefaultPassword(currentUser.getUserName(), currentUser.getPassword(), currentUser.getSeed())) {
				return Collections.singletonList(UserFacadeEjb.toDto(currentUser));
			} else {
				return Collections.emptyList();
			}
		}
	}

	@Override
	public void enableUsers(List<String> userUuids) {
		updateActiveState(userUuids, true);
	}

	@Override
	public void disableUsers(List<String> userUuids) {
		updateActiveState(userUuids, false);
	}

	private void updateActiveState(List<String> userUuids, boolean active) {

		List<User> users = userService.getByUuids(userUuids);
		for (User user : users) {
			User oldUser;
			try {
				oldUser = (User) BeanUtils.cloneBean(user);
			} catch (Exception e) {
				throw new IllegalArgumentException("Invalid bean access", e);
			}

			user.setActive(active);
			userService.ensurePersisted(user);

			userUpdateEvent.fire(new UserUpdateEvent(oldUser, user));
		}
	}

	public interface JurisdictionOverEntitySubqueryBuilder<ADO extends AbstractDomainObject> {

		Subquery<ADO> buildSubquery(CriteriaBuilder cb, CriteriaQuery<?> cq, Root<User> userRoot);
	}

	@LocalBean
	@Stateless
	public static class UserFacadeEjbLocal extends UserFacadeEjb {

	}
}
