/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.user;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;

@Entity(name = User.TABLE_NAME)
@DatabaseTable(tableName = User.TABLE_NAME)
public class User extends AbstractDomainObject {

	private static final long serialVersionUID = -629432920970152112L;

	public static final String TABLE_NAME = "users";
	public static final String I18N_PREFIX = "User";

	public static final String USER_NAME = "userName";
	public static final String ACTIVE = "active";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String USER_EMAIL = "userEmail";
	public static final String PHONE = "phone";
	public static final String ADDRESS = "address";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String HEALTH_FACILITY = "healthFacility";
	public static final String POINT_OF_ENTRY = "pointOfEntry";
	public static final String ASSOCIATED_OFFICER = "associatedOfficer";
	public static final String USER_ROLES_JSON = "userRole";
	public static final String LANGUAGE = "language";

	@Column(nullable = false)
	private String userName;
	@Column(nullable = false)
	private boolean active;

	@Column(nullable = false)
	private String firstName;
	@Column(nullable = false)
	private String lastName;
	@Column
	private String userEmail;
	@Column
	private String phone;
	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 2)
	private Location address;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Region region;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private District district;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Community community;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Facility healthFacility;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private PointOfEntry pointOfEntry;

	@ManyToOne(cascade = {})
	private User associatedOfficer;

	@Enumerated(EnumType.STRING)
	private Language language;

	@Enumerated(EnumType.STRING)
	private Disease limitedDisease;

	@Column(name = "userRole")
	private String userRolesJson;

	// initialized from userRolesJson
	private Set<UserRole> userRoles = null;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Location getAddress() {
		return address;
	}

	public void setAddress(Location address) {
		this.address = address;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
	}

	public Facility getHealthFacility() {
		return healthFacility;
	}

	public void setHealthFacility(Facility healthFacility) {
		this.healthFacility = healthFacility;
	}

	public PointOfEntry getPointOfEntry() {
		return pointOfEntry;
	}

	public void setPointOfEntry(PointOfEntry pointOfEntry) {
		this.pointOfEntry = pointOfEntry;
	}

	public User getAssociatedOfficer() {
		return associatedOfficer;
	}

	public void setAssociatedOfficer(User associatedOfficer) {
		this.associatedOfficer = associatedOfficer;
	}

	public Disease getLimitedDisease() {
		return limitedDisease;
	}

	public void setLimitedDisease(Disease limitedDisease) {
		this.limitedDisease = limitedDisease;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public String getUserRolesJson() {
		return userRolesJson;
	}

	public void setUserRolesJson(String userRolesJson) {
		this.userRolesJson = userRolesJson;
		userRoles = null;
	}

	@Transient // Needed for merge logic
	public Set<UserRole> getUserRoles() {
		if (userRoles == null) {
			Gson gson = new Gson();
			Type type = new TypeToken<Set<UserRole>>() {
			}.getType();
			userRoles = gson.fromJson(userRolesJson, type);
			if (userRoles == null) {
				userRoles = new HashSet<>();
			}
		}
		return userRoles;
	}

	public void setUserRoles(Set<UserRole> userRoles) {
		this.userRoles = userRoles;
		Gson gson = new Gson();
		userRolesJson = gson.toJson(userRoles);
	}

	public boolean hasUserRole(UserRole userRole) {
		return getUserRoles().contains(userRole);
	}

	public String getUserRolesString() {

		StringBuilder result = new StringBuilder();
		for (UserRole userRole : getUserRoles()) {
			if (result.length() > 0) {
				result.append(", ");
			}
			result.append(userRole.toString());
		}
		return result.toString();
	}

	@Override
	public String toString() {

		StringBuilder result = new StringBuilder();
		result.append(getFirstName()).append(" ").append(getLastName().toUpperCase());
		if (getUserRoles().size() > 0) {
			result.append(" - ");
			result.append(getUserRolesString());
		}
		return result.toString();
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}
}
