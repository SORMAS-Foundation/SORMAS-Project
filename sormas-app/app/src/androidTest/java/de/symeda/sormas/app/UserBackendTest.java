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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;

import java.sql.SQLException;
import java.util.Date;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserRole;

/**
 * Created by Mate Strysewske on 16.06.2017.
 */
@RunWith(AndroidJUnit4.class)
public class UserBackendTest {

	private static final String SECOND_REGION_UUID = "R2R2R2-R2R2R2-R2R2R2-R2R2R2R2";

	@Rule
	public final ActivityTestRule<TestBackendActivity> testActivityRule = new ActivityTestRule<>(TestBackendActivity.class, false, true);

	@Before
	public void initTest() throws SQLException {
		TestHelper.initTestEnvironment(false);

		Region secondRegion = new Region();
		secondRegion.setCreationDate(new Date());
		secondRegion.setChangeDate(new Date());
		secondRegion.setName("Second Region");
		secondRegion.setUuid(SECOND_REGION_UUID);
		DatabaseHelper.getRegionDao().create(secondRegion);
	}

	@Test
	public void testGetRandomRegionUser() {

		Region firstRegion = DatabaseHelper.getRegionDao().queryUuid(TestHelper.REGION_UUID);
		Region secondRegion = DatabaseHelper.getRegionDao().queryUuid(SECOND_REGION_UUID);

		User firstRegionUser = TestEntityCreator.createUser("Region1User", firstRegion, null, UserRole.SURVEILLANCE_SUPERVISOR);
		User secondRegionUser = TestEntityCreator.createUser("Region2User", secondRegion, null, UserRole.SURVEILLANCE_SUPERVISOR);

		User randomRegion1User = DatabaseHelper.getUserDao().getRandomRegionUser(firstRegion, UserRight.CASE_RESPONSIBLE);
		assertThat(randomRegion1User, is(firstRegionUser));

		User randomRegion2User = DatabaseHelper.getUserDao().getRandomRegionUser(secondRegion, UserRight.CASE_RESPONSIBLE);
		assertThat(randomRegion2User, is(secondRegionUser));

		assertNull(DatabaseHelper.getUserDao().getRandomRegionUser(firstRegion, UserRight.CONTACT_RESPONSIBLE));
	}

	@Test
	public void testGetRandomDistrictUser() {

		District firstDistrict = DatabaseHelper.getDistrictDao().queryUuid(TestHelper.DISTRICT_UUID);
		District secondDistrict = DatabaseHelper.getDistrictDao().queryUuid(TestHelper.SECOND_DISTRICT_UUID);

		User randomDistrict1User = DatabaseHelper.getUserDao().getRandomDistrictUser(firstDistrict, UserRight.CASE_RESPONSIBLE);
		assertThat(randomDistrict1User.getUuid(), is(TestHelper.USER_UUID));

		User randomDistrict2User = DatabaseHelper.getUserDao().getRandomDistrictUser(secondDistrict, UserRight.CASE_RESPONSIBLE);
		assertThat(randomDistrict2User.getUuid(), is(TestHelper.SECOND_USER_UUID));

		assertNull(DatabaseHelper.getUserDao().getRandomDistrictUser(firstDistrict, UserRight.CONTACT_RESPONSIBLE));

	}
}
