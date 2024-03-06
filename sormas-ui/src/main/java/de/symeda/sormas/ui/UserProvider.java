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
package de.symeda.sormas.ui;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.vaadin.ui.UI;

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.PseudonymizableDataAccessLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRightGroup;
import de.symeda.sormas.api.user.UserRoleDto;

public class UserProvider {

	private static final List userUserRoles = Arrays.asList(UserRight.USER_VIEW, UserRight.USER_ROLE_VIEW);

	private UserDto user;
	private UserReferenceDto userReference;
	private Set<UserRoleDto> userRoles;
	private Set<UserRight> userRights;
	private JurisdictionLevel jurisdictionLevel;
	private Boolean portHealthUser;
	private Boolean hasAssociatedDistrictUser;
	private Boolean hasOptionalHealthFacility;

	public UserDto getUser() {

		if (user == null) {
			user = FacadeProvider.getUserFacade().getCurrentUser();
		}
		return user;
	}

	public Set<UserRight> getUserRights() {

		if (userRights == null) {
			userRights = UserRoleDto.getUserRights(getUserRoles());
		}
		return userRights;
	}

	public Set<UserRoleDto> getUserRoles() {
		if (userRoles == null) {
			userRoles = FacadeProvider.getUserFacade().getUserRoles(getUser());
		}
		return userRoles;
	}

	public JurisdictionLevel getJurisdictionLevel() {
		if (jurisdictionLevel == null) {
			jurisdictionLevel = getUser().getJurisdictionLevel();
		}
		return jurisdictionLevel;
	}

	public boolean hasConfigurationAccess() {
		Set<UserRight> currentUserRights = getUserRights();
		return UserRight.getUserRightsOfGroup(UserRightGroup.CONFIGURATION).stream().anyMatch(currentUserRights::contains)
			|| UserRight.getUserRightsOfGroup(UserRightGroup.INFRASTRUCTURE).stream().anyMatch(currentUserRights::contains);
	}

	public boolean hasUserAccess() {
		Set<UserRight> currentUserRights = getUserRights();
		return userUserRoles.stream().anyMatch(currentUserRights::contains);
	}

	public boolean hasUserRight(UserRight userRight) {
		return getUserRights().contains(userRight);
	}

	public boolean hasAllUserRights(UserRight... userRights) {
		return getUserRights().containsAll(Arrays.asList(userRights));
	}

	//TODO: refactor this to hasUserRightWithAllowedFlag
	public boolean hasUserRightWithEditAllowedFlag(boolean isEditAllowed, UserRight userRight) {
		return isEditAllowed && hasUserRight(userRight);
	}

	public boolean hasAllUserRightsWithEditAllowedFlag(boolean isEditAllowed, UserRight... userRights) {
		return isEditAllowed && getUserRights().containsAll(Arrays.asList(userRights));
	}

	public boolean hasUserRightWithEditPermissionType(EditPermissionType editPermissionType, UserRight userRight) {
		return (editPermissionType == null || editPermissionType == EditPermissionType.ALLOWED) && hasUserRight(userRight);
	}

	public boolean hasNationJurisdictionLevel() {
		return getJurisdictionLevel() == JurisdictionLevel.NATION;
	}

	public boolean isAdmin() {
		return (user.getUserRoles().stream().filter(i -> i.getCaption().contains(I18nProperties.getEnumCaption(DefaultUserRole.ADMIN))).count() == 1);
	}

	public boolean hasRegionJurisdictionLevel() {
		return getJurisdictionLevel() == JurisdictionLevel.REGION;
	}

	public boolean hasNoneJurisdictionLevel() {
		return getJurisdictionLevel() == JurisdictionLevel.NONE;
	}

	public boolean hasLaboratoryOrExternalLaboratoryJurisdictionLevel() {
		JurisdictionLevel jurisdictionLevel = getJurisdictionLevel();
		return jurisdictionLevel == JurisdictionLevel.LABORATORY || jurisdictionLevel == jurisdictionLevel.EXTERNAL_LABORATORY;
	}

	public boolean hasExternalLaboratoryJurisdictionLevel() {
		return getJurisdictionLevel() == jurisdictionLevel.EXTERNAL_LABORATORY;
	}

	public boolean hasRegion(RegionReferenceDto regionReference) {
		RegionReferenceDto userRegionReference = UiUtil.getUser().getRegion();
		return Objects.equals(userRegionReference, regionReference);
	}

	public PseudonymizableDataAccessLevel getPseudonymizableDataAccessLevel(boolean inJurisdiction) {

		boolean sensitiveData = inJurisdiction
			? getUserRights().contains(UserRight.SEE_SENSITIVE_DATA_IN_JURISDICTION)
			: getUserRights().contains(UserRight.SEE_SENSITIVE_DATA_OUTSIDE_JURISDICTION);
		boolean personalData = inJurisdiction
			? getUserRights().contains(UserRight.SEE_PERSONAL_DATA_IN_JURISDICTION)
			: getUserRights().contains(UserRight.SEE_PERSONAL_DATA_OUTSIDE_JURISDICTION);
		return sensitiveData && personalData
			? PseudonymizableDataAccessLevel.ALL
			: personalData
				? PseudonymizableDataAccessLevel.PERSONAL
				: sensitiveData ? PseudonymizableDataAccessLevel.SENSITIVE : PseudonymizableDataAccessLevel.NONE;
	}

	public UserReferenceDto getUserReference() {

		if (userReference == null) {
			userReference = getUser().toReference();
		}
		return userReference;
	}

	public String getUuid() {
		return getUser().getUuid();
	}

	public String getUserName() {
		return getUser().getName();
	}

	public boolean isCurrentUser(UserDto user) {
		return getUser().equals(user);
	}

	/**
	 * Gets the user to which the current UI belongs. This is automatically defined
	 * when processing requests to the server. In other cases, (e.g. from background
	 * threads), the current UI is not automatically defined.
	 *
	 * @see UI#getCurrent()
	 *
	 * @return the current user instance if available, otherwise <code>null</code>
	 */
	public static UserProvider getCurrent() {

		UI currentUI = UI.getCurrent();
		if (currentUI instanceof HasUserProvider) {
			return ((HasUserProvider) currentUI).getUserProvider();
		}
		return null;
	}

	public boolean isPortHealthUser() {
		if (portHealthUser == null) {
			portHealthUser = FacadeProvider.getUserRoleFacade().isPortHealthUser(getUserRoles());
		}
		return portHealthUser;
	}

	public boolean hasAssociatedDistrictUser() {
		if (hasAssociatedDistrictUser == null) {
			hasAssociatedDistrictUser = FacadeProvider.getUserRoleFacade().hasAssociatedDistrictUser(getUserRoles());
		}
		return hasAssociatedDistrictUser;
	}

	public boolean hasOptionalHealthFacility() {
		if (hasOptionalHealthFacility == null) {
			hasOptionalHealthFacility = FacadeProvider.getUserRoleFacade().hasOptionalHealthFacility(getUserRoles());
		}
		return hasOptionalHealthFacility;
	}

	public interface HasUserProvider {

		UserProvider getUserProvider();
	}
}
