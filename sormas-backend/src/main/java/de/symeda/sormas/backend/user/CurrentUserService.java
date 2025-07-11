/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

import static de.symeda.sormas.backend.user.UserHelper.isRestrictedToAssignEntities;

import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import de.symeda.sormas.api.audit.AuditIgnore;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless
@LocalBean
@AuditIgnore
public class CurrentUserService {

	private CurrentUserContext currentUserContext;

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	public CurrentUserService() {
	}

	@Inject
	public void setCurrentUserContext(CurrentUserContext currentUserContext) {
		this.currentUserContext = currentUserContext;
	}

	public CurrentUserContext getCurrentUserContext() {
		if (currentUserContext != null) {
			return currentUserContext;
		}

		final Instance<CurrentUserContext> currentUserContextInstance = CDI.current().select(CurrentUserContext.class);
		return currentUserContextInstance.isUnsatisfied() ? null : currentUserContextInstance.get();
	}

	/**
	 * Returns the User entity corresponding to the current user.
	 *
	 * @TransactionScoped would be better for performance, but is not supported by the CDI based testing framework
	 */
	@RequestScoped
	public User getCurrentUser() {
		final User currentUser = getCurrentUserContext() != null ? getCurrentUserContext().getUserEntity() : null;

		if (currentUser == null) {
			return null;
		}

		return em.contains(currentUser) ? currentUser : em.merge(currentUser);
	}

	public boolean hasUserRight(UserRight userRight) {
		// this only works for user rights that are used in RolesAllowed or DeclareRoles annotations.
		// return context.isCallerInRole(userRight.name());
		// We don't want to have to do this for all the user rights, so we check against the user rights of the current user instead
		if (getCurrentUser() == null || getCurrentUser().getUserRoles() == null) {
			return false;
		}

		return getCurrentUser().hasUserRight(userRight); // todo cache this?
	}

	public boolean hasAnyUserRight(Set<UserRight> userRights) {
		// this only works for user rights that are used in RolesAllowed or DeclareRoles annotations.
		// return context.isCallerInRole(userRight.name());
		// We don't want to have to do this for all the user rights, so we check against the user rights of the current user instead
		if (getCurrentUser() == null || getCurrentUser().getUserRoles() == null) {
			return false;
		}

		return getCurrentUser().hasAnyUserRight(userRights);
	}

	public boolean isRestrictedToAssignedEntities() {
		return isRestrictedToAssignEntities(getCurrentUser());
	}
}
