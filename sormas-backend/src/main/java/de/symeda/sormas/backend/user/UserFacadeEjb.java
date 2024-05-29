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

import static de.symeda.sormas.api.AuthProvider.KEYCLOAK;
import static java.util.Objects.isNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.ws.rs.ForbiddenException;

import de.symeda.sormas.backend.util.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.AuthProvider;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.InfrastructureDataReferenceDto;
import de.symeda.sormas.api.audit.AuditIgnore;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.common.progress.ProcessedEntityStatus;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.InfrastructureHelper;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskContextIndexCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryReferenceDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserCriteria;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserFacade;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserReferenceWithTaskNumbersDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.api.user.UserRoleReferenceDto;
import de.symeda.sormas.api.user.UserSyncResult;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DefaultEntityHelper;
import de.symeda.sormas.api.utils.PasswordHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.FacadeHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseJurisdictionPredicateValidator;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactJoins;
import de.symeda.sormas.backend.contact.ContactJurisdictionPredicateValidator;
import de.symeda.sormas.backend.contact.ContactQueryContext;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.environment.Environment;
import de.symeda.sormas.backend.environment.EnvironmentJoins;
import de.symeda.sormas.backend.environment.EnvironmentJurisdictionPredicateValidator;
import de.symeda.sormas.backend.environment.EnvironmentQueryContext;
import de.symeda.sormas.backend.event.EventJurisdictionPredicateValidator;
import de.symeda.sormas.backend.event.EventQueryContext;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
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
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.location.LocationFacadeEjb.LocationFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.task.TaskFacadeEjb;
import de.symeda.sormas.backend.travelentry.TravelEntry;
import de.symeda.sormas.backend.travelentry.TravelEntryJoins;
import de.symeda.sormas.backend.travelentry.TravelEntryJurisdictionPredicateValidator;
import de.symeda.sormas.backend.travelentry.TravelEntryQueryContext;
import de.symeda.sormas.backend.user.UserRoleFacadeEjb.UserRoleFacadeEjbLocal;
import de.symeda.sormas.backend.user.event.PasswordResetEvent;
import de.symeda.sormas.backend.user.event.SyncUsersFromProviderEvent;
import de.symeda.sormas.backend.user.event.UserCreateEvent;
import de.symeda.sormas.backend.user.event.UserUpdateEvent;


