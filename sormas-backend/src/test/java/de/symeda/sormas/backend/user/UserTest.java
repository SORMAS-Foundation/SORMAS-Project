package de.symeda.sormas.backend.user;

import java.util.HashSet;
import java.util.Set;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import de.symeda.sormas.api.user.UserRole;

public class UserTest {

	@Test
	public void testHasAnyUserRole() {

		User u = new User();
		Set<UserRole> userRoles = new HashSet<>();
		u.setUserRoles(userRoles);

		MatcherAssert.assertThat(u.hasAnyUserRole(UserRole.ADMIN), Matchers.is(false));

		userRoles.add(UserRole.ADMIN);
		MatcherAssert.assertThat(u.hasAnyUserRole(UserRole.ADMIN), Matchers.is(true));

		userRoles.add(UserRole.CASE_OFFICER);
		MatcherAssert.assertThat(u.hasAnyUserRole(UserRole.ADMIN), Matchers.is(true));
		MatcherAssert.assertThat(u.hasAnyUserRole(UserRole.CASE_OFFICER), Matchers.is(true));
		MatcherAssert.assertThat(u.hasAnyUserRole(UserRole.CASE_SUPERVISOR), Matchers.is(false));

		MatcherAssert.assertThat(u.hasAnyUserRole(UserRole.ADMIN, UserRole.CASE_OFFICER), Matchers.is(true));
		MatcherAssert.assertThat(u.hasAnyUserRole(UserRole.CASE_OFFICER, UserRole.CASE_SUPERVISOR), Matchers.is(true));
	}
}
