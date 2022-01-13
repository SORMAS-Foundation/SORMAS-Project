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

package de.symeda.sormas.ui.security;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationException;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
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
 * Sormas provider uses the {@link CustomFormAuthenticationMechanism}. The {@link SormasIdentityStore} validated the credentials and obtains the user's roles.
 * <p/>
 * Keycloak provider uses the {@link OpenIdAuthenticationMechanism}  which configures itself automatically trough the Keycloak Open ID URL.
 * The token validation and it's roles are obtained by {@link SormasOpenIdIdentityStore}.<br/>
 *
 * @author Alex Vidrean
 * @see DefaultOpenIdAuthenticationDefinition
 * @see CustomFormAuthenticationMechanism
 * @see OpenIdAuthenticationMechanism
 * @see SormasOpenIdIdentityStore
 * @since 12-Aug-20
 */
@ApplicationScoped
public class MultiAuthenticationMechanism implements HttpAuthenticationMechanism {

	private final static Logger logger = LoggerFactory.getLogger(MultiAuthenticationMechanism.class);

	private final HttpAuthenticationMechanism authenticationMechanism;

	@Inject
	public MultiAuthenticationMechanism(
		OpenIdAuthenticationMechanism openIdAuthenticationMechanism, CustomFormAuthenticationMechanism customFormAuthenticationMechanism) {

		String authenticationProvider = FacadeProvider.getConfigFacade().getAuthenticationProvider();
		if (authenticationProvider.equalsIgnoreCase(AuthProvider.KEYCLOAK)) {
			try {
				authenticationMechanism = openIdAuthenticationMechanism;
			} catch (IllegalArgumentException e) {
				logger.warn("Undefined KEYCLOAK configuration. Configure the properties or disable the KEYCLOAK authentication provider.");
				throw e;
			}
		} else {
			customFormAuthenticationMechanism.setLoginToContinue(new LoginToContinueAnnotationLiteral("/login", false, "", "/login-error"));
			authenticationMechanism = customFormAuthenticationMechanism;
		}
	}

	@Override
	public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext)
		throws AuthenticationException {
		return authenticationMechanism.validateRequest(request, response, httpMessageContext);
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
