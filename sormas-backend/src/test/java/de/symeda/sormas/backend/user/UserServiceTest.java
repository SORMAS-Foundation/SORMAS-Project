package de.symeda.sormas.backend.user;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import de.symeda.sormas.api.AuthProvider;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.PasswordHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;

public class UserServiceTest extends AbstractBeanTest {

	private static MockedStatic<AuthProvider> mockAuthProvider;

	@BeforeAll
	public static void beforeClass() {
		AuthProvider authProvider = mock(AuthProvider.class);
		mockAuthProvider = mockStatic(AuthProvider.class);
		assertNotNull(mockAuthProvider);
		Mockito.when(AuthProvider.getProvider(any())).thenReturn(authProvider);
	}

	@AfterAll
	public static void AfterAll() {
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
			containsInAnyOrder(creator.getUserRole(DefaultUserRole.ADMIN), creator.getUserRole(DefaultUserRole.NATIONAL_USER)));

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
		UserDto supervisor = creator.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.CONTACT_SUPERVISOR));
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

		UserDto officer = creator.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.CONTACT_OFFICER));
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

		executeInTransaction(em -> {
			User user = getUserService().getByUserName("admin");
			assertNotNull(user);
			user.setSeed(PasswordHelper.createPass(16));
			user.setPassword(PasswordHelper.encodePassword("sadmin", user.getSeed()));
			getUserService().persist(user);
		});

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
			getUserService().persist(u);
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

	@Test
	public void testGetUserRefsByInfrastructure() {

		RDCF rdcf1 = creator.createRDCF("R1", "D1", "C1", "F1", "P1");
		RDCF rdcf2 = creator.createRDCF("R2", "D2", "C2", "F2", "P2");

		UserDto hospInf1 = creator.createUser(rdcf1.region.getUuid(), rdcf1.district.getUuid(), rdcf1.facility.getUuid(), "HI", "1");
		UserDto hospInf2 = creator.createUser(rdcf2.region.getUuid(), rdcf2.district.getUuid(), rdcf2.facility.getUuid(), "HI", "2");
		UserDto survOff11 = creator.createUser(
			rdcf1.region.getUuid(),
			rdcf1.district.getUuid(),
			null,
			"SO",
			"11",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER));
		UserDto surfOff12 = creator.createUser(
			rdcf1.region.getUuid(),
			rdcf1.district.getUuid(),
			null,
			"SO",
			"12",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER));
		UserDto survOff21 = creator.createUser(
			rdcf2.region.getUuid(),
			rdcf2.district.getUuid(),
			null,
			"SO",
			"21",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_OFFICER));
		UserDto survSup1 =
			creator.createUser(rdcf1.region.getUuid(), null, null, "SS", "1", creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		UserDto survSup2 =
			creator.createUser(rdcf2.region.getUuid(), null, null, "SS", "2", creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		UserDto commOff1 = creator.createUser(
			rdcf1.region.getUuid(),
			rdcf1.district.getUuid(),
			null,
			"CO",
			"1",
			creator.getUserRoleReference(DefaultUserRole.COMMUNITY_OFFICER));
		commOff1.setCommunity(rdcf1.community);
		getUserFacade().saveUser(commOff1, false);
		UserDto poeSup1 = creator.createUser(
			rdcf1.region.getUuid(),
			rdcf1.district.getUuid(),
			null,
			"PS",
			"1",
			creator.getUserRoleReference(DefaultUserRole.POE_SUPERVISOR));
		poeSup1.setPointOfEntry(rdcf1.pointOfEntry);
		getUserFacade().saveUser(poeSup1, false);

		UserDto envSurv1 = creator.createUser(
			rdcf1.region.getUuid(),
			rdcf1.district.getUuid(),
			null,
			"ES",
			"1",
			creator.getUserRoleReference(DefaultUserRole.ENVIRONMENTAL_SURVEILLANCE_USER));

		assertThat(
			getUserService().getUserRefsByInfrastructure(rdcf1.district.getUuid(), JurisdictionLevel.DISTRICT, JurisdictionLevel.DISTRICT, null),
			hasSize(3));
		assertThat(
			getUserService().getUserRefsByInfrastructure(rdcf1.region.getUuid(), JurisdictionLevel.REGION, JurisdictionLevel.REGION, null),
			hasSize(1));
		assertThat(
			getUserService()
				.getUserRefsByInfrastructure(rdcf1.facility.getUuid(), JurisdictionLevel.HEALTH_FACILITY, JurisdictionLevel.DISTRICT, null),
			hasSize(5));
		assertThat(
			getUserService().getUserRefsByInfrastructure(rdcf2.district.getUuid(), JurisdictionLevel.DISTRICT, JurisdictionLevel.DISTRICT, null),
			hasSize(1));
		assertThat(
			getUserService().getUserRefsByInfrastructure(rdcf2.district.getUuid(), JurisdictionLevel.DISTRICT, JurisdictionLevel.REGION, null),
			hasSize(2));
		assertThat(
			getUserService()
				.getUserRefsByInfrastructure(rdcf1.pointOfEntry.getUuid(), JurisdictionLevel.POINT_OF_ENTRY, JurisdictionLevel.POINT_OF_ENTRY, null),
			hasSize(1));
		assertThat(
			getUserService().getUserRefsByInfrastructure(rdcf1.community.getUuid(), JurisdictionLevel.COMMUNITY, JurisdictionLevel.REGION, null),
			hasSize(5));
		assertThat(getUserService().getUserRefsByInfrastructure(null, JurisdictionLevel.NATION, JurisdictionLevel.NATION, null), hasSize(1));
		assertThat(
			getUserService().getUserRefsByInfrastructure(rdcf1.region.getUuid(), JurisdictionLevel.REGION, JurisdictionLevel.NATION, null),
			hasSize(2));

		commOff1.setLimitedDiseases(Collections.singleton(Disease.EVD));
		getUserFacade().saveUser(commOff1, false);
		survOff11.setLimitedDiseases(Collections.singleton(Disease.CHOLERA));
		getUserFacade().saveUser(survOff11, false);
		assertThat(
			getUserService()
				.getUserRefsByInfrastructure(rdcf1.community.getUuid(), JurisdictionLevel.COMMUNITY, JurisdictionLevel.REGION, Disease.CHOLERA),
			hasSize(4));

		assertThat(
			getUserService().getUserRefsByInfrastructure(
				rdcf1.district.getUuid(),
				JurisdictionLevel.DISTRICT,
				JurisdictionLevel.NATION,
				null,
				UserRight.ENVIRONMENT_EDIT),
			hasSize(2));
	}
}
