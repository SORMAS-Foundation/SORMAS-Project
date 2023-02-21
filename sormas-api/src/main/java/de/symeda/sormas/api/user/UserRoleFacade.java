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

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.Remote;
import javax.validation.Valid;

import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface UserRoleFacade {

	JurisdictionLevel getJurisdictionLevel(Collection<UserRoleDto> roles);

	List<UserRoleDto> getAllAfter(Date date);

	List<UserRoleDto> getAll();

	List<UserRoleDto> getAllActive();

	List<String> getAllUuids();

	List<String> getDeletedUuids(Date date);

	UserRoleDto getByUuid(String uuid);

	UserRoleReferenceDto getReferenceByUuid(String uuid);

	UserRoleDto saveUserRole(@Valid UserRoleDto dto);

	void deleteUserRole(UserRoleReferenceDto dto);

	boolean hasUserRight(Collection<UserRoleDto> userRoles, UserRight userRight);

	boolean hasAnyUserRight(Collection<UserRoleDto> userRoles, Collection<UserRight> userRights);

	List<UserRoleReferenceDto> getAllAsReference();

	List<UserRoleReferenceDto> getAllActiveAsReference();

	boolean isPortHealthUser(Set<UserRoleDto> userRoles);

	boolean hasAssociatedDistrictUser(Set<UserRoleDto> userRoles);

	boolean hasOptionalHealthFacility(Set<UserRoleDto> userRoles);

	void validateUserRoleCombination(Collection<UserRoleDto> roles) throws UserRoleDto.UserRoleValidationException;

	UserRoleReferenceDto getReferenceById(long id);

	long count(UserRoleCriteria userRoleCriteria);

	List<UserRoleDto> getIndexList(UserRoleCriteria userRoleCriteria, int first, int max, List<SortProperty> sortProperties);

	String generateUserRolesDocument() throws IOException;

	Set<UserRoleDto> getDefaultUserRolesAsDto();

	Collection<UserRoleDto> getByReferences(Set<UserRoleReferenceDto> userRoles);
}
