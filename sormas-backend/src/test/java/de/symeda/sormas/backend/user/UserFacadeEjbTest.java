package de.symeda.sormas.backend.user;

import static de.symeda.sormas.api.user.DefaultUserRole.ADMIN_SUPERVISOR;
import static de.symeda.sormas.api.user.DefaultUserRole.CASE_OFFICER;
import static de.symeda.sormas.api.user.DefaultUserRole.CONTACT_OFFICER;
import static de.symeda.sormas.api.user.DefaultUserRole.CONTACT_SUPERVISOR;
import static de.symeda.sormas.api.user.DefaultUserRole.DISTRICT_OBSERVER;
import static de.symeda.sormas.api.user.DefaultUserRole.NATIONAL_USER;
import static de.symeda.sormas.api.user.DefaultUserRole.POE_INFORMANT;
import static de.symeda.sormas.api.user.DefaultUserRole.REST_EXTERNAL_VISITS_USER;
import static de.symeda.sormas.api.user.DefaultUserRole.SURVEILLANCE_OFFICER;
import static de.symeda.sormas.api.user.DefaultUserRole.SURVEILLANCE_SUPERVISOR;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.symeda.sormas.api.AuthProvider;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserCriteria;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserFacade;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.region.RegionFacadeEjb;
import de.symeda.sormas.backend.infrastructure.region.RegionService;

public class UserFacadeEjbTest extends AbstractBeanTest {

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetUsersByRegionAndRights() {
		RDCF rdcf = creator.createRDCF();
		RegionReferenceDto region = rdcf.region;

		// given region and right
		List<UserReferenceDto> result = getUserFacade().getUsersByRegionAndRights(region, null, UserRight.EXTERNAL_MESSAGE_VIEW);

		assertTrue(result.isEmpty());

		UserDto natUser = creator.createUser(rdcf, creator.getUserRoleReference(NATIONAL_USER)); // Has LAB_MASSAGES and TRAVEL_ENTRY_MANAGEMENT_ACCESS rights
		UserDto poeUser = creator.createUser(rdcf, "Some", "User", creator.getUserRoleReference(POE_INFORMANT)); // Does not have LAB_MASSAGES right, but has TRAVEL_ENTRY_MANAGEMENT_ACCESS.
		creator.createUser(rdcf, creator.getUserRoleReference(REST_EXTERNAL_VISITS_USER)); // Has neither LAB_MASSAGES nor TRAVEL_ENTRY_MANAGEMENT_ACCESS right
		result = getUserFacade().getUsersByRegionAndRights(region, null, UserRight.EXTERNAL_MESSAGE_VIEW);

		assertThat(result, hasSize(1));
		assertThat(result, contains(equalTo(natUser.toReference())));

		UserDto natUser2 = creator.createUser(rdcf, "Nat", "User2", creator.getUserRoleReference(NATIONAL_USER)); // Has LAB_MASSAGES right
		result = getUserFacade().getUsersByRegionAndRights(region, null, UserRight.EXTERNAL_MESSAGE_VIEW);

		assertThat(result, hasSize(2));
		assertThat(result, hasItems(equalTo(natUser.toReference()), equalTo(natUser2.toReference())));

		// given different region and right
		Region region2 = creator.createRegion("region2");
		result = getUserFacade().getUsersByRegionAndRights(RegionFacadeEjb.toReferenceDto(region2), null, UserRight.EXTERNAL_MESSAGE_VIEW);

		assertTrue(result.isEmpty());

		// given no region and right
		result = getUserFacade().getUsersByRegionAndRights(null, null, UserRight.EXTERNAL_MESSAGE_VIEW);

		assertThat(result, hasSize(3)); // there is an admin user with NATIONAL_USER role created at the init of the AbstractBeanTest
		assertThat(result, hasItems(equalTo(natUser.toReference()), equalTo(natUser2.toReference())));

		// given region and multiple rights
		result = getUserFacade().getUsersByRegionAndRights(region, null, UserRight.EXTERNAL_MESSAGE_VIEW, UserRight.TRAVEL_ENTRY_MANAGEMENT_ACCESS);

		assertThat(result, hasSize(3));
		assertThat(result, hasItems(equalTo(natUser.toReference()), equalTo(natUser2.toReference()), equalTo(poeUser.toReference())));

		// given different region and multiple rights
		result = getUserFacade().getUsersByRegionAndRights(
			RegionFacadeEjb.toReferenceDto(region2),
			null,
			UserRight.EXTERNAL_MESSAGE_VIEW,
			UserRight.TRAVEL_ENTRY_MANAGEMENT_ACCESS);

		assertTrue(result.isEmpty());

		// given no region and multiple rights
		result = getUserFacade().getUsersByRegionAndRights(null, null, UserRight.EXTERNAL_MESSAGE_VIEW, UserRight.TRAVEL_ENTRY_MANAGEMENT_ACCESS);

		assertThat(result, hasSize(4)); // there is an admin user with NATIONAL_USER role created at the init of the AbstractBeanTest
		assertThat(result, hasItems(equalTo(natUser.toReference()), equalTo(natUser2.toReference()), equalTo(poeUser.toReference())));

	}

