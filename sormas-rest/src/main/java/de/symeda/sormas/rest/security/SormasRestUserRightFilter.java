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

package de.symeda.sormas.rest.security;

import static javax.ws.rs.Priorities.AUTHORIZATION;

import java.io.IOException;

import javax.annotation.Priority;
import javax.enterprise.inject.spi.CDI;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;

import de.symeda.sormas.api.user.UserRight;

/**
 * Filter to ensure authenticated user has SORMAS_REST user right,
 * return status 403 when missing
 */

@Priority(AUTHORIZATION)
public class SormasRestUserRightFilter implements ContainerRequestFilter {

	private final SecurityContext securityContext;

	private final HttpServletRequest request;
	private final HttpServletResponse response;

	public SormasRestUserRightFilter(HttpServletRequest request, HttpServletResponse response) {
		securityContext = CDI.current().select(SecurityContext.class).get();

		this.request = request;
		this.response = response;
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		if (!isAuthenticated()) {

			AuthenticationStatus status = securityContext.authenticate(request, response, AuthenticationParameters.withParams());

			// Authentication was not done at all (i.e. no credentials present) or
			// authentication failed (i.e. wrong credentials, credentials expired, etc)
			if (status == AuthenticationStatus.NOT_DONE || status == AuthenticationStatus.SEND_FAILURE) {
				throw new NotAuthorizedException("Authentication resulted in " + status, Response.status(Response.Status.UNAUTHORIZED).build());
			}

			if (status == AuthenticationStatus.SUCCESS && !isAuthenticated()) { // compensate for possible Soteria bug, need to investigate
				throw new NotAuthorizedException(
					"Authentication not done (i.e. no JWT credential found)",
					Response.status(Response.Status.UNAUTHORIZED).build());
			}

		}

		boolean hasRestRole = requestContext.getSecurityContext().isUserInRole(UserRight.SORMAS_REST.name());

		if (!hasRestRole) {
			throw new ForbiddenException("Caller not in requested role");
		}
	}

	private boolean isAuthenticated() {
		return securityContext.getCallerPrincipal() != null;
	}
}
