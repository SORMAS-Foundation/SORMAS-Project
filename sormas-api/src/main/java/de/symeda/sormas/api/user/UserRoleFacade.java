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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Remote;
import javax.validation.Valid;

@Remote
public interface UserRoleFacade {

	JurisdictionLevel getJurisdictionLevel(Collection<UserRoleDto> roles);

	List<UserRoleDto> getAllAfter(Date date);

	List<UserRoleDto> getAll();

	List<String> getAllUuids();

	List<String> getDeletedUuids(Date date);

	UserRoleDto getByUuid(String uuid);

	UserRoleDto saveUserRole(@Valid UserRoleDto dto);

	void deleteUserRole(UserRoleDto dto);

	boolean hasUserRight(Collection<UserRoleDto> userRoles, UserRight userRight);

	boolean hasAnyUserRight(Collection<UserRoleDto> userRoles, Collection<UserRight> userRights);

	Set<UserRoleDto> getEnabledUserRoles();

	Set<UserRoleReferenceDto> getAllAsReference();

	List<UserRoleReferenceDto> getAllActiveAsReference();

	boolean isPortHealthUser(Set<UserRoleDto> userRoles);

	boolean hasAssociatedOfficer(Set<UserRoleDto> userRoles);

	boolean hasOptionalHealthFacility(Set<UserRoleDto> userRoles);

	void validateUserRoleCombination(Collection<UserRoleDto> roles) throws UserRoleDto.UserRoleValidationException;

	UserRoleReferenceDto getUserRoleReferenceById(long id);

	Map<UserRoleDto, Set<UserRight>> getUserRoleRights();
}
