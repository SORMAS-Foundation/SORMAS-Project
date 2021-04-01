package de.symeda.sormas.backend.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedStatic;

import de.symeda.sormas.api.AuthProvider;
import de.symeda.sormas.api.utils.PasswordHelper;
import de.symeda.sormas.backend.AbstractBeanTest;

public class UserServiceTest extends AbstractBeanTest {

	private static MockedStatic<AuthProvider> mockAuthProvider;

	@BeforeClass
	public static void beforeClass() {
		AuthProvider authProvider = mock(AuthProvider.class);
		mockAuthProvider = mockStatic(AuthProvider.class);
		assertNotNull(mockAuthProvider);
		mockAuthProvider.when(AuthProvider::getProvider).thenReturn(authProvider);
		when(authProvider.isUsernameCaseSensitive()).thenReturn(true);
	}

	@AfterClass
	public static void afterClass() {
		assertNotNull(mockAuthProvider);
		//Important: release static mock.
		mockAuthProvider.closeOnDemand();
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
		Set<User> defaultUsers = UserTestHelper.generateDefaultUsers(false);
		Set<User> randomUsers = UserTestHelper.generateRandomUsers(10);
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
