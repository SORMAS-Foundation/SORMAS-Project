package de.symeda.sormas.api.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.area.AreaReferenceDto;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.user.FormAccess;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.user.UserType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.FieldConstraints;

public class UserReportModelDto extends EntityDto {
	
	

	private static final long serialVersionUID = -8558187171374254398L;

	public static final String COLUMN_NAME_USERROLE = "userrole";
	public static final String COLUMN_NAME_FORMACCESS = "formaccess";
	public static final String COLUMN_NAME_USER_ID = "user_id";
	
	

	public static final String I18N_PREFIX = "User";

	public static final String ACTIVE = "active";
	public static final String USER_CAPTIONACTIVE = "capactive";
	public static final String USER_NAME = "userName";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String USER_POSITION = "userPosition";
	public static final String USER_ORGANISATION = "userOrganisation";
	public static final String NAME = "name";
	public static final String USER_EMAIL = "userEmail";
	public static final String PHONE = "phone";
	public static final String ADDRESS = "address";
	public static final String USER_ROLES = "userRoles";
	public static final String FORM_ACCESS = "formAccess";
	public static final String TABLE_NAME_USERTYPES = "usertype";
	
	public static final String REGION = "region";
	public static final String AREA = "area";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	
	public static final String COMMUNITY_NO = "clusterNumber";

	private boolean active = true;

	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String userName;

	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String firstName;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String lastName;
	private String userPosition;
	private String userOrganisation;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String userEmail;
	@Size(max = FieldConstraints.CHARACTER_LIMIT_SMALL, message = Validations.textTooLong)
	private String phone;
	
	private Set<UserRole> userRoles;
	
	private Set<FormAccess> formAccess;
	//can add a user type property to the user  
	private UserType usertype;	
		
	private AreaReferenceDto area;
	private RegionReferenceDto region;
	private DistrictReferenceDto district;

	private Set<CommunityReferenceDto> community;
	
	private String clusterNumber;
	
//
//	public static UserReportModelDto build() {
//		UserReportModelDto user = new UserReportModelDto();
//		user.setUuid(DataHelper.createUuid());
//		return user;
//	}

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

	public String getUserPosition() {
		return userPosition;
	}

	public void setUserPosition(String userPosition) {
		this.userPosition = userPosition;
	}

	public String getUserOrganisation() {
		return userOrganisation;
	}

	public void setUserOrganisation(String userOrganisation) {
		this.userOrganisation = userOrganisation;
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

	public Set<UserRole> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(Set<UserRole> userRoles) {
		this.userRoles = userRoles;
	}

	public Set<FormAccess> getFormAccess() {
		return formAccess;
	}

	public void setFormAccess(Set<FormAccess> formAccess) {
		this.formAccess = formAccess;
	}

	public UserType getUsertype() {
		return usertype;
	}

	public void setUsertype(UserType usertype) {
		this.usertype = usertype;
	}

	@Override
	public String toString() {
		return UserReferenceDto.buildCaption(firstName, lastName, userRoles, usertype);
	}


	public AreaReferenceDto getArea() {
		return area;
	}

	public void setArea(AreaReferenceDto area) {
		this.area = area;
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

	public Set<CommunityReferenceDto> getCommunity() {
		return community;
	}

	public void setCommunity(Set<CommunityReferenceDto> community) {
		this.community = community;
	}
	
	
	public String getClusterNumber() {
		List<String> stre = new ArrayList<>();
		
		for(CommunityReferenceDto comSet : getCommunity()) {
		//	System.out.println("...... "+comSet.getNumber());
			stre.add(comSet.getNumber() + "");
		}
		
		
		return stre.toString();
	}

	public UserReferenceDto toReference() {
		return new UserReferenceDto(getUuid(), getFirstName(), getLastName(), getUserRoles(), getFormAccess(), getUsertype());
	}

}
