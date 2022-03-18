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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.Valid;

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

	//Assumption: UserRoleConfigs are not changed during runtime
	private Map<UserRole, Set<UserRight>> userRoleRightsCache;
	private Map<UserRight, Set<UserRole>> userRightRolesCache;

	@Override
	public List<UserRoleConfigDto> getAllAfter(Date since) {
		return userRoleConfigService.getAllAfter(since).stream().map(c -> toDto(c)).collect(Collectors.toList());
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
	public UserRoleConfigDto saveUserRoleConfig(@Valid UserRoleConfigDto dto) {

		UserRoleConfig entity = fromDto(dto, true);
		userRoleConfigService.ensurePersisted(entity);
		resetUserRoleRightsCache();
		return toDto(entity);
	}

	@Override
	public void deleteUserRoleConfig(UserRoleConfigDto dto) {

		UserRoleConfig entity = userRoleConfigService.getByUuid(dto.getUuid());
		userRoleConfigService.deletePermanent(entity);
		resetUserRoleRightsCache();
	}

	@Override
	public Set<UserRight> getEffectiveUserRights(UserRole... userRoles) {
		return getEffectiveUserRights(Arrays.asList(userRoles));
	}

	@Override
	public Set<UserRight> getEffectiveUserRights(Collection<UserRole> userRoles) {

		Map<UserRole, Set<UserRight>> userRoleRights = getUserRoleRights();

		Set<UserRight> userRights = EnumSet.noneOf(UserRight.class);
		for (UserRole userRole : userRoles) {
			userRights.addAll(userRoleRights.get(userRole));
		}

		return userRights;
	}

	@Override
	public Set<UserRole> getEffectiveUserRoles(UserRight... userRights) {
		return getEffectiveUserRoles(Arrays.asList(userRights));
	}

	@Override
	public boolean hasUserRight(Collection<UserRole> userRoles, UserRight userRight) {
		Map<UserRole, Set<UserRight>> userRoleRights = getUserRoleRights();
		for (UserRole userRole : userRoles) {
			if (userRoleRights.get(userRole).contains(userRight))
				return true;
		}
		return false;
	}

	@Override
	public boolean hasAnyUserRight(Collection<UserRole> userRoles, Collection<UserRight> userRights) {
		Map<UserRole, Set<UserRight>> userRoleRights = getUserRoleRights();
		for (UserRole userRole : userRoles) {
			for (UserRight userRight : userRights) {
				if (userRoleRights.get(userRole).contains(userRight))
					return true;
			}
		}
		return false;
	}

	/**
	 * TODO #4461 may no longer be needed, because it should be replaced by joining the user rights of the user's user roles
	 */
	@Override
	public Set<UserRole> getEffectiveUserRoles(Collection<UserRight> userRights) {

		Map<UserRight, Set<UserRole>> userRightRoles = getUserRightRoles();

		Set<UserRole> userRoles = EnumSet.noneOf(UserRole.class);
		for (UserRight userRight : userRights) {
			userRoles.addAll(userRightRoles.get(userRight));
		}

		return userRoles;
	}

	public void resetUserRoleRightsCache() {
		userRoleRightsCache = null;
		userRightRolesCache = null;
	}

	@Override
	public Map<UserRole, Set<UserRight>> getUserRoleRights() {

		if (userRoleRightsCache == null) {
			userRoleRightsCache = buildRoleRightsMap();
		}

		return userRoleRightsCache;
	}

	private Map<UserRight, Set<UserRole>> getUserRightRoles() {

		if (userRightRolesCache == null) {
			userRightRolesCache = buildRightRolesMap();
		}

		return userRightRolesCache;
	}

	public UserRoleConfig fromDto(UserRoleConfigDto source, boolean checkChangeDate) {

		if (source == null) {
			return null;
		}

		UserRoleConfig target =
			DtoHelper.fillOrBuildEntity(source, userRoleConfigService.getByUuid(source.getUuid()), UserRoleConfig::new, checkChangeDate);

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

	@Override
	public Set<UserRole> getEnabledUserRoles() {

		Set<UserRole> userRolesList = Arrays.stream(UserRole.values()).collect(Collectors.toSet());

		List<UserRoleConfig> userRoleConfigList = userRoleConfigService.getAll();

		for (UserRoleConfig userRoleConfig : userRoleConfigList) {
			if (!userRoleConfig.isEnabled()) {
				userRolesList.remove(userRoleConfig.getUserRole());
			}
		}
		return userRolesList;
	}

	public Map<UserRole, Set<UserRight>> buildRoleRightsMap() {
		Map<UserRole, Set<UserRight>> map = new EnumMap<>(UserRole.class);

		userRoleConfigService.getAll().forEach(c -> map.put(c.getUserRole(), c.getUserRights()));

		//default values
		Arrays.stream(UserRole.values()).forEach(r -> map.computeIfAbsent(r, UserRole::getDefaultUserRights));

		//enum sets
		map.replaceAll((k, v) -> {
			if (v.isEmpty()) {
				return EnumSet.noneOf(UserRight.class);
			} else {
				return EnumSet.copyOf(v);
			}
		});

		return map;
	}

	public Map<UserRight, Set<UserRole>> buildRightRolesMap() {

		Map<UserRole, Set<UserRight>> userRoleRights = getUserRoleRights();

		Map<UserRight, Set<UserRole>> map = new EnumMap<>(UserRight.class);

		userRoleRights.forEach((role, rights) -> rights.forEach(right -> {
			if (!map.containsKey(right))
				map.put(right, EnumSet.noneOf(UserRole.class));
			map.get(right).add(role);
		}));

		return map;
	}

	@LocalBean
	@Stateless
	public static class UserRoleConfigFacadeEjbLocal extends UserRoleConfigFacadeEjb {

	}
}