	/**
	 * Testing with some users that might not be selected DISTINCT because of 1:n relations.
	 */
	@Test
	public void testGetIndexList() {

		// 1 region, 2 districts
		RDCF rdcf = creator.createRDCF();
		Region region = getBean(RegionService.class).getByUuid(rdcf.region.getUuid());
		creator.createDistrict("district 2", region);

		// user with a 2 districts region, 2 user roles
		creator.createUser(rdcf, "my", "User", creator.getUserRoleReference(CASE_OFFICER), creator.getUserRoleReference(CONTACT_OFFICER));

		// some other users to be filtered out
		creator.createUser(
			rdcf,
			"Some",
			"User",
			creator.getUserRoleReference(SURVEILLANCE_SUPERVISOR),
			creator.getUserRoleReference(CONTACT_SUPERVISOR));
		creator
			.createUser(rdcf, "Other", "User", creator.getUserRoleReference(SURVEILLANCE_OFFICER), creator.getUserRoleReference(DISTRICT_OBSERVER));

		List<UserDto> result;

		// 1. Check that query works without filter: All 3 distinct users are found (+ admin by default)
		result = getUserFacade().getIndexList(new UserCriteria(), 0, 100, null);
		assertThat(result, hasSize(4));
		assertThat(result.stream().map(EntityDto::getUuid).collect(Collectors.toSet()), hasSize(4));

		// 2. Check that only the expected user is found
		result = getUserFacade().getIndexList(new UserCriteria().freeText("myUser"), 0, 100, null);
		assertThat(result, hasSize(1));
		assertThat(result.get(0).getUserName(), equalTo("myUser"));
	}

	/**
	 * Test that the filtering on text fields and sorting be Location.address do not cause a database exception.
	 */
	@Test
	public void testGetIndexListFilteredAndOrderedByAddress() {
		final UserCriteria userCriteria = new UserCriteria().freeText("min");
		List<UserDto> result = getUserFacade().getIndexList(userCriteria, 0, 100, Collections.singletonList(new SortProperty(UserDto.ADDRESS)));

		final long count = getUserFacade().count(userCriteria);

		assertThat(result, hasSize(1));
		assertThat(count, is(1L));
		assertThat(result.get(0).getUserName(), equalTo("admin"));
	}

	@Test
	public void testGetValidLoginRoles() {
		AuthProvider authProvider = mock(AuthProvider.class);

		MockedStatic<AuthProvider> mockAuthProvider = mockStatic(AuthProvider.class);
		Mockito.when(AuthProvider.getProvider(any())).thenReturn(authProvider);

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, creator.getUserRoleReference(SURVEILLANCE_SUPERVISOR));
		String password = getUserFacade().resetPassword(user.getUuid());

		Set<UserRight> validLoginRights = getUserFacade().getValidLoginRights(user.getUserName(), password);
		assertThat(
			validLoginRights,
			containsInAnyOrder(
				UserRole.getUserRights(Collections.singletonList(creator.getUserRole(SURVEILLANCE_SUPERVISOR))).toArray(new UserRight[] {})));

		user.setActive(false);
		getUserFacade().saveUser(user, false);

