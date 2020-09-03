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

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.region.RegionDtoHelper;

@RunWith(AndroidJUnit4.class)
public class RegionBackendTest {

	@Rule
	public final ActivityTestRule<TestBackendActivity> testActivityRule = new ActivityTestRule<>(TestBackendActivity.class, false, true);

	@Before
	public void initTest() {
		TestHelper.initTestEnvironment(false);
	}

	@Test
	public void testHandlePulledList() {
		long startRegionCount = DatabaseHelper.getRegionDao().countOf();

		List<RegionDto> regions = new ArrayList<>();
		RegionDto region1 = RegionDto.build();
		region1.setCreationDate(new Date());
		region1.setChangeDate(new Date());
		region1.setName("TestA");
		regions.add(region1);
		RegionDto region2 = RegionDto.build();
		region2.setCreationDate(new Date());
		region2.setChangeDate(new Date());
		region2.setName("TestB");
		regions.add(region2);

		// this should cause a roll-back
		region2.setUuid(null);
		boolean hadException = false;
		try {
			new RegionDtoHelper().handlePulledList(DatabaseHelper.getRegionDao(), regions);
		} catch (DaoException e) {
			hadException = true;
		}
		assertTrue(hadException);

		long regionCount = DatabaseHelper.getRegionDao().countOf();
		assertEquals(startRegionCount, regionCount);

		// now it should work
		region2.setUuid(DataHelper.createUuid());
		hadException = false;
		try {
			new RegionDtoHelper().handlePulledList(DatabaseHelper.getRegionDao(), regions);
		} catch (DaoException e) {
			hadException = true;
		}
		assertFalse(hadException);

		regionCount = DatabaseHelper.getRegionDao().countOf();
		assertEquals(startRegionCount + 2, regionCount);
	}
}
