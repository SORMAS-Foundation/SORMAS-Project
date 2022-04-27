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

import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationException;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.BasicAuthenticationMechanismDefinition;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.security.enterprise.credential.CallerOnlyCredential;
import javax.security.enterprise.credential.Credential;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.glassfish.soteria.Utils;
import org.glassfish.soteria.cdi.BasicAuthenticationMechanismDefinitionAnnotationLiteral;
import org.glassfish.soteria.mechanisms.BasicAuthenticationMechanism;
import org.keycloak.KeycloakSecurityContext;

import de.symeda.sormas.api.AuthProvider;
import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DefaultEntityHelper;
import de.symeda.sormas.rest.security.config.KeycloakConfigResolver;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

/**
 * Mechanism which allows configuration of multiple providers trough a system property.
 * <p/>
 * Supported at the moment: Sormas and Keycloak.
 * <p/>
 * Sormas provider uses the {@link BasicAuthenticationMechanism}. The {@link SormasIdentityStore} validated the credentials and obtains the
 * user's roles.
 * <p/>
 * Keycloak provider uses {@link KeycloakHttpAuthenticationMechanism} and accepts Bearer and if enabled also Basic authentication.
 * Configuration provided by {@link KeycloakConfigResolver}.<br/>
 * The token validation is done trough {@link KeycloakFilter} and then the user is authenticated and it's roles are obtained by
 * {@link KeycloakIdentityStore}.<br/>
 * <b>Note:</b> As a precondition the Keycloak user needs a valid role which the {@link KeycloakFilter} will use to pre-validate the
 * request.
 *
 * @author Alex Vidrean
 * @see ConfigFacade#getAuthenticationProvider()
 * @see SormasIdentityStore
 * @see KeycloakIdentityStore
 * @see KeycloakConfigResolver
 * @see KeycloakHttpAuthenticationMechanism
 */

interface extractCallerFromRequest {

	String extract(HttpServletRequest request);
}

@OpenAPIDefinition(security = {
	@SecurityRequirement(name = "basicAuth"),
	@SecurityRequirement(name = "bearerAuth") })
@SecurityScheme(name = "basicAuth", type = SecuritySchemeType.HTTP, scheme = "basic")
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
@ApplicationScoped
public class MultiAuthenticationMechanism implements HttpAuthenticationMechanism {

	private final extractCallerFromRequest extractor;
	private final HttpAuthenticationMechanism authenticationMechanism;

	@Inject
	public MultiAuthenticationMechanism(KeycloakHttpAuthenticationMechanism keycloakHttpAuthenticationMechanism) {
		String authenticationProvider = FacadeProvider.getConfigFacade().getAuthenticationProvider();
		if (authenticationProvider.equals(AuthProvider.KEYCLOAK)) {
			authenticationMechanism = keycloakHttpAuthenticationMechanism;

			extractor = (request) -> {
				String authorization = request.getHeader("Authorization");
				boolean valid = StringUtils.isNotBlank(authorization) && StringUtils.startsWithAny(authorization, "Basic", "Bearer");
				if (!valid) {
					return String.format("Invalid Authorization header provided! Was: %s", authorization);
				}

				KeycloakSecurityContext keycloakContext = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());

				String caller = null;
				if (keycloakContext != null) {
					caller = keycloakContext.getToken().getPreferredUsername();
				}
				if (StringUtils.isEmpty(caller)) {
					return "Username could not be determined. Try to check Keycloak logs?";
				} else {
					return caller;
				}
			};

		} else {
			BasicAuthenticationMechanismDefinition definition = new BasicAuthenticationMechanismDefinitionAnnotationLiteral("sormas-rest-realm");
			authenticationMechanism = new BasicAuthenticationMechanism(definition);

			extractor = (request) -> {
				String authorization = request.getHeader("Authorization");
				boolean valid = StringUtils.isNotBlank(authorization) && StringUtils.startsWithAny(authorization, "Basic");
				if (!valid) {
					return String.format("Invalid Authorization header provided! Was: %s", authorization);
				}
				String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
				if (authorizationHeader != null && authorizationHeader.toLowerCase().startsWith("basic")) {
					String encodedCredentials = authorizationHeader.substring("Basic".length()).trim();
					String credentials = new String(Base64.decodeBase64(encodedCredentials));
					return credentials.split(":", 2)[0];
				} else {
					return "Username could not be determined.";
				}
			};
		}

	}

	@Override
	public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext context)
		throws AuthenticationException {
		if (request.getPathInfo().startsWith(SormasToSormasApiConstants.RESOURCE_PATH)) {
			// S2S auth will be handled by S2SAuthFilter
			return validateRequestS2S(context);
		}
		AuthenticationStatus authenticationStatus = authenticationMechanism.validateRequest(request, response, context);
		if (authenticationStatus.equals(AuthenticationStatus.SEND_FAILURE)) {

			FacadeProvider.getAuditLoggerFacade().logFailedRestLogin(extractor.extract(request), request.getMethod(), request.getRequestURI());
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

	private AuthenticationStatus validateRequestS2S(HttpMessageContext context) {

		UserDto s2sUser = FacadeProvider.getUserFacade().getByUserName(DefaultEntityHelper.SORMAS_TO_SORMAS_USER_NAME);
		if (s2sUser == null) {
			return AuthenticationStatus.SEND_FAILURE;
		}
		Set<UserRight> userRights = FacadeProvider.getUserFacade()
			.getUserRoles(s2sUser)
			.stream()
			.flatMap(userRoleDto -> userRoleDto.getUserRights().stream())
			.collect(Collectors.toSet());

		return context.notifyContainerAboutLogin(
			() -> DefaultEntityHelper.SORMAS_TO_SORMAS_USER_NAME,
			userRights.stream().map(Enum::name).collect(Collectors.toSet()));
	}
}
