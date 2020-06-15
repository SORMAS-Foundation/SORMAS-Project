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

import java.util.Set;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

public class UserDto extends EntityDto {

	private static final long serialVersionUID = -8558187171374254398L;

	public static final String COLUMN_NAME_USERROLE = "userrole";
	public static final String COLUMN_NAME_USER_ID = "user_id";

	public static final String I18N_PREFIX = "User";

	public static final String ACTIVE = "active";
	public static final String USER_NAME = "userName";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String NAME = "name";
	public static final String USER_EMAIL = "userEmail";
	public static final String PHONE = "phone";
	public static final String ADDRESS = "address";
	public static final String USER_ROLES = "userRoles";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String HEALTH_FACILITY = "healthFacility";
	public static final String ASSOCIATED_OFFICER = "associatedOfficer";
	public static final String LABORATORY = "laboratory";
	public static final String POINT_OF_ENTRY = "pointOfEntry";
	public static final String LIMITED_DISEASE = "limitedDisease";
	public static final String LANGUAGE = "language";

	private boolean active = true;

	private String userName;

	private String firstName;
	private String lastName;
	private String userEmail;
	private String phone;
	private LocationDto address;

	private Set<UserRole> userRoles;

	private RegionReferenceDto region;
	private DistrictReferenceDto district;
	// community of community informant
	private CommunityReferenceDto community;
	// facility of informant
	private FacilityReferenceDto healthFacility;
	// laboratory of lab user
	private FacilityReferenceDto laboratory;
	// point of entry of POE users
	private PointOfEntryReferenceDto pointOfEntry;

	private UserReferenceDto associatedOfficer;

	private Disease limitedDisease;

	private Language language;

	public static UserDto build() {
		UserDto user = new UserDto();
		user.setUuid(DataHelper.createUuid());
		user.setAddress(LocationDto.build());
		return user;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public String getName() {
		return firstName + " " + lastName;
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

	public LocationDto getAddress() {
		return address;
	}

	public void setAddress(LocationDto address) {
		this.address = address;
	}

	public Set<UserRole> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(Set<UserRole> userRoles) {
		this.userRoles = userRoles;
	}

	@Override
	public String toString() {
		return UserReferenceDto.buildCaption(firstName, lastName, userRoles);
	}

	public UserReferenceDto getAssociatedOfficer() {
		return associatedOfficer;
	}

	public void setAssociatedOfficer(UserReferenceDto associatedOfficer) {
		this.associatedOfficer = associatedOfficer;
	}

	public RegionReferenceDto getRegion() {
		return region;
	}

	public void setRegion(RegionReferenceDto region) {
		this.region = region;
	}

	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}

	public CommunityReferenceDto getCommunity() {
		return community;
	}

	public void setCommunity(CommunityReferenceDto community) {
		this.community = community;
	}

	public FacilityReferenceDto getHealthFacility() {
		return healthFacility;
	}

	public void setHealthFacility(FacilityReferenceDto healthFacility) {
		this.healthFacility = healthFacility;
	}

	public FacilityReferenceDto getLaboratory() {
		return laboratory;
	}

	public void setLaboratory(FacilityReferenceDto laboratory) {
		this.laboratory = laboratory;
	}

	public PointOfEntryReferenceDto getPointOfEntry() {
		return pointOfEntry;
	}

	public void setPointOfEntry(PointOfEntryReferenceDto pointOfEntry) {
		this.pointOfEntry = pointOfEntry;
	}

	public UserReferenceDto toReference() {
		return new UserReferenceDto(getUuid(), getFirstName(), getLastName(), getUserRoles());
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
}
