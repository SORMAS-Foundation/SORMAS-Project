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

import javax.annotation.Priority;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import de.symeda.sormas.api.audit.AuditIgnore;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.AdoService;
import de.symeda.sormas.backend.user.CurrentUserContext;

/**
 * Interceptor that automatically audits changes to AbstractDomainObject (ADO) entities.
 * 
 * This interceptor is triggered by the {@link AuditRecordUpdate} annotation and operates on
 * methods of {@link AdoService} classes. It captures method invocations that modify ADO entities
 * and schedules post-commit updates to record the change user information.
 * 
 * <p>
 * The interceptor:
 * <ul>
 * <li>Executes around methods annotated with {@link AuditRecordUpdate}</li>
 * <li>Only processes methods on {@link AdoService} instances</li>
 * <li>Schedules audit updates for all {@link AbstractDomainObject} parameters</li>
 * <li>Records the current user as the change user</li>
 * </ul>
 * 
 * <p>
 * The audit updates are scheduled to execute after transaction commit to ensure
 * data consistency and avoid interference with the primary business logic.
 */
@Interceptor
@AuditRecordUpdate
@AuditIgnore
@Priority(Interceptor.Priority.APPLICATION + 100)
public class AuditRecordUpdateInterceptor {

    private CurrentUserContext currentUserContext;

    private AuditTransactionSchedulerService auditService;

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
     * Sets the audit entity service for performing audit operations.
     * This method is called by the CDI container to inject the audit service.
     *
     * @param auditService
     *            the audit entity service to set
     */
    @Inject
    public void setAuditService(AuditTransactionSchedulerService auditService) {
        this.auditService = auditService;
    }

    /**
     * Retrieves the audit entity service, with fallback to CDI lookup if not injected.
     * This method provides a defensive mechanism to obtain the audit service
     * even if dependency injection fails or is unavailable.
     *
     * @return the audit entity service, or null if unavailable
     */
    public AuditTransactionSchedulerService getAuditService() {
        if (auditService != null) {
            return auditService;
        }
        final Instance<AuditTransactionSchedulerService> instance = CDI.current().select(AuditTransactionSchedulerService.class);
        return instance.isUnsatisfied() ? null : instance.get();
    }

    /**
     * Intercepts method calls to audit AbstractDomainObject modifications.
     * 
     * This method is the core of the interceptor functionality. It:
     * <ol>
     * <li>Validates that the target is an {@link AdoService} instance</li>
     * <li>Executes the original method</li>
     * <li>Iterates through method parameters to find {@link AbstractDomainObject} instances</li>
     * <li>Schedules post-commit audit updates for each ADO found</li>
     * </ol>
     * 
     * <p>
     * The audit updates record the current user as the change user for tracking
     * who made modifications to the entities. If no current user is available,
     * the audit update is skipped.
     * 
     * <p>
     * Note: This interceptor can only be applied to methods of {@link AdoService} instances.
     * Attempting to use it on other types will result in an {@link IllegalStateException}.
     *
     * @param ctx
     *            the invocation context containing method and parameter information
     * @return the result of the original method invocation
     * @throws Exception
     *             if the original method throws an exception
     * @throws IllegalStateException
     *             if the target is not an AdoService instance
     */
    @AroundInvoke
    public Object auditMethod(InvocationContext ctx) throws Exception {
        if (!(ctx.getTarget() instanceof AdoService<?>)) {
            throw new IllegalStateException("AuditADOChangeUserInterceptor can only be applied to methods of AdoService instances.");
        }

        // do after the method execution
        Object result = null;
        try {
            result = ctx.proceed();
        } catch (Exception e) {
            throw e;
        }

        // For each AbstractDomainObject parameter, schedule a post-commit change user ID update
        for (Object param : ctx.getParameters()) {
            if (!(param instanceof AbstractDomainObject)) {
                continue;
            }
            AbstractDomainObject ado = (AbstractDomainObject) param;

            Long changeUserId = getCurrentUserContext().getUserId();

            if (changeUserId == null) {
                //TODO - should we set the SYSTEM user here?
                continue;
            }

            //TODO: Handle @PrePersist vs @PreUpdate differently?
            getAuditService().schedulePostCommitAuditRecordUpdate(ado.getClass(), ado.getId(), changeUserId);
        }

        return result;
    }
}
