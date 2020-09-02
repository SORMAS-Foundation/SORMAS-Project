package de.symeda.sormas.backend.user;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.junit.Test;

import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;

public class UserFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetUsersByRegionAndRoles() {
		getUserFacade().getUsersByRegionAndRoles(null, UserRole.SURVEILLANCE_OFFICER);
	}

	@Test
	public void testGetValidLoginRoles() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR);
		String password = getUserFacade().resetPassword(user.getUuid());

		Set<UserRole> validLoginRoles = getUserFacade().getValidLoginRoles(user.getUserName(), password);
		assertThat(validLoginRoles, containsInAnyOrder(UserRole.SURVEILLANCE_SUPERVISOR));

		user.setActive(false);
		getUserFacade().saveUser(user);

		validLoginRoles = getUserFacade().getValidLoginRoles(user.getUserName(), password);
		assertThat(validLoginRoles, nullValue());
	}
}
