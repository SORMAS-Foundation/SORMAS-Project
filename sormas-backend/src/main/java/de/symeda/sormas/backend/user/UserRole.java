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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.UniqueConstraint;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.NotificationType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity(name = UserRole.TABLE_NAME)
@Audited
public class UserRole extends AbstractDomainObject {

	private static final long serialVersionUID = 9053095630718041842L;

	public static final String TABLE_NAME = "userroles";
	public static final String TABLE_NAME_EMAIL_NOTIFICATIONS = "userroles_emailnotifications";
	public static final String TABLE_NAME_SMS_NOTIFICATIONS = "userroles_smsnotifications";

	public static final String USER_RIGHTS = "userRights";
	public static final String CAPTION = "caption";
	public static final String ENABLED = "enabled";
	public static final String EMAIL_NOTIFICATIONS = "emailNotifications";
	public static final String SMS_NOTIFICATIONS = "smsNotifications";

	private Set<UserRight> userRights;
	private boolean enabled = true;
	private String caption;
	private String description;
	private boolean hasOptionalHealthFacility;
	private boolean hasAssociatedOfficer;
	private boolean portHealthUser;
	private JurisdictionLevel jurisdictionLevel;
	private List<NotificationType> emailNotifications;
	private List<NotificationType> smsNotifications;

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
	public boolean hasOptionalHealthFacility() {
		return hasOptionalHealthFacility;
	}

	public void setHasOptionalHealthFacility(boolean hasOptionalHealthFacility) {
		this.hasOptionalHealthFacility = hasOptionalHealthFacility;
	}

	@Column
	public boolean hasAssociatedOfficer() {
		return hasAssociatedOfficer;
	}

	public void setHasAssociatedOfficer(boolean hasAssociatedOfficer) {
		this.hasAssociatedOfficer = hasAssociatedOfficer;
	}

	@Column
	public boolean isPortHealthUser() {
		return portHealthUser;
	}

	public void setPortHealthUser(boolean portHealthUser) {
		this.portHealthUser = portHealthUser;
	}

	@Enumerated(EnumType.STRING)
	public JurisdictionLevel getJurisdictionLevel() {
		return jurisdictionLevel;
	}

	public void setJurisdictionLevel(JurisdictionLevel jurisdictionLevel) {
		this.jurisdictionLevel = jurisdictionLevel;
	}

	@ElementCollection(fetch = FetchType.LAZY)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name = TABLE_NAME_EMAIL_NOTIFICATIONS,
		joinColumns = @JoinColumn(name = "userrole_id", referencedColumnName = UserRole.ID, nullable = false),
		uniqueConstraints = @UniqueConstraint(columnNames = {
			"userrole_id",
			"notificationtype" }))
	@Column(name = "notificationtype", nullable = false)
	public List<NotificationType> getEmailNotifications() {
		return emailNotifications;
	}

	public void setEmailNotifications(List<NotificationType> emailNotifications) {
		this.emailNotifications = emailNotifications;
	}

	@ElementCollection(fetch = FetchType.LAZY)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name = TABLE_NAME_SMS_NOTIFICATIONS,
		joinColumns = @JoinColumn(name = "userrole_id", referencedColumnName = UserRole.ID, nullable = false),
		uniqueConstraints = @UniqueConstraint(columnNames = {
			"userrole_id",
			"notificationtype" }))
	@Column(name = "notificationtype", nullable = false)
	public List<NotificationType> getSmsNotifications() {
		return smsNotifications;
	}

	public void setSmsNotifications(List<NotificationType> smsNotifications) {
		this.smsNotifications = smsNotifications;
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
}
