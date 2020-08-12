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

import com.nimbusds.jose.Algorithm;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.user.UserDto;
import fish.payara.security.openid.OpenIdCredential;
import fish.payara.security.openid.controller.TokenController;
import fish.payara.security.openid.controller.UserInfoController;
import fish.payara.security.openid.domain.AccessTokenImpl;
import fish.payara.security.openid.domain.IdentityTokenImpl;
import fish.payara.security.openid.domain.OpenIdConfiguration;
import fish.payara.security.openid.domain.OpenIdContextImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Identity store validates the identity token & access token and returns the validation result with the caller name and groups.
 *
 * Implementation based on {@link fish.payara.security.openid.OpenIdIdentityStore}, but updated to read the user roles from the Sormas System.
 *
 * @author Alex Vidrean
 * @see fish.payara.security.openid.OpenIdIdentityStore
 */
@ApplicationScoped
public class SormasOpenIdIdentityStore implements IdentityStore {

	@Inject
	private OpenIdContextImpl context;

	@Inject
	private TokenController tokenController;

	@Inject
	private UserInfoController userInfoController;

	public CredentialValidationResult validate(OpenIdCredential credential) {
		HttpMessageContext httpContext = credential.getHttpContext();
		OpenIdConfiguration configuration = credential.getConfiguration();
		IdentityTokenImpl idToken = (IdentityTokenImpl) credential.getIdentityToken();

		Algorithm idTokenAlgorithm = idToken.getTokenJWT().getHeader().getAlgorithm();

		Map<String, Object> idTokenClaims;
		if (isNull(context.getIdentityToken())) {
			idTokenClaims = tokenController.validateIdToken(idToken, httpContext, configuration);
		} else {
			// If an ID Token is returned as a result of a token refresh request
			idTokenClaims = tokenController.validateRefreshedIdToken(context.getIdentityToken(), idToken, httpContext, configuration);
		}
		if (idToken.isEncrypted()) {
			idToken.setClaims(idTokenClaims);
		}
		context.setIdentityToken(idToken);

		AccessTokenImpl accessToken = (AccessTokenImpl) credential.getAccessToken();
		if (nonNull(accessToken)) {
			Map<String, Object> accesTokenClaims =
				tokenController.validateAccessToken(accessToken, idTokenAlgorithm, context.getIdentityToken().getClaims(), configuration);
			if (accessToken.isEncrypted()) {
				accessToken.setClaims(accesTokenClaims);
			}
			context.setAccessToken(accessToken);
			JsonObject userInfo = userInfoController.getUserInfo(configuration, accessToken);
			context.setClaims(userInfo);
		}

		context.setCallerName(getCallerName(configuration));
		Set<String> groups = getCallerGroups(context.getCallerName());
		//TODO: check if the context.setCallerGroups works for newer versions of Payara
		context.setCallerGroups(StringUtils.join(groups, ","));

		return new CredentialValidationResult(context.getCallerName(), groups);
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

	private Set<String> getCallerGroups(String username) {
		UserDto user = FacadeProvider.getUserFacade().getByUserName(username);

		if (user != null && CollectionUtils.isNotEmpty(user.getUserRoles())) {
			return user.getUserRoles().stream().map(Enum::name).collect(Collectors.toSet());
		}

		return Collections.emptySet();
	}

}
