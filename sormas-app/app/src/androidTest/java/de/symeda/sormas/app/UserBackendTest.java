/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.user.User;

/**
 * Created by Mate Strysewske on 16.06.2017.
 */
@RunWith(AndroidJUnit4.class)
public class UserBackendTest {

	@Rule
	public final ActivityTestRule<TestBackendActivity> testActivityRule = new ActivityTestRule<>(TestBackendActivity.class, false, true);

	@Before
	public void initTest() {
		TestHelper.initTestEnvironment(false);
	}

	@Test
	public void shouldGetByDistrictAndRole() {
		assertThat(DatabaseHelper.getUserDao().queryForAll().size(), is(3));

		District district = DatabaseHelper.getDistrictDao().queryUuid(TestHelper.DISTRICT_UUID);

		assertThat(DatabaseHelper.getUserDao().getByDistrictAndRole(district, UserRole.SURVEILLANCE_OFFICER, User.FIRST_NAME).size(), is(1));
		List<User> informants = DatabaseHelper.getUserDao().getByDistrictAndRole(district, UserRole.HOSPITAL_INFORMANT, User.FIRST_NAME);
		assertThat(informants.size(), is(1));
		assertThat(informants.get(0).getUserRoles(), contains(UserRole.HOSPITAL_INFORMANT));
	}
}
