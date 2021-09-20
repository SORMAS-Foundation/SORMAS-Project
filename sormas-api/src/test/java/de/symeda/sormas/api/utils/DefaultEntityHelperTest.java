/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;

import de.symeda.sormas.api.user.UserDto;

public class DefaultEntityHelperTest {

	private static final String DEFAULT_ADMIN_USERNAME = "admin";
	private static final String DEFAULT_ADMIN_PASS = "sadmin";
	private static final String DEFAULT_SURV_SUP_USER_PASS = "SurvSup";
	private static final String DEFAULT_CASE_SUP_USER_PASS = "CaseSup";
	private static final String DEFAULT_CONT_SUP_USER_PASS = "ContSup";
	private static final String DEFAULT_POE_SUP_USER_PASS = "PoeSup";
	private static final String DEFAULT_LAB_OFF_USER_PASS = "LabOff";
	private static final String DEFAULT_EVE_OFF_USER_PASS = "EveOff";
	private static final String DEFAULT_NAT_USER_USER_PASS = "NatUser";
	private static final String DEFAULT_NAT_CLIN_USER_PASS = "NatClin";
	private static final String DEFAULT_SURV_OFF_USER_PASS = "SurvOff";
	private static final String DEFAULT_HOSP_INF_USER_PASS = "HospInf";
	private static final String DEFAULT_COMM_OFF_USER_PASS = "CommOff";
	private static final String DEFAULT_POE_INF_USER_PASS = "PoeInf";

	private static final Map<String, String> DEFAULT_USERS = new HashMap<String, String>() {

		{
			put(DEFAULT_ADMIN_USERNAME, DEFAULT_ADMIN_PASS);
			put(DEFAULT_SURV_SUP_USER_PASS, DEFAULT_SURV_SUP_USER_PASS);
			put(DEFAULT_CASE_SUP_USER_PASS, DEFAULT_CASE_SUP_USER_PASS);
			put(DEFAULT_CONT_SUP_USER_PASS, DEFAULT_CONT_SUP_USER_PASS);
			put(DEFAULT_POE_SUP_USER_PASS, DEFAULT_POE_SUP_USER_PASS);
			put(DEFAULT_LAB_OFF_USER_PASS, DEFAULT_LAB_OFF_USER_PASS);
			put(DEFAULT_EVE_OFF_USER_PASS, DEFAULT_EVE_OFF_USER_PASS);
			put(DEFAULT_NAT_USER_USER_PASS, DEFAULT_NAT_USER_USER_PASS);
			put(DEFAULT_NAT_CLIN_USER_PASS, DEFAULT_NAT_CLIN_USER_PASS);
			put(DEFAULT_SURV_OFF_USER_PASS, DEFAULT_SURV_OFF_USER_PASS);
			put(DEFAULT_HOSP_INF_USER_PASS, DEFAULT_HOSP_INF_USER_PASS);
			put(DEFAULT_COMM_OFF_USER_PASS, DEFAULT_COMM_OFF_USER_PASS);
			put(DEFAULT_POE_INF_USER_PASS, DEFAULT_POE_INF_USER_PASS);
		}
	};

	@Test
	public void testIsDefaultUser() {
		for (String defaultUser : DEFAULT_USERS.keySet()) {
			assertTrue(DefaultEntityHelper.isDefaultUser(defaultUser));
		}
	}

	@Test
	public void testGetDefaultPassword() {
		for (String defaultUser : DEFAULT_USERS.keySet()) {
			assertEquals(DEFAULT_USERS.get(defaultUser), DefaultEntityHelper.getDefaultPassword(defaultUser));
		}
	}

	private void testUsesDefaultPasswordHelper(String username, String defaultPassword) {
		String seed = UUID.randomUUID().toString();
		String randomPass = UUID.randomUUID().toString();
		assertTrue(DefaultEntityHelper.usesDefaultPassword(username, PasswordHelper.encodePassword(defaultPassword, seed), seed));
		assertFalse(DefaultEntityHelper.usesDefaultPassword(username, PasswordHelper.encodePassword(randomPass, seed), seed));
	}

	@Test
	public void testUsesDefaultPassword() {
		for (String defaultUser : DEFAULT_USERS.keySet()) {
			testUsesDefaultPasswordHelper(defaultUser, DEFAULT_USERS.get(defaultUser));
		}
	}

	@Test
	public void testCurrentUserUsesDefaultPassword() {
		List<UserDto> defaultDtos = new ArrayList<>();
		UserDto admin = new UserDto();
		admin.setUserName(DEFAULT_ADMIN_USERNAME);
		defaultDtos.add(admin);
		UserDto randomUser = new UserDto();
		randomUser.setUserName(UUID.randomUUID().toString());

		assertTrue(DefaultEntityHelper.currentUserUsesDefaultPassword(defaultDtos, admin));
		assertFalse(DefaultEntityHelper.currentUserUsesDefaultPassword(defaultDtos, randomUser));
		defaultDtos.remove(admin);
		assertFalse(DefaultEntityHelper.currentUserUsesDefaultPassword(defaultDtos, admin));
	}

	@Test
	public void testOtherUsersUseDefaultPassword() {
		List<UserDto> defaultDtos = new ArrayList<>();
		UserDto admin = new UserDto();
		admin.setUserName(DEFAULT_ADMIN_USERNAME);
		defaultDtos.add(admin);
		UserDto randomUser = new UserDto();
		randomUser.setUserName(UUID.randomUUID().toString());

		assertTrue(DefaultEntityHelper.otherUsersUseDefaultPassword(defaultDtos, randomUser));
		assertFalse(DefaultEntityHelper.otherUsersUseDefaultPassword(defaultDtos, admin));
		defaultDtos.add(randomUser);
		assertTrue(DefaultEntityHelper.otherUsersUseDefaultPassword(defaultDtos, admin));
		assertTrue(DefaultEntityHelper.otherUsersUseDefaultPassword(defaultDtos, randomUser));
		assertFalse(DefaultEntityHelper.otherUsersUseDefaultPassword(new ArrayList<UserDto>(), admin));
	}

	@Test
	public void getDefaultUserNames() {
		assertEquals(DEFAULT_USERS.size(), DefaultEntityHelper.getDefaultUserNames().size());
		Set<String> result = DefaultEntityHelper.getDefaultUserNames();
		for (String defaultUser : DEFAULT_USERS.keySet()) {
			assertTrue(result.contains(defaultUser));
		}
	}
}
