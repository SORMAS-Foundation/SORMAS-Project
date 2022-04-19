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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.ValidationException;

public class UserRoleDto extends EntityDto {

	private static final long serialVersionUID = -547459523041494446L;

	public static final String I18N_PREFIX = "UserRoleDto";

	public static final String USER_RIGHTS = "userRights";
	public static final String CAPTION = "caption";
	public static final String DESCRIPTION = "description";
	public static final String HAS_OPTIONAL_HEALTH_FACILITY = "hasOptionalHealthFacility";
	public static final String HAS_ASSOCIATED_OFFICER = "hasAssociatedOfficer";
	public static final String PORT_HEALTH_USER = "portHealthUser";

	private Set<UserRight> userRights;
	private boolean enabled;
	private String caption;
	private String description;
	private boolean hasOptionalHealthFacility = true;
	private boolean hasAssociatedOfficer = true;
	private boolean portHealthUser = true;
	private JurisdictionLevel jurisdictionLevel;
	private List<NotificationType> emailNotifications;
	private List<NotificationType> smsNotifications;

	public static UserRoleDto build(UserRight... userRights) {

		UserRoleDto dto = new UserRoleDto();
		dto.setUuid(DataHelper.createUuid());
		Set<UserRight> userRightsSet = new HashSet<UserRight>();
		userRightsSet.addAll(Arrays.asList(userRights));
		dto.setUserRights(userRightsSet);
		return dto;
	}

	public Set<UserRight> getUserRights() {
		return userRights;
	}

	public void setUserRights(Set<UserRight> userRights) {
		this.userRights = userRights;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean hasOptionalHealthFacility() {
		return hasOptionalHealthFacility;
	}

	public void setHasOptionalHealthFacility(boolean hasOptionalHealthFacility) {
		this.hasOptionalHealthFacility = hasOptionalHealthFacility;
	}

	public boolean hasAssociatedOfficer() {
		return hasAssociatedOfficer;
	}

	public void setHasAssociatedOfficer(boolean hasAssociatedOfficer) {
		this.hasAssociatedOfficer = hasAssociatedOfficer;
	}

	public boolean isPortHealthUser() {
		return portHealthUser;
	}

	public void setPortHealthUser(boolean portHealthUser) {
		this.portHealthUser = portHealthUser;
	}

	public JurisdictionLevel getJurisdictionLevel() {
		return jurisdictionLevel;
	}

	public void setJurisdictionLevel(JurisdictionLevel jurisdictionLevel) {
		this.jurisdictionLevel = jurisdictionLevel;
	}

	public List<NotificationType> getEmailNotifications() {
		return emailNotifications;
	}

	public void setEmailNotifications(List<NotificationType> emailNotifications) {
		this.emailNotifications = emailNotifications;
	}

	public List<NotificationType> getSmsNotifications() {
		return smsNotifications;
	}

	public void setSmsNotifications(List<NotificationType> smsNotifications) {
		this.smsNotifications = smsNotifications;
	}

	public UserRoleReferenceDto toReference() {
		return new UserRoleReferenceDto(getUuid(), getCaption());
	}

	public static Set<UserRight> getUserRights(Collection<UserRoleDto> userRoles) {

		return userRoles != null ? userRoles.stream().flatMap(role -> role.getUserRights().stream()).collect(Collectors.toSet()) : null;
	}

	public static JurisdictionLevel getJurisdictionLevel(Collection<UserRoleDto> roles) {

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

	@SuppressWarnings("serial")
	public static class UserRoleValidationException extends ValidationException {

		private final UserRoleDto checkedUserRole;
		private final UserRoleDto forbiddenUserRole;

		public UserRoleValidationException(UserRoleDto checkedUserRole, UserRoleDto forbiddenUserRole) {
			super(
				checkedUserRole.getCaption() + " " + I18nProperties.getString(Strings.messageUserRoleCombination) + " "
					+ forbiddenUserRole.getCaption());
			this.checkedUserRole = checkedUserRole;
			this.forbiddenUserRole = forbiddenUserRole;
		}

		public UserRoleDto getCheckedUserRole() {
			return checkedUserRole;
		}

		public UserRoleDto getForbiddenUserRole() {
			return forbiddenUserRole;
		}
	}
}
