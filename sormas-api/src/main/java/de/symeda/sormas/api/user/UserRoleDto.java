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

import java.beans.Transient;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.ValidationException;
@AuditedClass
public class UserRoleDto extends EntityDto {

	private static final long serialVersionUID = -547459523041494446L;

	public static final String I18N_PREFIX = "UserRole";

	public static final String USER_RIGHTS = "userRights";
	public static final String CAPTION = "caption";
	public static final String JURISDICTION_LEVEL = "jurisdictionLevel";
	public static final String DESCRIPTION = "description";
	public static final String ENABLED = "enabled";
	public static final String HAS_OPTIONAL_HEALTH_FACILITY = "hasOptionalHealthFacility";
	public static final String HAS_ASSOCIATED_DISTRICT_USER = "hasAssociatedDistrictUser";
	public static final String PORT_HEALTH_USER = "portHealthUser";
	public static final String NOTIFICATION_TYPES = "notificationTypes";
	public static final String LINKED_DEFAULT_USER_ROLE = "linkedDefaultUserRole";
	public static final String RESTRICT_ACCESS_TO_ASSIGNED_ENTITIES = "restrictAccessToAssignedEntities";

	private Set<UserRight> userRights;
	private boolean enabled = true;

	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String caption;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_BIG, message = Validations.textTooLong)
	private String description;
	private boolean hasOptionalHealthFacility = false;
	private boolean hasAssociatedDistrictUser = true;
	private boolean portHealthUser = false;
	private DefaultUserRole linkedDefaultUserRole;
	private JurisdictionLevel jurisdictionLevel;
	private Set<NotificationType> emailNotificationTypes = Collections.emptySet();
	private Set<NotificationType> smsNotificationTypes = Collections.emptySet();
	private boolean restrictAccessToAssignedEntities = false;

	public static UserRoleDto build(UserRight... userRights) {

		UserRoleDto dto = new UserRoleDto();
		dto.setUuid(DataHelper.createUuid());
		Set<UserRight> userRightsSet = new HashSet<>();
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

	public boolean getHasOptionalHealthFacility() {
		return hasOptionalHealthFacility;
	}

	public void setHasOptionalHealthFacility(boolean hasOptionalHealthFacility) {
		this.hasOptionalHealthFacility = hasOptionalHealthFacility;
	}

	public boolean getHasAssociatedDistrictUser() {
		return hasAssociatedDistrictUser;
	}

	public void setHasAssociatedDistrictUser(boolean hasAssociatedDistrictUser) {
		this.hasAssociatedDistrictUser = hasAssociatedDistrictUser;
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

	public Set<NotificationType> getEmailNotificationTypes() {
		return emailNotificationTypes;
	}

	public void setEmailNotificationTypes(Set<NotificationType> emailNotificationTypes) {
		this.emailNotificationTypes = emailNotificationTypes;
	}

	public Set<NotificationType> getSmsNotificationTypes() {
		return smsNotificationTypes;
	}

	public void setSmsNotificationTypes(Set<NotificationType> smsNotificationTypes) {
		this.smsNotificationTypes = smsNotificationTypes;
	}

	public UserRoleReferenceDto toReference() {
		return new UserRoleReferenceDto(getUuid(), getCaption());
	}

	public static Set<UserRight> getUserRights(Collection<UserRoleDto> userRoles) {

		return userRoles != null ? userRoles.stream().flatMap(role -> role.getUserRights().stream()).collect(Collectors.toSet()) : null;
	}

	public DefaultUserRole getLinkedDefaultUserRole() {
		return linkedDefaultUserRole;
	}

	public void setLinkedDefaultUserRole(DefaultUserRole linkedDefaultUserRole) {
		this.linkedDefaultUserRole = linkedDefaultUserRole;
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

	@JsonIgnore
	public String i18nPrefix() {
		return I18N_PREFIX;
	}

	@Transient
	public NotificationTypes getNotificationTypes() {
		return NotificationTypes.of(smsNotificationTypes, emailNotificationTypes);
	}

	@Transient
	public void setNotificationTypes(NotificationTypes notificationTypes) {
		this.smsNotificationTypes = notificationTypes.sms;
		this.emailNotificationTypes = notificationTypes.email;
	}

	public boolean isRestrictAccessToAssignedEntities() {
		return restrictAccessToAssignedEntities;
	}

	public void setRestrictAccessToAssignedEntities(boolean restrictAccessToAssignedEntities) {
		this.restrictAccessToAssignedEntities = restrictAccessToAssignedEntities;
	}

	@Override
	public String buildCaption() {
		return caption;
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

	public static class NotificationTypes {

		private final Set<NotificationType> sms;
		private final Set<NotificationType> email;

		private NotificationTypes(Set<NotificationType> sms, Set<NotificationType> email) {
			this.sms = sms;
			this.email = email;
		}

		public static NotificationTypes of(Set<NotificationType> sms, Set<NotificationType> email) {
			return new NotificationTypes(sms, email);
		}

		public Set<NotificationType> getSms() {
			return sms;
		}

		public Set<NotificationType> getEmail() {
			return email;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof NotificationTypes)) {
				return false;
			}

			NotificationTypes notificationTypes = (NotificationTypes) obj;

			return sms.equals(notificationTypes.sms) && email.equals(notificationTypes.email);
		}
	}
}
