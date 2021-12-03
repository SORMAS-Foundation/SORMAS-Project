package de.symeda.sormas.backend.user;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Test;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.user.UserRoleConfigDto;
import de.symeda.sormas.backend.AbstractBeanTest;

public class UserRoleConfigFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetEffectiveUserRights() {

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
		Set<UserRight> mixedUserRights =
			getUserRoleConfigFacade().getEffectiveUserRights(UserRole.SURVEILLANCE_SUPERVISOR, UserRole.NATIONAL_OBSERVER);
		Set<UserRight> expectedUserRights = new HashSet<UserRight>(Arrays.asList(UserRight.CASE_CREATE, UserRight.CASE_EDIT));
		expectedUserRights.addAll(UserRole.NATIONAL_OBSERVER.getDefaultUserRights());
		assertThat(mixedUserRights, is(expectedUserRights));
	}

	// not testable, because history tables don't work with H2
//	@Test
//	public void testGetDeletedUuids() {
//		// 1. no role configured -> use defaults 
//		List<String> deletedUuids = getUserRoleConfigFacade().getDeletedUuids(null);
//		assertThat(deletedUuids, is(IsEmptyCollection.empty()));
//		
//		UserRoleConfigDto userRoleConfig = UserRoleConfigDto.build(UserRole.SURVEILLANCE_SUPERVISOR);
//		userRoleConfig = getUserRoleConfigFacade().saveUserRoleConfig(userRoleConfig);
//
//		deletedUuids = getUserRoleConfigFacade().getDeletedUuids(null);
//		assertThat(deletedUuids, is(IsEmptyCollection.empty()));
//
//		getUserRoleConfigFacade().deleteUserRoleConfig(userRoleConfig);
//
//		deletedUuids = getUserRoleConfigFacade().getDeletedUuids(null);
//		assertThat(deletedUuids, is(Arrays.asList(userRoleConfig.getUuid())));
//
//		deletedUuids = getUserRoleConfigFacade().getDeletedUuids(DateUtils.addMinutes(new Date(), -1));
//		assertThat(deletedUuids, is(Arrays.asList(userRoleConfig.getUuid())));
//
//		deletedUuids = getUserRoleConfigFacade().getDeletedUuids(new Date());
//		assertThat(deletedUuids, is(IsEmptyCollection.empty()));
//	}
}
