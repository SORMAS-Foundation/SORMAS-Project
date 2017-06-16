package de.symeda.sormas.app;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.epidata.EpiDataBurial;
import de.symeda.sormas.app.backend.epidata.EpiDataGathering;
import de.symeda.sormas.app.backend.epidata.EpiDataTravel;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.caze.CasesActivity;
import de.symeda.sormas.app.caze.SyncCasesTask;
import de.symeda.sormas.app.rest.TestEnvironmentInterceptor;
import de.symeda.sormas.app.util.SyncCallback;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Mate Strysewske on 14.06.2017.
 */
@RunWith(AndroidJUnit4.class)
public class CasesTest {

    private static Case caze;

    @Rule
    public final ActivityTestRule<CasesActivity> casesActivityRule = new ActivityTestRule<>(CasesActivity.class, false, true);

    @Test
    public void shouldCreateCase() {
        // Make sure that the test environment is actually destroyed; this is sometimes necessary when
        // the app has been launched outside of test mode previously
        TestHelper.destroyTestEnvironment();
        TestHelper.initTestEnvironment();

        // Assure that there are no cases in the app to start with
        assertThat(DatabaseHelper.getCaseDao().queryForAll().size(), is(0));

        caze = TestEntityCreator.createCase();

        // Assure that the case has been successfully created
        assertThat(DatabaseHelper.getCaseDao().queryForAll().size(), is(1));
    }

    @Test
    public void shouldMergeCasesAsExpected() {
        // Set case merge test flag to true to make sure that the interceptor is returning the
        // correct object
        TestEnvironmentInterceptor.setCaseMergeTest(true);
        // Get the current case object from the database
        Case mergeCase = DatabaseHelper.getCaseDao().queryUuid(caze.getUuid());

        mergeCase.setEpidNumber("AppEpidNumber");
        try {
            DatabaseHelper.getCaseDao().saveAndSnapshot(mergeCase);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        SyncCasesTask.syncCases(casesActivityRule.getActivity(), new SyncCallback() {
            @Override
            public void call(boolean syncFailed) {
                // Assure that the epid number has been updated with the one from the server
                assertThat(DatabaseHelper.getCaseDao().queryUuid(caze.getUuid()).getEpidNumber(), is("ServerEpidNumber"));
            }
        });

        TestEnvironmentInterceptor.setCaseMergeTest(false);
    }

    @Test
    public void shouldCreatePreviousHospitalization() {
        // Assure that there are no previous hospitalizations in the app to start with
        assertThat(DatabaseHelper.getPreviousHospitalizationDao().queryForAll().size(), is(0));

        TestEntityCreator.createPreviousHospitalization(caze);

        // Assure that the previous hospitalization has been successfully created
        assertThat(DatabaseHelper.getPreviousHospitalizationDao().queryForAll().size(), is(1));
    }

    @Test
    public void shouldCreateEpiDataBurial() {
        // Assure that there are no burials in the app to start with
        assertThat(DatabaseHelper.getEpiDataBurialDao().queryForAll().size(), is(0));

        TestEntityCreator.createEpiDataBurial(caze);

        // Assure that the burial has been successfully created
        assertThat(DatabaseHelper.getEpiDataBurialDao().queryForAll().size(), is(1));
    }

    @Test
    public void shouldCreateEpiDataGathering() {
        // Assure that there are no burials in the app to start with
        assertThat(DatabaseHelper.getEpiDataGatheringDao().queryForAll().size(), is(0));

        TestEntityCreator.createEpiDataGathering(caze);

        // Assure that the burial has been successfully created
        assertThat(DatabaseHelper.getEpiDataGatheringDao().queryForAll().size(), is(1));
    }

    @Test
    public void shouldCreateEpiDataTravel() {
        // Assure that there are no burials in the app to start with
        assertThat(DatabaseHelper.getEpiDataTravelDao().queryForAll().size(), is(0));

        TestEntityCreator.createEpiDataTravel(caze);

        // Assure that the burial has been successfully created
        assertThat(DatabaseHelper.getEpiDataTravelDao().queryForAll().size(), is(1));
        TestHelper.destroyTestEnvironment();
    }

}
