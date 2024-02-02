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

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_BIG;
import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.NotificationType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity(name = UserRole.TABLE_NAME)
@EntityListeners(UserRole.UserRoleListener.class)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class UserRole extends AbstractDomainObject {

    private static final long serialVersionUID = 9053095630718041842L;

	public static final String TABLE_NAME = "userroles";
	public static final String TABLE_NAME_EMAIL_NOTIFICATIONS = "userroles_emailnotificationtypes";
	public static final String TABLE_NAME_SMS_NOTIFICATIONS = "userroles_smsnotificationtypes";

	public static final String USER_RIGHTS = "userRights";
	public static final String CAPTION = "caption";
	public static final String DESCRIPTION = "description";
	public static final String ENABLED = "enabled";
	public static final String JURISDICTION_LEVEL = "jurisdictionLevel";
	public static final String LINKED_DEFAULT_USER_ROLE = "linkedDefaultUserRole";
	public static final String EMAIL_NOTIFICATIONS = "emailNotificationTypes";
	public static final String SMS_NOTIFICATIONS = "smsNotificationTypes";
	public static final String RESTRICT_ACCESS_TO_ASSIGNED_ENTITIES = "restrictAccessToAssignedEntities";

	private Set<UserRight> userRights;
	private boolean enabled = true;
	private String caption;
	private String description;
	private boolean hasOptionalHealthFacility;
	private boolean hasAssociatedDistrictUser;
	private boolean portHealthUser;
	private DefaultUserRole linkedDefaultUserRole;
	private JurisdictionLevel jurisdictionLevel;
	private Set<NotificationType> emailNotificationTypes = Collections.emptySet();
	private Set<NotificationType> smsNotificationTypes = Collections.emptySet();
	private boolean restrictAccessToAssignedEntities;

	@ElementCollection(fetch = FetchType.EAGER)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name = "userroles_userrights",
		joinColumns = @JoinColumn(name = "userrole_id", referencedColumnName = UserRole.ID, nullable = false),
		uniqueConstraints = @UniqueConstraint(columnNames = {
			"userrole_id",
			"userright" }))
	@Column(name = "userright", nullable = false)
	public Set<UserRight> getUserRights() {
		return userRights;
	}

	public void setUserRights(Set<UserRight> userRights) {
		this.userRights = userRights;
	}

	@Column(nullable = false)
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Column(length = CHARACTER_LIMIT_DEFAULT)
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	@Column(length = CHARACTER_LIMIT_BIG)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column
	public boolean getHasOptionalHealthFacility() {
		return hasOptionalHealthFacility;
	}

	public void setHasOptionalHealthFacility(boolean hasOptionalHealthFacility) {
		this.hasOptionalHealthFacility = hasOptionalHealthFacility;
	}

	@Column
	public boolean getHasAssociatedDistrictUser() {
		return hasAssociatedDistrictUser;
	}

	public void setHasAssociatedDistrictUser(boolean hasAssociatedDistrictUser) {
		this.hasAssociatedDistrictUser = hasAssociatedDistrictUser;
	}

	@Column
	public boolean isPortHealthUser() {
		return portHealthUser;
	}

	public void setPortHealthUser(boolean portHealthUser) {
		this.portHealthUser = portHealthUser;
	}

	@Column
	public boolean isRestrictAccessToAssignedEntities() {
		return restrictAccessToAssignedEntities;
	}

	public void setRestrictAccessToAssignedEntities(boolean restrictAccessToAssignedEntities) {
		this.restrictAccessToAssignedEntities = restrictAccessToAssignedEntities;
	}

	@Enumerated(EnumType.STRING)
	public JurisdictionLevel getJurisdictionLevel() {
		return jurisdictionLevel;
	}

	public void setJurisdictionLevel(JurisdictionLevel jurisdictionLevel) {
		this.jurisdictionLevel = jurisdictionLevel;
	}

	@Enumerated(EnumType.STRING)
	public DefaultUserRole getLinkedDefaultUserRole() {
		return linkedDefaultUserRole;
	}

	public void setLinkedDefaultUserRole(DefaultUserRole linkedDefaultUserRole) {
		this.linkedDefaultUserRole = linkedDefaultUserRole;
	}

	@ElementCollection(fetch = FetchType.LAZY)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name = TABLE_NAME_EMAIL_NOTIFICATIONS,
		joinColumns = @JoinColumn(name = "userrole_id", referencedColumnName = UserRole.ID, nullable = false),
		uniqueConstraints = @UniqueConstraint(columnNames = {
			"userrole_id",
			"notificationtype" }))
	@Column(name = "notificationtype", nullable = false)
	public Set<NotificationType> getEmailNotificationTypes() {
		return emailNotificationTypes;
	}

	public void setEmailNotificationTypes(Set<NotificationType> emailNotifications) {
		this.emailNotificationTypes = emailNotifications;
	}

	@ElementCollection(fetch = FetchType.LAZY)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name = TABLE_NAME_SMS_NOTIFICATIONS,
		joinColumns = @JoinColumn(name = "userrole_id", referencedColumnName = UserRole.ID, nullable = false),
		uniqueConstraints = @UniqueConstraint(columnNames = {
			"userrole_id",
			"notificationtype" }))
	@Column(name = "notificationtype", nullable = false)
	public Set<NotificationType> getSmsNotificationTypes() {
		return smsNotificationTypes;
	}

	public void setSmsNotificationTypes(Set<NotificationType> smsNotifications) {
		this.smsNotificationTypes = smsNotifications;
	}

	public static JurisdictionLevel getJurisdictionLevel(Collection<UserRole> roles) {

		boolean laboratoryJurisdictionPresent = false;
		for (UserRole role : roles) {
			final JurisdictionLevel jurisdictionLevel = role.getJurisdictionLevel();
			if (roles.size() == 1 || (jurisdictionLevel != JurisdictionLevel.NONE && jurisdictionLevel != JurisdictionLevel.LABORATORY)) {
				return jurisdictionLevel;
			} else if (jurisdictionLevel == JurisdictionLevel.LABORATORY) {
				laboratoryJurisdictionPresent = true;
			}
		}

		return laboratoryJurisdictionPresent ? JurisdictionLevel.LABORATORY : JurisdictionLevel.NONE;
	}

	public static boolean isPortHealthUser(Collection<UserRole> userRoles) {

		return userRoles.stream().anyMatch(UserRole::isPortHealthUser);
	}

	public static Set<UserRight> getUserRights(Collection<UserRole> userRoles) {

		return userRoles.stream().flatMap(role -> role.getUserRights().stream()).collect(Collectors.toSet());
	}

	static class UserRoleListener {

		@PrePersist
		@PreUpdate
		private void beforeAnyUpdate(UserRole userRole) {
			UserCache.getInstance().flush();
		}
	}
}
