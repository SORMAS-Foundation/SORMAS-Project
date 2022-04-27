package de.symeda.sormas.backend.user;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import de.symeda.sormas.api.AuthProvider;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.PasswordHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;

public class UserServiceTest extends AbstractBeanTest {

	private static MockedStatic<AuthProvider> mockAuthProvider;

	@BeforeClass
	public static void beforeClass() {
		AuthProvider authProvider = mock(AuthProvider.class);
		mockAuthProvider = mockStatic(AuthProvider.class);
		assertNotNull(mockAuthProvider);
		Mockito.when(AuthProvider.getProvider(any())).thenReturn(authProvider);
	}

	@AfterClass
	public static void afterClass() {
		assertNotNull(mockAuthProvider);
		//Important: release static mock.
		mockAuthProvider.closeOnDemand();
	}

	@Test
	public void testGetUserReferencesByJurisdictions() {

		List<String> regionUuids = null;
		List<String> districtUuids = null;
		List<String> communityUuids = null;
		List<JurisdictionLevel> jurisdictionLevels = null;

		// 0. No conditions, test signature with userRoles varArg parameter
		List<UserReference> result = getUserService().getUserReferencesByJurisdictions(regionUuids, districtUuids, null, null, null);
		assertThat(result, hasSize(1));
		UserReference admin = result.get(0);
		assertThat(
			admin.getUserRoles(),
			containsInAnyOrder(creator.getUserRoleMap().get(DefaultUserRole.ADMIN), creator.getUserRoleMap().get(DefaultUserRole.NATIONAL_USER)));

		// 1a. Find admin with several conditions
		jurisdictionLevels = Arrays.asList(JurisdictionLevel.NATION);
		result = getUserService().getUserReferencesByJurisdictions(regionUuids, districtUuids, communityUuids, jurisdictionLevels, null);
		assertThat(result, contains(admin));
		jurisdictionLevels = Arrays.asList(JurisdictionLevel.NATION, JurisdictionLevel.DISTRICT);
		result = getUserService().getUserReferencesByJurisdictions(regionUuids, districtUuids, communityUuids, jurisdictionLevels, null);
		assertThat(result, contains(admin));

		// 1b. Exclude admin by role
		jurisdictionLevels = Arrays.asList(JurisdictionLevel.DISTRICT);
		result = getUserService().getUserReferencesByJurisdictions(regionUuids, districtUuids, communityUuids, jurisdictionLevels, null);
		assertThat(result, is(empty()));

		// 2. Exclude inactive user as overall condition
		RDCF rdcf = creator.createRDCF();
		UserDto supervisor = creator.createUser(rdcf, creator.getUserRoleReferenceDtoMap().get(DefaultUserRole.CONTACT_SUPERVISOR));
		jurisdictionLevels = Arrays.asList(JurisdictionLevel.REGION);
		result = getUserService().getUserReferencesByJurisdictions(regionUuids, districtUuids, communityUuids, jurisdictionLevels, null);
		assertThat(result.get(0).getUuid(), equalTo(supervisor.getUuid()));
		getUserFacade().disableUsers(Arrays.asList(supervisor.getUuid()));
		result = getUserService().getUserReferencesByJurisdictions(regionUuids, districtUuids, communityUuids, jurisdictionLevels, null);
		assertThat(result, is(empty()));

		// 3. regions filter
		getUserFacade().enableUsers(Arrays.asList(supervisor.getUuid()));
		result = getUserService().getUserReferencesByJurisdictions(Arrays.asList(rdcf.region.getUuid()), null, null, null, null);
		assertThat(result, hasSize(1));
		assertThat(result.get(0).getUuid(), equalTo(supervisor.getUuid()));

		UserDto officer = creator.createUser(rdcf, creator.getUserRoleReferenceDtoMap().get(DefaultUserRole.CONTACT_OFFICER));
		result = getUserService().getUserReferencesByJurisdictions(Arrays.asList(rdcf.region.getUuid()), null, null, null, null);
		assertThat(result, hasSize(2));

		// 4. districts filter
		result = getUserService().getUserReferencesByJurisdictions(null, Arrays.asList(rdcf.district.getUuid()), null, null, null);
		assertThat(result, hasSize(2));
		assertThat(result.get(0).getUuid(), equalTo(supervisor.getUuid()));

		// 5. user rights
		result = getUserService()
			.getUserReferencesByJurisdictions(null, Arrays.asList(rdcf.district.getUuid()), null, null, Arrays.asList(UserRight.CONTACT_RESPONSIBLE));
		assertThat(result, hasSize(1));
		assertThat(result.get(0).getUuid(), equalTo(officer.getUuid()));
	}

	@Test
	public void testGetAllDefaultUsers() {
		User user = getUserService().getByUserName("admin");
		assertNotNull(user);

		user.setSeed(PasswordHelper.createPass(16));
		user.setPassword(PasswordHelper.encodePassword("sadmin", user.getSeed()));
		getEntityManager().merge(user);

		List<User> result = getUserService().getAllDefaultUsers();
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("admin", result.get(0).getUserName());
	}

	@Test
	public void testGetAllDefaultUsersWithExistingUsers() {
		Set<User> defaultUsers = UserTestHelper.generateDefaultUsers(false, creator);
		Set<User> randomUsers = UserTestHelper.generateRandomUsers(10, creator);
		Set<User> testUsers = new HashSet<>();
		testUsers.addAll(defaultUsers);
		testUsers.addAll(randomUsers);

		for (User u : testUsers) {
			getEntityManager().persist(u);
		}

		List<User> result = getUserService().getAllDefaultUsers();
		// Default users size + 1 because one default admin is created by the AbstractBeanTest
		assertEquals(defaultUsers.size() + 1, result.size());
		for (User defaultUser : defaultUsers) {
			assertTrue(result.contains(defaultUser));
		}
		for (User randomUser : randomUsers) {
			assertFalse(result.contains(randomUser));
		}
	}
}
