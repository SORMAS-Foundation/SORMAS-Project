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

package de.symeda.sormas.ui.security;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationException;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.glassfish.soteria.cdi.LoginToContinueAnnotationLiteral;
import org.glassfish.soteria.mechanisms.CustomFormAuthenticationMechanism;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.AuthProvider;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.ui.security.config.DefaultOpenIdAuthenticationDefinition;
import fish.payara.security.openid.OpenIdAuthenticationMechanism;

/**
 * Mechanism which allows configuration of multiple providers trough a system property.
 * <p/>
 * Supported at the moment: Sormas and Keycloak.
 * <p/>
 * Sormas provider uses the {@link CustomFormAuthenticationMechanism}. The {@link SormasIdentityStore} validated the credentials and obtains
 * the user's roles.
 * <p/>
 * Keycloak provider uses the {@link OpenIdAuthenticationMechanism} which configures itself automatically trough the Keycloak Open ID URL.
 * The token validation and it's roles are obtained by {@link SormasOpenIdIdentityStore}.<br/>
 *
 * @author Alex Vidrean
 * @see DefaultOpenIdAuthenticationDefinition
 * @see CustomFormAuthenticationMechanism
 * @see OpenIdAuthenticationMechanism
 * @see SormasOpenIdIdentityStore
 * @since 12-Aug-20
 */

interface extractCallerFromRequest {

	String extract(HttpServletRequest request);
}

@ApplicationScoped
public class MultiAuthenticationMechanism implements HttpAuthenticationMechanism {

	private final static Logger logger = LoggerFactory.getLogger(MultiAuthenticationMechanism.class);
	private final extractCallerFromRequest extractor;
	private final HttpAuthenticationMechanism authenticationMechanism;

	@Inject
	public MultiAuthenticationMechanism(
		OpenIdAuthenticationMechanism openIdAuthenticationMechanism,
		CustomFormAuthenticationMechanism customFormAuthenticationMechanism) {

		String authenticationProvider = FacadeProvider.getConfigFacade().getAuthenticationProvider();
		if (authenticationProvider.equalsIgnoreCase(AuthProvider.KEYCLOAK)) {
			try {
				authenticationMechanism = openIdAuthenticationMechanism;
				// OIDC Authorization Code Flow  exchanges authorization code for an ID Token, At this point in 
				// time we do not have access to the ID token, therefore, we cannot determine the username.
				// In addition to that, the Keycloak serves the login page on its own, therefore, 
				// this code will never be called if a user enters a wrong password. It is only suitable to audit
				// anything that might break during the flow once we went past the Keycloak server.
				extractor = (request) -> "Username cannot be determined without ID token. Check Keycloak logs for details.";

			} catch (IllegalArgumentException e) {
				logger.warn("Undefined KEYCLOAK configuration. Configure the properties or disable the KEYCLOAK authentication provider.");
				throw e;
			}
		} else {
			customFormAuthenticationMechanism.setLoginToContinue(new LoginToContinueAnnotationLiteral("/login", false, "", "/login-error"));
			authenticationMechanism = customFormAuthenticationMechanism;

			extractor = (request) -> ((UsernamePasswordCredential) ((AuthenticationParameters) request
				.getAttribute("org.glassfish.soteria.security.message.request.authParams")).getCredential()).getCaller();
		}
	}

	@Override
	public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext)
		throws AuthenticationException {
		AuthenticationStatus authenticationStatus = authenticationMechanism.validateRequest(request, response, httpMessageContext);

		if (authenticationStatus.equals(AuthenticationStatus.SEND_FAILURE)) {
			FacadeProvider.getAuditLoggerFacade().logFailedUiLogin(extractor.extract(request), request.getMethod(), request.getRequestURI());
		}

		return authenticationStatus;
	}
	@Override
	public AuthenticationStatus secureResponse(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext)
		throws AuthenticationException {
		return authenticationMechanism.secureResponse(request, response, httpMessageContext);
	}

	@Override
	public void cleanSubject(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) {
		authenticationMechanism.cleanSubject(request, response, httpMessageContext);
	}

}
