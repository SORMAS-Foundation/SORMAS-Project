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
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;

import org.apache.commons.collections.CollectionUtils;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.user.UserRight;

/**
 * Identity store used to obtain a user's groups from the Sormas DB instead of using the ones provided by the keycloak access token.
 * Allows managing of roles in the Sormas application.
 */
@ApplicationScoped
public class SormasIdentityStore implements IdentityStore {

	// TODO build salted credential cache for the credentials of the last 2 minutes

	public CredentialValidationResult validate(UsernamePasswordCredential usernamePasswordCredential) {

		Set<UserRight> userRights = FacadeProvider.getUserFacade()
			.getValidLoginUserRights(usernamePasswordCredential.getCaller(), usernamePasswordCredential.getPasswordAsString());

		if (CollectionUtils.isNotEmpty(userRights) && userRights.contains(UserRight.SORMAS_REST)) {
			return new CredentialValidationResult(
				usernamePasswordCredential.getCaller(),
				userRights.stream().map(Enum::name).collect(Collectors.toSet()));
		}

		return CredentialValidationResult.INVALID_RESULT;
	}
}
