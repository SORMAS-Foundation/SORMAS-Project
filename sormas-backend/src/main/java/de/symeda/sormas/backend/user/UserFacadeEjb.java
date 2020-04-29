/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.user;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
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
import javax.validation.ValidationException;

import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserCriteria;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserFacade;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.user.UserRole.UserRoleValidationException;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.infrastructure.PointOfEntryFacadeEjb;
import de.symeda.sormas.backend.infrastructure.PointOfEntryService;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.location.LocationFacadeEjb.LocationFacadeEjbLocal;
import de.symeda.sormas.backend.region.CommunityFacadeEjb;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.PasswordHelper;

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
	private CaseService caseService;
	@EJB
	private ContactService contactService;
	@EJB
	private EventService eventService;
	@EJB
	private PointOfEntryService pointOfEntryService;

	@Override
	public List<UserReferenceDto> getUsersByRegionAndRoles(RegionReferenceDto regionRef, UserRole... assignableRoles) {
		Region region = regionService.getByReferenceDto(regionRef);

		return userService.getAllByRegionAndUserRoles(region, assignableRoles).stream().map(f -> toReferenceDto(f))
				.collect(Collectors.toList());
	}

	@Override
	public List<UserReferenceDto> getUserRefsByDistrict(DistrictReferenceDto districtRef,
			boolean includeSupervisors, UserRole... userRoles) {
		
		District district = districtService.getByReferenceDto(districtRef);

		return userService.getAllByDistrict(district, includeSupervisors, userRoles).stream()
				.map(f -> toReferenceDto(f)).collect(Collectors.toList());
	}

	@Override
	public List<UserDto> getUsersByAssociatedOfficer(UserReferenceDto associatedOfficerRef, UserRole... userRoles) {

		User associatedOfficer = userService.getByReferenceDto(associatedOfficerRef);

		return userService.getAllByAssociatedOfficer(associatedOfficer, userRoles).stream()
				.map(f -> toDto(f)).collect(Collectors.toList());
	}

	@Override
	public List<UserDto> getAll(UserRole... roles) {

		return userService.getAllByRegionAndUserRoles(null, roles).stream().map(f -> toDto(f))
				.collect(Collectors.toList());
	}

	@Override
	public List<UserDto> getAllAfter(Date date) {
		return userService.getAllAfter(date, null).stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	@Override
	public List<UserDto> getByUuids(List<String> uuids) {
		return userService.getByUuids(uuids).stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	@Override
	public List<UserReferenceDto> getAllAfterAsReference(Date date) {
		return userService.getAllAfter(date, null).stream().map(c -> toReferenceDto(c)).collect(Collectors.toList());
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
	public UserReferenceDto getByUserNameAsReference(String userName) {
		return toReferenceDto(userService.getByUserName(userName));
	}

	@Override
	public UserDto saveUser(UserDto dto) {

		User user = fromDto(dto);

		try {
			UserRole.validate(user.getUserRoles());
		} catch (UserRoleValidationException e) {
			throw new ValidationException(e);
		}

		userService.ensurePersisted(user);

		return toDto(user);
	}
	
	@Override
	public List<UserDto> getIndexList(UserCriteria userCriteria, int first, int max, List<SortProperty> sortProperties) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);
		Root<User> user = cq.from(User.class);
		Join<User, District> district = user.join(User.DISTRICT, JoinType.LEFT);
		Join<User, Location> address = user.join(User.ADDRESS, JoinType.LEFT);
	
		// TODO: We'll need a user filter for users at some point, to make sure that users can edit their own details,
		// but not those of others
		
		Predicate filter = userService.buildCriteriaFilter(userCriteria, cb, user);
				
		if (filter != null) {
			cq.where(filter).distinct(true);
		}
		
		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<Order>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case UserDto.UUID:
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
					expression = address.get(Location.ADDRESS);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.desc(user.get(User.CHANGE_DATE)));
		}
		
		cq.select(user);
		
		List<User> resultList = em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
		return resultList.stream().map(u -> toDto(u)).collect(Collectors.toList());
	}
	
	@Override
	public long count(UserCriteria userCriteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<User> root = cq.from(User.class);
		
		Predicate filter = userService.buildCriteriaFilter(userCriteria, cb, root);
		
		if (filter != null) {
			cq.where(filter);
		}
		
		cq.select(cb.count(root));
		return em.createQuery(cq).getSingleResult();
	}

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

		source.getUserRoles().size();
		target.setUserRoles(new HashSet<UserRole>(source.getUserRoles()));
		return target;
	}

	public static UserReferenceDto toReferenceDto(User entity) {
		if (entity == null) {
			return null;
		}
		UserReferenceDto dto = new UserReferenceDto(entity.getUuid(), entity.toString());
		return dto;
	}

	private User fromDto(UserDto source) {

		User target = userService.getByUuid(source.getUuid());
		if (target == null) {
			target = userService.createUser();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);

		target.setActive(source.isActive());
		target.setFirstName(source.getFirstName());
		target.setLastName(source.getLastName());
		target.setPhone(source.getPhone());
		target.setAddress(locationFacade.fromDto(source.getAddress()));

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
		
		target.setUserRoles(new HashSet<UserRole>(source.getUserRoles()));

		return target;
	}

	@Override
	public boolean isLoginUnique(String uuid, String userName) {
		return userService.isLoginUnique(uuid, userName);
	}

	@Override
	public String resetPassword(String uuid) {
		return userService.resetPassword(uuid);
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
	public Set<UserRole> getValidLoginRoles(String userName, String password) {
		User user = userService.getByUserName(userName);
		if (user != null && user.isActive()) {
			if (DataHelper.equal(user.getPassword(), PasswordHelper.encodePassword(password, user.getSeed()))) {
				return new HashSet<UserRole>(user.getUserRoles());
			}
		}
		return null;
	}

	@LocalBean
	@Stateless
	public static class UserFacadeEjbLocal extends UserFacadeEjb {
	}
}
