package de.symeda.sormas.api.user;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.utils.DataHelper;

public class UserTypeConfigDto extends EntityDto{

	private static final long serialVersionUID = -547459523041494446L;

	public static final String I18N_PREFIX = "UserType";

	public static final String USER_TYPE = "userType";
	
	private UserType userType;
	
	public static UserTypeConfigDto build(UserType userType) {
		UserTypeConfigDto dto = new UserTypeConfigDto();
		dto.setUuid(DataHelper.createUuid());
		dto.setUserType(userType);
		
		return dto;
	}
	
	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}
	
	
}
