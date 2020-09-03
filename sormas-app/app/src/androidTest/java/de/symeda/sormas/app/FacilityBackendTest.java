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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.region.District;

/**
 * Created by Mate Strysewske on 14.06.2017.
 */
@RunWith(AndroidJUnit4.class)
public class FacilityBackendTest {

	@Rule
	public final ActivityTestRule<TestBackendActivity> testActivityRule = new ActivityTestRule<>(TestBackendActivity.class, false, true);

	@Before
	public void initTest() {
		TestHelper.initTestEnvironment(false);
	}

	@Test
	public void shouldGetHealthFacilitiesByDistrict() {
		District district = DatabaseHelper.getDistrictDao().queryUuid(TestHelper.DISTRICT_UUID);

		// There should be exactly one health facility in this district - labs are not included
		assertThat(DatabaseHelper.getFacilityDao().getActiveHealthFacilitiesByDistrict(district, false, false).size(), is(1));

		assertThat(DatabaseHelper.getFacilityDao().getActiveHealthFacilitiesByDistrict(district, true, true).size(), is(3));
	}
}
