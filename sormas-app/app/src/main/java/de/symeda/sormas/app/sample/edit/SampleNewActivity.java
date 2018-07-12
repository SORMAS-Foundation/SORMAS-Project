package de.symeda.sormas.app.sample.edit;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;

import java.util.Date;

import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.core.async.AsyncTaskResult;
import de.symeda.sormas.app.core.async.SavingAsyncTask;
import de.symeda.sormas.app.core.async.TaskResultHolder;
import de.symeda.sormas.app.shared.SampleFormNavigationCapsule;
import de.symeda.sormas.app.shared.ShipmentStatus;

public class SampleNewActivity extends BaseEditActivity<Sample> {

    public static final String TAG = SampleNewActivity.class.getSimpleName();

    private String caseUuid = null;

    private AsyncTask saveTask;

    @Override
    public ShipmentStatus getPageStatus() {
        return (ShipmentStatus) super.getPageStatus();
    }

    @Override
    protected void onCreateInner(Bundle savedInstanceState) {
        super.onCreateInner(savedInstanceState);
        caseUuid = getCaseUuidArg(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveCaseUuidState(outState, caseUuid);
    }

    @Override
    protected Sample queryRootEntity(String recordUuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Sample buildRootEntity() {
        // basic instead of reference, because we want to have at least the related person
        Case associatedCase = DatabaseHelper.getCaseDao().queryUuidBasic(caseUuid);
        return DatabaseHelper.getSampleDao().build(associatedCase);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        getSaveMenu().setTitle(R.string.action_save_sample);
        return result;
    }

    @Override
    protected BaseEditFragment buildEditFragment(LandingPageMenuItem menuItem, Sample activityRootData) {
        SampleFormNavigationCapsule dataCapsule = new SampleFormNavigationCapsule(
                SampleNewActivity.this, getRootEntityUuid(), getPageStatus()).setCaseUuid(caseUuid);
        return SampleNewFragment.newInstance(dataCapsule, activityRootData);
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_sample_new;
    }

    @Override
    public void saveData() {

        final Sample sampleToSave = getStoredRootEntity();

        if (sampleToSave.getReportingUser() == null) {
            sampleToSave.setReportingUser(ConfigProvider.getUser());
        }
        if (sampleToSave.getReportDateTime() == null) {
            sampleToSave.setReportDateTime(new Date());
        }

        saveTask = new SavingAsyncTask(getRootView(), sampleToSave) {

            @Override
            protected void onPreExecute() {
                showPreloader();
            }

            @Override
            public void doInBackground(TaskResultHolder resultHolder) throws DaoException, ValidationException {
                validateData(sampleToSave);
                DatabaseHelper.getSampleDao().saveAndSnapshot(sampleToSave);
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<TaskResultHolder> taskResult) {
                hidePreloader();
                super.onPostExecute(taskResult);
                if (taskResult.getResultStatus().isSuccess()) {
                    finish();
                }
            }
        }.executeOnThreadPool();
    }

    private void validateData(Sample data) throws ValidationException {
        // TODO validate
//        SampleValidator.clearErrorsForSampleData(getContentBinding());
////        if (!SampleValidator.validateSampleData(nContext, sampleToSave, getContentBinding())) {
////            return;
////        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (saveTask != null && !saveTask.isCancelled())
            saveTask.cancel(true);
    }

    public static <TActivity extends BaseActivity> void goToActivity(Context fromActivity, SampleFormNavigationCapsule dataCapsule) {
        BaseEditActivity.goToActivity(fromActivity, SampleNewActivity.class, dataCapsule);
    }
}
