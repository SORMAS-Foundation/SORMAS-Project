/*
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
 */
package de.symeda.sormas.rest.security;

import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.FacadeProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.HttpClientUtils;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.util.BasicAuthHelper;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationException;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.security.enterprise.credential.CallerOnlyCredential;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Mechanism which allows configuration of multiple providers trough a system property.
 * <p/>
 * Supported at the moment: Sormas and Keycloak.
 * <p/>
 * Sormas provider accepts Basic authentication. The {@link SormasIdentityStore} validated the credentials and obtains the user's roles.
 * <p/>
 * Keycloak provider accepts Bearer and if enabled also Basic authentication. Configuration provided by {@link KeycloakConfigResolver}.<br/>
 * The token validation is done trough {@link KeycloakFilter} and then the user is authenticated and it's roles are obtained by {@link KeycloakIdentityStore}.<br/>
 * <b>Note:</b> As a precondition the Keycloak user needs a valid role which the {@link KeycloakFilter} will use to pre-validate the request.
 *
 * @author Alex Vidrean
 * @see ConfigFacade#getAuthenticationProvider()
 * @see SormasIdentityStore
 * @see KeycloakIdentityStore
 * @see KeycloakConfigResolver
 */
@ApplicationScoped
public class MultiAuthenticationMechanism implements HttpAuthenticationMechanism {

	private String authenticationProvider;

	@Inject
	private IdentityStoreHandler identityStoreHandler;

	@PostConstruct
	public void init() {
		authenticationProvider = FacadeProvider.getConfigFacade().getAuthenticationProvider();
	}

	@Override
	public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext context)
		throws AuthenticationException {

		if (!hasValidAuthorization(request)) {
			return context.responseUnauthorized();
		}

		Credential credential;
		if (authenticationProvider.equals("KEYCLOAK")) {
			KeycloakSecurityContext keycloakContext = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
			if (keycloakContext == null) {
				//the filter will trigger the validateRequest once more if the context is empty
				return context.doNothing();
			}
			credential = new CallerOnlyCredential(keycloakContext.getToken().getPreferredUsername());
		} else {
			String[] credentials = BasicAuthHelper.parseHeader(request.getHeader("Authorization"));
			if (credentials == null || credentials.length != 2) {
				return context.responseUnauthorized();
			}
			credential = new UsernamePasswordCredential(credentials[0], credentials[1]);
		}

		CredentialValidationResult result = identityStoreHandler.validate(credential);
		return context.notifyContainerAboutLogin(result);
	}

	private boolean hasValidAuthorization(HttpServletRequest request) {
		String authorization = request.getHeader("Authorization");
		return StringUtils.isNotBlank(authorization) && StringUtils.startsWithAny(authorization, "Basic", "Bearer");
	}
}
