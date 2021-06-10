package de.symeda.sormas.backend.user;

import static de.symeda.sormas.api.user.UserRole.ADMIN;
import static de.symeda.sormas.api.user.UserRole.CASE_OFFICER;
import static de.symeda.sormas.api.user.UserRole.CASE_SUPERVISOR;
import static de.symeda.sormas.api.user.UserRole.COMMUNITY_INFORMANT;
import static de.symeda.sormas.api.user.UserRole.COMMUNITY_OFFICER;
import static de.symeda.sormas.api.user.UserRole.CONTACT_OFFICER;
import static de.symeda.sormas.api.user.UserRole.CONTACT_SUPERVISOR;
import static de.symeda.sormas.api.user.UserRole.DISTRICT_OBSERVER;
import static de.symeda.sormas.api.user.UserRole.EVENT_OFFICER;
import static de.symeda.sormas.api.user.UserRole.HOSPITAL_INFORMANT;
import static de.symeda.sormas.api.user.UserRole.LAB_USER;
import static de.symeda.sormas.api.user.UserRole.NATIONAL_OBSERVER;
import static de.symeda.sormas.api.user.UserRole.NATIONAL_USER;
import static de.symeda.sormas.api.user.UserRole.POE_INFORMANT;
import static de.symeda.sormas.api.user.UserRole.POE_SUPERVISOR;
import static de.symeda.sormas.api.user.UserRole.STATE_OBSERVER;
import static de.symeda.sormas.api.user.UserRole.SURVEILLANCE_OFFICER;
import static de.symeda.sormas.api.user.UserRole.SURVEILLANCE_SUPERVISOR;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.symeda.sormas.api.AuthProvider;
import de.symeda.sormas.api.HasUuid;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserCriteria;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.TestDataCreator.RDCFEntities;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionService;

public class UserFacadeEjbTest extends AbstractBeanTest {

	@InjectMocks
	private UserFacadeEjb userFacadeEjb;

	@Mock
	private UserService userService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetUsersByRegionAndRoles() {
		getUserFacade().getUsersByRegionAndRoles(null, SURVEILLANCE_OFFICER);
	}

	/**
	 * Testing with some users that might not be selected DISTINCT because of 1:n relations.
	 */
	@Test
	public void testGetIndexList() {

		// 1 region, 2 districts
		RDCFEntities rdcf = creator.createRDCFEntities();
		Region region = getBean(RegionService.class).getByUuid(rdcf.region.getUuid());
		creator.createDistrict("district 2", region);

		// user with a 2 districts region, 2 user roles
		creator.createUser(rdcf, "my", "User", CASE_OFFICER, CONTACT_OFFICER);

		// some other users to be filtered out
		creator.createUser(rdcf, SURVEILLANCE_SUPERVISOR, CONTACT_SUPERVISOR);
		creator.createUser(rdcf, SURVEILLANCE_OFFICER, DISTRICT_OBSERVER);

		List<UserDto> result;

		// 1. Check that query works without filter: All 3 distinct users are found (+ admin by default)
		result = getUserFacade().getIndexList(new UserCriteria(), 0, 100, null);
		assertThat(result, hasSize(4));
		assertThat(result.stream().map(e -> e.getUuid()).collect(Collectors.toSet()), hasSize(4));

		// 2. Check that only the expected user is found
		result = getUserFacade().getIndexList(new UserCriteria().freeText("myUser"), 0, 100, null);
		assertThat(result, hasSize(1));
		assertThat(result.get(0).getUserName(), equalTo("myUser"));
	}

