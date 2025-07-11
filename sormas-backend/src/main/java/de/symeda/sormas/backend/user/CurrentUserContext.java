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

import java.security.Principal;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.persistence.CacheRetrieveMode;
import javax.persistence.CacheStoreMode;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.security.enterprise.SecurityContext;

import org.hibernate.jpa.QueryHints;

import de.symeda.sormas.api.audit.AuditIgnore;
import de.symeda.sormas.backend.util.ModelConstants;

/**
 * Request-scoped context for managing the current authenticated user.
 * 
 * This context provides access to information about the currently authenticated user
 * including their ID, username, and full user entity. It automatically resolves
 * the current user from the security context during initialization and caches
 * the user information for the duration of the request.
 * 
 * <p>
 * The context:
 * <ul>
 * <li>Extracts the principal name from {@link SessionContext} or {@link SecurityContext}</li>
 * <li>Looks up the corresponding {@link User} entity from the database</li>
 * <li>Caches user information for efficient access during the request</li>
 * <li>Handles cases where no authenticated user is present</li>
 * </ul>
 * 
 * <p>
 * This context is automatically initialized when injected and provides
 * a convenient way to access current user information throughout the application.
 */
@RequestScoped
@AuditIgnore
public class CurrentUserContext {

    private Long userId;
    private String username;
    private User userEntity;

    private SessionContext sessionContext;

    private SecurityContext securityContext;

    @PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
    private EntityManager em;

    /**
     * Sets the session context for retrieving caller principal information.
     * This method is called by the CDI container to inject the session context.
     *
     * @param sessionContext
     *            the session context to set
     */
    @Resource
    public void setSessionContext(SessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    /**
     * Retrieves the session context, with fallback to CDI lookup if not injected.
     * This method provides a defensive mechanism to obtain the session context
     * even if dependency injection fails or is unavailable.
     *
     * @return the session context, or null if unavailable
     */
    public SessionContext getSessionContext() {
        if (sessionContext != null) {
            return sessionContext;
        }
        final Instance<SessionContext> instance = CDI.current().select(SessionContext.class);
        return instance.isUnsatisfied() ? null : instance.get();
    }

    /**
     * Sets the security context for retrieving caller principal information.
     * This method is called by the CDI container to inject the security context.
     *
     * @param securityContext
     *            the security context to set
     */
    @Inject
    public void setSecurityContext(SecurityContext securityContext) {
        this.securityContext = securityContext;
    }

    /**
     * Retrieves the security context, with fallback to CDI lookup if not injected.
     * This method provides a defensive mechanism to obtain the security context
     * even if dependency injection fails or is unavailable.
     *
     * @return the security context, or null if unavailable
     */
    public SecurityContext getSecurityContext() {
        if (securityContext != null) {
            return securityContext;
        }
        final Instance<SecurityContext> instance = CDI.current().select(SecurityContext.class);
        return instance.isUnsatisfied() ? null : instance.get();
    }

    /**
     * Initializes the current user context by resolving the authenticated user.
     * 
     * This method is automatically called after dependency injection is complete.
     * It attempts to resolve the current user by:
     * <ol>
     * <li>Extracting the principal name from session or security context</li>
     * <li>Checking if the principal is prohibited (ANONYMOUS or SYSTEM)</li>
     * <li>Looking up the user entity by username in the database</li>
     * <li>Caching the user information for the request duration</li>
     * </ol>
     * 
     * <p>
     * If no valid user is found or the principal is prohibited, all user
     * information fields are set to null.
     */
    @PostConstruct
    public void init() {

        String principalName = null;

        if (getSessionContext() != null && getSessionContext().getCallerPrincipal() != null) {
            principalName = getSessionContext().getCallerPrincipal().getName();
        }

        if (principalName == null && getSecurityContext() != null) {
            Principal principal = getSecurityContext().getCallerPrincipal();
            if (principal != null) {
                principalName = principal.getName();
            }
        }

        if (principalName == null || isProhibitedPrincipal(principalName)) {
            userId = null;
            username = null;
            userEntity = null;
            return;
        }

        TypedQuery<User> query = em.createQuery("SELECT u FROM users u WHERE u.userName = :userName", User.class);
        query.setHint("javax.persistence.cache.retrieveMode", CacheRetrieveMode.USE);
        query.setHint("javax.persistence.cache.storeMode", CacheStoreMode.USE);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        query.setParameter("userName", principalName);

        try {
            final User user = query.getSingleResult();
            userId = user.getId();
            username = user.getUserName();
            userEntity = user;
        } catch (NoResultException e) {
            userId = null;
            username = null;
            userEntity = null;
        }
    }

    /**
     * Returns the ID of the current authenticated user.
     *
     * @return the user ID, or null if no user is authenticated
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Returns the username of the current authenticated user.
     *
     * @return the username, or null if no user is authenticated
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the full user entity of the current authenticated user.
     *
     * @return the user entity, or null if no user is authenticated
     */
    public User getUserEntity() {
        return userEntity;
    }

    /**
     * Returns a JPA reference to the current authenticated user entity.
     * 
     * This method provides a lazy-loaded reference to the user entity using
     * the EntityManager.getReference() method. Unlike getUserEntity(), this
     * method returns a proxy that doesn't immediately load the full entity
     * data from the database, making it more efficient for use cases where
     * only the entity reference is needed (e.g., for setting foreign key
     * relationships).
     *
     * @return a reference to the user entity, or null if no user is authenticated
     */
    public User getUserEntityReference() {
        if (userId == null) {
            return null;
        }
        // Use the entity manager to get a reference to the user entity
        return em.getReference(User.class, userId);
    }

    /**
     * Checks if the given principal name is prohibited from being used as a current user.
     * 
     * Prohibited principals are system-level identities that should not be treated
     * as regular authenticated users for audit and business logic purposes.
     *
     * @param principalName
     *            the principal name to check
     * @return true if the principal is prohibited, false otherwise
     */
    private boolean isProhibitedPrincipal(String principalName) {
        return "ANONYMOUS".equals(principalName) || "SYSTEM".equals(principalName);
    }
}
