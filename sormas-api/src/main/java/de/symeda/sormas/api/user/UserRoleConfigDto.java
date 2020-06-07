/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 *******************************************************************************/
package de.symeda.sormas.api.user;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.utils.DataHelper;

public class UserRoleConfigDto extends EntityDto {

	private static final long serialVersionUID = -547459523041494446L;

	public static final String I18N_PREFIX = "UserRole";

	public static final String USER_ROLE = "userRole";
	public static final String USER_RIGHTS = "userRights";

	private UserRole userRole;
	private Set<UserRight> userRights;

	public static UserRoleConfigDto build(UserRole userRole, UserRight... userRights) {

		UserRoleConfigDto dto = new UserRoleConfigDto();
		dto.setUuid(DataHelper.createUuid());
		dto.setUserRole(userRole);
		Set<UserRight> userRightsSet = new HashSet<UserRight>();
		userRightsSet.addAll(Arrays.asList(userRights));
		dto.setUserRights(userRightsSet);
		return dto;
	}

	public UserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

	public Set<UserRight> getUserRights() {
		return userRights;
	}

	public void setUserRights(Set<UserRight> userRights) {
		this.userRights = userRights;
	}
}
