package de.symeda.sormas.backend.user;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import de.symeda.sormas.api.user.UserCriteria;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.TestDataCreator.RDCFEntities;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionService;

public class UserFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetUsersByRegionAndRoles() {
		getUserFacade().getUsersByRegionAndRoles(null, UserRole.SURVEILLANCE_OFFICER);
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
		creator.createUser(rdcf, "my", "User", UserRole.CASE_OFFICER, UserRole.CONTACT_OFFICER);

		// some other users to be filtered out
		creator.createUser(rdcf, UserRole.SURVEILLANCE_SUPERVISOR, UserRole.CONTACT_SUPERVISOR);
		creator.createUser(rdcf, UserRole.SURVEILLANCE_OFFICER, UserRole.DISTRICT_OBSERVER);

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
