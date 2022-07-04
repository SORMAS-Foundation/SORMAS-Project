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

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRoleDto;
import de.symeda.sormas.api.user.UserRoleFacade;
import de.symeda.sormas.api.user.UserRoleReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
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
	public UserRoleReferenceDto getReferenceByUuid(String uuid) {
		return toReferenceDto(userRoleService.getByUuid(uuid));
	}

	@Override
	public UserRoleDto saveUserRole(@Valid UserRoleDto dto) {

		validate(dto);

		UserRole entity = fromDto(dto, true);

		userRoleService.ensurePersisted(entity);
		return toDto(entity);
	}

	private void validate(UserRoleDto source) {
		if (StringUtils.isBlank(source.getCaption())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.specifyCaption));
		}
		if (Objects.isNull(source.getJurisdictionLevel())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.specifyJurisdictionLevel));
		}
		if (!userRoleService.isCaptionUnique(source.getUuid(), source.getCaption())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.captionNotUnique));
		}

		User currentUser = userService.getCurrentUser();
		if (currentUser.getUserRoles().stream().anyMatch(r -> DataHelper.isSame(r, source))) {
			Set<UserRole> currentUserRoles = currentUser.getUserRoles();
			Set<UserRight> currentUserRights = UserRole.getUserRights(currentUserRoles);
			Set<UserRight> newUserRights = UserRoleDto
				// replace old user role with the one being edited
				.getUserRights(currentUserRoles.stream().map(r -> DataHelper.isSame(r, source) ? source : toDto(r)).collect(Collectors.toList()));

			if (currentUserRights.contains(UserRight.USER_ROLE_EDIT) && !newUserRights.contains(UserRight.USER_ROLE_EDIT)) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.removeUserRightEditRightFromOwnUser));
			} else if (currentUserRights.contains(UserRight.USER_EDIT) && !newUserRights.contains(UserRight.USER_EDIT)) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.removeUserEditRightFromOwnUser));
			}
		}
	}

	@Override
	public void deleteUserRole(UserRoleReferenceDto dto) {

		UserRole entity = userRoleService.getByUuid(dto.getUuid());
		userRoleService.deletePermanent(entity);
	}

	@Override
	public boolean hasUserRight(Collection<UserRoleDto> userRoles, UserRight userRight) {

		return hasAnyUserRight(userRoles, Collections.singleton(userRight));
	}

	@Override
	public boolean hasAnyUserRight(Collection<UserRoleDto> userRoles, Collection<UserRight> userRights) {

		for (UserRoleDto userRole : userRoles) {
			for (UserRight userRight : userRights) {
				if (userRole.getUserRights().contains(userRight)) {
					return true;
				}
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
		target.setHasOptionalHealthFacility(source.getHasOptionalHealthFacility());
		target.setHasAssociatedDistrictUser(source.getHasAssociatedDistrictUser());
		target.setPortHealthUser(source.isPortHealthUser());
		target.setEmailNotificationTypes(source.getEmailNotificationTypes());
		target.setSmsNotificationTypes(source.getSmsNotificationTypes());
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
		target.setHasAssociatedDistrictUser(source.hasAssociatedDistrictUser());
		target.setPortHealthUser(source.isPortHealthUser());
		target.setEmailNotificationTypes(new HashSet<>(source.getEmailNotificationTypes()));
		target.setSmsNotificationTypes(new HashSet<>(source.getSmsNotificationTypes()));
		target.setJurisdictionLevel(source.getJurisdictionLevel());

		return target;
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
	public boolean hasAssociatedDistrictUser(Set<UserRoleDto> userRoles) {
		return userRoles.stream().filter(UserRoleDto::getHasAssociatedDistrictUser).findFirst().orElse(null) != null;
	}

	@Override
	public boolean hasOptionalHealthFacility(Set<UserRoleDto> userRoles) {
		return userRoles.stream().filter(UserRoleDto::getHasOptionalHealthFacility).findFirst().orElse(null) != null;
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
	public UserRoleReferenceDto getReferenceById(long id) {
		return toReferenceDto(userRoleService.getById(id));
	}

	@Override
	public Map<UserRoleDto, Set<UserRight>> getUserRoleRights() {
		HashMap map = new HashMap<>();

		getAll().forEach(c -> map.put(c, c.getUserRights()));

		return map;
	}

	@Override
	public Set<UserRoleDto> getDefaultUserRolesAsDto() {
		return Stream.of(DefaultUserRole.values()).map(r -> {
			UserRoleDto role = UserRoleDto.build();
			r.toUserRole(role);
			return role;
		}).collect(Collectors.toSet());
	}

	@Override
	public Collection<UserRoleDto> getByReferences(Set<UserRoleReferenceDto> references) {
		return userRoleService.getByUuids(references.stream().map(UserRoleReferenceDto::getUuid).collect(Collectors.toList()))
			.stream()
			.map(UserRoleFacadeEjb::toDto)
			.collect(Collectors.toList());
	}

	@LocalBean
	@Stateless
	public static class UserRoleFacadeEjbLocal extends UserRoleFacadeEjb {

		public UserRoleFacadeEjbLocal() {
		}
	}
}
