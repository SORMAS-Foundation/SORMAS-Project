/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.util;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.backend.user.CurrentUserService;

/**
 * Implementation for @RightsAllowed annotation to mimic @RightsAllowed
 */
public class RightsAllowedInterceptor {

	@Resource
	private SessionContext sessionContext;
	@EJB
	private CurrentUserService currentUserService;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final HashMap<String, UserRight> userRightValues = new HashMap<>();

	@AroundInvoke
	public Object checkRightsAllowed(InvocationContext context) throws Exception {

		if (context.getMethod().getAnnotation(PermitAll.class) != null) {
			// method permits
			return context.proceed();
		}

		RightsAllowed rightsAllowed = context.getMethod().getAnnotation(RightsAllowed.class);
		if (rightsAllowed == null) {

			if (context.getTarget().getClass().getAnnotation(PermitAll.class) != null) {
				// class permits & no method annotation
				return context.proceed();
			}

			rightsAllowed = context.getTarget().getClass().getAnnotation(RightsAllowed.class);
		}

		if (rightsAllowed != null) {

			if (userRightValues.isEmpty()) {
				synchronized (userRightValues) {
					if (userRightValues.isEmpty()) {
						for (UserRight right : UserRight.values()) {
							userRightValues.put(right.name(), right);
						}
					}
				}
			}

			boolean hasAnyRight = false;
			for (String right : rightsAllowed.value()) {
				if (UserRight._SYSTEM.equals(right)) {
					// UserRight._SYSTEM is used with @RunAs
					if (sessionContext.isCallerInRole(right)) {
						hasAnyRight = true;
						break;
					}

					/*
					 * if (sessionContext.isCallerInRole(right)) does not work with @RightsAllowed, because:
					 * 1. It's slow due to the same reason that lead us to implementing this
					 * 2. It wouldn't work anyway, because roles need to be initialized by user any javax annotation (e.g. @DeclareRoles)
					 */
				} else if (currentUserService.hasUserRight(userRightValues.get(right))) {
					hasAnyRight = true;
					break;
				}
			}

			if (!hasAnyRight) {
				logger.debug("Missing user rights: {}", String.join(", ", rightsAllowed.value()));
				throw new AccessDeniedException(I18nProperties.getString(Strings.errorForbidden));
			}
		}

		/*
		 * either there were no @PermitAll and @RightsAllowed annotations or we passed them
		 * for @DenyAll, we continue to use the existing javax security mechanisms
		 */
		return context.proceed();
	}
}
