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

import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.user.UserRight;

/**
 * See https://www.baeldung.com/java-ee-8-security
 * IdentityStore: https://developer.ibm.com/tutorials/j-javaee8-security-api-3/
 */
@ApplicationScoped
public class SormasIdentityStore implements IdentityStore {

	public CredentialValidationResult validate(UsernamePasswordCredential usernamePasswordCredential) {

		Set<UserRight> userRights = FacadeProvider.getUserFacade()
			.getValidLoginUserRights(usernamePasswordCredential.getCaller(), usernamePasswordCredential.getPasswordAsString());

		if (userRights != null && !userRights.isEmpty()) {
			return new CredentialValidationResult(
				usernamePasswordCredential.getCaller(),
				userRights.stream().map(Enum::name).collect(Collectors.toSet()));
		}

		return CredentialValidationResult.INVALID_RESULT;
	}
}
