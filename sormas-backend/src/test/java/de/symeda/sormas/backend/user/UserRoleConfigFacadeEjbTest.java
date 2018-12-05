package de.symeda.sormas.backend.user;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.user.UserRoleConfigDto;
import de.symeda.sormas.backend.AbstractBeanTest;

public class UserRoleConfigFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetEffectiveUserRights() throws Exception {
	
		// 1. no role configured -> use defaults 
		Set<UserRight> supervisorRights = getUserRoleConfigFacade().getEffectiveUserRights(UserRole.SURVEILLANCE_SUPERVISOR);
		assertThat(supervisorRights, is(UserRole.SURVEILLANCE_SUPERVISOR.getDefaultUserRights()));
		
		UserRoleConfigDto userRoleConfig = UserRoleConfigDto.build(UserRole.SURVEILLANCE_SUPERVISOR);
		userRoleConfig = getUserRoleConfigFacade().saveUserRoleConfig(userRoleConfig);
		
		// 2. role configured with no rights  
		supervisorRights = getUserRoleConfigFacade().getEffectiveUserRights(UserRole.SURVEILLANCE_SUPERVISOR);
		assertThat(supervisorRights, is(IsEmptyCollection.empty()));

		// 3. role configured with a few rights  
		userRoleConfig.getUserRights().add(UserRight.CASE_CREATE);
		userRoleConfig.getUserRights().add(UserRight.CASE_EDIT);
		userRoleConfig = getUserRoleConfigFacade().saveUserRoleConfig(userRoleConfig);

		supervisorRights = getUserRoleConfigFacade().getEffectiveUserRights(UserRole.SURVEILLANCE_SUPERVISOR);
		assertThat(supervisorRights, is(new HashSet<UserRight>(Arrays.asList(UserRight.CASE_CREATE, UserRight.CASE_EDIT))));
		
		// 4. combine configured and default rights  
		Set<UserRight> mixedUserRights = getUserRoleConfigFacade().getEffectiveUserRights(UserRole.SURVEILLANCE_SUPERVISOR, UserRole.NATIONAL_OBSERVER);
		Set<UserRight> expectedUserRights = new HashSet<UserRight>(Arrays.asList(UserRight.CASE_CREATE, UserRight.CASE_EDIT));
		expectedUserRights.addAll(UserRole.NATIONAL_OBSERVER.getDefaultUserRights());
		assertThat(mixedUserRights, is(expectedUserRights));

	}

}
