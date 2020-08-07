/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.rest;

import org.keycloak.KeycloakSecurityContext;

import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ApplicationScoped
public class JWTAuthenticationMechanism implements HttpAuthenticationMechanism {

	@Override
	public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext context) {
        KeycloakSecurityContext keycloakContext = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());

        //TODO: check if the request contains valid Basic / Bearer header
        if (keycloakContext != null) {
            context.notifyContainerAboutLogin(keycloakContext.getToken().getPreferredUsername(), keycloakContext.getToken().getRealmAccess().getRoles());
            return AuthenticationStatus.SUCCESS;
        }
        return context.doNothing();
	}

}
