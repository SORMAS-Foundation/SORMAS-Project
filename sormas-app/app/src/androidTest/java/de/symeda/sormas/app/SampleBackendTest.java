package de.symeda.sormas.app;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.TestResult;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import de.symeda.sormas.api.sample.SampleTestResultType;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.backend.sample.SampleTest;
import de.symeda.sormas.app.sample.SamplesActivity;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Mate Strysewske on 16.06.2017.
 */
@RunWith(AndroidJUnit4.class)
public class SampleBackendTest {

    @Rule
    public final ActivityTestRule<TestBackendActivity> testActivityRule = new ActivityTestRule<>(TestBackendActivity.class, false, true);

    @Before
    public void initTest() {
        TestHelper.initTestEnvironment();
    }

    @Test
    public void shouldCreateSample() {
        TestHelper.initTestEnvironment();

        assertThat(DatabaseHelper.getSampleDao().queryForAll().size(), is(0));
        TestEntityCreator.createSample();

        assertThat(DatabaseHelper.getSampleDao().queryForAll().size(), is(1));
    }

    @Test
    public void shouldCreateSampleTest() {
        assertThat(DatabaseHelper.getSampleTestDao().queryForAll().size(), is(0));

        Sample sample = TestEntityCreator.createSample();
        TestEntityCreator.createSampleTest(sample);

        assertThat(DatabaseHelper.getSampleTestDao().queryForAll().size(), is(1));
    }

    @Test
    public void shouldMergeSamplesAsExpected() throws DaoException {
        Sample sample = TestEntityCreator.createSample();
        SampleTest sampleTest = TestEntityCreator.createSampleTest(sample);

        sample.setComment("AppSampleComment");
        sampleTest.setTestResult(SampleTestResultType.NEGATIVE);

        DatabaseHelper.getSampleDao().saveAndSnapshot(sample);
        DatabaseHelper.getSampleDao().accept(sample);
        DatabaseHelper.getSampleTestDao().saveAndSnapshot(sampleTest);
        DatabaseHelper.getSampleTestDao().accept(sampleTest);

        Sample mergeSample = (Sample) sample.clone();
        mergeSample.setAssociatedCase((Case) sample.getAssociatedCase().clone());
        mergeSample.setId(null);
        mergeSample.setComment("ServerSampleComment");

        SampleTest mergeSampleTest = (SampleTest) sampleTest.clone();
        mergeSampleTest.setId(null);
        mergeSampleTest.setTestResult(SampleTestResultType.POSITIVE);

        DatabaseHelper.getSampleDao().mergeOrCreate(mergeSample);
        DatabaseHelper.getSampleTestDao().mergeOrCreate(mergeSampleTest);

        Sample updatedSample = DatabaseHelper.getSampleDao().queryUuid(sample.getUuid());
        assertThat(updatedSample.getComment(), is("ServerSampleComment"));
        SampleTest updatedSampleTest = DatabaseHelper.getSampleTestDao().queryUuid(sampleTest.getUuid());
        assertThat(updatedSampleTest.getTestResult(), is(SampleTestResultType.POSITIVE));
    }

}