	@Test
	public void testGetUserByRoleAndJurisdiction() {
		UserReferenceDto userAdmin = getUserFacade().getAllUserRefs(false).get(0);

		RDCF rdcf1 = creator.createRDCF("Region1", "District1_1", "Community1_1", "Facility1", "PointOfEntry1");
		RDCF rdcf2 = creator.createRDCF("Region2", "District2", "Community2", "Facility2", "PointOfEntry2");

		RegionReferenceDto region1 = rdcf1.region;
		RegionReferenceDto region2 = rdcf2.region;

		DistrictReferenceDto district1_1 = rdcf1.district;
		DistrictReferenceDto district1_2 =
			new DistrictReferenceDto(creator.createDistrict("District1_2", getBean(RegionService.class).getByUuid(rdcf1.region.getUuid())).getUuid());
		DistrictReferenceDto district2 = rdcf2.district;

		CommunityReferenceDto community1_1 = rdcf1.community;
		CommunityReferenceDto community1_2 = creator.createCommunity("Community1_2", district1_1).toReference();
		CommunityReferenceDto community2 = rdcf2.community;

		FacilityReferenceDto facility1 = rdcf1.facility;
		FacilityReferenceDto facility2 = rdcf2.facility;

		PointOfEntryReferenceDto pointOfEntry1 = rdcf1.pointOfEntry;
		PointOfEntryReferenceDto pointOfEntry2 = rdcf2.pointOfEntry;

		//Jurisdiction Region
		UserDto userR1 = creator.createUser(region1.getUuid(), null, null, null, "Irmin", "Schmidt", EVENT_OFFICER);
		UserDto userR2 = creator.createUser(region2.getUuid(), null, null, null, "Michael", "Karoli", STATE_OBSERVER);

		// Jurisdiction District
		UserDto userD1 = creator.createUser(region1.getUuid(), district1_1.getUuid(), null, null, "Malcolm", "Mooney", CASE_OFFICER);
		UserDto userD2 = creator.createUser(region1.getUuid(), district1_2.getUuid(), null, null, "Rosko", "Gee", SURVEILLANCE_OFFICER);
		UserDto userD3 = creator.createUser(region2.getUuid(), district2.getUuid(), null, null, "Tim", "Hardin", CONTACT_OFFICER);

		// Jurisdiction Community
		UserDto userC1 =
			creator.createUser(region1.getUuid(), district1_1.getUuid(), community1_1.getUuid(), null, "Blixa", "Bargeld", COMMUNITY_INFORMANT);
		UserDto userC2 =
			creator.createUser(region1.getUuid(), district1_1.getUuid(), community1_2.getUuid(), null, "Jaki", "Liebezeit", COMMUNITY_OFFICER);
		UserDto userC3 =
			creator.createUser(region1.getUuid(), district2.getUuid(), community2.getUuid(), null, "Holger", "Czukay", COMMUNITY_INFORMANT);

		// Jurisdiction Nation
		UserDto userN1 = creator.createUser(null, null, null, null, "Fred", "Cole", NATIONAL_USER);
		UserDto userN2 = creator.createUser(null, null, null, null, "Toody", "Cole", NATIONAL_OBSERVER);

		// Jurisdiction Health Facility
		UserDto userHF1 = creator.createUser(null, null, facility1.getUuid(), "FM", "Einheit", HOSPITAL_INFORMANT);

		// Jurisdiction Point of Entry
		UserDto userPOE1 = creator.createPoeUser(pointOfEntry1.getUuid(), "Alexander", "Hacke", POE_INFORMANT);

		// Jurisdiction Laboratory		
		UserDto userLab1 = creator.createLabUser(facility2.getUuid(), "Damo", "Suzuki", LAB_USER);

		// Supervisors
		UserDto userS1 = creator.createUser(region1.getUuid(), null, facility1.getUuid(), "Joey", "Ramone", CASE_SUPERVISOR);
		UserDto userS2 = creator.createUser(region1.getUuid(), null, facility1.getUuid(), "Johnny", "Ramone", CONTACT_SUPERVISOR);
		UserDto userS3 = creator.createUser(region2.getUuid(), null, facility1.getUuid(), "Dee Dee", "Ramone", SURVEILLANCE_SUPERVISOR);
		UserDto userS4 = creator.createUser(region2.getUuid(), null, facility1.getUuid(), "Tommy", "Ramone", POE_SUPERVISOR);

		// Tests
		assertResult(getUserFacade().getUsersByRegionAndRoles(region1, COMMUNITY_OFFICER, SURVEILLANCE_OFFICER), userD2, userC2);
		assertResult(
			getUserFacade()
				.getUsersByRegionsAndRoles(Arrays.asList(region1, region2), COMMUNITY_INFORMANT, CASE_OFFICER, CONTACT_OFFICER, STATE_OBSERVER),
			userR2,
			userD1,
			userD3,
			userC1,
			userC3);

		assertResult(
			getUserFacade().getUserRefsByDistrict(district1_1, false, STATE_OBSERVER, CASE_OFFICER, COMMUNITY_OFFICER, POE_INFORMANT),
			userD1,
			userC2);
		assertResult(
			getUserFacade()
				.getUserRefsByDistricts(Arrays.asList(district1_1, district2), false, SURVEILLANCE_OFFICER, CONTACT_OFFICER, COMMUNITY_INFORMANT),
			userD3,
			userC1,
			userC3);

		assertResult(getUserFacade().getUserRefsByDistrict(district1_1, true, CASE_OFFICER), userD1, userS1, userS2, userS3);

		assertResult(getUserFacade().getUsersWithSuperiorJurisdiction(userN1));
		assertResult(getUserFacade().getUsersWithSuperiorJurisdiction(userR1), userAdmin, userN1, userN2);
		assertResult(getUserFacade().getUsersWithSuperiorJurisdiction(userD1), userR1, userS1, userS2);
		assertResult(getUserFacade().getUsersWithSuperiorJurisdiction(userC1), userD1);

		// Bug? userC2 is in a different community
		assertResult(getUserFacade().getUsersWithSuperiorJurisdiction(userHF1), userD1, userC1, userC2);

		assertEquals(18, getUserFacade().getAllUserRefs(false).size());

		userS4.setActive(false);
		getUserFacade().saveUser(userS4);

		assertEquals(17, getUserFacade().getAllUserRefs(false).size());
		assertEquals(18, getUserFacade().getAllUserRefs(true).size());
	}

	private <T extends HasUuid> void assertResult(List<T> list, HasUuid... elements) {
		Pair<String, Boolean> comparisonResult = containsExactly(list, elements);
		assertTrue(comparisonResult.getElement0(), comparisonResult.getElement1());
	}

