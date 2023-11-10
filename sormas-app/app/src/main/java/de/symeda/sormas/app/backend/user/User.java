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

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_TEXT;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.pointofentry.PointOfEntry;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.util.MetaProperty;

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
	public static final String LIMITED_DISEASES = "limitedDiseases";
	public static final String JURISDICTION_LEVEL = "jurisdictionLevel";

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

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User associatedOfficer;

	@Enumerated(EnumType.STRING)
	private Language language;

	@Column(name = "limitedDiseases", length = CHARACTER_LIMIT_TEXT)
	private String limitedDiseasesJson;

	private Set<Disease> limitedDiseases;

	// initialized from userRolesJson
	private Set<UserRole> userRoles = null;

	@Enumerated(EnumType.STRING)
	private JurisdictionLevel jurisdictionLevel;

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

	public String getLimitedDiseasesJson() {
		return limitedDiseasesJson;
	}

	public void setLimitedDiseasesJson(String limitedDiseasesJson) {
		this.limitedDiseasesJson = limitedDiseasesJson;
	}

	public Set<Disease> getLimitedDiseases() {
		if (limitedDiseases == null) {
			Gson gson = new Gson();
			Type type = new TypeToken<Set<Disease>>() {
			}.getType();
			limitedDiseases = gson.fromJson(limitedDiseasesJson, type);
		}

		return limitedDiseases;
	}

	public void setLimitedDiseases(Set<Disease> limitedDiseases) {
		if (limitedDiseases == null) {
			this.limitedDiseases = null;
			this.limitedDiseasesJson = null;
		} else {
			this.limitedDiseases = limitedDiseases;
			Gson gson = new Gson();
			limitedDiseasesJson = gson.toJson(limitedDiseases.stream().map(Disease::name).collect(Collectors.toSet()));
		}
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	@MetaProperty
	public Set<UserRole> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(Set<UserRole> userRoles) {
		this.userRoles = userRoles;
	}

	public boolean hasUserRole(UserRole userRole) {
		return userRoles != null && userRoles.contains(userRole);
	}

	public String getUserRolesString() {

		StringBuilder result = new StringBuilder();
		if (userRoles != null) {
			for (UserRole userRole : userRoles) {
				if (result.length() > 0) {
					result.append(", ");
				}
				result.append(userRole.getCaption());
			}
		}
		return result.toString();
	}

	public JurisdictionLevel getJurisdictionLevel() {
		return jurisdictionLevel;
	}

	public void setJurisdictionLevel(JurisdictionLevel jurisdictionLevel) {
		this.jurisdictionLevel = jurisdictionLevel;
	}

	public boolean hasJurisdictionLevel(JurisdictionLevel... jurisdictionLevels) {
		JurisdictionLevel userJurisdictionLevel = getJurisdictionLevel();
		return Arrays.asList(jurisdictionLevels).contains(userJurisdictionLevel);
	}

	@Override
	public String buildCaption() {

		StringBuilder result = new StringBuilder();
		result.append(getFirstName()).append(" ").append(getLastName().toUpperCase());
		if (getUserRoles() != null && getUserRoles().size() > 0) {
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
