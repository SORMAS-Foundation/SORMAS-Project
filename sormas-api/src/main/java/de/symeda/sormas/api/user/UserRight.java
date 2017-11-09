package de.symeda.sormas.api.user;

import static de.symeda.sormas.api.user.UserRole.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum UserRight {

	CASE_EDIT_DISEASE(
			NATIONAL_USER,
			SURVEILLANCE_SUPERVISOR
			),
	CASE_EDIT_FACILITY(
			NATIONAL_USER,
			SURVEILLANCE_SUPERVISOR
			),
	;
	
	private final Set<UserRole> userRoles;
	
	private UserRight(UserRole... userRoles) {
		this.userRoles = Collections.unmodifiableSet(new HashSet<UserRole>(Arrays.asList(userRoles)));
	}
	
	public boolean isForRole(UserRole userRole) {
		return userRoles.contains(userRole);
	}
	
	public Set<UserRole> getUserRoles() {
		return userRoles;
	}
}
