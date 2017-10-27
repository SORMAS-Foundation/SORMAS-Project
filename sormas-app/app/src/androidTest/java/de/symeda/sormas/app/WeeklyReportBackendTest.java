package de.symeda.sormas.app;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.report.WeeklyReport;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

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
