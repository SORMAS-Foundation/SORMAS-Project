package de.symeda.sormas.backend.user;

import java.util.HashSet;
import java.util.Set;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;

public class UserTest extends AbstractBeanTest {

	@Test
	public void testHasAnyUserRole() {

		final TestDataCreator creator = new TestDataCreator(this);
		User u = new User();
		Set<UserRole> userRoles = new HashSet<>();
		u.setUserRoles(userRoles);

		MatcherAssert.assertThat(u.hasAnyUserRole(creator.getUserRole(DefaultUserRole.ADMIN)), Matchers.is(false));

		userRoles.add(creator.getUserRole(DefaultUserRole.ADMIN));
		MatcherAssert.assertThat(u.hasAnyUserRole(creator.getUserRole(DefaultUserRole.ADMIN)), Matchers.is(true));

		userRoles.add(creator.getUserRole(DefaultUserRole.CASE_OFFICER));
		MatcherAssert.assertThat(u.hasAnyUserRole(creator.getUserRole(DefaultUserRole.ADMIN)), Matchers.is(true));
		MatcherAssert.assertThat(u.hasAnyUserRole(creator.getUserRole(DefaultUserRole.CASE_OFFICER)), Matchers.is(true));
		MatcherAssert.assertThat(u.hasAnyUserRole(creator.getUserRole(DefaultUserRole.CASE_SUPERVISOR)), Matchers.is(false));

		MatcherAssert.assertThat(
			u.hasAnyUserRole(creator.getUserRole(DefaultUserRole.ADMIN), creator.getUserRole(DefaultUserRole.CASE_OFFICER)),
			Matchers.is(true));
		MatcherAssert.assertThat(
			u.hasAnyUserRole(
				creator.getUserRole(DefaultUserRole.CASE_OFFICER),
				creator.getUserRole(DefaultUserRole.CASE_SUPERVISOR)),
			Matchers.is(true));
	}
}
