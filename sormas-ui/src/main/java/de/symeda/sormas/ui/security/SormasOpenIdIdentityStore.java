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
package de.symeda.sormas.ui.security;

import static javax.security.enterprise.identitystore.CredentialValidationResult.Status.VALID;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRoleDto;
import fish.payara.security.openid.OpenIdCredential;
import fish.payara.security.openid.OpenIdIdentityStore;
import fish.payara.security.openid.api.AccessToken;
import fish.payara.security.openid.api.OpenIdConstant;
import fish.payara.security.openid.domain.OpenIdContextImpl;

/**
 * Identity store validates the identity token & access token and returns the validation result with the caller name and groups.
 * Implementation based on {@link fish.payara.security.openid.OpenIdIdentityStore}, but updated to read the user roles from the Sormas
 * System.
 *
 * @author Alex Vidrean
 * @see fish.payara.security.openid.OpenIdIdentityStore
 */
@ApplicationScoped
public class SormasOpenIdIdentityStore implements IdentityStore {

	@Inject
	private OpenIdContextImpl context;

	@Inject
	private OpenIdIdentityStore openIdIdentityStore;

	public CredentialValidationResult validate(OpenIdCredential credential) throws InvocationTargetException, IllegalAccessException {
		CredentialValidationResult result = openIdIdentityStore.validate(credential);
		if (result.getStatus() == VALID) {
			UserDto user = FacadeProvider.getUserFacade().getByUserName(context.getCallerName());
			Set<String> groups = getCallerGroups(user);
			context.setCallerGroups(groups);
			updateLocale(context.getAccessToken(), user);
			return new CredentialValidationResult(context.getCallerName(), groups);
		}
		return result;
	}

	private void updateLocale(AccessToken accessToken, UserDto userDto) {
		Optional<String> localeClaim = accessToken.getJwtClaims().getStringClaim(OpenIdConstant.LOCALE);
		localeClaim.ifPresent(locale -> {
			if (userDto != null && userDto.getLanguage() != null && !userDto.getLanguage().getLocale().getLanguage().equals(locale)) {
				userDto.setLanguage(Language.fromLocaleString(localeClaim.get()));
				FacadeProvider.getUserFacade().saveUser(userDto);
			}
		});
	}

	private Set<String> getCallerGroups(UserDto user) {

		if (user != null) {
			Set<UserRight> userRights = UserRoleDto.getUserRights(FacadeProvider.getUserFacade().getUserRoles(user));

			return userRights != null ? userRights.stream().map(Enum::name).collect(Collectors.toSet()) : Collections.emptySet();
		}

		return Collections.emptySet();
	}
}