	private <T extends HasUuid> Pair<String, Boolean> containsExactly(List<T> list, HasUuid... elements) {
		List<HasUuid> list2 = Arrays.asList(elements);

		if (list == null) {
			return list2.isEmpty() ? new Pair<>("Ok", true) : new Pair<>("Expected a nonempty list, but got null", false);
		}

		List<String> uuids1 = list.stream().map(e -> e.getUuid()).sorted().collect(Collectors.toList());
		List<String> uuids2 = list2.stream().map(e -> e.getUuid()).sorted().collect(Collectors.toList());

		String failMessage = "Expected [" + StringUtils.join(uuids2, ", ") + "] but got [" + StringUtils.join(uuids1, ", ") + "]";

		if (uuids1.size() != uuids2.size()) {
			return new Pair<>(failMessage, false);
		}

		for (int i = 0; i < list.size(); i++) {
			if (!uuids1.get(i).equals(uuids2.get(i))) {
				return new Pair<>(failMessage, false);
			}
		}
		return new Pair<>("Ok", true);
	}

	/**
	 * Test that the filtering on text fields and sorting be Location.address do not cause a database exception.
	 */
	@Test
	public void testGetIndexListFilteredAndOrderedByAddress() {

		List<UserDto> result =
			getUserFacade().getIndexList(new UserCriteria().freeText("min"), 0, 100, Collections.singletonList(new SortProperty(UserDto.ADDRESS)));

		assertThat(result, hasSize(1));
		assertThat(result.get(0).getUserName(), equalTo("admin"));
	}

	@Test
	public void testGetValidLoginRoles() {

		AuthProvider authProvider = mock(AuthProvider.class);

		MockedStatic<AuthProvider> mockAuthProvider = mockStatic(AuthProvider.class);
		mockAuthProvider.when(AuthProvider::getProvider).thenReturn(authProvider);
		when(authProvider.isUsernameCaseSensitive()).thenReturn(true);

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createUser(rdcf, SURVEILLANCE_SUPERVISOR);
		String password = getUserFacade().resetPassword(user.getUuid());

		Set<UserRole> validLoginRoles = getUserFacade().getValidLoginRoles(user.getUserName(), password);
		assertThat(validLoginRoles, containsInAnyOrder(SURVEILLANCE_SUPERVISOR));

		user.setActive(false);
		getUserFacade().saveUser(user);

		validLoginRoles = getUserFacade().getValidLoginRoles(user.getUserName(), password);
		assertThat(validLoginRoles, nullValue());

		//Important: release static mock.
		mockAuthProvider.closeOnDemand();
	}

	@Test
	public void testGetExistentDefaultUsers() {
		Set<User> defaultUsers = UserTestHelper.generateDefaultUsers(true);
		Set<User> randomUsers = UserTestHelper.generateRandomUsers(30);

		List<User> testUsers = new ArrayList<>();
		testUsers.addAll(defaultUsers);
		testUsers.addAll(randomUsers);

		for (User user : testUsers) {
			Mockito.when(userService.getCurrentUser()).thenReturn(user);
			Mockito.when(userService.getAllDefaultUsers()).thenReturn(Arrays.asList(user));
			if (defaultUsers.contains(user)) {
				assertEquals(1, userFacadeEjb.getUsersWithDefaultPassword().size());
				assertEquals(UserFacadeEjb.toDto(user), userFacadeEjb.getUsersWithDefaultPassword().get(0));
			} else {
				assertEquals(0, userFacadeEjb.getUsersWithDefaultPassword().size());
			}

			Mockito.when(userService.getAllDefaultUsers()).thenReturn(testUsers);
			if (user.hasAnyUserRole(ADMIN)) {
				assertEquals(defaultUsers.size(), userFacadeEjb.getUsersWithDefaultPassword().size());
				for (User defUser : defaultUsers) {
					assertTrue(userFacadeEjb.getUsersWithDefaultPassword().contains(UserFacadeEjb.toDto(defUser)));
				}
				for (User randomUser : randomUsers) {
					assertFalse(userFacadeEjb.getUsersWithDefaultPassword().contains(UserFacadeEjb.toDto(randomUser)));
				}

			} else if (defaultUsers.contains(user)) {
				assertEquals(1, userFacadeEjb.getUsersWithDefaultPassword().size());
				assertEquals(UserFacadeEjb.toDto(user), userFacadeEjb.getUsersWithDefaultPassword().get(0));
			} else {
				assertEquals(0, userFacadeEjb.getUsersWithDefaultPassword().size());
			}
		}
	}

	@Test
	public void testGetExistentDefaultUsersUpperCase() {
		Set<User> defaultUsers = UserTestHelper.generateDefaultUsers(true);
		for (User user : defaultUsers) {
			user.setUserName(user.getUserName().toUpperCase());
			Mockito.when(userService.getCurrentUser()).thenReturn(user);
			Mockito.when(userService.getAllDefaultUsers()).thenReturn(Arrays.asList(user));
			assertEquals(0, userFacadeEjb.getUsersWithDefaultPassword().size());
		}

	}
}
