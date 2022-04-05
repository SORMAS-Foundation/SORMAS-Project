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
package de.symeda.sormas.backend.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.Valid;

import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.api.user.UserRoleFacade;
import de.symeda.sormas.api.user.UserRoleReferenceDto;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "UserRoleFacade")
public class UserRoleFacadeEjb implements UserRoleFacade {

	@EJB
	private UserRoleService userRoleService;
	@EJB
	private UserService userService;

	@Override
	public List<UserRoleDto> getAllAfter(Date since) {
		return userRoleService.getAllAfter(since).stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	@Override
	public List<UserRoleDto> getAll() {
		return userRoleService.getAll().stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	@Override
	public List<String> getAllUuids() {

		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		return userRoleService.getAllUuids();
	}

	@Override
	public List<String> getDeletedUuids(Date since) {
		return userRoleService.getDeletedUuids(since);
	}

	@Override
	public UserRoleDto getByUuid(String uuid) {
		return toDto(userRoleService.getByUuid(uuid));
	}

	@Override
	public UserRoleDto saveUserRole(@Valid UserRoleDto dto) {

		UserRole entity = fromDto(dto, true);
		userRoleService.ensurePersisted(entity);
		return toDto(entity);
	}

	@Override
	public void deleteUserRole(UserRoleDto dto) {

		UserRole entity = userRoleService.getByUuid(dto.getUuid());
		userRoleService.deletePermanent(entity);
	}

	@Override
	public boolean hasUserRight(Collection<UserRoleDto> userRoles, UserRight userRight) {
		for (UserRoleDto userRole : userRoles) {
			if (userRole.getUserRights().contains(userRight))
				return true;
		}
		return false;
	}

	@Override
	public boolean hasAnyUserRight(Collection<UserRoleDto> userRoles, Collection<UserRight> userRights) {
		for (UserRoleDto userRole : userRoles) {
			for (UserRight userRight : userRights) {
				if (userRole.getUserRights().contains(userRight))
					return true;
			}
		}
		return false;
	}

	public UserRole fromDto(UserRoleDto source, boolean checkChangeDate) {

		if (source == null) {
			return null;
		}

		UserRole target = DtoHelper.fillOrBuildEntity(source, userRoleService.getByUuid(source.getUuid()), UserRole::new, checkChangeDate);

		Set<UserRight> userRights = Optional.of(target).map(UserRole::getUserRights).orElseGet(HashSet::new);
		target.setUserRights(userRights);
		userRights.clear();
		userRights.addAll(source.getUserRights());
		target.setEnabled(source.isEnabled());
		target.setCaption(source.getCaption());
		target.setDescription(source.getDescription());
		target.setHasOptionalHealthFacility(source.hasOptionalHealthFacility());
		target.setHasAssociatedOfficer(source.hasAssociatedOfficer());
		target.setPortHealthUser(source.isPortHealthUser());
		target.setEmailNotifications(source.getEmailNotifications());
		target.setSmsNotifications(source.getSmsNotifications());
		target.setJurisdictionLevel(source.getJurisdictionLevel());

		return target;
	}

	public static UserRoleDto toDto(UserRole source) {

		if (source == null) {
			return null;
		}

		UserRoleDto target = new UserRoleDto();
		DtoHelper.fillDto(target, source);

		target.setUserRights(new HashSet<>(source.getUserRights()));
		target.setEnabled(source.isEnabled());
		target.setCaption(source.getCaption());
		target.setDescription(source.getDescription());
		target.setHasOptionalHealthFacility(source.hasOptionalHealthFacility());
		target.setHasAssociatedOfficer(source.hasAssociatedOfficer());
		target.setPortHealthUser(source.isPortHealthUser());
		target.setEmailNotifications(new ArrayList<>(source.getEmailNotifications()));
		target.setSmsNotifications(new ArrayList<>(source.getSmsNotifications()));
		target.setJurisdictionLevel(source.getJurisdictionLevel());

		return target;
	}

	@Override
	public Set<UserRoleDto> getEnabledUserRoles() {

		Set<UserRoleDto> userRoles = getAll().stream().collect(Collectors.toSet());

		for (UserRoleDto userRole : userRoles) {
			if (!userRole.isEnabled()) {
				userRoles.remove(userRole);
			}
		}
		return userRoles;
	}

	@Override
	public Set<UserRoleReferenceDto> getAllAsReference() {
		List<UserRoleDto> all = getAll();
		return all != null ? all.stream().map(userRole -> userRole.toReference()).collect(Collectors.toSet()) : null;
	}

	@Override
	public List<UserRoleReferenceDto> getAllActiveAsReference() {
		return userRoleService.getAllActive().stream().map(UserRoleFacadeEjb::toReferenceDto).collect(Collectors.toList());
	}

	@Override
	public boolean isPortHealthUser(Set<UserRoleDto> userRoles) {
		return userRoles.stream().filter(userRoleDto -> userRoleDto.isPortHealthUser()).findFirst().orElse(null) != null;
	}

	@Override
	public boolean hasAssociatedOfficer(Set<UserRoleDto> userRoles) {
		return userRoles.stream().filter(userRoleDto -> userRoleDto.hasAssociatedOfficer()).findFirst().orElse(null) != null;
	}

	@Override
	public boolean hasOptionalHealthFacility(Set<UserRoleDto> userRoles) {
		return userRoles.stream().filter(userRoleDto -> userRoleDto.hasOptionalHealthFacility()).findFirst().orElse(null) != null;
	}

	public static UserRoleReferenceDto toReferenceDto(UserRole entity) {

		if (entity == null) {
			return null;
		}

		return new UserRoleReferenceDto(entity.getUuid(), entity.getCaption());
	}

	@Override
	public JurisdictionLevel getJurisdictionLevel(Collection<UserRoleDto> roles) {

		boolean laboratoryJurisdictionPresent = false;
		for (UserRoleDto role : roles) {
			final JurisdictionLevel jurisdictionLevel = role.getJurisdictionLevel();
			if (roles.size() == 1 || (jurisdictionLevel != JurisdictionLevel.NONE && jurisdictionLevel != JurisdictionLevel.LABORATORY)) {
				return jurisdictionLevel;
			} else if (jurisdictionLevel == JurisdictionLevel.LABORATORY) {
				laboratoryJurisdictionPresent = true;
			}
		}

		return laboratoryJurisdictionPresent ? JurisdictionLevel.LABORATORY : JurisdictionLevel.NONE;
	}

	@Override
	public void validateUserRoleCombination(Collection<UserRoleDto> roles) throws UserRoleDto.UserRoleValidationException {
		UserRoleDto previousCheckedRole = null;
		for (UserRoleDto userRole : roles) {
			final JurisdictionLevel jurisdictionLevel = userRole.getJurisdictionLevel();
			if (jurisdictionLevel != JurisdictionLevel.NONE && jurisdictionLevel != JurisdictionLevel.LABORATORY) {
				if (previousCheckedRole != null && previousCheckedRole.getJurisdictionLevel() != jurisdictionLevel) {
					throw new UserRoleDto.UserRoleValidationException(userRole, previousCheckedRole);
				} else {
					previousCheckedRole = userRole;
				}
			}
		}
	}

	@Override
	public UserRoleReferenceDto getUserRoleReferenceById(long id) {
		return toReferenceDto(userRoleService.getById(id));
	}

	@Override
	public Map<UserRoleDto, Set<UserRight>> getUserRoleRights() {
		HashMap map = new HashMap<>();

		getAll().forEach(c -> map.put(c, c.getUserRights()));

		return map;
	}

	@LocalBean
	@Stateless
	public static class UserRoleFacadeEjbLocal extends UserRoleFacadeEjb {

		public UserRoleFacadeEjbLocal() {
		}
	}
}
