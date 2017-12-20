package de.symeda.sormas.api.user;

import java.util.Set;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

public class UserDto extends EntityDto {

	private static final long serialVersionUID = -8558187171374254398L;

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
	public static final String HEALTH_FACILITY = "healthFacility";
	public static final String ASSOCIATED_OFFICER = "associatedOfficer";
	public static final String LABORATORY = "laboratory";

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
	// facility of informant
	private FacilityReferenceDto healthFacility;
	// laboratory of lab user
	private FacilityReferenceDto laboratory;

	private UserReferenceDto associatedOfficer;
	
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
		return buildCaption(firstName, lastName, userRoles);
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
	
	public UserReferenceDto toReference() {
		return new UserReferenceDto(getUuid());
	}
	
	public static String buildCaption(String firstName, String lastName, Set<UserRole> userRoles) {
		StringBuilder result = new StringBuilder();
		result.append(firstName).append(" ").append(lastName.toUpperCase());
		boolean first = true;
		for (UserRole userRole : userRoles) {
			if (first) {
				result.append(" - ");
				first = false;
			} else {
				result.append(", ");
			}
			result.append(userRole.toShortString());
		}
		return result.toString();
	}
}