@Stateless(name = "UserFacade")
public class UserFacadeEjb implements UserFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private CurrentUserService currentUserService;
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
	private UserRoleFacadeEjbLocal userRoleFacade;
	@EJB
	private FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;
	@EJB
	private UserRoleService userRoleService;
	@EJB
	private PersonService personService;
	@EJB
	private ConfigFacadeEjbLocal configFacade;
	@Inject
	private Event<UserCreateEvent> userCreateEvent;
	@Inject
	private Event<UserUpdateEvent> userUpdateEvent;
	@Inject
	private Event<PasswordResetEvent> passwordResetEvent;
	@Inject
	private Event<SyncUsersFromProviderEvent> syncUsersFromProviderEventEvent;

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
		target.setLimitedDiseases(source.getLimitedDiseases());
		target.setLanguage(source.getLanguage());
		target.setHasConsentedToGdpr(source.isHasConsentedToGdpr());

		target.setUserRoles(source.getUserRoles().stream().map(UserRoleFacadeEjb::toReferenceDto).collect(Collectors.toSet()));
		target.setJurisdictionLevel(source.getJurisdictionLevel());
		return target;
	}

	public static UserReferenceDto toReferenceDto(User entity) {
		if (entity == null) {
			return null;
		}
		return new UserReferenceDto(entity.getUuid(), entity.getFirstName(), entity.getLastName());
	}

	public static UserReferenceDto toReferenceDto(UserReference entity) {
		if (entity == null) {
			return null;
		}
		return new UserReferenceDto(entity.getUuid(), entity.getFirstName(), entity.getLastName());
	}

	@Override
	@PermitAll
	public List<UserReferenceDto> getUsersByRegionAndRights(RegionReferenceDto regionRef, Disease limitedDisease, UserRight... userRights) {
		return userService
			.getUserReferences(
				regionRef != null ? Collections.singletonList(regionRef.getUuid()) : null,
				null,
				null,
				true,
				limitedDisease,
				userRights)
			.stream()
			.map(UserFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	/*
	 * Get all users with the next higher jurisdiction, whose location contains the current users location
	 * For facility users, this includes district and community users, if their district/community is identical with that of the facility
	 */
	@Override
	@PermitAll
	public List<UserReferenceDto> getUsersWithSuperiorJurisdiction(UserDto user) {
		JurisdictionLevel superordinateJurisdiction = InfrastructureHelper.getSuperordinateJurisdiction(user.getJurisdictionLevel());

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
	@PermitAll
	public List<UserReferenceDto> getUserRefsByDistrict(DistrictReferenceDto districtRef, Disease limitedDisease, UserRight... userRights) {

		return userService
			.getUserReferences(
				null,
				districtRef != null ? Collections.singletonList(districtRef.getUuid()) : null,
				null,
				true,
				limitedDisease,
				userRights)
			.stream()
			.map(UserFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	@Override
	@PermitAll
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
				null,
				excludeLimitedDiseaseUsers,
				Arrays.asList(userRights))
			.stream()
			.map(UserFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	@Override
	@PermitAll
	public List<UserReferenceDto> getUserRefsByDistricts(List<DistrictReferenceDto> districtRefs, Disease limitedDisease, UserRight... userRights) {

		return userService
			.getUserReferences(
				null,
				districtRefs.stream().map(DistrictReferenceDto::getUuid).collect(Collectors.toList()),
				null,
				true,
				limitedDisease,
				userRights)
			.stream()
			.map(UserFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	@Override
	@PermitAll
	public List<UserReferenceDto> getUserRefsByInfrastructure(
		InfrastructureDataReferenceDto infrastructure,
		JurisdictionLevel jurisdictionLevel,
		JurisdictionLevel allowedJurisdictionLevel,
		Disease limitedDisease,
		UserRight... userRights) {

		if (jurisdictionLevel.getOrder() < allowedJurisdictionLevel.getOrder()) {
			return Collections.emptyList();
		}

		return userService
			.getUserRefsByInfrastructure(
				infrastructure != null ? infrastructure.getUuid() : null,
				jurisdictionLevel,
				allowedJurisdictionLevel,
				limitedDisease,
				userRights)
			.stream()
			.map(UserFacadeEjb::toReferenceDto)
			.collect(Collectors.toList());
	}

	@Override
	@PermitAll
	public List<UserReferenceDto> getAllUserRefs(boolean includeInactive) {

		return userService.getUserReferences(null, null, !includeInactive).stream().map(UserFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	private List<UserReferenceDto> getAssignableUsersBasedOnContext(TaskContextIndexCriteria taskContextIndexCriteria) {
		List<UserReferenceDto> availableUsers = new ArrayList<>();
		if (taskContextIndexCriteria.getUuid() == null) {
			taskContextIndexCriteria = new TaskContextIndexCriteria(TaskContext.GENERAL);
		}
		switch (taskContextIndexCriteria.getTaskContext()) {
		case CASE:
			availableUsers.addAll(getUsersHavingCaseInJurisdiction(new CaseReferenceDto(taskContextIndexCriteria.getUuid())));
			break;
		case CONTACT:
			availableUsers.addAll(getUsersHavingContactInJurisdiction(new ContactReferenceDto(taskContextIndexCriteria.getUuid())));
			break;
		case EVENT:
			availableUsers.addAll(getUsersHavingEventInJurisdiction(new EventReferenceDto(taskContextIndexCriteria.getUuid())));
			break;
		case TRAVEL_ENTRY:
			availableUsers.addAll(getUsersHavingTravelEntryInJurisdiction(new TravelEntryReferenceDto(taskContextIndexCriteria.getUuid())));
			break;
		default:
			availableUsers.addAll(getAllUserRefs(false));

		}
		return availableUsers;
	}

	@PermitAll
	public List<UserReferenceWithTaskNumbersDto> getAssignableUsersWithTaskNumbers(TaskContextIndexCriteria taskContextIndexCriteria) {

		List<UserReferenceDto> availableUsers = getAssignableUsersBasedOnContext(taskContextIndexCriteria);
		Map<String, Long> userTaskCounts =
			taskFacade.getPendingTaskCountPerUser(availableUsers.stream().map(UserReferenceDto::getUuid).collect(Collectors.toList()));

		return availableUsers.stream()
			.map(userReference -> new UserReferenceWithTaskNumbersDto(userReference, userTaskCounts.get(userReference.getUuid())))
			.collect(Collectors.toList());

	}

	@Override
	@PermitAll
	public Set<UserRoleDto> getUserRoles(UserDto userDto) {
		User user = userService.getByUuid(userDto.getUuid());
		return user != null ? user.getUserRoles().stream().map(UserRoleFacadeEjb::toDto).collect(Collectors.toSet()) : null;
	}

	@Override
	@RightsAllowed({
		UserRight._WEEKLYREPORT_VIEW,
		UserRight._WEEKLYREPORT_CREATE })
	public List<UserDto> getUsersByAssociatedOfficer(UserReferenceDto associatedOfficerRef, UserRight... userRights) {

		User associatedOfficer = userService.getByReferenceDto(associatedOfficerRef);
		return userService.getAllByAssociatedOfficer(associatedOfficer, userRights).stream().map(UserFacadeEjb::toDto).collect(Collectors.toList());
	}

	@Override
	@PermitAll
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
						cb.or(cb.isNull(userRoot.get(User.LIMITED_DISEASES)), caseRoot.get(Case.DISEASE).in(userRoot.get(User.LIMITED_DISEASES)))));
			return caseJurisdictionSubquery;
		});
	}

	@Override
	@PermitAll
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
							cb.isNull(userRoot.get(User.LIMITED_DISEASES)),
							contactRoot.get(Contact.DISEASE).in(userRoot.get(User.LIMITED_DISEASES)))));
			return contactJurisdictionSubquery;
		});
	}

	@Override
	@PermitAll
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
							cb.isNull(userRoot.get(User.LIMITED_DISEASES)),
							eventRoot.get(de.symeda.sormas.backend.event.Event.DISEASE).in(userRoot.get(User.LIMITED_DISEASES)))));
			return eventJurisdictionSubquery;
		});
	}

	@Override
	@PermitAll
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
							cb.isNull(userRoot.get(User.LIMITED_DISEASES)),
							travelEntryRoot.get(TravelEntry.DISEASE).in(userRoot.get(User.LIMITED_DISEASES)))));
			return travelEntrySubquery;
		});
	}

	@Override
	@PermitAll
	public List<UserReferenceDto> getUsersHavingEnvironmentInJurisdiction(EnvironmentReferenceDto environmentReferenceDto) {

		return getUsersHavingEntityInJurisdiction((cb, cq, userRoot) -> {

			final Subquery<Environment> environmentSubquery = cq.subquery(Environment.class);
			final Root<Environment> environmentRoot = environmentSubquery.from(Environment.class);
			final EnvironmentJurisdictionPredicateValidator environmentJurisdictionPredicateValidator =
				EnvironmentJurisdictionPredicateValidator.of(new EnvironmentQueryContext(cb, cq, new EnvironmentJoins(environmentRoot)), userRoot);

			environmentSubquery.select(environmentRoot)
				.where(
					cb.and(
						cb.equal(environmentRoot.get(AbstractDomainObject.UUID), environmentReferenceDto.getUuid()),
						cb.isTrue(environmentJurisdictionPredicateValidator.inJurisdictionOrOwned())));
			return environmentSubquery;
		});
	}

	private <ADO extends AbstractDomainObject> List<UserReferenceDto> getUsersHavingEntityInJurisdiction(
		JurisdictionOverEntitySubqueryBuilder<ADO> subqueryBuilder) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<User> cq = cb.createQuery(User.class);
		final Root<User> root = cq.from(User.class);
		cq.select(root);

		cq.where(CriteriaBuilderHelper.and(cb, cb.isTrue(root.get(User.ACTIVE)), cb.exists(subqueryBuilder.buildSubquery(cb, cq, root))));

		cq.distinct(true);
		cq.orderBy(cb.asc(root.get(AbstractDomainObject.ID)));
		List<User> resultList = em.createQuery(cq).setHint(ModelConstants.READ_ONLY, true).getResultList();
		return resultList.stream().map(UserFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	@Override
	@RightsAllowed(UserRight._USER_VIEW)
	public Page<UserDto> getIndexPage(UserCriteria userCriteria, int offset, int size, List<SortProperty> sortProperties) {
		List<UserDto> userIndexList = getIndexList(userCriteria, offset, size, sortProperties);
		long totalElementCount = count(userCriteria);
		return new Page<>(userIndexList, offset, size, totalElementCount);
	}

	@Override
	@PermitAll
	public List<UserDto> getAllAfter(Date date) {
		return userService.getAllAfter(date).stream().map(UserFacadeEjb::toDto).collect(Collectors.toList());
	}

	@Override
	@PermitAll
	public List<UserDto> getByUuids(List<String> uuids) {
		return userService.getByUuids(uuids).stream().map(UserFacadeEjb::toDto).collect(Collectors.toList());
	}

	@Override
	@PermitAll
	public List<String> getAllUuids() {

		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		return userService.getAllUuids();
	}

	@Override
	@PermitAll
	public UserDto getByUuid(String uuid) {
		return toDto(userService.getByUuid(uuid));
	}

	@Override
	@PermitAll
	public UserDto getByUserName(String userName) {
		return toDto(userService.getByUserName(userName));
	}

	@Override
	@PermitAll
	public UserDto saveUser(@Valid UserDto dto, boolean isUserSettingsUpdate) {
		if ((!userService.hasRight(UserRight.USER_CREATE) && !userService.hasRight(UserRight.USER_EDIT) && !DataHelper.isSame(getCurrentUser(), dto))
			|| (featureConfigurationFacade.isFeatureEnabled(FeatureType.AUTH_PROVIDER_TO_SORMAS_USER_SYNC)
				&& !DataHelper.isSame(getCurrentUser(), dto))) {
			throw new AccessDeniedException(I18nProperties.getString(Strings.errorForbidden));
		}

		User existingUser = userService.getByUuid(dto.getUuid());
		// current user should be able to edit itself
		if (!DataHelper.isSame(userService.getCurrentUser(), dto)) {
			FacadeHelper.checkCreateAndEditRights(existingUser, userService, UserRight.USER_CREATE, UserRight.USER_EDIT);
		}

		if (!isLoginUnique(existingUser == null ? null : existingUser.getUuid(), dto.getUserName())) {
			throw new ValidationException(I18nProperties.getValidationError(Validations.userNameNotUnique));
		}

		validateUserRoles(dto.getUserRoles(), isUserSettingsUpdate, existingUser, dto.getCreationDate() != null);

		User user = fillOrBuildEntity(dto, existingUser, true);
		userService.ensurePersisted(user);

		if (existingUser == null) {
			userCreateEvent.fire(new UserCreateEvent(user));
		} else {
			userUpdateEvent.fire(new UserUpdateEvent(existingUser, user));
		}

		return toDto(user);
	}

	@Nullable
	private User validateUserRoles(Set<UserRoleReferenceDto> roles, boolean isUserSettingsUpdate, User user, boolean isUserUpdate) {
		Collection<UserRoleDto> newRoles = userRoleFacade.getByReferences(roles);

		try {
			userRoleFacade.validateUserRoleCombination(newRoles);
		} catch (UserRoleDto.UserRoleValidationException e) {
			throw new ValidationException(e);
		}

		User oldUser = null;
		Set<UserRight> oldUserRights = Collections.emptySet();
		if (isUserUpdate) {
			try {
				oldUser = (User) BeanUtils.cloneBean(user);
				oldUserRights = UserRole.getUserRights(oldUser.getUserRoles());
			} catch (Exception e) {
				throw new IllegalArgumentException("Invalid bean access", e);
			}
		}

		if (DataHelper.isSame(user, userService.getCurrentUser()) && !isUserSettingsUpdate) {
			Set<UserRight> newUserRights = UserRoleDto.getUserRights(newRoles);

			if (oldUserRights.contains(UserRight.USER_ROLE_EDIT) && !newUserRights.contains(UserRight.USER_ROLE_EDIT)) {
				throw new ValidationException(I18nProperties.getValidationError(Validations.removeUserRightEditRightFromOwnUser));
			} else if (!newUserRights.contains(UserRight.USER_EDIT)) {
				throw new ValidationException(I18nProperties.getValidationError(Validations.removeUserEditRightFromOwnUser));
			}
		}
		return oldUser;
	}

	@Override
	@RightsAllowed(UserRight._USER_EDIT)
	public UserDto saveUserRolesAndRestrictions(UserDto userDto, Set<UserRoleReferenceDto> userRoles) {
		User user = userService.getByReferenceDto(userDto.toReference());

		User oldUser;
		try {
			oldUser = (User) BeanUtils.cloneBean(user);
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}

		validateUserRoles(userDto.getUserRoles(), false, user, true);

		fillEntityUserRoles(user, userDto);

		user.setRegion(regionService.getByReferenceDto(userDto.getRegion()));
		user.setDistrict(districtService.getByReferenceDto(userDto.getDistrict()));
		user.setCommunity(communityService.getByReferenceDto(userDto.getCommunity()));
		user.setHealthFacility(facilityService.getByReferenceDto(userDto.getHealthFacility()));
		user.setAssociatedOfficer(userService.getByReferenceDto(userDto.getAssociatedOfficer()));
		user.setLaboratory(facilityService.getByReferenceDto(userDto.getLaboratory()));
		user.setPointOfEntry(pointOfEntryService.getByReferenceDto(userDto.getPointOfEntry()));
		user.setLimitedDiseases(userDto.getLimitedDiseases());

		userService.ensurePersisted(user);

		userUpdateEvent.fire(new UserUpdateEvent(oldUser, user));

		return toDto(user);
	}

	@Override
	@RightsAllowed(UserRight._USER_VIEW)
	public List<UserDto> getIndexList(UserCriteria userCriteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);
		Root<User> user = cq.from(User.class);
		Join<User, District> district = user.join(User.DISTRICT, JoinType.LEFT);
		Join<User, Location> address = user.join(User.ADDRESS, JoinType.LEFT);
		Join<Location, Region> region = address.join(Location.REGION, JoinType.LEFT);
		Join<User, Facility> facility = user.join(User.HEALTH_FACILITY, JoinType.LEFT);

		// TODO: We'll need a user filter for users at some point, to make sure that users can edit their own details,
		// but not those of others

		Predicate filter = null;

		if (userCriteria != null) {
			filter = userService.buildCriteriaFilter(userCriteria, cb, user);
		}

		if (userCriteria != null && Boolean.TRUE.equals(userCriteria.getShowOnlyRestrictedAccessToAssignedEntities())) {
			Join<Object, Object> rolesJoin = user.join(User.USER_ROLES, JoinType.LEFT);
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(rolesJoin.get(UserRole.RESTRICT_ACCESS_TO_ASSIGNED_ENTITIES), true));
		}

		if (filter != null) {
			/*
			 * No preemptive distinct because this does collide with
			 * ORDER BY User.location.address (which is not part of the SELECT clause).
			 */
			cq.where(filter);
		}

		if (sortProperties != null && !sortProperties.isEmpty()) {
			List<Order> orderList = new ArrayList<>();
			for (SortProperty sortProperty : sortProperties) {
				CriteriaBuilderHelper.OrderBuilder orderBuilder = CriteriaBuilderHelper.createOrderBuilder(cb, sortProperty.ascending);
				final List<Order> order;
				switch (sortProperty.propertyName) {
				case EntityDto.UUID:
				case UserDto.ACTIVE:
					order = orderBuilder.build(user.get(sortProperty.propertyName));
					break;
				case UserDto.USER_NAME:
				case UserDto.USER_EMAIL:
					order = orderBuilder.build(cb.lower(user.get(sortProperty.propertyName)));
					break;
				case UserDto.NAME:
					order = orderBuilder.build(cb.lower(user.get(User.FIRST_NAME)), cb.lower(user.get(User.LAST_NAME)));
					break;
				case UserDto.DISTRICT:
					order = orderBuilder.build(cb.lower(district.get(District.NAME)));
					break;
				case UserDto.ADDRESS:
					order = orderBuilder.build(cb.lower(region.get(Region.NAME)));
					break;
				case UserDto.HEALTH_FACILITY:
					order = orderBuilder.build(cb.lower(facility.get(Facility.NAME)));
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				orderList.addAll(order);
			}
			cq.orderBy(orderList);
		} else {
			cq.orderBy(cb.desc(user.get(AbstractDomainObject.CHANGE_DATE)));
		}

		cq.select(user);

		final List<UserDto> resultList = QueryHelper.getResultList(em, cq, first, max, UserFacadeEjb::toDto);
		// because the selection is based on User entity and we need userRole join (we cannot avoid it) which pulls duplicate rows,
		// and distinct on the query does not work because of sorting (e.g. Address)
		// we need to deduplicate the list using java code
		return resultList.stream().distinct().collect(Collectors.toList());
	}

	@Override
	@RightsAllowed(UserRight._USER_VIEW)
	public long count(UserCriteria userCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<User> root = cq.from(User.class);

		Predicate filter = null;

		if (userCriteria != null) {
			filter = userService.buildCriteriaFilter(userCriteria, cb, root);
		}

		if (userCriteria != null && Boolean.TRUE.equals(userCriteria.getShowOnlyRestrictedAccessToAssignedEntities())) {
			Join<Object, Object> rolesJoin = root.join(User.USER_ROLES, JoinType.LEFT);
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(rolesJoin.get(UserRole.RESTRICT_ACCESS_TO_ASSIGNED_ENTITIES), true));
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.countDistinct(root));
		return em.createQuery(cq).getSingleResult();
	}

	private User fillOrBuildEntity(UserDto source, User target, boolean checkChangeDate) {
		boolean targetWasNull = isNull(target);

		target = DtoHelper.fillOrBuildEntity(source, target, userService::createUser, checkChangeDate);

		if (targetWasNull) {
			FacadeHelper.setUuidIfDtoExists(target.getAddress(), source.getAddress());
		}

		target.setActive(source.isActive());
		target.setFirstName(source.getFirstName());
		target.setLastName(source.getLastName());
		target.setPhone(source.getPhone());
		target.setAddress(locationFacade.fillOrBuildEntity(source.getAddress(), target.getAddress(), checkChangeDate));

		target.setUserName(source.getUserName());
		target.setUserEmail(source.getUserEmail());

		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setCommunity(communityService.getByReferenceDto(source.getCommunity()));
		target.setHealthFacility(facilityService.getByReferenceDto(source.getHealthFacility()));
		target.setAssociatedOfficer(userService.getByReferenceDto(source.getAssociatedOfficer()));
		target.setLaboratory(facilityService.getByReferenceDto(source.getLaboratory()));
		target.setPointOfEntry(pointOfEntryService.getByReferenceDto(source.getPointOfEntry()));
		target.setLimitedDiseases(source.getLimitedDiseases());
		target.setLanguage(source.getLanguage());
		target.setHasConsentedToGdpr(source.isHasConsentedToGdpr());

		fillEntityUserRoles(target, source);

		target.updateJurisdictionLevel();

		return target;
	}

	private void fillEntityUserRoles(User target, UserDto source) {
		//Make sure userroles of target are attached
		Set<UserRole> userRoles = Optional.of(target).map(User::getUserRoles).orElseGet(HashSet::new);
		target.setUserRoles(userRoles);
		//Preparation
		Set<String> targetUserRoleUuids = target.getUserRoles().stream().map(UserRole::getUuid).collect(Collectors.toSet());
		Set<String> sourceUserRoleUuids = source.getUserRoles().stream().map(UserRoleReferenceDto::getUuid).collect(Collectors.toSet());
		List<UserRole> newUserRoles = source.getUserRoles()
			.stream()
			.filter(userRoleReferenceDto -> !targetUserRoleUuids.contains(userRoleReferenceDto.getUuid()))
			.map(userRoleReferenceDto -> userRoleService.getByReferenceDto(userRoleReferenceDto))
			.collect(Collectors.toList());
		//Add new userroles
		target.getUserRoles().addAll(newUserRoles);
		//Remove userroles that were removed
		target.getUserRoles().removeIf(userRole -> !sourceUserRoleUuids.contains(userRole.getUuid()));
	}

	@Override
	@RightsAllowed({
		UserRight._USER_CREATE,
		UserRight._USER_EDIT })
	public boolean isLoginUnique(String uuid, String userName) {
		return userService.isLoginUnique(uuid, userName);
	}

	@Override
	@RightsAllowed({
		UserRight._USER_CREATE,
		UserRight._USER_EDIT })
	public String resetPassword(String uuid) {
		String resetPassword = userService.resetPassword(uuid);
		passwordResetEvent.fire(new PasswordResetEvent(userService.getByUuid(uuid)));
		return resetPassword;
	}

	@Override
	@PermitAll
	@AuditIgnore
	public UserDto getCurrentUser() {
		return toDto(userService.getCurrentUser());
	}

	@PermitAll
	@AuditIgnore
	public String updatePassword(String uuid, String password) {
		String updatePassword = userService.updatePassword(uuid, password);
		passwordResetEvent.fire(new PasswordResetEvent(userService.getByUuid(uuid)));

		return updatePassword;
	}

	@PermitAll
	@Override
	public String updateUserPassword(String uuid, String password, String currentPassword) {
		String updatePassword = updatePassword(uuid, password);
		passwordResetEvent.fire(new PasswordResetEvent(userService.getByUuid(uuid)));

		return updatePassword;

	}
	@PermitAll
	@Override
	public String generatePassword() {
		return userService.generatePassword();

	}


	
	@Override
	@PermitAll
	public UserReferenceDto getCurrentUserAsReference() {
		return new UserReferenceDto(userService.getCurrentUser().getUuid());
	}

	@Override
	@PermitAll
	@AuditIgnore
	public Set<UserRight> getValidLoginRights(String userName, String password) {
			User user = userService.getByUserName(userName);
		if (user != null && user.isActive() && DataHelper.equal(user.getPassword(), PasswordHelper.encodePassword(password, user.getSeed()))) {
			return new HashSet<>(UserRole.getUserRights(user.getUserRoles()));
		}

		return null;
	}

	@PermitAll
	@Override
	public Set<UserRoleDto> getValidLoginRoles(String userName, String password) {
		User user = userService.getByUserName(userName);
		if (user != null && user.isActive()
			&& (DataHelper.equal(user.getPassword(), PasswordHelper.encodePassword(password, user.getSeed())))) {
			return getUserRoles(toDto(user));
		}
		return Collections.emptySet();
	}
	
	@Override
	@PermitAll
	//@RightsAllowed(UserRight._USER_EDIT)
	public boolean validatePassword(String uuid, String password) {
		User user = userService.getCurrentUser();
		if (user != null) {
			return DataHelper.equal(user.getPassword(), PasswordHelper.encodePassword(password, user.getSeed()));
		}
		return false;
	}

	@PermitAll
	@Override
	public String checkPasswordStrength(String password) {
		return PasswordValidator.checkPasswordStrength(password);
	}

	@PermitAll
	@Override
	public void removeUserAsSurveillanceAndContactOfficer(String userUuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Case> caseQuery = cb.createQuery(Case.class);
		Root<Case> caseRoot = caseQuery.from(Case.class);
		Join<Case, User> surveillanceOfficerJoin = caseRoot.join(Case.SURVEILLANCE_OFFICER, JoinType.LEFT);

		caseQuery.where(cb.equal(surveillanceOfficerJoin.get(AbstractDomainObject.UUID), userUuid));
		List<Case> cases = em.createQuery(caseQuery).getResultList();

		List<User> possibleUsersBasedOnCasesResponsibleDistrict = getPossibleUsersBasedOnCasesResponsibleDistrict(cases);
		List<User> possibleUsersBasedOnCasesDistrict = getPossibleUsersBasedOnCasesDistrict(cases);
		Set<User> possibleUsersBasedOnCasesFacility = getPossibleUsersBasedOnCasesFacility(cases);

		cases.forEach(c -> {
			c.setSurveillanceOfficer(null);
			caseFacade.setCaseResponsible(
				c,
				true,
				possibleUsersBasedOnCasesResponsibleDistrict,
				possibleUsersBasedOnCasesDistrict,
				possibleUsersBasedOnCasesFacility);
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

	private List<User> getPossibleUsersBasedOnCasesResponsibleDistrict(List<Case> cases) {
		List<String> responsibleDistrictsUuidsAmongCases = cases.stream()
			.map(Case::getResponsibleDistrict)
			.collect(Collectors.toSet())
			.stream()
			.filter(Objects::nonNull)
			.collect(Collectors.toSet())
			.stream()
			.map(District::getUuid)
			.collect(Collectors.toList());

		List<User> possibleUserForReplacement = getUsersFromCasesByDistricts(responsibleDistrictsUuidsAmongCases);

		return possibleUserForReplacement;
	}

	private List<User> getPossibleUsersBasedOnCasesDistrict(List<Case> cases) {
		List<String> districtsUuidsAmongCases = cases.stream()
			.map(Case::getDistrict)
			.collect(Collectors.toSet())
			.stream()
			.filter(Objects::nonNull)
			.collect(Collectors.toSet())
			.stream()
			.map(District::getUuid)
			.collect(Collectors.toList());

		List<User> possibleUserForReplacement = getUsersFromCasesByDistricts(districtsUuidsAmongCases);

		return possibleUserForReplacement;
	}

	@NotNull
	private List<User> getUsersFromCasesByDistricts(List<String> districtsUuidsAmongCases) {
		List<User> possibleUserForReplacement = userService
			.getUserReferencesByJurisdictions(
				null,
				districtsUuidsAmongCases,
				null,
				Collections.singletonList(JurisdictionLevel.DISTRICT),
				Arrays.asList(UserRight.CASE_RESPONSIBLE))
			.stream()
			.map(userReference -> userService.getByUuid(userReference.getUuid()))
			.filter(user -> !UserHelper.isRestrictedToAssignEntities(user))
			.collect(Collectors.toList());
		return possibleUserForReplacement;
	}

	private Set<User> getPossibleUsersBasedOnCasesFacility(List<Case> cases) {
		return cases.stream()
			.map(Case::getHealthFacility)
			.filter(Objects::nonNull)
			.filter(f -> f.getType() == FacilityType.HOSPITAL)
			.distinct()
			.map(userService::getFacilityUsersOfHospital)
			.flatMap(Collection::stream)
			.collect(Collectors.toSet());
	}

	@Override
	@RightsAllowed({
		UserRight._USER_CREATE,
		UserRight._USER_EDIT })
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
	@RightsAllowed({
		UserRight._USER_CREATE,
		UserRight._USER_EDIT,
		UserRight._SYSTEM })
	public void syncUsersFromAuthenticationProvider() {

		if (!isSyncEnabled()) {
			throw new ForbiddenException("No default role for new users from authentication provider is configured");
		}

		String defaultRoleName = configFacade.getAuthenticationProviderSyncedNewUserRole();
		UserRole defaultRole = userRoleService.getByCaption(defaultRoleName);

		if (defaultRole == null) {
			throw new ForbiddenException("No default role for new users from authentication provider is configured");
		}

		List<User> existingUsers = userService.getAll();

		syncUsersFromProviderEventEvent.fire(new SyncUsersFromProviderEvent(existingUsers, (syncedUsers, deletedUsers) -> {
			syncedUsers.forEach(user -> {
				if (user.getId() == null) {
					user.setUuid(DataHelper.createUuid());
					user.setUserRoles(Collections.singleton(defaultRole));
					UserService.setNewPassword(user);
				}
				userService.ensurePersisted(user);
			});

			deletedUsers.forEach(user -> {
				user.setActive(false);
				userService.ensurePersisted(user);
			});
		}));
	}

	@Override
	@RightsAllowed({
		UserRight._USER_CREATE,
		UserRight._USER_EDIT,
		UserRight._SYSTEM })
	public boolean isSyncEnabled() {
		AuthProvider authProvider = AuthProvider.getProvider(configFacade);
		return KEYCLOAK.equalsIgnoreCase(authProvider.getName())
			&& (featureConfigurationFacade.isFeatureDisabled(FeatureType.AUTH_PROVIDER_TO_SORMAS_USER_SYNC)
				|| StringUtils.isNotBlank(configFacade.getAuthenticationProviderSyncedNewUserRole()));

	}

	@Override
	// TODO - default password change only for ADMIN??
	@PermitAll
	public List<UserDto> getUsersWithDefaultPassword() {
		if (userService.hasRight(UserRight.USER_EDIT)) {
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
			User currentUser = userService.getCurrentUser();
			if (DefaultEntityHelper.isDefaultUser(currentUser.getUserName())
				&& DefaultEntityHelper.usesDefaultPassword(currentUser.getUserName(), currentUser.getPassword(), currentUser.getSeed())) {
				return Collections.singletonList(UserFacadeEjb.toDto(currentUser));
			} else {
				return Collections.emptyList();
			}
		}
	}

	@Override
	@RightsAllowed(UserRight._USER_EDIT)
	public List<ProcessedEntity> enableUsers(List<String> userUuids) {
		return updateActiveState(userUuids, true);
	}

	@Override
	@RightsAllowed(UserRight._USER_EDIT)
	public List<ProcessedEntity> disableUsers(List<String> userUuids) {
		return updateActiveState(userUuids, false);
	}

	private List<ProcessedEntity> updateActiveState(List<String> userUuids, boolean active) {
		List<ProcessedEntity> processedEntities = new ArrayList<>();

		List<User> users = userService.getByUuids(userUuids);
		for (User user : users) {
			try {
				User oldUser = (User) BeanUtils.cloneBean(user);
				user.setActive(active);
				userService.ensurePersisted(user);

				userUpdateEvent.fire(new UserUpdateEvent(oldUser, user));
				processedEntities.add(new ProcessedEntity(user.getUuid(), ProcessedEntityStatus.SUCCESS));
			} catch (Exception e) {
				processedEntities.add(new ProcessedEntity(user.getUuid(), ProcessedEntityStatus.INTERNAL_FAILURE));
				logger.error("The event with uuid {} could not be restored due to an Exception", user.getUuid(), e);
			}
		}

		return processedEntities;
	}

	@Override
	@RightsAllowed(UserRight._USER_ROLE_VIEW)
	public long getUserCountHavingRole(UserRoleReferenceDto userRoleRef) {
		return userService.countWithRole(userRoleRef);
	}

	@Override
	@RightsAllowed(UserRight._USER_ROLE_VIEW)
	public List<UserReferenceDto> getUsersHavingOnlyRole(UserRoleReferenceDto userRoleRef) {
		return userService.getAllWithOnlyRole(userRoleRef).stream().map(UserFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	@Override
	@PermitAll
	public List<UserRight> getUserRights(String userUuid) {

		User user = StringUtils.isBlank(userUuid) ? currentUserService.getCurrentUser() : userService.getByUuid(userUuid);

		if (user != null) {
			if (getCurrentUser().getUuid().equals(user.getUuid())
				|| (currentUserService.hasUserRight(UserRight.USER_ROLE_VIEW) && currentUserService.hasUserRight(UserRight.USER_VIEW))) {
				return UserRole.getUserRights(user.getUserRoles()).stream().sorted(Comparator.comparing(Enum::name)).collect(Collectors.toList());
			} else {
				throw new AccessDeniedException(I18nProperties.getString(Strings.errorForbidden));
			}
		} else {
			throw new EntityNotFoundException(I18nProperties.getString(Strings.errorNotFound));
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
