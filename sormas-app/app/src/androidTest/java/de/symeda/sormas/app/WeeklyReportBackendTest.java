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

import java.util.Date;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.report.WeeklyReport;

/**
 * Created by Mate Strysewske on 12.10.2017.
 */
@RunWith(AndroidJUnit4.class)
public class WeeklyReportBackendTest {

	@Rule
	public final ActivityTestRule<TestBackendActivity> testActivityRule = new ActivityTestRule<>(TestBackendActivity.class, false, true);

	@Before
	public void initTest() {
		TestHelper.initTestEnvironment(true);
	}

	@Test
	public void shouldCreateReportWithoutCases() {
		// Assure that there are no weekly reports in the app to start with
		assertThat(DatabaseHelper.getWeeklyReportDao().queryForAll().size(), is(0));

		WeeklyReport report = TestEntityCreator.createWeeklyReport(DateHelper.getPreviousEpiWeek(new Date()));

		// Assure that the weekly report has been successfully created
		assertThat(DatabaseHelper.getWeeklyReportDao().queryForAll().size(), is(1));

		// Assure that the weekly report does not have any entries
		assertThat(report.getTotalNumberOfCases(), is(0));
	}

	@Test
	public void shouldCreateReportWithCases() {
		TestEntityCreator.createCase();
		WeeklyReport report = TestEntityCreator.createWeeklyReport(DateHelper.getEpiWeek(new Date()));

		// Assure that the weekly report has an entry
		assertThat(report.getTotalNumberOfCases(), is(1));
	}
}
