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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.user;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.NotificationProtocol;
import de.symeda.sormas.api.user.NotificationType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRoleCriteria;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilterAndJurisdiction;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;

@Stateless
@LocalBean
public class UserRoleService extends AdoServiceWithUserFilterAndJurisdiction<UserRole> {

	@EJB
	private UserService userService;

	public UserRoleService() {
		super(UserRole.class);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, UserRole> from) {
		// a user can read all user role configurations
		return null;
	}

	public UserRole getByCaption(String caption) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UserRole> cq = cb.createQuery(UserRole.class);
		Root<UserRole> from = cq.from(UserRole.class);
		cq.where(cb.equal(from.get(UserRole.CAPTION), caption));

		UserRole entity = em.createQuery(cq).getResultList().stream().findFirst().orElse(null);

		return entity;
	}

	public UserRole getByLinkedDefaultUserRole(DefaultUserRole linkedDefaultUserRole) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UserRole> cq = cb.createQuery(UserRole.class);
		Root<UserRole> from = cq.from(UserRole.class);
		cq.where(cb.equal(from.get(UserRole.LINKED_DEFAULT_USER_ROLE), linkedDefaultUserRole));
		cq.orderBy(cb.asc(from.get(UserRole.CREATION_DATE)));

		UserRole entity = em.createQuery(cq).getResultList().stream().findFirst().orElse(null);

		return entity;
	}

	public List<String> getDeletedUuids(Date since) {

		String queryString = "SELECT " + AbstractDomainObject.UUID + " FROM " + UserRole.TABLE_NAME + AbstractDomainObject.HISTORY_TABLE_SUFFIX + " h"
			+ " WHERE sys_period @> CAST (?1 AS timestamptz)" + " AND NOT EXISTS (SELECT FROM " + UserRole.TABLE_NAME + " WHERE "
			+ AbstractDomainObject.ID + " = h." + AbstractDomainObject.ID + ")";
		Query nativeQuery = em.createNativeQuery(queryString);
		nativeQuery.setParameter(1, since);
		@SuppressWarnings("unchecked")
		List<String> results = nativeQuery.getResultList();
		return results;
	}

	public List<UserRole> getAllActive() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UserRole> cq = cb.createQuery(UserRole.class);
		Root<UserRole> from = cq.from(UserRole.class);
		cq.where(cb.isTrue(from.get(UserRole.ENABLED)));

		return em.createQuery(cq).getResultList();
	}

	public List<UserRole> getActiveByNotificationTypes(NotificationProtocol protocol, Set<NotificationType> notificationTypes) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UserRole> cq = cb.createQuery(UserRole.class);
		Root<UserRole> from = cq.from(UserRole.class);

		Join<UserRole, NotificationType> notificationsJoin =
			from.join(NotificationProtocol.EMAIL.equals(protocol) ? UserRole.EMAIL_NOTIFICATIONS : UserRole.SMS_NOTIFICATIONS, JoinType.LEFT);
		Predicate notificationsFilter = notificationsJoin.in(notificationTypes);

		cq.where(cb.and(cb.isTrue(from.get(UserRole.ENABLED)), notificationsFilter));

		return em.createQuery(cq).getResultList();
	}

	public boolean hasUserRight(Collection<UserRole> userRoles, UserRight userRight) {

		return hasAnyUserRight(userRoles, Collections.singleton(userRight));
	}

	public boolean hasAnyUserRight(Collection<UserRole> userRoles, Collection<UserRight> userRights) {

		for (UserRole userRole : userRoles) {
			for (UserRight userRight : userRights) {
				if (userRole.getUserRights().contains(userRight)) {
					return true;
				}
			}
		}
		return false;
	}

	public Predicate buildCriteriaFilter(UserRoleCriteria userRoleCriteria, CriteriaBuilder cb, Root<UserRole> from, UserRoleJoins joins) {

		Predicate filter = null;

		if (userRoleCriteria.getEnabled() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(UserRole.ENABLED), userRoleCriteria.getEnabled()));
		}

		if (userRoleCriteria.getUserRight() != null) {
			Predicate userRightsFilter = joins.getUserRights().in(userRoleCriteria.getUserRight());
			filter = CriteriaBuilderHelper.and(cb, filter, userRightsFilter);
		}

		if (userRoleCriteria.getJurisdictionLevel() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(UserRole.JURISDICTION_LEVEL), userRoleCriteria.getJurisdictionLevel()));
		}

		if (userRoleCriteria.getShowOnlyRestrictedAccessToAssignedEntities() != null
			&& Boolean.TRUE.equals(userRoleCriteria.getShowOnlyRestrictedAccessToAssignedEntities())) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(UserRole.RESTRICT_ACCESS_TO_ASSIGNED_ENTITIES), true));
		}

		return filter;
	}

	public boolean isCaptionUnique(String excludedUuid, String caption) {
		UserRole userRole = getByCaption(caption.trim());
		return userRole == null || userRole.getUuid().equals(excludedUuid);
	}

	@Override
	public void deletePermanent(UserRole userRole) {

		List<User> usersWithRole = userService.getAllWithRole(userRole);
		for (User u : usersWithRole) {
			if (u.getUserRoles().size() > 1) {
				u.getUserRoles().remove(userRole);
			} else if (u.getUserRoles().stream().noneMatch(r -> DataHelper.isSame(r, userRole))) {
				u.getUserRoles().remove(userRole);
			} else {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.cantRemoveLastRole));
			}

			userService.ensurePersisted(u);
		}

		super.deletePermanent(userRole);
	}
}
