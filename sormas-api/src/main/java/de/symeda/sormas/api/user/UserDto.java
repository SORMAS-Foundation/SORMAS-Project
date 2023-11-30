/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.user;

import java.util.Collections;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.audit.AuditIncludeProperty;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.FeatureIndependent;
import de.symeda.sormas.api.utils.FieldConstraints;

@FeatureIndependent
public class UserDto extends EntityDto {

	private static final long serialVersionUID = -8558187171374254398L;

	public static final String COLUMN_NAME_USERROLE = "userrole_id";
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
    public static final String LIMITED_DISEASES = "limitedDiseases";
	public static final String LANGUAGE = "language";
	public static final String HAS_CONSENTED_TO_GDPR = "hasConsentedToGdpr";
	public static final String JURISDICTION_LEVEL = "jurisdictionLevel";

	private boolean active = true;

	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@AuditIncludeProperty
	private String userName;

	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String firstName;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String lastName;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String userEmail;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String phone;
	@Valid
	private LocationDto address;

	private Set<UserRoleReferenceDto> userRoles = Collections.emptySet();

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

	private Set<Disease> limitedDiseases;

	private Language language;

	private boolean hasConsentedToGdpr;

	private JurisdictionLevel jurisdictionLevel;

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

	public Set<UserRoleReferenceDto> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(Set<UserRoleReferenceDto> userRoles) {
		this.userRoles = userRoles;
	}

	@Override
	public String buildCaption() {
		return UserReferenceDto.buildCaption(firstName, lastName);
	}

	@JsonIgnore
	public String i18nPrefix() {
		return I18N_PREFIX;
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
		return new UserReferenceDto(getUuid(), getFirstName(), getLastName());
	}

	public Set<Disease> getLimitedDiseases() {
		return limitedDiseases;
	}

	public void setLimitedDiseases(Set<Disease> limitedDiseases) {
		this.limitedDiseases = limitedDiseases;
	}

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

	public JurisdictionLevel getJurisdictionLevel() {
		return jurisdictionLevel;
	}

	public void setJurisdictionLevel(JurisdictionLevel jurisdictionLevel) {
		this.jurisdictionLevel = jurisdictionLevel;
	}
}
