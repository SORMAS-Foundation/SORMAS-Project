package de.symeda.sormas.backend.util;

import java.util.Arrays;
import java.util.HashSet;

import de.symeda.sormas.api.user.UserHelper;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.user.User;

public class MockDataGenerator {
	
    public static User createUser(UserRole userRole, String firstName, String lastName, String password) {
    	User user = new User();
    	user.setFirstName(firstName);
    	user.setLastName(lastName);
    	if (userRole != null) {
    		user.setUserRoles(new HashSet<UserRole>(Arrays.asList(userRole)));
    	}
    	user.setUserName(UserHelper.getSuggestedUsername(user.getFirstName(), user.getLastName()));
		user.setSeed(PasswordHelper.createPass(16));
		user.setPassword(PasswordHelper.encodePassword(password, user.getSeed()));
    	return user;
    }
    
}

