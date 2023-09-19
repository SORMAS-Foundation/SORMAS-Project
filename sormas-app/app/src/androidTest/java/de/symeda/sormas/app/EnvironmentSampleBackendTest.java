/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.environment.environmentsample.EnvironmentSample;

@RunWith(AndroidJUnit4.class)
public class EnvironmentSampleBackendTest {

	@Rule
	public final ActivityTestRule<TestBackendActivity> testActivityRule = new ActivityTestRule<>(TestBackendActivity.class, false, true);

	@Before
	public void initTest() {
		TestHelper.initTestEnvironment(false);
	}

	@Test
	public void shouldAcceptAsExpected() throws DaoException {
		EnvironmentSample sample = TestEntityCreator.createEnvironmentSample();
		assertThat(sample.isModified(), is(false));

		sample.setFieldSampleId("123");
		sample.setPhValue(5);
		DatabaseHelper.getEnvironmentSampleDao().saveAndSnapshot(sample);
		sample = DatabaseHelper.getEnvironmentSampleDao().queryUuid(sample.getUuid());
		assertThat(sample.isModified(), is(true));
		assertNotNull(DatabaseHelper.getEnvironmentSampleDao().querySnapshotByUuid(sample.getUuid()));

		DatabaseHelper.getEnvironmentSampleDao().accept(sample);
		sample = DatabaseHelper.getEnvironmentSampleDao().queryUuid(sample.getUuid());
		assertNull(DatabaseHelper.getEnvironmentSampleDao().querySnapshotByUuid(sample.getUuid()));
		assertThat(sample.isModified(), is(false));
	}

}
