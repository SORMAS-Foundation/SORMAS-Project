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

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;

@Entity(name = UserRoleConfig.TABLE_NAME)
@DatabaseTable(tableName = UserRoleConfig.TABLE_NAME)
public class UserRoleConfig extends AbstractDomainObject {

	private static final long serialVersionUID = 9053095630718041842L;

	public static final String TABLE_NAME = "userrolesconfig";
	public static final String I18N_PREFIX = "UserRole";

	public static final String USER_ROLE = "userRole";
	public static final String USER_RIGHTS = "userRights";

	@Enumerated(EnumType.STRING)
	private UserRole userRole;

	@Column(name = "userRights", length = 1024)
	private String userRightsJson;

	// initialized from userRightsJson
	private Set<UserRight> userRights;

	public UserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

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

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}
}
