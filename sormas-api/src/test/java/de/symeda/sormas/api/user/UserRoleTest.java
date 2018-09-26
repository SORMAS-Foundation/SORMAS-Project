package de.symeda.sormas.api.user;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class UserRoleTest {

	@Test
	public void checkConsistencyOfCombinableRoles() throws Exception {

		for (UserRole userRole : UserRole.values()) {
			for (UserRole combinableUserRole : userRole.getCombinableRoles()) {
				assertThat(combinableUserRole.toString() + " should also have the user role as entry", 
						combinableUserRole.getCombinableRoles(), hasItem(userRole));
			}
		}
	}
}
