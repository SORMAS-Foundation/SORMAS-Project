package de.symeda.sormas.app;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.rest.TestEnvironmentInterceptor;
import de.symeda.sormas.app.sample.SamplesActivity;
import de.symeda.sormas.app.sample.SyncSamplesTask;
import de.symeda.sormas.app.util.SyncCallback;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Mate Strysewske on 16.06.2017.
 */
@RunWith(AndroidJUnit4.class)
public class SamplesTest {

    private static Sample sample;

    @Rule
    public final ActivityTestRule<SamplesActivity> samplesActivityRule = new ActivityTestRule<>(SamplesActivity.class, false, true);

    @Test
    public void shouldCreateSample() {
        TestHelper.destroyTestEnvironment();
        TestHelper.initTestEnvironment();

        assertThat(DatabaseHelper.getSampleDao().queryForAll().size(), is(0));

        sample = TestEntityCreator.createSample();

        assertThat(DatabaseHelper.getSampleDao().queryForAll().size(), is(1));
    }

    @Test
    public void shouldMergeSamplesAsExpected() {
        TestEnvironmentInterceptor.setSampleMergeTest(true);
        Sample mergeSample = DatabaseHelper.getSampleDao().queryUuid(sample.getUuid());

        mergeSample.setComment("AppSampleComment");
        try {
            DatabaseHelper.getSampleDao().saveAndSnapshot(mergeSample);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }

        SyncSamplesTask.syncSamplesWithCallback(samplesActivityRule.getActivity(), null, new SyncCallback() {
            @Override
            public void call(boolean syncFailed) {
                assertThat(DatabaseHelper.getSampleDao().queryUuid(sample.getUuid()).getComment(), is("ServerSampleComment"));
            }
        });

        TestEnvironmentInterceptor.setSampleMergeTest(false);
    }

    @Test
    public void shouldCreateSampleTest() {
        assertThat(DatabaseHelper.getSampleTestDao().queryForAll().size(), is(0));

        TestEntityCreator.createSampleTest(sample);

        assertThat(DatabaseHelper.getSampleTestDao().queryForAll().size(), is(1));
    }

}
