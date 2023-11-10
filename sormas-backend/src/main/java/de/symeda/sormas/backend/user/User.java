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

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.NotExposedToApi;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;

@Entity(name = User.TABLE_NAME)
@EntityListeners(User.UserListener.class)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User extends AbstractDomainObject {

	private static final long serialVersionUID = -629432920970152112L;

	public static final String TABLE_NAME = "users";
	public static final String TABLE_NAME_USERROLES = "userroles";
	public static final String TABLE_NAME_USERS_USERROLES = "users_userroles";
	public static final String TABLE_NAME_USERROLES_USERRIGHTS = "userroles_userrights";
	public static final String TABLE_NAME_USERROLES_EMAILNOTIFICATIONTYPES = "userroles_emailnotificationtypes";
	public static final String TABLE_NAME_USERROLES_SMSNOTIFICATIONTYPES = "userroles_smsnotificationtypes";

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
	public static final String HAS_CONSENTED_TO_GDPR = "hasConsentedToGdpr";
	public static final String JURISDICTION_LEVEL = "jurisdictionLevel";
	public static final String LIMITED_DISEASES = "limitedDiseases";

	private String userName;
	@NotExposedToApi
	private String password;
	@NotExposedToApi
	private String seed;

	private boolean active = true;

	private String firstName;
	private String lastName;
	private String userEmail;
	private String phone;
	private Location address;

	private Set<UserRole> userRoles;
	private JurisdictionLevel jurisdictionLevel;

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

	private Set<Disease> limitedDiseases;

	private Language language;

	private boolean hasConsentedToGdpr;

	@Column(nullable = false, length = CHARACTER_LIMIT_DEFAULT)
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Size(max = 64)
	@Column(name = "password", nullable = false, length = 64)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "seed", nullable = false, length = 16)
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

	@Column(nullable = false, length = CHARACTER_LIMIT_DEFAULT)
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(nullable = false, length = CHARACTER_LIMIT_DEFAULT)
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

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public Location getAddress() {
		if (address == null) {
			address = new Location();
		}
		return address;
	}

	public void setAddress(Location address) {
		this.address = address;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	@ManyToMany(cascade = {}, fetch = FetchType.LAZY)
	@JoinTable(name = TABLE_NAME_USERS_USERROLES, joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "userrole_id"))
	public Set<UserRole> getUserRoles() {
		return userRoles;
	}

	/**
	 * Call updateJurisdictionLevel afterwards if you need to access the jurisdiction level.
	 * This is not done automatically to avoid unnecessary calls when setUserRoles is used by the JPA provider
	 */
	public void setUserRoles(Set<UserRole> userRoles) {
		this.userRoles = userRoles;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public JurisdictionLevel getJurisdictionLevel() {
		return jurisdictionLevel;
	}

	@Transient
	public boolean isAdmin() {
		return (this.getUserRoles().stream().filter(i -> i.getCaption().contains(I18nProperties.getEnumCaption(DefaultUserRole.ADMIN))).count() == 1);
	}

	public void setJurisdictionLevel(JurisdictionLevel jurisdictionLevel) {
		this.jurisdictionLevel = jurisdictionLevel;
	}

	@PrePersist
	@PreUpdate
	public void updateJurisdictionLevel() {
		jurisdictionLevel = UserRole.getJurisdictionLevel(this.getUserRoles());
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	public User getAssociatedOfficer() {
		return associatedOfficer;
	}

	public void setAssociatedOfficer(User associatedOfficer) {
		this.associatedOfficer = associatedOfficer;
	}

	public UserReferenceDto toReference() {
		return new UserReferenceDto(getUuid(), getFirstName(), getLastName());
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	public Facility getHealthFacility() {
		return healthFacility;
	}

	public void setHealthFacility(Facility healthFacility) {
		this.healthFacility = healthFacility;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	public Facility getLaboratory() {
		return laboratory;
	}

	public void setLaboratory(Facility laboratory) {
		this.laboratory = laboratory;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	public PointOfEntry getPointOfEntry() {
		return pointOfEntry;
	}

	public void setPointOfEntry(PointOfEntry pointOfEntry) {
		this.pointOfEntry = pointOfEntry;
	}

	@Convert(converter = DiseaseSetConverter.class)
	public Set<Disease> getLimitedDiseases() {
		return limitedDiseases;
	}

	public void setLimitedDiseases(Set<Disease> limitedDisease) {
		this.limitedDiseases = limitedDisease;
	}

	@Enumerated(EnumType.STRING)
	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public boolean isHasConsentedToGdpr() {
		return hasConsentedToGdpr;
	}

	public void setHasConsentedToGdpr(boolean hasConsentedToGdpr) {
		this.hasConsentedToGdpr = hasConsentedToGdpr;
	}

	/**
	 * Checks if the User possesses any of the specified userRoles
	 */
	public boolean hasAnyUserRole(UserRole... userRoles) {
		return Arrays.stream(userRoles).anyMatch(getUserRoles()::contains);
	}

	public boolean hasAnyUserRole(Collection<UserRole> userRoles) {
		return userRoles.stream().anyMatch(getUserRoles()::contains);
	}

	/**
	 * Checks if the User possesses the given access right.
	 *
	 * @param userRight
	 *            the access right in question
	 * @return true if the user has the access right, false otherwise
	 */
	public boolean hasUserRight(UserRight userRight) {
		return this.getUserRoles().stream().anyMatch(userRole -> userRole.getUserRights().contains(userRight));
	}

	public boolean hasAnyUserRight(Set<UserRight> userRights) {
		return this.getUserRoles().stream().anyMatch(userRole -> userRole.getUserRights().stream().anyMatch(userRights::contains));
	}

	public static String buildCaptionForNotification(User user) {
		if (user == null) {
			return "-";
		}

		String caption = user.getFirstName() + " " + user.getLastName();
		if (StringUtils.isNotEmpty(user.getUserEmail())) {
			caption += " (" + user.getUserEmail() + ")";
		}
		return caption;
	}

	static class UserListener {

		@PostPersist
		@PostUpdate
		private void afterAnyUpdate(User user) {
			UserCache.getInstance().remove(user.getUserName());
		}
	}
}