		validLoginRights = getUserFacade().getValidLoginRights(user.getUserName(), password);
		assertThat(validLoginRights, nullValue());

		//Important: release static mock.
		mockAuthProvider.closeOnDemand();
	}

	@Test
	public void testGetExistentDefaultUsers() {
		Set<User> defaultUsers = UserTestHelper.generateDefaultUsers(true, creator);
		Set<User> randomUsers = UserTestHelper.generateRandomUsers(3, creator);

		List<User> testUsers = new ArrayList<>();
		testUsers.addAll(defaultUsers);
		testUsers.addAll(randomUsers);

		for (User user : testUsers) {
			getUserService().persist(user);
		}

		for (User user : testUsers) {
			loginWith(UserFacadeEjb.toDto(user));
			List<UserDto> persistedDefaultUsers = getUserFacade().getUsersWithDefaultPassword();

			if (user.hasUserRight(UserRight.USER_EDIT)) {
				assertEquals(defaultUsers.size(), persistedDefaultUsers.size());
				for (User defUser : defaultUsers) {
					assertThat(persistedDefaultUsers, hasItem(UserFacadeEjb.toDto(defUser)));
				}
				for (User randomUser : randomUsers) {
					assertThat(persistedDefaultUsers, not(hasItem(UserFacadeEjb.toDto(randomUser))));
				}
			} else if (defaultUsers.contains(user)) {
				assertEquals(1, persistedDefaultUsers.size());
				assertEquals(UserFacadeEjb.toDto(user), persistedDefaultUsers.get(0));
			} else {
				assertEquals(0, persistedDefaultUsers.size());
			}
		}
	}

	@Test
	public void testGetExistentDefaultUsersUpperCase() {
		Set<User> defaultUsers = UserTestHelper.generateDefaultUsers(true, creator);
		for (User user : defaultUsers) {
			user.setUserName(user.getUserName().toUpperCase());
			getUserService().persist(user);
		}
		assertEquals(0, getUserFacade().getUsersWithDefaultPassword().size());
	}

	@Test
	public void testGetByUserName() {
		final UserFacade userFacade = getUserFacade();
		assertNull(userFacade.getByUserName("HansPeter"));

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, "Hans", "Peter", creator.getUserRoleReference(SURVEILLANCE_OFFICER));

		assertEquals(user, userFacade.getByUserName("HANSPETER"));
		assertEquals(user, userFacade.getByUserName("hanspeter"));
		assertEquals(user, userFacade.getByUserName("HansPeter"));
		assertEquals(user, userFacade.getByUserName("hansPETER"));
	}

	@Test
	public void testLoginUnique() {
		final UserFacade userFacade = getUserFacade();
		assertNull(userFacade.getByUserName("HansPeter"));

		RDCF rdcf = creator.createRDCF();
		creator.createUser(rdcf, "Hans", "Peter", creator.getUserRoleReference(SURVEILLANCE_OFFICER));

		assertTrue(userFacade.isLoginUnique(String.valueOf(UUID.randomUUID()), "MarieLisa"));
		assertFalse(userFacade.isLoginUnique(String.valueOf(UUID.randomUUID()), "HansPeter"));
		assertFalse(userFacade.isLoginUnique(String.valueOf(UUID.randomUUID()), "hanspeter"));
	}

	@Test
	public void testFailOnSavingDuplicateUser() {
		final UserFacade userFacade = getUserFacade();
		assertNull(userFacade.getByUserName("HansPeter"));

		RDCF rdcf = creator.createRDCF();
		creator.createUser(rdcf, "Hans", "Peter", creator.getUserRoleReference(SURVEILLANCE_OFFICER));
		assertThrows(ValidationException.class, () -> creator.createUser(rdcf, "Hans", "Peter", creator.getUserRoleReference(SURVEILLANCE_OFFICER)));
		assertThrows(ValidationException.class, () -> creator.createUser(rdcf, "hans", "peter", creator.getUserRoleReference(SURVEILLANCE_OFFICER)));
		assertThrows(ValidationException.class, () -> creator.createUser(rdcf, "HANS", "PETER", creator.getUserRoleReference(SURVEILLANCE_OFFICER)));
	}

	@Test
	public void testGetUserRefsByDistrictsWithLimitedDiseaseUsers() {

		RDCF rdcf = creator.createRDCF();

		UserDto generalSurveillanceOfficer = creator.createSurveillanceOfficer(rdcf);
		UserDto limitedSurveillanceOfficer =
			creator.createUser(rdcf, "Limited Dengue", "SURVEILLANCE_OFFICER", Disease.DENGUE, creator.getUserRoleReference(SURVEILLANCE_OFFICER));

		List<UserReferenceDto> userReferenceDtos = getUserFacade().getUserRefsByDistricts(Arrays.asList(rdcf.district), Disease.CORONAVIRUS);

		assertNotNull(userReferenceDtos);
		assertTrue(userReferenceDtos.contains(generalSurveillanceOfficer));
		assertFalse(userReferenceDtos.contains(limitedSurveillanceOfficer));

	}

	@Test
	public void testGetUserRefsByDistrictsForResponsibleSurveillanceOfficer() {
		RDCF rdcf = creator.createRDCF();
		RDCF rdcfOther = creator.createRDCF("otherRegion", "oderDistrict", "otherCommunity", "otherFacility");

		UserDto surveilanceOfficerDefault = creator.createSurveillanceOfficer(rdcf);
		UserDto surveilanceSupervisorDefault = creator.createSurveillanceSupervisor(rdcf);
		UserDto adminSupervisorDefault = creator.createUser(rdcf, "Admin", "Supervisor", creator.getUserRoleReference(ADMIN_SUPERVISOR));
		UserDto adminSupervisorOther = creator.createUser(rdcfOther, "Admin", "Supervisor Other", creator.getUserRoleReference(ADMIN_SUPERVISOR));

		List<UserReferenceDto> userReferenceDtos = getUserFacade().getUserRefsByDistricts(Arrays.asList(rdcf.district), Disease.CORONAVIRUS);
		assertNotNull(userReferenceDtos);
		assertEquals(3, userReferenceDtos.size());
		List<String> userReferenceUUIDs = userReferenceDtos.stream().map(u -> u.getUuid()).collect(Collectors.toList());
		assertTrue(userReferenceUUIDs.contains(surveilanceOfficerDefault.getUuid()));
		assertTrue(userReferenceUUIDs.contains(surveilanceSupervisorDefault.getUuid()));
		assertTrue(userReferenceUUIDs.contains(adminSupervisorDefault.getUuid()));
		assertFalse(userReferenceUUIDs.contains(adminSupervisorOther.getUuid()));
	}

	@Test
	public void testGetUserRefsByDistrictsWithLimitedDiseaseUsersSingleDistrict() {

		RDCF rdcf = creator.createRDCF();

		UserDto generalSurveillanceOfficer = creator.createSurveillanceOfficer(rdcf); // has TRAVEL_ENTRY_MANAGEMENT_ACCESS, but not the EXTERNAL_MESSAGES right
		UserDto limitedSurveillanceOfficer =
			creator.createUser(rdcf, "Limited Dengue", "SURVEILLANCE_OFFICER", Disease.DENGUE, creator.getUserRoleReference(SURVEILLANCE_OFFICER)); // has TRAVEL_ENTRY_MANAGEMENT_ACCESS, but not the EXTERNAL_MESSAGES right

		// given district and disease
		List<UserReferenceDto> userReferenceDtos = getUserFacade().getUserRefsByDistrict(rdcf.district, Disease.CORONAVIRUS);

		assertThat(userReferenceDtos, hasSize(1));
		assertTrue(userReferenceDtos.contains(generalSurveillanceOfficer));

		// given disease
		userReferenceDtos = getUserFacade().getUserRefsByDistrict(null, Disease.CORONAVIRUS);

		assertThat(userReferenceDtos, hasSize(2)); // there is an admin user created at the init of the AbstractBeanTest
		assertTrue(userReferenceDtos.contains(generalSurveillanceOfficer));

		// given only null parameters
		userReferenceDtos = getUserFacade().getUserRefsByDistrict(null, null);

		assertThat(userReferenceDtos, hasSize(3)); // there is an admin user created at the init of the AbstractBeanTest
		assertThat(userReferenceDtos, hasItems(equalTo(generalSurveillanceOfficer.toReference()), equalTo(limitedSurveillanceOfficer.toReference())));

		// given district, disease and right
		userReferenceDtos = getUserFacade().getUserRefsByDistrict(rdcf.district, Disease.CORONAVIRUS, UserRight.TRAVEL_ENTRY_MANAGEMENT_ACCESS);

		assertThat(userReferenceDtos, hasSize(1));
		assertTrue(userReferenceDtos.contains(generalSurveillanceOfficer));

		userReferenceDtos = getUserFacade().getUserRefsByDistrict(rdcf.district, Disease.CORONAVIRUS, UserRight.EXTERNAL_MESSAGE_VIEW);

		assertTrue(userReferenceDtos.isEmpty());

		// given disease and right
		userReferenceDtos = getUserFacade().getUserRefsByDistrict(null, Disease.CORONAVIRUS, UserRight.TRAVEL_ENTRY_MANAGEMENT_ACCESS);

		assertThat(userReferenceDtos, hasSize(2)); // there is an admin user with NATIONAL_USER role created at the init of the AbstractBeanTest
		assertTrue(userReferenceDtos.contains(generalSurveillanceOfficer));

		userReferenceDtos = getUserFacade().getUserRefsByDistrict(null, Disease.CORONAVIRUS, UserRight.EXTERNAL_MESSAGE_VIEW);

		assertThat(userReferenceDtos, hasSize(1)); // there is an admin user with NATIONAL_USER role created at the init of the AbstractBeanTest

		// given only null parameters except for right
		userReferenceDtos = getUserFacade().getUserRefsByDistrict(null, null, UserRight.TRAVEL_ENTRY_MANAGEMENT_ACCESS);

		assertThat(userReferenceDtos, hasSize(3)); // there is an admin user with NATIONAL_USER role created at the init of the AbstractBeanTest
		assertThat(userReferenceDtos, hasItems(equalTo(generalSurveillanceOfficer.toReference()), equalTo(limitedSurveillanceOfficer.toReference())));

		userReferenceDtos = getUserFacade().getUserRefsByDistrict(null, null, UserRight.EXTERNAL_MESSAGE_VIEW);

		assertThat(userReferenceDtos, hasSize(1)); // there is an admin user with NATIONAL_USER role created at the init of the AbstractBeanTest

	}

	@Test
	public void testGetUserRefsByDistrictsWithExcludeLimitedDiseaseUsersAndSingleDistrict() {

		RDCF rdcf = creator.createRDCF();

		UserDto generalSurveillanceOfficer = creator.createSurveillanceOfficer(rdcf); // has TRAVEL_ENTRY_MANAGEMENT_ACCESS
		UserDto limitedSurveillanceOfficer =
			creator.createUser(rdcf, "Limited Dengue", "SURVEILLANCE_OFFICER", Disease.DENGUE, creator.getUserRoleReference(SURVEILLANCE_OFFICER)); // has TRAVEL_ENTRY_MANAGEMENT_ACCESS
		UserDto generalRestUser = creator.createUser(rdcf, "REST", "USER", creator.getUserRoleReference(REST_EXTERNAL_VISITS_USER)); // does not have TRAVEL_ENTRY_MANAGEMENT_ACCESS

		// given district and one right
		List<UserReferenceDto> userReferenceDtos =
			getUserFacade().getUserRefsByDistrict(rdcf.district, true, UserRight.TRAVEL_ENTRY_MANAGEMENT_ACCESS);

		assertThat(userReferenceDtos, hasSize(1));
		assertTrue(userReferenceDtos.contains(generalSurveillanceOfficer));

		userReferenceDtos = getUserFacade().getUserRefsByDistrict(rdcf.district, false, UserRight.TRAVEL_ENTRY_MANAGEMENT_ACCESS);

		assertThat(userReferenceDtos, hasSize(2));
		assertThat(userReferenceDtos, hasItems(equalTo(generalSurveillanceOfficer.toReference()), equalTo(limitedSurveillanceOfficer.toReference())));

		// given no district and one right
		userReferenceDtos = getUserFacade().getUserRefsByDistrict(null, true, UserRight.TRAVEL_ENTRY_MANAGEMENT_ACCESS);

		assertThat(userReferenceDtos, hasSize(2)); // there is an admin user created at the init of the AbstractBeanTest
		assertTrue(userReferenceDtos.contains(generalSurveillanceOfficer));

		userReferenceDtos = getUserFacade().getUserRefsByDistrict(null, false, UserRight.TRAVEL_ENTRY_MANAGEMENT_ACCESS);

		assertThat(userReferenceDtos, hasSize(3)); // there is an admin user created at the init of the AbstractBeanTest
		assertThat(userReferenceDtos, hasItems(equalTo(generalSurveillanceOfficer.toReference()), equalTo(limitedSurveillanceOfficer.toReference())));

		// given district and multiple rights
		userReferenceDtos =
			getUserFacade().getUserRefsByDistrict(rdcf.district, true, UserRight.TRAVEL_ENTRY_MANAGEMENT_ACCESS, UserRight.SORMAS_REST);

		assertThat(userReferenceDtos, hasSize(2));
		assertThat(userReferenceDtos, hasItems(equalTo(generalSurveillanceOfficer.toReference()), equalTo(generalRestUser.toReference())));

		userReferenceDtos =
			getUserFacade().getUserRefsByDistrict(rdcf.district, false, UserRight.TRAVEL_ENTRY_MANAGEMENT_ACCESS, UserRight.SORMAS_REST);

		assertThat(userReferenceDtos, hasSize(3));
		assertThat(
			userReferenceDtos,
			hasItems(
				equalTo(generalSurveillanceOfficer.toReference()),
				equalTo(limitedSurveillanceOfficer.toReference()),
				equalTo(generalRestUser.toReference())));

		// given no district and multiple rights
		userReferenceDtos = getUserFacade().getUserRefsByDistrict(null, true, UserRight.TRAVEL_ENTRY_MANAGEMENT_ACCESS, UserRight.SORMAS_REST);

		assertThat(userReferenceDtos, hasSize(3)); // there is an admin user created at the init of the AbstractBeanTest
		assertThat(userReferenceDtos, hasItems(equalTo(generalSurveillanceOfficer.toReference()), equalTo(generalRestUser.toReference())));

		userReferenceDtos = getUserFacade().getUserRefsByDistrict(null, false, UserRight.TRAVEL_ENTRY_MANAGEMENT_ACCESS, UserRight.SORMAS_REST);

		assertThat(userReferenceDtos, hasSize(4)); // there is an admin user created at the init of the AbstractBeanTest
		assertThat(
			userReferenceDtos,
			hasItems(
				equalTo(generalSurveillanceOfficer.toReference()),
				equalTo(limitedSurveillanceOfficer.toReference()),
				equalTo(generalRestUser.toReference())));

	}

	@Test
	void getUserRights() {

		RDCF rdcf = creator.createRDCF();
		UserDto userWithoutAccess = creator.createSurveillanceOfficer(rdcf);
		UserRight[] surveillanceOfficerRights =
			UserRole.getUserRights(Collections.singletonList(creator.getUserRole(SURVEILLANCE_OFFICER))).toArray(new UserRight[] {});

		// Successfully retrieve user rights of other user
		loginWith(nationalAdmin);
		List<UserRight> userRights = getUserFacade().getUserRights(userWithoutAccess.getUuid());
		assertThat(userRights, containsInAnyOrder(surveillanceOfficerRights));
		// Successfully retrieve own user rights with user without access to the USER_VIEW and USERROLE_VIEW rights
		loginWith(userWithoutAccess);
		userRights = getUserFacade().getUserRights(null);
		assertThat(userRights, containsInAnyOrder(surveillanceOfficerRights));
		// Prevent users without access from retrieving user rights of other users
		assertThrows(AccessDeniedException.class, () -> getUserFacade().getUserRights(nationalAdmin.getUuid()));

		// Uuid which does not exist in the system
		loginWith(nationalAdmin);
		assertThrows(EntityNotFoundException.class, () -> getUserFacade().getUserRights("12345"));
	}
}
