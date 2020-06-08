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

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.user.UserRoleConfigDto;
import de.symeda.sormas.api.user.UserRoleConfigFacade;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "UserRoleConfigFacade")
public class UserRoleConfigFacadeEjb implements UserRoleConfigFacade {

	@EJB
	private UserRoleConfigService userRoleConfigService;
	@EJB
	private UserService userService;

	@Override
	public List<UserRoleConfigDto> getAllAfter(Date since) {
		return userRoleConfigService.getAllAfter(since, null).stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	@Override
	public List<UserRoleConfigDto> getAll() {
		return userRoleConfigService.getAll().stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	@Override
	public List<String> getAllUuids() {

		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		return userRoleConfigService.getAllUuids();
	}

	@Override
	public List<String> getDeletedUuids(Date since) {
		return userRoleConfigService.getDeletedUuids(since);
	}

	@Override
	public UserRoleConfigDto getByUuid(String uuid) {
		return toDto(userRoleConfigService.getByUuid(uuid));
	}

	@Override
	public UserRoleConfigDto saveUserRoleConfig(UserRoleConfigDto dto) {

		UserRoleConfig entity = fromDto(dto);
		userRoleConfigService.ensurePersisted(entity);
		return toDto(entity);
	}

	@Override
	public void deleteUserRoleConfig(UserRoleConfigDto dto) {

		UserRoleConfig entity = userRoleConfigService.getByUuid(dto.getUuid());
		userRoleConfigService.delete(entity);
	}

	@Override
	public Set<UserRight> getEffectiveUserRights(UserRole... userRoles) {

		Set<UserRight> userRights = EnumSet.noneOf(UserRight.class);

		for (UserRole userRole : userRoles) {
			UserRoleConfig userRoleConfig = userRoleConfigService.getByUserRole(userRole);
			if (userRoleConfig != null) {
				userRights.addAll(userRoleConfig.getUserRights());
			} else {
				userRights.addAll(userRole.getDefaultUserRights());
			}
		}

		return userRights;
	}

	public UserRoleConfig fromDto(UserRoleConfigDto source) {

		if (source == null) {
			return null;
		}

		UserRoleConfig target = userRoleConfigService.getByUuid(source.getUuid());
		if (target == null) {
			target = new UserRoleConfig();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);

		target.setUserRole(source.getUserRole());
		target.setUserRights(new HashSet<UserRight>(source.getUserRights()));

		return target;
	}

	public static UserRoleConfigDto toDto(UserRoleConfig source) {

		if (source == null) {
			return null;
		}

		UserRoleConfigDto target = new UserRoleConfigDto();
		DtoHelper.fillDto(target, source);

		target.setUserRole(source.getUserRole());
		target.setUserRights(new HashSet<UserRight>(source.getUserRights()));

		return target;
	}

	@LocalBean
	@Stateless
	public static class UserRoleConfigFacadeEjbLocal extends UserRoleConfigFacadeEjb {

	}
}
