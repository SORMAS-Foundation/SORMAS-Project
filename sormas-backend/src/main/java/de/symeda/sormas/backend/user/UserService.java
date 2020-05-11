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

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.user.UserCriteria;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.util.PasswordHelper;

@Stateless
@LocalBean
public class UserService extends AbstractAdoService<User> {

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
	
	@Override
	public User getCurrentUser() {
		return super.getCurrentUser();
	}

	public User getByUserName(String userName) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		ParameterExpression<String> userNameParam = cb.parameter(String.class, User.USER_NAME);
		CriteriaQuery<User> cq = cb.createQuery(getElementClass());
		Root<User> from = cq.from(getElementClass());
		cq.where(cb.equal(from.get(User.USER_NAME), userNameParam));
		
		TypedQuery<User> q = em.createQuery(cq)
			.setParameter(userNameParam, userName);
		
		User entity = q.getResultList().stream()
				.findFirst()
				.orElse(null);
		
		return entity;
	}
	
	public List<User> getAllByRegionAndUserRoles(Region region, UserRole... userRoles) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(getElementClass());
		Root<User> from = cq.from(getElementClass());
		
		Predicate filter = null;
		if (region != null) {
			filter = cb.equal(from.get(User.REGION), region);
		}
		
		if (userRoles.length > 0) {
			Join<User, UserRole> joinRoles = from.join(User.USER_ROLES, JoinType.LEFT);
			Predicate rolesFilter = joinRoles.in(Arrays.asList(userRoles));
			if (filter != null) {
				filter = cb.and(filter, rolesFilter);
			} else {
				filter = rolesFilter;
			}
		}
		
		if (filter != null) {
			cq.where(filter).distinct(true);
		}
		
		cq.orderBy(cb.asc(from.get(AbstractDomainObject.ID)));
		return em.createQuery(cq).getResultList();
	}
	
	public List<User> getInformantsOfFacility(Facility facility) {
		if (facility == null || facility.getType() == FacilityType.LABORATORY) {
			throw new IllegalArgumentException("Facility is null or a laboratory");
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(getElementClass());
		Root<User> from = cq.from(getElementClass());
		Join<User, UserRole> joinRoles = from.join(User.USER_ROLES, JoinType.LEFT);

		Predicate filter = cb.and(cb.equal(from.get(User.HEALTH_FACILITY), facility),
				joinRoles.in(Arrays.asList(new UserRole[] { UserRole.HOSPITAL_INFORMANT })));

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
		Join<User, UserRole> joinRoles = from.join(User.USER_ROLES, JoinType.LEFT);
		
		Predicate filter = cb.and(
				cb.equal(from.get(User.LABORATORY), facility),
				joinRoles.in(Arrays.asList(new UserRole[]{UserRole.LAB_USER, UserRole.EXTERNAL_LAB_USER})));
		
		cq.where(filter).distinct(true);
		return em.createQuery(cq).getResultList();
	}
	
	/**
	 * @param district
	 * @param includeSupervisors If set to true, all supervisors are returned independent of the district
	 * @param userRoles
	 * @return
	 */
	public List<User> getAllByDistrict(District district, boolean includeSupervisors, UserRole... userRoles) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(getElementClass());
		Root<User> from = cq.from(getElementClass());

		buildDistrictQuery(cb, cq, from, district, includeSupervisors, userRoles);
		
		cq.orderBy(cb.asc(from.get(AbstractDomainObject.ID)));
		return em.createQuery(cq).getResultList();
	}

	/**
	 * @param entityDistrict
	 * @param includeSupervisors If set to true, all supervisors are returned independent of the district
	 * @param userRoles
	 * @return
	 */
	public List<User> getAllByAssociatedOfficer(User associatedOfficer, UserRole... userRoles) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(getElementClass());
		Root<User> from = cq.from(getElementClass());

		Predicate filter = cb.equal(from.get(User.ASSOCIATED_OFFICER), associatedOfficer);
		filter = and(cb, filter, buildUserRolesFilter(from, userRoles));
		cq.where(filter);
		
		cq.orderBy(cb.asc(from.get(AbstractDomainObject.ID)));
		return em.createQuery(cq).getResultList();
	}

	public boolean isLoginUnique(String uuid, String userName) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		ParameterExpression<String> userNameParam = cb.parameter(String.class, User.USER_NAME);
		CriteriaQuery<User> cq = cb.createQuery(getElementClass());
		Root<User> from = cq.from(getElementClass());
		cq.where(cb.equal(from.get(User.USER_NAME), userNameParam));
		
		TypedQuery<User> q = em.createQuery(cq)
			.setParameter(userNameParam, userName);
		
		User entity = q.getResultList().stream()
				.findFirst()
				.orElse(null);
		
		return entity==null || (entity!=null&&entity.getUuid().equals(uuid));
	}
	
	public String resetPassword(String userUuid) {
		User user = getByUuid(userUuid);

		if (user == null) {
//			logger.warn("resetPassword() for unknown user '{}'", realmUserUuid);
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
			filter = and(cb, filter, cb.equal(from.get(User.ACTIVE), userCriteria.getActive()));
		}
		if (userCriteria.getUserRole() != null) {
			Join<User, UserRole> joinRoles = from.join(User.USER_ROLES, JoinType.LEFT);
			filter = and(cb, filter, joinRoles.in(Arrays.asList(userCriteria.getUserRole())));
		}
		if (userCriteria.getRegion() != null) {
			filter = and(cb, filter, cb.equal(from.join(Case.REGION, JoinType.LEFT).get(Region.UUID), userCriteria.getRegion().getUuid()));
		}
		if (userCriteria.getDistrict() != null) {
			filter = and(cb, filter, cb.equal(from.join(Case.DISTRICT, JoinType.LEFT).get(District.UUID), userCriteria.getDistrict().getUuid()));
		}
		if (userCriteria.getFreeText() != null) {
			String[] textFilters = userCriteria.getFreeText().split("\\s+");
			for (int i = 0; i < textFilters.length; i++) {
				String textFilter = "%" + textFilters[i].toLowerCase() + "%";
				if (!DataHelper.isNullOrEmpty(textFilter)) {
					Predicate likeFilters = cb.or(
							cb.like(cb.lower(from.get(User.FIRST_NAME)), textFilter),
							cb.like(cb.lower(from.get(User.LAST_NAME)), textFilter),
							cb.like(cb.lower(from.get(User.USER_NAME)), textFilter),
							cb.like(cb.lower(from.get(User.USER_EMAIL)), textFilter),
							cb.like(cb.lower(from.get(User.PHONE)), textFilter),
							cb.like(cb.lower(from.get(User.UUID)), textFilter));
					filter = and(cb, filter, likeFilters);
				}
			}
			
		}
		
		return filter;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<User, User> from) {
		// a user can read all other users
		return null;
	}
	
	public Predicate buildUserRolesFilter(Root<User> from, UserRole... userRoles) {
		if (userRoles.length > 0) {
			Join<User, UserRole> joinRoles = from.join(User.USER_ROLES, JoinType.LEFT);
			return joinRoles.in(Arrays.asList(userRoles));
		}
		return null;
	}

	private void buildDistrictQuery(CriteriaBuilder cb, CriteriaQuery<User> cq, Root<User> from, District district, boolean includeSupervisors, UserRole... userRoles) {
		Predicate filter = cb.equal(from.get(User.DISTRICT), district);
		
		filter = and(cb, filter, buildUserRolesFilter(from, userRoles));

		if (includeSupervisors) {
			Join<User, UserRole> joinRoles = from.join(User.USER_ROLES, JoinType.LEFT);
			Predicate supervisorFilter = joinRoles.in(Arrays.asList(UserRole.CASE_SUPERVISOR, UserRole.CONTACT_SUPERVISOR, UserRole.SURVEILLANCE_SUPERVISOR));
			if (filter != null) {
				filter = cb.or(filter, supervisorFilter);
			} else {
				filter = supervisorFilter;
			}
		}
		
		if (filter != null) {
			cq.where(filter);
		}
	}

	public Long countByAssignedOfficer(User officer, UserRole... userRoles) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<User> from = cq.from(getElementClass());
		Predicate filter = cb.equal(from.get(User.ASSOCIATED_OFFICER), officer);
		filter = and(cb, filter, buildUserRolesFilter(from, userRoles));
		cq.where(filter);
		cq.select(cb.count(from));
		return em.createQuery(cq).getSingleResult();
	}
	
	public Long countByRegion(Region region, UserRole... userRoles) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<User> from = cq.from(getElementClass());
		Predicate filter = cb.equal(from.get(User.REGION), region);
		filter = and(cb, filter, buildUserRolesFilter(from, userRoles));
		cq.where(filter);
		cq.select(cb.count(from));
		return em.createQuery(cq).getSingleResult();
	}
	
	public boolean hasRole (UserRole userRoleName){
        return getCurrentUser().getUserRoles().contains(userRoleName);
    }
	
	public boolean hasAnyRole(Set<UserRole> typeRoles) {
        Set<UserRole> userRoles = getCurrentUser().getUserRoles();
        return !userRoles.stream().filter(userRole -> typeRoles.contains(userRole)).collect(Collectors.toList()).isEmpty();
    }
}
