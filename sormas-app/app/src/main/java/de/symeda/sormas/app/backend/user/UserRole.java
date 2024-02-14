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
package de.symeda.sormas.app.backend.user;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.table.DatabaseTable;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

@Entity(name = UserRole.TABLE_NAME)
@DatabaseTable(tableName = UserRole.TABLE_NAME)
public class UserRole extends AbstractDomainObject {

	private static final long serialVersionUID = 9053095630718041842L;

	public static final String TABLE_NAME = "userRoles";
	public static final String I18N_PREFIX = "UserRole";

	public static final String USER_RIGHTS = "userRights";

	@Column(name = "userRights", length = 1024)
	private String userRightsJson;

	// initialized from userRightsJson
	private Set<UserRight> userRights;
	@Column
	private boolean enabled = true;
	@Column
	private String caption;
	@Column
	private String description;
	@Column
	private boolean hasOptionalHealthFacility;
	@Column
	private boolean hasAssociatedDistrictUser;
	@Column
	private boolean portHealthUser;
	@Enumerated(EnumType.STRING)
	private JurisdictionLevel jurisdictionLevel;
	@Enumerated(EnumType.STRING)
	private DefaultUserRole linkedDefaultUserRole;
	@Column
	private boolean restrictAccessToAssignedEntities;

	public String getUserRightsJson() {
		return userRightsJson;
	}

	public void setUserRightsJson(String userRightsJson) {
		this.userRightsJson = userRightsJson;
		userRights = null;
	}

	@Transient // Needed for merge logic
	public Set<UserRight> getUserRights() {
		if (userRights == null) {
			Gson gson = new Gson();
			Type type = new TypeToken<Set<UserRight>>() {
			}.getType();
			userRights = gson.fromJson(userRightsJson, type);
			if (userRights == null) {
				userRights = new HashSet<>();
			}
		}
		return userRights;
	}

	public void setUserRights(Set<UserRight> userRights) {
		this.userRights = userRights;
		Gson gson = new Gson();
		userRightsJson = gson.toJson(userRights);
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

	public boolean hasAssociatedDistrictUser() {
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

	public boolean isRestrictAccessToAssignedEntities() {
		return restrictAccessToAssignedEntities;
	}

	public void setRestrictAccessToAssignedEntities(boolean restrictAccessToAssignedEntities) {
		this.restrictAccessToAssignedEntities = restrictAccessToAssignedEntities;
	}

	public JurisdictionLevel getJurisdictionLevel() {
		return jurisdictionLevel;
	}

	public void setJurisdictionLevel(JurisdictionLevel jurisdictionLevel) {
		this.jurisdictionLevel = jurisdictionLevel;
	}

	public DefaultUserRole getLinkedDefaultUserRole() {
		return linkedDefaultUserRole;
	}

	public void setLinkedDefaultUserRole(DefaultUserRole linkedDefaultUserRole) {
		this.linkedDefaultUserRole = linkedDefaultUserRole;
	}

	public static boolean isPortHealthUser(Collection<UserRole> userRoles) {

		return userRoles.stream().anyMatch(UserRole::isPortHealthUser);
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

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}
}
