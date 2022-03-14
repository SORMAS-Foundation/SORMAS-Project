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
import javax.security.enterprise.credential.CallerOnlyCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;

import org.apache.commons.collections.CollectionUtils;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;

/**
 * @author Alex Vidrean
 * @since 07-Aug-20
 */
@ApplicationScoped
public class KeycloakIdentityStore implements IdentityStore {

	public CredentialValidationResult validate(CallerOnlyCredential credential) {

		UserDto user = FacadeProvider.getUserFacade().getByUserName(credential.getCaller());

		if (user != null) {
			Set<UserRight> userRights =
				FacadeProvider.getUserRoleConfigFacade().getEffectiveUserRights(user.getUserRoles().toArray(new UserRole[] {}));

			if (CollectionUtils.isNotEmpty(userRights) && userRights.contains(UserRight.SORMAS_REST)) {
				return new CredentialValidationResult(credential.getCaller(), userRights.stream().map(Enum::name).collect(Collectors.toSet()));
			}
		}

		return CredentialValidationResult.INVALID_RESULT;

	}
}
