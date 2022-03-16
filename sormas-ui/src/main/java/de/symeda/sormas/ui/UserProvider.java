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

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;

public class UserProvider {

	private static final List configurationUserRoles = Arrays.asList(
		UserRight.DOCUMENT_TEMPLATE_MANAGEMENT,
		UserRight.INFRASTRUCTURE_VIEW,
		UserRight.LINE_LISTING_CONFIGURE,
		UserRight.OUTBREAK_VIEW,
		UserRight.POPULATION_MANAGE);

	private UserDto user;
	private UserReferenceDto userReference;
	private Set<UserRight> userRights;
	private JurisdictionLevel jurisdictionLevel;

	public UserDto getUser() {

		if (user == null) {
			user = FacadeProvider.getUserFacade().getCurrentUser();
		}
		return user;
	}

	public Set<UserRight> getUserRights() {

		if (userRights == null) {
			userRights = FacadeProvider.getUserRoleConfigFacade().getEffectiveUserRights(getUser().getUserRoles());
		}
		return userRights;
	}

	public Set<UserRole> getUserRoles() {
		return getUser().getUserRoles();
	}

	public JurisdictionLevel getJurisdictionLevel() {
		if (jurisdictionLevel == null) {
			jurisdictionLevel = UserRole.getJurisdictionLevel(getUser().getUserRoles());
		}
		return jurisdictionLevel;
	}

	public boolean hasConfigurationAccess() {
		Set<UserRight> currentUserRights = getUserRights();
		return configurationUserRoles.stream().anyMatch(currentUserRights::contains);
	}

	public boolean hasUserRight(UserRight userRight) {
		return getUserRights().contains(userRight);
	}

	public boolean hasAllUserRights(UserRight... userRights) {
		return getUserRights().containsAll(Arrays.asList(userRights));
	}

	public boolean hasNationJurisdictionLevel() {
		return getJurisdictionLevel() == JurisdictionLevel.NATION;
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
		RegionReferenceDto userRegionReference = getCurrent().getUser().getRegion();
		return Objects.equals(userRegionReference, regionReference);
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

	public interface HasUserProvider {

		UserProvider getUserProvider();
	}
}
