package de.symeda.sormas.api.user;

import java.util.Set;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.location.LocationDto;

public class UserDto extends UserReferenceDto {

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
	public static final String ASSOCIATED_OFFICER = "associatedOfficer";

	private boolean active = true;
	
	private String userName;
	
	private String firstName;
	private String lastName;
	private String userEmail;
	private String phone;
	private LocationDto address;	
	
	private ReferenceDto region;
	private ReferenceDto district;
	private Set<UserRole> userRoles;
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
		return firstName + " " + lastName.toUpperCase();
	}

	
	public UserReferenceDto getAssociatedOfficer() {
		return associatedOfficer;
	}

	public void setAssociatedOfficer(UserReferenceDto associatedOfficer) {
		this.associatedOfficer = associatedOfficer;
	}

	public ReferenceDto getRegion() {
		return region;
	}

	public void setRegion(ReferenceDto region) {
		this.region = region;
	}

	public ReferenceDto getDistrict() {
		return district;
	}

	public void setDistrict(ReferenceDto district) {
		this.district = district;
	}

}
