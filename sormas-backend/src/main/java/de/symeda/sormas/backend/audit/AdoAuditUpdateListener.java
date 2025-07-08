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

package de.symeda.sormas.backend.audit;

import javax.ejb.Stateless;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.user.CurrentUserContext;
import de.symeda.sormas.backend.user.User;

/**
 * Stateless JPA entity listener for auditing AbstractDomainObject lifecycle events.
 * 
 * This listener automatically updates audit information on AbstractDomainObject entities
 * during persist and update operations. It captures the current authenticated user
 * and sets them as the change user for tracking purposes.
 * 
 * <p>
 * The listener:
 * <ul>
 * <li>Responds to {@link PrePersist} and {@link PreUpdate} events</li>
 * <li>Only processes entities that extend {@link AbstractDomainObject}</li>
 * <li>Sets the current user as the change user for audit tracking</li>
 * <li>Uses defensive CDI lookup for dependency resolution</li>
 * </ul>
 * 
 * <p>
 * This listener should be registered with JPA entities that extend
 * AbstractDomainObject to enable automatic audit user tracking.
 */
@Stateless
public class AdoAuditUpdateListener {

    private CurrentUserContext currentUserContext;

    /**
     * Sets the current user context for audit operations.
     * This method is called by the CDI container to inject the current user context.
     *
     * @param currentUserContext
     *            the current user context to set
     */
    @Inject
    public void setCurrentUserContext(CurrentUserContext currentUserContext) {
        this.currentUserContext = currentUserContext;
    }

    /**
     * Retrieves the current user context, with fallback to CDI lookup if not injected.
     * This method provides a defensive mechanism to obtain the current user context
     * even if dependency injection fails or is unavailable.
     *
     * @return the current user context, or null if unavailable
     */
    public CurrentUserContext getCurrentUserContext() {
        if (currentUserContext != null) {
            return currentUserContext;
        }
        final Instance<CurrentUserContext> instance = CDI.current().select(CurrentUserContext.class);
        return instance.isUnsatisfied() ? null : instance.get();
    }

    /**
     * JPA lifecycle callback that updates audit information before entity persistence.
     * 
     * This method is automatically invoked by the JPA provider when an AbstractDomainObject
     * entity is about to be persisted or updated. It sets the current authenticated user
     * as the change user for audit tracking purposes.
     * 
     * <p>
     * The method:
     * <ul>
     * <li>Only processes entities that are instances of {@link AbstractDomainObject}</li>
     * <li>Retrieves the current user from the user context</li>
     * <li>Sets the change user on the entity if a current user is available</li>
     * <li>Skips the update if no current user is authenticated</li>
     * </ul>
     * 
     * @param entity
     *            the entity being persisted or updated
     */
    @PrePersist
    @PreUpdate
    public void updateADOAuditRecord(Object entity) {
        if (!(entity instanceof AbstractDomainObject)) {
            return;
        }
        final AbstractDomainObject ado = (AbstractDomainObject) entity;
        final User changeUser = getCurrentUserContext().getUserEntityReference();
        if (changeUser != null) {
            ado.setChangeUser(changeUser);
        }
    }
}
