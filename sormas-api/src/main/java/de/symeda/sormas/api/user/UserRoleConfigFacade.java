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

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.Remote;

@Remote
public interface UserRoleConfigFacade {

	List<UserRoleConfigDto> getAllAfter(Date date);

	List<UserRoleConfigDto> getAll();

	List<String> getAllUuids();

	List<String> getDeletedUuids(Date date);

	UserRoleConfigDto getByUuid(String uuid);

	UserRoleConfigDto saveUserRoleConfig(UserRoleConfigDto dto);

	void deleteUserRoleConfig(UserRoleConfigDto dto);

	/**
	 * Will fallback to default user rights for each role that has no configuration defined
	 */
	Set<UserRight> getEffectiveUserRights(UserRole... userRoles);
}
