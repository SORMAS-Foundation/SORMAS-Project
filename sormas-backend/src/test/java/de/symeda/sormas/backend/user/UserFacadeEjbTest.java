package de.symeda.sormas.backend.user;

import org.junit.Test;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;

public class UserFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetUsersByRegionAndRoles() throws Exception {
		getUserFacade().getUsersByRegionAndRoles(null, UserRole.SURVEILLANCE_OFFICER);
	}

}
