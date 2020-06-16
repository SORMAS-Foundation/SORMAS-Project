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

import java.util.Arrays;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import de.symeda.auditlog.api.Audited;
import de.symeda.auditlog.api.AuditedAttribute;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

@Entity(name = "users")
@Audited
public class User extends AbstractDomainObject {

	private static final long serialVersionUID = -629432920970152112L;

	public static final String TABLE_NAME_USERROLES = "users_userroles";

	public static final String USER_NAME = "userName";
	public static final String PASSWORD = "password";
	public static final String SEED = "seed";
	public static final String ACTIVE = "active";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String USER_EMAIL = "userEmail";
	public static final String PHONE = "phone";
	public static final String ADDRESS = "address";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String USER_ROLES = "userRoles";
	public static final String HEALTH_FACILITY = "healthFacility";
	public static final String LABORATORY = "laboratory";
	public static final String POINT_OF_ENTRY = "pointOfEntry";
	public static final String ASSOCIATED_OFFICER = "associatedOfficer";
	public static final String LANGUAGE = "language";

	private String userName;
	private String password;
	private String seed;

	private boolean active = true;

	private String firstName;
	private String lastName;
	private String userEmail;
	private String phone;
	private Location address;

	private Set<UserRole> userRoles;

	private Region region;
	private District district;
	// community of community informant
	private Community community;
	// facility of hospital informant
	private Facility healthFacility;
	// laboratory of lab user
	private Facility laboratory;
	// point of entry of POE users
	private PointOfEntry pointOfEntry;

	private User associatedOfficer;

	private Disease limitedDisease;

	private Language language;

	@Column(nullable = false, length = COLUMN_LENGTH_DEFAULT)
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Size(max = 64)
	@Column(name = "password", nullable = false, length = 64)
	@AuditedAttribute(anonymous = true, anonymizingString = "*****")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "seed", nullable = false, length = 16)
	@AuditedAttribute(anonymous = true, anonymizingString = "*****")
	public String getSeed() {
		return seed;
	}

	public void setSeed(String seed) {
		this.seed = seed;
	}

	@Column(nullable = false)
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Column(nullable = false, length = COLUMN_LENGTH_DEFAULT)
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(nullable = false, length = COLUMN_LENGTH_DEFAULT)
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

	@ManyToOne(cascade = CascadeType.ALL)
	public Location getAddress() {
		if (address == null) {
			address = new Location();
		}
		return address;
	}

	public void setAddress(Location address) {
		this.address = address;
	}

	@ManyToOne(cascade = {})
	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	@ElementCollection(fetch = FetchType.LAZY)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name = TABLE_NAME_USERROLES,
		joinColumns = @JoinColumn(name = "user_id", referencedColumnName = User.ID, nullable = false),
		uniqueConstraints = @UniqueConstraint(columnNames = {
			"user_id",
			"userrole" }))
	@Column(name = "userrole", nullable = false)
	public Set<UserRole> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(Set<UserRole> userRoles) {
		this.userRoles = userRoles;
	}

	@ManyToOne(cascade = {})
	public User getAssociatedOfficer() {
		return associatedOfficer;
	}

	public void setAssociatedOfficer(User associatedOfficer) {
		this.associatedOfficer = associatedOfficer;
	}

	@Override
	public String toString() {
		return UserReferenceDto.buildCaption(getFirstName(), getLastName(), getUserRoles());
	}

	public UserReferenceDto toReference() {
		return new UserReferenceDto(getUuid(), getFirstName(), getLastName(), getUserRoles());
	}

	@Transient
	public boolean isSupervisor() {
		for (UserRole userRole : getUserRoles()) {
			if (userRole.isSupervisor()) {
				return true;
			}
		}
		return false;
	}

	@ManyToOne(cascade = {})
	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	@ManyToOne(cascade = {})
	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
	}

	@ManyToOne(cascade = {})
	public Facility getHealthFacility() {
		return healthFacility;
	}

	public void setHealthFacility(Facility healthFacility) {
		this.healthFacility = healthFacility;
	}

	@ManyToOne(cascade = {})
	public Facility getLaboratory() {
		return laboratory;
	}

	public void setLaboratory(Facility laboratory) {
		this.laboratory = laboratory;
	}

	@ManyToOne(cascade = {})
	public PointOfEntry getPointOfEntry() {
		return pointOfEntry;
	}

	public void setPointOfEntry(PointOfEntry pointOfEntry) {
		this.pointOfEntry = pointOfEntry;
	}

	@Enumerated(EnumType.STRING)
	public Disease getLimitedDisease() {
		return limitedDisease;
	}

	public void setLimitedDisease(Disease limitedDisease) {
		this.limitedDisease = limitedDisease;
	}

	@Enumerated(EnumType.STRING)
	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	/**
	 * Checks if the User possesses any of the specified userRoles
	 */
	public boolean hasAnyUserRole(UserRole... userRoles) {
		return Arrays.stream(userRoles).anyMatch(getUserRoles()::contains);
	}

	@Transient
	public JurisdictionLevel getJurisdictionLevel(){
		return UserRole.getJurisdictionLevel(this.getUserRoles());
	}
}
