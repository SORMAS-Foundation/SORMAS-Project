/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import java.lang.annotation.Annotation;

import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.CDI;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.security.enterprise.credential.CallerOnlyCredential;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.spi.KeycloakAccount;

/**
 * @author Alex Vidrean
 * @since 13-Aug-20
 */
@Typed(KeycloakHttpAuthenticationMechanism.class)
public class KeycloakHttpAuthenticationMechanism implements HttpAuthenticationMechanism {

	@Override
	public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) {
		if (!hasValidAuthorization(request)) {
			return httpMessageContext.responseUnauthorized();
		}
		KeycloakSecurityContext keycloakContext = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
		if (keycloakContext == null) {
			//the filter will trigger the validateRequest once more if the context is empty
			return httpMessageContext.doNothing();
		}
		Credential credential = new CallerOnlyCredential(keycloakContext.getToken().getPreferredUsername());
		IdentityStoreHandler identityStoreHandler = CDI.current().select(IdentityStoreHandler.class, new Annotation[0]).get();
		CredentialValidationResult result = identityStoreHandler.validate(credential);

		if (result.getStatus() == CredentialValidationResult.Status.VALID) {
			KeycloakAccount keycloakAccount = (KeycloakAccount) request.getAttribute(KeycloakAccount.class.getName());
			keycloakAccount.getRoles().addAll(result.getCallerGroups());
		}

		httpMessageContext.setRegisterSession(result.getCallerPrincipal().getName(), result.getCallerGroups());
		return httpMessageContext.notifyContainerAboutLogin(result);
	}

	private boolean hasValidAuthorization(HttpServletRequest request) {
		String authorization = request.getHeader("Authorization");
		return StringUtils.isNotBlank(authorization) && StringUtils.startsWithAny(authorization, "Basic", "Bearer");
	}
}
