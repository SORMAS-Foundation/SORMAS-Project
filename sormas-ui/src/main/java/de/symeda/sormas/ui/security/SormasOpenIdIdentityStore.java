/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jwt.JWTClaimsSet;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.user.UserDto;
import fish.payara.security.openid.OpenIdCredential;
import fish.payara.security.openid.api.OpenIdConstant;
import fish.payara.security.openid.controller.TokenController;
import fish.payara.security.openid.domain.AccessTokenImpl;
import fish.payara.security.openid.domain.IdentityTokenImpl;
import fish.payara.security.openid.domain.OpenIdConfiguration;
import fish.payara.security.openid.domain.OpenIdContextImpl;

/**
 * Identity store validates the identity token & access token and returns the validation result with the caller name and groups.
 * Implementation based on {@link fish.payara.security.openid.OpenIdIdentityStore}, but updated to read the user roles from the Sormas System.
 *
 * @author Alex Vidrean
 * @see fish.payara.security.openid.OpenIdIdentityStore
 */
@ApplicationScoped
public class SormasOpenIdIdentityStore implements IdentityStore {

	private final static Logger logger = LoggerFactory.getLogger(SormasOpenIdIdentityStore.class);

	@Inject
	private OpenIdContextImpl context;

	@Inject
	private TokenController tokenController;

	@Inject
	private OpenIdConfiguration configuration;

	public CredentialValidationResult validate(OpenIdCredential credential) throws InvocationTargetException, IllegalAccessException {
		HttpMessageContext httpContext = credential.getHttpContext();
		IdentityTokenImpl idToken = (IdentityTokenImpl) credential.getIdentityToken();

		Algorithm idTokenAlgorithm = idToken.getTokenJWT().getHeader().getAlgorithm();

		JWTClaimsSet idTokenClaims;
		if (isNull(context.getIdentityToken())) {
			idTokenClaims = tokenController.validateIdToken(idToken, httpContext);
		} else {
			// If an ID Token is returned as a result of a token refresh request
			idTokenClaims = tokenController.validateRefreshedIdToken(context.getIdentityToken(), idToken);
		}
		if (idToken.isEncrypted()) {
			idToken.withClaims(idTokenClaims);
		}
		context.setIdentityToken(idToken);

		AccessTokenImpl accessToken = (AccessTokenImpl) credential.getAccessToken();
		if (nonNull(accessToken)) {
			Map<String, Object> accessTokenClaims =
				tokenController.validateAccessToken(accessToken, idTokenAlgorithm, context.getIdentityToken().getClaims());
			if (accessToken.isEncrypted()) {
				accessToken.setClaims(accessTokenClaims);
			}

			context.setAccessToken(accessToken);
			//TODO: remove  this when tested it works
//	should now be automatically taken from the configuration or access token
//			JsonObject userInfo = userInfoController.getUserInfo(openIdConfiguration, accessToken);
//			context.setClaims(userInfo);
		}

		context.setCallerName(getCallerName(configuration));
		UserDto user = FacadeProvider.getUserFacade().getByUserName(context.getCallerName());
		Set<String> groups = getCallerGroups(user);
		setGroups(groups);
		updateLocale(accessToken, user);

		return new CredentialValidationResult(getCallerName(configuration),	groups);
	}

	private void updateLocale(AccessTokenImpl accessToken, UserDto userDto) {
		String locale = (String) accessToken.getClaims().get(OpenIdConstant.LOCALE);

		if (userDto != null && userDto.getLanguage() != null && locale != null && !userDto.getLanguage().getLocale().getLanguage().equals(locale)) {
			UserDto user = FacadeProvider.getUserFacade().getByUserName(accessToken.getClaim(OpenIdConstant.PREFERRED_USERNAME).toString());
			user.setLanguage(Language.fromLocaleString(locale));
			FacadeProvider.getUserFacade().saveUser(user);
		}
	}

	private String getCallerName(OpenIdConfiguration configuration) {
		String callerNameClaim = configuration.getClaimsConfiguration().getCallerNameClaim();
		String callerName = context.getClaimsJson().getString(callerNameClaim, null);
		if (callerName == null) {
			callerName = (String) context.getIdentityToken().getClaim(callerNameClaim);
		}
		if (callerName == null) {
			callerName = (String) context.getAccessToken().getClaim(callerNameClaim);
		}
		if (callerName == null) {
			callerName = context.getSubject();
		}
		return callerName;
	}

	private Set<String> getCallerGroups(UserDto user) {
		if (user != null && CollectionUtils.isNotEmpty(user.getUserRoles())) {
			return user.getUserRoles().stream().map(Enum::name).collect(Collectors.toSet());
		}

		return Collections.emptySet();
	}

	/**
	 * Due to different API for the {@link OpenIdContextImpl#setCallerGroups} method, we have to check first the parameter type before setting it.
	 *
	 * @param groups user groups
	 */
	private void setGroups(Set<String> groups) throws InvocationTargetException, IllegalAccessException {
		//TODO: remove unneeded reflection workaround
		for (Method method : context.getClass().getMethods()) {
			if (StringUtils.equals(method.getName(), "setCallerGroups")) {
				Parameter[] parameters = method.getParameters();
				if (ArrayUtils.isEmpty(parameters)) {
					logger.debug("Unexpected number of parameters. Expected 1, but got 0.");
					return;
				}

				if (parameters.length > 1) {
					logger.debug("Unexpected number of parameters. Expected 1, but got {}", parameters.length);
					return;
				}

				if (parameters[0].getType().isAssignableFrom(String.class)) {
					method.invoke(context, StringUtils.join(groups, ","));
				} else if (parameters[0].getType().isAssignableFrom(Set.class)) {
					method.invoke(context, groups);
				} else {
					logger.warn("Unexpected parameter type. Expected Set or String, but got {}", parameters[0].getType().getCanonicalName());
					return;
				}
			}
		}
	}
}
