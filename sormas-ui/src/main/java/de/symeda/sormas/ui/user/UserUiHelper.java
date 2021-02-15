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

package de.symeda.sormas.ui.user;

import java.util.Collections;
import java.util.Set;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.UserProvider;

public class UserUiHelper {

	public static Set<UserRole> getAssignableRoles(Set<UserRole> assignedUserRoles) {

		final Set<UserRole> assignedRoles = assignedUserRoles == null ? Collections.emptySet() : assignedUserRoles;

		Set<UserRole> allRoles = UserRole.getAssignableRoles(UserProvider.getCurrent().getUserRoles());

		if (!FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_SWITZERLAND)) {
			allRoles.remove(UserRole.BAG_USER);
		}

		Set<UserRole> enabledUserRoles = FacadeProvider.getUserRoleConfigFacade().getEnabledUserRoles();

		allRoles.removeIf(userRole -> !enabledUserRoles.contains(userRole) && !assignedRoles.contains(userRole));

		return allRoles;
	}
}
